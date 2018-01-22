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
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
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
import OWLExpressionTemplates.UninstantiatedCardinalityException;
import RuleRestrictions.AbsCardinalityRestriction;
import RuleRestrictions.CardinalitySign;
import RuleRestrictions.DisjointDatatypesRestriction;
import RuleRestrictions.RelCardinalityRestriction;
import RuleRestrictions.RestrictionChecker;
import RuleRestrictions.RuleRestriction;
import RuleRestrictions.SubSetRestriction;
import OWLExpressionTemplates.ExistsOrForAll;
import OWLExpressionTemplates.ExpressionGroup;
import OWLExpressionTemplates.GenericExpStr;
import OWLExpressionTemplates.InterUnion;
import OWLExpressionTemplates.OWLAxiomStr;
import OWLExpressionTemplates.TemplateDataProperty;
import OWLExpressionTemplates.TemplateDataRange;
import OWLExpressionTemplates.TemplateIndividual;
import OWLExpressionTemplates.TemplateLiteral;
import OWLExpressionTemplates.TemplateObjectProperty;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDisjointClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectAllValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectCardinalityRestrictionImpl;
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

	private Instantiation currentInstantiation;
	private List<Instantiation> allInstantiations;
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
		this.ruleRestrictions = new RuleRestriction[]{};
	}

	public RuleString(String ruleID, String ruleName, OWLAxiomStr conclusion, OWLAxiomStr... premises) {
		this(ruleID, ruleName, conclusion, new ArrayList<OWLAxiomStr>(Arrays.asList(premises)));
	}

	public RuleString(String ruleID, String ruleName, RuleRestriction[] ruleRestrictions, OWLAxiomStr conclusion, OWLAxiomStr... premises) {
		this(ruleID, ruleName, conclusion, premises);
		this.ruleRestrictions = ruleRestrictions;
	}



	private void initializeInstantiations() {
		
		// Create the initial default instantiation of the True and False values.
		allInstantiations = new ArrayList<Instantiation>();		
		Instantiation defaultInstantiation = new Instantiation();
		
		OWLDataFactory dataFact = new OWLDataFactoryImpl();
		defaultInstantiation.getVariableInstantiation().put("F", dataFact.getOWLNothing());
		defaultInstantiation.getVariableInstantiation().put("T", dataFact.getOWLThing());
		
		allInstantiations.add(defaultInstantiation);
	}
	
	
	public boolean matchExpressions(List<OWLAxiom> expressions, List<OWLAxiomStr> expressionStr) {

		initializeInstantiations();
		
		for (int i = 0; i < expressions.size(); i++) {

			List<Instantiation> prevInstantiations = new ArrayList<Instantiation>(allInstantiations);
			allInstantiations = new ArrayList<Instantiation>();
			
			// For every premise, attempt to match it to every current instantiation.
			for (Instantiation instantiation : prevInstantiations) {
				
				currentInstantiation = instantiation;
			
				if (matchAxiom(expressions.get(i), expressionStr.get(i)) && currentInstantiation != null) {
					allInstantiations.add(currentInstantiation);
				}
			}
		}
		
		cleanupInstantiations();
		
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

	
	private void cleanupInstantiations() {
		
		List<Instantiation> prevInstantiations = new ArrayList<Instantiation>(allInstantiations);
		allInstantiations = new ArrayList<Instantiation>();

		for (Instantiation instantiation : prevInstantiations) {
			
			RestrictionChecker restrictionChecker = new RestrictionChecker(ruleRestrictions, instantiation);
			
			if (restrictionChecker.checkRestrictionsForInstantiation()) {
				allInstantiations.add(instantiation);
			}			
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
	

	// Assume sub object property consists of two primitives.
	private boolean matchSubObjectProperty(OWLSubObjectPropertyOfAxiom subObjPropAxiom, OWLAxiomStr pattern) {
		
		List<GenericExpStr> patternExpressions = pattern.getExpressions();
		
		if (patternExpressions.size() == 2) {
			
			if (patternExpressions.get(0) instanceof TemplateObjectProperty &&
				patternExpressions.get(1) instanceof TemplateObjectProperty) {
			
				TemplateObjectProperty subProperty = (TemplateObjectProperty) patternExpressions.get(0);
				TemplateObjectProperty superProperty = (TemplateObjectProperty) patternExpressions.get(1);
				
				return matchPrimitive(subObjPropAxiom.getSubProperty(), subProperty) &&
						matchPrimitive(subObjPropAxiom.getSuperProperty(), superProperty);
			}
		}		
		return false;
	}
	
	// Used to match an axiom containing a single primitive property as an argument.
	// Cases include: functional object property, inverse functional object property,
	// symmetric object property, transitive object property and functional data property.
	private boolean matchAxiomWithProperty(HasProperty<OWLProperty> axiomWithProperty, OWLAxiomStr pattern) {
		
		List<GenericExpStr> patternExpressions = pattern.getExpressions();
		
		if (patternExpressions.size() == 1) {
			if (patternExpressions.get(0) instanceof TemplatePrimitive) {
				TemplatePrimitive primitiveProperty = (TemplatePrimitive) patternExpressions.get(0);
				
				return matchPrimitive(axiomWithProperty.getProperty(), primitiveProperty);
			}
		}
		
		return false;
	}

	
	// This assumes that the first listed inverse property has already been instantiated.
	private boolean matchInverseObjectProperty(OWLInverseObjectPropertiesAxiom invObjPropAxiom, OWLAxiomStr pattern) {
		
		List<GenericExpStr> patternExpressions = pattern.getExpressions();

		if (patternExpressions.size() != 2) {
			return false; 
		}

		if (patternExpressions.get(0) instanceof TemplateObjectProperty && patternExpressions.get(1) instanceof TemplateObjectProperty) {

			TemplateObjectProperty instantiatedProperty = (TemplateObjectProperty) patternExpressions.get(0);
			TemplateObjectProperty uninstantiatedProperty = (TemplateObjectProperty) patternExpressions.get(1);

			// We assume that the first property has already been instantiated.
			if (!currentInstantiation.getVariableInstantiation().containsKey(instantiatedProperty.getAtomic())) {
				return false;
			}

			OWLObject obj = currentInstantiation.getVariableInstantiation().get(instantiatedProperty.getAtomic());

			if (obj instanceof OWLObjectPropertyExpression) {
				OWLObjectPropertyExpression instantiatedPropertyValue = (OWLObjectPropertyExpression) obj;
				
				if (instantiatedPropertyValue.equals(invObjPropAxiom.getFirstProperty())) {
					return matchPrimitive(invObjPropAxiom.getSecondProperty(), uninstantiatedProperty);
				
				} else if (instantiatedPropertyValue.equals(invObjPropAxiom.getSecondProperty())) {
					return matchPrimitive(invObjPropAxiom.getFirstProperty(), uninstantiatedProperty);
				}
			}
		}
		return false;
	}
	
	
	private boolean matchRBoxAxiom(OWLAxiom rBoxAxiom, OWLAxiomStr pattern) {
		
		if (rBoxAxiom.isOfType(AxiomType.SUB_OBJECT_PROPERTY)) {
			OWLSubObjectPropertyOfAxiom subObjPropAxiom = (OWLSubObjectPropertyOfAxiom) rBoxAxiom;
			return matchSubObjectProperty(subObjPropAxiom, pattern);

		} else if (rBoxAxiom.isOfType(AxiomType.INVERSE_OBJECT_PROPERTIES)) {
			OWLInverseObjectPropertiesAxiom invObjPropAxiom = (OWLInverseObjectPropertiesAxiom) rBoxAxiom;
			return matchInverseObjectProperty(invObjPropAxiom, pattern);
			
		} else if (rBoxAxiom.isOfType(AxiomType.FUNCTIONAL_OBJECT_PROPERTY) ||
				rBoxAxiom.isOfType(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY) ||
				rBoxAxiom.isOfType(AxiomType.SYMMETRIC_OBJECT_PROPERTY) ||
				rBoxAxiom.isOfType(AxiomType.TRANSITIVE_OBJECT_PROPERTY) ||
				rBoxAxiom.isOfType(AxiomType.FUNCTIONAL_DATA_PROPERTY)) {

			HasProperty<OWLProperty> axiomWithProperty = (HasProperty<OWLProperty>) rBoxAxiom;
			return matchAxiomWithProperty(axiomWithProperty, pattern);
			
		} 
		return false;
	}


	private boolean matchObjectPropertyDomain(OWLObjectPropertyDomainAxiom objPropDomAxiom, TemplateObjectProperty property, AtomicCls domain) {

		return match(objPropDomAxiom.getDomain(), domain) &&
				matchPrimitive(objPropDomAxiom.getProperty(), property);
	}
	
	
	private boolean matchObjectPropertyRange(OWLObjectPropertyRangeAxiom objRngDomAxiom, TemplateObjectProperty property, AtomicCls range) {

		return match(objRngDomAxiom.getRange(), range) &&
				matchPrimitive(objRngDomAxiom.getProperty(), property);
	}
	
	private boolean matchDataPropertyRange(OWLDataPropertyRangeAxiom dataRngDomAxiom, TemplateDataProperty property, TemplateDataRange range) {

		return matchPrimitive(dataRngDomAxiom.getRange(), range) &&
				matchPrimitive(dataRngDomAxiom.getProperty(), property);
	}
	
	
	private boolean matchDataPropertyDomain(OWLDataPropertyDomainAxiom dataPropDomAxiom, TemplateDataProperty property, AtomicCls range) {

		return match(dataPropDomAxiom.getDomain(), range) &&
				matchPrimitive(dataPropDomAxiom.getProperty(), property);
	}

	
	// Here we assume that in all 4 cases (data domain, data range, object domain and object range) that both
	// variables are primitive.
	private boolean matchDataOrObjectDomainOrRange(OWLAxiom axiom, OWLAxiomStr pattern) {

		List<GenericExpStr> patternExpressions = pattern.getExpressions();

		if (patternExpressions.size() != 2) {
			return false; 
		}
		
		if (patternExpressions.get(1) instanceof AtomicCls) {
			AtomicCls cls = (AtomicCls) patternExpressions.get(1);
			
			if (axiom instanceof OWLObjectPropertyDomainAxiom && patternExpressions.get(0) instanceof TemplateObjectProperty) {
				return matchObjectPropertyDomain((OWLObjectPropertyDomainAxiom) axiom, (TemplateObjectProperty) patternExpressions.get(0), cls);						
			
			} else if (axiom instanceof OWLObjectPropertyRangeAxiom && patternExpressions.get(0) instanceof TemplateObjectProperty) {
				return matchObjectPropertyRange((OWLObjectPropertyRangeAxiom) axiom, (TemplateObjectProperty) patternExpressions.get(0), cls);

			} else if (axiom instanceof OWLDataPropertyDomainAxiom && patternExpressions.get(0) instanceof TemplateDataProperty) {
				return matchDataPropertyDomain((OWLDataPropertyDomainAxiom) axiom, (TemplateDataProperty) patternExpressions.get(0), cls);				
			}
	
		} else if (axiom instanceof OWLDataPropertyRangeAxiom && patternExpressions.get(1) instanceof TemplateDataRange &&
				   patternExpressions.get(0) instanceof TemplateDataProperty) {
			
			return matchDataPropertyRange((OWLDataPropertyRangeAxiom) axiom, (TemplateDataProperty) patternExpressions.get(0), (TemplateDataRange) patternExpressions.get(1));
		}	
		return false;
	}
	
	
	private boolean matchSubClassAxiom(OWLSubClassOfAxiom subClsAxiom, OWLAxiomStr pattern) {
		
		if (!(pattern.getExpressions().size() == 2 &&
			  pattern.getExpressions().get(0) instanceof ClsExpStr &&
			  pattern.getExpressions().get(1) instanceof ClsExpStr)) {
			
			return false;
		}
		
		ClsExpStr subClass = (ClsExpStr) pattern.getExpressions().get(0);
		ClsExpStr superClass = (ClsExpStr) pattern.getExpressions().get(1);
				
		return match(subClsAxiom.getSubClass(), subClass) && 
			   match(subClsAxiom.getSuperClass(), superClass);
	}

	
	private boolean matchTBoxAxiom(OWLAxiom tBoxAxiom, OWLAxiomStr pattern) {

		if (tBoxAxiom.isOfType(AxiomType.SUBCLASS_OF)) {
			return matchSubClassAxiom((OWLSubClassOfAxiom) tBoxAxiom, pattern);

		} else if (tBoxAxiom.isOfType(AxiomType.EQUIVALENT_CLASSES)) {

			OWLEquivalentClassesAxiom eqvClassesAxiom = (OWLEquivalentClassesAxiom) tBoxAxiom;
			return matchGroupAxiomNonUniquely(eqvClassesAxiom.getClassExpressions(), pattern.getExpressionGroup());

		} else if (tBoxAxiom.isOfType(AxiomType.DISJOINT_CLASSES)){

			OWLDisjointClassesAxiom disjClassesAxiom = (OWLDisjointClassesAxiom) tBoxAxiom;
			return matchGroupAxiomNonUniquely(disjClassesAxiom.getClassExpressions(), pattern.getExpressionGroup());

		} else if (tBoxAxiom.isOfType(AxiomType.OBJECT_PROPERTY_RANGE) ||
				   tBoxAxiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN) ||
				   tBoxAxiom.isOfType(AxiomType.DATA_PROPERTY_DOMAIN) ||
				   tBoxAxiom.isOfType(AxiomType.DATA_PROPERTY_RANGE)) {

			return matchDataOrObjectDomainOrRange(tBoxAxiom, pattern);		
		}
		return false;		
	}


	// Currently we assume that the axiom only contains 2 individuals, both of which have been instantiated.
	private boolean matchDifferentIndividuals(OWLDifferentIndividualsAxiom diffIndividualsAxiom, OWLAxiomStr pattern) {
		
		List<GenericExpStr> patternExpressions = pattern.getExpressions();
		List<OWLIndividual> individuals = diffIndividualsAxiom.getIndividualsAsList();

		if (patternExpressions.size() != 2 || individuals.size() != 2) {
			return false; 
		}
		
		if (patternExpressions.get(0) instanceof TemplateLiteral && patternExpressions.get(1) instanceof TemplateLiteral) {
			
			TemplateLiteral i = (TemplateLiteral) patternExpressions.get(0);
			TemplateLiteral j = (TemplateLiteral) patternExpressions.get(1);
			
			// Check that both individuals have been instantiated and
			// attempt to match the two possible orderings.
			if (currentInstantiation.getVariableInstantiation().containsKey(i.getAtomic()) &&
				currentInstantiation.getVariableInstantiation().containsKey(j.getAtomic())) {
				
				return 	(matchPrimitive(individuals.get(0), i) && matchPrimitive(individuals.get(1), j)) ||
						(matchPrimitive(individuals.get(1), i) && matchPrimitive(individuals.get(0), j));			
			}			
		}
		return false;
	}
	
	private boolean matchABoxAxiom(OWLAxiom aBoxAxiom, OWLAxiomStr pattern) {

		// Currently only a "Different Individuals" axiom that has only two individuals is matched.
		if (aBoxAxiom.isOfType(AxiomType.DIFFERENT_INDIVIDUALS)) {
			OWLDifferentIndividualsAxiom diffIndividualsAxiom = (OWLDifferentIndividualsAxiom) aBoxAxiom;				
			return matchDifferentIndividuals(diffIndividualsAxiom, pattern);
		}	
		return false;
	}


	
	// Attempts to match an axiom which may potentially have multiple acceptable matchings.
	private boolean matchGroupAxiomNonUniquely(Set<OWLClassExpression> classExpressions, ExpressionGroup pattern) {

		// If the pattern only has an anonymous group, match the entire class expression set to it and return.
		if (pattern.hasAnonymousExpressions() && pattern.getNamedExpressions().length == 0) {
			return matchAnonymousGroupExpression(classExpressions, pattern.getAnonymousGroupName());
		}
		
		// Otherwise, generate all permutations of the given set of class expressions.
		List<Instantiation> newInstantiations = new ArrayList<Instantiation>();		
		PermutationGenerator<OWLClassExpression> permGen = new PermutationGenerator<OWLClassExpression>();
		List<List<OWLClassExpression>> allPermutations = permGen.generatePermutations(new ArrayList<OWLClassExpression> (classExpressions));
		Instantiation oldInstantiation = currentInstantiation;
		boolean atLeastOneMatch = false;

		// For every permutation, attempt to match it to the pattern.
		for (List<OWLClassExpression> permutation : allPermutations) {

			currentInstantiation = new Instantiation(oldInstantiation);

			// If the match is successful, add this new instantiation.
			if (matchOrderedGroup(permutation, pattern)) {
				newInstantiations.add(currentInstantiation);
				atLeastOneMatch= true;
			}				
		}

		// If there was at least one match, add all the new instantiations.
		if (atLeastOneMatch) {
			allInstantiations.addAll(newInstantiations);
		}
		
		// Setting currentInstantiation to null signifies that all instantiations have been added anyway.
		currentInstantiation = null;
		
		return atLeastOneMatch;
	}


	
	// Attempt to match two ordered groups.
	private boolean matchOrderedGroup(List<OWLClassExpression> clsExpList, ExpressionGroup pattern) {

		// Need to match all named expressions, hence given class expression list should be at least as large.	
		if (clsExpList.size() < pattern.getNamedExpressions().length) {
			return false;
		}

		for (GenericExpStr namedExpression : pattern.getNamedExpressions()) {

			OWLClassExpression currentClsExp = clsExpList.remove(0);

			if (!(namedExpression instanceof ClsExpStr)) {
				return false;
			}
			
			// Type-convert the named expression.
			ClsExpStr namedClsExpression = (ClsExpStr) namedExpression;
			
			if (!match(currentClsExp, namedClsExpression)) {
				return false;
			}
		}
		return !pattern.hasAnonymousExpressions() ||
			   matchAnonymousGroupExpression(new HashSet<OWLClassExpression>(clsExpList), pattern.getAnonymousGroupName());
	}


	
	
	// Matches an intersection or union expression, provided that there is a single
	// unique matching available.
	private boolean matchGroupExpressionUniquely(OWLClassExpression classExp, ClsExpStr pattern) {
		
		if (!(pattern instanceof InterUnion)) {
			return false;
		}
		
		InterUnion specialisedPattern = (InterUnion) pattern;
		ExpressionGroup groupPattern = specialisedPattern.getExpressionGroup();
		OWLNaryBooleanClassExpression groupExpression = (OWLNaryBooleanClassExpression) classExp;			
		
		// All named expressions in the pattern should be matched.
		// Hence there must be at least as many given class expressions.
		if (groupExpression.getOperands().size() < groupPattern.getNamedExpressions().length) {
			return false;
		}
		
		Set<OWLClassExpression> operands = groupExpression.getOperands();
		
		return matchNamedExpressions(operands, groupPattern.getNamedExpressions()) &&
				matchAnonymousGroupExpression(operands, groupPattern.getAnonymousGroupName());
	}
	
	
	// Method for matching an anonymous group against a given set of class expressions.
	private boolean matchAnonymousGroupExpression(Set<OWLClassExpression> classExpressions, String anonymousGroupName) {
		
		// If the group has been instantiated, check if sets are the same.
		if (currentInstantiation.getGroupInstantiation().containsKey(anonymousGroupName)) {
			return classExpressions.equals(currentInstantiation.getGroupInstantiation().get(anonymousGroupName));
		
		// Otherwise insert new instantiation.
		} else {
			currentInstantiation.getGroupInstantiation().put(anonymousGroupName, classExpressions);
			return true;
		}
	}
	
	
	// For the purposes of these rules, intersection and union only have atomic named expressions.
	private boolean matchNamedExpressions(Set<OWLClassExpression> classExpressions, ClsExpStr[] namedExpressions) {
		
		for (ClsExpStr expression : namedExpressions) {
			
			if (!(expression instanceof AtomicCls)) {
				return false;
			}
			
			AtomicCls atomicExpression = (AtomicCls) expression;
			String atomicExpressionName = atomicExpression.getPlaceholder();
			
			if (!currentInstantiation.getVariableInstantiation().containsKey(atomicExpressionName)) {
				return false;
			}
			
			OWLObject obj = currentInstantiation.getVariableInstantiation().get(atomicExpressionName);
			
			if (classExpressions.contains(obj)) {
				classExpressions.remove(obj);
			} else {
				return false;
			}		
		}
		return true;
	}
	

	

	// Important assumption:
	// Currently all class expression pattern matching is assumed to be producing at most one instantiation.
	private boolean match(OWLClassExpression classExp, ClsExpStr pattern) {

		if (pattern.getExpressionType() == null) {
			return matchAtomicCls(classExp, (AtomicCls) pattern);
		}

		ClassExpressionType classExpType = classExp.getClassExpressionType();

		if (classExpType.equals(pattern.getExpressionType())) {

			if (classExpType.equals(ClassExpressionType.OBJECT_INTERSECTION_OF) || 
				classExpType.equals(ClassExpressionType.OBJECT_UNION_OF)) {

				return matchGroupExpressionUniquely(classExp, pattern);
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)) {

				return matchComplementClassExpression((OWLObjectComplementOf) classExp, pattern);

			}  else if (classExpType.equals(ClassExpressionType.OBJECT_HAS_VALUE) ||
					    classExpType.equals(ClassExpressionType.DATA_HAS_VALUE)) {
				
				return matchHasValueExpression(classExp, pattern);
						
			} else if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) ||
					   classExpType.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM) ||
					   classExpType.equals(ClassExpressionType.DATA_SOME_VALUES_FROM)) {

				return matchQuantifiedClassExpression(classExp, pattern);
				
			} else if (classExpType.equals(ClassExpressionType.DATA_MIN_CARDINALITY) ||			
					   classExpType.equals(ClassExpressionType.DATA_MAX_CARDINALITY) ||
					   classExpType.equals(ClassExpressionType.DATA_EXACT_CARDINALITY) ||
					   classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)  ||
					   classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY) ||
					   classExpType.equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {

				return matchCardinalityExpression(classExp, pattern);
			} 
		}
		return false;
	}
	

	
	// Match object/data has value expression.
	private boolean matchHasValueExpression(OWLClassExpression classExp, ClsExpStr pattern) {
		
		if (!(pattern instanceof ExistsOrForAll)) {
			return false;
		}
		
		ExistsOrForAll specialisedPattern = (ExistsOrForAll) pattern;

		if (classExp instanceof OWLObjectHasValue && specialisedPattern.getExpression() instanceof TemplateIndividual) {
			
			OWLObjectHasValue objHasValue = (OWLObjectHasValue) classExp;
			TemplateIndividual individual = (TemplateIndividual) specialisedPattern.getExpression();
			
			return matchPrimitive(objHasValue.getProperty(), specialisedPattern.getProperty()) && 
				   matchPrimitive(objHasValue.getFiller(),individual);	

			
		} else if (classExp instanceof OWLDataHasValue && specialisedPattern.getExpression() instanceof TemplateLiteral) {
			
			OWLDataHasValue quantDataRest = (OWLDataHasValue) classExp;
			TemplateLiteral literal = (TemplateLiteral) specialisedPattern.getExpression();
			
			return matchPrimitive(quantDataRest.getProperty(), specialisedPattern.getProperty()) && 
				   matchPrimitive(quantDataRest.getFiller(), literal);
		}	
		return false;
	}
	
	
	// Match object/data some/all values from expressions.
	private boolean matchQuantifiedClassExpression(OWLClassExpression classExp, ClsExpStr pattern) {
		
		if (!(pattern instanceof ExistsOrForAll)) {
			return false;
		}
		
		ExistsOrForAll specialisedPattern = (ExistsOrForAll) pattern;
		
		if (classExp instanceof OWLQuantifiedObjectRestriction && specialisedPattern.getExpression() instanceof ClsExpStr)  {
			
			OWLQuantifiedObjectRestriction quantObjRestriction = (OWLQuantifiedObjectRestriction) classExp;
			ClsExpStr patternFiller = (ClsExpStr) specialisedPattern.getExpression();
			
			return matchPrimitive(quantObjRestriction.getProperty(), specialisedPattern.getProperty()) && 
				   match(quantObjRestriction.getFiller(), patternFiller);
	
			
		} else if (classExp instanceof OWLQuantifiedDataRestriction && specialisedPattern.getExpression() instanceof TemplateDataRange)  {
			
			OWLQuantifiedDataRestriction quantDataRestriction = (OWLQuantifiedDataRestriction) classExp;
			TemplateDataRange dataRangePrimitive = (TemplateDataRange) specialisedPattern.getExpression();

			return matchPrimitive(quantDataRestriction.getProperty(), specialisedPattern.getProperty()) && 
				   matchPrimitive(quantDataRestriction.getFiller(), dataRangePrimitive);
		}
		return false;
	}
	
	
	
	// Match object and data min/max/exact cardinality expressions.
	private boolean matchCardinalityExpression(OWLClassExpression classExp, ClsExpStr pattern) {
		
		if (!(pattern instanceof CardExpGen)) {
			return false;
		}
				
		CardExpGen specialisedPattern = (CardExpGen) pattern;

		// Currently we assume all data cardinality expressions contain a primitive data range.
		if (classExp instanceof OWLDataCardinalityRestriction && specialisedPattern.getExpression() instanceof TemplateDataRange) {

			OWLDataCardinalityRestriction cardRestExpression = (OWLDataCardinalityRestriction) classExp;							
			TemplateDataRange dataRangePrimitive = (TemplateDataRange) specialisedPattern.getExpression();
			
			return matchCardinality(cardRestExpression.getCardinality(), specialisedPattern.getCardinality()) && 
				   matchPrimitive(cardRestExpression.getProperty(), specialisedPattern.getProperty()) &&
				   matchPrimitive(cardRestExpression.getFiller(), dataRangePrimitive);

			
		// An object cardinality expression can contain more complex expressions.
		} else if (classExp instanceof OWLObjectCardinalityRestriction && specialisedPattern.getExpression() instanceof ClsExpStr) {
			
			OWLObjectCardinalityRestriction cardRestExpression = (OWLObjectCardinalityRestriction) classExp;
			ClsExpStr innerClassExpression = (ClsExpStr) specialisedPattern.getExpression();
			
			return  matchCardinality(cardRestExpression.getCardinality(), specialisedPattern.getCardinality()) && 
					matchPrimitive(cardRestExpression.getProperty(), specialisedPattern.getProperty()) && 
					match(cardRestExpression.getFiller(), innerClassExpression);
		}
		return false;
	}

	
	private boolean matchComplementClassExpression(OWLObjectComplementOf complementClass, ClsExpStr pattern) {

		if (!(pattern instanceof ComplementCls)) {
			return false;
		}
		
		ComplementCls complementClsPattern = (ComplementCls) pattern;	
		return match(complementClass.getOperand(), complementClsPattern.getSubExpression());
	}
	
	
	private boolean matchPrimitive(OWLObject entity, TemplatePrimitive pattern) {
		return addToMap(entity, pattern.getAtomic());
	}
	
	private boolean matchAtomicCls(OWLClassExpression clsExp, AtomicCls atomicCls) {
		return addToMap(clsExp, atomicCls.getPlaceholder());
	}
	
	private boolean matchCardinality(int cardinality, String pattern) {
				
		if (!currentInstantiation.getCardinalityInstantiation().keySet().contains(pattern)) {
			currentInstantiation.getCardinalityInstantiation().put(pattern, cardinality);
			return true;
		} else {
			return currentInstantiation.getCardinalityInstantiation().get(pattern).equals(cardinality);
		}
	}


	private boolean addToMap(OWLObject owlObj, String key) {

		if (!currentInstantiation.getVariableInstantiation().keySet().contains(key)) {
			currentInstantiation.getVariableInstantiation().put(key, owlObj);
			return true;			
		} else {
			return currentInstantiation.getVariableInstantiation().get(key).equals(owlObj);			
		}		
	}


	
	
	

	// Return all possible conclusions that can be generated.
	public List<OWLAxiom> generateConclusions(List<OWLAxiom> premises) {

		List<OWLAxiom> conclusions = new ArrayList<OWLAxiom>();

		// Attempt to match premises.
		if (matchPremises(premises)) {

			// Iterate over all possible instantiations and attempt to generate a conclusion from each one.
			for (Instantiation instantiation : allInstantiations) {
				currentInstantiation = instantiation;
				conclusions.addAll(generateConclusionsFromCurrentInstantiation());				
			}
		}

		return conclusions;
	}


	
	
	private OWLAxiom generateSubObjectPropertyAxiom() {
		
		
		return null;
	}
	
	// Given the current instantiation, generate all possible conclusions from it.
	// Assumption: given the way the rules are structured, there is no ambiguity in how the axioms
	// themselves are generated.
	private List<OWLAxiom> generateConclusionsFromCurrentInstantiation() {

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

	private boolean checkExpressionIsObjSomeOrAllValuesFrom(ExistsOrForAll existsOrForAll) {
		
		OWLObject generatedProperty = generate(existsOrForAll.getProperty());
		
		if (!(generatedProperty instanceof OWLObjectPropertyExpression) || 
			!(existsOrForAll.getExpression() instanceof ClsExpStr) || generatedProperty == null) {
			
			return false;
		}
		
		ClsExpStr expressionStr = (ClsExpStr) existsOrForAll.getExpression();
		List<OWLObject> generatedExps = generate(expressionStr);
		
		// We assume there can be at most a single unique generated value.
		if (generatedExps == null || !(generatedExps.size() == 1 && generatedExps.get(0) instanceof OWLClassExpression)) {
			return false;
		}

		return true;
	}
	

	
	private OWLObjectSomeValuesFrom generateObjSomeValuesFrom(ExistsOrForAll existsOrForAll) {
				
		if (!checkExpressionIsObjSomeOrAllValuesFrom(existsOrForAll)) {
			return null;
		}
		
		OWLObjectPropertyExpression generatedProperty = (OWLObjectPropertyExpression) generate(existsOrForAll.getProperty());
		ClsExpStr expressionStr = (ClsExpStr) existsOrForAll.getExpression();
		OWLClassExpression generatedClsExp = (OWLClassExpression) generate(expressionStr).get(0);

		return new OWLObjectSomeValuesFromImpl(generatedProperty, generatedClsExp);
	}
	
	
	private OWLObjectAllValuesFrom generateObjAllValuesFrom(ExistsOrForAll existsOrForAll) {
		
		if (!checkExpressionIsObjSomeOrAllValuesFrom(existsOrForAll)) {
			return null;
		}
		
		OWLObjectPropertyExpression generatedProperty = (OWLObjectPropertyExpression) generate(existsOrForAll.getProperty());
		ClsExpStr expressionStr = (ClsExpStr) existsOrForAll.getExpression();
		OWLClassExpression generatedClsExp = (OWLClassExpression) generate(expressionStr).get(0);
	
		return new OWLObjectAllValuesFromImpl(generatedProperty, generatedClsExp);
	}
	
	
	private boolean checkExpressionIsObjCardinality(CardExpGen cardinalityExpression) {

		if (!(cardinalityExpression.getExpression() instanceof ClsExpStr)) {
			return false;
		}
		
		// Check whether the cardinality value is instantiated.
		try {
			generateCardinality(cardinalityExpression.getCardinality());
		} catch (UninstantiatedCardinalityException e) {
			return false;
		}
		
		OWLObject objPropExp =  generate(cardinalityExpression.getProperty());
		List<OWLObject> classExpressions =  generate((ClsExpStr) cardinalityExpression.getExpression());			
		
		if (!(objPropExp instanceof OWLObjectPropertyExpression) || classExpressions.size() != 1 ||
			!(classExpressions.get(0) instanceof OWLClassExpression)) {
			
			return false;
		}		
		return true;	
	}
	
		
	private OWLObjectMaxCardinality generateObjMaxCardinality(CardExpGen cardinalityExpression) {
		
		if (!checkExpressionIsObjCardinality(cardinalityExpression)) {
			return null;
		}
		
		int cardinality;
		
		try {
			cardinality = generateCardinality(cardinalityExpression.getCardinality());
		} catch (UninstantiatedCardinalityException e) {
			e.printStackTrace();
			return null;
		}
		
		OWLObjectPropertyExpression objPropExp =  (OWLObjectPropertyExpression) generate(cardinalityExpression.getProperty());
		OWLClassExpression classExp =  (OWLClassExpression) generate((ClsExpStr) cardinalityExpression.getExpression()).get(0);			
		
		return new OWLObjectMaxCardinalityImpl(objPropExp, cardinality, classExp );
	}
	
	
	private OWLObjectExactCardinality generateObjExactCardinality(CardExpGen cardinalityExpression) {
		
		OWLObjectCardinalityRestriction genericCardinalityExpr = generateObjMaxCardinality(cardinalityExpression);	
		return new OWLObjectExactCardinalityImpl(genericCardinalityExpr.getProperty(), genericCardinalityExpr.getCardinality(), genericCardinalityExpr.getFiller());
	}
	
	
	private OWLObjectMinCardinality generateObjMinCardinality(CardExpGen cardinalityExpression) {
		
		OWLObjectCardinalityRestriction genericCardinalityExpr = generateObjMaxCardinality(cardinalityExpression);	
		return new OWLObjectMinCardinalityImpl(genericCardinalityExpr.getProperty(), genericCardinalityExpr.getCardinality(), genericCardinalityExpr.getFiller());
	}
	
	
	
	// All types of generated expressions are unique, except for
	// the intersection and union types, where multiple conclusions may be generated.
	private List<OWLObject> generate(ClsExpStr conclusionExp) {

		List<OWLObject> generatedExpressions = new ArrayList<OWLObject>();
		OWLObject generatedExpression = null;

		if (conclusionExp.getExpressionType() == null) {
			generatedExpression = currentInstantiation.getVariableInstantiation().get(((AtomicCls) conclusionExp).getPlaceholder());
			generatedExpressions.add(generatedExpression);
			return generatedExpressions;

		} else {

			ClassExpressionType classExpType = conclusionExp.getExpressionType();

			if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
				generatedExpression = generateObjSomeValuesFrom((ExistsOrForAll) conclusionExp);
							
			} else if(classExpType.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {
				generatedExpression = generateObjAllValuesFrom((ExistsOrForAll) conclusionExp);
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)) {
				generatedExpression = generateObjMinCardinality((CardExpGen) conclusionExp);
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {
				generatedExpression = generateObjExactCardinality((CardExpGen) conclusionExp);
				
			}else if (classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY)) {
				generatedExpression = generateObjMaxCardinality((CardExpGen) conclusionExp);
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_UNION_OF)) {
				generatedExpressions.addAll(generateUnionExpressions((InterUnion) conclusionExp));				
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
				generatedExpressions.addAll(generateIntersectionExpressions((InterUnion) conclusionExp));
			}
		}
		
		if (generatedExpression != null) {
			generatedExpressions.add(generatedExpression);
		}

		return generatedExpressions;
	}

	
	private List<OWLObjectUnionOf> generateUnionExpressions(InterUnion interUnion) {
		
		Set<Set<OWLClassExpression>> allGroups = generateGroup(interUnion);
		List<OWLObjectUnionOf> allUnionExpressions = new ArrayList<OWLObjectUnionOf>();
 		
		for (Set<OWLClassExpression> group : allGroups) {
				OWLObjectUnionOfImpl union = new OWLObjectUnionOfImpl(group);
				allUnionExpressions.add(union);			
		}
		return allUnionExpressions;
	}
	
	
	private List<OWLObjectIntersectionOf> generateIntersectionExpressions(InterUnion interUnion) {
		
		Set<Set<OWLClassExpression>> allGroups = generateGroup(interUnion);
		List<OWLObjectIntersectionOf> allUnionExpressions = new ArrayList<OWLObjectIntersectionOf>();
 		
		for (Set<OWLClassExpression> group : allGroups) {
			OWLObjectIntersectionOf union = new OWLObjectIntersectionOfImpl(group);
			allUnionExpressions.add(union);			
		}
		return allUnionExpressions;
	}
	
	
	// Given the structure of the rules, here we assume that
	// we are given a group that contains 0 or more named classes, all of which
	// must be instantiated, and at most 1 anonymous group.
	// If the group is uninstantiated, then we attempt to generate it based on subset restrictions.
	// If this is impossible, then we return nothing.
	private Set<Set<OWLClassExpression>> generateGroup(InterUnion interUnion) {
		
		Set<Set<OWLClassExpression>> allGroups = new HashSet<Set<OWLClassExpression>>();
		Set<OWLClassExpression> generatedNamedExpressions = generateNamedExpressions(interUnion);

		if (generatedNamedExpressions == null) {
			return allGroups;
		}
		
		if (interUnion.getExpressionGroup().hasAnonymousExpressions()) {
			allGroups = generateAnonymousExpression(interUnion, generatedNamedExpressions);		
		} else {
			allGroups.add(generatedNamedExpressions);
		}	
		return allGroups;
	}
	

	private Set<Set<OWLClassExpression>> generateAnonymousExpression(InterUnion interUnion, Set<OWLClassExpression> generatedNamedExpressions) {

		ExpressionGroup expGroup = interUnion.getExpressionGroup();
		String anonGroupName = expGroup.getAnonymousGroupName();
		Set<Set<OWLClassExpression>> possibleInstantiations = new HashSet<Set<OWLClassExpression>>();
			
		// If the group is instantiated, retrieve its value.
		if (currentInstantiation.getGroupInstantiation().containsKey(anonGroupName)) {			
			possibleInstantiations.add(currentInstantiation.getGroupInstantiation().get(anonGroupName));			
		
		// If it is not, attempt to generate it using subset restrictions.
		} else {

			Set<OWLClassExpression> superSet = findInstantiatedSuperset(expGroup);
			
			// We expect there to be exactly one superset, so we return a null otherwise.
			if (superSet == null) {
				return null;
			}
			
			PermutationGenerator<OWLClassExpression> permGen = new PermutationGenerator<OWLClassExpression>();
			possibleInstantiations = permGen.generateStrictNonEmptyPowerSet(superSet);
			
			// Add the named expression instantiations to every instantiation.
			for (Set<OWLClassExpression> instantiation : possibleInstantiations) {
				instantiation.addAll(generatedNamedExpressions);
			}		
		}
		return possibleInstantiations;
	}

	

	// Given an anonymous group, attempt to find a subset restriction in which it is a subset,
	// and it has an instantiated superset.
	// Assumption: assume there is at most one such restriction.
	private Set<OWLClassExpression> findInstantiatedSuperset(ExpressionGroup expGroup) {
		
		String anonGroupName = expGroup.getAnonymousGroupName();
		String superSetName = "";
		Set<OWLClassExpression> superSet = null;
		
		for (RuleRestriction restriction : ruleRestrictions) {
			if (restriction instanceof SubSetRestriction) {

				SubSetRestriction subSetRest = (SubSetRestriction) restriction;

				if (subSetRest.getSubClass().equals(anonGroupName)) {

					superSetName = subSetRest.getSuperClass();
					
					if (currentInstantiation.getGroupInstantiation().containsKey(superSetName)) {
						
						// We expect there to be only one superset. Hence if we find more than one,
						// we return a null value to signify an error.
						if (superSet != null) {
							return null;
						} else {
							superSet = currentInstantiation.getGroupInstantiation().get(superSetName);
						}
					}
				}					
			}						
		}
		return superSet;
	}
	
	
	
	// We assume that every named expression is generated uniquely.
	private Set<OWLClassExpression> generateNamedExpressions(InterUnion interUnion) {

		ExpressionGroup expGroup = interUnion.getExpressionGroup();
		ClsExpStr[] namedExpressions = expGroup.getNamedExpressions();
		Set<OWLClassExpression> generatedGroup = new HashSet<OWLClassExpression>();
		
		for (ClsExpStr namedExpression : namedExpressions) {
			
			List<OWLObject> generatedClasses = generate(namedExpression);
			
			if (generatedClasses.size() != 1 || !(generatedClasses.get(0) instanceof OWLClassExpression)) {
				return null;
			}
			
			OWLClassExpression generatedClassExpression = (OWLClassExpression) generatedClasses.get(0);
			generatedGroup.add(generatedClassExpression);
		}	
		return generatedGroup;
	}
	
	

	private OWLObject generate(TemplatePrimitive conclusionExp) {
		return currentInstantiation.getVariableInstantiation().get(conclusionExp.getAtomic());
	}
	
	private int generateCardinality(String pattern) throws UninstantiatedCardinalityException {
		if (currentInstantiation.getCardinalityInstantiation().containsKey(pattern)) {
			return currentInstantiation.getCardinalityInstantiation().get(pattern);
		} else {
			
			// Need to decide on how to generate non-defined cardinalities
			throw new UninstantiatedCardinalityException();
		}
	}


	private Set<OWLClassExpression> generateGroup(ExpressionGroup expGroupStr) {

		Set<OWLClassExpression> expGroup = new HashSet<OWLClassExpression>();

		for (GenericExpStr namedExpression : expGroupStr.getNamedExpressions()) {		
			expGroup.add((OWLClassExpression) currentInstantiation.getVariableInstantiation().get(((AtomicCls) namedExpression).getPlaceholder()));
		}

		return expGroup;
	}
}
