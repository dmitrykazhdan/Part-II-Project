package DataSetExtractor;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;



/*
 This class, together with the "ProcessOntology" class parse 
 a set of ontologies in a given directory.
 They then generate an output folder that consists of all
 Explanations generated from all the ontologies.
 An Explanation consists of an entailment and the justifications
 for this entailment.
 */

public class CorpusProcessing {
	
	public static void main(String args[])   {
		
		File corpusDir = new File("/Users/AdminDK/Desktop/TestOntology");
		File outputDir = new File("/Users/AdminDK/Desktop/Explanations");
		
		// Extract all ontologies from the specified directory.
		File[] ontologies = extractOntologyFiles(corpusDir);
		
		// Process all of the extracted ontologies.
		for (File ontology : ontologies) {
			
			String ontologyFileName = ontology.getAbsolutePath();
			
			try {											
				OntologyProcessing processOntology = new OntologyProcessing(ontologyFileName, outputDir.getAbsolutePath());
				processOntology.GenerateExplanations();
				
			} catch (OWLOntologyCreationException e) {				
				System.out.println("Could not process ontology: " + ontologyFileName);
				e.printStackTrace();
				
			} catch (InterruptedException | ExecutionException | IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	
	private static File[] extractOntologyFiles(File corpusDir) {
		
		// Extract all files with ".owl" or ".xml" extension from the specified directory.
		File[] ontologies = corpusDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".owl") || name.endsWith(".xml");
		    }
		});		
		return ontologies;		
	}
}
