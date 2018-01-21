package OWLExpressionTemplates;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression string representing an "exists" or a "for all" expression.
// Namely: some values from, all values from, and has value for object and data properties.
public class ExistsOrForAll extends ClsExpStr {

	private TemplatePrimitive property;
	private GenericExpStr expression;

	private ExistsOrForAll(ClassExpressionType expType, TemplatePrimitive property, GenericExpStr expression) {
		super(expType);
		this.property = property;
		this.expression = expression;
	}
	
	public TemplatePrimitive getProperty() { return property; }
	
	public GenericExpStr getExpression() { return expression; }

	
	// Static methods used to generate expressions:
		
	// Literal expressions:
	public static ExistsOrForAll createLiteralSomeValFrom(String property, String dataLiteral) {
		return new ExistsOrForAll(ClassExpressionType.DATA_HAS_VALUE, 
				new TemplateDataProperty(property), new TemplateLiteral(dataLiteral));
	}
	
	public static ExistsOrForAll createIndividualSomeValFrom(String property, String individual) {
		return new ExistsOrForAll(ClassExpressionType.OBJECT_HAS_VALUE, 
				new TemplateObjectProperty(property), new TemplateIndividual(individual));
	}
	
		
	// Data expressions
	public static ExistsOrForAll createDataSomeValFrom(String property, String dataRange) {
		return new ExistsOrForAll(ClassExpressionType.DATA_SOME_VALUES_FROM, 
				new TemplateDataProperty(property), new TemplateDataRange(dataRange));
	}
	

	// Object expressions:
	public static ExistsOrForAll createObjSomeValFrom(String property, String cls) {
		return new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new TemplateObjectProperty(property), new AtomicCls(cls));
	}
		
	public static ExistsOrForAll createObjSomeValFrom(String property, ClsExpStr cls) {
		return new ExistsOrForAll(ClassExpressionType.OBJECT_SOME_VALUES_FROM, 
				new TemplateObjectProperty(property), cls);
	}
			
	public static ExistsOrForAll createObjAllValFrom(String property, String cls) {
		return new ExistsOrForAll(ClassExpressionType.OBJECT_ALL_VALUES_FROM, 
				new TemplateObjectProperty(property), new AtomicCls(cls));
	}

}
