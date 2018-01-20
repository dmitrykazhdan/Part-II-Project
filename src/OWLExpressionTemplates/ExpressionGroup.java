package OWLExpressionTemplates;

public class ExpressionGroup {

	private String groupName;
	private String anonymousGroupName = "";
	private ClsExpStr[] namedExpressions;
	private boolean hasAnonymousExpressions;
	
	public ExpressionGroup(String groupName, ClsExpStr[] namedExpressions) {
		this.groupName = groupName;
		this.namedExpressions = namedExpressions;
		this.hasAnonymousExpressions = false;
		this.anonymousGroupName = "";
	}
	
	public ExpressionGroup(String groupName, ClsExpStr[] namedExpressions, String anonymousGroupName) {
		this.groupName = groupName;
		this.namedExpressions = namedExpressions;
		this.hasAnonymousExpressions = true;
		this.anonymousGroupName = anonymousGroupName;
	}
	
	public ClsExpStr[] getNamedExpressions() { return namedExpressions; }
	
	public boolean hasAnonymousExpressions() {return hasAnonymousExpressions; }
	
	public String getAnonymousGroupName() { return anonymousGroupName; }
	
}
