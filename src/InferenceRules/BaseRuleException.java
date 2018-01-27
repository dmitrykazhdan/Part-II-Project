package InferenceRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import OWLExpressionTemplates.ClsExpStr;
import OWLExpressionTemplates.ExistsOrForAll;
import OWLExpressionTemplates.OWLAxiomStr;
import OWLExpressionTemplates.SubClassStr;
import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

/*
 This class processes expressions of the form:
 	C <= A --> C <= B
 	or of C <-> A --> C <= B 
 	where the class C must be the same between
 	the two axioms, but otherwise has no other restrictions on it, hence it does not have
 	to be specified in the patterns.
 	
 
 */
public class BaseRuleException {

	private ClsExpStr correctClsExp;
	private ClsExpStr laconicClsExp;
	private ClsExpStr justificationClsExp;
	
	public BaseRuleException(ClsExpStr laconicClsExp,  ClsExpStr justificationClsExp) {
		this(laconicClsExp, justificationClsExp, null);
	}
	
	public BaseRuleException(ClsExpStr laconicClsExp,  ClsExpStr justificationClsExp, ClsExpStr correctClsExp) {
		this.laconicClsExp = laconicClsExp;
		this.justificationClsExp = justificationClsExp;
		this.correctClsExp = correctClsExp;
	}
	

	public ProofTree matchException(OWLAxiom laconicAxiom, OWLAxiom justificationAxiom) {	
		
		if (!(laconicAxiom instanceof OWLSubClassOfAxiom)) {
			return null;
		}
				

		OWLSubClassOfAxiom laconicSubClsAxiom = (OWLSubClassOfAxiom) laconicAxiom;
		OWLClassExpression laconicSubCls = laconicSubClsAxiom.getSubClass();
		OWLClassExpression laconicSuperCls = laconicSubClsAxiom.getSuperClass();
		OWLClassExpression justSuperCls = null;

		if (justificationAxiom instanceof OWLEquivalentClassesAxiom) {

			OWLEquivalentClassesAxiom equivClassesAxiom = (OWLEquivalentClassesAxiom) justificationAxiom;
			List<OWLClassExpression> equivClassesAxiomExpressions = equivClassesAxiom.getClassExpressionsAsList();

			if (equivClassesAxiomExpressions.size() > 2 || !equivClassesAxiomExpressions.contains(laconicSubCls)) {
				return null;
			}

			equivClassesAxiomExpressions.remove(laconicSubCls);
			justSuperCls = equivClassesAxiomExpressions.get(0);


		} else if (justificationAxiom instanceof OWLSubClassOfAxiom) {

			OWLSubClassOfAxiom justSubClsAxiom = (OWLSubClassOfAxiom) justificationAxiom;
			OWLClassExpression justSubCls = justSubClsAxiom.getSubClass();
			justSuperCls = justSubClsAxiom.getSuperClass();

			if (!laconicSubCls.equals(justSubCls)) {
				return null;
			}					
		}

		OWLAxiom correctedConclusion = null;
		OWLClassExpression correctedConclusionClsExp= null;
		
		if (justSuperCls instanceof OWLObjectIntersectionOf && laconicSuperCls instanceof OWLObjectIntersectionOf) {
			correctedConclusionClsExp = matchComplexException((OWLObjectIntersectionOf) justSuperCls, (OWLObjectIntersectionOf) laconicSuperCls);

		} else {
			correctedConclusionClsExp = matchBaseCaseWithConc(laconicSuperCls, justSuperCls);
		}
		
		if (correctedConclusionClsExp == null) {
			return null;
		}
		correctedConclusion = new OWLSubClassOfAxiomImpl(laconicSubCls, correctedConclusionClsExp, new ArrayList<OWLAnnotation>());
		return getCorrectedTree(correctedConclusion, justificationAxiom);
	}
	
	
	
