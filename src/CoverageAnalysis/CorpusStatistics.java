package CoverageAnalysis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	private Map<Integer, Integer> passedEntailments;
	private Map<Integer, Integer> failedCoverageEntailments;
	private Map<Integer, Integer> failedTimeOutEntailments;
	
	
	public CorpusStatistics() {
		ruleUsageCounts = new HashMap<RuleString, Integer>();
		
		passedEntailments = new HashMap<Integer, Integer>();		
		passedEntailments.put(1, 0);
		passedEntailments.put(2, 0);
		passedEntailments.put(3, 0);
		
		failedCoverageEntailments = new HashMap<Integer, Integer>();		
		failedCoverageEntailments.put(1, 0);
		failedCoverageEntailments.put(2, 0);
		failedCoverageEntailments.put(3, 0);

		failedTimeOutEntailments = new HashMap<Integer, Integer>();		
		failedTimeOutEntailments.put(1, 0);
		failedTimeOutEntailments.put(2, 0);
		failedTimeOutEntailments.put(3, 0);
		
		for (Integer ruleNumber : GenerateRules.getRules().keySet()) {
			for (RuleString rule : GenerateRules.getRules().get(ruleNumber)) {
				ruleUsageCounts.put(rule, 0);
			}
		}
	}
	
	public void updateComputedTreeStatistics(ProofTree proofTree) {
		updateComputedTrees(proofTree.getAxiom());
		updateRuleCounts(proofTree);
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
	
	
	private void updateComputedTrees(OWLAxiom entailment) {
		updateStatistics(getEntailmentType(entailment) , passedEntailments);
	}
	
	public void updateFailsByTimeout(OWLAxiom entailment) {
		updateStatistics(getEntailmentType(entailment), failedTimeOutEntailments );
		failedByTimeout++;
	}
	
	public void updateFailsByRuleCoverage(OWLAxiom entailment) {
		updateStatistics(getEntailmentType(entailment), failedCoverageEntailments);
		failedByRuleCoverage++;
	}
	
	
	private void updateStatistics(int typeCode, Map<Integer, Integer> entailmentCountTypes) {
		entailmentCountTypes.put(typeCode, entailmentCountTypes.get(typeCode) + 1);	
	}
	
	
	public int getEntailmentType(OWLAxiom entailment) {

		int typeCode = 0;
		
		if (entailment instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom subClsAxiom = (OWLSubClassOfAxiom) entailment;
			if (subClsAxiom.getSubClass().isOWLThing()) {
				typeCode = 1;
				
			} else if (subClsAxiom.getSuperClass().isOWLNothing()) {
				typeCode = 2;
				
			} else {
				typeCode = 3;
			}
		}	
		return typeCode;
	}
	
	
	
	public void writeStatisticsToFile(String ouputFilePath) {
		
		PrintWriter writer;
		
		try {
			writer = new PrintWriter(ouputFilePath + "output.txt", "UTF-8");
			writer.println("Total justifications: " + totalJustifications);
			writer.println("Computed Proof Trees: " + computedProofTrees);
			writer.println("Failures by timeout: " + failedByTimeout);
			writer.println("Failures by coverage: " + failedByRuleCoverage );	
			writer.println("");
			writer.println("[Passed, Failed by coverage, Failed by Timeout]");
			writer.println("T <= A  " + passedEntailments.get(1) + ",		" + failedCoverageEntailments.get(1) + ",		" + failedTimeOutEntailments.get(1));
			writer.println("A <= F  " + passedEntailments.get(2) + ",		" + failedCoverageEntailments.get(2) + ",		" + failedTimeOutEntailments.get(2));
			writer.println("A <= B  " + passedEntailments.get(3) + ",		" + failedCoverageEntailments.get(3) + ",		" + failedTimeOutEntailments.get(3));
			writer.println("");
			writer.println("");
			writer.println("Rule Coverage Statistics: ");
			writeSortedRules(writer);					
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
	}
	
	public void incrementTotalComputedTrees() {
		computedProofTrees++;
	}
	
	public void incrementTotalJustifications() {
		totalJustifications++;
	}
		
	public int getTotalJustifications() {
		return totalJustifications;
	}
	
	public int getTotalTreesComputed() {
		return computedProofTrees;
	}
	
	public int getComputedJustifications() {
		return totalJustifications - (failedByTimeout + failedByRuleCoverage);
	}
	
	
	 private void writeSortedRules(PrintWriter writer) { 
		 
	        Set<Entry<RuleString, Integer>> set = ruleUsageCounts.entrySet();
	        
	        List<Entry<RuleString, Integer>> list = new ArrayList<Entry<RuleString, Integer>>(set);
	        Collections.sort( list, new Comparator<Map.Entry<RuleString, Integer>>()
	        {
	            public int compare( Map.Entry<RuleString, Integer> o1, Map.Entry<RuleString, Integer> o2 )
	            {
	                return (o2.getValue()).compareTo( o1.getValue() );
	            }
	        } );
	        
	        for(Map.Entry<RuleString, Integer> entry:list){
				writer.println("Rule ID: " + entry.getKey().getRuleID() + " Rule Name: " + entry.getKey().getRuleName() + " count: " + entry.getValue());
	        }
	 }
}

