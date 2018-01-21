package RuleRestrictions;

public class AbsCardinalityRestriction extends CardinalityRestriction {

	private Integer absoluteBound;
	
	public AbsCardinalityRestriction(String cardinalityName, CardinalitySign type, Integer absoluteBound) {
		super(cardinalityName, type);
		this.absoluteBound = absoluteBound;
	}
		
	public Integer getAbsoluteBound() { return absoluteBound; }
}
