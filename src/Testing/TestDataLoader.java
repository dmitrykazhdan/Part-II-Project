package Testing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

public class TestDataLoader {

	public static List<OWLAxiom> loadPremises(String explanationFilename) throws IOException {
		Explanation<OWLAxiom> explanation = loadExplanation(explanationFilename);
		List<OWLAxiom> premises = new ArrayList<OWLAxiom>(explanation.getAxioms());
		return premises;
	}
	
	public static OWLAxiom loadConclusion(String explanationFilename) throws IOException {
		Explanation<OWLAxiom> explanation = loadExplanation(explanationFilename);
		return explanation.getEntailment();
	}
	
	public static Explanation<OWLAxiom> loadExplanation(String explanationFilename) throws IOException {
		
		InputStream fileInputStream = new FileInputStream(explanationFilename);
		Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
		fileInputStream.close();
		return explanation;
	}

	
}
