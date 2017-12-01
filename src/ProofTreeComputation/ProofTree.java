package ProofTreeComputation;

import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;


public class ProofTree {
	
	
	private OWLAxiom axiom;
	private String inferenceRule;
	private List<ProofTree> subTrees;
	
	
	public ProofTree(OWLAxiom axiom, List<ProofTree> subTrees, String inferenceRule) {
		
		this.axiom = axiom;
		this.subTrees = subTrees;
		this.inferenceRule = inferenceRule;
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

}
