package InferenceRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import OWLExpressionTemplates.AtomicCls;
import OWLExpressionTemplates.CardExpGen;
import OWLExpressionTemplates.ClsExpStr;
import OWLExpressionTemplates.ExistsOrForAll;
import OWLExpressionTemplates.ExpressionGroup;
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
		RuleException case1 = new RuleException(laconicStr, justificationAxiomStr, justificationAxiomStr, true, true);
	
		
		
		// Case 2
		justificationAxiomStr = new SubClassStr("C", ExistsOrForAll.createIndividualSomeValFrom("Ro", "i"));
		laconicStr = new SubClassStr("C", ExistsOrForAll.createObjSomeValFrom("Ro", "T"));		
		RuleException case2 = new RuleException(laconicStr, justificationAxiomStr, justificationAxiomStr, true, true);
		
		
		
		// Case 3
		
		// 3.1
		justificationAxiomStr = new SubClassStr("C", CardExpGen.createObjMinCard("n", "Ro", "D"));
		laconicStr = new SubClassStr("C", CardExpGen.createObjMinCard("n", "Ro", "T"));		
		RuleException case3_1 = new RuleException(laconicStr, justificationAxiomStr, justificationAxiomStr, false, false);
		
		// 3.2
		justificationAxiomStr = new SubClassStr("C", CardExpGen.createObjExactCard("n1", "Ro", "D"));
		laconicStr = new SubClassStr("C", CardExpGen.createObjMinCard("n2", "Ro", "T"));		
		OWLAxiomStr correctedConclusion = new SubClassStr("C", CardExpGen.createObjMinCard("n2", "Ro", "D"));
		RuleException case3_2 = new RuleException(laconicStr, justificationAxiomStr, correctedConclusion, true, true);



		
		// Case 4
		
		// 4.1
		justificationAxiomStr = new SubClassStr("C", CardExpGen.createObjMaxCard("n", "Ro", "D"));
		laconicStr = new SubClassStr("C", CardExpGen.createObjMaxCard("n", "Ro", "T"));		
		RuleException case4_1 = new RuleException(laconicStr, justificationAxiomStr, justificationAxiomStr, false, false);
				
		// 4.2
		justificationAxiomStr = new SubClassStr("C", CardExpGen.createObjExactCard("n", "Ro", "D"));
		laconicStr = new SubClassStr("C", CardExpGen.createObjMinCard("n", "Ro", "T"));		
		correctedConclusion = new SubClassStr("C", CardExpGen.createObjMaxCard("n", "Ro", "D"));
		RuleException case4_2 = new RuleException(laconicStr, justificationAxiomStr, correctedConclusion, true, true);

		
			
		// Case 5
		
		
		
		
		// Case 6
		laconicStr = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, new TemplateObjectProperty("Ro"), new TemplateObjectProperty("Qo"));
		
		justificationAxiomStr = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, 
				new TemplateObjectProperty("Ro"),
				new TemplateObjectProperty("So"));
		
		
		RuleException case6 = new RuleException(laconicStr, justificationAxiomStr, justificationAxiomStr, false, false) {
			
			@Override
			public ProofTree matchException(OWLAxiom laconicAxiom, OWLAxiom justificationAxiom) {
				
				if (laconicAxiom instanceof OWLSubObjectPropertyOfAxiom && justificationAxiom instanceof OWLInverseObjectPropertiesAxiom) {
					
					OWLSubObjectPropertyOfAxiom laconicSubObj = (OWLSubObjectPropertyOfAxiom) laconicAxiom;
					OWLInverseObjectPropertiesAxiom invObjProp = (OWLInverseObjectPropertiesAxiom) justificationAxiom;
					Set<OWLObjectPropertyExpression> properties = invObjProp.getProperties();
					
					if ((invObjProp.getFirstProperty().equals(laconicSubObj.getSubProperty()) &&
							invObjProp.getSecondProperty().getInverseProperty().equals(laconicSubObj.getSuperProperty())) ||
						(invObjProp.getSecondProperty().equals(laconicSubObj.getSubProperty()) &&
							invObjProp.getFirstProperty().getInverseProperty().equals(laconicSubObj.getSuperProperty()))) {
											
						return new ProofTree(justificationAxiom, null, null);
					}
				}			
				return null;
			}			
		};
		

		
		
		ruleExceptions.add(case1);
		ruleExceptions.add(case2);
		ruleExceptions.add(case3_1);
		ruleExceptions.add(case3_2);
		ruleExceptions.add(case4_1);
		ruleExceptions.add(case4_2);
		ruleExceptions.add(case6);

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
