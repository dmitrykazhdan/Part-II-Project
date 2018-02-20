package DataSetExtractor;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;



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
		
		String corpusDirStr = "";
		String outputDirStr = "";
		
		if (args.length == 2) {
			corpusDirStr = args[0];
			outputDirStr = args[1];
			
		} else {		
			System.out.println("Input the following arguments: ");
			System.out.println("1) Path to folder containing the ontology corpus.");
			System.out.println("2) Path to folder where the (entailment, justification) data should be placed.");
			return;
		}
		
		File corpusDir = new File(corpusDirStr);
		File outputDir = new File(outputDirStr);
		
		// Extract all ontologies from the specified directory.
		File[] ontologies = extractOntologyFiles(corpusDir);
		
		// Process all of the extracted ontologies.
		for (File ontology : ontologies) {
			
			System.out.println("Processing ontology: " + ontology.getName() + " Thread count: " + Thread.activeCount());
			
			try {											
				OntologyProcessing processOntology = new OntologyProcessing(ontology, outputDir);
				processOntology.GenerateExplanations();
				
			} catch (OWLOntologyCreationException | InconsistentOntologyException | IllegalArgumentException | InterruptedException | ExecutionException | IOException e) {				
				System.out.println("Could not process ontology: " + ontology.getName());
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
