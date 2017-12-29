package OWLExpressionTemplates;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression string representing a cardinality expression.
// Namely: minimum, maximum and exact cardinality of object and data properties. 
public class CardExpStr extends ClsExpStr {
	
	private TemplatePrimitive property;
	private String cardinality;
	private String lowerBound;
	private GenericExpStr expression;
	private boolean isRelativeBound;
		
	public CardExpStr(ClassExpressionType expType, String cardinality, boolean isRelativeBound, String lowerBound, TemplatePrimitive property, GenericExpStr expression) {
		
		super(expType);
		this.property = property;
		this.cardinality = cardinality;
		this.lowerBound = lowerBound;
		this.expression = expression;
		this.isRelativeBound = isRelativeBound;
	}
	
	public String getLowerBound() { return lowerBound; }
	
	public String getCardinality() { return cardinality; }
	
	public TemplatePrimitive getProperty() { return property; }
	
	public GenericExpStr getExpression() { return expression; }
	
	public boolean isRelativeBound() { return isRelativeBound; }
	
}
