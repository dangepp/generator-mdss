package <%= config.packages.rest %>;

import <%= config.packages.restModel %>.PatientModel;
import <%= config.packages.service %>.PatientService;
import <%= config.packages.service %>.ServiceException;
import <%= config.packages.restModel %>.ScoreModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * REST controller for diagnosing patients.
 */
@Slf4j
@RestController
public class PatientController {

	private PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * POST  /patient : diagnose the given patient.
     *
     * @param model the patient to be diagnosed
     * @return the ranked scores of the patient
     */
    @RequestMapping(value = "/patient",method = RequestMethod.POST)
    public List<ScoreModel> postPatient(@RequestBody PatientModel model) throws ServiceException {
        log.info("Got patient: {}",model);
        return patientService.diagnosePatient(model);
    }

}