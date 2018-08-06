package <%= config.packages.restModel %>;

import lombok.Data;

/**
 * Model for Scores.
 */
@Data
public class ScoreModel {

    private double similarity;

    private double rank;

    private double explanation;

    private DiseaseMetaModel disease;

}