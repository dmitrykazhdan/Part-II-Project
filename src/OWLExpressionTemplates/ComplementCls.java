package OWLExpressionTemplates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.ClassExpressionType;

public class ComplementCls extends ClsExpStr {

	private ClsExpStr expression;
	
	public ComplementCls (ClsExpStr expression)  {
		super(ClassExpressionType.OBJECT_COMPLEMENT_OF);
		this.expression = expression;
	}

	public ClsExpStr getSubExpression() { return expression; }
}
