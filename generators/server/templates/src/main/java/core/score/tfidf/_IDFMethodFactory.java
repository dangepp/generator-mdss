package <%= config.packages.tfidf %>;

import <%= config.packages.profile %>.DiseaseProfile;
import <%= config.packages.profile %>.PatientProfile;
import <%= config.packages.category %>.*;
import <%= config.packages.enum %>.Category;
import <%= config.packages.score %>.*;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Score Factory for the TFIDF based Score.
 */
@Service
public class IDFMethodFactory extends AbstractScoreFactory {

    private Map<Category, Map<String, Double>> idfVector;

    private Map<DiseaseProfile, Map<Category, Map<String, Double>>> diseaseTfVectors;

    private TFOnlyScoreComputations tfOnlyScoreComputations;

    private IDFOnlyYesScoreComputations idfOnlyYesScoreComputations;

    private static final Map<Category, Double> WEIGHTS = new HashMap<>();

    static {
        //todo weights in db?
        <%_ categories.forEach(function(value) { _%>
            <%_ if(value.weight) {_%>
                WEIGHTS.put(Category.<%= value.enumName %>, <%= value.weight %>d);
        <%_ }}); _%>

    }

    public IDFMethodFactory() {
        this.idfOnlyYesScoreComputations = new IDFOnlyYesScoreComputations();
        this.tfOnlyScoreComputations = new TFOnlyScoreComputations();
    }

    @Override
    public List<Score> calculateScores(PatientProfile patient) throws ScoreException {
        Map<Category, Map<String, Double>> patientVector = tfOnlyScoreComputations.tfVector(patient);

        List<Score> scores = new ArrayList<>();
        for (Map.Entry<DiseaseProfile, Map<Category, Map<String, Double>>> diseaseMapEntry : diseaseTfVectors.entrySet()) {
            if (!ConflictUtil.isConflict(diseaseMapEntry.getKey(), patient)) {
                Score score = new Score(diseaseMapEntry.getKey());
                double sim = 0.0;
                for (Map.Entry<Category, Map<String, Double>> classMapEntry : patientVector.entrySet()) {
                    sim += calcSimilarity(classMapEntry.getValue(),
                    diseaseMapEntry.getValue().get(classMapEntry.getKey()),
                    idfVector.get(classMapEntry.getKey()),
                    WEIGHTS.get(classMapEntry.getKey()), 1.0 / classMapEntry.getValue().size());
                }
                score.setSimilarity(sim);
                score.setRank(sim + Math.log(diseaseMapEntry.getKey().getOverallFrequency().frequency));
                scores.add(score);
            }
        }
        return normalizeLogScoresAndSort(scores);

    }

    private double calcSimilarity(Map<String, Double> patient, Map<String, Double> disease, Map<String, Double> diseaseIdf, Double posWeight, Double negWeight) {
        double sim = 0.0;
        posWeight = posWeight != null ? posWeight : 1.0;
        for (String s : patient.keySet()) {
            double localSim = (diseaseIdf.get(s) + Math.log(disease.get(s))) * negWeight;
            sim += localSim > 0.0 ? localSim * posWeight : localSim;
        }
        return sim;
    }

    @Override
    public String getScoreName() {
        return "IDFScore";
    }

    @Override
    public void setDiseases(List<DiseaseProfile> diseases) {
        this.idfVector = this.idfOnlyYesScoreComputations.setStatistics(new IDFStatistics(diseases)).idfVector();
        this.diseaseTfVectors = new HashMap<>();
        for (DiseaseProfile disease : diseases) {
            this.diseaseTfVectors.put(disease, this.tfOnlyScoreComputations.tfVector(disease));
        }
    }

    @Override
    protected double modifyThreshold(ScoreType type) {
        return Math.log(type.threshold);
    }

}