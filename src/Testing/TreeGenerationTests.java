package Testing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;

public class TreeGenerationTests {

	@Test
	public void testEmptyJustification() {
				
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		
		OWLAxiom entailment = factory.getOWLSubClassOfAxiom(classX, classY);		
		Set<OWLAxiom> justification = new HashSet<OWLAxiom>();
		Explanation<OWLAxiom> explanation = new Explanation<OWLAxiom>(entailment, justification);
		List<ProofTree> proofTrees = ProofTreeGenerator.generateProofTrees(explanation);
		
		assertTrue(proofTrees == null);
	}
	
	
	
	@Test
	public void testJustificationContainingConclusion() {
				
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));
		
		OWLAxiom entailment = factory.getOWLSubClassOfAxiom(classX, classY);		
		Set<OWLAxiom> justification = new HashSet<OWLAxiom>();
		justification.add(factory.getOWLSubClassOfAxiom(classX, classY));
		
		Explanation<OWLAxiom> explanation = new Explanation<OWLAxiom>(entailment, justification);
		List<ProofTree> proofTrees = ProofTreeGenerator.generateProofTrees(explanation);		
		assertTrue(proofTrees == null);
		
		justification.add(factory.getOWLSubClassOfAxiom(classY, classZ));
		explanation = new Explanation<OWLAxiom>(entailment, justification);
		proofTrees = ProofTreeGenerator.generateProofTrees(explanation);		
		assertTrue(proofTrees == null);
	}
	
	
	@Test
	public void testJustificationOfSizeOne() {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLObjectProperty propertyRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));
		
		Set<OWLAxiom> justification = new HashSet<OWLAxiom>();
		justification.add(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectExactCardinality(5, propertyRo, classY)));
		OWLAxiom entailment = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMinCardinality(3, propertyRo, classY));	
		Explanation<OWLAxiom> explanation = new Explanation<OWLAxiom>(entailment, justification);
		List<ProofTree> proofTrees = ProofTreeGenerator.generateProofTrees(explanation);		
				
		assertTrue(proofTrees != null);		
	}
	
	
	
	
	
}

