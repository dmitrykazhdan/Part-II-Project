package ProofTreeComputation;

import java.util.ArrayList;
import java.util.List;

import InferenceRules.InstanceOfRule;


// Partition with rules that can be applied to the subsets.
public class PartitionWithRules {

	private List<InstanceOfRule> elements;
	
	public PartitionWithRules(List<InstanceOfRule> elements) {
		this.elements = elements;
	}
	
	public List<InstanceOfRule> getItems() {
		return elements;
	}	
}
