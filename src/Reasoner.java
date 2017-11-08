
import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;




public class Reasoner {
	
	public static void main(String args[]) throws OWLOntologyCreationException  {
		
		File file = new File("/Users/AdminDK/Desktop/TestOntology/pizza.owl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		
		System.out.println(ontology.toString());
	}
}
