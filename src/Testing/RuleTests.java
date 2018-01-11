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
import java.util.Map;

import org.junit.Test;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import InferenceRules.GenerateRules;
import InferenceRules.RuleString;
import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;

public class RuleTests {


	@Test 
	public void testCorrectRuleApplication() throws IOException {
		
		Map<Integer, List<RuleString>> rules = GenerateRules.getRules();
		String testsPath = "src/TestOntology/CorrectApplicationTests/";
		
		
		// Temporary Code
		List<String> tmp = new ArrayList<String>();
//		tmp.add("26");
//		tmp.add("27");
//		tmp.add("28");
//		tmp.add("29");
//		tmp.add("30");
//		tmp.add("31");
//		tmp.add("32");
//		tmp.add("33");
//		tmp.add("34");
//		tmp.add("35");
//		tmp.add("36.1");
//		tmp.add("36.2");
//		tmp.add("36.3");	
		
//		tmp.add("39");	
		
//		tmp.add("43.1");	
//		tmp.add("43.2");
//		tmp.add("43.3");
	
		// End of Temporary Code
		
		
		// Iterate over all of the rules
		for (int premiseNumber : rules.keySet()) {
			for (RuleString rule : rules.get(premiseNumber)) {
				
				// Temporary Code
				if (!tmp.contains(rule.getRuleID())) {
					continue;
				}
				// End of Temporary Code
				
				
				// Compute the path to the rule test data
				String ruleTestFolderName = testsPath + rule.getRuleID() + "/";
				List<OWLAxiom> premises = new ArrayList<OWLAxiom>();
				
				// Add all premises
				for (int i = 1; i <= premiseNumber; i++) {
					String premiseFilename = ruleTestFolderName + "Premise" + i + ".xml";
					premises.addAll(loadPremises(premiseFilename));
				}
				
				assertTrue(rule.matchPremises(premises));
							
				List<OWLAxiom> generatedConclusions = rule.generateConclusions(premises);		
				
				// Add checking for multiple conclusions as well
				assertTrue(generatedConclusions.size() == 1);
												
				OWLAxiom conclusion = loadConclusion(ruleTestFolderName + "Conclusion.xml");
				assertTrue(conclusion.equalsIgnoreAnnotations(generatedConclusions.get(0)));						
			}
		}	
	}
	
	
	
	private List<OWLAxiom> loadPremises(String explanationFilename) throws IOException {
		Explanation<OWLAxiom> explanation = loadExplanation(explanationFilename);
		List<OWLAxiom> premises = new ArrayList<OWLAxiom>(explanation.getAxioms());
		return premises;
	}
	
	private OWLAxiom loadConclusion(String explanationFilename) throws IOException {
		Explanation<OWLAxiom> explanation = loadExplanation(explanationFilename);
		return explanation.getEntailment();
	}
	
	private Explanation<OWLAxiom> loadExplanation(String explanationFilename) throws IOException {
		
		InputStream fileInputStream = new FileInputStream(explanationFilename);
		Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
		fileInputStream.close();
		return explanation;
	}

}
