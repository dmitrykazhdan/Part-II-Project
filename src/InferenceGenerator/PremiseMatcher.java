package InferenceGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.HasProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
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
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLQuantifiedDataRestriction;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import InferenceRules.Instantiation;
import InferenceRules.PermutationGenerator;
import OWLExpressionTemplates.AtomicCls;
import OWLExpressionTemplates.CardExpGen;
import OWLExpressionTemplates.ClsExpStr;
import OWLExpressionTemplates.ComplementCls;
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
import OWLExpressionTemplates.TemplatePrimitive;
import RuleRestrictions.RestrictionChecker;
import RuleRestrictions.RuleRestriction;
import RuleRestrictions.RuleRestrictions;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class PremiseMatcher extends RuleMatcherGenerator{

	// Rule restrictions to check.
	private RuleRestriction[] ruleRestrictions;
	private Instantiation currentInstantiation;
	private List<Instantiation> allInstantiations;
	

	public PremiseMatcher(List<OWLAxiom> expressions, List<OWLAxiomStr> expressionStr, RuleRestriction[] ruleRestrictions) {		
		super(expressions, expressionStr);
		this.ruleRestrictions = ruleRestrictions;
	}
	
	
	public List<Instantiation> getAllMatchedInstantiations() {

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
		
		return allInstantiations;
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
	
	
	
	private void cleanupInstantiations() {
		allInstantiations = checkInstantiationRestrictions(allInstantiations, ruleRestrictions);
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
}
