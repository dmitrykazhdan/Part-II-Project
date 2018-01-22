package InferenceGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

import InferenceRules.Instantiation;
import InferenceRules.PermutationGenerator;
import OWLExpressionTemplates.AtomicCls;
import OWLExpressionTemplates.CardExpGen;
import OWLExpressionTemplates.ClsExpStr;
import OWLExpressionTemplates.ExistsOrForAll;
import OWLExpressionTemplates.ExpressionGroup;
import OWLExpressionTemplates.GenericExpStr;
import OWLExpressionTemplates.InterUnion;
import OWLExpressionTemplates.OWLAxiomStr;
import OWLExpressionTemplates.TemplatePrimitive;
import OWLExpressionTemplates.UninstantiatedCardinalityException;
import RuleRestrictions.RuleRestriction;
import RuleRestrictions.SubSetRestriction;
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

public class ConclusionGenerator extends RuleMatcherGenerator{

	
	private OWLAxiomStr conclusionStr;
	private Instantiation currentInstantiation;
	private List<Instantiation> allInstantiations;
	

	public ConclusionGenerator(List<OWLAxiom> expressions, List<OWLAxiomStr> expressionStr, OWLAxiomStr conclusionStr, RuleRestriction[] ruleRestrictions) {		
		super(expressions, expressionStr, ruleRestrictions);
		this.conclusionStr = conclusionStr;
	}
	
	
	
	// Return all possible conclusions that can be generated.
	public List<OWLAxiom> generateConclusions() {

		// Attempt to match premises.
		PremiseMatcher matcher = new PremiseMatcher(this.expressions, this.expressionStr, this.ruleRestrictions);
		allInstantiations = matcher.getAllMatchedInstantiations();
		List<OWLAxiom> conclusions = new ArrayList<OWLAxiom>();

		if (allInstantiations == null) {
			return conclusions;
		}
		
		// Iterate over all possible instantiations and attempt to generate a conclusion from each one.
		for (Instantiation instantiation : allInstantiations) {
			currentInstantiation = instantiation;
			conclusions.addAll(generateConclusionFromCurrentInstantiation());	
		}
		return conclusions;
	}


	
	
	private OWLSubObjectPropertyOfAxiom generateSubObjectPropertyAxiom() {
		
		OWLObject subProperty = generate((TemplatePrimitive) conclusionStr.getExpressions().get(0));
		OWLObject superProperty = generate((TemplatePrimitive) conclusionStr.getExpressions().get(1));
		
		if (subProperty instanceof OWLObjectProperty && superProperty instanceof OWLObjectProperty) {
			return new OWLSubObjectPropertyOfAxiomImpl((OWLObjectProperty) subProperty, (OWLObjectProperty) superProperty, new ArrayList<OWLAnnotation>());
		}
		return null;
	}
	
	
	// Important assumption: given the way the rules are structured, we can assume that
	// the subclass and superclass instantiations are independent, hence can simply return all possible
	// pairings between the two.
	private List<OWLSubClassOfAxiom> generateSubClassOfAxioms() {
		
		List<OWLSubClassOfAxiom> subClassAxioms = new ArrayList<OWLSubClassOfAxiom>();
		List<OWLObject> subClsList = generate((ClsExpStr) conclusionStr.getExpressions().get(0));
		List<OWLObject> superClsList = generate((ClsExpStr) conclusionStr.getExpressions().get(1));
		
		for (OWLObject subCls : subClsList) {
			for (OWLObject  superCls : superClsList) {
				
				if (subCls instanceof OWLClassExpression && superCls instanceof OWLClassExpression) {		
					subClassAxioms.add(new OWLSubClassOfAxiomImpl((OWLClassExpression) subCls, (OWLClassExpression) superCls, new ArrayList<OWLAnnotation>()));
				} else {
					return new ArrayList<OWLSubClassOfAxiom>();
				}			
			}
		}
		return subClassAxioms;
	}
	
	
	private OWLTransitiveObjectPropertyAxiom generateTransitiveProperty() {
		OWLObject transProperty = generate((TemplatePrimitive) conclusionStr.getExpressions().get(0));
		
		if (transProperty instanceof OWLObjectProperty) {
			return new OWLTransitiveObjectPropertyAxiomImpl((OWLObjectProperty) transProperty, new ArrayList<OWLAnnotation>());
		}
		return null;
	}
	

