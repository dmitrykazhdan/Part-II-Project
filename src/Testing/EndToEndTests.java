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
