package <%= config.packages.tfidf %>;


import <%= config.packages.profile %>.DiseaseProfile;
import <%= config.packages.profile %>.PatientProfile;
import <%= config.packages.enum %>.Likelihood;
import <%= config.packages.enum %>.Category;
import <%= config.packages.category %>.*;

import java.util.*;

/**
 * Score Computations for the TF part of the TFIDF based Score.
 */
class TFOnlyScoreComputations {

    private boolean keepOnlyYes;

    TFOnlyScoreComputations() {
        this.keepOnlyYes = false;
    }

    private <E extends Enum<E>> Map<String, Double> score(Set<E> input, Class<E> classE) {
        Set<E> checkedInput = checkInput(input);
        Map<String, Double> vector = new HashMap<>();
        for (E e : classE.getEnumConstants()) {
            if (checkedInput.contains(e) || !keepOnlyYes) {
                vector.put(e.name(), getLikelihood(checkedInput.contains(e)).probability);
            }
        }
        return vector;
    }

    //for dependent entities
    private <E extends Enum<E>> Map<String, Double> score(Set<E> input, Class<E> classE, Likelihood dependency) {
        Set<E> checkedInput = checkInput(input);
        Map<String, Double> vector = new HashMap<>();
        if (checkedInput.isEmpty()) {
            for (E e : classE.getEnumConstants()) {
                vector.put(e.name(), dependency != null ? dependency.probability : Likelihood.NO.probability);
            }
        }
        for (E e : classE.getEnumConstants()) {
            if (checkedInput.contains(e) || !keepOnlyYes) {
                vector.put(e.name(), getLikelihood(checkedInput.contains(e)).probability);
            }
        }
        return vector;
    }

    private <E extends Enum<E>> Map<String, Double> score(Map<E, Likelihood> input, Class<E> classE) {
        Map<E, Likelihood> checkedInput = checkInput(input);
        Map<String, Double> vector = new HashMap<>();
        for (E e : classE.getEnumConstants()) {
            if (checkedInput.containsKey(e) || !keepOnlyYes) {
                vector.put(e.name(), getLikelihood(checkedInput.get(e)).probability);
            }
        }
        return vector;
    }

    //for dependent entities
    private <E extends Enum<E>> Map<String, Double> score(Map<E, Likelihood> input, Class<E> classE, Likelihood dependency) {
        Map<E, Likelihood> checkedInput = checkInput(input);
        Map<String, Double> vector = new HashMap<>();
        if (checkedInput.isEmpty()) {
            for (E e : classE.getEnumConstants()) {
                vector.put(e.name(), dependency != null ? dependency.probability : Likelihood.NO.probability);
            }
        }
        for (E e : classE.getEnumConstants()) {
            if (checkedInput.containsKey(e) || !keepOnlyYes) {
                vector.put(e.name(), getLikelihood(checkedInput.get(e)).probability);
            }
        }
        return vector;
    }

    private <E extends Enum<E>> Map<String, Double> scoreRatio(Map<E, Double> input, Class<E> classE) {
        Map<E, Double> checkedInput = checkRatioInput(input);
        Map<String, Double> vector = new HashMap<>();
        Optional<Double> max = checkedInput.values().stream().max(Comparator.naturalOrder());
        if(max.isPresent() && checkedInput.keySet().size() == classE.getEnumConstants().length){
            for (E e : classE.getEnumConstants()) {
                vector.put(e.name(), checkedInput.get(e) / max.get());
            }
        } else {
            for (E e : classE.getEnumConstants()) {
                vector.put(e.name(), Likelihood.YES.probability);
            }
        }
        return vector;
    }

    private <E extends Enum<E>> Map<String, Double> score(E input, Class<E> classE) {
        Map<String, Double> vector = new HashMap<>();
        if (keepOnlyYes) {
            if (input != null) {
                vector.put(input.name(), Likelihood.YES.probability);
            }
        } else {
            for (E e : classE.getEnumConstants()) {
                vector.put(e.name(), getLikelihood(e.equals(input)).probability);
            }
        }

        return vector;
    }

    private <E extends Enum<E>> Set<E> checkInput(Set<E> input) {
        if (input == null) {
            return new HashSet<>();
        }
        return input;
    }

    private <E extends Enum<E>> Map<E,Likelihood> checkInput(Map<E,Likelihood> input) {
        if (input == null) {
            return new HashMap<>();
        }
        return input;
    }

    private <E extends Enum<E>> Map<E,Double> checkRatioInput(Map<E,Double> input) {
        if (input == null) {
            return new HashMap<>();
        }
        return input;
    }

    //generated
    Map<Category, Map<String, Double>> tfVector(DiseaseProfile disease) {
        this.keepOnlyYes = false;
        Map<Category, Map<String, Double>> map = new HashMap<>();
        <%_ categories.forEach(function(value) { _%>
        map.put(Category.<%= value.enumName %>, <%= value.type === 'ratio' ? 'scoreRatio' : 'score' %>(disease.get<%= value.name %>(), <%= value.name %>.class<%= value.type !== 'ratio' && value.dependentOn ? ', disease.get'+ value.dependentOn.varName + '().get('+ value.dependentOn.varName+'.'+value.dependentOn.item+')' : '' %>));
        <%_ }); _%>
        return map;
    }

    Map<Category, Map<String, Double>> tfVector(PatientProfile patient) {
        this.keepOnlyYes = true;
        Map<Category, Map<String, Double>> map = new HashMap<>();
        <%_ categories.forEach(function(value) { _%>
        map.put(Category.<%= value.enumName %>, score(patient.get<%= value.name %>(), <%= value.name %>.class));
        <%_ }); _%>
        return map;

    }

    private Likelihood getLikelihood(Likelihood likelihood) {
        if (likelihood == null) {
            return Likelihood.NO;
        }
        return likelihood;
    }
    //todo configurable and on class?
    private Likelihood getLikelihood(boolean likelihood) {
        if (likelihood) {
            return Likelihood.YES;
        }
        return Likelihood.NO;
    }

}