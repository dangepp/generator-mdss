package <%= config.packages.restFile %>;

import <%= config.packages.category %>.*;
import <%= config.packages.restModel %>.DiseaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base for file specific disease mappers.
 */
class DiseaseBaseMapper {

    /**
     * Maps data to DiseaseModels.
     * @param data - the raw disease data. Contains diseaseId : disaseData
     * @return - the mapped disease models
     * @throws MapperException if the diseases could not be mapped
     */
    List<DiseaseModel> convertData(Map<String, Map<String, String>> data) throws MapperException {
        List<DiseaseModel> retList = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> stringMapEntry : data.entrySet()) {
            DiseaseModel model = new DiseaseModel();
            model.setId(stringMapEntry.getKey());
            try {
                model = MappingHelper.getBaseAttributesModel(stringMapEntry.getValue(), model);

                //generated likelihood categories
	            <%_ categories.forEach(function(value) { _%>
                <%_ if(value.type === 'likelihood') {_%>
                model.set<%= value.name %>(MappingHelper.generateLikelihoodMapForCategory(<%= value.name %>.class, stringMapEntry.getValue(), Attributes.<%= lodash.toUpper(lodash.snakeCase(value.name)) %>_MAP));
                <%_ }}); _%>
    
                //generated contain categories
                <%_ categories.forEach(function(value) { _%>
                <%_ if(value.type === 'contain') {_%>
                model.set<%= value.name %>(MappingHelper.stringToEnumSet(<%= value.name %>.class, stringMapEntry.getValue().get(Attributes.<%= lodash.toUpper(lodash.snakeCase(value.name)) %>)));
                <%_ }}); _%>
            
                //generated ratio categories
                <%_ categories.forEach(function(value) { _%>
                <%_ if(value.type === 'ratio') {_%>
                model.set<%= value.name %>(MappingHelper.stringToRatio(<%= value.name %>.class, stringMapEntry.getValue().get(Attributes.<%= lodash.toUpper(lodash.snakeCase(value.name)) %>)));
                <%_ }}); _%>


            } catch (MapperExecutionException e) {
                throw new MapperException(String.format("Could not map Disease with id=%s", model.getId()), e);
            }
            retList.add(model);
        }
        return retList;
    }


}
