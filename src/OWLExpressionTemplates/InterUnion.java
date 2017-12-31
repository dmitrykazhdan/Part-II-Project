package OWLExpressionTemplates;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression representing either a union or an intersection or a complement expression.
public class InterUnion extends ClsExpStr {

	private ExpressionGroup expressions;
		
	private InterUnion (ClassExpressionType expType, ExpressionGroup expressions)  {
		super(expType);
		this.expressions = expressions;
	}
	
	public ExpressionGroup getExpressionGroup() { return expressions; }
	
	
	// Static methods used to generate expressions:
	
	public static InterUnion createUnionExpression(ExpressionGroup expressions) {
		return new InterUnion(ClassExpressionType.OBJECT_UNION_OF, expressions);
	}
	
	public static InterUnion createIntersectionExpression(ExpressionGroup expressions) {
		return new InterUnion(ClassExpressionType.OBJECT_INTERSECTION_OF, expressions);
	}
}
