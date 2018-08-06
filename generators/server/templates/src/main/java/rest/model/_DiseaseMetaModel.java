package <%= config.packages.restModel %>;

import <%= config.packages.enum %>.Frequency;
import <%= config.packages.enum %>.Category;
import lombok.Data;

import java.util.Set;

/**
 * Meta data of disease.
 */
@Data
public class DiseaseMetaModel extends BaseModel {

	private String id;
    private String diseaseName;
    private Frequency overallFrequency;
    private Set<Category> softConflictCategories;

}
