package InferenceRules;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression string representing a cardinality expression.
// Namely: minimum, maximum and exact cardinality of object and data properties. 
public class CardExpStr extends ClsExpStr {
	
	private EntityStr property;
	private String cardinality;
	private String lowerBound;
	private ClsExpStr expression;
	private boolean isRelativeBound;
		
	public CardExpStr(ClassExpressionType expType, boolean isRelativeBound, EntityStr property, String cardinality, String lowerBound, ClsExpStr expression) {
		
		super(expType);
		this.property = property;
		this.cardinality = cardinality;
		this.lowerBound = lowerBound;
		this.expression = expression;
		this.isRelativeBound = isRelativeBound;
	}
	
	public String getLowerBound() { return lowerBound; }
	
	public String getCardinality() { return cardinality; }
	
	public EntityStr getProperty() { return property; }
	
	public ClsExpStr getExpression() { return expression; }
	
	public boolean isRelativeBound() { return isRelativeBound; }
	
}
