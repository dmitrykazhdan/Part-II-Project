package EntailmentJustificationExtractor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class ProcessOntology {

	public static void GenerateExplanations(String ontologyFilename) throws OWLOntologyCreationException, IOException {
		
		// Load the ontology from the specified file.
		File ontologyFile = new File(ontologyFilename);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
			
		// Create a reasoner for the ontology.
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		
		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		
		// Create an explanation generator from the reasoner and the ontology.
		ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
		ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(ontology);
				
		// Get all the classes from the ontology.
		Set<OWLClass> allClasses = ontology.getClassesInSignature();
		
		File outputFile = new File("/Users/AdminDK/Desktop/test.txt");
		OutputStream fileOutputStream = null;
		
		// If the output file does not exist, create it.
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		
		
		try {
			
			fileOutputStream = new FileOutputStream(outputFile);
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
				 
		for (OWLClass currentSuperclass : allClasses) {

			Set<OWLClass>  subClasses = GetAllSubclasses(reasoner, currentSuperclass);
		
			for (OWLClass currentSubclass : subClasses) {
								
				OWLAxiom entailment = dataFactory.getOWLSubClassOfAxiom(currentSubclass, currentSuperclass);				
				Set<Explanation<OWLAxiom>> justification = gen.getExplanations(entailment, Integer.MAX_VALUE);				
				StoreExplanations(justification, fileOutputStream);
			
			}		
		}	
		
		fileOutputStream.close();
	}
	
	
	private static Set<OWLClass> GetAllSubclasses(OWLReasoner reasoner, OWLClass superclass) {
		
		// For every class in the ontology, compute all of its subclasses.
		Set<OWLClass> subClasses = reasoner.getSubClasses(superclass, false).getFlattened();
		
		// Note that "getSubClasses" returns strict subclasses.
		// Hence need to manually add equivalent classes as well.
		subClasses.addAll(reasoner.getEquivalentClasses(superclass).getEntities());
		
		return subClasses;
	}
	
	
	private static void StoreExplanations(Set<Explanation<OWLAxiom>> explanationSet, OutputStream out) throws IOException {
		
		// Write all explanations in the given set to the output stream.
		for (Explanation<OWLAxiom> explanation : explanationSet) {
			Explanation.store(explanation, out);
		}	
	}
}
