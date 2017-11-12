import java.io.File;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;



public class ExtractEntailments {
	
	public static void main(String args[]) throws OWLOntologyCreationException  {
		
		// Add FileNotFound Exception
		File file = new File("/Users/AdminDK/Desktop/TestOntology/testOnt.owl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
		
		
		// Usage of the reasoner:
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		
		reasoner.precomputeInferences();
		
		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		OWLClass everything = dataFactory.getOWLThing();
		OWLClass NonVegetarianPizza = dataFactory.getOWLClass(IRI.create("http://www.pizza.com/ontologies/pizza.owl#NonVegetarianPizza"));
		
		NodeSet<OWLClass> subClasses = reasoner.getSubClasses(NonVegetarianPizza, true);
			
		System.out.println(subClasses.getFlattened());
        System.out.println("Done");
		

        // Usage of the explanation API:
//		ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
//		ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(ontology);
//		OWLAxiom entailment = null;
//		Set<Explanation<OWLAxiom>> expl = gen.getExplanations(entailment, 5);

		
	}
}
