package InferenceRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.HasProperty;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLQuantifiedDataRestriction;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
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
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDisjointClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectAllValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectExactCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMaxCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMinCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyDomainAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyRangeAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectUnionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubObjectPropertyOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLTransitiveObjectPropertyAxiomImpl;

public class RuleString {

	private String ruleID;
	private String ruleName;
	private OWLAxiomStr conclusionStr;
	private List<OWLAxiomStr> premisesStr;
	private int premiseNumber;


	private List<Map<String, OWLObject>> allInstantiations;
	private Map<String, OWLObject> currentVariableInstantiation;
	private Map<String, Integer> usedCardinalities;
	private Map<String, Set<OWLClassExpression>> currentGroupInstantiation;
	private RuleRestriction[] ruleRestrictions;


	public String getRuleID() {
		return ruleID;
	}
	
	public RuleString(String ruleID, String ruleName, OWLAxiomStr conclusion, List<OWLAxiomStr> premisesStr) {
		this.ruleID = ruleID;
		this.ruleName = ruleName;
		this.premisesStr = premisesStr;
		this.premiseNumber = premisesStr.size();
		this.conclusionStr = conclusion;
	}

	public RuleString(String ruleID, String ruleName, OWLAxiomStr conclusion, OWLAxiomStr... premises) {
		this.ruleID = ruleID;
		this.ruleName = ruleName;
		this.premisesStr = new ArrayList<OWLAxiomStr>(Arrays.asList(premises));		
		this.premiseNumber = premisesStr.size();
		this.conclusionStr = conclusion;
	}

