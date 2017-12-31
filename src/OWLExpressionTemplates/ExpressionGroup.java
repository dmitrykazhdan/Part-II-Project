package OWLExpressionTemplates;

import java.util.List;

public class ExpressionGroup {

	private ClsExpStr[] namedExpressions;
	private ClsExpStr[] anonymousExpressions;
	
	public ExpressionGroup(ClsExpStr[] namedExpressions, ClsExpStr[] anonymousExpressions) {
		this.namedExpressions = namedExpressions;
		this.anonymousExpressions = anonymousExpressions;
	}
	
	public ClsExpStr[] getNamedExpressions() { return namedExpressions; }
	
}
