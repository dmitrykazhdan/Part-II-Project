package Testing;

import static org.junit.Assert.*;

import java.util.Arrays;
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

import InferenceRules.GenerateRules;
import InferenceRules.InstanceOfRule;
import InferenceRules.RuleString;
import ProofTreeComputation.PartitionWithApplicableInfRules;
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
	
	
	
	@Test
	public void addInferredNodesToTreeTest() {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));
		OWLObjectProperty propertyRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));

		
		// Some sample data to use
		RuleString sampleRule = GenerateRules.getRule("10");
		
		OWLAxiom sampleNode1 = factory.getOWLSubClassOfAxiom(classX, classY);
		OWLAxiom sampleNode2 = factory.getOWLEquivalentClassesAxiom(classY, classY);
		OWLAxiom sampleNode3 = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectSomeValuesFrom(propertyRo, classY));
		
		ProofTree sampleProofTree1 = new ProofTree(sampleNode1, null, null);
		ProofTree sampleProofTree2 = new ProofTree(sampleNode3, Arrays.asList(new ProofTree(sampleNode2, null, null), sampleProofTree1), sampleRule);
		
		
		
		// Test case 1:
		OWLAxiom entailment = factory.getOWLSubClassOfAxiom(classZ, classY);
		OWLAxiom testAxiom1 = factory.getOWLEquivalentClassesAxiom(classX, factory.getOWLObjectAllValuesFrom(propertyRo, classY));
		OWLAxiom testAxiom2_1 = factory.getOWLSubClassOfAxiom(classX, classY); 
		OWLAxiom testAxiom2_2 = factory.getOWLSubClassOfAxiom(classY, classZ);
		OWLAxiom testAxiom3 = factory.getOWLSubClassOfAxiom(classX, classX); 
		
		
		InstanceOfRule instance1 = new InstanceOfRule(Arrays.asList(testAxiom1), null, GenerateRules.getRule("7"));
		InstanceOfRule instance2 = new InstanceOfRule(Arrays.asList(testAxiom2_1, testAxiom2_2), null, GenerateRules.getRule("39"));
		InstanceOfRule instance3 = new InstanceOfRule(Arrays.asList(testAxiom3), null, null);
		PartitionWithApplicableInfRules partitionWithRules = new PartitionWithApplicableInfRules(Arrays.asList(instance1, instance2, instance3));
		
		
		ProofTree tmpTree1 = new ProofTree(testAxiom1, null, null);
		ProofTree tmpTree2 = new ProofTree(testAxiom2_1, null, null);
		ProofTree tmpTree3 = new ProofTree(testAxiom2_2, Arrays.asList(sampleProofTree1, sampleProofTree1), GenerateRules.getRule("45"));
		ProofTree tmpTree4 = new ProofTree(testAxiom3, Arrays.asList(sampleProofTree2),  GenerateRules.getRule("45"));
		ProofTree originalProofTree = new ProofTree(entailment, Arrays.asList(tmpTree1, tmpTree2, tmpTree3, tmpTree4), null);
		
		
		OWLAxiom generatedAxiom1 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectAllValuesFrom(propertyRo, factory.getOWLNothing()), classX);
		OWLAxiom generatedAxiom2 = factory.getOWLSubClassOfAxiom(classX, classZ);
		ProofTree t1 = new ProofTree(generatedAxiom1, Arrays.asList(tmpTree1), GenerateRules.getRule("7"));
		ProofTree t2 = new ProofTree(generatedAxiom2, Arrays.asList(tmpTree2, tmpTree3), GenerateRules.getRule("39"));
	
		ProofTree correctTree = new ProofTree(entailment, Arrays.asList(t1, t2, tmpTree4), null);		
		List<ProofTree> generatedTrees = ProofTreeGenerator.addInferredNodesToTree(originalProofTree, partitionWithRules);
	
		assertTrue(generatedTrees.size() == 1);
		assertTrue(generatedTrees.get(0).equals(correctTree));
	}
	
	
	
}

