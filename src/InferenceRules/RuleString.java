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
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
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

import OWLExpressionTemplates.AtomicCls;
import OWLExpressionTemplates.CardExpGen;
import OWLExpressionTemplates.ClsExpStr;
import OWLExpressionTemplates.ComplementCls;
import OWLExpressionTemplates.TemplatePrimitive;
import OWLExpressionTemplates.ExistsOrForAll;
import OWLExpressionTemplates.InterUnion;
import OWLExpressionTemplates.OWLAxiomStr;
import OWLExpressionTemplates.TemplatePrimitive;
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
	private Map<String, OWLNaryBooleanClassExpression> groupOfObjects;
	private List<NaryClassExpressionSubset> subSetRestrictions;


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
	
	public RuleString(String ruleID, String ruleName, List<NaryClassExpressionSubset> subSetRestrictions, OWLAxiomStr conclusion, OWLAxiomStr... premises) {
		this.ruleID = ruleID;
		this.ruleName = ruleName;
		this.subSetRestrictions = subSetRestrictions;
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

		if (axiom.isOfType(pattern.getConstructor())) {
			
			if (axiom.isOfType(AxiomType.RBoxAxiomTypes)) {
				return matchRBoxAxiom(axiom, pattern);
				
			} else if (axiom.isOfType(AxiomType.TBoxAxiomTypes)) {
				return matchTBoxAxiom(axiom, pattern);
				
			} else if (axiom.isOfType(AxiomType.ABoxAxiomTypes)) {				
				return matchABoxAxiom(axiom, pattern);
			}		
		}
		return false;
	}


	
	private boolean matchRBoxAxiom(OWLAxiom rBoxAxiom, OWLAxiomStr pattern) {
		
		if (rBoxAxiom.isOfType(AxiomType.SUB_OBJECT_PROPERTY)) {

			OWLSubObjectPropertyOfAxiom subObjPropAxiom = (OWLSubObjectPropertyOfAxiom) rBoxAxiom;

			return matchPrimitive(subObjPropAxiom.getSubProperty(), (TemplatePrimitive) pattern.getExpressions().get(0))
					&& matchPrimitive(subObjPropAxiom.getSuperProperty(), (TemplatePrimitive) pattern.getExpressions().get(1));

		} else if (rBoxAxiom.isOfType(AxiomType.INVERSE_OBJECT_PROPERTIES)) {

			OWLInverseObjectPropertiesAxiom invObjPropAxiom = (OWLInverseObjectPropertiesAxiom) rBoxAxiom;
	
			return matchPrimitive(invObjPropAxiom.getFirstProperty(), (TemplatePrimitive) pattern.getExpressions().get(0))
					&& matchPrimitive(invObjPropAxiom.getSecondProperty(), (TemplatePrimitive) pattern.getExpressions().get(1));
					
		} else if (rBoxAxiom.isOfType(AxiomType.FUNCTIONAL_OBJECT_PROPERTY) ||
					rBoxAxiom.isOfType(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY) ||
					rBoxAxiom.isOfType(AxiomType.SYMMETRIC_OBJECT_PROPERTY) ||
					rBoxAxiom.isOfType(AxiomType.TRANSITIVE_OBJECT_PROPERTY) ||
					rBoxAxiom.isOfType(AxiomType.FUNCTIONAL_DATA_PROPERTY)) {
								
			HasProperty<OWLProperty> axiomWithProperty = (HasProperty<OWLProperty>) rBoxAxiom;
			return matchPrimitive(axiomWithProperty.getProperty(), (TemplatePrimitive) pattern.getExpressions().get(0));
		
		} else {
			
			return false;
		}
	}
	
	
	
	private boolean matchTBoxAxiom(OWLAxiom tBoxAxiom, OWLAxiomStr pattern) {
		
		if (tBoxAxiom.isOfType(AxiomType.SUBCLASS_OF)) {

			OWLSubClassOfAxiom subClsAxiom = (OWLSubClassOfAxiom) tBoxAxiom;
			
			return match(subClsAxiom.getSubClass(), (ClsExpStr) pattern.getExpressions().get(0))
					&& match(subClsAxiom.getSuperClass(), (ClsExpStr) pattern.getExpressions().get(1));

		} else if (tBoxAxiom.isOfType(AxiomType.OBJECT_PROPERTY_RANGE) ) {

			OWLObjectPropertyRangeAxiom objPropRngAxiom = (OWLObjectPropertyRangeAxiom) tBoxAxiom;
			return matchPrimitive(objPropRngAxiom.getProperty(), (TemplatePrimitive) pattern.getExpressions().get(0)) &&
					matchPrimitive(objPropRngAxiom.getRange(), (TemplatePrimitive) pattern.getExpressions().get(1));

		} else if (tBoxAxiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN)) {

			OWLObjectPropertyDomainAxiom objPropDomAxiom = (OWLObjectPropertyDomainAxiom) tBoxAxiom;
			return matchPrimitive(objPropDomAxiom.getProperty(), (TemplatePrimitive) pattern.getExpressions().get(0)) &&
					matchPrimitive(objPropDomAxiom.getDomain(), (TemplatePrimitive) pattern.getExpressions().get(1));

		} else if (tBoxAxiom.isOfType(AxiomType.DATA_PROPERTY_DOMAIN)) {

			OWLDataPropertyDomainAxiom dataPropDomAxiom = (OWLDataPropertyDomainAxiom) tBoxAxiom;
			return matchPrimitive(dataPropDomAxiom.getProperty(), (TemplatePrimitive) pattern.getExpressions().get(0)) &&
					matchPrimitive(dataPropDomAxiom.getDomain(), (TemplatePrimitive) pattern.getExpressions().get(1));
		
		} else if (tBoxAxiom.isOfType(AxiomType.DATA_PROPERTY_RANGE)) {

			OWLDataPropertyRangeAxiom dataPropRngAxiom = (OWLDataPropertyRangeAxiom) tBoxAxiom;
			return matchPrimitive(dataPropRngAxiom.getProperty(), (TemplatePrimitive) pattern.getExpressions().get(0)) &&
					matchPrimitive(dataPropRngAxiom.getRange(), (TemplatePrimitive) pattern.getExpressions().get(1));
		
		} else if (tBoxAxiom.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
			
			OWLEquivalentClassesAxiom eqvClassesAxiom = (OWLEquivalentClassesAxiom) tBoxAxiom;
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
		} else {
			return false;
		}
	}

	
	
	
	
	private boolean matchABoxAxiom(OWLAxiom aBoxAxiom, OWLAxiomStr pattern) {

		if (aBoxAxiom.isOfType(AxiomType.DIFFERENT_INDIVIDUALS)) {
			
			OWLDifferentIndividualsAxiom diffIndividualsAxiom = (OWLDifferentIndividualsAxiom) aBoxAxiom;				
			boolean matchedFirstArgument = false;
			boolean matchedSecondArgument = false;
			
			for (OWLIndividual i : diffIndividualsAxiom.getIndividualsAsList()) {
				if (matchPrimitive(i, (TemplatePrimitive) pattern.getExpressions().get(0))) {
					matchedFirstArgument = true;
				}
				
				if (matchPrimitive(i, (TemplatePrimitive) pattern.getExpressions().get(1))) {
					matchedSecondArgument = true;
				}
			}
			
			return matchedFirstArgument && matchedSecondArgument;
		}	else {
			return false;
		}
	}

	
	

	private boolean match(OWLClassExpression classExp, ClsExpStr pattern) {
		
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
				return match(compObj.getOperand(), ((ComplementCls) pattern).getSubExpression());
					
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) ||
					   classExpType.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM) ||
					   classExpType.equals(ClassExpressionType.OBJECT_HAS_VALUE)) {
					
				OWLQuantifiedObjectRestriction objSomeValFrom = (OWLQuantifiedObjectRestriction) classExp;
				ExistsOrForAll specialisedPattern = (ExistsOrForAll) pattern;
				
				return matchPrimitive(objSomeValFrom.getProperty(), specialisedPattern.getProperty())
						&& match(objSomeValFrom.getFiller(), (ClsExpStr) specialisedPattern.getExpression());
				
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)  ||
						classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY) ||
						classExpType.equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {
							
				OWLObjectCardinalityRestriction objCardRest = (OWLObjectCardinalityRestriction) classExp;				
				CardExpGen specialisedPattern = (CardExpGen) pattern;

				return matchCardinality(objCardRest.getCardinality(), specialisedPattern) 
						&& matchPrimitive(objCardRest.getProperty(), specialisedPattern.getProperty())
						&& match(objCardRest.getFiller(), (ClsExpStr) specialisedPattern.getExpression());
	
				
			}  else if (classExpType.equals(ClassExpressionType.DATA_SOME_VALUES_FROM) ||
					   classExpType.equals(ClassExpressionType.DATA_ALL_VALUES_FROM) ||
					   classExpType.equals(ClassExpressionType.DATA_HAS_VALUE)) {
				
				OWLQuantifiedDataRestriction quantDataRest = (OWLQuantifiedDataRestriction) classExp;
				ExistsOrForAll specialisedPattern = (ExistsOrForAll) pattern;
				
				return matchPrimitive(quantDataRest.getProperty(), specialisedPattern.getProperty())
						&& matchPrimitive(quantDataRest.getFiller(), (TemplatePrimitive) specialisedPattern.getExpression());

				
			}  else if (classExpType.equals(ClassExpressionType.DATA_MIN_CARDINALITY) ||
					   classExpType.equals(ClassExpressionType.DATA_MAX_CARDINALITY) ||
					   classExpType.equals(ClassExpressionType.DATA_EXACT_CARDINALITY)) {
				
				OWLObjectCardinalityRestriction dataCardRest = (OWLObjectCardinalityRestriction) classExp;				
				CardExpGen specialisedPattern = (CardExpGen) pattern;

				return matchCardinality(dataCardRest.getCardinality(), specialisedPattern) 
						&& matchPrimitive(dataCardRest.getProperty(), specialisedPattern.getProperty())
						&& matchPrimitive(dataCardRest.getFiller(), (TemplatePrimitive) specialisedPattern.getExpression());
			} 
		}
		return false;
	}



	private boolean matchPrimitive(OWLObject entity, TemplatePrimitive pattern) {
		return addToMap(entity, pattern.getAtomic());
	}
	

	private boolean matchCardinality(Integer givenCardinality, CardExpGen specialisedPattern) {
		
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
		
			CardExpGen specialisedPattern = (CardExpGen) conclusionExp;
			
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
	
	
	private OWLObject generate(TemplatePrimitive conclusionExp) {
		return usedSymbols.get(conclusionExp.getAtomic());
	}
}
