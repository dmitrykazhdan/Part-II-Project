
import java.io.File;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;




public class ExtractEntailments {
	
	public static void main(String args[]) throws OWLOntologyCreationException  {
		
		File file = new File("/Users/AdminDK/Desktop/TestOntology/pizza.owl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		
		
		OWLReasoner reasoner = new Reasoner(ontology);
			
		reasoner.precomputeInferences();
		System.out.println(reasoner.getUnsatisfiableClasses());
        System.out.println("Done");
		
		
	}
}
