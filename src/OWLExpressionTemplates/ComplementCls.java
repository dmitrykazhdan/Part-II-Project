package OWLExpressionTemplates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.ClassExpressionType;

public class ComplementCls extends ClsExpStr {

	private ClsExpStr expression;
	
	public ComplementCls (ClassExpressionType expType, ClsExpStr expression)  {
		super(expType);
		this.expression = expression;
	}

	public ClsExpStr getSubExpression() { return expression; }
}
