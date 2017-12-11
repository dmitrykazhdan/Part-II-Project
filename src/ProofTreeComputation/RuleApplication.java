package ProofTreeComputation;

import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

// A list of axioms with a rule that applies to them.
public class RuleApplication {

	private List<OWLAxiom> premises;
	private OWLAxiom conclusion;
	private InferenceRule rule;
	
	public RuleApplication (List<OWLAxiom> premises, OWLAxiom conclusion, InferenceRule rule) {
		
		this.premises = premises;
		this.conclusion = conclusion;
		this.rule = rule;
	}
	
	public List<OWLAxiom> getPremises() {
		return premises;
	}
	
	public InferenceRule getRule() {
		return rule;
	}
	
	public OWLAxiom getConclusion() {
		return conclusion;
	}
}
