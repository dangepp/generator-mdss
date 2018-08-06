package <%= config.packages.enum %>;

/**
 * Enumeration for all categories.
 */
public enum Category {
		<%_ for (var i = 0; i < categories.length; i++) {_%>
    	<%= categories[i].enumName %><%= (i !== categories.length -1)? ',' : '' %>
	<%_}_%>
}
