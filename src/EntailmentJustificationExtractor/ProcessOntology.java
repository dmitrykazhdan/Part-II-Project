package EntailmentJustificationExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class ProcessOntology {

	public static void GenerateExplanations(String ontologyFilename, String outputDir) throws OWLOntologyCreationException, IOException {
		
		// Load the ontology from the specified file.
		File ontologyFile = new File(ontologyFilename);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
			
		OWLReasoner r = new Reasoner(new Configuration(), ontology);
		
		// Create the HermiT reasoner for the ontology.
		OWLReasonerFactory reasonerFactory = new ReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		reasoner.precomputeInferences();
		
		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		
		// Create an explanation generator from the reasoner and the ontology.
		ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
		ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(ontology);
				
		// Get all the classes from the ontology.
		Set<OWLClass> allClasses = ontology.getClassesInSignature();
			
		
		for (OWLClass currentSuperclass : allClasses) {

			// For every class, compute all of its non-strict subclasses.
			Set<OWLClass> subClasses = GetNonStrictSubclasses(reasoner, currentSuperclass);
		
			for (OWLClass currentSubclass : subClasses) {
				
				// Generate a subsumption entailment from the (subclass, superclass) pair.
				OWLAxiom entailment = dataFactory.getOWLSubClassOfAxiom(currentSubclass, currentSuperclass);				
				
				// For every such subsumption entailment, compute all of its justifications.
				Set<Explanation<OWLAxiom>> justification = gen.getExplanations(entailment, 4);

				// Write these explanations to the output file.
				StoreExplanations(justification, outputDir);
			
			}		
		}	
	}
	
	
	private static Set<OWLClass> GetNonStrictSubclasses(OWLReasoner reasoner, OWLClass superclass) {
		
		// For every class in the ontology, compute all of its subclasses (direct and indirect).
		Set<OWLClass> subClasses = reasoner.getSubClasses(superclass, false).getFlattened();
		
		// Note that "getSubClasses" returns strict subclasses.
		// Hence need to manually add equivalent classes as well.
		subClasses.addAll(reasoner.getEquivalentClasses(superclass).getEntities());
		
		return subClasses;
	}
	
	
	private static void StoreExplanations(Set<Explanation<OWLAxiom>> explanationSet, String outputDir) throws IOException {
		
		// Write all explanations in the given set to the output stream.
		for (Explanation<OWLAxiom> explanation : explanationSet) {
			
			// Generate unique identifier when naming the file
			String uuid = UUID.randomUUID().toString();			
			File outputFile = new File(outputDir + uuid + ".xml");
			OutputStream fileOutputStream = new FileOutputStream(outputFile);
			
			// Store the explanation in the file
			Explanation.store(explanation, fileOutputStream);
			fileOutputStream.close();
		}		
	}
}
