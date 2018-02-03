package DataSetExtractor;
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
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class OntologyProcessing {

	private String ontologyFilename;
	private OWLDataFactory dataFactory;
	private OWLOntology ontology;
	private OWLReasoner reasoner;
	private ExplanationGenerator<OWLAxiom> explanationGen;
	private String outputDirPath;
	
	
	public OntologyProcessing(String ontologyFilename, String outputDirPath) throws OWLOntologyCreationException {

		this.ontologyFilename = ontologyFilename;
		this.outputDirPath = outputDirPath;
				
		// Load the ontology from the specified file.
		File ontologyFile = new File(ontologyFilename);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		this.ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);

		// Create the reasoner for the ontology.
		OWLReasonerFactory reasonerFactory = new ReasonerFactory();
		this.reasoner = reasonerFactory.createReasoner(ontology);

		this.dataFactory = manager.getOWLDataFactory();

		// Create an explanation generator from the reasoner and the ontology.
		ExplanationGeneratorFactory<OWLAxiom> genFac = ExplanationManager.createExplanationGeneratorFactory(reasonerFactory);
		this.explanationGen = genFac.createExplanationGenerator(ontology);
	}
	
	
	
	public void GenerateExplanations() throws IOException {
		
		List<OWLAxiom> allSubsumptions = new ArrayList<OWLAxiom>();

		allSubsumptions = computeAllSubsumptionEntailments();

		for (OWLAxiom entailment : allSubsumptions) {

			Set<Explanation<OWLAxiom>> explanationSet = new HashSet<Explanation<OWLAxiom>>();

			// For every such subsumption entailment, compute all of its justifications.
			explanationSet = explanationGen.getExplanations(entailment);

			// Write these explanations to the output file.
			StoreExplanations(explanationSet, outputDirPath, ontologyFilename);
		}			
	}
	
	
	private List<OWLAxiom> computeAllSubsumptionEntailments() {
		
		List<OWLAxiom> allSubsumptions = new ArrayList<OWLAxiom>();
		allSubsumptions.addAll(computeOWLNothingSubsumptions());
		
		// Get all the classes from the ontology.
		Set<OWLClass> allClasses = ontology.getClassesInSignature();

		// For every class "A" in allClasses, compute all subsumption entailments of the form
		// B <= A for some other class B.
		for (OWLClass currentSuperclass : allClasses) {

			// For every class, compute all of its (non-strict) subclasses.
			Set<OWLClass> subClasses = GetNonStrictSubclasses(reasoner, currentSuperclass);

			for (OWLClass currentSubclass : subClasses) {

				// Generate a subsumption entailment from the (subclass, superclass) pair.
				OWLAxiom entailment = dataFactory.getOWLSubClassOfAxiom(currentSubclass, currentSuperclass);				
				allSubsumptions.add(entailment);
			}		
		}		
		return allSubsumptions;
	}
	
	
	private List<OWLAxiom> computeOWLNothingSubsumptions() {

		List<OWLAxiom> allOWLNothingSubsumptions = new ArrayList<OWLAxiom>();

		OWLClass owlNothing = dataFactory.getOWLNothing();
		Set<OWLClass> subClasses = GetNonStrictSubclasses(reasoner, owlNothing);

		for (OWLClass currentSubclass : subClasses) {
			OWLAxiom entailment = dataFactory.getOWLSubClassOfAxiom(currentSubclass, owlNothing);				
			allOWLNothingSubsumptions.add(entailment);
		}		
		return allOWLNothingSubsumptions;
	}

	
	private static Set<OWLClass> GetNonStrictSubclasses(OWLReasoner reasoner, OWLClass superclass) {

		// For every class in the ontology, compute all of its subclasses (direct and indirect).
		Set<OWLClass> subClasses = reasoner.getSubClasses(superclass, false).getFlattened();

		// Note that "getSubClasses" returns strict subclasses.
		// Hence need to manually add equivalent classes as well.
		subClasses.addAll(reasoner.getEquivalentClasses(superclass).getEntities());
		
		// Remove the trivial statement that the class is equivalent to itself.
		subClasses.remove(superclass);
		
		return subClasses;
	}


	private static void StoreExplanations(Set<Explanation<OWLAxiom>> explanationSet, String outputDir, String ontName) throws IOException {

		// Write all non-trivial explanations in the given set to the output stream.
		for (Explanation<OWLAxiom> explanation : explanationSet) {

			if (!isTrivialExplanation(explanation)) {
				
				// Generate unique identifier when naming the file
				String uuid = UUID.randomUUID().toString();			
				File outputFile = new File(outputDir + ontName + "_" + uuid + ".xml");
				OutputStream fileOutputStream = new FileOutputStream(outputFile);

				// Store the explanation in the file
				Explanation.store(explanation, fileOutputStream);
				fileOutputStream.close();
			}
		}		
	}

	
	/* Currently it is assumed that trivial subsumptions are:
	1) X <= T
	2) F <= X
	3) A --> A
	*/
	private static boolean isTrivialExplanation(Explanation<OWLAxiom> explanation) {

		OWLAxiom conclusion = explanation.getEntailment();
		Set<OWLAxiom> justification = explanation.getAxioms();
			
		if (justification.contains(conclusion.getAxiomWithoutAnnotations())) {
			return true;
			
		} else if (conclusion.isOfType(AxiomType.SUBCLASS_OF)) {
			
			OWLSubClassOfAxiom subClassOfAxiom = (OWLSubClassOfAxiom) conclusion;
			
			if (subClassOfAxiom.getSubClass().isOWLNothing() ||
				subClassOfAxiom.getSuperClass().isOWLThing()) {
				
				return true;
			}			
		} 			
		return false;
	}
}
