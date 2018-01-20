package InferenceRules;

// Abstract cardinality restriction class.
public abstract class CardinalityRestriction implements RuleRestriction {
	
	// Cardinality identifier.
	private String cardinality;
	
	// Specify which type of cardinality this is.
	private CardinalitySign type;

	public CardinalityRestriction(String cardinality, CardinalitySign type) {
		this.cardinality = cardinality;
		this.type = type;
	}
	
	public String getCardinality() { return cardinality; }
	
	public CardinalitySign getCardinalityType() { return type; }
}
