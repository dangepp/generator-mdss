package <%= config.packages.score %>;

import java.util.*;

/**
 * Abstract base class for scores using LOGARITHMIC scale scores
 * and mixing of scores for ranking.
 */
public abstract class AbstractScoreFactory implements ScoreFactory {

    private static final int TOP_MOST = 6;

    @Override
    public List<Score> bestOf(List<Score> diagnoses) {
        Set<Score> selectionSet = new HashSet<>();
        for (ScoreType scoreType : ScoreType.values()) {
            selectionSet.addAll(selectDiagnoses(scoreType, diagnoses));
        }
        List<Score> retScores = new ArrayList<>(selectionSet);
        retScores.sort(Score.comparator(ScoreType.SIMILARITY).reversed());
        return retScores;
    }

    private Set<Score> selectDiagnoses(ScoreType scoreType, List<Score> diagnoses) {
        Set<Score> selectedDiagnoses = new HashSet<>();
        diagnoses.sort(Score.comparator(scoreType).reversed());
        double threshold = modifyThreshold(scoreType);
        int topmost = TOP_MOST;
        for (Score diagnose : diagnoses) {
            if (topmost <= 0 || diagnose.getScore(scoreType) < threshold) {
                break;
            }
            if (diagnose.getScore(ScoreType.SIMILARITY) >= modifyThreshold(ScoreType.SIMILARITY)) {
                selectedDiagnoses.add(diagnose);
            }
            topmost--;
            if (topmost == 0) {
                topmost = Integer.MAX_VALUE;
                threshold = diagnose.getScore(scoreType);
            }
        }
        return selectedDiagnoses;
    }

    protected abstract double modifyThreshold(ScoreType type);

    protected List<Score> normalizeLogScoresAndSort(List<Score> scores) {
        Optional<Score> optRankMax = scores.stream().max(Score.comparator(ScoreType.RANK));
        Optional<Score> optSimMax = scores.stream().max(Score.comparator(ScoreType.SIMILARITY));
        if (optRankMax.isPresent() && optSimMax.isPresent()) {
            double rankMax = optRankMax.get().getRank();
            double simMax = optSimMax.get().getSimilarity();
            scores.forEach(a -> {
                a.setRank(a.getRank() - rankMax);
                a.setSimilarity(a.getSimilarity() - simMax);
            });
        }
        scores.sort(Score.comparator(ScoreType.SIMILARITY));
        return scores;
    }
}
