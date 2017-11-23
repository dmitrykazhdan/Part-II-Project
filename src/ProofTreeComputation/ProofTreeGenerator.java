package ProofTreeComputation;

import java.util.List;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ProofTreeGenerator {

	private List<ProofTree> ComputeInitialProofTrees(Explanation<OWLAxiom> justification, OWLAxiom entailment) {
		
		return null;		
	}
	
	
	private List<ProofTree> ComputeCompleteProofTrees(ProofTree initialTree) {
		return null;
	}
	

	private List<ProofTree> ComputeProofTrees(Explanation<OWLAxiom> justification, OWLAxiom entailment) {
		
		
		// Think of better variable names
		List<ProofTree> initialProofTreeList = ComputeInitialProofTrees(justification, entailment);
		List<ProofTree> completeProofTreeList = null;
		List<ProofTree> completeProofTrees = null;
		
		
		for (ProofTree initialTree : initialProofTreeList) {
			
			
			completeProofTrees = ComputeCompleteProofTrees(initialTree);
			completeProofTreeList.addAll(completeProofTrees);
			
			
		}
		
		return completeProofTreeList;		
	}
	
	


	public static ProofTree GenerateProofTree(OWLAxiom entailment, Explanation<OWLAxiom> justification) {
		
		/* Algorithm implementation goes here: */
		
		return null;
	}
	
}