	public RuleString(String ruleID, String ruleName, RuleRestriction[] ruleRestrictions, OWLAxiomStr conclusion, OWLAxiomStr... premises) {
		this.ruleID = ruleID;
		this.ruleName = ruleName;
		this.ruleRestrictions = ruleRestrictions;
		this.premisesStr = new ArrayList<OWLAxiomStr>(Arrays.asList(premises));		
		this.premiseNumber = premisesStr.size();
		this.conclusionStr = conclusion;
	}




	
	public boolean matchExpressions(List<OWLAxiom> expressions, List<OWLAxiomStr> expressionStr) {

		// Create the initial default instantiation of the True and False values.
		currentGroupInstantiation = new HashMap<String, Set<OWLClassExpression>>();
		usedCardinalities = new HashMap<String, Integer>();
		allInstantiations = new ArrayList<Map<String, OWLObject>>();		
		Map<String, OWLObject> defaultInstantiation = new HashMap<String, OWLObject>();
		
		OWLDataFactory dataFact = new OWLDataFactoryImpl();
		defaultInstantiation.put("F", dataFact.getOWLNothing());
		defaultInstantiation.put("T", dataFact.getOWLThing());
		
		allInstantiations.add(defaultInstantiation);
		
		for (int i = 0; i < expressions.size(); i++) {

			List<Map<String, OWLObject>> oldInstantiations = new ArrayList<Map<String, OWLObject>>(allInstantiations);
			allInstantiations = new ArrayList<Map<String, OWLObject>>();
			
			// For every premise, attempt to match it to every current instantiation.
			for (Map<String, OWLObject> instantiation : oldInstantiations) {

				currentVariableInstantiation = new HashMap<String, OWLObject>(instantiation);
				
				if (matchAxiom(expressions.get(i), expressionStr.get(i)) && currentVariableInstantiation != null) {
					allInstantiations.add(currentVariableInstantiation);
				}
			}
		}

		return !(allInstantiations.size() == 0);
	}

	
	public boolean matchPremises(List<OWLAxiom> premises) {
		List<OWLAxiomStr> expressions = new ArrayList<OWLAxiomStr>(premisesStr);
		return matchExpressions(premises, expressions);
	}
	

	
	// When matching both premises and a conclusion, simply treat the conclusion as an extra premise
	// and use the premise-matching algorithm.
	public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {	

		List<OWLAxiom> premisesAndConclusion = new ArrayList<OWLAxiom>(premises);
		premisesAndConclusion.add(conclusion);
		List<OWLAxiomStr> expressions = new ArrayList<OWLAxiomStr>(premisesStr);
		expressions.add(conclusionStr);
		return matchExpressions(premisesAndConclusion, expressions);
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


	private boolean matchAxiom(OWLAxiom axiom, OWLAxiomStr pattern) {

		if (axiom.isOfType(pattern.getConstructor())) {

			// Different definitions of "RBox", "TBox" and "ABox" axioms exist, hence
			// even though this implementation assumes the definition given in the thesis,
			// all other types will be checked.
			return matchRBoxAxiom(axiom, pattern) || matchTBoxAxiom(axiom, pattern)
						|| matchABoxAxiom(axiom, pattern);
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
					match(objPropRngAxiom.getRange(), (ClsExpStr) pattern.getExpressions().get(1));

		} else if (tBoxAxiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN)) {

			OWLObjectPropertyDomainAxiom objPropDomAxiom = (OWLObjectPropertyDomainAxiom) tBoxAxiom;
			return matchPrimitive(objPropDomAxiom.getProperty(), (TemplatePrimitive) pattern.getExpressions().get(0)) &&
					match(objPropDomAxiom.getDomain(), (ClsExpStr) pattern.getExpressions().get(1));

		} else if (tBoxAxiom.isOfType(AxiomType.DATA_PROPERTY_DOMAIN)) {

			OWLDataPropertyDomainAxiom dataPropDomAxiom = (OWLDataPropertyDomainAxiom) tBoxAxiom;
			return matchPrimitive(dataPropDomAxiom.getProperty(), (TemplatePrimitive) pattern.getExpressions().get(0)) &&
					match(dataPropDomAxiom.getDomain(), (ClsExpStr) pattern.getExpressions().get(1));

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



	private boolean matchABoxAxiom(OWLAxiom aBoxAxiom, OWLAxiomStr pattern) {

		// Currently only a "Different Individuals" axiom that has only two individuals is matched.
		if (aBoxAxiom.isOfType(AxiomType.DIFFERENT_INDIVIDUALS)) {

			OWLDifferentIndividualsAxiom diffIndividualsAxiom = (OWLDifferentIndividualsAxiom) aBoxAxiom;				
			List<OWLIndividual> individuals = diffIndividualsAxiom.getIndividualsAsList();
			TemplatePrimitive iStr  = (TemplatePrimitive) pattern.getExpressions().get(0);
			TemplatePrimitive jStr  = (TemplatePrimitive) pattern.getExpressions().get(1);

			return (individuals.size() == 2) && (
					(matchPrimitive(individuals.get(0), iStr) && matchPrimitive(individuals.get(1), jStr)) ||
					(matchPrimitive(individuals.get(1), iStr) && matchPrimitive(individuals.get(0), jStr)));
		}	

		return false;
	}




	private boolean matchGroupAxiom(Set<OWLClassExpression> classExpressions, ExpressionGroup pattern) {

		List<Map<String, OWLObject>> newInstantiations = new ArrayList<Map<String, OWLObject>>();		
		PermutationGenerator<OWLClassExpression> permGen = new PermutationGenerator<OWLClassExpression>();
		List<List<OWLClassExpression>> allPermutations = permGen.generatePermutations(new ArrayList<OWLClassExpression> (classExpressions));

		Map<String, OWLObject> oldInstantiation = currentVariableInstantiation;
		boolean atLeastOneMatch = false;

		for (List<OWLClassExpression> permutation : allPermutations) {

			currentVariableInstantiation = new HashMap<String, OWLObject>(oldInstantiation);

			if (matchOrderedList(permutation, pattern)) {
				newInstantiations.add(currentVariableInstantiation);
				atLeastOneMatch= true;
			}				
		}

		if (atLeastOneMatch) {
			allInstantiations.addAll(newInstantiations);
		}
		
		currentVariableInstantiation = null;
		
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





	// Assumption: all of the named expressions in "pattern" are already instantiated.
	private boolean matchFullyInstantiatedGroup(Set<OWLClassExpression> classExpressions, ExpressionGroup pattern) {

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



	// Currently all class expression pattern matching is assumed to be producing at most one instantiation.
	private boolean match(OWLClassExpression classExp, ClsExpStr pattern) {

		if (pattern.getExpressionType() == null) {
			return addToMap(classExp, ((AtomicCls) pattern).getPlaceholder());
		}

		ClassExpressionType classExpType = classExp.getClassExpressionType();

		if (classExpType.equals(pattern.getExpressionType())) {

			if (classExpType.equals(ClassExpressionType.OBJECT_INTERSECTION_OF) || 
					classExpType.equals(ClassExpressionType.OBJECT_UNION_OF)) {

				OWLNaryBooleanClassExpression groupExpression = (OWLNaryBooleanClassExpression) classExp;			
				return matchFullyInstantiatedGroup(groupExpression.getOperands(), ((InterUnion) pattern).getExpressionGroup());

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

				return  matchCardinality(objCardRest.getCardinality(), specialisedPattern.getCardinality())
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

				OWLDataCardinalityRestriction dataCardRest = (OWLDataCardinalityRestriction) classExp;				
				CardExpGen specialisedPattern = (CardExpGen) pattern;

				return matchCardinality(dataCardRest.getCardinality(), specialisedPattern.getCardinality())
						&& matchPrimitive(dataCardRest.getProperty(), specialisedPattern.getProperty())
						&& matchPrimitive(dataCardRest.getFiller(), (TemplatePrimitive) specialisedPattern.getExpression());
			} 
		}
		return false;
	}



	private boolean matchPrimitive(OWLObject entity, TemplatePrimitive pattern) {
		return addToMap(entity, pattern.getAtomic());
	}
	
	private boolean matchCardinality(int cardinality, String pattern) {
				
		if (!usedCardinalities.keySet().contains(pattern)) {
			usedCardinalities.put(pattern, cardinality);
			return true;
		} else {
			return usedCardinalities.get(pattern).equals(cardinality);
		}
	}


	private boolean addToMap(OWLObject owlObj, String key) {

		if (!currentVariableInstantiation.keySet().contains(key)) {
			currentVariableInstantiation.put(key, owlObj);
			return true;			
		} else {
			return currentVariableInstantiation.get(key).equals(owlObj);			
		}		
	}



	// Return all possible conclusions that can be generated.
	public List<OWLAxiom> generateConclusions(List<OWLAxiom> premises) {

		List<OWLAxiom> conclusion = new ArrayList<OWLAxiom>();

		// Attempt to match premises.
		if (matchPremises(premises)) {

			// Iterate over all possible instantiations and attempt to generate a conclusion
			// from each one.
			for (Map<String, OWLObject> instantiation : allInstantiations) {
				currentVariableInstantiation = instantiation;
				conclusion.addAll(generateConclusion());				
			}
		}

		return conclusion;
	}


	private List<OWLAxiom> generateConclusion() {

		List<OWLAxiom> conclusions = new ArrayList<OWLAxiom>();
		OWLAxiom conclusionAxiom = null;
		AxiomType<?>  conclusionType = conclusionStr.getConstructor();

		if (conclusionType.equals(AxiomType.SUBCLASS_OF)) {
			OWLClassExpression subCls = (OWLClassExpression) generate((ClsExpStr) conclusionStr.getExpressions().get(0)).get(0);
			OWLClassExpression superCls = (OWLClassExpression) generate((ClsExpStr) conclusionStr.getExpressions().get(1)).get(0);
			conclusionAxiom = new OWLSubClassOfAxiomImpl(subCls, superCls, new ArrayList<OWLAnnotation>());
			conclusions.add(conclusionAxiom);

		} else if (conclusionType.equals(AxiomType.SUB_OBJECT_PROPERTY)) {
			OWLObjectProperty subProperty = (OWLObjectProperty) generate((TemplatePrimitive) conclusionStr.getExpressions().get(0));
			OWLObjectProperty superProperty = (OWLObjectProperty) generate((TemplatePrimitive) conclusionStr.getExpressions().get(1));
			conclusionAxiom = new OWLSubObjectPropertyOfAxiomImpl(subProperty, superProperty, new ArrayList<OWLAnnotation>());
			conclusions.add(conclusionAxiom);

		} else if (conclusionType.equals(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {

			OWLObjectProperty transProperty = (OWLObjectProperty) generate((TemplatePrimitive) conclusionStr.getExpressions().get(0));
			conclusionAxiom = new OWLTransitiveObjectPropertyAxiomImpl(transProperty, new ArrayList<OWLAnnotation>());
			conclusions.add(conclusionAxiom);

		} else if (conclusionType.equals(AxiomType.OBJECT_PROPERTY_DOMAIN)) {

			OWLObjectProperty property = (OWLObjectProperty) generate((TemplatePrimitive) conclusionStr.getExpressions().get(0));
			OWLClassExpression classExp = (OWLClassExpression) generate((ClsExpStr) conclusionStr.getExpressions().get(1)).get(0);
			conclusionAxiom = new OWLObjectPropertyDomainAxiomImpl(property, classExp, new HashSet<OWLAnnotation>());
			conclusions.add(conclusionAxiom);

		} else if (conclusionType.equals(AxiomType.OBJECT_PROPERTY_RANGE)) {

			OWLObjectProperty property = (OWLObjectProperty) generate((TemplatePrimitive) conclusionStr.getExpressions().get(0));
			OWLClassExpression classExp = (OWLClassExpression) generate((ClsExpStr) conclusionStr.getExpressions().get(1)).get(0);
			conclusionAxiom = new OWLObjectPropertyRangeAxiomImpl(property, classExp, new HashSet<OWLAnnotation>());
			conclusions.add(conclusionAxiom);


			// Assumption: all free variables in the template have been instantiated
		} else if (conclusionType.equals(AxiomType.DISJOINT_CLASSES)) {

			conclusionAxiom = new OWLDisjointClassesAxiomImpl(generateGroup(conclusionStr.getExpressionGroup()), new HashSet<OWLAnnotation>());			
			conclusions.add(conclusionAxiom);
		}

		return 	conclusions;

	}



	private List<OWLObject> generate(ClsExpStr conclusionExp) {

		List<OWLObject> expressions = new ArrayList<OWLObject>();
		OWLObject expression = null;


		if (conclusionExp.getExpressionType() == null) {
			expression = currentVariableInstantiation.get(((AtomicCls) conclusionExp).getPlaceholder());
			expressions.add(expression);
		} else {

			ClassExpressionType classExpType = conclusionExp.getExpressionType();

			if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) ||
					classExpType.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {

				ExistsOrForAll specialisedPattern = (ExistsOrForAll) conclusionExp;

				OWLObjectPropertyExpression objPropExp = (OWLObjectPropertyExpression) generate(specialisedPattern.getProperty());
				OWLClassExpression classExp = (OWLClassExpression) generate((ClsExpStr) specialisedPattern.getExpression()).get(0);			

				if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) ) {
					expression =  new OWLObjectSomeValuesFromImpl(objPropExp, classExp);
				} else {
					expression =  new OWLObjectAllValuesFromImpl(objPropExp, classExp);			
				}
				expressions.add(expression);


			} else if (classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY) ||
					classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY) ||
					classExpType.equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {

				CardExpGen specialisedPattern = (CardExpGen) conclusionExp;

				int cardinality = generateCardinality(specialisedPattern.getCardinality());
				OWLObjectPropertyExpression objPropExp = (OWLObjectPropertyExpression) generate(specialisedPattern.getProperty());
				OWLClassExpression classExp = (OWLClassExpression) generate((ClsExpStr) specialisedPattern.getExpression()).get(0);			

				if (classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)) {
					expression =  new OWLObjectMinCardinalityImpl(objPropExp, cardinality, classExp );				
				} else if (classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY)) {
					expression =  new OWLObjectMaxCardinalityImpl(objPropExp, cardinality, classExp );				
				} else {
					expression =  new OWLObjectExactCardinalityImpl(objPropExp, cardinality, classExp );				
				}
				expressions.add(expression);


			} else if (classExpType.equals(ClassExpressionType.OBJECT_UNION_OF) ||
					classExpType.equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {

				Set<Set<OWLClassExpression>> possibleSets = new HashSet<Set<OWLClassExpression>>();

				Set<OWLClassExpression> expressionSet = new HashSet<OWLClassExpression>();
				ExpressionGroup expGroup = ((InterUnion) conclusionExp).getExpressionGroup();
				ClsExpStr[] namedExpressions = expGroup.getNamedExpressions();
				boolean noNamedExpressions = (namedExpressions == null) || (namedExpressions.length == 0);


				// Assumption: for now, either the group is fully named, or fully anonymous.
				if (expGroup.hasAnonymousExpressions() && noNamedExpressions)  {

					String anonGroupName = expGroup.getAnonymousGroupName();
					String superSetName = "";
					boolean groupFound = false;

					for (RuleRestriction restriction : ruleRestrictions) {
						if (restriction instanceof subSetRestriction) {
							subSetRestriction subSetRest = (subSetRestriction) restriction;

							if (subSetRest.getSubClass().equals(anonGroupName)) {
								superSetName = subSetRest.getSuperClass();
								groupFound = true;
								break;
							}					
						}						
					}

					if (groupFound) {
						Set<OWLClassExpression> superSet = currentGroupInstantiation.get(superSetName);
						PermutationGenerator<OWLClassExpression> permGen = new PermutationGenerator<OWLClassExpression>();

						possibleSets = permGen.generatePowerSet(superSet);
						Set<OWLClassExpression> emptySet = new HashSet<OWLClassExpression>();
						possibleSets.remove(superSet);
						possibleSets.remove(emptySet);
					}

				} else if (!expGroup.hasAnonymousExpressions() && !noNamedExpressions) {

					for (ClsExpStr namedExpression : namedExpressions) {
						expressionSet.add((OWLClassExpression) generate(namedExpression).get(0)); 
					}

					possibleSets.add(expressionSet);
				}


				for (Set<OWLClassExpression> group : possibleSets) {

					if (classExpType.equals(ClassExpressionType.OBJECT_UNION_OF)) {
						OWLObjectUnionOfImpl union = new OWLObjectUnionOfImpl(group);
						expressions.add(union);			

					} else {
						OWLObjectIntersectionOfImpl intersection = new OWLObjectIntersectionOfImpl(group);
						expressions.add(intersection);
					}

				}
			} 
		}

		return expressions;
	}


	private OWLObject generate(TemplatePrimitive conclusionExp) {
		return currentVariableInstantiation.get(conclusionExp.getAtomic());
	}
	
	private int generateCardinality(String pattern) {
		if (usedCardinalities.containsKey(pattern)) {
			return usedCardinalities.get(pattern);
		} else {
			
			// Need to decide on how to generate non-defined cardinalities
			return -1;
		}
	}


	private Set<OWLClassExpression> generateGroup(ExpressionGroup expGroupStr) {

		Set<OWLClassExpression> expGroup = new HashSet<OWLClassExpression>();

		for (GenericExpStr namedExpression : expGroupStr.getNamedExpressions()) {		
			expGroup.add((OWLClassExpression) currentVariableInstantiation.get(((AtomicCls) namedExpression).getPlaceholder()));
		}

		return expGroup;
	}
}
