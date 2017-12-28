package InferenceRules;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
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

import uk.ac.manchester.cs.owl.owlapi.OWLObjectAllValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectExactCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMaxCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMinCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

public class RuleString {

	private String ruleID;
	private String ruleName;
	private OWLAxiomStr conclusion;
	private List<OWLAxiomStr> premisesStr;
	private int premiseNumber;

	private Map<String, OWLObject> usedSymbols;
	private Map<String, Integer> usedCardinalities;
	private Map<String, String> lessThanMap;



	public RuleString(String ruleID, String ruleName, OWLAxiomStr conclusion, List<OWLAxiomStr> premisesStr) {
		this.ruleID = ruleID;
		this.ruleName = ruleName;
		this.premisesStr = premisesStr;
		this.premiseNumber = premisesStr.size();
		this.conclusion = conclusion;
	}
	
	public RuleString(String ruleID, String ruleName, OWLAxiomStr conclusion, OWLAxiomStr... premises) {
		this.ruleID = ruleID;
		this.ruleName = ruleName;
		this.premisesStr = new ArrayList<OWLAxiomStr>(Arrays.asList(premises));		
		this.premiseNumber = premisesStr.size();
		this.conclusion = conclusion;
	}



	public boolean matchPremises(List<OWLAxiom> premises) {

		usedSymbols = new HashMap<String, OWLObject>();

		for (int i = 0; i < premises.size(); i++) {
			if (!match(premises.get(i), premisesStr.get(i))) {
				return false;
			}
		}

		return checkCardinalityRestrictions();
	}
	
	
	public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {		
		List<OWLAxiom> premisesAndConclusion = new ArrayList<OWLAxiom>(premises);
		premisesAndConclusion.add(conclusion);
		return matchPremises(premisesAndConclusion);	
	}




