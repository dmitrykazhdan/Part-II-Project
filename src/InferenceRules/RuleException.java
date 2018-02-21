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
 	C <= A --> C <= B.
 	
 	Where C <= A - laconic axiom,
 		  C <= B - justification axiom
 	
 	If includeEquivalentCase is enabled, the following case is included:
 	C <= A --> C <--> B
 	
 	If includeIntersectionCase is enabled, then 
 
 	C <= A --> C <= B
 	or of C <-> A --> C <= B 
 	where the class C must be the same between
 	the two axioms, but otherwise has no other restrictions on it, hence it does not have
 	to be specified in the patterns.
 	
 
 */
public class RuleException {

	private String exceptionID;
	protected OWLAxiomStr correctAxiomStr;
	protected OWLAxiomStr laconicAxiomStr;
	private OWLAxiomStr justificationAxiomStr;
	
	// Includes the case where the justification axiom is of the for C <--> B
	private boolean includeEquivalentCase;
	
	// Includes the case where the laconic class expression is of the form A ^ B
	private boolean includeIntersectionCase;

	
	public RuleException(String exceptionID, OWLAxiomStr laconicAxiomStr,  OWLAxiomStr justificationAxiomStr, OWLAxiomStr correctAxiomStr, boolean includeEquivalentCase, boolean includeIntersectionCase) {
		
		this.exceptionID = exceptionID;
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
		OWLSubClassOfAxiom justSubClsAxiom = null;
		
		if (justificationAxiom instanceof OWLEquivalentClassesAxiom) {
			justSubClsAxiom = parseEqvClsAxiom(laconicSubClsAxiom, (OWLEquivalentClassesAxiom) justificationAxiom);
		} else if (justificationAxiom instanceof OWLSubClassOfAxiom) {
			justSubClsAxiom = (OWLSubClassOfAxiom) justificationAxiom;
		}
						
		if (justSubClsAxiom == null || !justSubClsAxiom.getSubClass().equals(laconicSubClsAxiom.getSubClass())) {
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
			
			// Iterate over the two expressions in the intersection.
			// Attempt to match each one.
			for (OWLClassExpression innerExp : operands) {
				OWLSubClassOfAxiom justSubCls = new OWLSubClassOfAxiomImpl(laconicSubClsAxiom.getSubClass(), innerExp, new ArrayList<OWLAnnotation>());
				generatedConclusion = generateConclusion(laconicSubClsAxiom, justSubCls);
				
				if (generatedConclusion != null) {
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
	
	
	// Given the laconic axiom A <= B, and the justification axiom in the form A <= D, generate
	// the correct version of the axiom by creating a rule LA ^ JA --> CA and generating CA.
	private OWLAxiom generateConclusion(OWLSubClassOfAxiom laconicSubClsAxiom, OWLSubClassOfAxiom justSubClsAxiom) {
		
		RuleString tmp = new RuleString("t", "t", correctAxiomStr, laconicAxiomStr, justificationAxiomStr);
		List<OWLAxiom> generatedConclusions = tmp.generateConclusions(laconicSubClsAxiom, justSubClsAxiom);
		
		// Note that such generation should be unique.
		if (generatedConclusions == null || generatedConclusions.size() == 0) {
			return null;
		}	
		return generatedConclusions.get(0);
	}
	
	
	// Given a laconic axiom C <= A, check that the equivalent classes justification
	// axiom is of the form C <--> B for some B, and return the C <= B axiom.
	private OWLSubClassOfAxiom parseEqvClsAxiom(OWLSubClassOfAxiom laconicSubClsAxiom, OWLEquivalentClassesAxiom justificationAxiom) {
				
		if (includeEquivalentCase) {
			List<OWLClassExpression> innerExpressions = justificationAxiom.getClassExpressionsAsList();
			
			if (!innerExpressions.contains(laconicSubClsAxiom.getSubClass()) || innerExpressions.size() != 2) {
				return null;
			}		
			innerExpressions.remove(laconicSubClsAxiom.getSubClass());
			return new OWLSubClassOfAxiomImpl(laconicSubClsAxiom.getSubClass(), innerExpressions.get(0), new ArrayList<OWLAnnotation>());		
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
		List<ProofTree> generatedTrees = ProofTreeGenerator.computeCompleteProofTrees(root);
		
		if (generatedTrees == null || generatedTrees.size() == 0) {
			return null;
		} else {
			return generatedTrees.get(0);
		}		
	}
	
}
