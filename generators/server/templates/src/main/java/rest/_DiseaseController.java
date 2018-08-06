package <%= config.packages.rest %>;

import <%= config.packages.restModel %>.DiseaseModel;
import <%= config.packages.service %>.DiseaseService;
import <%= config.packages.restFile %>.DiseaseMapper;
import <%= config.packages.restFile %>.MapperException;
import <%= config.packages.restException %>.UnprocessableEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing the disease knowledge base.
 */
@Slf4j
@RestController
public class DiseaseController {

    private DiseaseService diseaseService;
    
    private DiseaseMapper mapper;

    @Autowired
    public DiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
        this.mapper = new DiseaseMapper();
    }

    /**
     * POST  /diseases/xls : parse, validate and save the diseases in the given .xls file.
     * @param excelfile - the diseas .xls file to be saved
     * @param redirectAttributes - RedirectAttributes to redirect the request 
     * @return - redirect message
     */
    @RequestMapping(value = "/diseases/xls", method = RequestMethod.POST)
    public String postXlsDiseases(@RequestParam("excelFile") MultipartFile excelfile,
                                    RedirectAttributes redirectAttributes) {
        try {
            diseaseService.saveDisease(mapper.convertFile(excelfile));
        } catch (MapperException e) {
            throw new UnprocessableEntityException("Could not Map input of the xlsx file", e);
        }
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + excelfile.getOriginalFilename() + "!");
        return "redirect:/";

	}

    /**
     * GET  /diseases : get all saved diseases.
     * @return - all diseases saved in the db
     */
    @RequestMapping(value = "/diseases",method = RequestMethod.GET)
    public List<DiseaseModel> getDiseases() {
        return diseaseService.getDiseases();
    }
}