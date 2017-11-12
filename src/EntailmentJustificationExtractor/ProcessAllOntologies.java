package EntailmentJustificationExtractor;
import java.io.File;
import java.io.FilenameFilter;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class ProcessAllOntologies {
	
	public static void main(String args[])   {
		
		File ontologyDir = new File("/Users/AdminDK/Desktop/TestOntology");
		
		File[] ontologies = ontologyDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".owl");
		    }
		});
		
		for (int i = 0; i < ontologies.length; i++) {
			
			String ontName = ontologies[i].getAbsolutePath();
			
			try {								
				ProcessOntology procOnt = new ProcessOntology(ontName);
				
				System.out.println(procOnt.getEntailmentsWithJustifications().toString());
				
			} catch (OWLOntologyCreationException e) {				
				System.out.println("Could not process ontology: " + ontName);
				e.printStackTrace();
			}			
		}
		
	}
}
