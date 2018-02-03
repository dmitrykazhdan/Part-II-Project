package DataSetExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class SubSumptionComputationThread implements Callable<List<OWLAxiom>>{

	private OWLDataFactory dataFactory;
	private OWLOntology ontology;
	private OWLReasoner reasoner;

	public SubSumptionComputationThread(OWLDataFactory dataFactory, OWLOntology ontology, OWLReasoner reasoner) {
		this.dataFactory = dataFactory;
		this.ontology = ontology;
		this.reasoner = reasoner;
	}
	
	@Override
	public List<OWLAxiom> call() throws Exception {
		return ComputeAllSubsumptionEntailments();
	}
	
	
	private List<OWLAxiom> ComputeAllSubsumptionEntailments() {
		
		List<OWLAxiom> allSubsumptions = new ArrayList<OWLAxiom>();
		allSubsumptions.addAll(ComputeOWLNothingSubsumptions());
		
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
	
	
	private List<OWLAxiom> ComputeOWLNothingSubsumptions() {

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
}
