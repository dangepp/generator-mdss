package <%= config.packages.category %>;

/**
 * Feature enumeration for the category <%= name %>
 */
public enum <%= name %> <%- hasParts ? 'implements HasParts<'+name+'>' : '' %>{
	<%_ for (var i = 0; i < values.length; i++) {_%>
    	<%= values[i] %><%= (i !== values.length -1)? ',' : '' %>
	<%_}_%>

	<%_ if(hasParts) { _%>
	;
	@Override
	public boolean isPartOf(<%- name %> whole) {
    return this.name().startsWith(whole.name());
	}
	<%_}_%>

}
