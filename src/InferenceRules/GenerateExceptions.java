package InferenceRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
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

// RuleException handles the case when C <= A is unpacked from C <= B, and returns the correct C <= D.
// If the "equivalent case" is included, the the justification axiom can also be of the form C <--> B
// If the "intersection case" is included, the just. axiom can also be of the form C <--> G ^ B.

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
		RuleException case1 = new RuleException("1", laconicStr, justificationAxiomStr, justificationAxiomStr, true, true);
			
		// Case 2
		justificationAxiomStr = new SubClassStr("C", ExistsOrForAll.createIndividualSomeValFrom("Ro", "i"));
		laconicStr = new SubClassStr("C", ExistsOrForAll.createObjSomeValFrom("Ro", "T"));		
		RuleException case2 = new RuleException("2", laconicStr, justificationAxiomStr, justificationAxiomStr, true, true);
					
		// Case 3	
		// 3.1
		justificationAxiomStr = new SubClassStr("C", CardExpGen.createObjMinCard("n", "Ro", "D"));
		laconicStr = new SubClassStr("C", CardExpGen.createObjMinCard("n", "Ro", "T"));		
		RuleException case3_1 = new RuleException("3.1", laconicStr, justificationAxiomStr, justificationAxiomStr, false, false);
		
		// 3.2
		justificationAxiomStr = new SubClassStr("C", CardExpGen.createObjExactCard("n1", "Ro", "D"));
		laconicStr = new SubClassStr("C", CardExpGen.createObjMinCard("n2", "Ro", "T"));		
		OWLAxiomStr correctedConclusion = new SubClassStr("C", CardExpGen.createObjMinCard("n2", "Ro", "D"));
		RuleException case3_2 = new RuleException("3.2", laconicStr, justificationAxiomStr, correctedConclusion, true, true);

		
		// Case 4
		
		// 4.1
		justificationAxiomStr = new SubClassStr("C", CardExpGen.createObjMaxCard("n", "Ro", "D"));
		laconicStr = new SubClassStr("C", CardExpGen.createObjMaxCard("n", "Ro", "T"));		
		RuleException case4_1 = new RuleException("4.1", laconicStr, justificationAxiomStr, justificationAxiomStr, false, false);
				
		// 4.2
		justificationAxiomStr = new SubClassStr("C", CardExpGen.createObjExactCard("n", "Ro", "D"));
		laconicStr = new SubClassStr("C", CardExpGen.createObjMaxCard("n", "Ro", "T"));		
		correctedConclusion = new SubClassStr("C", CardExpGen.createObjMaxCard("n", "Ro", "D"));
		RuleException case4_2 = new RuleException("4.2", laconicStr, justificationAxiomStr, correctedConclusion, true, true);

				
		// Case 5
		RuleException case5 = new RuleException("5", null, null, null, false, false) {
			
			@Override
			public ProofTree matchException(OWLAxiom laconicAxiom, OWLAxiom justificationAxiom) {
				
				if (laconicAxiom instanceof OWLSubClassOfAxiom && justificationAxiom instanceof OWLSubClassOfAxiom) {
					
					OWLSubClassOfAxiom laconicSubClsAxiom = (OWLSubClassOfAxiom) laconicAxiom;
					OWLSubClassOfAxiom justSubClsAxiom = (OWLSubClassOfAxiom) justificationAxiom;
					
					if (!laconicSubClsAxiom.getSubClass().equals(justSubClsAxiom.getSubClass()) ||
						!(laconicSubClsAxiom.getSuperClass() instanceof OWLDataHasValue) ||
						!(justSubClsAxiom.getSuperClass() instanceof OWLDataHasValue)) {
						
						return null;
					}
					
					OWLDataHasValue laconicDataVal = (OWLDataHasValue) laconicSubClsAxiom.getSuperClass();
					OWLDataHasValue justDataVal = (OWLDataHasValue) justSubClsAxiom.getSuperClass();
					
					if (laconicDataVal.getProperty().equals(justDataVal.getProperty()) &&
						laconicDataVal.getFiller().isTopEntity()) {
											
						return new ProofTree(justificationAxiom, null, null);
					}					
				}
				return null;
			}
		};
		
		
		
		// Case 6
		RuleException case6 = new RuleException("6", null, null, null, false, false) {
			
			@Override
			public ProofTree matchException(OWLAxiom laconicAxiom, OWLAxiom justificationAxiom) {
				
				if (laconicAxiom instanceof OWLSubObjectPropertyOfAxiom && justificationAxiom instanceof OWLInverseObjectPropertiesAxiom) {
					
					OWLSubObjectPropertyOfAxiom laconicSubObj = (OWLSubObjectPropertyOfAxiom) laconicAxiom;
					OWLInverseObjectPropertiesAxiom invObjProp = (OWLInverseObjectPropertiesAxiom) justificationAxiom;
					
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
		ruleExceptions.add(case5);
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
