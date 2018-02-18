package RuleRestrictions;

public class UnequalLiteralsRestriction implements RuleRestriction {
	private String firstLiteral;
	private String secondLiteral;
	
	public UnequalLiteralsRestriction(String firstLiteral, String secondLiteral) {
		this.firstLiteral = firstLiteral;
		this.secondLiteral = secondLiteral;
	}
	
	public String getSecondLiteral() { return secondLiteral; }
	
	public String getFirstLiteral() { return firstLiteral; }
}
