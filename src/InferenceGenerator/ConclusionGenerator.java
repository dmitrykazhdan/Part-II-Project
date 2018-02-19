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
import org.semanticweb.owlapi.model.OWLIndividual;
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
import OWLExpressionTemplates.SubClassStr;
import OWLExpressionTemplates.TemplatePrimitive;
import OWLExpressionTemplates.UninstantiatedCardinalityException;
import RuleRestrictions.AbsCardinalityRestriction;
import RuleRestrictions.CardinalityRestriction;
import RuleRestrictions.CardinalitySign;
import RuleRestrictions.GroupContainsRestriction;
import RuleRestrictions.RelCardinalityRestriction;
import RuleRestrictions.RuleRestriction;
import RuleRestrictions.RuleRestrictions;
import RuleRestrictions.SubSetRestriction;
import uk.ac.manchester.cs.owl.owlapi.OWLDisjointClassesAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectAllValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectExactCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectHasValueImpl;
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

	protected OWLAxiomStr conclusionStr;
	private Instantiation currentInstantiation;
	private List<Instantiation> allInstantiations;
	private RuleRestrictions ruleRestrictions;

	public ConclusionGenerator(List<OWLAxiom> premises, List<OWLAxiomStr> premisesStr, OWLAxiomStr conclusionStr, RuleRestrictions ruleRestrictions) {		
		super(premises, premisesStr);
		this.ruleRestrictions =  ruleRestrictions;
		this.conclusionStr = conclusionStr;
	}
		
	
	// Return all possible conclusions that can be generated.
	public List<OWLAxiom> generateConclusions() {

		// Attempt to match premises.
		PremiseMatcher matcher = new PremiseMatcher(this.expressions, this.expressionStr, ruleRestrictions.getPremiseRestrictions());
		allInstantiations = matcher.getAllMatchedInstantiations();
		List<OWLAxiom> conclusions = new ArrayList<OWLAxiom>();

		if (allInstantiations == null) {
			return conclusions;
		}
		
		// Iterate over all possible instantiations and attempt to generate a conclusion from each one.
		for (Instantiation instantiation : allInstantiations) {
			currentInstantiation = instantiation;
			List<OWLAxiom> newGeneratedConclusions = generateConclusionsFromCurrentInstantiation();		
			conclusions.addAll(newGeneratedConclusions);	
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
		
		if (!(conclusionStr instanceof SubClassStr)) {
			return subClassAxioms;
		}
		
		SubClassStr subClassStr = (SubClassStr) conclusionStr;
		
		List<OWLObject> subClsList = generate(subClassStr.getSubClassStr());
		List<OWLObject> superClsList = generate(subClassStr.getSuperClassStr());
		
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
	private List<OWLAxiom> generateConclusionsFromCurrentInstantiation() {

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
		return new OWLDisjointClassesAxiomImpl(generateFullyNamedGroup(conclusionStr.getExpressionGroup()), new HashSet<OWLAnnotation>());			
	}

	private boolean checkExpressionIsObjSomeOrAllValuesFrom(ExistsOrForAll existsOrForAll) {
		
		OWLObject generatedProperty = generate(existsOrForAll.getProperty());
		
		if (generatedProperty == null ||
			!(generatedProperty instanceof OWLObjectPropertyExpression) || 
			!(existsOrForAll.getExpression() instanceof ClsExpStr)) {
			
			return false;
		}
		
		ClsExpStr expressionStr = (ClsExpStr) existsOrForAll.getExpression();
		List<OWLObject> generatedExps = generate(expressionStr);
		
		if (generatedExps == null) {
			return false;
		}
		return true;
	}
	

	
	
	private List<OWLObject> generateObjSomeOrAllValuesFrom(ExistsOrForAll existsOrForAll, boolean someValFrom) {
		
		if (!checkExpressionIsObjSomeOrAllValuesFrom(existsOrForAll)) {
			return null;
		}
		
		OWLObjectPropertyExpression generatedProperty = (OWLObjectPropertyExpression) generate(existsOrForAll.getProperty());
		ClsExpStr expressionStr = (ClsExpStr) existsOrForAll.getExpression();
		List<OWLObject> generatedClsExpressions =  generate(expressionStr);
		List<OWLObject> quantifiedExpressions = new ArrayList<OWLObject>();
		
		for (OWLObject expression : generatedClsExpressions) {
			if (!(expression instanceof OWLClassExpression)) {
				return new ArrayList<OWLObject>();
			} else {
				
				if (someValFrom) {
					quantifiedExpressions.add(new OWLObjectSomeValuesFromImpl(generatedProperty, (OWLClassExpression) expression));
				} else {
					quantifiedExpressions.add(new OWLObjectAllValuesFromImpl(generatedProperty, (OWLClassExpression) expression));
				}
			}
		}		
		return quantifiedExpressions;
	}
	
	
	private List<OWLObject> generateObjSomeValuesFrom(ExistsOrForAll existsOrForAll) {
		return generateObjSomeOrAllValuesFrom(existsOrForAll, true);
	}
	
	private List<OWLObject> generateObjAllValuesFrom(ExistsOrForAll existsOrForAll) {
		return generateObjSomeOrAllValuesFrom(existsOrForAll, false);
	}
	
	
	private boolean checkExpressionIsObjCardinality(CardExpGen cardinalityExpression) {

		if (!(cardinalityExpression.getExpression() instanceof ClsExpStr)) {
			return false;
		}
		
		// Check whether the cardinality value is instantiated.
		try {
			generateCardinalities(cardinalityExpression.getCardinality());
		} catch (UninstantiatedCardinalityException e) {
			return false;
		}
		
		OWLObject objPropExp =  generate(cardinalityExpression.getProperty());
		List<OWLObject> classExpressions =  generate((ClsExpStr) cardinalityExpression.getExpression());			
		
		// For now assume that the class expression in a cardinality expression is always unique.
		// Potentially can drop this assumption, if required.
		if (!(objPropExp instanceof OWLObjectPropertyExpression) || classExpressions.size() != 1 ||
				!(classExpressions.get(0) instanceof OWLClassExpression)) {		
			return false;
		}		
		return true;	
	}
	
		
	private List<OWLObjectMaxCardinality> generateObjMaxCardinality(CardExpGen cardinalityExpression) {
		
		if (!checkExpressionIsObjCardinality(cardinalityExpression)) {
			return null;
		}
		
		List<OWLObjectMaxCardinality> cardinalityExpressions = new ArrayList<OWLObjectMaxCardinality>();
		List<Integer> cardinalities;
		
		try {
			cardinalities = generateCardinalities(cardinalityExpression.getCardinality());
		} catch (UninstantiatedCardinalityException e) {
			e.printStackTrace();
			return null;
		}
		
		OWLObjectPropertyExpression objPropExp =  (OWLObjectPropertyExpression) generate(cardinalityExpression.getProperty());
		OWLClassExpression classExp =  (OWLClassExpression) generate((ClsExpStr) cardinalityExpression.getExpression()).get(0);			
		
		for (Integer cardinality : cardinalities) {
			cardinalityExpressions.add(new OWLObjectMaxCardinalityImpl(objPropExp, cardinality, classExp));
		}		
		return cardinalityExpressions;
	}
	
	
	private List<OWLObjectExactCardinality> generateObjExactCardinality(CardExpGen cardinalityExpression) {
		
		List<OWLObjectMaxCardinality> maxCardinalityExpressions = generateObjMaxCardinality(cardinalityExpression);	
		List<OWLObjectExactCardinality> exactCardinalityExpressions = new ArrayList<OWLObjectExactCardinality>();

		if (maxCardinalityExpressions == null) {
			return exactCardinalityExpressions;
		}
		
		for (OWLObjectMaxCardinality maxCardExp : maxCardinalityExpressions) {
			exactCardinalityExpressions.add(new OWLObjectExactCardinalityImpl(maxCardExp.getProperty(), maxCardExp.getCardinality(), maxCardExp.getFiller()));
		}	
		return exactCardinalityExpressions;
	}
	
	
	private List<OWLObjectMinCardinality> generateObjMinCardinality(CardExpGen cardinalityExpression) {
		
		List<OWLObjectMaxCardinality> maxCardinalityExpressions = generateObjMaxCardinality(cardinalityExpression);	
		List<OWLObjectMinCardinality> minCardinalityExpressions = new ArrayList<OWLObjectMinCardinality>();

		if (maxCardinalityExpressions == null) {
			return minCardinalityExpressions;
		}
		
		for (OWLObjectMaxCardinality maxCardExp : maxCardinalityExpressions) {
			minCardinalityExpressions.add(new OWLObjectMinCardinalityImpl(maxCardExp.getProperty(), maxCardExp.getCardinality(), maxCardExp.getFiller()));
		}	
		return minCardinalityExpressions;	
	}

	
	
	private OWLObject generate(TemplatePrimitive conclusionExp) {
		return currentInstantiation.getVariableInstantiation().get(conclusionExp.getAtomic());
	}
	
	// If the cardinality is instantiated, return it.
	// Otherwise attempt to find its upper and lower bound, in which case generate all cardinalities in the range
	// [LB, min(LB + 5), UB].
	// If bounds don't exist- throw exception.
	private List<Integer> generateCardinalities(String pattern) throws UninstantiatedCardinalityException {
		
		List<Integer> cardinalities = new ArrayList<Integer>();
		
		if (currentInstantiation.getCardinalityInstantiation().containsKey(pattern)) {
			cardinalities.add(currentInstantiation.getCardinalityInstantiation().get(pattern));
			return cardinalities;
			
		} else {	
			
			List<Integer> cardinalityBounds = findCardinalityBounds(pattern);
			
			if (cardinalityBounds == null || cardinalityBounds.size() != 2) {
				throw new UninstantiatedCardinalityException();
			}			
			return generateCardinalitiesInRange(cardinalityBounds.get(0), cardinalityBounds.get(1));			
		}
	}
	
	
	// Given [LB, UB] generate all integers in the range [LB, min(UB, LB + 5)]
	private List<Integer> generateCardinalitiesInRange(int lowerBound, int upperBound) {
		
		List<Integer> cardinalities = new ArrayList<Integer>();
		
		if ((lowerBound > upperBound) || (lowerBound < 0)) {
			return cardinalities;
		}	
		upperBound = Math.min(upperBound, lowerBound + 5);
		
		for (int i = lowerBound; i <= upperBound; i++) {
			cardinalities.add(i);
		}	
		return cardinalities;
	}
	
	// This assumes that the cardinality of interest has been defined as a "cardinality", not as
	// a relative bound. Thus the rules will have to be defined appropriately.
	private List<Integer> findCardinalityBounds(String cardinality) {
		
		List<Integer> upperAndLowerBounds = new ArrayList<Integer>();
		upperAndLowerBounds.add(Integer.MIN_VALUE);
		upperAndLowerBounds.add(Integer.MAX_VALUE);
		Integer bound = -1;
		CardinalitySign type;
		
		for (RuleRestriction restriction : ruleRestrictions.conclusionRestrictions()){
			if (restriction instanceof CardinalityRestriction &&
				((CardinalityRestriction) restriction).getCardinality().equals(cardinality)) {
				
				if (restriction instanceof AbsCardinalityRestriction) {
					bound = ((AbsCardinalityRestriction) restriction).getAbsoluteBound();
			
				} else if (restriction instanceof RelCardinalityRestriction) {
					String relBound = ((RelCardinalityRestriction) restriction).getRelativeBound();
					
					if (currentInstantiation.getCardinalityInstantiation().containsKey(relBound)) {
						bound = currentInstantiation.getCardinalityInstantiation().get(relBound);					
					} else {
						continue;
					}				
				}				
				type = ((CardinalityRestriction) restriction).getCardinalityType();
				updateBounds(upperAndLowerBounds, bound, type);
			}
		}
		
		if (upperAndLowerBounds.get(0) == Integer.MIN_VALUE || upperAndLowerBounds.get(1) == Integer.MAX_VALUE) {
			return null;
		} else {
			return upperAndLowerBounds;
		}
	}

	
	private void updateBounds(List<Integer> currentBounds, Integer newBound, CardinalitySign boundType) {
		Integer lowerBound = currentBounds.get(0);
		Integer upperBound = currentBounds.get(1);

		switch(boundType) {
		case L:
			upperBound = Math.min(newBound - 1, upperBound);
			break;
		case LEQ:
			upperBound = Math.min(newBound, upperBound);
			break;
		case G:
			lowerBound = Math.max(newBound + 1, lowerBound);
			break;
		case GEQ:
			lowerBound = Math.max(newBound, lowerBound);
			break;
		default:
		}	
		currentBounds.set(0, lowerBound);
		currentBounds.set(1, upperBound);		
	}
	

	private Set<OWLClassExpression> generateFullyNamedGroup(ExpressionGroup expGroupStr) {

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
		
		for (RuleRestriction restriction : ruleRestrictions.conclusionRestrictions()) {
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
	
	
	// If the template has been instantiated, that template is returned.
	// Otherwise the "group contains restriction" is used to generate the different possible classes.
	private List<OWLObject> generateAtomicCls(AtomicCls atomicCls) {
		
		List<OWLObject> generatedExpressions = new ArrayList<OWLObject>();
		
		if (currentInstantiation.getVariableInstantiation().containsKey(atomicCls.getPlaceholder())) {
			generatedExpressions.add(currentInstantiation.getVariableInstantiation().get(atomicCls.getPlaceholder()));
		} else {
			generatedExpressions.addAll(getContainedClasses(atomicCls));
		}
		return generatedExpressions;
	}
	
	
	private List<OWLObject> getContainedClasses(AtomicCls atomicCls) {
		
		List<OWLObject> allClasses = new ArrayList<OWLObject>();
		
		for (RuleRestriction restriction : ruleRestrictions.conclusionRestrictions()){
			if (restriction instanceof GroupContainsRestriction) {

				GroupContainsRestriction groupContainsRestriction = (GroupContainsRestriction) restriction;
				String anonGroupName = groupContainsRestriction.getAnonymousGroupName();
				
				if (!groupContainsRestriction.getAtomicClsName().equals(atomicCls.getPlaceholder())) {
					continue;
				}
								
				if (currentInstantiation.getGroupInstantiation().containsKey(anonGroupName)) {
					Set<OWLClassExpression> group = currentInstantiation.getGroupInstantiation().get(anonGroupName);
					
					for (OWLClassExpression cls : group) {
						allClasses.add(cls);
					}				
				}			
			}
		}
		return allClasses;
	}
	
	
	private List<OWLObject> generateObjectHasValue(ExistsOrForAll hasValueExpression) {
		
		OWLObjectPropertyExpression property = (OWLObjectPropertyExpression) generate(hasValueExpression.getProperty());
		OWLIndividual individual = (OWLIndividual) generate((TemplatePrimitive) hasValueExpression.getExpression());
		
		if (individual != null && property != null) {
			List<OWLObject> values = new ArrayList<OWLObject>();
			values.add(new OWLObjectHasValueImpl(property, individual));
			return values;
		}		
		return null;
	}
	
	

	// All types of generated expressions are unique, except for
	// the intersection and union types, where multiple conclusions may be generated.
	private List<OWLObject> generate(ClsExpStr conclusionExp) {

		List<OWLObject> generatedExpressions = new ArrayList<OWLObject>();

		if (conclusionExp.getExpressionType() == null) {
			generatedExpressions.addAll(generateAtomicCls((AtomicCls) conclusionExp));
			return generatedExpressions;
			
		} else {

			ClassExpressionType classExpType = conclusionExp.getExpressionType();

			if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
				generatedExpressions.addAll(generateObjSomeValuesFrom((ExistsOrForAll) conclusionExp));
							
			}  else if (classExpType.equals(ClassExpressionType.OBJECT_HAS_VALUE)) {
				generatedExpressions.addAll(generateObjectHasValue((ExistsOrForAll) conclusionExp));
									
			} else if(classExpType.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {
				generatedExpressions.addAll(generateObjAllValuesFrom((ExistsOrForAll) conclusionExp));
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)) {
				generatedExpressions.addAll(generateObjMinCardinality((CardExpGen) conclusionExp));
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {
				generatedExpressions.addAll(generateObjExactCardinality((CardExpGen) conclusionExp));
				
			}else if (classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY)) {
				generatedExpressions.addAll(generateObjMaxCardinality((CardExpGen) conclusionExp));
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_UNION_OF)) {
				generatedExpressions.addAll(generateUnionExpressions((InterUnion) conclusionExp));				
				
			} else if (classExpType.equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
				generatedExpressions.addAll(generateIntersectionExpressions((InterUnion) conclusionExp));
			}
		}	
		return generatedExpressions;
	}

	
	private List<OWLClassExpression> generateUnionExpressions(InterUnion interUnion) {
		return generateIntersectionOrUnionExpressions(interUnion, true);
	}
	
	private List<OWLClassExpression> generateIntersectionExpressions(InterUnion interUnion) {
		return generateIntersectionOrUnionExpressions(interUnion, false);
	}
	
	
	private List<OWLClassExpression> generateIntersectionOrUnionExpressions(InterUnion interUnion, boolean generateUnion) {
		
		Set<Set<OWLClassExpression>> allGroups = generateGroup(interUnion);
		List<OWLClassExpression> allGroupExpressions = new ArrayList<OWLClassExpression>();
 		
		for (Set<OWLClassExpression> group : allGroups) {
			
			// Here we assume that an intersection and a union should contain > 1 class.
			// The case of a single class is handled in the atomic class generation.
			if (group.size() > 1) {
				OWLClassExpression union = null;
				
				if (generateUnion) {
					union = new OWLObjectUnionOfImpl(group);
				} else {
					union = new OWLObjectIntersectionOfImpl(group);
				}			
				allGroupExpressions.add(union);
			}					
		}
		return allGroupExpressions;
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
