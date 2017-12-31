package OWLExpressionTemplates;

import java.util.List;

public class ExpressionGroup {

	private String name;
	private GenericExpStr[] namedExpressions;
	private GenericExpStr[] anonymousExpressions;
	
	public ExpressionGroup(String name, GenericExpStr[] namedExpressions, GenericExpStr[] anonymousExpressions) {
		this.name = name;
		this.namedExpressions = namedExpressions;
		this.anonymousExpressions = anonymousExpressions;
	}
	
	public GenericExpStr[] getNamedExpressions() { return namedExpressions; }
	
}
