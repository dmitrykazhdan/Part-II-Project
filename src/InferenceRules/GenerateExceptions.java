package InferenceRules;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import OWLExpressionTemplates.ExistsOrForAll;
import OWLExpressionTemplates.OWLAxiomStr;
import OWLExpressionTemplates.SubClassStr;
import OWLExpressionTemplates.TemplateObjectProperty;
import ProofTreeComputation.ProofTree;

/*
Base Case:
 Strategy:
 1) Match(premise, incorrectConclusion) to make sure this is the exception case.
 2) Either leave it as-is, or attempt to recursively construct a proof tree from
    the premise to the correctedConclusion by calling generateProofTree(premise, correctedConclusion)

 */


/*
Very basic case (given two basic axioms where you need to return the original axiom)
 Given (laconicAxiom, justificationAxiom).
 
 1) Match(laconicAxiom,justificationAxiom) to Exception Pattern
 2) Return justificationAxiom
 
 
Less basic case (given two basic axioms where you need to return a new proof tree)
 Given (laconicAxiom, justificationAxiom)
 1) Match(laconicAxiom,justificationAxiom) to Exception Pattern 
 2) Use the list of ruleStrings to generate intermediate conclusions one-by-one.
 3) When you reach the last rule, use that rule and the last generated conclusion to match
    against the corrected one.
 4) Return entire proof tree.


Difficult case (given two intersection axioms where you need to return a new proof tree)
 Given (laconicAxiom, justificationAxiom)
 1) Match using:
 	check that every axiom in laconic is either an original axiom in justification, or a 
 	transformed axiom in the justification
 2) Generate the corrected conclusion based on the incorrect one.
 3) Use intermediate rules to construct proof tree.
 
 
 
 */
public class GenerateExceptions {

	private static List<BaseRuleException> ruleExceptions = null;
	
	private static void generateExceptions() {
		
		if (ruleExceptions != null) {
			return;
		}
		
		ruleExceptions = new ArrayList<BaseRuleException>();
		
		// Case 1
		OWLAxiomStr justificationAxiomStr = new SubClassStr("C", ExistsOrForAll.createObjSomeValFrom("Ro", "D"));
		OWLAxiomStr laconicAxiom = new SubClassStr("C", ExistsOrForAll.createObjSomeValFrom("Ro", "T"));		
		BaseRuleException case1 = new BaseRuleException(laconicAxiom, justificationAxiomStr);

		
		
		ruleExceptions.add(case1);
	}
	

	
	public static BaseRuleException matchException(OWLAxiom laconicAxiom, OWLAxiom justificationAxiom) {
		
		generateExceptions();
		
		for (BaseRuleException ruleException : ruleExceptions) {
			if (ruleException.matchException(laconicAxiom, justificationAxiom)) {
				return ruleException;
			}
		}		
		return null;
	}
	
	
	public static boolean isException(ProofTree tree){
		return (matchException(tree.getAxiom(), tree.getSubTrees().get(0).getAxiom()) == null);
	}
	
	
	public static ProofTree applyExceptionRule(ProofTree tree) {
		
		OWLAxiom laconicAxiom = tree.getAxiom();
		OWLAxiom justificationAxiom = tree.getSubTrees().get(0).getAxiom();
		BaseRuleException matchedException = matchException(laconicAxiom, justificationAxiom);
		
		if (matchedException != null) {
			ProofTree correctedTree = matchedException.getCorrectedTree(laconicAxiom, justificationAxiom);
			return correctedTree;
		}
		return null;
	}
}
