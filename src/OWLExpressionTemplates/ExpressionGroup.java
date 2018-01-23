package OWLExpressionTemplates;


//Class containing an abstract group expression.
//A group consists of:
//1) Named expressions, which are used in other places of the rule and are part of the
//rule's variable instantiation. 

//2) Anonymous expressions. These are expressions whose size is not pre-defined.
//They match an group of classes participating in a conjunction, and group these under a common nae,e.
//These are part of the rule's group instantiation.
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
