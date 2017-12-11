package ProofTreeComputation;

import java.util.List;

// Partition with rules that can be applied to the subsets.
public class PartitionWithRules {

	private List<RuleApplication> elements;
	
	public PartitionWithRules(List<RuleApplication> elements) {
		this.elements = elements;
	}
	
	public List<RuleApplication> getItems() {
		return elements;
	}	
}
