package InferenceRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
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
import org.semanticweb.owlapi.model.OWLObjectProperty;
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
import OWLExpressionTemplates.ExpressionGroup;
import OWLExpressionTemplates.GenericExpStr;
import OWLExpressionTemplates.InterUnion;
import OWLExpressionTemplates.OWLAxiomStr;
import OWLExpressionTemplates.TemplatePrimitive;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectAllValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectExactCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMaxCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMinCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyDomainAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyRangeAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubObjectPropertyOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLTransitiveObjectPropertyAxiomImpl;

public class RuleString {

	private String ruleID;
	private String ruleName;
	private OWLAxiomStr conclusion;
	private List<OWLAxiomStr> premisesStr;
	private int premiseNumber;


	private List<Map<String, OWLObject>> allInstantiations;
	private Map<String, OWLObject> currentVariableInstantiation;
	private Map<String, Integer> usedCardinalities;
	private Map<String, Set<OWLClassExpression>> currentGroupInstantiation;
	private RuleRestriction[] ruleRestrictions;


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

	public RuleString(String ruleID, String ruleName, RuleRestriction[] ruleRestrictions, OWLAxiomStr conclusion, OWLAxiomStr... premises) {
		this.ruleID = ruleID;
		this.ruleName = ruleName;
		this.ruleRestrictions = ruleRestrictions;
		this.premisesStr = new ArrayList<OWLAxiomStr>(Arrays.asList(premises));		
		this.premiseNumber = premisesStr.size();
		this.conclusion = conclusion;
	}



	public boolean matchPremises(List<OWLAxiom> premises) {

		allInstantiations = new ArrayList<Map<String, OWLObject>>();
		allInstantiations.add(new HashMap<String, OWLObject>());

		for (int i = 0; i < premises.size(); i++) {
			for (Map<String, OWLObject> instantiation : allInstantiations) {

				currentVariableInstantiation = new HashMap<String, OWLObject>(instantiation);
				match(premises.get(i), premisesStr.get(i));
			}

		}

		return checkConstraints();
	}


	public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {		
		List<OWLAxiom> premisesAndConclusion = new ArrayList<OWLAxiom>(premises);
		premisesAndConclusion.add(conclusion);
		return matchPremises(premisesAndConclusion);	
	}




	private boolean checkConstraints() {

		for (RuleRestriction restriction : ruleRestrictions) {

			if (restriction instanceof AbsCardinalityRestriction) {

				AbsCardinalityRestriction absCardRest = (AbsCardinalityRestriction) restriction;
				Integer lowerBound = absCardRest.getSmallerCardinality();
				Integer cardinality = usedCardinalities.get(absCardRest.getLargerCardinality());
				return compare(cardinality, lowerBound, absCardRest.isStrictInequality());

			} else if (restriction instanceof RelCardinalityRestriction) {

				RelCardinalityRestriction relCardRest = (RelCardinalityRestriction) restriction;
				Integer lowerBound = usedCardinalities.get(relCardRest.getSmallerCardinality());
				Integer cardinality = usedCardinalities.get(relCardRest.getLargerCardinality());
				return compare(cardinality, lowerBound, relCardRest.isStrictInequality());

			} else if (restriction instanceof subSetRestriction) {
				// Implement
			}

		}
		return false;
	}


