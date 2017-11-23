package ProofTreeComputation;

import java.util.List;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owl.explanation.impl.laconic.LaconicExplanationGenerator;
import org.semanticweb.owl.explanation.impl.laconic.LaconicExplanationGeneratorFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class ProofTreeGenerator {

	/* Perhaps allow both methods for a Set<OWLAxiom> and an Explanation<OWLAxiom> ? */
	
	
	/*
	For our purpose, an initial tree is as an incomplete proof tree in which the root node
	is an entailment, the terminal nodes are axioms of a justification, and for each terminal
	node that contains unnecessary parts, a non-terminal node that links the root node to
	the associated terminal node is added.
	
	Such a non-terminal node, if existing, and the associated terminal nodes 
	form a local tree which corresponds to a single-premise deduction rule.
	
	Each non-terminal node is a lemma that unpacks part of the 
	meaning of the associated axiom in the justification.
	
	
	Horridge's algorithm is used to compute all laconic justifications from the given justification.
	An initial proof tree is constructed for each laconic justification.
	
	Constructing the tree:
	
	A tree in which the root node is the entailment, and all terminal nodes are axioms 
	in the original justification is initialised.
	
	For each axiom in the laconic justification, a reasoner is queried to identify 
	from which axiom in the original justification this axiom follows.
	If the two axioms are identical, no lemma will be added.
	
	Otherwise, the axiom in the laconic justification will be used as a lemma, 
	and a non-terminal node containing this lemma will be added between the root node 
	and the associated terminal node.
	
	 */

	private List<ProofTree> ComputeInitialProofTrees(Set<OWLAxiom> justification, OWLAxiom entailment) {
		
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		ExplanationGeneratorFactory<OWLAxiom> generatorFactory = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
		ExplanationGeneratorFactory<OWLAxiom> laconicGeneratorFactory = new LaconicExplanationGeneratorFactory<OWLAxiom>(generatorFactory);			
		ExplanationGenerator<OWLAxiom> laconicExplanationGenerator = laconicGeneratorFactory.createExplanationGenerator(justification);
		
		// Compute all sets of laconic justifications from the set of 		 
		// (potentially non-laconic) justifications given. 
		Set<Explanation<OWLAxiom>> laconicJustifications = laconicExplanationGenerator.getExplanations(entailment);
		
		
		return null;		
	}
	
	
	private List<ProofTree> ComputeCompleteProofTrees(ProofTree initialTree) {
		return null;
	}
	

	private List<ProofTree> ComputeProofTrees(Set<OWLAxiom> justification, OWLAxiom entailment) {
		
		
		
		// the construction of the proof trees is through 
		// exhaustive search of possible applications of the deduction rules
		
		// first, superfluous parts in the justification are eliminated 
		// resulting in one or more initial trees
		
		// then for each initial tree, all complete
		// proof trees are constructed
		
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
	
	


	public static ProofTree GenerateProofTree(OWLAxiom entailment, Set<OWLAxiom> justification) {
		
		/* Algorithm implementation goes here: */
		
		return null;
	}
	
	
	public static ProofTree GenerateProofTree(OWLAxiom entailment, Explanation<OWLAxiom> justification) {
		
		return GenerateProofTree(entailment, justification.getAxioms());
	}
	
}
