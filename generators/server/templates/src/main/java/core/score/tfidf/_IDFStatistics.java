package <%= config.packages.tfidf %>;

import <%= config.packages.profile %>.DiseaseProfile;
import <%= config.packages.enum %>.Likelihood;
import <%= config.packages.util %>.*;
import <%= config.packages.category %>.*;

import lombok.Data;

import java.util.List;

/**
 * IDF statistics based on the disease base
 */
@Data
class IDFStatistics {

    private int numberOfDiseases;

    //generated likelihood categories
    <%_ categories.forEach(function(value) { _%>
    <%_ if(value.type === 'likelihood') {_%>
    private EnumMapCounter<<%= value.name %>, Likelihood> <%= value.varName %> = new EnumMapCounter<>();
    <%_ }}); _%>

    //generated contain categories
    <%_ categories.forEach(function(value) { _%>
    <%_ if(value.type === 'contain') {_%>
    private EnumSetCounter<<%= value.name %>> <%= value.varName %> = new EnumSetCounter<>();
    <%_ }}); _%>

    //generated ratio categories
    <%_ categories.forEach(function(value) { _%>
    <%_ if(value.type === 'ratio') {_%>
    private DoubleRegister<<%= value.name %>> <%= value.varName %> = new DoubleRegister<>();
    <%_ }}); _%>

    
    IDFStatistics(List<DiseaseProfile> diseases) {
        numberOfDiseases = diseases.size();

        for(DiseaseProfile disease : diseases) {
            <%_ categories.forEach(function(value) { _%>
            <%= value.varName %>.add(disease.get<%= value.name %>());
            <%_ }); _%>
        }
    }

}

