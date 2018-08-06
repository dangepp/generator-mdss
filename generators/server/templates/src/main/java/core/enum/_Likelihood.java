package <%= config.packages.enum %>;

/**
 * Enumeration of all likelihoods for features of diseases.
 */
public enum Likelihood {

	<%_ otherLikelihood.forEach(function(value) { _%>
	<%= value.name %>(<%= value.percentage %>),
	<%_ }); _%>
	YES(<%= yesLikelihood %>),
	NO(<%= noLikelihood %>);

	public final double probability;

	Likelihood(double probability) {
		this.probability = probability;
	}

	public static Likelihood[] yesValues(){
		return new Likelihood[] {
			<%_ yesValues.forEach(function(value) { _%>
			<%= value %>,
			<%_ }); _%>
			YES
		};
	}

}
