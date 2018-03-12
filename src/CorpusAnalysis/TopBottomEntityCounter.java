package CorpusAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class TopBottomEntityCounter {

	
	public static void main(String args[]) throws IOException {

		Path justificationFolderPath = Paths.get("/Users/AdminDK/Desktop/Refined (06.03.2018)/NonTrivialComputedExplanations");
		File justificationFiles = new File(justificationFolderPath.toString());

		File[] allFiles = justificationFiles.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});	
		
		int edgeEntityCount = 0;
		
		for (File explanationFile : allFiles) {
			
			InputStream fileInputStream;
			fileInputStream = new FileInputStream(explanationFile.getAbsolutePath());
			Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
			Set<OWLAxiom> justification = explanation.getAxioms();
			
			boolean has = processJustification(justification);
			
			if (has) {
				System.out.println(edgeEntityCount);
				edgeEntityCount++;
			}		
			fileInputStream.close();
		}		
		System.out.println(edgeEntityCount);	
	}

	
	
	private static boolean processJustification(Set<OWLAxiom> justification) {
		
		for (OWLAxiom axiom : justification) {
			
			if (axiom.isLogicalAxiom()) {
				Set<OWLClass> expressions = axiom.getClassesInSignature();	
				
				for (OWLClass cls : expressions) {
					if (cls.isOWLNothing() || cls.isOWLThing()) {
						return true;
					}
				}				
			}		
		}
		return false;
	}
	
	
}
