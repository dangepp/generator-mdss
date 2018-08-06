package <%= config.packages.score %>;

import <%= config.packages.profile %>.PatientProfile;
import <%= config.packages.profile %>.DiseaseProfile;

import java.util.List;

/**
 * Calculates Scores based on the previously set DiseaseBase
 */
public interface ScoreFactory {

    /**
     * Calculates scores to all diseases this factory holds.
     * @param patient - the patient for score calculation
     * @return - a list of scores
     * @throws ScoreException - if something went wrong calculating the scores.
     */
    List<Score> calculateScores(PatientProfile patient) throws ScoreException;

    /**
     * Ranks the scores and only returns the best selection of it.
     * @param diagnoses - the score to be ranked
     * @return - a selection of scores
     */
    List<Score> bestOf(List<Score> diagnoses);

    /**
     * The name of the score.
     * @return - the name of the score used
     */
    String getScoreName();

    /**
     * Sets the disaese base for this factory.
     * @param diseases - the disesase to be used to calculate scores
     */
    void setDiseases(List<DiseaseProfile> diseases);

}
