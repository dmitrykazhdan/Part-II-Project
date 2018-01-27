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
				if (rule.matchPremises(premisePermutation) && !applicableRules.contains(rule)) {
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
				if (rule.matchPremisesAndConclusion(premisePermutation, conclusion) && !applicableRules.contains(rule)) {
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

		for (List<OWLAxiom> premisePermutation : premisePermutations) {

			List<OWLAxiom> conclusions = rule.generateConclusions(premisePermutation);	

			if (conclusions != null) {

				List<InstanceOfRule> appliedRules = new ArrayList<InstanceOfRule>();
				
				for (OWLAxiom conclusion : conclusions) {
					appliedRules.add(new InstanceOfRule(premisePermutation, conclusion, rule));
					return appliedRules;
				}
			}
		}
		return null;	
	}	
	
		
	private static List<List<OWLAxiom>> getPremisePermutations(List<OWLAxiom> premises) {
		
		List<List<OWLAxiom>> allPermutations = new ArrayList<List<OWLAxiom>>();
		
		if (premises.size() == 0) {
			List<OWLAxiom> emptyPermutation = new ArrayList<OWLAxiom>();
			allPermutations.add(emptyPermutation);
			return allPermutations;
		}
		
		for (OWLAxiom premise : premises) {
			List<OWLAxiom> premiseCopy = new ArrayList<OWLAxiom>(premises);
			premiseCopy.remove(premise);			
			List<List<OWLAxiom>> subPremises = getPremisePermutations(premiseCopy);
			
			for (List<OWLAxiom> subPermutation : subPremises) {
				subPermutation.add(premise);
				allPermutations.add(subPermutation);
			}		
		}
		
		return allPermutations;
	}
}
