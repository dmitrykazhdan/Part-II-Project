package OWLExpressionTemplates;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression string representing a cardinality expression.
// Namely: minimum, maximum and exact cardinality of object and data properties. 
public class CardExpGen extends ClsExpStr {
	
	private TemplatePrimitive property;
	private String cardinality;
	private GenericExpStr expression;
		
	private CardExpGen(ClassExpressionType expType, String cardinality, TemplatePrimitive property, GenericExpStr expression) {		
		super(expType);
		this.property = property;
		this.cardinality = cardinality;
		this.expression = expression;
	}
	
	public String getCardinality() { return cardinality; }
	
	public TemplatePrimitive getProperty() { return property; }
	
	public GenericExpStr getExpression() { return expression; }
	
	
	// Static methods used to generate the expressions.
	
	
	// Data cardinality expressions:
	public static CardExpGen createDataExactCard(String cardinality, String property, String expression) {
		return createDataCardExp(ClassExpressionType.DATA_EXACT_CARDINALITY, cardinality, property, expression);			
	}
		
	public static CardExpGen createDataMaxCard(String cardinality, String property, String expression) {
		return createDataCardExp(ClassExpressionType.DATA_MAX_CARDINALITY, cardinality, property, expression);
	}
	
	public static CardExpGen createDataMinCard(String cardinality, String property, String expression) {
		return createDataCardExp(ClassExpressionType.DATA_MIN_CARDINALITY, cardinality, property, expression);
	}
	
	
	private static CardExpGen createDataCardExp(ClassExpressionType expType, String cardinality, String property, String expression) {
		return new CardExpGen(expType, cardinality, new TemplateDataProperty(property), new TemplateDataRange(expression));
	}
	
	
	
	// Object cardinality expressions:
	public static CardExpGen createObjExactCard(String cardinality, String property, String expression) {
		return createObjCardExp(ClassExpressionType.OBJECT_EXACT_CARDINALITY, cardinality, property, expression);
	}
	
	public static CardExpGen createObjExactCard(String cardinality, String property, ClsExpStr expression) {
		return createObjCardExp(ClassExpressionType.OBJECT_EXACT_CARDINALITY, cardinality, property, expression);
	}
	
	
	public static CardExpGen createObjMaxCard(String cardinality, String property, String expression) {
		return createObjCardExp(ClassExpressionType.OBJECT_MAX_CARDINALITY, cardinality, property, expression);
	}
	
	
	public static CardExpGen createObjMaxCard(String cardinality, String property, ClsExpStr expression) {
		return createObjCardExp(ClassExpressionType.OBJECT_MAX_CARDINALITY, cardinality, property, expression);
	}
	
	
	public static CardExpGen createObjMinCard(String cardinality, String property, String expression) {
		return createObjCardExp(ClassExpressionType.OBJECT_MIN_CARDINALITY, cardinality, property, expression);
	}
	
	public static CardExpGen createObjMinCard(String cardinality, String property, ClsExpStr expression) {
		return createObjCardExp(ClassExpressionType.OBJECT_MIN_CARDINALITY, cardinality, property, expression);
	}
	
	
	private static CardExpGen createObjCardExp(ClassExpressionType expType, String cardinality, String property, String expression) {
		return new CardExpGen(expType, cardinality, new TemplateObjectProperty(property), new AtomicCls(expression));
	}
	
	private static CardExpGen createObjCardExp(ClassExpressionType expType, String cardinality, String property, ClsExpStr expression) {
		return new CardExpGen(expType, cardinality, new TemplateObjectProperty(property), expression);
	}
	
}
