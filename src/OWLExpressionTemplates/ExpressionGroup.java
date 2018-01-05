package OWLExpressionTemplates;

public class ExpressionGroup {

	private String groupName;
	private String anonymousGroupName = "";
	private GenericExpStr[] namedExpressions;
	private boolean hasAnonymousExpressions;
	
	public ExpressionGroup(String groupName, GenericExpStr[] namedExpressions) {
		this.groupName = groupName;
		this.namedExpressions = namedExpressions;
		this.hasAnonymousExpressions = false;
		this.anonymousGroupName = "";
	}
	
	public ExpressionGroup(String groupName, GenericExpStr[] namedExpressions, String anonymousGroupName) {
		this.groupName = groupName;
		this.namedExpressions = namedExpressions;
		this.hasAnonymousExpressions = true;
		this.anonymousGroupName = anonymousGroupName;
	}
	
	public GenericExpStr[] getNamedExpressions() { return namedExpressions; }
	
	public boolean hasAnonymousExpressions() {return hasAnonymousExpressions; }
	
	public String getAnonymousGroupName() { return anonymousGroupName; }
	
}
