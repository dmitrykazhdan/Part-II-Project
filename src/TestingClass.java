
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import InferenceRules.GenerateRules;
import InferenceRules.RuleFinder;
import InferenceRules.RuleString;
import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;

public class TestingClass {
	
	public static void main(String args[]) throws IOException   {

		String explanationDirName = "/Users/AdminDK/Desktop/TestExplanations/";
		File explanationDir = new File(explanationDirName);
		
		File[] explanationFiles = explanationDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".xml");
		    }
		});
		
		
		for (int i = 0; i < explanationFiles.length; i++) {
			
			String explanationFilename = explanationFiles[i].getAbsolutePath();		
			InputStream fileInputStream = new FileInputStream(explanationFilename);

			Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);			
			
//			List<OWLAxiom> premises = new ArrayList<OWLAxiom>(explanation.getAxioms());
//			RuleString rule39 = GenerateRules.getRule("39");
//			
//			boolean m = rule39.matchPremises(premises);
			
			ProofTree proofTree = ProofTreeGenerator.GenerateProofTree(explanation);
			
			System.out.println("Proof Tree computed successfully.");
		}	
	}
	
}
