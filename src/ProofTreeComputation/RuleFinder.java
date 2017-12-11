package ProofTreeComputation;

import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

public class RuleFinder {

	
	
	
	public static InferenceRule findMultiplePremiseRule(OWLAxiom conclusion, List<OWLAxiom> premises) {
		
		
		return null;
	}
	
	
	public static InferenceRule findSinglePremiseRule(OWLAxiom conclusion, OWLAxiom premise) {
		

		return null;
	}
	
	
	
	public static InferenceRule findRuleApplication(List<OWLAxiom> premises, OWLAxiom conclusion) {
		
		if (premises.size() == 1) {
			return findSinglePremiseRule(premises.get(0), conclusion);
		} else {
			return findMultiplePremiseRule(conclusion, premises);
		}
	}
	
}
