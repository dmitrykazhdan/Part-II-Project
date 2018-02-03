package DataSetExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

import CoverageAnalysis.TreeGeneratorThread;
import ProofTreeComputation.ProofTree;

public class OntologyProcessing {

	private File ontologyFile;
	private OWLDataFactory dataFactory;
	private OWLOntology ontology;
	private OWLReasoner reasoner;
	private ExplanationGenerator<OWLAxiom> explanationGen;
	private File outputDir;
	
	
	public OntologyProcessing(File ontologyFile, File outputDir) throws OWLOntologyCreationException {

		this.ontologyFile = ontologyFile;
		this.outputDir = outputDir;
				
		// Load the ontology from the specified file.
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
	
	
	
	public void GenerateExplanations() throws IOException, InterruptedException, ExecutionException {
		
		// Compute all subsumption entailments.
		List<OWLAxiom> allSubsumptions = ComputeAllSubsumptionEntailments();
		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		// For every entailment, generate all of its justifications.
		for (OWLAxiom entailment : allSubsumptions) {

			Set<Explanation<OWLAxiom>> explanationSet = new HashSet<Explanation<OWLAxiom>>();
			Future<Set<Explanation<OWLAxiom>>> explanationGenThreadCall = executor.submit(new ExplanationGeneratorThread(entailment, explanationGen));
			
			// Set a time limit of 10 minutes to the computation of all justifications.
			try {
				explanationSet = explanationGenThreadCall.get(10, TimeUnit.MINUTES);
			} catch (TimeoutException e) {
				System.out.println("Timeout on computing all justifications. Ontology: " + ontologyFile.getName() + " entailment: " + entailment.toString());
			}
			
			if (explanationSet != null) {
				// Write these explanations to the output file.
				StoreExplanations(explanationSet);
			}
		}			
	}
	
	
	private List<OWLAxiom> ComputeAllSubsumptionEntailments() throws InterruptedException, ExecutionException {
		
		List<OWLAxiom> allSubsumptions = new ArrayList<OWLAxiom>();
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<List<OWLAxiom>> subsumptionThreadCall = executor.submit(new SubSumptionComputationThread(dataFactory, ontology, reasoner));
		
		// Set a time limit of 10 minutes to the computation of all subsumption entailments.
		try {
			allSubsumptions = subsumptionThreadCall.get(10, TimeUnit.MINUTES);
		} catch (TimeoutException e) {
			System.out.println("Timeout on computing all subsumption entailments. Ontology: " + ontologyFile.getName());
		}		
		return allSubsumptions;
	}
	

	private void StoreExplanations(Set<Explanation<OWLAxiom>> explanationSet) throws IOException {

		// Write all non-trivial explanations in the given set to the output stream.
		for (Explanation<OWLAxiom> explanation : explanationSet) {

			if (!IsTrivialExplanation(explanation)) {
				
				// Generate unique identifier when naming the file
				String uuid = UUID.randomUUID().toString();		
				
				Path outputFilePath = outputDir.toPath().resolve(ontologyFile.getName());
				File outputFile = new File(outputFilePath.toString() +  "_" + uuid + ".xml");
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
	private static boolean IsTrivialExplanation(Explanation<OWLAxiom> explanation) {

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
