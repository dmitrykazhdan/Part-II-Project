package InferenceRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression representing either a union or an intersection or a complement expression.
public class InterUnionComp extends ClsExpStr {

	private List<ClsExpStr> subExpressions;
	
	public InterUnionComp (ClassExpressionType expType, List<ClsExpStr> subExpressions)  {
		super(expType);
		this.subExpressions = subExpressions;
	}
	
	public InterUnionComp (ClassExpressionType expType, ClsExpStr... subExpressions)  {
		super(expType);
		this.subExpressions = new ArrayList<ClsExpStr>(Arrays.asList(subExpressions));
	}
	
	public List<ClsExpStr> getSubExpressions() { return subExpressions; }
	
	
}
