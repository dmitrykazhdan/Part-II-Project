package Testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.List;
import InferenceRules.GenerateExceptions;
import InferenceRules.GenerateRules;
import InferenceRules.RuleString;
import ProofTreeComputation.ProofTree;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectHasValueImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;

public class ExceptionTests {

	
	@Test
	public void testCase1() throws IOException, OWLOntologyCreationException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLObjectProperty propertyRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));

		
		OWLAxiom correctAxiom = factory.getOWLSubClassOfAxiom(classX, new OWLObjectSomeValuesFromImpl(propertyRo, classY));
		
		// Base Case
		OWLAxiom justificationAxiom = factory.getOWLSubClassOfAxiom(classX, new OWLObjectSomeValuesFromImpl(propertyRo, classY));
		OWLAxiom laconicAxiom = factory.getOWLSubClassOfAxiom(classX, new OWLObjectSomeValuesFromImpl(propertyRo, factory.getOWLThing()));
		ProofTree laconicTree = new ProofTree(laconicAxiom, Arrays.asList( new ProofTree(justificationAxiom, null, null)), null);
		
		ProofTree correctTree = new ProofTree(justificationAxiom, null, null);
		ProofTree exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
		
		
		// Equivalence Case
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, new OWLObjectSomeValuesFromImpl(propertyRo, classY));
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		
		correctTree = new ProofTree(correctAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("1"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
		
		
		// Intersection Case
		OWLObjectIntersectionOf inter = new OWLObjectIntersectionOfImpl(new HashSet<OWLClassExpression>(Arrays.asList(classZ, new OWLObjectSomeValuesFromImpl(propertyRo, classY))));
		justificationAxiom = factory.getOWLSubClassOfAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		
		correctTree = new ProofTree(correctAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("3.2"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
	
		
		// Equivalence + Intersection Case
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		
		correctTree = new ProofTree(correctAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("2.2"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));		
	}
	
	
	
	@Test
	public void testCase2() throws IOException, OWLOntologyCreationException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLObjectProperty propertyRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));
		OWLIndividual individual = factory.getOWLNamedIndividual(IRI.create("urn:absolute:testingOntology#Individual_i1"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));
		
		OWLAxiom correctAxiom = factory.getOWLSubClassOfAxiom(classX, new OWLObjectHasValueImpl(propertyRo, individual));
		
		// Base Case
		OWLAxiom justificationAxiom = factory.getOWLSubClassOfAxiom(classX, new OWLObjectHasValueImpl(propertyRo, individual));
		OWLAxiom laconicAxiom = factory.getOWLSubClassOfAxiom(classX, new OWLObjectSomeValuesFromImpl(propertyRo, factory.getOWLThing()));
		ProofTree laconicTree = new ProofTree(laconicAxiom, Arrays.asList( new ProofTree(justificationAxiom, null, null)), null);
		
		ProofTree correctTree = new ProofTree(justificationAxiom, null, null);
		ProofTree exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
		
		
		// Equivalence Case
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, new OWLObjectHasValueImpl(propertyRo, individual));
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		
		correctTree = new ProofTree(correctAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("1"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
		
		
		// Intersection Case
		OWLObjectIntersectionOf inter = new OWLObjectIntersectionOfImpl(new HashSet<OWLClassExpression>(Arrays.asList(classZ, new OWLObjectHasValueImpl(propertyRo, individual))));
		justificationAxiom = factory.getOWLSubClassOfAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		
		correctTree = new ProofTree(correctAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("3.2"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));

		
		// Equivalence + Intersection Case
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		
		correctTree = new ProofTree(correctAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("2.2"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));		
	}
	
	
	
	@Test
	public void testCase3_1() throws IOException, OWLOntologyCreationException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		int cardinality = 3;
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLObjectProperty propertyRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));
				
		// Base Case
		OWLAxiom justificationAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMinCardinality(cardinality, propertyRo, classY));
		OWLAxiom laconicAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMinCardinality(cardinality, propertyRo, factory.getOWLThing()));
		ProofTree laconicTree = new ProofTree(laconicAxiom, Arrays.asList( new ProofTree(justificationAxiom, null, null)), null);
		
		ProofTree correctTree = new ProofTree(justificationAxiom, null, null);
		ProofTree exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
		
		
		
		// Equivalence Case (ensure it does not work)
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, factory.getOWLObjectMinCardinality(cardinality, propertyRo, classY));
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);		
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(exceptionTree == null);
		
		
		// Intersection Case (ensure it does not work)
		OWLObjectIntersectionOf inter = new OWLObjectIntersectionOfImpl(new HashSet<OWLClassExpression>(Arrays.asList(classZ, factory.getOWLObjectMinCardinality(cardinality, propertyRo, classY))));
		justificationAxiom = factory.getOWLSubClassOfAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);		
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(exceptionTree == null);

		
		// Equivalence + Intersection Case (ensure it does not work)
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(exceptionTree == null);
	}
	
	
	
	@Test
	public void testCase3_2() throws IOException, OWLOntologyCreationException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		int n1 = 5;
		int n2 = 3;
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLObjectProperty propertyRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));
		OWLIndividual individual = factory.getOWLNamedIndividual(IRI.create("urn:absolute:testingOntology#Individual_i1"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));
		
		OWLAxiom correctAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMinCardinality(n2, propertyRo, classY));
		
		// Base Case
		OWLAxiom justificationAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectExactCardinality(n1, propertyRo, classY));
		OWLAxiom laconicAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMinCardinality(n2, propertyRo, factory.getOWLThing()));
		ProofTree laconicTree = new ProofTree(laconicAxiom, Arrays.asList( new ProofTree(justificationAxiom, null, null)), null);
		
		ProofTree correctTree = new ProofTree(correctAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("6.1"));
		ProofTree exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
			
		
		// Equivalence Case
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, factory.getOWLObjectExactCardinality(n1, propertyRo, classY));
		laconicAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMinCardinality(n2, propertyRo, factory.getOWLThing()));
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList( new ProofTree(justificationAxiom, null, null)), null);
		
		ProofTree subTree = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectExactCardinality(n1, propertyRo, classY)), Arrays.asList( new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("1"));
		correctTree = new ProofTree(correctAxiom, Arrays.asList(subTree), GenerateRules.getRule("6.1"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));

		
		// Intersection Case
		OWLObjectIntersectionOf inter = new OWLObjectIntersectionOfImpl(new HashSet<OWLClassExpression>(Arrays.asList(classZ, factory.getOWLObjectExactCardinality(n1, propertyRo, classY))));
		justificationAxiom = factory.getOWLSubClassOfAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		
		subTree = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectExactCardinality(n1, propertyRo, classY)), Arrays.asList( new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("3.2"));
		correctTree = new ProofTree(correctAxiom, Arrays.asList(subTree), GenerateRules.getRule("6.1"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
		
		
			
		// Equivalence + Intersection Case
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		
		subTree = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectExactCardinality(n1, propertyRo, classY)), Arrays.asList( new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("2.2"));
		correctTree = new ProofTree(correctAxiom, Arrays.asList(subTree), GenerateRules.getRule("6.1"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
		
	}
	
	
	
	
	@Test
	public void testCase4_1() throws IOException, OWLOntologyCreationException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		int cardinality = 7;
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLObjectProperty propertyRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));
				
		// Base Case
		OWLAxiom justificationAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMaxCardinality(cardinality, propertyRo, classY));
		OWLAxiom laconicAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMaxCardinality(cardinality, propertyRo, factory.getOWLThing()));
		ProofTree laconicTree = new ProofTree(laconicAxiom, Arrays.asList( new ProofTree(justificationAxiom, null, null)), null);
		
		ProofTree correctTree = new ProofTree(justificationAxiom, null, null);
		ProofTree exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
		
		
		
		// Equivalence Case (ensure it does not work)
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, factory.getOWLObjectMaxCardinality(cardinality, propertyRo, classY));
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);		
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(exceptionTree == null);
		
		
		// Intersection Case (ensure it does not work)
		OWLObjectIntersectionOf inter = new OWLObjectIntersectionOfImpl(new HashSet<OWLClassExpression>(Arrays.asList(classZ, factory.getOWLObjectMaxCardinality(cardinality, propertyRo, classY))));
		justificationAxiom = factory.getOWLSubClassOfAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);		
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(exceptionTree == null);

		
		// Equivalence + Intersection Case (ensure it does not work)
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(exceptionTree == null);
	}

	
	
	@Test
	public void testCase4_2() throws IOException, OWLOntologyCreationException {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		int n = 5;
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLClass classY = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassY"));
		OWLObjectProperty propertyRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));
		OWLClass classZ = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassZ"));
		
		OWLAxiom correctAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMaxCardinality(n, propertyRo, classY));
		
		// Base Case
		OWLAxiom justificationAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectExactCardinality(n, propertyRo, classY));
		OWLAxiom laconicAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMaxCardinality(n, propertyRo, factory.getOWLThing()));
		ProofTree laconicTree = new ProofTree(laconicAxiom, Arrays.asList( new ProofTree(justificationAxiom, null, null)), null);
		
		ProofTree correctTree = new ProofTree(correctAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("6.2"));
		ProofTree exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
			
		
		// Equivalence Case
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, factory.getOWLObjectExactCardinality(n, propertyRo, classY));
		laconicAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectMaxCardinality(n, propertyRo, factory.getOWLThing()));
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList( new ProofTree(justificationAxiom, null, null)), null);
		
		ProofTree subTree = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectExactCardinality(n, propertyRo, classY)), Arrays.asList( new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("1"));
		correctTree = new ProofTree(correctAxiom, Arrays.asList(subTree), GenerateRules.getRule("6.2"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));

		
		// Intersection Case
		OWLObjectIntersectionOf inter = new OWLObjectIntersectionOfImpl(new HashSet<OWLClassExpression>(Arrays.asList(classZ, factory.getOWLObjectExactCardinality(n, propertyRo, classY))));
		justificationAxiom = factory.getOWLSubClassOfAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		
		subTree = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectExactCardinality(n, propertyRo, classY)), Arrays.asList( new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("3.2"));
		correctTree = new ProofTree(correctAxiom, Arrays.asList(subTree), GenerateRules.getRule("6.2"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
		
		
			
		// Equivalence + Intersection Case
		justificationAxiom = factory.getOWLEquivalentClassesAxiom(classX, inter);
		laconicTree = new ProofTree(laconicAxiom, Arrays.asList(new ProofTree(justificationAxiom, null, null)), null);
		
		subTree = new ProofTree(factory.getOWLSubClassOfAxiom(classX, factory.getOWLObjectExactCardinality(n, propertyRo, classY)), Arrays.asList( new ProofTree(justificationAxiom, null, null)), GenerateRules.getRule("2.2"));
		correctTree = new ProofTree(correctAxiom, Arrays.asList(subTree), GenerateRules.getRule("6.2"));
		exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
		
	}
	
	
	
	
	
	@Test
	public void testCase5()  {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLDataProperty propertyRd = factory.getOWLDataProperty(IRI.create("urn:absolute:testingOntology#PropertyRd"));
		OWLLiteral literal = factory.getOWLLiteral("literal", factory.getOWLDatatype(IRI.create("urn:absolute:testingOntology#DatatypeDt")));
		
		OWLAxiom correctAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLDataHasValue(propertyRd, literal));
		
		OWLAxiom justificationAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLDataHasValue(propertyRd, literal));
		OWLAxiom laconicAxiom = factory.getOWLSubClassOfAxiom(classX, factory.getOWLDataHasValue(propertyRd, factory.getOWLLiteral("plain", factory.getRDFPlainLiteral())));
		ProofTree laconicTree = new ProofTree(laconicAxiom, Arrays.asList( new ProofTree(justificationAxiom, null, null)), null);
		
		ProofTree correctTree = new ProofTree(correctAxiom, null, null);
		ProofTree exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
	}

	
	
		
	@Test
	public void testCase6()  {
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		OWLObjectProperty propertyRo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertyRo"));
		OWLObjectProperty propertySo = factory.getOWLObjectProperty(IRI.create("urn:absolute:testingOntology#PropertySo"));
		
		OWLAxiom correctAxiom = factory.getOWLInverseObjectPropertiesAxiom(propertySo, propertyRo);
		
		OWLAxiom justificationAxiom = factory.getOWLInverseObjectPropertiesAxiom(propertyRo, propertySo);
		OWLAxiom laconicAxiom = factory.getOWLSubObjectPropertyOfAxiom(propertyRo, factory.getOWLObjectInverseOf(propertySo));
		ProofTree laconicTree = new ProofTree(laconicAxiom, Arrays.asList( new ProofTree(justificationAxiom, null, null)), null);
		
		ProofTree correctTree = new ProofTree(correctAxiom, null, null);
		ProofTree exceptionTree = GenerateExceptions.matchException(laconicTree);
		assertTrue(correctTree.equals(exceptionTree));
	}

}
