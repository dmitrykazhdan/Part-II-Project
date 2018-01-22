package Testing;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		
		
		// Temporary Code
		List<String> tmp = new ArrayList<String>();


// TBC:
		
		// 1, 2, 4
//		tmp.add("3.1");
//		tmp.add("3.2");		
//		tmp.add("5.1");
//		tmp.add("5.2");
//		tmp.add("6.1");
//		tmp.add("6.2");
//		tmp.add("6.3");
// Rules 8 and 9 cannot be generated yet.
		
//		tmp.add("7");
//		tmp.add("10");
//		tmp.add("11");		
//		tmp.add("12.1");
//		tmp.add("12.2");		
//		tmp.add("13.1");
//		tmp.add("13.2");
//		tmp.add("14.1");
//		tmp.add("14.2");
//		tmp.add("15");
//		tmp.add("16");
//		tmp.add("17.1");
//		tmp.add("17.2");		
//		tmp.add("18.1");
//		tmp.add("18.2");
//		tmp.add("19.1");
//		tmp.add("19.2");		
//		tmp.add("20.1");
//		tmp.add("20.2");
//		tmp.add("20.3");
//	    tmp.add("21.1");
//		tmp.add("21.2");
//		tmp.add("21.3");
//		tmp.add("22.1");
//		tmp.add("22.2");
//		tmp.add("22.3");		
//		tmp.add("23");
//		tmp.add("24");
//		tmp.add("25.1");
//		tmp.add("25.2");
		tmp.add("26");
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
//		tmp.add("37.1");
//		tmp.add("37.2");
//		tmp.add("37.3");
//		tmp.add("38.1");
//		tmp.add("38.2");
//		tmp.add("38.3");
//		tmp.add("39");	
//		tmp.add("40");
//		tmp.add("41.1");	
//		tmp.add("41.2");	
//		tmp.add("41.3");	
//		tmp.add("42.1");
//		tmp.add("42.2");	
//		tmp.add("42.3");
//		tmp.add("43.1");	
//		tmp.add("43.2");
//		tmp.add("43.3");	
//		tmp.add("44.1");
//		tmp.add("44.2");
//		tmp.add("44.3");
//		tmp.add("45");
//		tmp.add("46");
//		tmp.add("47");
//		tmp.add("48");
//		tmp.add("49.1");
//		tmp.add("49.2");
//		tmp.add("50");
//		tmp.add("51");
//		tmp.add("52");
//		tmp.add("53");
//		tmp.add("54");
//		tmp.add("55.1");
//		tmp.add("55.2");
//		tmp.add("56");
//		tmp.add("57");
		
		
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
				
				System.out.println(rule.getRuleID());
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
