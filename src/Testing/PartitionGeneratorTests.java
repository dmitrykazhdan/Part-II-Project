package Testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import ProofTreeComputation.Partition;
import ProofTreeComputation.PartitionGenerator;

public class PartitionGeneratorTests {

	
	@Test
	public void generateAllPartitionsTests() {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));
		
		OWLAxiom axiom1 = factory.getOWLSubClassOfAxiom(classX, classY);
		OWLAxiom axiom2 =factory.getOWLSubClassOfAxiom(classY, classZ);
		OWLAxiom axiom3 = factory.getOWLSubClassOfAxiom(classZ, classX);
		
		
		List<OWLAxiom> axioms = new ArrayList<OWLAxiom>();
		axioms.add(axiom1);
		axioms.add(axiom2);
		axioms.add(axiom3);
		
		List<Partition> partitions = new ArrayList<Partition>();
		partitions.add(new Partition(Arrays.asList(Arrays.asList(axiom1), Arrays.asList(axiom2), Arrays.asList(axiom3))));
		partitions.add(new Partition(Arrays.asList(Arrays.asList(axiom1, axiom2), Arrays.asList(axiom3))));
		partitions.add(new Partition(Arrays.asList(Arrays.asList(axiom1, axiom3), Arrays.asList(axiom2))));
		partitions.add(new Partition(Arrays.asList(Arrays.asList(axiom1), Arrays.asList(axiom2, axiom3))));
		partitions.add(new Partition(Arrays.asList(Arrays.asList(axiom1, axiom2, axiom3))));
		
		List<Partition> generatedPartitions = PartitionGenerator.generateAllPartitions(axioms);
		assertTrue(containSameElements(generatedPartitions, partitions));
		
		
		// Case with one element
		axioms = new ArrayList<OWLAxiom>();
		axioms.add(axiom1);
		partitions = new ArrayList<Partition>();
		partitions.add(new Partition(Arrays.asList(Arrays.asList(axiom1))));
		
		generatedPartitions = PartitionGenerator.generateAllPartitions(axioms);
		assertTrue(containSameElements(generatedPartitions, partitions));
	}
	
	
	private boolean containSameElements(List<Partition> list1, List<Partition> list2) {
		
		if (list1.size() != list2.size()) {
			return false;
		}
		
		for (Partition partition : list1) {
			if (!containsPartition(list2, partition)) {
				return false;
			}
		}
		return true;		
	}
	
	
	private boolean containsPartition(List<Partition> list, Partition p) {
	
		for (Partition partition : list) {
			if (samePartition(partition, p)) {
				return true;
			}
		}		
		return false;
	}
	
	private boolean samePartition(Partition p1, Partition p2) {
		
		if (p1.getElements().size() != p2.getElements().size()) {
			return false;
		}
		boolean contains = false;
		
		for (List<OWLAxiom> subSet : p1.getElements()) {
			contains = false;
			Set<OWLAxiom> s1 = new HashSet<OWLAxiom>(subSet);
			
			for (List<OWLAxiom> secondSubSet : p2.getElements()) {
					
				Set<OWLAxiom> s2 = new HashSet<OWLAxiom>(secondSubSet);
				
				if (s1.equals(s2)) {
					contains = true;
					break;
				}				
			}
			
			if (!contains) {
				return false;
			}
		}
		return true;
	}
}
