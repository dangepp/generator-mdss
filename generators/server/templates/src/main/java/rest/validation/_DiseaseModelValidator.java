package <%= config.packages.restValidation %>;

import <%= config.packages.enum %>.Likelihood;
import <%= config.packages.restModel %>.DiseaseModel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/**
 * ModelValidator for diseases. Validates @AtLeast and @DependentOn constraints.
 */
@Slf4j
public class DiseaseModelValidator implements ConstraintValidator<ModelConstraint, DiseaseModel> {

    private static final Map<Field, DependentOnWrapper> DEPENDENT_ON_MAP;

    private static final Map<Field, AtLeastWrapper> AT_LEAST_MAP;

    static {
        DEPENDENT_ON_MAP = new HashMap<>();
        AT_LEAST_MAP = new HashMap<>();
        for (Field field : DiseaseModel.class.getDeclaredFields()) {
            DependentOn dependentOn = field.getAnnotation(DependentOn.class);
            AtLeast atLeast = field.getAnnotation(AtLeast.class);

            if (dependentOn != null) {
                Class<? extends Enum> fieldEnumClass = ValidationHelper.getEnumClassOfModelField(field);
                if (!(field.getType().isAssignableFrom(EnumSet.class) || field.getType().isAssignableFrom(EnumMap.class))) {
                    throw new IllegalArgumentException(String.format("Expected java.util.EnumMap or java.util.EnumSet as Type for dependent field, but got %s in field %s", field.getType().getName(), field.getName()));
                }
                Pair<Field, ? extends Enum> dependency = ValidationHelper.getDependencyEnumFromPattern(dependentOn, field, DiseaseModel.class);

                if (!(dependency.getFirst().getType().isAssignableFrom(EnumSet.class) || dependency.getFirst().getType().isAssignableFrom(EnumMap.class))) {
                    throw new IllegalArgumentException(String.format("Expected java.util.EnumMap or java.util.EnumSet as Type for dependency field, but got %s in field %s", dependency.getFirst().getType().getName(), dependency.getFirst().getName()));
                }

                Integer atLeastVal = dependentOn.atLeast();

                if (atLeast != null) {
                    log.warn(String.format("DependentOn and AtLeast used on field '%s' should not be used together," +
                            " use the DependentOn.atLeast parameter instead, using AtLeast value for validating", field.getName()));
                    atLeastVal = atLeast.value();
                }

                DEPENDENT_ON_MAP.put(field, new DependentOnWrapper(dependency.getFirst(), dependency.getSecond(), atLeastVal, fieldEnumClass));
            } else if (atLeast != null) {
                if (atLeast.value() < 1) {
                    log.warn("AtLeast value on field {} is smaller than 1, skipping validation of field", field.getName());
                } else {
                    AT_LEAST_MAP.put(field, new AtLeastWrapper(atLeast.value(), ValidationHelper.getEnumClassOfModelField(field)));
                }
            }
        }

    }

