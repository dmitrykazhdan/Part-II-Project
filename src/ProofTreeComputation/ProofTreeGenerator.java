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
	An initial tree is as an incomplete proof tree in which the root node
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
	in the original justification is initialized.
	
	For each axiom in the laconic justification, a reasoner is queried to identify 
	from which axiom in the original justification this axiom follows.
	If the two axioms are identical, no lemma will be added.
	
	Otherwise, the axiom in the laconic justification will be used as a lemma, 
	and a non-terminal node containing this lemma will be added between the root node 
	and the associated terminal node.
	
	 */
	
	
	
	public static List<ProofTree> generateProofTrees(Explanation<OWLAxiom> explanation) {	
		return generateProofTrees(explanation.getEntailment(), explanation.getAxioms());
	}
	
	
	public static List<ProofTree> generateProofTrees(OWLAxiom entailment, Set<OWLAxiom> justification) {
		
		List<ProofTree> proofTreeList = computeProofTrees(justification, entailment);
				
		if ((proofTreeList != null) && proofTreeList.size() > 0) {
			return proofTreeList;
		} else {
			return null;
		}
	}
	
	
	/*
	 Construction of the proof trees is done by exhaustive search of possible applications of the deduction rules. 
	 First, superfluous parts in the justification are eliminated resulting in one or more initial trees,
	 then for each initial tree, all complete proof trees are constructed.
	 */
	private static List<ProofTree> computeProofTrees(Set<OWLAxiom> justification, OWLAxiom entailment) {

		List<ProofTree> initialProofTreeList = null;
		
		try {
			initialProofTreeList = computeInitialProofTrees(justification, entailment);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		
		List<ProofTree> allProofTrees = new ArrayList<ProofTree>();
				
		for (ProofTree initialTree : initialProofTreeList) {
						
			List<ProofTree> completedProofTrees = computeCompleteProofTrees(initialTree);
			
			if (completedProofTrees != null) {
				allProofTrees.addAll(completedProofTrees);
			}			
		}		
		return allProofTrees;		
	}
	
	
	/*
	 * COMPUTATION OF INITIAL TREES
	 */
	private static List<ProofTree> computeInitialProofTrees(Set<OWLAxiom> justification, OWLAxiom entailment) throws OWLOntologyCreationException {
		
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

	
	
	// Remove non-logical axioms from an axiom set.
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
		
		// Compute all sets of laconic justifications from the set of (potentially non-laconic) justification given.	 		 
		Set<Explanation<OWLAxiom>> laconicJustifications = laconicExplanationGenerator.getExplanations(entailment);		
		return laconicJustifications;
	}
	

	
	private static List<ProofTree> findRulesForLemmas(List<ProofTree> lemmaTrees) {
		
		List<ProofTree> appliedTrees = new ArrayList<ProofTree>();
		
		// We assume that these trees will either be single terminal nodes
		// or lemmas, which are always trees with a root and one child
		for (ProofTree tree : lemmaTrees) {
			
			// If the tree is a single node, then can add it directly.
			if (tree.getSubTrees() == null) {			
				appliedTrees.add(tree);
				
			} else {
				
				// Check whether the given subtree is an instance of an exception.
				ProofTree exceptionTree = GenerateExceptions.matchException(tree);
				
				if (exceptionTree != null) {
					appliedTrees.add(exceptionTree);
					
				} else {
					
					// Otherwise, attempt to find matching rule.
					ProofTree appliedTree = matchLemmaToRule(tree);
					
					if (appliedTree == null) {
						return null;
					} else {
						appliedTrees.add(appliedTree);
					}
				}
			} 
		}			
		return appliedTrees;
	}
	
	
	// Given a tree consisting of a root node and a single child leaf, attempt to match it to an inference rule.
	private static ProofTree matchLemmaToRule(ProofTree lemmaTree) {
		
		OWLAxiom laconicAxiom = lemmaTree.getAxiom();
		OWLAxiom justificationAxiom = lemmaTree.getSubTrees().get(0).getAxiom();
		List<OWLAxiom> premises = new ArrayList<OWLAxiom>();					
		premises.add(justificationAxiom);
							
		List<RuleString> applicableRules = RuleFinder.findRuleAppGivenConclusion(premises, laconicAxiom);

		// If there are multiple applicable rules returned, use the first one, since it does not matter
		// which one to use.
		if (applicableRules == null || applicableRules.size() == 0) {							
			System.out.println("Could not find rule for laconic axiom!");
			return null;
		} else {
			ProofTree appliedTree = new ProofTree(lemmaTree);
			appliedTree.setInferenceRule(applicableRules.get(0));
			return appliedTree;
		}
	}
	
	
	// Given a justification and its corresponding laconic justification, attempt to produce a set of initial proof trees
	// by matching the laconic and non-laconic justification axioms.
	private static List<ProofTree> matchLaconicToNonLaconicJust(Set<OWLAxiom> justification, Set<OWLAxiom> laconicJustification) throws OWLOntologyCreationException {
		
		// The set of sub-trees to be returned.
		List<ProofTree> matchedAxiomTrees = new ArrayList<ProofTree>();
		
		for (OWLAxiom laconicAxiom : laconicJustification) {	
			
			if (justification.contains(laconicAxiom)) {
				
				// If the laconic axiom is equivalent to an existing one, add it directly.
				matchedAxiomTrees.add(new ProofTree(laconicAxiom, null, null));

			} else {

				// Otherwise attempt to find from which axiom the laconic axiom follows.
				boolean foundMatchingAxiom = false;
				
				for (OWLAxiom justificationAxiom : justification) {
					if (isEntailed(justificationAxiom, laconicAxiom)) {

						ProofTree leaf = new ProofTree(justificationAxiom, null, null);
						List<ProofTree> leaves = new ArrayList<ProofTree>();
						leaves.add(leaf);
						foundMatchingAxiom = true;
						ProofTree lemma = new ProofTree(laconicAxiom, leaves, null);		
						matchedAxiomTrees.add(lemma);
						break;						
					}
				}
				
				// If a corresponding axiom was not found for a laconic axiom, return null.
				if (!foundMatchingAxiom) {
					return null;
				}
			} 
		}
		return matchedAxiomTrees;
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
	 * END OF COMPUTATION OF INITIAL TREES
	 */
	
	
	
	
	/*
		COMPUTATION OF COMPLETE PROOF TREES
	 */
	public static List<ProofTree> computeCompleteProofTrees(ProofTree initialTree) {
		
		List<ProofTree> completeProofTreeList = new ArrayList<ProofTree>();
		List<ProofTree> incompleteProofTreeList = new ArrayList<ProofTree>();
		incompleteProofTreeList.add(initialTree);
		
		while (!incompleteProofTreeList.isEmpty()) {
					
			List<ProofTree> newIncompleteProofTreeList = new ArrayList<ProofTree>();

			for (ProofTree incompleteProofTree : incompleteProofTreeList) {
				
				// Attempt to match the current root children to the root.
				List<ProofTree> matchedTrees = matchEntailmentToChildAxioms(incompleteProofTree);
				
				// If such a match is found, add all matches to the list.
				if (matchedTrees != null && matchedTrees.size() > 0) {					
					completeProofTreeList.addAll(matchedTrees);
					
				} else {
					
					// Otherwise compute all partitions where there is at least one applicable rule.
					List<PartitionWithApplicableInfRules> partitionList = PartitionGenerator.generateAllPartitionsWithRules(incompleteProofTree.getChildAxioms());
					
					// Compute new incomplete trees by generating new lemma nodes from the applicable rules.
					for (PartitionWithApplicableInfRules partition : partitionList) {
						List<ProofTree> newProofTrees = addInferredNodesToTree(incompleteProofTree, partition);	
						
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
	
	
	private static List<ProofTree> matchEntailmentToChildAxioms(ProofTree incompleteProofTree) {

		List<ProofTree> completedProofTrees = new ArrayList<ProofTree>();
		OWLAxiom entailment = incompleteProofTree.getAxiom();
		List<OWLAxiom> childAxioms = incompleteProofTree.getChildAxioms();
		
		// Attempt to find all rules matching the entailment to its child axioms.
		List<RuleString> applicableRules = RuleFinder.findRuleAppGivenConclusion(childAxioms, entailment);
		
		// If at least one such rule is found, return all trees with their inference rule set.
		if (applicableRules != null && applicableRules.size() > 0) {					
			for (RuleString applicableRule : applicableRules) {
				ProofTree copiedTree = new ProofTree(incompleteProofTree);
				copiedTree.setInferenceRule(applicableRule);
				completedProofTrees.add(copiedTree);
			}
		}	
		return completedProofTrees;
	}

	
	// This method takes a proof tree and a partition of its root children.
	// It then generates new intermediate nodes from the partition and creates new trees by adding these nodes
	// between the root node and the root child nodes of the given tree.
	private static List<ProofTree> addInferredNodesToTree(ProofTree oldTree, PartitionWithApplicableInfRules partition) {

		List<List<InstanceOfRule>> allPartitionInferences = new ArrayList<List<InstanceOfRule>>();
		List<ProofTree> newTrees = new ArrayList<ProofTree>();
		newTrees.add(new ProofTree(oldTree));
		
		// Compute all possible applications of rules.
		for (InstanceOfRule partitionSubSet : partition.getItems()) {
			if (partitionSubSet.getRule() != null) {

				// Generate all conclusions from the partition subset.
				List<InstanceOfRule> newInferences = RuleFinder.generateInferences(partitionSubSet);

				// If no conclusion returned, this is an error. Return null.
				if (newInferences == null) {
					return null;
				}			
				allPartitionInferences.add(newInferences);
			}
		}
		
		
		
		
		
		for (InstanceOfRule partitionSubSet : partition.getItems()) {

			if (partitionSubSet.getRule() != null) {
				
				// Generate all conclusions from the partition subset.
				List<InstanceOfRule> newInferences = RuleFinder.generateInferences(partitionSubSet);

				// If no conclusion returned, this is an error. Return null.
				if (newInferences == null) {
					return null;
				}

				List<ProofTree> intermediateTrees = new ArrayList<ProofTree>();

				// Add all generated conclusions to all intermediate trees.
				for (ProofTree incompleteTree : newTrees) { 		
					for (InstanceOfRule newInference : newInferences) {
						
						ProofTree proofTreeWithAddedLemmas = addLemmasToIntermediateTree(incompleteTree, newInference);
						
						if (proofTreeWithAddedLemmas == null) {
							return null;
						}					
						intermediateTrees.add(proofTreeWithAddedLemmas);
					}	
				}			
				newTrees = intermediateTrees;
			}
		}
		return newTrees;
	}
	
	
	
	private static List<ProofTree> generateTreesFromPartitionApplications(List<ProofTree> originalSubTrees, List<List<InstanceOfRule>> partition) {
		
		List<InstanceOfRule> generatedLemmas = partition.remove(0);
		List<ProofTree> generatedTrees = new ArrayList<ProofTree>();
		
		for (InstanceOfRule subSet : generatedLemmas) {
			generatedTrees.addAll(generateTreesFromPartitionApplications(partition));
		}
		
		return null;
	}
	
	// Given an incomplete tree and an instance of a rule application,
	// add a lemma node to the tree between the root node and its child axioms that represent the base axioms
	// of the rule application.
	private static ProofTree addLemmasToIntermediateTree(ProofTree incompleteTree, InstanceOfRule newInference ) {

		// Create a copy of the given tree.
		ProofTree copiedTree = new ProofTree(incompleteTree);
		List<ProofTree> subTrees = copiedTree.getSubTrees();

		ProofTree newLemmaSubTree = new ProofTree(newInference.getConclusion(), new ArrayList<ProofTree>(), newInference.getRule());

		// Connect the lemma subtree root to appropriate subtrees in the original trees.
		for (ProofTree subTree : subTrees) {
			if (newInference.getPremises().contains(subTree.getAxiom())) {
				newInference.getPremises().remove(subTree.getAxiom());
				newLemmaSubTree.getSubTrees().add(subTree);
			}
		}
		
		// If not all premises of the inference have been matched, there has been an error and a null is returned.
		if (newInference.getPremises().size() > 0) {
			return null;
		}

		// Remove connections from these subtrees to the root.
		for (ProofTree subTree : newLemmaSubTree.getSubTrees()) {
			subTrees.remove(subTree);
		}
		copiedTree.getSubTrees().add(newLemmaSubTree);							
		return copiedTree;
	}
	
	/*
		END OF COMPUTATION OF COMPLETE PROOF TREES
	 */
}
