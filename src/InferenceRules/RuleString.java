package InferenceRules;

import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class RuleString {

	private List<OWLAxiomStr> ruleContents;
	private int premiseNumber;
	
	private Map<String, OWLObject> usedSymbols;

	
	public RuleString(List<OWLAxiomStr> ruleContents, int premiseNumber) {
		this.ruleContents = ruleContents;
		this.premiseNumber = premiseNumber;
	}
	
	
	
	public boolean matchPremises(List<OWLAxiom> premises) {
		return false;
	}
	
	
	public boolean match(OWLAxiom axiom, OWLAxiomStr pattern) {
		
		if (axiom.isOfType(pattern.getConstructor())) {
			
			if (axiom.isOfType(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)) {
				
				OWLFunctionalObjectPropertyAxiom funObjAxiom = (OWLFunctionalObjectPropertyAxiom) axiom;				
				
				return match((OWLEntity) funObjAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));
								
			} else if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {
				
				OWLSubClassOfAxiom subClsAxiom = (OWLSubClassOfAxiom) axiom;
				
				return match(subClsAxiom.getSubClass(), (ClsExpStr) pattern.getExpressions().get(0))
						&& match(subClsAxiom.getSuperClass(), (ClsExpStr) pattern.getExpressions().get(1));
			}
			
		}
		return false;
	}
	
	
	
	
	
	private boolean match(OWLClassExpression classExp, ClsExpStr pattern) {
		
		if (pattern.isAtomic) {
			return addToMap((OWLObject) classExp, pattern.getAtomic());
		}
		
		if (classExp.getClassExpressionType().equals(pattern.getConstructor())) {
			
			
		}
		return false;
	}
	
	
	
	private boolean match(OWLEntity entity, EntityStr pattern) {
		return addToMap((OWLObject) entity, pattern.getAtomic());
	}
	
	
	private boolean addToMap(OWLObject owlObj, String key) {
		
		if (!usedSymbols.keySet().contains(key)) {
			usedSymbols.put(key, owlObj);
			return true;			
		} else {
			return usedSymbols.get(key).equals(owlObj);			
		}		
	}
	
}
