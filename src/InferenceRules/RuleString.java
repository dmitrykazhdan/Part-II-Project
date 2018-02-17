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

import InferenceGenerator.ConclusionGenerator;
import InferenceGenerator.PremiseMatcher;
import OWLExpressionTemplates.AtomicCls;
import OWLExpressionTemplates.CardExpGen;
import OWLExpressionTemplates.ClsExpStr;
import OWLExpressionTemplates.ComplementCls;
import OWLExpressionTemplates.TemplatePrimitive;
import OWLExpressionTemplates.UninstantiatedCardinalityException;
import ProofTreeComputation.ProofTree;
import RuleRestrictions.AbsCardinalityRestriction;
import RuleRestrictions.CardinalitySign;
import RuleRestrictions.DisjointDatatypesRestriction;
import RuleRestrictions.RelCardinalityRestriction;
import RuleRestrictions.RestrictionChecker;
import RuleRestrictions.RuleRestriction;
import RuleRestrictions.RuleRestrictions;
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

	private RuleRestrictions ruleRestrictions;

	public int getPremiseNumber() {
		return premiseNumber;
	}

	public String getRuleID() {
		return ruleID;
	}
	
	public String getRuleName() {
		return ruleName;
	}
	
	public RuleString(String ruleID, String ruleName, OWLAxiomStr conclusion, List<OWLAxiomStr> premisesStr) {
		this.ruleID = ruleID;
		this.ruleName = ruleName;
		this.premisesStr = premisesStr;
		this.premiseNumber = premisesStr.size();
		this.conclusionStr = conclusion;
		this.ruleRestrictions = new RuleRestrictions();
	}

	public RuleString(String ruleID, String ruleName, OWLAxiomStr conclusion, OWLAxiomStr... premises) {
		this(ruleID, ruleName, conclusion, new ArrayList<OWLAxiomStr>(Arrays.asList(premises)));
	}

	public RuleString(String ruleID, String ruleName, RuleRestrictions ruleRestrictions, OWLAxiomStr conclusion, OWLAxiomStr... premises) {
		this(ruleID, ruleName, conclusion, premises);
		this.ruleRestrictions = ruleRestrictions;
	}


	// Attempt to match the given set of axiom expressions to the given set of expression templates, given the restrictions
	private boolean matchExpressions(List<OWLAxiom> expressions, List<OWLAxiomStr> expressionStr, RuleRestriction[] ruleRestrictions) {

		PremiseMatcher matcher = new PremiseMatcher(expressions, expressionStr, ruleRestrictions);
		List<Instantiation> allInstantiations = matcher.getAllMatchedInstantiations();		
		return !(allInstantiations.size() == 0);
	}
	
	

	public boolean matchPremises(List<OWLAxiom> premises) {
		List<OWLAxiomStr> expressions = new ArrayList<OWLAxiomStr>(premisesStr);
		return matchExpressions(premises, expressions, ruleRestrictions.getPremiseRestrictions());
	}
	

	// When matching both premises and a conclusion, simply treat the conclusion as an extra premise
	// and use the premise-matching algorithm.
	public boolean matchPremisesAndConclusion(List<OWLAxiom> premises, OWLAxiom conclusion) {	

		List<OWLAxiom> premisesAndConclusion = new ArrayList<OWLAxiom>(premises);
		premisesAndConclusion.add(conclusion);
		
		List<OWLAxiomStr> expressions = new ArrayList<OWLAxiomStr>(premisesStr);
		expressions.add(conclusionStr);
		
		return matchExpressions(premisesAndConclusion, expressions, ruleRestrictions.getAllRestrictions());
	}
	
	
	// Return all possible conclusions that can be generated.
	public List<OWLAxiom> generateConclusions(List<OWLAxiom> premises) {
		ConclusionGenerator conclusionGenerator = new ConclusionGenerator(premises, premisesStr, conclusionStr, ruleRestrictions);
		return conclusionGenerator.generateConclusions();
	}

	public List<OWLAxiom> generateConclusions(OWLAxiom... premises) {
		return generateConclusions(Arrays.asList(premises));
	}
}
