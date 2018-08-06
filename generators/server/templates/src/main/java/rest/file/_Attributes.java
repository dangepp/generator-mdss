package <%= config.packages.restFile %>;

import <%= config.packages.category %>.*;

import java.util.Arrays;
import java.util.List;
import java.util.EnumMap;

/**
 * Constant class containing all constants used for disease file mapping.
 */
public class Attributes {

    public static final String ID_HEADER = "ID";
    public static final String NAME_HEADER = "NAME";
    public static final String FREQUENCY_HEADER = "FREQUENCY";

    public static final String CONFLICT_HEADER = "CONFLICT_EXCLUSION";


    public static final String DOES_NOT_APPLY = "DNA";

    public static final String SET_DELIMITER = ",";

    public static final String RATIO_DELIMITER = ":";


    //generated categories
    <%_ categories.forEach(function(value) { _%>
    <%_ if(value.type === 'likelihood') {_%>
    <%_ value.values.forEach(function(val) { _%>
    public static final String <%= lodash.toUpper(lodash.snakeCase(value.name+val)) %> = "<%= lodash.toUpper(lodash.snakeCase(value.name+val)) %>";
    <%_ });} else {_%>
    public static final String <%= lodash.toUpper(lodash.snakeCase(value.name)) %> = "<%= lodash.toUpper(lodash.snakeCase(value.name)) %>";
    <%_ }}); _%>

    //generated map string to category 
    <%_ categories.forEach(function(value) { _%>
    <%_ if(value.type === 'likelihood') {_%>
    public static final EnumMap<<%= value.name %>, String> <%= lodash.toUpper(lodash.snakeCase(value.name)) %>_MAP = new EnumMap<>(<%= value.name %>.class);
    static {
    <%_ value.values.forEach(function(val) { _%> 
        <%= lodash.toUpper(lodash.snakeCase(value.name)) %>_MAP.put(<%= value.name %>.<%= val %>, <%= lodash.toUpper(lodash.snakeCase(value.name+val)) %>);
    <%_ }); _%>
    } 
    <%_}}); _%>
    
    public static List<String> getAllCategories() {
        return Arrays.asList(
        <%_ categories.forEach(function(value) { _%>
        <%_ if(value.type === 'likelihood') {_%>
        <%_ value.values.forEach(function(val) { _%>
        <%= lodash.toUpper(lodash.snakeCase(value.name+val)) %>,
        <%_ });} else {_%>
        <%= lodash.toUpper(lodash.snakeCase(value.name)) %>,
        <%_ }}); _%>
        NAME_HEADER, 
        FREQUENCY_HEADER,
        CONFLICT_HEADER);
    }
}
