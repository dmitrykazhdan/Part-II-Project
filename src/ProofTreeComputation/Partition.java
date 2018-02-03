package ProofTreeComputation;

import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLAxiom;

// A partition of a set of owl axioms.
// Here a partition is represented as a list of lists.
public class Partition {

	private List<List<OWLAxiom>> elements;
	
	public Partition(List<List<OWLAxiom>> elements) {
		this.elements = elements;
	}
	
	public List<List<OWLAxiom>> getElements() {
		return elements;
	}	
	
	// Create copy of the partition.
	// Note that axioms themselves are not copied (a shallow copy).
	public Partition(Partition partition) {
		
		elements = new ArrayList<List<OWLAxiom>>();
		
		for (List<OWLAxiom> partitionSubset : partition.elements) {
			List<OWLAxiom> partitionSubsetCopy = new ArrayList<OWLAxiom>(partitionSubset);
			elements.add(partitionSubsetCopy);			
		}		
	}
}
