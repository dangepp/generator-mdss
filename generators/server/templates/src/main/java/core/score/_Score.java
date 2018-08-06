package <%= config.packages.score %>;

import <%= config.packages.profile %>.DiseaseProfile;
import lombok.Data;

import java.util.Comparator;
/**
 * Score data class
 */
@Data
public class Score {

    public static Comparator<Score> comparator(ScoreType type) {
        return (score, t1) -> {
            int comp = type.comp.apply(score)
                    .compareTo(type.comp.apply(t1));
            if (comp == 0) {
                comp = - score.disease.getId()
                        .compareTo(t1.disease.getId());
            }
            return comp;

        };
    }

    public Score(DiseaseProfile disease) {
        this.disease = disease;
    }

    private double similarity;

    private double rank;

    private double explanation;

    private double csv;

    private DiseaseProfile disease;

    Double getScore(ScoreType type) {
        if (ScoreType.SIMILARITY.equals(type)) {
            return this.similarity;
        } else if (ScoreType.RANK.equals(type)) {
            return this.rank;
        } else {
            return null;
        }
    }

}
