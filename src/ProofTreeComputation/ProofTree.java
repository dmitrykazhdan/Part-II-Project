package ProofTreeComputation;

import java.util.ArrayList;
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
