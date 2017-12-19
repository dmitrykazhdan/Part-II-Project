package ProofTreeComputation;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;


public class ProofTree {
	
	
	private OWLAxiom axiom;
	private InferenceRule inferenceRule;
	private List<ProofTree> subTrees;
	
	
	public ProofTree(OWLAxiom axiom, List<ProofTree> subTrees, InferenceRule inferenceRule) {
		
		this.axiom = axiom;
		this.subTrees = subTrees;
		this.inferenceRule = inferenceRule;
	}
	
	// Create a copy of the tree.
	public ProofTree(ProofTree proofTree) {
		
		this.axiom = proofTree.getAxiom();
		this.inferenceRule = proofTree.inferenceRule;
			
		if (proofTree.getSubTrees() == null) {
			this.subTrees = null;
		} else {
			
			this.subTrees = new ArrayList<ProofTree>();
			
			for (ProofTree subTree : proofTree.getSubTrees()) {
				ProofTree copySubTree = new ProofTree(subTree);
				this.subTrees.add(copySubTree);
			}		
		}
	}
	
		
	public OWLAxiom getAxiom() {		
		return axiom;
	}
	
	public void setAxiom(OWLAxiom axiom) {		
		this.axiom = axiom;
	}
	
	public List<ProofTree> getSubTrees() {		
		return subTrees;
	}
	
	public void setSubTrees(List<ProofTree> subTrees) {		
		this.subTrees = subTrees;
	}
	
	public void setInferenceRule(InferenceRule inferenceRule) {
		this.inferenceRule = inferenceRule;
	}
	
	// Return the root axioms of all direct children  as a list.
	public List<OWLAxiom> getChildAxioms() {
		
		if (subTrees == null) {
			return null;
		}
		
		List<OWLAxiom> childAxioms = new ArrayList<OWLAxiom>();
		
		for (ProofTree subTree : subTrees) {
			childAxioms.add(subTree.getAxiom());
		}
		
		return childAxioms;
		
	}

}
