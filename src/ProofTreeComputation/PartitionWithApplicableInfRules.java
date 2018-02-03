package ProofTreeComputation;

import java.util.ArrayList;
import java.util.List;

import InferenceRules.InstanceOfRule;


// Partition with rules that can be applied to its subsets.
public class PartitionWithApplicableInfRules {

	private List<InstanceOfRule> elements;
	
	public PartitionWithApplicableInfRules(List<InstanceOfRule> elements) {
		this.elements = elements;
	}
	
	public PartitionWithApplicableInfRules(PartitionWithApplicableInfRules original) {
		this.elements = new ArrayList<InstanceOfRule>(original.getItems());
	}
	
	public List<InstanceOfRule> getItems() {
		return elements;
	}	
}
