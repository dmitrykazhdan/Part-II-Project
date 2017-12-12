package ProofTreeComputation;

import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLAxiom;

// A partition is simply a list of lists in this case. 
public class Partition {

	private List<List<OWLAxiom>> elements;
	
	public Partition(List<List<OWLAxiom>> el) {
		elements = el;
	}
	
	public List<List<OWLAxiom>> getElements() {
		return elements;
	}	
	
	// Create copy of the partition.
	// Note that axioms themselves are not copied (not a full deep copy).
	public Partition(Partition partition) {
		
		elements = new ArrayList<List<OWLAxiom>>();
		
		for (List<OWLAxiom> partSubset : partition.elements) {
			List<OWLAxiom> partSubsetCopy = new ArrayList<OWLAxiom>(partSubset);
			elements.add(partSubsetCopy);			
		}		
	}
}
