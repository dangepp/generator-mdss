package <%= config.packages.prob %>;

import <%= config.packages.category %>.*;
import <%= config.packages.profile %>.DiseaseProfile;
import <%= config.packages.profile %>.PatientProfile;
import <%= config.packages.score %>.*;
import <%= config.packages.enum %>.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Score Factory for the porbabilistic Score.
 */
@Service
public class ProbMethodFactory extends AbstractScoreFactory {

    private static final Map<Category, Double> WEIGHTS = new HashMap<>();

    static {
        //todo weights in db?
        <%_ categories.forEach(function(value) { _%>
            <%_ if(value.weight) {_%>
                WEIGHTS.put(Category.<%= value.enumName %>, <%= value.weight %>d);
        <%_ }}); _%>

    }

    private ScoreComputations computations;

    private List<DiseaseProfile> diseases;

    public ProbMethodFactory() {
        this.computations = new ScoreComputations();
    }

    @Override
    public List<Score> calculateScores(PatientProfile patient) throws ScoreException {
        List<Score> scores = new ArrayList<>();
        for (DiseaseProfile disease : this.diseases) {
            Score newScore = new Score(disease);
            Map<Category, Double> scoresPerCategory = this.computations.computeScoresPerCategory(patient, disease);

            double overallScore = 0;
            for (Map.Entry<Category, Double> stringDoubleEntry : scoresPerCategory.entrySet()) {
                Double weight = WEIGHTS.get(stringDoubleEntry.getKey());
                Double score = stringDoubleEntry.getValue();
                if (weight != null) {
                    score *= weight;
                }
                overallScore += score;
            }
            newScore.setSimilarity(overallScore);
            newScore.setRank(overallScore + Math.log(disease.getOverallFrequency().frequency));
            scores.add(newScore);
        }
        return normalizeLogScoresAndSort(scores);
    }

    @Override
    public String getScoreName() {
        return "LogProb";
    }

    @Override
    public void setDiseases(List<DiseaseProfile> diseases) {
        this.diseases = diseases;
    }

    @Override
    protected double modifyThreshold(ScoreType type) {
        return Math.log(type.threshold);
    }

}