	private OWLClassExpression matchBaseCaseWithConc(OWLClassExpression laconic, OWLClassExpression justification) {

		OWLAxiomStr premise1 = new SubClassStr("C", justificationClsExp);
		OWLAxiomStr premise2 = new SubClassStr("C", laconicClsExp);
		OWLAxiomStr conclusion = new SubClassStr("C", correctClsExp);		
		RuleString tmpRule = new RuleString("tmp", "tmp", conclusion, premise1, premise2);
		
		OWLAxiom tmpAxiom1 = new OWLSubClassOfAxiomImpl(justification, justification, new ArrayList<OWLAnnotation>());
		OWLAxiom tmpAxiom2 = new OWLSubClassOfAxiomImpl(laconic, laconic, new ArrayList<OWLAnnotation>());

		List<OWLAxiom> generatedAxioms = tmpRule.generateConclusions(tmpAxiom1, tmpAxiom2);
		
		if (generatedAxioms != null && generatedAxioms.size() > 0) {
			OWLSubClassOfAxiom c = (OWLSubClassOfAxiom) generatedAxioms.get(0);
			return c.getSuperClass();
		}
		
		return null;	
	}
	
	
	
	private OWLClassExpression generateBaseCase(OWLClassExpression justification) {

		OWLAxiomStr premise = new SubClassStr("C", justificationClsExp);
		OWLAxiomStr conclusion = new SubClassStr("C", correctClsExp);		
		RuleString tmpRule = new RuleString("tmp", "tmp", conclusion, premise);
		
		OWLAxiom tmpAxiom = new OWLSubClassOfAxiomImpl(justification, justification, new ArrayList<OWLAnnotation>());
		List<OWLAxiom> generatedAxioms = tmpRule.generateConclusions(tmpAxiom);
		
		if (generatedAxioms != null && generatedAxioms.size() > 0) {
			OWLSubClassOfAxiom c = (OWLSubClassOfAxiom) generatedAxioms.get(0);
			return c.getSuperClass();
		}
		
		return null;
	}
	
	

	
	private OWLObjectIntersectionOf matchComplexException(OWLObjectIntersectionOf justification, OWLObjectIntersectionOf conclusion) {

		List<OWLClassExpression> justificationExpressions = justification.getOperandsAsList();
		List<OWLClassExpression> conclusionExpressions = conclusion.getOperandsAsList();
		Set<OWLClassExpression> generatedIntersection = new HashSet<OWLClassExpression>();
		
		for (OWLClassExpression justificationExp : justificationExpressions) {
			if (conclusionExpressions.contains(justificationExp)) {
				conclusionExpressions.remove(justificationExp);
				generatedIntersection.add(justificationExp);
				
			} else {
				
				OWLClassExpression generatedConclusion = generateBaseCase(justificationExp);
				
				if (conclusionExpressions.contains(generatedConclusion)) {
					conclusionExpressions.remove(generatedConclusion);
					generatedIntersection.add(generatedConclusion);
				}			
			}
		}
		
		if (conclusionExpressions.size() > 0) {
			return null;
		} else {
			return new OWLObjectIntersectionOfImpl(generatedIntersection);
		}
	}

	

	
	// Given a (laconic, justification) axiom pair, return a corrected proof tree.
	private ProofTree getCorrectedTree(OWLAxiom correctedAxiom, OWLAxiom justificationAxiom) {
		
		ProofTree leaf = new ProofTree(justificationAxiom, null, null);
		
		if (correctedAxiom.equalsIgnoreAnnotations(justificationAxiom)) {
			return leaf;
		} 
		
		ProofTree root = new ProofTree(correctedAxiom, Arrays.asList(new ProofTree[] {leaf}), null);
		List<ProofTree> generatedTrees = ProofTreeGenerator.ComputeCompleteProofTrees(root);
		
		if (generatedTrees == null || generatedTrees.size() != 1) {
			return null;
		} else {
			return generatedTrees.get(0);
		}		
	}
	
}
