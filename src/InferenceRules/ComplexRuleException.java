package InferenceRules;

import java.util.Arrays;

import org.semanticweb.owlapi.model.OWLAxiom;

public class ComplexRuleException {

	BaseRuleException baseCase;
	
	public ComplexRuleException(BaseRuleException baseCase) {
		this.baseCase = baseCase;
	}
	
	
//	public boolean matchException(OWLAxiom laconicAxiom, OWLAxiom justificationAxiom) {	
//		RuleString exceptionRule = new RuleString("e1.1", "exception case1", laconicAxiomStr, justificationAxiomStr);		
//		return exceptionRule.matchPremisesAndConclusion(Arrays.asList(new OWLAxiom[] {justificationAxiom}), laconicAxiom);
//	}
}
