package <%= config.packages.restModel %>;

import <%= config.packages.category %>.*;
import <%= config.packages.restValidation %>.DependentOn;
import <%= config.packages.restValidation %>.ModelConstraint;

import lombok.Data;

import java.util.EnumSet;

/**
 * Model for patients.
 */
@Data
@ModelConstraint
public class PatientModel extends BaseModel {

	//generated nonExclusive categories
	<%_ categories.forEach(function(value) { _%>
	<%_ if(!value.exclusive) {_%>
		<%_if(value.validationAnnotation.patient) {_%>
	<%- value.validationAnnotation.patient %>
			<%_ } _%>
	private EnumSet<<%= value.name %>> <%= value.varName %>;
	<%_ }}); _%>

	//generated exclusive categories
	<%_ categories.forEach(function(value) { _%>
	<%_ if(value.exclusive) {_%>
		<%_if(value.validationAnnotation.patient) {_%>
	<%- value.validationAnnotation.patient %>
		<%_ } _%>
	private <%= value.name %> <%= value.varName %>;
	<%_ }}); _%>

}