    @Override
    public void initialize(ModelConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(DiseaseModel model, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        return checkBaseAttributes(model, constraintValidatorContext) && checkDependentOn(model, constraintValidatorContext) && checkAtLeast(model, constraintValidatorContext);
    }

    private boolean checkBaseAttributes(DiseaseModel model, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(model.getId())) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("The id of a disease is empty").addConstraintViolation();
            return false;
        }
        if (StringUtils.isEmpty(model.getDiseaseName())) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("The name of a disease is empty").addConstraintViolation();
            return false;
        }
        if (model.getOverallFrequency() == null) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("The frequency of a disease is missing").addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean checkAtLeast(DiseaseModel model, ConstraintValidatorContext constraintValidatorContext) {
        for (Map.Entry<Field, AtLeastWrapper> fieldAtLeastWrapperEntry : AT_LEAST_MAP.entrySet()) {
            Object toValidate = ValidationHelper.getObjectFromField(model, fieldAtLeastWrapperEntry.getKey());
            if (toValidate == null) {
                constraintValidatorContext
                        .buildConstraintViolationWithTemplate(
                                String.format("At least %d values expected for field %s, but got 0", fieldAtLeastWrapperEntry.getValue().getAtLeast(), fieldAtLeastWrapperEntry.getKey().getName())
                        ).addConstraintViolation();
                return false;
            } else {
                if (toValidate instanceof EnumSet) {
                    int size = ((EnumSet) toValidate).size();
                    if (size < fieldAtLeastWrapperEntry.getValue().getAtLeast()) {
                        constraintValidatorContext
                                .buildConstraintViolationWithTemplate(
                                        String.format("At least %d values expected for field %s, but got %d", fieldAtLeastWrapperEntry.getValue().getAtLeast(), fieldAtLeastWrapperEntry.getKey().getName(), size)
                                ).addConstraintViolation();
                        return false;
                    }
                } else if (toValidate instanceof EnumMap) {
                    EnumMap<?, Likelihood> enumMap = (EnumMap<?, Likelihood>) toValidate; //already validated in constructor
                    int yesValues = 0;
                    for (Enum anEnum : fieldAtLeastWrapperEntry.getValue().getFieldKeyClass().getEnumConstants()) {
                        Likelihood likelihood = enumMap.get(anEnum);
                        if (likelihood == null) {
                            constraintValidatorContext
                                    .buildConstraintViolationWithTemplate(
                                            String.format("The enum '%s' of %s is not containing a likelihood value", anEnum.name(), fieldAtLeastWrapperEntry.getKey().getName())
                                    ).addConstraintViolation();
                            return false;
                        }
                        if (ValidationHelper.YES_LIKELIHOOD_SET.contains(likelihood)) {
                            yesValues++;
                        }
                    }
                    if (yesValues < fieldAtLeastWrapperEntry.getValue().getAtLeast()) {
                        constraintValidatorContext
                                .buildConstraintViolationWithTemplate(
                                        String.format("Expected at least %d yes likelihood values for field %s but got only %d", fieldAtLeastWrapperEntry.getValue().getAtLeast(), fieldAtLeastWrapperEntry.getKey().getName(), yesValues)
                                ).addConstraintViolation();
                        return false;
                    }
                } else {
                    throw new IllegalArgumentException(String.format("Expected java.util.EnumMap or java.util.EnumSet as Type for dependent field, but got %s in field %s", fieldAtLeastWrapperEntry.getKey().getType().getName(), fieldAtLeastWrapperEntry.getKey().getName()));
                }
            }
        }
        return true;
    }

    private boolean checkDependentOn(DiseaseModel model, ConstraintValidatorContext constraintValidatorContext) {
        // already validated, that can be checked!
        for (Map.Entry<Field, DependentOnWrapper> fieldPairEntry : DEPENDENT_ON_MAP.entrySet()) {
            boolean isDependency = ValidationHelper.isDependencyFulfilled(model, fieldPairEntry.getValue().getField(), fieldPairEntry.getValue().getDependencyEnum());

            Object toValidate = ValidationHelper.getObjectFromField(model, fieldPairEntry.getKey());
            if (toValidate == null) {
                if (isDependency) {
                    constraintValidatorContext
                            .buildConstraintViolationWithTemplate(
                                    String.format("The dependency of %s is fulfilled, but no values given", fieldPairEntry.getKey().getName())
                            ).addConstraintViolation();
                    return false;
                }
            }
            if (toValidate instanceof EnumSet) {
                EnumSet enumSetValidate = (EnumSet) toValidate;
                if (enumSetValidate.size() < fieldPairEntry.getValue().getAtLeast() && isDependency) {
                    constraintValidatorContext
                            .buildConstraintViolationWithTemplate(
                                    String.format("The dependency of %s is fulfilled, but too few values are given", fieldPairEntry.getKey().getName())
                            ).addConstraintViolation();
                    return false;
                } else if (!enumSetValidate.isEmpty() && !isDependency) {
                    constraintValidatorContext
                            .buildConstraintViolationWithTemplate(
                                    String.format("The dependency of %s is not fulfilled, but values are given", fieldPairEntry.getKey().getName())
                            ).addConstraintViolation();
                    return false;
                }
            } else if (toValidate instanceof EnumMap) {
                EnumMap<?, Likelihood> enumMapValidate = (EnumMap<?, Likelihood>) toValidate;
                if (!enumMapValidate.isEmpty() && !isDependency) {
                    constraintValidatorContext
                            .buildConstraintViolationWithTemplate(
                                    String.format("The dependency of %s is not fulfilled, but values are given", fieldPairEntry.getKey().getName())
                            ).addConstraintViolation();
                    return false;
                } else if (isDependency) {
                    int yesValues = 0;
                    for (Enum anEnum : fieldPairEntry.getValue().getFieldKeyClass().getEnumConstants()) {
                        Likelihood likelihood = enumMapValidate.get(anEnum);
                        if (likelihood == null) {
                            constraintValidatorContext
                                    .buildConstraintViolationWithTemplate(
                                            String.format("The enum '%s' of %s is not containing a likelihood value", anEnum.name(), fieldPairEntry.getKey().getName())
                                    ).addConstraintViolation();
                            return false;
                        }
                        if (ValidationHelper.YES_LIKELIHOOD_SET.contains(likelihood)) {
                            yesValues++;
                        }
                    }
                    if (yesValues < fieldPairEntry.getValue().getAtLeast()) {
                        constraintValidatorContext
                                .buildConstraintViolationWithTemplate(
                                        String.format("Expected at least %d yes likelihood values for field %s but got only %d", fieldPairEntry.getValue().getAtLeast(), fieldPairEntry.getKey().getName(), yesValues)
                                ).addConstraintViolation();
                        return false;
                    }
                }

            } else {
                throw new IllegalArgumentException(String.format("Expected java.util.EnumMap or java.util.EnumSet as Type for dependent field, but got %s in field %s", fieldPairEntry.getKey().getType().getName(), fieldPairEntry.getKey().getName()));
            }
        }
        return true;
    }

    @Getter
    private static class DependentOnWrapper {
        private Field field;
        private Enum dependencyEnum;
        private Integer atLeast;
        private Class<? extends Enum> fieldKeyClass;

        DependentOnWrapper(Field field, Enum dependencyEnum, Integer atLeast, Class<? extends Enum> fieldKeyClass) {
            this.field = field;
            this.dependencyEnum = dependencyEnum;
            this.atLeast = atLeast;
            this.fieldKeyClass = fieldKeyClass;
        }
    }

    @Getter
    private static class AtLeastWrapper {
        private Integer atLeast;
        private Class<? extends Enum> fieldKeyClass;

        AtLeastWrapper(Integer atLeast, Class<? extends Enum> fieldKeyClass) {
            this.atLeast = atLeast;
            this.fieldKeyClass = fieldKeyClass;
        }
    }
}