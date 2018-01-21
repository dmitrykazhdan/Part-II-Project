package RuleRestrictions;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;

import InferenceRules.Instantiation;

public class RestrictionChecker {
	
	private RuleRestriction[] restrictions;
	private Instantiation instantiation;
	
	
	public RestrictionChecker(RuleRestriction[] restrictions, Instantiation instantiation) {
		this.restrictions = restrictions;
		this.instantiation = instantiation;
	}
	
	public boolean checkRestrictionsForInstantiation() {
		
		for (RuleRestriction restriction : restrictions) {
			if (!checkRestriction(restriction)) {
				return false;
			}		
		}
		
		return true;
	}

	// Method for checking the various constraints that can be imposed on a rule.
	private boolean checkRestriction(RuleRestriction restriction) {

		if (restriction instanceof AbsCardinalityRestriction) {			
			return checkAbsoluteCardinalityRestriction((AbsCardinalityRestriction) restriction);

		} else if (restriction instanceof RelCardinalityRestriction) {
			return checkRelativeCardinalityRestriction((RelCardinalityRestriction) restriction);				

		} else if (restriction instanceof SubSetRestriction) {
			return checkSubSetRestriction((SubSetRestriction) restriction);

		} else if (restriction instanceof DisjointDatatypesRestriction) {
			return checkDisjointDatatypesRestriction((DisjointDatatypesRestriction) restriction);

		} else {
			return false;
		}
	}
	
	
	private boolean checkDisjointDatatypesRestriction(DisjointDatatypesRestriction restriction) {
		
		OWLDataRange dataRange1 = getDataRange(restriction.getFirstDataProperty());
		OWLDataRange dataRange2 = getDataRange(restriction.getSecondDataProperty());
		// Complete checking:
		return true;
		
	}
	
	// Attempt to retrieve an object given a name.
	// The name can either be a datatype identifier, or a literal identifier.
	private OWLDataRange getDataRange(String name) {
		
		OWLObject object = instantiation.getVariableInstantiation().get(name);
		
		if (object instanceof OWLLiteral) {
			return ((OWLLiteral) object).getDatatype();
		} else if (object instanceof OWLDatatype) {
			return (OWLDataRange) object;
		} else {
			return null;
		}
	}
	
	
	private boolean checkSubSetRestriction(SubSetRestriction restriction) {
		
		String subClassName = restriction.getSubClass();
		String superClassName = restriction.getSuperClass();
		
		// If the subclass or the superclass were unmatched, then the check fails.
		if (!instantiation.getGroupInstantiation().containsKey(subClassName) ||
				!instantiation.getGroupInstantiation().containsKey(superClassName)) {
			
			return false;
		}
		
		Set<OWLClassExpression> subClass = instantiation.getGroupInstantiation().get(subClassName);
		Set<OWLClassExpression> superClass = instantiation.getGroupInstantiation().get(superClassName);
		
		return superClass.containsAll(subClass);
	}
	
	
	private boolean checkRelativeCardinalityRestriction(RelCardinalityRestriction restriction) {

		String relativeBound = restriction.getRelativeBound();
		
		// If this name has not been matched, then the check fails.
		if (!instantiation.getCardinalityInstantiation().containsKey(relativeBound)) {
			return false;
		}
		
		// Otherwise retrieve the value of the relative bound and perform exact matching.
		Integer absoluteBound = instantiation.getCardinalityInstantiation().get(relativeBound);
		
		// Convert to an absolute cardinality restriction.
		AbsCardinalityRestriction abssoluteCardinalityRestriction = new AbsCardinalityRestriction(restriction.getCardinality(), restriction.getCardinalityType(), absoluteBound);
		
		return checkAbsoluteCardinalityRestriction(abssoluteCardinalityRestriction);
	}	
	
	
	private boolean checkAbsoluteCardinalityRestriction(AbsCardinalityRestriction restriction) {
		
		// Retrieve the absolute bound and the cardinality identifier.
		Integer absoluteBound = restriction.getAbsoluteBound();
		String cardinalityName = restriction.getCardinality();
		
		// If this name has not been matched, then the check fails.
		if (!instantiation.getCardinalityInstantiation().containsKey(cardinalityName)) {
			return false;
		}
		
		Integer cardinality = instantiation.getCardinalityInstantiation().get(cardinalityName);
		
		return evaluateInequality(cardinality, restriction.getCardinalityType(), absoluteBound);

	}
	
	
	// Evaluate a given inequality based on its type.
	private boolean evaluateInequality(Integer cardinality, CardinalitySign type, Integer bound) {
		
		switch(type) {
			case L:
				return cardinality < bound;
			case G:
				return cardinality > bound;
			case EQ:
				return cardinality == bound;
			case LEQ:
				return cardinality <= bound;
			case GEQ:
				return cardinality >= bound;
			default:
				return false;			
		}
	}
}
