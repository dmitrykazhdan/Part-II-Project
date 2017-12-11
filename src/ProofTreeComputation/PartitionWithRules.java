package ProofTreeComputation;

import java.util.List;

// Partition with rules that can be applied to the subsets.
public class PartitionWithRules {

	private List<RuleWithAxioms> elements;
	
	public PartitionWithRules(List<RuleWithAxioms> elements) {
		this.elements = elements;
	}
	
	public List<RuleWithAxioms> getItems() {
		return elements;
	}	
}
