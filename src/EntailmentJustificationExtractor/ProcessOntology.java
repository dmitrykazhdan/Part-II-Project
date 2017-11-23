package EntailmentJustificationExtractor;
import java.io.File;
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
	
	
	private Map<OWLAxiom, Set<Explanation<OWLAxiom>>> entailmentsWithJustifications = new HashMap<OWLAxiom, Set<Explanation<OWLAxiom>>>();
	private OWLOntology ontology;
	
	public ProcessOntology(String ontologyFilename) throws OWLOntologyCreationException {
		
		File file = new File(ontologyFilename);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		ontology = manager.loadOntologyFromOntologyDocument(file);	
		
		computeEntailmentsWithJustifications(manager);
	}
	
		
	public void computeEntailmentsWithJustifications(OWLOntologyManager manager) {
				
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		
		// Can you simply get all subclass axioms?
		// How to write to file conveniently?
		// Can you write these to file directly without an intermediate representation
		// Consider removing "entailmentsWithJustifications" and just writing to file

		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		
		ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
		ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(ontology);
				
		Set<OWLClass> allClasses = ontology.getClassesInSignature();
				
		for (OWLClass currentSuperclass : allClasses) {
			
			Set<OWLClass> subClasses = reasoner.getSubClasses(currentSuperclass, false).getFlattened();
			
			subClasses.addAll(reasoner.getEquivalentClasses(currentSuperclass).getEntities());
						
			for (OWLClass currentSubclass : subClasses) {
								
				OWLAxiom entailment = dataFactory.getOWLSubClassOfAxiom(currentSubclass, currentSuperclass);
				
				// Note size of entailments setting
				Set<Explanation<OWLAxiom>> justification = gen.getExplanations(entailment, 2);
				
				entailmentsWithJustifications.put(entailment, justification);
				
			}		
		}	
	}
	
	public Map<OWLAxiom, Set<Explanation<OWLAxiom>>> getEntailmentsWithJustifications() {		
		return entailmentsWithJustifications;
	}

}
