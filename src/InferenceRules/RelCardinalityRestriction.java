package InferenceRules;

public class RelCardinalityRestriction extends CardinalityRestriction {

	private String relativeBound;
	
	public RelCardinalityRestriction(String largerCardinality, CardinalitySign type, String relativeBound) {
		super(largerCardinality, type);
		this.relativeBound = relativeBound;
	}
		
	public String getSmallerCardinality() { return relativeBound; }
}
