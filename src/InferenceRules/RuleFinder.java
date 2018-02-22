package InferenceRules;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

public class RuleFinder {

	// Given premises only, find a rule that fits the pattern.
	public static List<RuleString> findRuleAppWithoutConclusion(List<OWLAxiom> premises) {

		List<RuleString> applicableRules = new ArrayList<RuleString>();
		List<RuleString> allRules = GenerateRules.getRules().get(premises.size());
		List<List<OWLAxiom>> premisePermutations = getPremisePermutations(premises);

		if (allRules == null) { return null;} 

		for (List<OWLAxiom> premisePermutation : premisePermutations) {
			for (RuleString rule : allRules) {
				if (!applicableRules.contains(rule) && rule.matchPremises(premisePermutation)) {
					applicableRules.add(rule);
				}
			}
		}
		return applicableRules;
	}
	
	
	
	// Given premises and a conclusion, find a rule that fits the pattern.
	public static List<RuleString> findRuleAppGivenConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {

		List<RuleString> applicableRules = new ArrayList<RuleString>();
		List<RuleString> allRules = GenerateRules.getRules().get(premises.size());
		List<List<OWLAxiom>> premisePermutations = getPremisePermutations(premises);

		if (allRules == null) {
			return null;
		}

		for (List<OWLAxiom> premisePermutation : premisePermutations) {
			for (RuleString rule : allRules) {
				if (!applicableRules.contains(rule) && rule.matchPremisesAndConclusion(premisePermutation, conclusion)) {
					applicableRules.add(rule);
				}
			}
		}
		return applicableRules;
	}
	
	
	// Given an incomplete rule application (premises with a rule), attempt
	// to generate a conclusion.
	public static List<InstanceOfRule> generateInferences(InstanceOfRule incompleteRuleApplication) {
		
		List<OWLAxiom> premises = incompleteRuleApplication.getPremises();
		List<List<OWLAxiom>> premisePermutations = getPremisePermutations(premises);
		RuleString rule = incompleteRuleApplication.getRule();
		List<InstanceOfRule> appliedRules = new ArrayList<InstanceOfRule>();

		if (rule == null) { return null; }
		
		for (List<OWLAxiom> premisePermutation : premisePermutations) {

			List<OWLAxiom> conclusions = rule.generateConclusions(premisePermutation);	

			if (conclusions != null) {			
				for (OWLAxiom conclusion : conclusions) {
					appliedRules.add(new InstanceOfRule(premisePermutation, conclusion, rule));
				}
			}
		}
		return appliedRules;	
	}	
	
		
	private static List<List<OWLAxiom>> getPremisePermutations(List<OWLAxiom> premises) {
		PermutationGenerator<OWLAxiom> permGen = new PermutationGenerator<OWLAxiom>();
		return permGen.generatePermutations(premises);
	}
}
