package <%= config.packages.score %>;

import java.util.function.Function;

/**
 * Type of Scores with their respective thresholds.
 */
public enum ScoreType {

    RANK(Score::getRank, 1E-4),
    SIMILARITY(Score::getSimilarity, 1E-8);

    public final Function<Score, Double> comp;
    public final double threshold;

    ScoreType(Function<Score, Double> comp, double threshold) {
        this.comp = comp;
        this.threshold = threshold;
    }
}
