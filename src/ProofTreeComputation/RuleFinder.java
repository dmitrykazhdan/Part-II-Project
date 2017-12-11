package ProofTreeComputation;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

public class RuleFinder {

	public static InferenceRule findRuleApplication(List<OWLAxiom> premises, OWLAxiom conclusion) {
		
		List<InferenceRule> rules = RuleGenerator.getRules().get(premises.size());
		
		for (InferenceRule rule : rules) {
			if (rule.ruleApplicable(premises, conclusion)) {
				return rule;
			}
		}
		
		return null;
	}
	
	
	public static RuleApplication generateInference(List<OWLAxiom> premises) {
		
		List<InferenceRule> rules = RuleGenerator.getRules().get(premises.size());
		
		for (InferenceRule rule : rules) {
			
			OWLAxiom conclusion = rule.generateConclusion(premises);
			
			if (conclusion != null) {
				
				RuleApplication appliedRule = new RuleApplication(premises, conclusion, rule);
				
				return appliedRule;
			}
		}
		
		return null;
	}
	
}