	private boolean compare(Integer largerInt, Integer smallerInt, boolean strictInequalty) {
		if (strictInequalty) {
			return largerInt > smallerInt;
		} else {
			return largerInt >= smallerInt;
		}
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
			return matchGroupAxiom(eqvClassesAxiom.getClassExpressions(), pattern.getExpressionGroup());

		} else if (tBoxAxiom.isOfType(AxiomType.DISJOINT_CLASSES)){

			OWLDisjointClassesAxiom disjClassesAxiom = (OWLDisjointClassesAxiom) tBoxAxiom;
			return matchGroupAxiom(disjClassesAxiom.getClassExpressions(), pattern.getExpressionGroup());

		} else {
			return false;
		}
	}




	private boolean matchGroupAxiom(Set<OWLClassExpression> classExpressions, ExpressionGroup pattern) {

		List<Map<String, OWLObject>> newInstantiations = new ArrayList<Map<String, OWLObject>>();		
		PermutationGenerator<OWLClassExpression> permGen = new PermutationGenerator<OWLClassExpression>();
		List<List<OWLClassExpression>> allPermutations = permGen.generatePermutations(new ArrayList<OWLClassExpression> (classExpressions));

		Map<String, OWLObject> oldInstantiation = currentVariableInstantiation;
		allInstantiations.remove(currentVariableInstantiation);
		
		boolean atLeastOneMatch = false;
		
		for (List<OWLClassExpression> permutation : allPermutations) {

			currentVariableInstantiation = new HashMap<String, OWLObject>(oldInstantiation);

			if (matchOrderedList(permutation, pattern)) {
				newInstantiations.add(currentVariableInstantiation);
				atLeastOneMatch= true;
			}				
		}
		
		allInstantiations.addAll(newInstantiations);
		return atLeastOneMatch;
	}



	private boolean matchOrderedList(List<OWLClassExpression> expList, ExpressionGroup pattern) {

		if (expList.size() < pattern.getNamedExpressions().length) {
			return false;
		}

		for (GenericExpStr namedExpression : pattern.getNamedExpressions()) {
			OWLClassExpression exp = expList.remove(0);

			if (!match(exp, (ClsExpStr) namedExpression)) {
				return false;
			}
		}

		return true;
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



	private boolean match(Set<OWLClassExpression> classExpressions, ExpressionGroup pattern) {

		if (!pattern.hasAnonymousExpressions() && (classExpressions.size() != pattern.getNamedExpressions().length)) {
			return false;
		}

		// throw an exception in the appropriate place
		for (GenericExpStr exp : pattern.getNamedExpressions()) {

			if (exp instanceof AtomicCls) {

				String placeholder = ((AtomicCls) exp).getPlaceholder();

				if (currentVariableInstantiation.containsKey(placeholder)) {

					OWLObject obj = currentVariableInstantiation.get(placeholder);

					if (classExpressions.contains(obj)) {
						classExpressions.remove(obj);
					} else {
						return false;
					}
				} else {
					return false;
				}

			}  else {
				return false;
			}		
		}

		if (pattern.hasAnonymousExpressions()) {
			currentGroupInstantiation.put(pattern.getAnonymousGroupName(), classExpressions);
		}

		return true;
	}


	private boolean match(OWLClassExpression classExp, ClsExpStr pattern) {

		if (pattern.getExpressionType() == null) {
			return addToMap((OWLObject) classExp, ((AtomicCls) pattern).getPlaceholder());
		}

		ClassExpressionType classExpType = classExp.getClassExpressionType();

		if (classExpType.equals(pattern.getExpressionType())) {

			if (classExpType.equals(ClassExpressionType.OBJECT_INTERSECTION_OF) || 
					classExpType.equals(ClassExpressionType.OBJECT_UNION_OF)) {

				OWLNaryBooleanClassExpression groupExpression = (OWLNaryBooleanClassExpression) classExp;			
				return match(groupExpression.getOperands(), ((InterUnion) pattern).getExpressionGroup());

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

				return matchPrimitive(objCardRest.getProperty(), specialisedPattern.getProperty())
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

				return matchPrimitive(dataCardRest.getProperty(), specialisedPattern.getProperty())
						&& matchPrimitive(dataCardRest.getFiller(), (TemplatePrimitive) specialisedPattern.getExpression());
			} 
		}
		return false;
	}



	private boolean matchPrimitive(OWLObject entity, TemplatePrimitive pattern) {
		return addToMap(entity, pattern.getAtomic());
	}


	private boolean addToMap(OWLObject owlObj, String key) {

		if (!currentVariableInstantiation.keySet().contains(key)) {
			currentVariableInstantiation.put(key, owlObj);
			return true;			
		} else {
			return currentVariableInstantiation.get(key).equals(owlObj);			
		}		
	}




	public OWLAxiom generateConclusion(List<OWLAxiom> premises) {

		OWLAxiom conclusionAxiom = null;

		if (matchPremises(premises)) {

			AxiomType  conclusionType = conclusion.getConstructor();

			if (conclusionType.equals(AxiomType.SUBCLASS_OF)) {
				OWLClassExpression subCls = (OWLClassExpression) generate((ClsExpStr) conclusion.getExpressions().get(0));
				OWLClassExpression superCls = (OWLClassExpression) generate((ClsExpStr) conclusion.getExpressions().get(1));
				conclusionAxiom = new OWLSubClassOfAxiomImpl(subCls, superCls, new ArrayList<OWLAnnotation>());

			} else if (conclusionType.equals(AxiomType.SUB_OBJECT_PROPERTY)) {
				OWLObjectProperty subProperty = (OWLObjectProperty) generate((TemplatePrimitive) conclusion.getExpressions().get(0));
				OWLObjectProperty superProperty = (OWLObjectProperty) generate((TemplatePrimitive) conclusion.getExpressions().get(1));
				conclusionAxiom = new OWLSubObjectPropertyOfAxiomImpl(subProperty, superProperty, new ArrayList<OWLAnnotation>());

			} else if (conclusionType.equals(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {

				OWLObjectProperty transProperty = (OWLObjectProperty) generate((TemplatePrimitive) conclusion.getExpressions().get(0));
				conclusionAxiom = new OWLTransitiveObjectPropertyAxiomImpl(transProperty, new ArrayList<OWLAnnotation>());

			} else if (conclusionType.equals(AxiomType.OBJECT_PROPERTY_DOMAIN)) {

				OWLObjectProperty property = (OWLObjectProperty) generate((TemplatePrimitive) conclusion.getExpressions().get(0));
				OWLClassExpression classExp = (OWLClassExpression) generate((ClsExpStr) conclusion.getExpressions().get(1));
				conclusionAxiom = new OWLObjectPropertyDomainAxiomImpl(property, classExp, null);

			} else if (conclusionType.equals(AxiomType.OBJECT_PROPERTY_RANGE)) {

				OWLObjectProperty property = (OWLObjectProperty) generate((TemplatePrimitive) conclusion.getExpressions().get(0));
				OWLClassExpression classExp = (OWLClassExpression) generate((ClsExpStr) conclusion.getExpressions().get(1));
				conclusionAxiom = new OWLObjectPropertyRangeAxiomImpl(property, classExp, null);

			} else if (conclusionType.equals(AxiomType.DISJOINT_CLASSES)) {
				// ToDo
			}
		}		
		return conclusionAxiom;
	}



	private OWLObject generate(ClsExpStr conclusionExp) {

		if (conclusionExp.getExpressionType() == null) {
			return currentVariableInstantiation.get(((AtomicCls) conclusionExp).getPlaceholder());
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
		return currentVariableInstantiation.get(conclusionExp.getAtomic());
	}
}
