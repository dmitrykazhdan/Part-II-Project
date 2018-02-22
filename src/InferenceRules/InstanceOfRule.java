package InferenceRules;

import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLAxiom;

// A list of axioms with a rule that applies to them.
public class InstanceOfRule {

	private List<OWLAxiom> premises;
	private OWLAxiom conclusion;
	private RuleString rule;
	
	public InstanceOfRule (List<OWLAxiom> premises, OWLAxiom conclusion, RuleString rule) {
		
		this.premises = premises;
		this.conclusion = conclusion;
		this.rule = rule;
	}
	
	public InstanceOfRule(InstanceOfRule instanceOfRule) {
		
		this.premises = new ArrayList<OWLAxiom>(instanceOfRule.getPremises());
		this.conclusion = instanceOfRule.getConclusion();
		this.rule = instanceOfRule.getRule();
	}

	public List<OWLAxiom> getPremises() {
		return premises;
	}
	
	public RuleString getRule() {
		return rule;
	}
	
	public OWLAxiom getConclusion() {
		return conclusion;
	}
}

