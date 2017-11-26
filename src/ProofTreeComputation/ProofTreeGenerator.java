package ProofTreeComputation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owl.explanation.impl.laconic.LaconicExplanationGenerator;
import org.semanticweb.owl.explanation.impl.laconic.LaconicExplanationGeneratorFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class ProofTreeGenerator {

	
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

	private List<ProofTree> ComputeInitialProofTrees(Set<OWLAxiom> justification, OWLAxiom entailment) throws OWLOntologyCreationException {
		
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		ExplanationGeneratorFactory<OWLAxiom> generatorFactory = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
		ExplanationGeneratorFactory<OWLAxiom> laconicGeneratorFactory = new LaconicExplanationGeneratorFactory<OWLAxiom>(generatorFactory);			
		ExplanationGenerator<OWLAxiom> laconicExplanationGenerator = laconicGeneratorFactory.createExplanationGenerator(justification);		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
						
		// Compute all sets of laconic justifications from the set of 		 
		// (potentially non-laconic) justifications given. 
		Set<Explanation<OWLAxiom>> laconicJustifications = laconicExplanationGenerator.getExplanations(entailment);
		
		List<ProofTree> initialTrees = new ArrayList<ProofTree>();
				
		Map<OWLAxiom, OWLOntology> justificationAxiomToOnt = new HashMap<OWLAxiom, OWLOntology>();
		
		for (OWLAxiom axiom : justification) {
			
			Set<OWLAxiom> ontAxiomSet = new HashSet<OWLAxiom>();
			ontAxiomSet.add(axiom);
			
			justificationAxiomToOnt.put(axiom, manager.createOntology(ontAxiomSet));
		}
		
		
		for (Explanation<OWLAxiom> laconicJustification : laconicJustifications) {
			
			List<ProofTree> subTrees = new ArrayList<ProofTree>();
			
			for (OWLAxiom axiom : justification) {			
				subTrees.add(new ProofTree(axiom, null));
			}
			
					
			for (OWLAxiom laconicAxiom : laconicJustification.getAxioms()) {	
				
				if (!justification.contains(laconicAxiom)) {
					
					if (ExceptionAxiom(laconicAxiom)) {
						continue;
					}
					
					for (OWLAxiom justificationAxiom : justificationAxiomToOnt.keySet()) {
												
						OWLReasoner reasoner = reasonerFactory.createReasoner(justificationAxiomToOnt.get(justificationAxiom)); 
						
						if (reasoner.isEntailed(laconicAxiom)) {
							
							ProofTree leaf = new ProofTree(justificationAxiom, null);
							List<ProofTree> leaves = new ArrayList<ProofTree>();
							leaves.add(leaf);
														
							ProofTree lemma = new ProofTree(laconicAxiom, leaves);												
							subTrees.set(subTrees.indexOf(leaf), lemma);
							
							break;
						}						
					}				
				}
			}
			
			ProofTree initialTree = new ProofTree(entailment, subTrees);
			initialTrees.add(initialTree);
		}
		
		
		
		return initialTrees;		
	}
	
	
	
	public boolean ExceptionAxiom(OWLAxiom axiom) {
		
		
		return false;
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
		List<ProofTree> initialProofTreeList = null;
		try {
			initialProofTreeList = ComputeInitialProofTrees(justification, entailment);
		} catch (OWLOntologyCreationException e) {

			e.printStackTrace();
		}
		
		List<ProofTree> completeProofTreeList = new ArrayList<ProofTree>();
		List<ProofTree> completeProofTrees = new ArrayList<ProofTree>();
		
		
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
