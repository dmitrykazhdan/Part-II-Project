package RuleRestrictions;

public class RuleRestrictions {

	private RuleRestriction[] premiseRestrictions;
	private RuleRestriction[] conclusionRestrictions;
	
	public RuleRestrictions(RuleRestriction[] premiseRestrictions, RuleRestriction[] conclusionRestrictions) {
		this.premiseRestrictions = premiseRestrictions;
		this.conclusionRestrictions = conclusionRestrictions;
	}
	
	public RuleRestrictions() {
		this(new RuleRestriction[]{}, new RuleRestriction[]{});
	}
	
	public RuleRestrictions(RuleRestriction... restrictions) {
		this(restrictions, new RuleRestriction[]{});		
	}
	
	
	public RuleRestriction[] getAllRestrictions() {
		RuleRestriction[] allRestrictions = new RuleRestriction[premiseRestrictions.length + conclusionRestrictions.length];
		
		for (int i = 0; i < premiseRestrictions.length; i++) {
			allRestrictions[i] = premiseRestrictions[i];
		}
		
		for (int i = 0; i < conclusionRestrictions.length; i++) {
			allRestrictions[i + premiseRestrictions.length] = conclusionRestrictions[i];
		}
		
		return allRestrictions;
	}
	
	public RuleRestriction[] getPremiseRestrictions() { return premiseRestrictions; }
		
	public RuleRestriction[] conclusionRestrictions() { return conclusionRestrictions; }
	
}
