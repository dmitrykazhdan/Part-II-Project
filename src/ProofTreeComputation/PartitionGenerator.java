package ProofTreeComputation;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

import InferenceRules.InstanceOfRule;
import InferenceRules.RuleFinder;
import InferenceRules.RuleString;


public class PartitionGenerator {
	
	public static List<PartitionWithApplicableInfRules> generateAllPartitionsWithRules(List<OWLAxiom> nodes) {
		
		List<Partition> partitions = generateAllPartitions(nodes);
		List<PartitionWithApplicableInfRules> allRuleApplications = new ArrayList<PartitionWithApplicableInfRules>();
		
		boolean atLeastOneApplication = false;
			
		for (Partition partition : partitions) {
			
			List<PartitionWithApplicableInfRules> newPartitionsWithRules = new ArrayList<PartitionWithApplicableInfRules>();
			newPartitionsWithRules.add(new PartitionWithApplicableInfRules(new ArrayList<InstanceOfRule>()));
			atLeastOneApplication = false;
			
			// For now, we apply as much rules as we can to a partition.
			// Hence if we have [(N1, N2, R1), (N3, N4, R2), (N5, R3)],
			// we apply all rules and return only that combination, instead
			// of returning: [(N1, N2), (N3, N4, R2), (N5, R3)],
			//				 [(N1, N2, R1), (N3, N4), (N5, R3)]	
			//				 [(N1, N2, R1), (N3, N4, R2), (N5)]				
			//				 ... etc.				
			for (List<OWLAxiom> subSet : partition.getElements()) {
				
				List<RuleString> applicableRules = RuleFinder.findRuleAppWithoutConclusion(subSet);
				
				// If there is no applicable rule, simply add the subset without a conclusion to all
				// partitions computed so far.
				if (applicableRules == null || applicableRules.size() == 0) {
					
					for (PartitionWithApplicableInfRules partialPartition : newPartitionsWithRules) {
						InstanceOfRule emptyConclusionSubSet = new InstanceOfRule(subSet, null, null);					
						partialPartition.getItems().add(emptyConclusionSubSet);
					}				
					continue;
				}
				
				atLeastOneApplication = true;
				List<PartitionWithApplicableInfRules> oldPartitionsWithRules = newPartitionsWithRules;		
				newPartitionsWithRules = new ArrayList<PartitionWithApplicableInfRules>();
				
				for (RuleString applicableRule : applicableRules) {
					
					for (PartitionWithApplicableInfRules newPartitionWithRules : oldPartitionsWithRules) {
						
						PartitionWithApplicableInfRules copiedPartition = new PartitionWithApplicableInfRules(newPartitionWithRules);
						InstanceOfRule ruleApplication = new InstanceOfRule(subSet, null, applicableRule);
						copiedPartition.getItems().add(ruleApplication);
						newPartitionsWithRules.add(copiedPartition);
					}
				}			
			}	
			
			if (atLeastOneApplication) {
				allRuleApplications.addAll(newPartitionsWithRules);
			}
		}		
		return allRuleApplications;
	}
	
	
	
	public static List<Partition> generateAllPartitions(List<OWLAxiom> nodes) {
		
		List<Partition> allPartitions = new ArrayList<Partition>();
		
		if (nodes.size() == 0) {
			Partition emptyPartition = new Partition(new ArrayList<List<OWLAxiom>>());
			allPartitions.add(emptyPartition);
			return allPartitions;
		}
				
		OWLAxiom node = nodes.get(0);
		List<Partition> partitionOfSublist = generateAllPartitions(nodes.subList(1, nodes.size()));
		
		for (Partition partition : partitionOfSublist) {
						
			List<OWLAxiom> newSubset = new ArrayList<OWLAxiom>();
			newSubset.add(node);
			
			Partition newPartition = new Partition(partition);
			newPartition.getElements().add(newSubset);
			allPartitions.add(newPartition);
			
			for (List<OWLAxiom> subSet : partition.getElements()) {
				
				List<OWLAxiom> newSubSet = new ArrayList<OWLAxiom>(subSet);
				newSubSet.add(node);
				
				newPartition = new Partition(partition);
				newPartition.getElements().remove(subSet);
				newPartition.getElements().add(newSubSet);
				allPartitions.add(newPartition);				
			}		
		}				
		return allPartitions;
	}
}
