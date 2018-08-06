package <%= config.packages.restValidation %>;

import <%= config.packages.enum %>.Likelihood;
import <%= config.packages.restModel %>.BaseModel;

import org.springframework.data.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Helper methods for the model validators.
 */
public class ValidationHelper {

    static final Set<Likelihood> YES_LIKELIHOOD_SET = new HashSet<>(Arrays.asList(Likelihood.yesValues()));

    private static final String DEPENDENCY_SPLIT = "\\.";

    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("\\w+" + DEPENDENCY_SPLIT + "[A-Z_]+");

    static Pair<Field, ? extends Enum> getDependencyEnumFromPattern(DependentOn dependentOn, Field field, Class underlyingClass) {
        if (!DEPENDENCY_PATTERN.matcher(dependentOn.value()).matches()) {
            throw new IllegalArgumentException(String.format("The Dependency on the Field %s does not match the basic dependency pattern '%s'", field.getName(), DEPENDENCY_PATTERN.pattern()));
        }
        String dependencyFieldString = dependentOn.value().split(DEPENDENCY_SPLIT)[0];
        String dependencyEnumString = dependentOn.value().split(DEPENDENCY_SPLIT)[1];
        Field dependencyField;
        try {
            dependencyField = underlyingClass.getDeclaredField(dependencyFieldString);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(String.format("Dependency for field %s contains not existent field", field.getName()), e);
        }

        Class<? extends Enum> enumClass = getEnumClassOfModelField(dependencyField);

        return Pair.of(dependencyField, Enum.valueOf(enumClass, dependencyEnumString));
    }

    static boolean isDependencyFulfilled(BaseModel model, Field field, Enum e) {
        Object dependencyObj = getObjectFromField(model, field);
        if (dependencyObj == null) {
            return false;
        }
        if (dependencyObj instanceof Enum) {
            return dependencyObj.equals(e);
        } else if (dependencyObj instanceof EnumSet) {
            EnumSet enumSetDependency = (EnumSet) dependencyObj;
            return enumSetDependency.contains(e);
        } else if (dependencyObj instanceof EnumMap) {
            EnumMap enumMapDependency = (EnumMap) dependencyObj;
            Object value = enumMapDependency.get(e);
            if (value == null) {
                return false;
            }
            if (value instanceof Likelihood) {
                return YES_LIKELIHOOD_SET.contains(value);
            } else {
                throw new IllegalStateException("Expected Dependency Field EnumMap to have Likelihood values, got %s instead in field %s");
            }
        } else {
            throw new IllegalStateException(String.format("Expected java.util.EnumMap or java.util.EnumSet as Type for dependent field, but got %s in field %s", field.getType().getName(), field.getName()));
        }
    }

    static Object getObjectFromField(BaseModel model, Field field) {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            return field.get(model);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("Cannot access field %s of diseaseModel", field.getName()), e);
        } finally {
            field.setAccessible(accessible);
        }
    }

    static Class<? extends Enum> getEnumClassOfModelField(Field field) {
        checkField(field);
        //does not have to check, because checked above.
        if (field.getType().isEnum()) {
            return (Class<? extends Enum>) field.getType();
        } else {
            //must be correctly parametrized enummap or enumset because of checkField
            return (Class<? extends Enum>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }
    }

    //basic check, if the field is suitable for the respective validator must be checked
    private static void checkField(Field field) {
        //if field is enum ==> checks out automatically
        if (!field.getType().isEnum()) {
            if (!(field.getType().isAssignableFrom(EnumSet.class) || field.getType().isAssignableFrom(EnumMap.class))) {
                throw new IllegalArgumentException(String.format("Expected java.util.EnumMap or java.util.EnumSet as Type for dependent field, but got %s in field %s", field.getType().getName(), field.getName()));
            }
            if (!(field.getGenericType() instanceof ParameterizedType)) {
                throw new IllegalArgumentException(String.format("Expected dependency field '%s' to be parametrized", field.getName()));
            }
            Class eClass = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            if (!eClass.isEnum()) {
                throw new IllegalArgumentException(String.format("First Parametrized type %s is not an enum", eClass.getName()));
            }
            if (field.getType().isAssignableFrom(EnumMap.class)) {
                Class likelihoodClass = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
                if (!likelihoodClass.isEnum() || !((Class<? extends Enum>) likelihoodClass).isAssignableFrom(Likelihood.class)) {
                    throw new IllegalArgumentException(String.format("Second Parametrized type of field %s is not Likelihood", field.getName()));
                }
            }
        }
    }
}
