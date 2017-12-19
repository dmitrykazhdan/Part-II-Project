package InferenceRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.HasProperty;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLQuantifiedDataRestriction;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

public class RuleString {

	private OWLAxiomStr conclusion;
	private List<OWLAxiomStr> premisesStr;
	private int premiseNumber;

	private Map<String, OWLObject> usedSymbols;


	public RuleString(List<OWLAxiomStr> premisesStr, OWLAxiomStr conclusion, int premiseNumber) {
		this.premisesStr = premisesStr;
		this.premiseNumber = premiseNumber;
		this.conclusion = conclusion;
	}



	public boolean matchPremises(List<OWLAxiom> premises) {

		usedSymbols = new HashMap<String, OWLObject>();

		for (int i = 0; i < premises.size(); i++) {
			if (!match(premises.get(i), premisesStr.get(i))) {
				return false;
			}
		}

		return true;
	}


	private boolean match(OWLAxiom axiom, OWLAxiomStr pattern) {

		/*
		 Skipped:

		 Needed for rules:		 
		 17) Equivalent classes
		 18) Disjoint classes
		 28) Different individuals


		 Not needed:
		 2) Equivalent object properties
		 3) Disjoint object properties
		 7) Reflexive object properties
		 8) Irreflexive object property
		 10) Asymmetric object property
		 12) Sub-data property of
		 13) Equivalent data properties
		 14) Disjoint data properties

		 19) Disjoint union of classes

		 24) C(a) individual is an instance of.
		 25) Ro(a, b) individual is connected by Ro to b.
		 26) ~Ro(a, b)
		 27) Sam(a, b)
		 29) Individual a is connected by Rd to a literal l of type Dt.
		 30) Individual a cannot be connected by Rd to a Dt value of l.

		 */

		if (axiom.isOfType(pattern.getConstructor())) {

			// RBox axioms:
			if (axiom.isOfType(AxiomType.RBoxAxiomTypes)) {

				if (axiom.isOfType(AxiomType.SUB_OBJECT_PROPERTY)) {

					OWLSubObjectPropertyOfAxiom subObjPropAxiom = (OWLSubObjectPropertyOfAxiom) axiom;
					return match(subObjPropAxiom.getSubProperty(), (EntityStr) pattern.getExpressions().get(0))
							&& match(subObjPropAxiom.getSuperProperty(), (EntityStr) pattern.getExpressions().get(1));

				} else if (axiom.isOfType(AxiomType.INVERSE_OBJECT_PROPERTIES)) {

					OWLInverseObjectPropertiesAxiom invObjPropAxiom = (OWLInverseObjectPropertiesAxiom) axiom;
					return match(invObjPropAxiom.getFirstProperty(), (EntityStr) pattern.getExpressions().get(0))
							&& match(invObjPropAxiom.getSecondProperty(), (EntityStr) pattern.getExpressions().get(1));

							
				} else if (axiom.isOfType(AxiomType.FUNCTIONAL_OBJECT_PROPERTY) ||
							axiom.isOfType(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY) ||
							axiom.isOfType(AxiomType.SYMMETRIC_OBJECT_PROPERTY) ||
							axiom.isOfType(AxiomType.TRANSITIVE_OBJECT_PROPERTY) ||
							axiom.isOfType(AxiomType.FUNCTIONAL_DATA_PROPERTY)) {
										
					HasProperty<OWLProperty> axiomWithProperty = (HasProperty<OWLProperty>) axiom;
					return match(axiomWithProperty.getProperty(), (EntityStr) pattern.getExpressions().get(0));
				} 
			}


			// TBox Axioms
			if (axiom.isOfType(AxiomType.TBoxAxiomTypes)) {

				if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {

					OWLSubClassOfAxiom subClsAxiom = (OWLSubClassOfAxiom) axiom;
					return match(subClsAxiom.getSubClass(), (ClsExpStr) pattern.getExpressions().get(0))
							&& match(subClsAxiom.getSuperClass(), (ClsExpStr) pattern.getExpressions().get(1));

				} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_RANGE) ) {

					OWLObjectPropertyRangeAxiom objPropRngAxiom = (OWLObjectPropertyRangeAxiom) axiom;
					return match(objPropRngAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0)) &&
							match(objPropRngAxiom.getRange(), (EntityStr) pattern.getExpressions().get(1));

				} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN)) {

					OWLObjectPropertyDomainAxiom objPropDomAxiom = (OWLObjectPropertyDomainAxiom) axiom;
					return match(objPropDomAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0)) &&
							match(objPropDomAxiom.getDomain(), (EntityStr) pattern.getExpressions().get(1));

				} else if (axiom.isOfType(AxiomType.DATA_PROPERTY_DOMAIN)) {

					OWLDataPropertyDomainAxiom dataPropDomAxiom = (OWLDataPropertyDomainAxiom) axiom;
					return match(dataPropDomAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0)) &&
							match(dataPropDomAxiom.getDomain(), (EntityStr) pattern.getExpressions().get(1));
				
				} else if (axiom.isOfType(AxiomType.DATA_PROPERTY_RANGE)) {

					OWLDataPropertyRangeAxiom dataPropRngAxiom = (OWLDataPropertyRangeAxiom) axiom;
					return match(dataPropRngAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0)) &&
							match(dataPropRngAxiom.getRange(), (EntityStr) pattern.getExpressions().get(1));
				} 
			}
		}
		return false;
	}





	private boolean match(OWLClassExpression classExp, ClsExpStr pattern) {
		/*
		 Skipped:
		 		 
		 Not needed for rules:
		 
		 4.  One(a, b, ...)		 
		 8.  Exists Ro.Self		 
		 18. Inv(Ro)
	 		 
		 */
		if (pattern.isAtomic) {
			return addToMap((OWLObject) classExp, pattern.getAtomic());
		}

		ClassExpressionType classExpType = classExp.getClassExpressionType();
		
		if (classExpType.equals(pattern.getConstructor())) {

			if (classExpType.equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
				// Need some exception handling.
			} else if (classExpType.equals(ClassExpressionType.OBJECT_UNION_OF)) {
				// Need some exception handling.
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)) {
				
				OWLObjectComplementOf compObj = (OWLObjectComplementOf) classExp;
				return match(compObj.getOperand(), (ClsExpStr) pattern.getChildren().get(0));
							
			} else if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) ||
					   classExpType.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {
					
				OWLQuantifiedObjectRestriction objSomeValFrom = (OWLQuantifiedObjectRestriction) classExp;
				return match(objSomeValFrom.getProperty(), (EntityStr) pattern.getChildren().get(0))
						&& match(objSomeValFrom.getFiller(), (ClsExpStr)  pattern.getChildren().get(1));
							
			} else if (classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)  ||
						classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY) ||
						classExpType.equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {
							
				OWLObjectCardinalityRestriction objCardRest = (OWLObjectCardinalityRestriction) classExp;				
				int n = objCardRest.getCardinality();
				
				return match(objCardRest.getProperty(), (EntityStr) pattern.getChildren().get(0))
						&& match(objCardRest.getFiller(), (ClsExpStr)  pattern.getChildren().get(1));
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_HAS_VALUE)) {
				
				OWLObjectHasValue objHasVal = (OWLObjectHasValue) classExp;
				
				
			} else if (classExpType.equals(ClassExpressionType.DATA_SOME_VALUES_FROM) ||
					   classExpType.equals(ClassExpressionType.DATA_ALL_VALUES_FROM)) {
				
				OWLQuantifiedDataRestriction quantDataRest = (OWLQuantifiedDataRestriction) classExp;
				return match(quantDataRest.getProperty(), (EntityStr) pattern.getChildren().get(0)) &&
						match(quantDataRest.getFiller(), (EntityStr) pattern.getChildren().get(1));
				
			} else if (classExpType.equals(ClassExpressionType.DATA_HAS_VALUE)) {
				// STUB
				
			} else if (classExpType.equals(ClassExpressionType.DATA_MIN_CARDINALITY) ||
					   classExpType.equals(ClassExpressionType.DATA_MAX_CARDINALITY) ||
					   classExpType.equals(ClassExpressionType.DATA_EXACT_CARDINALITY)) {
				
				OWLDataCardinalityRestriction dataCardRest = (OWLDataCardinalityRestriction) classExp;
				int n = dataCardRest.getCardinality();
				
				return match(dataCardRest.getProperty(), (EntityStr) pattern.getChildren().get(0))
						&& match(dataCardRest.getFiller(), (EntityStr)  pattern.getChildren().get(1));
			} 
		}
		return false;
	}



	private boolean match(OWLObject entity, EntityStr pattern) {
		return addToMap(entity, pattern.getAtomic());
	}


	private boolean addToMap(OWLObject owlObj, String key) {

		if (!usedSymbols.keySet().contains(key)) {
			usedSymbols.put(key, owlObj);
			return true;			
		} else {
			return usedSymbols.get(key).equals(owlObj);			
		}		
	}




	public OWLAxiom generate(List<OWLAxiom> premises) {

		/*
		 Types of conclusion axioms:		 
		 X <= Y
		 Ro <= So
		 Tra(So)
		 Dom(Ro, Y)
		 Rng(Ro, Y)
		 Dis(U, V)
 
		 */
		
		
		if (matchPremises(premises)) {

			OWLAxiom conclusionAxiom;

			if (conclusion.getConstructor().equals(AxiomType.SUBCLASS_OF)) {
				OWLClassExpression subCls = (OWLClassExpression) generate((ClsExpStr) conclusion.getExpressions().get(0));
				OWLClassExpression superCls = (OWLClassExpression) generate((ClsExpStr) conclusion.getExpressions().get(1));
				conclusionAxiom = new OWLSubClassOfAxiomImpl(subCls, superCls, new ArrayList<OWLAnnotation>());
				return conclusionAxiom;
			}
		}		
		return null;	
	}

	
	/*
	 Types of conclusion expressions:
	 
	 
	 */

	private OWLObject generate(ClsExpStr classExpression) {
		if (classExpression.isAtomic) {
			return usedSymbols.get(classExpression.getAtomic());
		}

		return null;
	}


}
