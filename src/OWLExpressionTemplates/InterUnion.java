package OWLExpressionTemplates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.ClassExpressionType;

// Expression representing either a union or an intersection or a complement expression.
public class InterUnion extends ClsExpStr {

	private String name;
	private List<ClsExpStr> namedExpressions;
	private List<ClsExpStr> anonymousExpressions;

	
	public InterUnion (ClassExpressionType expType, String name, List<ClsExpStr> namedExpressions, List<ClsExpStr> anonymousExpressions)  {
		super(expType);
		this.name = name;
		this.namedExpressions = namedExpressions;
		this.anonymousExpressions = anonymousExpressions;
	}
	
	public InterUnion (ClassExpressionType expType, String name, ClsExpStr... namedExpressions)  {
		super(expType);
		this.name = name;
		this.namedExpressions = new ArrayList<ClsExpStr>(Arrays.asList(namedExpressions));
		this.anonymousExpressions = new ArrayList<ClsExpStr>();
	}
	

	public List<ClsExpStr> getNamedExpressions() { return namedExpressions; }
	
	
}
