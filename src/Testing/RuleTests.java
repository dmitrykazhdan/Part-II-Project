package Testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.junit.Test;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import InferenceRules.GenerateExceptions;
import InferenceRules.GenerateRules;
import InferenceRules.RuleString;
import ProofTreeComputation.ProofTree;

public class RuleTests {


	@Test 
	public void testCorrectRuleApplication() throws IOException {

		Map<Integer, List<RuleString>> rules = GenerateRules.getRules();
		String testsPath = "src/TestOntology/CorrectApplicationTests/";

		// Iterate over all of the rules
		for (int premiseNumber : rules.keySet()) {
			for (RuleString rule : rules.get(premiseNumber)) {

				// Compute the path to the rule test data
				String ruleTestFolderName = testsPath + rule.getRuleID() + "/";
				List<OWLAxiom> premises = new ArrayList<OWLAxiom>();

				// Add all premises
				for (int i = 1; i <= premiseNumber; i++) {
					String premiseFilename = ruleTestFolderName + "Premise" + i + ".xml";
					premises.addAll(TestDataLoader.loadPremises(premiseFilename));
				}			
				System.out.println(rule.getRuleID());
				assertTrue(rule.matchPremises(premises));

				Set<OWLAxiom> generatedConclusions = new HashSet<OWLAxiom>(rule.generateConclusions(premises));						
				File testFolderDir = new File(ruleTestFolderName);
				File[] conclusionFiles = testFolderDir.listFiles((FileFilter) new PrefixFileFilter("Conclusion", IOCase.INSENSITIVE));
				Set<OWLAxiom> conclusions = new HashSet<OWLAxiom>();

				for (File file : conclusionFiles) {
					conclusions.add(TestDataLoader.loadConclusion(file.getAbsolutePath()));
				}	

				for (OWLAxiom conclusion : conclusions) {
					assertTrue(rule.matchPremisesAndConclusion(premises, conclusion));
				}			
				assertTrue(conclusions.equals(generatedConclusions));						
			}
		}	
	}




	@Test
	public void testDisjointDataRangeRules() {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();

		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLDataProperty propertyRd = factory.getOWLDataProperty(IRI.create("urn:absolute:testingOntology#PropertyRd"));

		// Check for disjoint, built-in data ranges.
		OWLAxiom premise1 = factory.getOWLSubClassOfAxiom(classX, factory.getOWLDataSomeValuesFrom(propertyRd, factory.getIntegerOWLDatatype()));
		OWLAxiom premise2 = factory.getOWLDataPropertyRangeAxiom(propertyRd, factory.getBooleanOWLDatatype());					
		assertTrue(GenerateRules.getRule("12.1").matchPremises(Arrays.asList(premise1, premise2)));


		// Check for non-disjoint, built-in data ranges.
		premise2 = factory.getOWLDataPropertyRangeAxiom(propertyRd, factory.getIntegerOWLDatatype());		
		assertTrue(!GenerateRules.getRule("12.1").matchPremises(Arrays.asList(premise1, premise2)));


		// Check for user-defined data ranges.
		premise2 = factory.getOWLDataPropertyRangeAxiom(propertyRd, factory.getOWLDatatype(IRI.create("urn:absolute:testingOntology#DatatypeDt")));		
		assertTrue(!GenerateRules.getRule("12.1").matchPremises(Arrays.asList(premise1, premise2)));	
	}



	@Test
	public void testUnequalLiteralRules() {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();

		OWLClass classX = factory.getOWLClass(IRI.create("urn:absolute:testingOntology#ClassX"));		
		OWLDataProperty propertyRd = factory.getOWLDataProperty(IRI.create("urn:absolute:testingOntology#PropertyRd"));
		OWLLiteral literal1 = factory.getOWLLiteral("literal1", factory.getOWLDatatype(IRI.create("urn:absolute:testingOntology#DatatypeDt")));
		OWLLiteral literal2 = factory.getOWLLiteral("literal2", factory.getOWLDatatype(IRI.create("urn:absolute:testingOntology#DatatypeDt")));


		// Check when literals are equal.
		OWLAxiom premise1 = factory.getOWLSubClassOfAxiom(classX, factory.getOWLDataHasValue(propertyRd, literal1));
		OWLAxiom premise2 = factory.getOWLSubClassOfAxiom(classX, factory.getOWLDataHasValue(propertyRd, literal2));
		OWLAxiom premise3 = factory.getOWLFunctionalDataPropertyAxiom(propertyRd);			
		assertTrue(!GenerateRules.getRule("56.1").matchPremises(Arrays.asList(premise1, premise2, premise3)));
		assertTrue(GenerateRules.getRule("56.2").matchPremises(Arrays.asList(premise1, premise2, premise3)));


		// Check when literals are unequal.
		premise2 = factory.getOWLSubClassOfAxiom(classX, factory.getOWLDataHasValue(propertyRd, literal1));
		assertTrue(!GenerateRules.getRule("56.1").matchPremises(Arrays.asList(premise1, premise2)));
		assertTrue(!GenerateRules.getRule("56.2").matchPremises(Arrays.asList(premise1, premise2)));
	}
}
