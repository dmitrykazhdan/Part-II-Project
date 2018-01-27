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

	private OWLAxiomStr correctAxiomStr;
	private OWLAxiomStr laconicAxiomStr;
	private OWLAxiomStr justificationAxiomStr;
	

	public RuleException(OWLAxiomStr laconicAxiomStr,  OWLAxiomStr justificationAxiomStr, OWLAxiomStr correctAxiomStr) {
		this.laconicAxiomStr = laconicAxiomStr;
		this.justificationAxiomStr = justificationAxiomStr;
		this.correctAxiomStr = correctAxiomStr;
	}
	

	public ProofTree matchException(OWLAxiom laconicAxiom, OWLAxiom justificationAxiom) {
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
