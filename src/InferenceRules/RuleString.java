package InferenceRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

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

					return match((OWLEntity) subObjPropAxiom.getSubProperty(), (EntityStr) pattern.getExpressions().get(0))
							&& match((OWLEntity) subObjPropAxiom.getSuperProperty(), (EntityStr) pattern.getExpressions().get(1));


				} else if (axiom.isOfType(AxiomType.INVERSE_OBJECT_PROPERTIES)) {

					OWLInverseObjectPropertiesAxiom invObjPropAxiom = (OWLInverseObjectPropertiesAxiom) axiom;

					return match((OWLEntity) invObjPropAxiom.getFirstProperty(), (EntityStr) pattern.getExpressions().get(0))
							&& match((OWLEntity) invObjPropAxiom.getSecondProperty(), (EntityStr) pattern.getExpressions().get(1));


				} else if (axiom.isOfType(AxiomType.FUNCTIONAL_OBJECT_PROPERTY)) {

					OWLFunctionalObjectPropertyAxiom funObjAxiom = (OWLFunctionalObjectPropertyAxiom) axiom;				

					return match((OWLEntity) funObjAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));


				} else if (axiom.isOfType(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)) {

					OWLInverseFunctionalObjectPropertyAxiom invFuncObjPropAxiom = (OWLInverseFunctionalObjectPropertyAxiom) axiom;

					return match((OWLEntity) invFuncObjPropAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));


				} else if (axiom.isOfType(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {

					OWLInverseFunctionalObjectPropertyAxiom invFuncObjPropAxiom = (OWLInverseFunctionalObjectPropertyAxiom) axiom;

					return match((OWLEntity) invFuncObjPropAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));


				} else if (axiom.isOfType(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {

					OWLInverseFunctionalObjectPropertyAxiom invFuncObjPropAxiom = (OWLInverseFunctionalObjectPropertyAxiom) axiom;

					return match((OWLEntity) invFuncObjPropAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));


				}  else if (axiom.isOfType(AxiomType.FUNCTIONAL_DATA_PROPERTY)) {

					OWLInverseFunctionalObjectPropertyAxiom invFuncObjPropAxiom = (OWLInverseFunctionalObjectPropertyAxiom) axiom;

					return match((OWLEntity) invFuncObjPropAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));

				}
			}


			// TBox Axioms
			if (axiom.isOfType(AxiomType.TBoxAxiomTypes)) {

				if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {

					OWLSubClassOfAxiom subClsAxiom = (OWLSubClassOfAxiom) axiom;

					return match(subClsAxiom.getSubClass(), (ClsExpStr) pattern.getExpressions().get(0))
							&& match(subClsAxiom.getSuperClass(), (ClsExpStr) pattern.getExpressions().get(1));


				} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_RANGE)) {

					OWLInverseFunctionalObjectPropertyAxiom invFuncObjPropAxiom = (OWLInverseFunctionalObjectPropertyAxiom) axiom;

					return match((OWLEntity) invFuncObjPropAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));


				} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN)) {

					OWLInverseFunctionalObjectPropertyAxiom invFuncObjPropAxiom = (OWLInverseFunctionalObjectPropertyAxiom) axiom;

					return match((OWLEntity) invFuncObjPropAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));


				} else if (axiom.isOfType(AxiomType.DATA_PROPERTY_RANGE)) {

					OWLInverseFunctionalObjectPropertyAxiom invFuncObjPropAxiom = (OWLInverseFunctionalObjectPropertyAxiom) axiom;

					return match((OWLEntity) invFuncObjPropAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));


				} else if (axiom.isOfType(AxiomType.DATA_PROPERTY_DOMAIN)) {

					OWLInverseFunctionalObjectPropertyAxiom invFuncObjPropAxiom = (OWLInverseFunctionalObjectPropertyAxiom) axiom;

					return match((OWLEntity) invFuncObjPropAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));
				} 
			}


			// ABox axioms
			if (axiom.isOfType(AxiomType.ABoxAxiomTypes)) {
				if (axiom.isOfType(AxiomType.DIFFERENT_INDIVIDUALS)) {

					OWLInverseFunctionalObjectPropertyAxiom invFuncObjPropAxiom = (OWLInverseFunctionalObjectPropertyAxiom) axiom;

					return match((OWLEntity) invFuncObjPropAxiom.getProperty(), (EntityStr) pattern.getExpressions().get(0));
				}
			}

		}
		return false;
	}





	private boolean match(OWLClassExpression classExp, ClsExpStr pattern) {

		if (pattern.isAtomic) {
			return addToMap((OWLObject) classExp, pattern.getAtomic());
		}

		if (classExp.getClassExpressionType().equals(pattern.getConstructor())) {

			if (classExp.getClassExpressionType().equals(ClassExpressionType.DATA_MIN_CARDINALITY)) {

				OWLObjectMinCardinality objMinCard = (OWLObjectMinCardinality) classExp;
				OWLObjectPropertyExpression property = objMinCard.getProperty();
				OWLClassExpression innerClassExp = objMinCard.getFiller();

				return match((OWLEntity) property, (EntityStr) pattern.getChildren().get(0))
						&& match(innerClassExp, (ClsExpStr) pattern.getChildren().get(1));

			}			
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




	public OWLAxiom generate(List<OWLAxiom> premises) {

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


	private OWLObject generate(ClsExpStr classExpression) {
		if (classExpression.isAtomic) {
			return usedSymbols.get(classExpression.getAtomic());
		}

		return null;
	}


}
