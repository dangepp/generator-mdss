package <%= config.packages.restModel %>;

import <%= config.packages.enum %>.*;
import <%= config.packages.category %>.*;
import <%= config.packages.restValidation %>.DependentOn;
import <%= config.packages.restValidation %>.AtLeast;
import <%= config.packages.restValidation %>.ModelConstraint;
import lombok.Data;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Model for diseases.
 */
@Data
@ModelConstraint
public class DiseaseModel extends DiseaseMetaModel {

	//generated likelihood categories
	<%_ categories.forEach(function(value) { _%>
	<%_ if(value.type === 'likelihood') {_%>
		<%_if(value.validationAnnotation.disease) {_%>
	<%- value.validationAnnotation.disease %>
		<%_ } _%>
	private EnumMap<<%= value.name %>, Likelihood> <%= value.varName %>;
	<%_ }}); _%>

	//generated contain categories
	<%_ categories.forEach(function(value) { _%>
	<%_ if(value.type === 'contain') {_%>
		<%_if(value.validationAnnotation.disease) {_%>
	<%- value.validationAnnotation.disease %>
		<%_ } _%>
	private EnumSet<<%= value.name %>> <%= value.varName %>;
	<%_ }}); _%>

	//generated ratio categories
	<%_ categories.forEach(function(value) { _%>
	<%_ if(value.type === 'ratio') {_%>
		<%_if(value.validationAnnotation.disease) {_%>
	<%- value.validationAnnotation.disease %>
		<%_ } _%>
	private EnumMap<<%= value.name %>, Double> <%= value.varName %>;
	<%_ }}); _%>

}
