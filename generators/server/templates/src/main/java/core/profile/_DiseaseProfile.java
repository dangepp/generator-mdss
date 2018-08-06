package <%= config.packages.profile %>;

import <%= config.packages.enum %>.*;
import <%= config.packages.category %>.*;
import lombok.Data;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

/**
 * Profile class for diseases.
 */
@Data
public class DiseaseProfile {

	private String id;
  private String diseaseName;
	private Frequency overallFrequency;
	private Set<Category> softConflictCategories;

	//generated likelihood categories
	<%_ categories.forEach(function(value) { _%>
	<%_ if(value.type === 'likelihood') {_%>
	private EnumMap<<%= value.name %>, Likelihood> <%= value.varName %>;
	<%_ }}); _%>

	//generated contain categories
	<%_ categories.forEach(function(value) { _%>
	<%_ if(value.type === 'contain') {_%>
	private EnumSet<<%= value.name %>> <%= value.varName %>;
	<%_ }}); _%>

	//generated ratio categories
	<%_ categories.forEach(function(value) { _%>
	<%_ if(value.type === 'ratio') {_%>
	private EnumMap<<%= value.name %>, Double> <%= value.varName %>;
	<%_ }}); _%>

}
