package Testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

import InferenceRules.GenerateRules;
import InferenceRules.RuleString;
import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;

public class RuleTests {

	
	
	@Test
	public void testRule39() throws IOException {
		
		String explanationFilename = "src/TestOntology/Test1.xml";		
		InputStream fileInputStream = new FileInputStream(explanationFilename);
		Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);			
		
		List<OWLAxiom> premises = new ArrayList<OWLAxiom>(explanation.getAxioms());
		RuleString rule39 = GenerateRules.getRule("39");
		
		boolean match = rule39.matchPremises(premises);
		
		assertTrue(match);
	}
	
	
	
	
	@Test
	public void testRule44_1() throws IOException {
		
		String explanationFilename = "src/TestOntology/Test2/Test2_C.xml";		
		InputStream fileInputStream = new FileInputStream(explanationFilename);
		Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);			
		
		List<OWLAxiom> premises = new ArrayList<OWLAxiom>(explanation.getAxioms());
		RuleString rule39 = GenerateRules.getRule("44.1");
		
		boolean match = rule39.matchPremises(premises);
		
		assertTrue(match);
	}

}
