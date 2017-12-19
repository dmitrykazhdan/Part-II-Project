package InferenceRules;

import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;

public class OWLAxiomString {

	private AxiomType constructor;
	private List<ClassExpressionString> children;
	
	public OWLAxiomString (AxiomType constructor, List<ClassExpressionString> children) {
		
		this.constructor = constructor;
		this.children = children;
	}

}

