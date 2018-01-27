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
public class RuleException {

	protected OWLAxiomStr correctAxiomStr;
	protected OWLAxiomStr laconicAxiomStr;
	private OWLAxiomStr justificationAxiomStr;
	private boolean includeEquivalentCase;
	private boolean includeIntersectionCase;

	public RuleException(OWLAxiomStr laconicAxiomStr,  OWLAxiomStr justificationAxiomStr, OWLAxiomStr correctAxiomStr, boolean includeEquivalentCase, boolean includeIntersectionCase) {
		this.laconicAxiomStr = laconicAxiomStr;
		this.justificationAxiomStr = justificationAxiomStr;
		this.correctAxiomStr = correctAxiomStr;
		this.includeEquivalentCase = includeEquivalentCase;
		this.includeIntersectionCase = includeIntersectionCase;
	}
	

	public ProofTree matchException(OWLAxiom laconicAxiom, OWLAxiom justificationAxiom) {
			
		if (!(laconicAxiom instanceof OWLSubClassOfAxiom)) {
			return null;
		}
		
		OWLSubClassOfAxiom laconicSubClsAxiom = (OWLSubClassOfAxiom) laconicAxiom;
		OWLSubClassOfAxiom justSubClsAxiom = convertJustificationToSubCls(laconicSubClsAxiom, justificationAxiom);
		
		if (justSubClsAxiom == null) {
			return null;
		}
		
		OWLAxiom generatedConclusion = null;
		
		if (includeIntersectionCase && justSubClsAxiom.getSuperClass() instanceof OWLObjectIntersectionOf) {
			
			OWLObjectIntersectionOf justSuperCls = (OWLObjectIntersectionOf) justSubClsAxiom.getSuperClass();
			List<OWLClassExpression> operands = justSuperCls.getOperandsAsList();
			
			// Currently allow only intersections of size 2, as specified in the thesis.
			if (operands.size() != 2) {
				return null;
			}
			
			for (OWLClassExpression innerExp : operands) {
				OWLSubClassOfAxiom justSubCls = new OWLSubClassOfAxiomImpl(laconicSubClsAxiom.getSubClass(), innerExp, new ArrayList<OWLAnnotation>());

				if (generateConclusion(laconicSubClsAxiom, justSubCls) != null) {
					generatedConclusion = generateConclusion(laconicSubClsAxiom, justSubCls);
					break;
				}
			}
			
		
		} else {
			generatedConclusion = generateConclusion(laconicSubClsAxiom, justSubClsAxiom);
		}
				
		if (generatedConclusion == null) {
			return null;
		}	
		
		return getCorrectedTree(generatedConclusion, justificationAxiom);
	}
	
	
	
	private OWLAxiom generateConclusion(OWLSubClassOfAxiom laconicSubClsAxiom, OWLSubClassOfAxiom justSubClsAxiom) {
		
		RuleString tmp = new RuleString("t", "t", correctAxiomStr, laconicAxiomStr, justificationAxiomStr);
		List<OWLAxiom> generatedConclusions = tmp.generateConclusions(laconicSubClsAxiom, justSubClsAxiom);
		
		if (generatedConclusions == null || generatedConclusions.size() != 1) {
			return null;
		}
		
		return generatedConclusions.get(0);
	}
	
	
	private OWLSubClassOfAxiom convertJustificationToSubCls(OWLSubClassOfAxiom laconicSubClsAxiom, OWLAxiom justificationAxiom) {
				
		if (includeEquivalentCase && justificationAxiom instanceof OWLEquivalentClassesAxiom) {

			OWLEquivalentClassesAxiom equivAxiom = (OWLEquivalentClassesAxiom) justificationAxiom;
			List<OWLClassExpression> innerExpressions = equivAxiom.getClassExpressionsAsList();
			
			if (!innerExpressions.contains(laconicSubClsAxiom.getSubClass()) || innerExpressions.size() != 2) {
				return null;
			}		
			innerExpressions.remove(laconicSubClsAxiom.getSubClass());
			return new OWLSubClassOfAxiomImpl(laconicSubClsAxiom.getSubClass(), innerExpressions.get(0), new ArrayList<OWLAnnotation>());
			
		} else if (justificationAxiom instanceof OWLSubClassOfAxiom) {
			return (OWLSubClassOfAxiom) justificationAxiom;
			
		} 
		return null;
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
