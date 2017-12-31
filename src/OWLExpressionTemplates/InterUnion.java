package OWLExpressionTemplates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression representing either a union or an intersection or a complement expression.
public class InterUnion extends ClsExpStr {

	private ExpressionGroup expressions;
		
	public InterUnion (ClassExpressionType expType, ExpressionGroup expressions)  {
		super(expType);
		this.expressions = expressions;
	}
	
	public ExpressionGroup getExpressionGroup() { return expressions; }
}
