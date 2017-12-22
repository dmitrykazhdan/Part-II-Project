package InfRuleAsAnonClass;

import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

public abstract class InferenceRule {

	private String ruleID;
	private String ruleName;
	private int premiseNumber;
	
	public InferenceRule(String ruleID, String ruleName, int premiseNumber) {		
		this.ruleID = ruleID;
		this.ruleName = ruleName;
		this.premiseNumber = premiseNumber;
	}
	
	public abstract boolean matchPremises(List<OWLAxiom> premises);
	public abstract boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion);
	public abstract OWLAxiom generateConclusion(List<OWLAxiom> premises);
	
}
