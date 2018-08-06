package <%= config.packages.restValidation %>;

import <%= config.packages.restModel %>.PatientModel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * ModelValidator for patients. Validates @DependentOn constraints.
 */
@Slf4j
public class PatientModelValidator implements ConstraintValidator<ModelConstraint, PatientModel> {

    private static final Map<Field, DependentOnWrapper> DEPENDENT_ON_MAP;

    static {
        DEPENDENT_ON_MAP = new HashMap<>();
        for (Field field : PatientModel.class.getDeclaredFields()) {
            DependentOn dependentOn = field.getAnnotation(DependentOn.class);
            if (dependentOn != null) {
                Class<? extends Enum> fieldEnumClass = ValidationHelper.getEnumClassOfModelField(field);
                if (!(field.getType().isAssignableFrom(EnumSet.class) || field.getType().isEnum())) {
                    throw new IllegalArgumentException(String.format("Expected enum or java.util.EnumSet as Type for dependent field, but got %s in field %s", field.getType().getName(), field.getName()));
                }
                Pair<Field, ? extends Enum> dependency = ValidationHelper.getDependencyEnumFromPattern(dependentOn, field, PatientModel.class);

                if (!(dependency.getFirst().getType().isAssignableFrom(EnumSet.class) || dependency.getFirst().getType().isEnum())) {
                    throw new IllegalArgumentException(String.format("Expected enum or java.util.EnumSet as Type for dependency field, but got %s in field %s", dependency.getFirst().getType().getName(), dependency.getFirst().getName()));
                }

                DEPENDENT_ON_MAP.put(field, new DependentOnWrapper(dependency.getFirst(), dependency.getSecond(), fieldEnumClass));
            }

        }

    }

    @Override
    public void initialize(ModelConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(PatientModel model, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        return checkDependentOn(model, constraintValidatorContext);
    }

    private boolean checkDependentOn(PatientModel model, ConstraintValidatorContext constraintValidatorContext) {
        // already validated, that can be checked!
        for (Map.Entry<Field, DependentOnWrapper> fieldPairEntry : DEPENDENT_ON_MAP.entrySet()) {
            boolean isDependency = ValidationHelper.isDependencyFulfilled(model, fieldPairEntry.getValue().getField(), fieldPairEntry.getValue().getDependencyEnum());

            Object toValidate = ValidationHelper.getObjectFromField(model, fieldPairEntry.getKey());
            if (toValidate != null) { 
                if (toValidate instanceof EnumSet) {
                    EnumSet enumSetValidate = (EnumSet) toValidate;
                    if (!enumSetValidate.isEmpty() && !isDependency) {
                    constraintValidatorContext
                    .buildConstraintViolationWithTemplate(
                                    String.format("The dependency of %s is not fulfilled, but values are given", fieldPairEntry.getKey().getName())
                            ).addConstraintViolation();
                    return false;
                }
                } else if (toValidate instanceof Enum) {
                    if (!isDependency) {
                        constraintValidatorContext
                        .buildConstraintViolationWithTemplate(
                                        String.format("The dependency of %s is not fulfilled, but values are given", fieldPairEntry.getKey().getName())
                                        ).addConstraintViolation();
                        return false;
                    }
                } else {
                    throw new IllegalArgumentException(String.format("Expected java.util.EnumMap or java.util.EnumSet as Type for dependent field, but got %s in field %s", fieldPairEntry.getKey().getType().getName(), fieldPairEntry.getKey().getName()));
                }
            } 
        }
        return true;
    }
    
    @Getter
    private static class DependentOnWrapper {
        private Field field;
        private Enum dependencyEnum;
        private Class<? extends Enum> fieldKeyClass;

        DependentOnWrapper(Field field, Enum dependencyEnum, Class<? extends Enum> fieldKeyClass) {
            this.field = field;
            this.dependencyEnum = dependencyEnum;
            this.fieldKeyClass = fieldKeyClass;
        }
    }

}