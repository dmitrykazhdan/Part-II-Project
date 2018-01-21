package RuleRestrictions;

public class RelCardinalityRestriction extends CardinalityRestriction {

	private String relativeBound;
	
	public RelCardinalityRestriction(String cardinality, CardinalitySign type, String relativeBound) {
		super(cardinality, type);
		this.relativeBound = relativeBound;
	}
		
	public String getRelativeBound() { return relativeBound; }
}
