package CoverageAnalysis;

import java.util.List;
import java.util.concurrent.Callable;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;

public class TreeGeneratorThread implements Callable<List<ProofTree>> {

	private Explanation<OWLAxiom> explanation;
	
	public TreeGeneratorThread(Explanation<OWLAxiom> explanation) {
		this.explanation = explanation;		
	}

	@Override
	public List<ProofTree> call() throws Exception {
		return ProofTreeGenerator.GenerateProofTree(explanation);
	}
}