	// Given the current instantiation, generate all possible conclusions from it.
	// Assumption: given the way the rules are structured, there is no ambiguity in how the axioms
	// themselves are generated.
	private List<OWLAxiom> generateConclusionFromCurrentInstantiation() {

		OWLAxiom conclusionAxiom = null;
		List<OWLAxiom> conclusions = new ArrayList<OWLAxiom>();
		AxiomType<?>  conclusionType = conclusionStr.getConstructor();

		if (conclusionType.equals(AxiomType.SUBCLASS_OF)) {
			conclusions.addAll(generateSubClassOfAxioms());	

		} else if (conclusionType.equals(AxiomType.SUB_OBJECT_PROPERTY)) {
			conclusionAxiom = generateSubObjectPropertyAxiom();
			
		} else if (conclusionType.equals(AxiomType.TRANSITIVE_OBJECT_PROPERTY)) {
			conclusionAxiom = generateTransitiveProperty();

		} else if (conclusionType.equals(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
			conclusions.addAll(generateObjectPropertyDomainAxioms());

		} else if (conclusionType.equals(AxiomType.OBJECT_PROPERTY_RANGE)) {
			conclusions.addAll(generateObjectPropertyRangeAxioms());
		
		} else if (conclusionType.equals(AxiomType.DISJOINT_CLASSES)) {
			conclusionAxiom =  generateDisjointClassesAxiom();
		}
		
		if (conclusionAxiom != null) {
			conclusions.add(conclusionAxiom);
		}
		return 	conclusions;
	}
	
	
	private List<OWLObjectPropertyDomainAxiom> generateObjectPropertyDomainAxioms() {
		
		List<OWLObjectPropertyDomainAxiom> objectPropertyDomainAxioms = new ArrayList<OWLObjectPropertyDomainAxiom>();
		OWLObject property = generate((TemplatePrimitive) conclusionStr.getExpressions().get(0));
		List<OWLObject> fillerExpressions = generate((ClsExpStr) conclusionStr.getExpressions().get(1));
		
		if (!(property instanceof OWLObjectProperty)) {
			return objectPropertyDomainAxioms;
		}
		
		for (OWLObject filler : fillerExpressions) {
			if (filler instanceof OWLClassExpression) {
				objectPropertyDomainAxioms.add(new OWLObjectPropertyDomainAxiomImpl((OWLObjectProperty) property, (OWLClassExpression) filler, new HashSet<OWLAnnotation>()));
			} else {
				return new ArrayList<OWLObjectPropertyDomainAxiom>();
			}
		}		
		return objectPropertyDomainAxioms;
	}
	
	
	
	private List<OWLObjectPropertyRangeAxiom> generateObjectPropertyRangeAxioms() {
		
		List<OWLObjectPropertyRangeAxiom> objectPropertyRangeAxioms = new ArrayList<OWLObjectPropertyRangeAxiom>();
		OWLObject property = generate((TemplatePrimitive) conclusionStr.getExpressions().get(0));
		List<OWLObject> fillerExpressions = generate((ClsExpStr) conclusionStr.getExpressions().get(1));
		
		if (!(property instanceof OWLObjectProperty)) {
			return objectPropertyRangeAxioms;
		}
		
		for (OWLObject filler : fillerExpressions) {
			if (filler instanceof OWLClassExpression) {
				objectPropertyRangeAxioms.add(new OWLObjectPropertyRangeAxiomImpl((OWLObjectProperty) property, (OWLClassExpression) filler, new HashSet<OWLAnnotation>()));
			} else {
				return new ArrayList<OWLObjectPropertyRangeAxiom>();
			}
		}		
		return objectPropertyRangeAxioms;
	}
	
	
	// Assumption: all free variables in the template have been instantiated.
	private OWLDisjointClassesAxiom generateDisjointClassesAxiom() {
		return new OWLDisjointClassesAxiomImpl(generateGroup(conclusionStr.getExpressionGroup()), new HashSet<OWLAnnotation>());			
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
	

}