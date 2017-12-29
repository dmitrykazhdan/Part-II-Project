package OWLExpressionTemplates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression representing either a union or an intersection or a complement expression.
public class InterUnion extends ClsExpStr {

	private List<ClsExpStr> subExpressions;
	
	public InterUnion (ClassExpressionType expType, List<ClsExpStr> subExpressions)  {
		super(expType);
		this.subExpressions = subExpressions;
	}
	
	public InterUnion (ClassExpressionType expType, ClsExpStr... subExpressions)  {
		super(expType);
		this.subExpressions = new ArrayList<ClsExpStr>(Arrays.asList(subExpressions));
	}
	
	public List<ClsExpStr> getSubExpressions() { return subExpressions; }
	
	
}
