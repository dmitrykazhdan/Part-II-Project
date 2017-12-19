package InferenceRules;

import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLObject;

public class OWLAxiomStr {

	private AxiomType constructor;
	private List<GenericExpStr> children;
	
	public OWLAxiomStr (AxiomType constructor, List<GenericExpStr> children) {		
		this.constructor = constructor;
		this.children = children;
	}
	
	public AxiomType getConstructor() {
		return constructor;
	}
	
	public List<GenericExpStr> getExpressions() {
		return children;
	}

}

