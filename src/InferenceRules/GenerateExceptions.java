package InferenceRules;

import java.util.List;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import OWLExpressionTemplates.OWLAxiomStr;
import OWLExpressionTemplates.TemplateObjectProperty;
import ProofTreeComputation.ProofTree;

public class GenerateExceptions {

	private static List<RuleString> exceptions = null;
	
	private static void generateExceptions() {
		
		if (exceptions == null) {
			return;
		}
	

		
		OWLAxiomStr original = new OWLAxiomStr(AxiomType.INVERSE_OBJECT_PROPERTIES, 
				new TemplateObjectProperty("Ro"),
				new TemplateObjectProperty("So"));
				
				
		OWLAxiomStr laconic = new OWLAxiomStr(AxiomType.SUB_OBJECT_PROPERTY, 
											new TemplateObjectProperty("Ro"),
											new TemplateObjectProperty("Qo"));
		
		
		RuleString case6 = new RuleString("6", "Case 6", laconic, original) {
			@Override
			public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {	
				
				boolean matched = super.matchPremisesAndConclusion(premises, conclusion);
				
				if (matched) {
					OWLSubObjectPropertyOfAxiom subObjPropOf = (OWLSubObjectPropertyOfAxiom) premises.get(0);
					OWLObjectPropertyExpression subProperty = subObjPropOf.getSubProperty();
					OWLObjectPropertyExpression invSuperProperty = subObjPropOf.getSuperProperty().getInverseProperty();
					OWLInverseObjectPropertiesAxiom inv = (OWLInverseObjectPropertiesAxiom) conclusion;
					return inv.getProperties().contains(subProperty) && inv.getProperties().contains(invSuperProperty);
					
				}
				return false;
			}
		};
	
		exceptions.add(case6);
	}
	
	public static boolean matchException(OWLAxiom laconic, OWLAxiom original) {
		return false;
	}
	
	
	public static boolean isException(ProofTree tree){
		return matchException(tree.getAxiom(), tree.getSubTrees().get(0).getAxiom());
	}
	
	public static ProofTree applyExceptionRule(ProofTree tree) {
		return null;
	}
}
