package InferenceRules;

import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class RuleString {

	private List<OWLAxiomString> ruleContents;
	private int premiseNumber;
	
	public boolean matchPremises(List<OWLAxiom> premises) {
		return false;
	}
	
	
	public boolean match(OWLAxiom axiom, OWLAxiomString pattern) {
		
		if (axiom.isOfType(pattern.getConstructor())) {
			
			if (axiom.isOfType(AxiomType.FUNCTIONAL_DATA_PROPERTY)) {
				
			}
			
		}
		return false;
	}
	
	private boolean match(OWLClassExpression classExp, ClassExpressionString pattern) {
		return false;
	}
	
}
