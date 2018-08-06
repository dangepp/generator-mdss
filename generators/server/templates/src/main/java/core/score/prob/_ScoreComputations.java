package <%= config.packages.prob %>;

import <%= config.packages.enum %>.Likelihood;
import <%= config.packages.enum %>.Category;
import <%= config.packages.category %>.*;
import <%= config.packages.profile %>.DiseaseProfile;
import <%= config.packages.profile %>.PatientProfile;

import java.util.*;

/**
 * Score Computations for the Probabilistic Score
 */
class ScoreComputations {

    // penalty for mismatches regarding specific/special sites and additional signs
    private static final double MISMATCH_PENALTY = Math.log(0.01);

    Map<Category, Double> computeScoresPerCategory(PatientProfile patient, DiseaseProfile disease) {
        Map<Category, Double> scores = new HashMap<>();
            //generated likelihood categories
        <%_ categories.forEach(function(value) { _%>
        <%_ if(value.type === 'likelihood') {_%>
        scores.put(Category.<%= value.enumName %>, probability(disease.get<%= value.name %>(), patient.get<%= value.name %>()));
        <%_ }}); _%>
    
        //generated contain categories
        <%_ categories.forEach(function(value) { _%>
        <%_ if(value.type === 'contain') {_%>
        scores.put(Category.<%= value.enumName %>, similarity(disease.get<%= value.name %>(), patient.get<%= value.name %>()));
        <%_ }}); _%>
    
        //generated ratio categories
        <%_ categories.forEach(function(value) { _%>
        <%_ if(value.type === 'ratio') {_%>
        scores.put(Category.<%= value.enumName %>, ratioProbability(disease.get<%= value.name %>(), patient.get<%= value.name %>()));
        <%_ }}); _%>
        return scores;
    }

    private static <T> double ratioProbability(Map<T, Double> disease, Set<T> patient) {
        if (disease == null || patient == null || patient.isEmpty()) {
            return 0;
        }
        double value = 0.0;
        for(T t : patient) {
            value += Math.log(disease.get(t));
        }
        return value;
    }

    private static <T> double probability(Map<T, Likelihood> disease, Set<T> patient) {
        if (disease == null || patient == null || patient.isEmpty()) {
            return 0;
        }
        double numerator = 0.0;
        for (T p : patient) {
            if (disease.get(p) == null) {
                return 0;
            }
            numerator += Math.log(disease.get(p).probability);
        }
        numerator *= (1.0 / patient.size());
        double denominator = 0.0;
        for (T d : disease.keySet()) {
            if (disease.get(d) == null) {
                return 0;
            }
            denominator += disease.get(d).probability;
        }
        return denominator == 0.0 ? 0 : numerator - Math.log(denominator);
    }

    private static <T extends Enum<T>> double similarity(Set<T> disease, Set<T> patient) {
        if (disease == null || patient == null || patient.isEmpty()) {
            return 0;
        }
        double total = 0.0;
        for (T p : patient) {
            if (!disease.contains(p)) {
                total += MISMATCH_PENALTY;
            }
        }
        return total * (1.0 / patient.size());
    }

    private static <T extends Enum<T>> double similarity(Set<T> disease, T patient) {
        return similarity(disease, setOf(patient));
    }

    private static <T extends Enum<T>> double probability(Map<T, Likelihood> disease, T patient) {
        return probability(disease, setOf(patient));
    }

    private static <T extends Enum<T>> double ratioProbability(Map<T, Double> disease, T patient) {
        return ratioProbability(disease, setOf(patient));
    }

    private static <E extends Enum<E>> Set<E> setOf(E e) {
        return e == null ? null
                : EnumSet.of(e);
    }

}
