package ProofTreeComputation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owl.explanation.impl.laconic.LaconicExplanationGenerator;
import org.semanticweb.owl.explanation.impl.laconic.LaconicExplanationGeneratorFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
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

	private static List<ProofTree> ComputeInitialProofTrees(Set<OWLAxiom> justification, OWLAxiom entailment) throws OWLOntologyCreationException {
		
		// Create a laconic explanation generator.
		OWLReasonerFactory reasonerFactory = new ReasonerFactory();
		ExplanationGeneratorFactory<OWLAxiom> laconicGeneratorFactory = ExplanationManager.createLaconicExplanationGeneratorFactory(reasonerFactory);
		ExplanationGenerator<OWLAxiom> laconicExplanationGenerator = laconicGeneratorFactory.createExplanationGenerator(justification);		
		
		// Compute all sets of laconic justifications from 		 		 
		// the set of (potentially non-laconic) justifications given.
		Set<Explanation<OWLAxiom>> laconicJustifications = laconicExplanationGenerator.getExplanations(entailment, 4);
		/* timeout if computation is taking too long */
						
		List<ProofTree> initialTrees = new ArrayList<ProofTree>();
					
		// In order to check whether one axiom is entailed by another,
		// we must convert the axiom into an ontology first.
		// Thus we create a map from a justification axiom to an ontology created from this axiom.
		Map<OWLAxiom, OWLOntology> justificationAxiomToOnt = new HashMap<OWLAxiom, OWLOntology>();

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		// Populate the map using all of the justification axioms.
		for (OWLAxiom axiom : justification) {
			
			Set<OWLAxiom> ontAxiomSet = new HashSet<OWLAxiom>();
			ontAxiomSet.add(axiom);		
			justificationAxiomToOnt.put(axiom, manager.createOntology(ontAxiomSet));
		}
		
		
		for (Explanation<OWLAxiom> laconicJustification : laconicJustifications) {
			
			// Create a list of proof trees, consisting of the initial justification axioms
			// which will be the terminal nodes of the initial tree
			// for this laconic justification.
			List<ProofTree> subTrees = new ArrayList<ProofTree>();
			
			for (OWLAxiom axiom : justification) {			
				subTrees.add(new ProofTree(axiom, null, null));
			}
			
					
			for (OWLAxiom laconicAxiom : laconicJustification.getAxioms()) {	
				
				// If the laconic axiom is contained in the justification axioms,
				// it means that it is equivalent to one of these axioms and thus
				// will not be a lemma.
				if (!justification.contains(laconicAxiom)) {
					
					// Check whether this axiom is an exception that has to be ignored.
					if (ExceptionAxiom(laconicAxiom)) {
						continue;
					}
					
					// Question: how do we know that a laconic justification
					// follows from at most 1 axiom in the original justification?
					// See the API documentation.
					// Also, can multiple laconic axioms follow from a single non-laconic axiom?
					for (OWLAxiom justificationAxiom : justificationAxiomToOnt.keySet()) {
												
						OWLReasoner reasoner = reasonerFactory.createReasoner(justificationAxiomToOnt.get(justificationAxiom)); 
												
						if (reasoner.isEntailed(laconicAxiom)) {
							
							ProofTree leaf = new ProofTree(justificationAxiom, null, null);
							List<ProofTree> leaves = new ArrayList<ProofTree>();
							leaves.add(leaf);
							
							String rule = findSinglePremiseRule(laconicAxiom, justificationAxiom);
														
							ProofTree lemma = new ProofTree(laconicAxiom, leaves, rule);												
							subTrees.set(subTrees.indexOf(leaf), lemma);
							
							break;
						}						
					}				
				}
			}
			
			// Create this initial tree with the entailment as the root
			// and the lemmas/axioms as the subtrees and leaves.
			ProofTree initialTree = new ProofTree(entailment, subTrees, null);
			
			// Add this initial tree to the list of all initial trees.
			initialTrees.add(initialTree);
		}
		
		
		
		return initialTrees;		
	}
	
	
	public static String findSinglePremiseRule(OWLAxiom entailment, OWLAxiom axiom) {
		
		if ((axiom.isOfType(AxiomType.EQUIVALENT_CLASSES)) && (entailment.isOfType(AxiomType.SUBCLASS_OF))) {			
			if (axiom.getClassesInSignature().containsAll(entailment.getClassesInSignature())) {				
				return "EquCls";
			}			
		}
		
		return null;
	}
	
	
	public static boolean ExceptionAxiom(OWLAxiom axiom) {
				
		return false;
	}
	
	
	private static List<ProofTree> ComputeCompleteProofTrees(ProofTree initialTree) {
		return null;
	}
	
	
	private static List<ProofTree> ComputeProofTrees(Set<OWLAxiom> justification, OWLAxiom entailment) {
			
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
		List<ProofTree> proofTreeList = ComputeProofTrees(justification, entailment);
		
		return null;
	}
	
	
	public static ProofTree GenerateProofTree(Explanation<OWLAxiom> explanation) {
		
		return GenerateProofTree(explanation.getEntailment(), explanation.getAxioms());
	}
	
}
