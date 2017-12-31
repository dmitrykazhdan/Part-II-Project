package InferenceRules;

public abstract class CardinalityRestriction implements RuleRestriction {
	
	private String largerCardinality;
	private boolean strictInequality;

	public CardinalityRestriction(String largerCardinality, boolean strictInequality) {
		this.largerCardinality = largerCardinality;
		this.strictInequality = strictInequality;
	}
	
	public String getLargerCardinality() { return largerCardinality; }
	
	public boolean isStrictInequality() { return strictInequality; }

}
