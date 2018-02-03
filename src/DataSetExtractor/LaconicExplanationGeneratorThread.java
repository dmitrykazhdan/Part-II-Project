package DataSetExtractor;

import java.util.Set;
import java.util.concurrent.Callable;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owlapi.model.OWLAxiom;

public class LaconicExplanationGeneratorThread implements Callable<Set<Explanation<OWLAxiom>>> {

	private OWLAxiom entailment;
	private ExplanationGenerator<OWLAxiom> explanationGen;
	
	public LaconicExplanationGeneratorThread(OWLAxiom entailment, ExplanationGenerator<OWLAxiom> explanationGen) {
		this.entailment = entailment;
		this.explanationGen = explanationGen;
	}
	
	@Override
	public Set<Explanation<OWLAxiom>> call() throws Exception {
		return explanationGen.getExplanations(entailment);
	}
}
