package InferenceRules;

import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

import OWLExpressionTemplates.OWLAxiomStr;
import ProofTreeComputation.ProofTree;

public class BaseRuleException {

	private OWLAxiomStr laconicAxiomStr;
	private OWLAxiomStr justificationAxiomStr;
	private OWLAxiomStr correctAxiomStr;
	private List<RuleString> intermediateRules;
	
	public BaseRuleException(OWLAxiomStr laconicAxiomStr,  OWLAxiomStr justificationAxiomStr) {
		this.laconicAxiomStr = laconicAxiomStr;
		this.justificationAxiomStr = justificationAxiomStr;
		this.correctAxiomStr = null;
		this.intermediateRules = null;
	}
	
	public BaseRuleException(OWLAxiomStr laconicAxiomStr,  OWLAxiomStr justificationAxiomStr, OWLAxiomStr correctAxiomStr, RuleString... intermediateRules) {
		this.laconicAxiomStr = laconicAxiomStr;
		this.justificationAxiomStr = justificationAxiomStr;
		this.correctAxiomStr = correctAxiomStr;
		this.intermediateRules = Arrays.asList(intermediateRules);
	}
	
	
	public boolean matchException(OWLAxiom laconicAxiom, OWLAxiom justificationAxiom) {
		return false;
	}

	
	public ProofTree getCorrectedTree(OWLAxiom laconicAxiom, OWLAxiom justificationAxiom) {
		
		if (!matchException(laconicAxiom, justificationAxiom)) {
			return null;
		}
		
		// If corrected axiom string is set to null, simply return the original justification axiom.
		if (correctAxiomStr == null && intermediateRules == null) {
			return new ProofTree(justificationAxiom, null, null);
		}
		return null;
	}
	
}
