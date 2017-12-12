package CoverageAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;

public class GenerateTrees {
	
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
			ProofTree proofTree = ProofTreeGenerator.GenerateProofTree(explanation);
			
			System.out.println("Proof Tree computed successfully.");
					
		}	
	}
	
}
