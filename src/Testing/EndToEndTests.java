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
import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;


public class EndToEndTests {

	
	@Test
	public void test1() {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();

		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLObjectProperty objPropRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));
		OWLObjectProperty objPropSo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertySo"));
		OWLObjectProperty objPropTo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyTo"));
		
		OWLAxiom ax1 = factory.getOWLSubObjectPropertyOfAxiom(objPropRo, objPropSo);
		OWLAxiom ax2 = factory.getOWLSubObjectPropertyOfAxiom(objPropSo, objPropTo);
		OWLAxiom ax3 = factory.getOWLObjectPropertyRangeAxiom(objPropTo, classX);
		OWLAxiom ax4 = factory.getOWLSubClassOfAxiom(classX, classY);
				
		OWLAxiom entailment = factory.getOWLObjectPropertyRangeAxiom(objPropRo, classY);		
	
		Set<OWLAxiom> justification = new HashSet<OWLAxiom>();
		justification.add(ax1);	
		justification.add(ax2);
		justification.add(ax3);			
		justification.add(ax4);

		Explanation<OWLAxiom> explanation = new Explanation<OWLAxiom>(entailment, justification);
		List<ProofTree> proofTrees = ProofTreeGenerator.generateProofTrees(explanation);		
		assertTrue(proofTrees.size() == 1);
		

		ProofTree leaf1 = new ProofTree(ax1,null,null);
		ProofTree leaf2 = new ProofTree(ax2,null,null);
		ProofTree leaf3 = new ProofTree(ax3,null,null);
		ProofTree leaf4 = new ProofTree(ax4,null,null);
		
		ProofTree i1 = new ProofTree(factory.getOWLSubObjectPropertyOfAxiom(objPropRo, objPropTo), 
									Arrays.asList(leaf1, leaf2), GenerateRules.getRule("26"));

		ProofTree i2 = new ProofTree(factory.getOWLObjectPropertyRangeAxiom(objPropTo, classY), 
				Arrays.asList(leaf3, leaf4), GenerateRules.getRule("32"));

		ProofTree ent = new ProofTree(factory.getOWLObjectPropertyRangeAxiom(objPropRo, classY), 
				Arrays.asList(i1, i2), GenerateRules.getRule("33"));

		
		assertTrue(proofTrees.get(0).equals(ent));
		
	}
	
	
	
	
	@Test
	public void test2() {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();

		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));

		OWLObjectProperty objPropRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));
		
		OWLAxiom ax1 = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectSomeValuesFrom(objPropRo, factory.getOWLThing()));
		OWLAxiom ax2 = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectAllValuesFrom(objPropRo, classY));
		OWLAxiom ax3 = factory.getOWLTransitiveObjectPropertyAxiom(objPropRo);
		OWLAxiom ax4 = factory.getOWLSubClassOfAxiom(classY, factory.getOWLObjectSomeValuesFrom(objPropRo, classZ));
		
		OWLAxiom entailment = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectSomeValuesFrom(objPropRo, classZ))	;
	
		Set<OWLAxiom> justification = new HashSet<OWLAxiom>();
		justification.add(ax1);	
		justification.add(ax2);
		justification.add(ax3);			
		justification.add(ax4);

		Explanation<OWLAxiom> explanation = new Explanation<OWLAxiom>(entailment, justification);
		List<ProofTree> proofTrees = ProofTreeGenerator.generateProofTrees(explanation);		
		
		removeDuplicateTrees(proofTrees);
		assertTrue(proofTrees.size() == 1);
		

		ProofTree leaf1 = new ProofTree(ax3,null,null);
		ProofTree leaf2 = new ProofTree(ax4,null,null);
		ProofTree leaf3 = new ProofTree(ax1,null,null);
		ProofTree leaf4 = new ProofTree(ax2,null,null);
		
		ProofTree i1 = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectSomeValuesFrom(objPropRo, classY)), 
									Arrays.asList(leaf3, leaf4), GenerateRules.getRule("48"));

		ProofTree ent = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectSomeValuesFrom(objPropRo, classZ)), 
				Arrays.asList(leaf1, leaf2, i1), GenerateRules.getRule("55.1"));

		
		assertTrue(proofTrees.get(0).equals(ent));
		
	}
	
	
	@Test
	public void test3() {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();

		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));

		
		OWLAxiom ax1 = factory.getOWLEquivalentClassesAxiom(classX, factory.getOWLObjectIntersectionOf(classY, classZ));
		OWLAxiom ax2 = factory.getOWLDisjointClassesAxiom(classX, classY);
		
		OWLAxiom entailment = factory.getOWLSubClassOfAxiom(classX, factory.getOWLNothing())	;
	
		Set<OWLAxiom> justification = new HashSet<OWLAxiom>();
		justification.add(ax1);	
		justification.add(ax2);

		Explanation<OWLAxiom> explanation = new Explanation<OWLAxiom>(entailment, justification);
		List<ProofTree> proofTrees = ProofTreeGenerator.generateProofTrees(explanation);		
		
		removeDuplicateTrees(proofTrees);
		assertTrue(proofTrees.size() == 1);
		

		ProofTree leaf1 = new ProofTree(ax1,null,null);
		ProofTree leaf2 = new ProofTree(ax2,null,null);
		
		ProofTree i1 = new ProofTree(factory.getOWLSubClassOfAxiom(classX, classY), 
									Arrays.asList(leaf1), GenerateRules.getRule("2.2"));

		ProofTree ent = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLNothing()), 
				Arrays.asList(i1, leaf2), GenerateRules.getRule("15"));

		
		assertTrue(proofTrees.get(0).equals(ent));
		
	}
	
	
	
	@Test
	public void test4() {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();

		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY1 = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY1"));		
		OWLClass classY2 = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY2"));		
		OWLClass classY3 = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY3"));		
		OWLClass classZ1 = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ1"));		
		OWLClass classZ2 = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ2"));		
		OWLClass classZ3 = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ3"));		
		OWLClass classZ4 = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ4"));		
		OWLClass classU = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassU"));		
		OWLClass classV = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassV"));		

		
		OWLAxiom ax1 = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectIntersectionOf(classY1, classY2, classY3));
		OWLAxiom ax2 = factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(classY1, classY2), factory.getOWLObjectIntersectionOf(classZ1, classZ2));
		OWLAxiom ax3 = factory.getOWLSubClassOfAxiom(classU, classZ3);
		OWLAxiom ax4 = factory.getOWLSubClassOfAxiom(classV, classZ3);
		OWLAxiom ax5 = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectUnionOf(classU, classV));

		
		OWLAxiom entailment = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectIntersectionOf(classZ1, classZ3))	;
	
		Set<OWLAxiom> justification = new HashSet<OWLAxiom>();
		justification.add(ax1);	
		justification.add(ax2);
		justification.add(ax3);
		justification.add(ax4);
		justification.add(ax5);
		

		Explanation<OWLAxiom> explanation = new Explanation<OWLAxiom>(entailment, justification);
		List<ProofTree> proofTrees = ProofTreeGenerator.generateProofTrees(explanation);		
		
		removeDuplicateTrees(proofTrees);
		assertTrue(proofTrees.size() == 1);
		

		ProofTree leaf1 = new ProofTree(ax1,null,null);
		ProofTree leaf2 = new ProofTree(ax2,null,null);
		ProofTree leaf3 = new ProofTree(ax4,null,null);
		ProofTree leaf4 = new ProofTree(ax3,null,null);
		ProofTree leaf5 = new ProofTree(ax5,null,null);
		
		ProofTree i1 = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectIntersectionOf(classY1, classY2)), 
									Arrays.asList(leaf1), GenerateRules.getRule("3.1"));

		ProofTree i2 = new ProofTree(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(classY1, classY2), classZ1), 
				Arrays.asList(leaf2), GenerateRules.getRule("3.2"));
		
		ProofTree i3 = new ProofTree(factory.getOWLSubClassOfAxiom(classX, classZ1), 
				Arrays.asList(i1, i2), GenerateRules.getRule("39"));

		ProofTree i4 = new ProofTree(factory.getOWLSubClassOfAxiom(classX, classZ3), 
				Arrays.asList(leaf3, leaf4, leaf5), GenerateRules.getRule("54"));

		ProofTree ent = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectIntersectionOf(classZ1, classZ3)), 
				Arrays.asList(i3, i4), GenerateRules.getRule("40"));

		
		assertTrue(proofTrees.get(0).equals(ent));
		
	}
	
	
	
	
	public static void removeDuplicateTrees(List<ProofTree> proofTrees) {
		
		Set<ProofTree> proofTreeSet = new HashSet<ProofTree>();
		proofTreeSet.addAll(proofTrees);
		proofTrees.clear();
		
		for (ProofTree proofTree : proofTreeSet) {
			if (!proofTrees.contains(proofTree)) {
				proofTrees.add(proofTree);
			}
		}
		
	}
	
	
	
	
}
