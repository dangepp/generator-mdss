package <%= config.packages.service %>;

import <%= config.packages.restModel %>.DiseaseModel;
import <%= config.packages.profileRepo %>.DiseaseRepository;
import <%= config.packages.serviceMapper %>.DiseaseMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

import java.util.List;

@Slf4j
@Service
@Validated
public class DiseaseService {

    private DiseaseRepository diseaseRepository;
    private PatientService patientService;

    @Autowired
    public DiseaseService(DiseaseRepository diseaseRepository,
                          PatientService patientService) {
        this.diseaseRepository = diseaseRepository;
        this.patientService = patientService;
    }

    /**
     * Saves the given Disease models and refreshes 
     * the disease base of the PatientService
     * @param model - The diseases to be saved
     */
	public void saveDisease(@Valid List<DiseaseModel> model) {
		diseaseRepository.saveAll(DiseaseMapper.INSTANCE.modelToEntity(model));
		patientService.reNewDiseaseBase();
	}

    /**
     * Returns all saved diseases
     * @return - all saved diseases
     */
	public List<DiseaseModel> getDiseases() {
		return DiseaseMapper.INSTANCE.entityToModel(diseaseRepository.findAll());
	}
}