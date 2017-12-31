package OWLExpressionTemplates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLObject;

public class OWLAxiomStr {

	private AxiomType constructor;
	private List<GenericExpStr> children;
	private ExpressionGroup expGroup;
	
	public OWLAxiomStr (AxiomType constructor, List<GenericExpStr> children) {		
		this.constructor = constructor;
		this.children = children;
	}
	
	public OWLAxiomStr (AxiomType constructor, ExpressionGroup expGroup) {		
		this.constructor = constructor;
		this.children = children;
		this.expGroup = expGroup;
	}
	
	public OWLAxiomStr (AxiomType constructor, GenericExpStr... children) {		
		this.constructor = constructor;		
		this.children = new ArrayList<GenericExpStr>(Arrays.asList(children));
	}
	
	public AxiomType getConstructor() {
		return constructor;
	}
	
	public List<GenericExpStr> getExpressions() {
		return children;
	}

}

