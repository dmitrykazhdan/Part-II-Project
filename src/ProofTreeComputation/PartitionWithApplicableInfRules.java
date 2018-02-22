package ProofTreeComputation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

import InferenceRules.InstanceOfRule;


// Partition with rules that can be applied to its subsets.
public class PartitionWithApplicableInfRules {

	private List<InstanceOfRule> elements;
	
	public PartitionWithApplicableInfRules(List<InstanceOfRule> elements) {
		this.elements = elements;
	}
	
	public PartitionWithApplicableInfRules(InstanceOfRule... elements) {
		this.elements = Arrays.asList(elements);
	}
	
	public PartitionWithApplicableInfRules(PartitionWithApplicableInfRules original) {
		this.elements = new ArrayList<InstanceOfRule>();
		
		for (InstanceOfRule instanceOfRule : original.getItems()) {
			elements.add(new InstanceOfRule(instanceOfRule));
		}		
	}
	
	public List<InstanceOfRule> getItems() {
		return elements;
	}	
	
	public List<OWLAxiom> getAllPremiseNodes() {
		
		List<OWLAxiom> allPremises = new ArrayList<OWLAxiom>();
		
		for (InstanceOfRule subset : elements) {
			allPremises.addAll(subset.getPremises());
		}
		return allPremises;
	}
}
