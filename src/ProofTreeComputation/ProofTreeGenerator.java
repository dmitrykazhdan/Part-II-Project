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
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import InferenceRules.GenerateExceptions;
import InferenceRules.InstanceOfRule;
import InferenceRules.RuleFinder;
import InferenceRules.RuleString;

public class ProofTreeGenerator {

	
	/*
	For our purpose, an initial tree is as an incomplete proof tree in which the root node
	is an entailment, the terminal nodes are axioms of a justification, and for each terminal
	node that contains unnecessary parts, a non-terminal node that links the root node to
	the associated terminal node is added.
	
	Such a non-terminal node, if existing, and the associated terminal node 
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
	
	private static Set<OWLAxiom> getLogicalAxioms (Set<OWLAxiom> axiomSet) {
		
		Set<OWLAxiom> logicalAxiomSet = new HashSet<OWLAxiom>();
		
		for (OWLAxiom axiom : axiomSet) {
			
			if (axiom.isLogicalAxiom()) {
				logicalAxiomSet.add(axiom);
			}
		}
		
		return logicalAxiomSet;
	}
	
	
	private static Set<Explanation<OWLAxiom>> getLaconicJustifications (Set<OWLAxiom> justification, OWLAxiom entailment) {
		
		// Create a laconic explanation generator.
		OWLReasonerFactory reasonerFactory = new ReasonerFactory();
		ExplanationGeneratorFactory<OWLAxiom> laconicGeneratorFactory = ExplanationManager.createLaconicExplanationGeneratorFactory(reasonerFactory);
		ExplanationGenerator<OWLAxiom> laconicExplanationGenerator = laconicGeneratorFactory.createExplanationGenerator(justification);		
		
		// Compute all sets of laconic justifications from 		 		 
		// the set of (potentially non-laconic) justification given.
		Set<Explanation<OWLAxiom>> laconicJustifications = laconicExplanationGenerator.getExplanations(entailment, 64);
		/* timeout if computation is taking too long */
		
