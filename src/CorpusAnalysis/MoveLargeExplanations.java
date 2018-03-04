package CorpusAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.nio.file.Files;

public class MoveLargeExplanations {

	public static void main(String args[]) throws IOException {
		
		Path explanationDirPath = Paths.get("/Users/AdminDK/Desktop/Explanations");
		Path largeExplanationsDirPath = Paths.get("/Users/AdminDK/Desktop/LargeExplanations");
				
		File explanationsDir = new File(explanationDirPath.toString());
				
		File[] explanations = explanationsDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".xml");
		    }
		});	
		InputStream fileInputStream;

		for (File explanationFile : explanations) {
			
			fileInputStream = new FileInputStream(explanationFile.getAbsolutePath());
			Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
			Set<OWLAxiom> justification = explanation.getAxioms();
			
			if (countLogicalAxioms(justification) >= 6) {
				Files.move(explanationFile.toPath(), largeExplanationsDirPath.resolve(explanationFile.getName()),  java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Moving...");
			}	
		}	
	}
	
	
	
	private static int countLogicalAxioms(Set<OWLAxiom> justification) {
		
		int logicalCount = 0;
		
		for (OWLAxiom ax : justification) {
			if (ax.isLogicalAxiom()){
				logicalCount++;
			}
		}
		return logicalCount;
	}
}
