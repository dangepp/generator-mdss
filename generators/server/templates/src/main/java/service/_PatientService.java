package <%= config.packages.service %>;

import <%= config.packages.profileRepo %>.DiseaseRepository;
import <%= config.packages.score %>.ScoreFactory;
import <%= config.packages.score %>.ScoreException;
import <%= config.packages.score %>.Score;
import <%= config.packages.serviceMapper %>.PatientMapper;
import <%= config.packages.serviceMapper %>.ScoreMapper;
import <%= config.packages.restModel %>.PatientModel;
import <%= config.packages.restModel %>.ScoreModel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
@Validated
@Scope("singleton")
public class PatientService {

	private DiseaseRepository diseaseRepository;

	private ScoreFactory scoreFactory;

	@Autowired
	public PatientService(DiseaseRepository diseaseRepository,
						  ScoreFactory scoreFactory) {
		this.scoreFactory = scoreFactory;
		this.diseaseRepository = diseaseRepository;
		this.reNewDiseaseBase();
	}

	/**
	 * Diagnoses a PatientModel
	 * @param model - The patient to be diagnosed
	 * @return The ranked scores
	 * @throws ServiceException if the patient could not be diagnosed
	 */
	public List<ScoreModel> diagnosePatient(@Valid PatientModel model) throws ServiceException {
        log.info(this.scoreFactory.getScoreName());
        try {
            return ScoreMapper.INSTANCE.entityToModel(
            		this.scoreFactory
							.bestOf(this.scoreFactory
									.calculateScores(PatientMapper.INSTANCE
											.modelToEntity(model))));
        } catch (ScoreException e) {
            throw new ServiceException("Could not diagnose Patient", e);
        }
	}

	/**
	 * Refreshes the disease base
	 */
	void reNewDiseaseBase() {
		this.scoreFactory.setDiseases(this.diseaseRepository.findAll());
	}
}