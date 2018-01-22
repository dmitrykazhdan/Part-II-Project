package InferenceGenerator;

import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

import OWLExpressionTemplates.OWLAxiomStr;
import RuleRestrictions.RuleRestriction;

public abstract class RuleMatcherGenerator {

	// The actual axiom expressions.
	protected List<OWLAxiom> expressions;
	
	// The pattern to match against.
	protected List<OWLAxiomStr> expressionStr;
	
	// Rule restrictions to check.
	protected RuleRestriction[] ruleRestrictions;

	
	public RuleMatcherGenerator(List<OWLAxiom> expressions, List<OWLAxiomStr> expressionStr, RuleRestriction[] ruleRestrictions) {
		this.expressions = expressions;
		this.expressionStr = expressionStr;
		this.ruleRestrictions = ruleRestrictions;
	}
}
