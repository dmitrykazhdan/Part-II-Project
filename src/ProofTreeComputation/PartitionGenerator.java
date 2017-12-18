package ProofTreeComputation;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

public class PartitionGenerator {
	
	public static List<PartitionWithRules> generateAllPartitionsWithRules(List<OWLAxiom> nodes) {
		
		List<Partition> partitions = generateAllPartitions(nodes);
		List<PartitionWithRules> allRuleApplications = new ArrayList<PartitionWithRules>();
		
		boolean atLeastOneApplication = false;
			
		for (Partition partition : partitions) {
			
			PartitionWithRules newPartitionWithRules = new PartitionWithRules(new ArrayList<RuleApplication>());
			atLeastOneApplication = false;
			
			// For now, we apply as much rules as we can to a partition.
			// Hence if we have [(N1, N2, R1), (N3, N4, R2), (N5, R3)],
			// we apply all rules and return only that combination, instead
			// of returning: [(N1, N2), (N3, N4, R2), (N5, R3)],
			//				 [(N1, N2, R1), (N3, N4), (N5, R3)]	
			//				 [(N1, N2, R1), (N3, N4, R2), (N5)]				
			//				 ... etc.				
			for (List<OWLAxiom> subSet : partition.getElements()) {
				
				InferenceRule applicableRule = RuleFinder.findRuleAppWithoutConclusion(subSet);
				
				if (applicableRule != null) {
					atLeastOneApplication = true;
				}
				
				RuleApplication ruleApplication = new RuleApplication(subSet, null, applicableRule);
				newPartitionWithRules.getItems().add(ruleApplication);
			}	
			
			if (atLeastOneApplication) {
				allRuleApplications.add(newPartitionWithRules);
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
