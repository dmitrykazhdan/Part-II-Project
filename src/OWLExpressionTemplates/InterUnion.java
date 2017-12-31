package OWLExpressionTemplates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression representing either a union or an intersection or a complement expression.
public class InterUnion extends ClsExpStr {

	private String name;
	private ExpressionGroup expressions;
		
	public InterUnion (ClassExpressionType expType, String name, ExpressionGroup expressions)  {
		super(expType);
		this.name = name;
		this.expressions = expressions;
	}
	
	public ExpressionGroup getExpressionGroup() { return expressions; }
}
