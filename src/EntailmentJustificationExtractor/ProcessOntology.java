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
		
		List<OWLClass> classesToExpand = new ArrayList<OWLClass>();
		Set<OWLClass> expandedClasses = new HashSet<OWLClass>();
		
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		
		reasoner.precomputeInferences();
		
		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		
		ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
		ExplanationGenerator<OWLAxiom> gen = genFac.createExplanationGenerator(ontology);
		
		OWLClass thingClass = dataFactory.getOWLThing();
		
		classesToExpand.add(thingClass);
		
		while (!classesToExpand.isEmpty()) {
			
			OWLClass currentSuperclass = classesToExpand.remove(0);
			
			if (expandedClasses.contains(currentSuperclass)) {
				continue;
			}
			
			expandedClasses.add(currentSuperclass);
			Set<OWLClass> subClasses = reasoner.getSubClasses(currentSuperclass, true).getFlattened();
			
			for (OWLClass currentSubclass : subClasses) {
								
				OWLAxiom entailment = dataFactory.getOWLSubClassOfAxiom(currentSubclass, currentSuperclass);
				
				// Note size of entailments setting
				Set<Explanation<OWLAxiom>> justification = gen.getExplanations(entailment, 2);
				
				entailmentsWithJustifications.put(entailment, justification);
				
				classesToExpand.add(currentSubclass);
			}		
		}
		

		
	}
	
	public Map<OWLAxiom, Set<Explanation<OWLAxiom>>> getEntailmentsWithJustifications() {		
		return entailmentsWithJustifications;
	}

}
