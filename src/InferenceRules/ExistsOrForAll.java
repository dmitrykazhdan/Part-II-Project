package InferenceRules;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression string representing an "exists" or a "for all" expression.
// Namely: some values from, all values from, and has value for object and data properties.
public class ExistsOrForAll extends ClsExpStr {

	private EntityStr property;
	private ClsExpStr expression;

	public ExistsOrForAll(ClassExpressionType expType, EntityStr property, ClsExpStr expression) {
		super(expType);
		this.property = property;
		this.expression = expression;
	}
	
	public EntityStr getProperty() { return property; }
	
	public ClsExpStr getExpression() { return expression; }

}