		return laconicJustifications;
	}
	

	private static List<ProofTree> ComputeInitialProofTrees(Set<OWLAxiom> justification, OWLAxiom entailment) throws OWLOntologyCreationException {
		
		List<ProofTree> initialTrees = new ArrayList<ProofTree>();

		// Remove any non-logical axioms that have to be excluded from the proof trees.
		justification = getLogicalAxioms(justification);
		
		Set<Explanation<OWLAxiom>> laconicJustifications = getLaconicJustifications(justification, entailment);
					
		for (Explanation<OWLAxiom> laconicJustification : laconicJustifications) {
			
			// Match up the laconic axioms to the axioms in the justification which entail them.
			List<ProofTree> subTrees = matchLaconicToNonLaconicJust(justification, laconicJustification.getAxioms());
			
			if (subTrees == null) {
				continue;
			}
			
			// Attempt to assign rules to lemma nodes.
			List<ProofTree> subTreesWithRules = findRulesForLemmas(subTrees);
			
			if (subTreesWithRules != null) {
				
				// Create this initial tree with the entailment as the root
				// and the lemmas/axioms as the subtrees and leaves.
				ProofTree initialTree = new ProofTree(entailment, subTreesWithRules, null);
			
				// Add this initial tree to the list of all initial trees.
				initialTrees.add(initialTree);
			}
		}

		return initialTrees;		
	}	
	
	
	private static List<ProofTree> findRulesForLemmas(List<ProofTree> trees) {
		
		List<ProofTree> appliedTrees = new ArrayList<ProofTree>();
		
		// We assume that these trees will either be single terminal nodes
		// or lemmas, which are always trees with a root and one child
		for (ProofTree tree : trees) {
			
			if (tree.getSubTrees() != null) {
				
				ProofTree exceptionTree = GenerateExceptions.matchException(tree);
				
				if (exceptionTree != null) {
					appliedTrees.add(exceptionTree);
					
				} else {
					
					OWLAxiom laconicAxiom = tree.getAxiom();
					OWLAxiom justificationAxiom = tree.getSubTrees().get(0).getAxiom();
					List<OWLAxiom> premises = new ArrayList<OWLAxiom>();					
					premises.add(justificationAxiom);
					
					// Attempt to find matching rule.
					List<RuleString> applicableRules = RuleFinder.findRuleAppGivenConclusion(premises, laconicAxiom);
					
					if (applicableRules == null || applicableRules.size() == 0) {							
						System.out.println("Could not find rule for laconic axiom!");
						return null;
					} else {
						ProofTree appliedTree = tree;
						appliedTree.setInferenceRule(applicableRules.get(0));
						appliedTrees.add(appliedTree);
					}
				}
			} else {
				
				appliedTrees.add(tree);
			}
		}			
		return appliedTrees;
	}
	
	
	
	// Given a justification and its corresponding laconic justification, attempt to produce a set of initial proof trees
	// by matching the laconic and non-laconic justification axioms.
	private static List<ProofTree> matchLaconicToNonLaconicJust(Set<OWLAxiom> justification, Set<OWLAxiom> laconicJustification) throws OWLOntologyCreationException {
		
		// The set of sub-trees to be returned.
		List<ProofTree> trees = new ArrayList<ProofTree>();
		
		Set<OWLAxiom> unusedLaconicAxioms = new HashSet<OWLAxiom>(laconicJustification);
		
		for (OWLAxiom laconicAxiom : laconicJustification) {	
			
			if (justification.contains(laconicAxiom)) {
				
				// If the laconic axiom is equivalent to an existing one, add it directly.
				trees.add(new ProofTree(laconicAxiom, null, null));
				unusedLaconicAxioms.remove(laconicAxiom);
				
			} else {

				// Otherwise attempt to find from which axiom the laconic axiom follows.
				for (OWLAxiom justificationAxiom : justification) {

					if (isEntailed(justificationAxiom, laconicAxiom)) {

						ProofTree leaf = new ProofTree(justificationAxiom, null, null);
						List<ProofTree> leaves = new ArrayList<ProofTree>();
						leaves.add(leaf);

						ProofTree lemma = new ProofTree(laconicAxiom, leaves, null);		
						trees.add(lemma);
						unusedLaconicAxioms.remove(laconicAxiom);
						break;						
					}
				}									
			} 
		}
		
		// If all laconic axioms were matched successfully, return the sub-tree set. Otherwise return null.
		if (unusedLaconicAxioms.size() == 0) {
			return trees;
		} else {
			return null;
		}
	}
	
	
	// Given two axioms, check if one is entailed by the other.
	private static boolean isEntailed(OWLAxiom justificationAxiom, OWLAxiom laconicAxiom) throws OWLOntologyCreationException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLReasonerFactory reasonerFactory = new ReasonerFactory();	
		
		// In order to check whether one axiom is entailed by another, we must convert the axiom into an ontology first.
		Set<OWLAxiom> axiomSet = new HashSet<OWLAxiom>();
		axiomSet.add(justificationAxiom);						
		OWLReasoner reasoner = reasonerFactory.createReasoner(manager.createOntology(axiomSet)); 

		return reasoner.isEntailed(laconicAxiom);	
	}
	
	
	
	/*
		COMPUTATION OF COMPLETE PROOF TREES
	 */
	public static List<ProofTree> ComputeCompleteProofTrees(ProofTree initialTree) {
		
		List<ProofTree> completeProofTreeList = new ArrayList<ProofTree>();
		List<ProofTree> incompleteProofTreeList = new ArrayList<ProofTree>();
		List<ProofTree> newIncompleteProofTreeList =  new ArrayList<ProofTree>();

		incompleteProofTreeList.add(initialTree);
		
		// Add rules to all of the rule applications.
		// Need to copy tree as appropriate.
		while (!incompleteProofTreeList.isEmpty()) {
					
			newIncompleteProofTreeList = new ArrayList<ProofTree>();

			for (ProofTree incompleteProofTree : incompleteProofTreeList) {
				
				OWLAxiom rootAxiom = incompleteProofTree.getAxiom();
				List<OWLAxiom> childAxioms = incompleteProofTree.getChildAxioms();				
				List<RuleString> applicableRules = RuleFinder.findRuleAppGivenConclusion(childAxioms, rootAxiom);
				
				if (applicableRules != null && applicableRules.size() > 0) {	
					
					for (RuleString applicableRule : applicableRules) {
						ProofTree copiedTree = new ProofTree(incompleteProofTree);
						copiedTree.setInferenceRule(applicableRule);
						completeProofTreeList.add(incompleteProofTree);
					}
				} else {
					
					List<PartitionWithRules> partitionList = PartitionGenerator.generateAllPartitionsWithRules(childAxioms);
					
					for (PartitionWithRules partition : partitionList) {
						List<ProofTree> newProofTrees = ComputeProofByApplyingPartition(incompleteProofTree, partition);	
						
						if (newProofTrees != null) {
							newIncompleteProofTreeList.addAll(newProofTrees);
						}					
					}
				}				
			}
			
			incompleteProofTreeList = newIncompleteProofTreeList;
		}	
		return completeProofTreeList;
	}	
	/*
		END OF COMPUTATION OF COMPLETE PROOF TREES
	 */
	
	
	private static List<ProofTree> ComputeProofByApplyingPartition(ProofTree oldTree, PartitionWithRules partition) {

		List<ProofTree> newTrees = new ArrayList<ProofTree>();
		newTrees.add(new ProofTree(oldTree));

		for (InstanceOfRule subSet : partition.getItems()) {

			if (subSet.getRule() != null) {
				
				List<InstanceOfRule> newInferences = RuleFinder.generateInferences(subSet);

				if (newInferences == null) {
					return null;
				}

				List<ProofTree> newNewTrees = new ArrayList<ProofTree>();

				for (ProofTree incompleteTree : newTrees) { 
			
					for (InstanceOfRule newInference : newInferences) {

						ProofTree copiedTree = new ProofTree(incompleteTree);
						List<ProofTree> subTrees = copiedTree.getSubTrees();

						ProofTree newSubTree = new ProofTree(newInference.getConclusion(), new ArrayList<ProofTree>(), newInference.getRule());

						// Consider using a map ***
						for (ProofTree subTree : subTrees) {
							if (newInference.getPremises().contains(subTree.getAxiom())) {
								newInference.getPremises().remove(subTree.getAxiom());
								newSubTree.getSubTrees().add(subTree);
							}
						}

						for (ProofTree subTree : newSubTree.getSubTrees()) {
							subTrees.remove(subTree);
						}

						copiedTree.getSubTrees().add(newSubTree);							
						newNewTrees.add(copiedTree);
					}	
				}
				
				newTrees = newNewTrees;
			}
		}

		return newTrees;
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
			
			if (completeProofTrees != null) {
				completeProofTreeList.addAll(completeProofTrees);
			}
			
		}
		
		return completeProofTreeList;		
	}
	
	


	public static List<ProofTree> GenerateProofTree(OWLAxiom entailment, Set<OWLAxiom> justification) {
		
		/* Algorithm implementation goes here: */
		List<ProofTree> proofTreeList = ComputeProofTrees(justification, entailment);
		
		
		// Get most understandable tree.
		if ((proofTreeList != null) && proofTreeList.size() > 0) {
			return proofTreeList;
		} else {
			return null;
		}
	}
	
	
	public static List<ProofTree> GenerateProofTree(Explanation<OWLAxiom> explanation) {
		
		return GenerateProofTree(explanation.getEntailment(), explanation.getAxioms());
	}
	
}
