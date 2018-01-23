package InferenceGenerator;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

import InferenceRules.Instantiation;
import OWLExpressionTemplates.OWLAxiomStr;
import RuleRestrictions.RestrictionChecker;
import RuleRestrictions.RuleRestriction;
import RuleRestrictions.RuleRestrictions;

public abstract class RuleMatcherGenerator {

	// The actual axiom expressions.
	protected List<OWLAxiom> expressions;
	
	// The pattern to match against.
	protected List<OWLAxiomStr> expressionStr;
	
	
	public RuleMatcherGenerator(List<OWLAxiom> expressions, List<OWLAxiomStr> expressionStr) {
		this.expressions = expressions;
		this.expressionStr = expressionStr;
	}
	
	
	protected List<Instantiation> checkInstantiationRestrictions(List<Instantiation> instantiations, RuleRestriction[] ruleRestrictions) {
		
		List<Instantiation> prevInstantiations = new ArrayList<Instantiation>(instantiations);
		instantiations = new ArrayList<Instantiation>();

		for (Instantiation instantiation : prevInstantiations) {
			
			RestrictionChecker restrictionChecker = new RestrictionChecker(ruleRestrictions, instantiation);
			
			if (restrictionChecker.checkRestrictionsForInstantiation()) {
				instantiations.add(instantiation);
			}			
		}
		return instantiations;
	}
}
