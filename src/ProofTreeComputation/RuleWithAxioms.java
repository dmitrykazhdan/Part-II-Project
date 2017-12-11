package ProofTreeComputation;

import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

// A list of axioms with a rule that applies to them.
public class RuleWithAxioms {

	private List<OWLAxiom> axioms;
	private String rule;
	
	public RuleWithAxioms (List<OWLAxiom> axioms, String rule) {
		
		this.axioms = axioms;
		this.rule = rule;
	}
}
