package InferenceRules;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression string representing a cardinality expression.
// Namely: minimum, maximum and exact cardinality of object and data properties. 
public class CardExpStr extends ClsExpStr {
	
	private EntityStr property;
	private int cardinality;
	private int lowerBound;
	private int upperBound;
	private GenericExpStr expression;
		
	public CardExpStr(ClassExpressionType expType, EntityStr property, int cardinality, int lowerBound, int upperBound, GenericExpStr expression) {
		
		super(expType);
		this.property = property;
		this.cardinality = cardinality;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.expression = expression;
	}
	
	public int getLowerBound() { return lowerBound; }
	
	public int getUpperBound() { return upperBound; }
	
	public int getCardinality() { return cardinality; }
	
	public EntityStr getProperty() { return property; }
	
	public GenericExpStr getExpression() { return expression; }
	
}
