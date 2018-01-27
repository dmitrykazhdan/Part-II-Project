package InferenceRules;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import OWLExpressionTemplates.ClsExpStr;
import OWLExpressionTemplates.ExistsOrForAll;
import OWLExpressionTemplates.InterUnion;
import OWLExpressionTemplates.OWLAxiomStr;
import OWLExpressionTemplates.SubClassStr;
import OWLExpressionTemplates.TemplateObjectProperty;
import ProofTreeComputation.ProofTree;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectHasValueImpl;

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

	private static List<RuleException> ruleExceptions = null;
	
	private static void generateExceptions() {
		
		if (ruleExceptions != null) {
			return;
		}
		
		ruleExceptions = new ArrayList<RuleException>();
				
		// Case 1	
		OWLAxiomStr justificationAxiomStr = new SubClassStr("C", ExistsOrForAll.createObjSomeValFrom("Ro", "D"));
		OWLAxiomStr laconicStr = new SubClassStr("C", ExistsOrForAll.createObjSomeValFrom("Ro", "T"));		
		RuleException case1 = new RuleException(laconicStr, laconicStr, justificationAxiomStr);

		
		
		ruleExceptions.add(case1);
	}
	

	
	public static ProofTree matchException(ProofTree tree) {
		
		generateExceptions();
		
		OWLAxiom laconicAxiom = tree.getAxiom();
		OWLAxiom justificationAxiom = tree.getSubTrees().get(0).getAxiom();
		
		for (RuleException ruleException : ruleExceptions) {
			ProofTree generatedTree = ruleException.matchException(laconicAxiom, justificationAxiom);
			
			if (generatedTree != null) {
				return generatedTree;
			}
		}		
		return null;
	}
}
