package CoverageAnalysis;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import InferenceRules.GenerateRules;
import InferenceRules.RuleString;
import ProofTreeComputation.ProofTree;

public class CorpusStatistics {

	private int totalJustifications = 0;
	private int computedProofTrees = 0;
	private int failedByTimeout = 0;
	private int failedByRuleCoverage = 0;
	private Map<RuleString, Integer> ruleUsageCounts;
	private Map<Integer, Integer> entailmentCountTypes;
	
	
	public CorpusStatistics() {
		ruleUsageCounts = new HashMap<RuleString, Integer>();
		entailmentCountTypes = new HashMap<Integer, Integer>();
		
		entailmentCountTypes.put(1, 0);
		entailmentCountTypes.put(2, 0);
		entailmentCountTypes.put(3, 0);
		
		for (Integer ruleNumber : GenerateRules.getRules().keySet()) {
			for (RuleString rule : GenerateRules.getRules().get(ruleNumber)) {
				ruleUsageCounts.put(rule, 0);
			}
		}
	}
	
	public void updateStatistics(ProofTree proofTree) {
		updateEntailmentTypeCounts(proofTree);
		updateRuleCounts(proofTree);
	}
	
	private void updateEntailmentTypeCounts(ProofTree proofTree) {
		
		OWLAxiom entailment = proofTree.getAxiom();
		int typeCode = 0;
		
		if (entailment instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom subClsAxiom = (OWLSubClassOfAxiom) entailment;
			if (subClsAxiom.getSubClass().isOWLThing()) {
				typeCode = 0;
				
			} else if (subClsAxiom.getSuperClass().isOWLNothing()) {
				typeCode = 1;
				
			} else {
				typeCode = 2;
			}
		}	
		entailmentCountTypes.put(typeCode, entailmentCountTypes.get(typeCode) + 1);	
	}
	
	private void updateRuleCounts(ProofTree proofTree) {
		
		if (proofTree.getSubTrees() != null) {
			RuleString rule = proofTree.getInferenceRule();
			ruleUsageCounts.put(rule, ruleUsageCounts.get(rule) + 1);
			for (ProofTree subTree : proofTree.getSubTrees()) {
				updateRuleCounts(subTree);
			}
		}
	}
	
	public void writeStatisticsToFile() {
		
	}
	
	public void incrementTotalComputedTrees() {
		computedProofTrees++;
	}
	
	public void incrementTotalJustifications() {
		totalJustifications++;
	}
	
	public void incrementFailsByTimeout() {
		failedByTimeout++;
	}
	
	public void incrementFailsByRuleCoverage() {
		failedByRuleCoverage++;
	}
	
	public int getTotalJustifications() {
		return totalJustifications;
	}
	
	public int getTotalTreesComputed() {
		return computedProofTrees;
	}
}

