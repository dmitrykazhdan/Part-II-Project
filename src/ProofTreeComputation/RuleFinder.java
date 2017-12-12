package ProofTreeComputation;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

public class RuleFinder {

	// Given premises only, find a rule that fits the pattern.
	public static InferenceRule findRuleAppWithoutConclusion(List<OWLAxiom> premises) {
		
		List<InferenceRule> rules = RuleGenerator.getRules().get(premises.size());
		
		if (rules == null) { return null;} 
		
		for (InferenceRule rule : rules) {
			if (rule.matchPremises(premises)) {
				return rule;
			}
		}
		
		return null;
	}
	
	
	
	// Given premises and a conclusion, find a rule that fits the pattern.
	public static InferenceRule findRuleAppGivenConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {
		
		List<InferenceRule> rules = RuleGenerator.getRules().get(premises.size());
		
		if (rules == null) {
			return null;
		}
		
		for (InferenceRule rule : rules) {
			if (rule.matchPremisesAndConclusion(premises, conclusion)) {
				return rule;
			}
		}
		
		return null;
	}
	
	
	// Given an incomplete rule application (premises with a rule), attempt
	// to generate a conclusion.
	public static RuleApplication generateInference(RuleApplication incompleteRuleApplication) {
		
		List<OWLAxiom> premises = incompleteRuleApplication.getPremises();
		InferenceRule rule = incompleteRuleApplication.getRule();
		OWLAxiom conclusion = rule.generateConclusion(premises);	

		if (conclusion != null) {
			RuleApplication appliedRule = new RuleApplication(premises, conclusion, rule);
			return appliedRule;
		} else {
			return null;
		}	
	}	
}
