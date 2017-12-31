package InferenceRules;

public class RelCardinalityRestriction extends CardinalityRestriction {

	private String smallerCardinality;
	
	public RelCardinalityRestriction(String largerCardinality, String smallerCardinality, boolean strictInequality) {
		super(largerCardinality, strictInequality);
		this.smallerCardinality = smallerCardinality;
	}
		
	public String getSmallerCardinality() { return smallerCardinality; }
}
