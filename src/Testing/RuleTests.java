package Testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.junit.Test;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;
import InferenceRules.GenerateRules;
import InferenceRules.RuleString;

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
					premises.addAll(loadPremises(premiseFilename));
				}
				
				System.out.println(rule.getRuleID());
				assertTrue(rule.matchPremises(premises));
							
				Set<OWLAxiom> generatedConclusions = new HashSet<OWLAxiom>(rule.generateConclusions(premises));		
				
				File testFolderDir = new File(ruleTestFolderName);
				File[] conclusionFiles = testFolderDir.listFiles((FileFilter) new PrefixFileFilter("Conclusion", IOCase.INSENSITIVE));
				Set<OWLAxiom> conclusions = new HashSet<OWLAxiom>();
				
				for (File file : conclusionFiles) {
					conclusions.add(loadConclusion(file.getAbsolutePath()));
				}	
				// Potential: 2, 3, 4, 5, 21, 22
				for (OWLAxiom conclusion : conclusions) {
					if (!rule.matchPremisesAndConclusion(premises, conclusion)) {
						System.out.println("Could not match rule:" + rule.getRuleID());
					}
				}
				
				assertTrue(conclusions.equals(generatedConclusions));						
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
