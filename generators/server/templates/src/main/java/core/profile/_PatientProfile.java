package <%= config.packages.profile %>;

import <%= config.packages.category %>.*;
import lombok.Data;

import java.util.EnumSet;

/**
 * Profile class for patients.
 */
@Data
public class PatientProfile {

	//generated nonExclusive categories
	<%_ categories.forEach(function(value) { _%>
	<%_ if(!value.exclusive) {_%>
	private EnumSet<<%= value.name %>> <%= value.varName %>;
	<%_ }}); _%>

	//generated exclusive categories
	<%_ categories.forEach(function(value) { _%>
	<%_ if(value.exclusive) {_%>
	private <%= value.name %> <%= value.varName %>;
	<%_ }}); _%>

}
