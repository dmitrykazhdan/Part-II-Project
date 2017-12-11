package ProofTreeComputation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class GenerateRules {

	private static List<InferenceRule> rules = null;

	public static List<InferenceRule> getRules() {

		if (rules == null) {
			generateRules();
		} 

		return rules;
	}


	private static void generateRules() {



		// RULE 1
		InferenceRule rule1 = 
				new InferenceRule("1", "EquCls", 1) {
			@Override	
			public boolean ruleApplicable(List<OWLAxiom> premises, OWLAxiom conclusion) {

				OWLAxiom premise = premises.get(0);

				if ((premise.isOfType(AxiomType.EQUIVALENT_CLASSES)) && (conclusion.isOfType(AxiomType.SUBCLASS_OF))			
						&& premise.getClassesInSignature().containsAll(conclusion.getClassesInSignature())) {				
					return  true;								
				} else {
					return false;
				}
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				return null;
			}					
		};



		// RULE 6
		InferenceRule rule6 = 
				new InferenceRule("6", "ObjExt", 1) {
			@Override	
			public boolean ruleApplicable(List<OWLAxiom> premises, OWLAxiom conclusion) {

				OWLAxiom premise = premises.get(0);

				if ((premise.isOfType(AxiomType.SUBCLASS_OF)) && (conclusion.isOfType(AxiomType.SUBCLASS_OF))) {			

					OWLClassExpression axiomSuperCls = ((OWLSubClassOfAxiom) premise).getSuperClass();
					OWLClassExpression axiomSubCls = ((OWLSubClassOfAxiom) premise).getSubClass();
					OWLClassExpression entSuperCls = ((OWLSubClassOfAxiom) conclusion).getSuperClass();
					OWLClassExpression entSubCls = ((OWLSubClassOfAxiom) conclusion).getSubClass();


					if ((axiomSubCls.equals(entSubCls)) &&			
							(axiomSuperCls.getClassExpressionType().equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) 
							&& (entSuperCls.getClassExpressionType().equals(ClassExpressionType.OBJECT_MIN_CARDINALITY))) {

						int n1 = ((OWLObjectExactCardinality) axiomSuperCls).getCardinality();
						int n2 = ((OWLObjectMinCardinality) entSuperCls).getCardinality();

						if ((n1 >= n2) && (n2 >= 0)) {

							return true;

						}				
					}
				}

				return false;
			}

			@Override
			public OWLAxiom generateConclusion(List<OWLAxiom> premises) {
				return null;
			}					
		};	

		rules.add(rule1);

	}	
}
