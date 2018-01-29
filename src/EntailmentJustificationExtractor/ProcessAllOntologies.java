package EntailmentJustificationExtractor;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;



/*
 This class, together with the "ProcessOntology" class parse 
 a set of ontologies in a given directory.
 They then generate an output file that consists of all
 Explanations generated from all the ontologies.
 An Explanation consists of an entailment and the justifications
 for this entailment.
 */

public class ProcessAllOntologies {
	
	public static void main(String args[])   {
		
		File ontologyDir = new File("/Users/AdminDK/Desktop/TestOntology");
		String outputDir = "/Users/AdminDK/Desktop/Explanations/";
		
		// Extract all files with ".owl" extension from the specified directory.
		File[] ontologies = ontologyDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".owl") || name.endsWith(".xml");
		    }
		});
		
		
		// Process all of the extracted ontologies.
		for (int i = 0; i < ontologies.length; i++) {
			
			String ontName = ontologies[i].getAbsolutePath();
			
			try {											
				ProcessOntology.GenerateExplanations(ontName, outputDir);
					
			} catch (OWLOntologyCreationException e) {				
				System.out.println("Could not process ontology: " + ontName);
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
	}
}
