package <%= config.packages.enum %>;

/**
 * Enumeration of all frequencies for diseases.
 */
public enum Frequency {

	<%_ for (var i = 0; i < frequencies.length; i++) {_%>
    	<%= frequencies[i].name %>(<%= frequencies[i].percentage %>)<%= (i !== frequencies.length -1)? ',' : ';' %>
	<%_}_%>

    public final double frequency;

    Frequency(double frequency) {
        this.frequency = frequency;
    }

}
