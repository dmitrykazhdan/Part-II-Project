package OWLExpressionTemplates;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression string representing an "exists" or a "for all" expression.
// Namely: some values from, all values from, and has value for object and data properties.
public class ExistsOrForAll extends ClsExpStr {

	private TemplatePrimitive property;
	private GenericExpStr expression;

	public ExistsOrForAll(ClassExpressionType expType, TemplatePrimitive property, GenericExpStr expression) {
		super(expType);
		this.property = property;
		this.expression = expression;
	}
	
	public TemplatePrimitive getProperty() { return property; }
	
	public GenericExpStr getExpression() { return expression; }

}
