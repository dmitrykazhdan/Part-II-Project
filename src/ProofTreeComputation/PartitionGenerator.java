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
			
			atLeastOneApplication = false;
			for (List<OWLAxiom> subSet : partition.getElements()) {
				
				
				
				
			}			
		}
			
		return allRuleApplications;
	}
	
	
	
	public static List<Partition> generateAllPartitions(List<OWLAxiom> nodes) {
		
		List<Partition> allPartitions = new ArrayList<Partition>();
		
		if (nodes.size() == 0) {
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
