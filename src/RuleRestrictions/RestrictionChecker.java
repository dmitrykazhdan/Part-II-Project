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

		} else if (restriction instanceof GroupContainsRestriction) {
			return checkGroupContainsRestriction((GroupContainsRestriction) restriction);
			
		} else if (restriction instanceof UnequalLiteralsRestriction) {
			return checkUnequalLiteralsRestriction((UnequalLiteralsRestriction) restriction);
			
		} else {
			return false;
		}
	}
	
	
	private boolean checkUnequalLiteralsRestriction(UnequalLiteralsRestriction restriction) {
		
		String firstLiteral = restriction.getFirstLiteral();
		String secondLiteral = restriction.getSecondLiteral();
		
		if (!instantiation.getVariableInstantiation().containsKey(firstLiteral) ||
			!instantiation.getVariableInstantiation().containsKey(secondLiteral)) {
				
			return false;
		}
		
		OWLLiteral l0 = (OWLLiteral) instantiation.getVariableInstantiation().get(firstLiteral);
		OWLLiteral l1 = (OWLLiteral) instantiation.getVariableInstantiation().get(secondLiteral);		
		return !l0.equals(l1);
	}
	
	
	private boolean checkGroupContainsRestriction(GroupContainsRestriction restriction) {
		
		String groupName = restriction.getAnonymousGroupName();
		String atomicClsName = restriction.getAtomicClsName();
		
		if (!instantiation.getGroupInstantiation().containsKey(groupName) ||
			!instantiation.getVariableInstantiation().containsKey(atomicClsName)) {
			
			return false;			
		}
		
		OWLClassExpression atomicCls = (OWLClassExpression) instantiation.getVariableInstantiation().get(atomicClsName);
		Set<OWLClassExpression> group = instantiation.getGroupInstantiation().get(groupName);
		
		return group.contains(atomicCls);
	}
	
	
	// Currently only built-in datatypes are checked for disjointness.
	private boolean checkDisjointDatatypesRestriction(DisjointDatatypesRestriction restriction) {
		
		OWLDatatype datatype1 = getDatatype(restriction.getFirstDataProperty());
		OWLDatatype datatype2 = getDatatype(restriction.getSecondDataProperty());
		
		if (datatype1 == null || datatype2 == null) {
			return false;
		}		
		return datatype1.isBuiltIn() && datatype2.isBuiltIn() && 
				!datatype1.getBuiltInDatatype().equals(datatype2.getBuiltInDatatype());
	}
	
	
	// Attempt to retrieve a datatype, given its name.
	private OWLDatatype getDatatype(String name) {
		
		if (!instantiation.getVariableInstantiation().containsKey(name)) {
			return null; 
		} 
		OWLObject object = instantiation.getVariableInstantiation().get(name);
		
		if (object instanceof OWLDatatype) {
			return (OWLDatatype) object;
		} else if (object instanceof OWLLiteral) {
			return (OWLDatatype) ((OWLLiteral) object).getDatatype();
		}
		return null;
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
		AbsCardinalityRestriction absoluteCardinalityRestriction = new AbsCardinalityRestriction(restriction.getCardinality(), restriction.getCardinalityType(), absoluteBound);
		
		return checkAbsoluteCardinalityRestriction(absoluteCardinalityRestriction);
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
