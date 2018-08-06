package <%= config.packages.restFile %>;

import <%= config.packages.enum %>.Frequency;
import <%= config.packages.enum %>.Category;
import <%= config.packages.enum %>.Likelihood;
import <%= config.packages.restModel %>.DiseaseModel;

import org.springframework.util.StringUtils;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Helper methods for mapping disease files
 */
public final class MappingHelper {

    private MappingHelper() {}

    static DiseaseModel getBaseAttributesModel(Map<String, String> data, DiseaseModel model) throws StringMappingException {
        model.setDiseaseName(data.get(Attributes.NAME_HEADER));
        model.setOverallFrequency(stringToEnum(Frequency.class, data.get(Attributes.FREQUENCY_HEADER)));
        model.setSoftConflictCategories(stringToEnumSet(Category.class, data.get(Attributes.CONFLICT_HEADER)));
        return model;

    }

    static <T extends Enum<T>> EnumMap<T, Likelihood> generateLikelihoodMapForCategory(Class<T> clazz, Map<String, String> data, EnumMap<T, String> mappings) throws AttributeMissmatchException {
        EnumMap<T, Likelihood> retMap = new EnumMap<>(clazz);
        for (T t : clazz.getEnumConstants()) {
            try {
                Likelihood likelihood = stringToLikelihood(data.get(mappings.get(t)));
                if (likelihood != null) {
                    retMap.put(t, likelihood);
                }
            } catch (MapperExecutionException e) {
                throw new AttributeMissmatchException(String.format("The Likelihood for the value='%s' of Category='%s' could not be converted", mappings.get(t), clazz.getSimpleName()), e);
            }
        }
        return retMap;
    }

    static Likelihood stringToLikelihood(String string) throws MapperExecutionException {
        return stringToEnum(Likelihood.class, string);
    }

    static <T extends Enum<T>> EnumMap<T, Double> stringToRatio(Class<T> clazz, String string) throws MapperExecutionException {
        if (StringUtils.isEmpty(string) || Attributes.DOES_NOT_APPLY.equals(string.toUpperCase())) {
            return null;
        }
        EnumMap<T, Double> map = new EnumMap<>(clazz);
        String[] values = string.split(Attributes.RATIO_DELIMITER);
        if (values.length != clazz.getEnumConstants().length) {
            throw new StringMappingException(String.format("The Ratio given for %s does not have the same number of arguments as the Category has attributes", clazz.getSimpleName()));
        }
        double sum = 0.0;
        for (int i = 0; i < values.length; i++) {
            try {
                double value = Double.parseDouble(values[i]);
                map.put(clazz.getEnumConstants()[i], value);
                sum += value;
            } catch (NumberFormatException e) {
                throw new StringMappingException(String.format("Unconvertable Ratio value='%s' for category %s ", values[i], clazz.getSimpleName()));
            }
        }
        double finalSum = sum;
        map.replaceAll((a, b) -> b / finalSum);
        return map;
    }

    static <T extends Enum<T>> EnumSet<T> stringToEnumSet(Class<T> clazz, String string) throws StringMappingException {
        EnumSet<T> set = EnumSet.noneOf(clazz);
        if (StringUtils.isEmpty(string) || Attributes.DOES_NOT_APPLY.equals(string.toUpperCase())) {
            return set;
        }
        for (String value : string.split(Attributes.SET_DELIMITER)) {
            try {
                T tEnum = stringToEnum(clazz, value);
                if (tEnum != null) {
                    set.add(tEnum);
                }
            } catch (StringMappingException e) {
                throw new StringMappingException(String.format("Unrecognized %s value='%s' in given input='%s'", clazz.getSimpleName(), value, string));
            }
        }
        return set;
    }

    static <T extends Enum<T>> T stringToEnum(Class<T> clazz, String string) throws StringMappingException {
        if (StringUtils.isEmpty(string) || Attributes.DOES_NOT_APPLY.equals(string.toUpperCase())) {
            return null;
        }
        try {
            return T.valueOf(clazz, string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new StringMappingException(String.format("Unrecognized %s value='%s'", clazz.getSimpleName(), string));
        }
    }
}
