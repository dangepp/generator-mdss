package <%= config.packages.tfidf %>;

import <%= config.packages.category %>.*;
import <%= config.packages.util %>.*;
import <%= config.packages.enum %>.Likelihood;
import <%= config.packages.enum %>.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Score Computations for the IDF part of the TFIDF based Score.
 */
class IDFOnlyYesScoreComputations {

    private IDFStatistics statistics;

    IDFOnlyYesScoreComputations setStatistics(IDFStatistics statistics) {
        this.statistics = statistics;
        return this;
    }

    private double idf(double df) {
        return df == 0.0 ? 0.0 : Math.log(statistics.getNumberOfDiseases() / df);
    }

    //generated
    Map<Category, Map<String, Double>> idfVector() {
        Map<Category, Map<String, Double>> map = new HashMap<>();
        <%_ categories.forEach(function(value) { _%>
        map.put(Category.<%= value.enumName %>, idfScore(this.statistics.get<%= value.name %>(), <%= value.name %>.class));
        <%_ }); _%>
        return map;

    }

    private <E extends Enum<E>> Map<String, Double> idfScore(EnumMapCounter<E, Likelihood> input, Class<E> clazz) {
        Map<String, Double> vector = new HashMap<>();
        for (E e : clazz.getEnumConstants()) {
            int statistic = 0;
            for (Likelihood likelihood : Likelihood.yesValues()) {
                statistic += input.get(e, likelihood);
            }
            vector.put(e.name(), idf(statistic));
        }
        return vector;
    }

    private <E extends Enum<E>> Map<String, Double> idfScore(EnumSetCounter<E> input, Class<E> clazz) {
        Map<String, Double> vector = new HashMap<>();
        for (E e : clazz.getEnumConstants()) {
            vector.put(e.name(), idf(input.seen(e)));
        }
        return vector;
    }

    private <E extends Enum<E>> Map<String, Double> idfScore(DoubleRegister<E> input, Class<E> clazz) {
        Map<String, Double> vector = new HashMap<>();
        for (E e : clazz.getEnumConstants()) {
            vector.put(e.name(), idf(input.get(e)));
        }
        return vector;
    }

}
