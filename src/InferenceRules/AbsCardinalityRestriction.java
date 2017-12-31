package InferenceRules;

public class AbsCardinalityRestriction extends CardinalityRestriction {

	private Integer smallerCardinality;
	
	public AbsCardinalityRestriction(String largerCardinality, Integer smallerCardinality, boolean strictInequality) {
		super(largerCardinality, strictInequality);
		this.smallerCardinality = smallerCardinality;
	}
		
	public Integer getSmallerCardinality() { return smallerCardinality; }
}