	private boolean checkCardinalityRestrictions() {
		
		for (String smallerCardinality : lessThanMap.keySet()) {
			
			Integer smallerCardinalityVal = usedCardinalities.get(smallerCardinality);
			Integer largerCardinalityVal = usedCardinalities.get(lessThanMap.get(smallerCardinality));
			
			if (smallerCardinalityVal > largerCardinalityVal) {
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
				
				} else if (axiom.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
					
					OWLEquivalentClassesAxiom eqvClassesAxiom = (OWLEquivalentClassesAxiom) axiom;
					ClsExpStr firstPatternClsExp = (ClsExpStr) pattern.getExpressions().get(0);
					ClsExpStr secondPatternClsExp = (ClsExpStr) pattern.getExpressions().get(1);
					
					for (OWLEquivalentClassesAxiom pairwiseAxiom : eqvClassesAxiom.asPairwiseAxioms()) {
						
						OWLClassExpression firstClsExp = pairwiseAxiom.getClassExpressionsAsList().get(0);
						OWLClassExpression secondClsExp = pairwiseAxiom.getClassExpressionsAsList().get(1);
						
						if ((match(firstClsExp, firstPatternClsExp) && match(secondClsExp, secondPatternClsExp)) ||
							(match(firstClsExp, secondPatternClsExp) && match(secondClsExp, firstPatternClsExp))){
							return true;
						}					
					}
					
					return false;
				}
			}
		}
		
		
		// ABox Axioms
		if (axiom.isOfType(AxiomType.ABoxAxiomTypes)) {
			
			// Note, this assumes that the "Different Individuals" string pattern only has 2 children!
			if (axiom.isOfType(AxiomType.DIFFERENT_INDIVIDUALS)) {
				
				OWLDifferentIndividualsAxiom diffIndividualsAxiom = (OWLDifferentIndividualsAxiom) axiom;				
				boolean matchedFirstArgument = false;
				boolean matchedSecondArgument = false;
				
				for (OWLIndividual i : diffIndividualsAxiom.getIndividualsAsList()) {
					if (match(i, (EntityStr) pattern.getExpressions().get(0))) {
						matchedFirstArgument = true;
					}
					
					if (match(i, (EntityStr) pattern.getExpressions().get(1))) {
						matchedSecondArgument = true;
					}
				}
				
				return matchedFirstArgument && matchedSecondArgument;
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
		if (pattern.getExpressionType() == null) {
			return addToMap((OWLObject) classExp, ((AtomicCls) pattern).getPlaceholder());
		}

		ClassExpressionType classExpType = classExp.getClassExpressionType();
		
		if (classExpType.equals(pattern.getExpressionType())) {

			if (classExpType.equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
				// Need some exception handling.
			} else if (classExpType.equals(ClassExpressionType.OBJECT_UNION_OF)) {
				// Need some exception handling.
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)) {
				
				OWLObjectComplementOf compObj = (OWLObjectComplementOf) classExp;
				return match(compObj.getOperand(), ((InterUnionComp) pattern).getSubExpressions().get(0));
					
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) ||
					   classExpType.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {
					
				OWLQuantifiedObjectRestriction objSomeValFrom = (OWLQuantifiedObjectRestriction) classExp;
				ExistsOrForAll specialisedPattern = (ExistsOrForAll) pattern;
				
				return match(objSomeValFrom.getProperty(), specialisedPattern.getProperty())
						&& match(objSomeValFrom.getFiller(), (ClsExpStr) specialisedPattern.getExpression());
				
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)  ||
						classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY) ||
						classExpType.equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {
							
				OWLObjectCardinalityRestriction objCardRest = (OWLObjectCardinalityRestriction) classExp;				
				CardExpStr specialisedPattern = (CardExpStr) pattern;

				return matchCardinality(objCardRest.getCardinality(), specialisedPattern) 
						&& match(objCardRest.getProperty(), specialisedPattern.getProperty())
						&& match(objCardRest.getFiller(), (ClsExpStr) specialisedPattern.getExpression());
	
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_HAS_VALUE)) {
				
				OWLObjectHasValue objHasVal = (OWLObjectHasValue) classExp;
				
				
			} else if (classExpType.equals(ClassExpressionType.DATA_SOME_VALUES_FROM) ||
					   classExpType.equals(ClassExpressionType.DATA_ALL_VALUES_FROM)) {
				
				OWLQuantifiedDataRestriction quantDataRest = (OWLQuantifiedDataRestriction) classExp;
				ExistsOrForAll specialisedPattern = (ExistsOrForAll) pattern;
				
				return match(quantDataRest.getProperty(), specialisedPattern.getProperty())
						&& match(quantDataRest.getFiller(), (EntityStr) specialisedPattern.getExpression());
				
			} else if (classExpType.equals(ClassExpressionType.DATA_HAS_VALUE)) {
				// STUB
				
			} else if (classExpType.equals(ClassExpressionType.DATA_MIN_CARDINALITY) ||
					   classExpType.equals(ClassExpressionType.DATA_MAX_CARDINALITY) ||
					   classExpType.equals(ClassExpressionType.DATA_EXACT_CARDINALITY)) {
				
				OWLObjectCardinalityRestriction dataCardRest = (OWLObjectCardinalityRestriction) classExp;				
				CardExpStr specialisedPattern = (CardExpStr) pattern;

				return matchCardinality(dataCardRest.getCardinality(), specialisedPattern) 
						&& match(dataCardRest.getProperty(), specialisedPattern.getProperty())
						&& match(dataCardRest.getFiller(), (EntityStr) specialisedPattern.getExpression());
			} 
		}
		return false;
	}



	private boolean match(OWLObject entity, EntityStr pattern) {
		return addToMap(entity, pattern.getAtomic());
	}
	

	private boolean matchCardinality(Integer givenCardinality, CardExpStr specialisedPattern) {
		
		usedCardinalities.put(specialisedPattern.getCardinality(), givenCardinality);
	
		if (specialisedPattern.isRelativeBound()) {						
			lessThanMap.put(specialisedPattern.getLowerBound(), specialisedPattern.getCardinality());
			return true;
			
		} else {
			return givenCardinality >= Integer.parseInt(specialisedPattern.getLowerBound());
		}
	}


	private boolean addToMap(OWLObject owlObj, String key) {

		if (!usedSymbols.keySet().contains(key)) {
			usedSymbols.put(key, owlObj);
			return true;			
		} else {
			return usedSymbols.get(key).equals(owlObj);			
		}		
	}




	public OWLAxiom generateConclusion(List<OWLAxiom> premises) {

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
	 	 
	 Need to be implemented:
	 
	 1)  Conjunction.
	 2)  Exists Ro
	 3)  Union
	 4)  Object-min-cardinality
	 5)  Object-max-cardinality
	 6)  Object-all-values-from
	 7)  Object-exact-cardinality	 
	 */

	private OWLObject generate(ClsExpStr conclusionExp) {
		
		if (conclusionExp.getExpressionType() == null) {
			return usedSymbols.get(((AtomicCls) conclusionExp).getPlaceholder());
		}
		
		ClassExpressionType classExpType = conclusionExp.getExpressionType();
		
		if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) ||
			classExpType.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {
			
			ExistsOrForAll specialisedPattern = (ExistsOrForAll) conclusionExp;

			OWLObjectPropertyExpression objPropExp = (OWLObjectPropertyExpression) generate(specialisedPattern.getProperty());
			OWLClassExpression classExp = (OWLClassExpression) generate((ClsExpStr) specialisedPattern.getExpression());			
			
			if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) ) {
				return new OWLObjectSomeValuesFromImpl(objPropExp, classExp);
			} else {
				return new OWLObjectAllValuesFromImpl(objPropExp, classExp);			
			}
			
		} else if (classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY) ||
				   classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY) ||
				   classExpType.equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {
		
			CardExpStr specialisedPattern = (CardExpStr) conclusionExp;
			
			int cardinality = Integer.parseInt(specialisedPattern.getCardinality());
			OWLObjectPropertyExpression objPropExp = (OWLObjectPropertyExpression) generate(specialisedPattern.getProperty());
			OWLClassExpression classExp = (OWLClassExpression) generate((ClsExpStr) specialisedPattern.getExpression());			
						
			if (classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)) {
				return new OWLObjectMinCardinalityImpl(objPropExp, cardinality, classExp );				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY)) {
				return new OWLObjectMaxCardinalityImpl(objPropExp, cardinality, classExp );				
			} else {
				return new OWLObjectExactCardinalityImpl(objPropExp, cardinality, classExp );				
			}
						
		} else if (classExpType.equals(ClassExpressionType.OBJECT_UNION_OF)) {
			// STUB
		} else if (classExpType.equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
			// STUB
		}

		return null;
	}
	
	
	private OWLObject generate(EntityStr conclusionExp) {
		return usedSymbols.get(conclusionExp.getAtomic());
	}
}
