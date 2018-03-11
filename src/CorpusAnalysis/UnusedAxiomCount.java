package CorpusAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;

public class UnusedAxiomCount {


	public static void main(String args[]) throws IOException {

		Path justificationFolderPath = Paths.get("/Users/AdminDK/Desktop/FailedExplanations");
		File justificationFiles = new File(justificationFolderPath.toString());

		File[] allFiles = justificationFiles.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});	
		
		Map<AxiomType, Integer> typeCounts = getUnusedAxiomsMap();

		for (File explanationFile : allFiles) {
			
			InputStream fileInputStream;
			fileInputStream = new FileInputStream(explanationFile.getAbsolutePath());
			Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
			Set<OWLAxiom> justification = explanation.getAxioms();			
			processJustification(justification, typeCounts);
			fileInputStream.close();
		}
		
		for (AxiomType type : typeCounts.keySet()) {
			System.out.println("Type: " + type.toString() + " 	Count: " + typeCounts.get(type));
		}		
	}
	
	
	private static void processJustification(Set<OWLAxiom> justification, Map<AxiomType, Integer> typeCounts) {
		
		for (OWLAxiom axiom : justification) {
			if (typeCounts.keySet().contains(axiom.getAxiomType())) {
				typeCounts.put(axiom.getAxiomType(), typeCounts.get(axiom.getAxiomType()) + 1);
			}
		}		
	}
	
	
	
	private static Map<AxiomType, Integer> getUnusedAxiomsMap() {
		
		Map<AxiomType, Integer> typeCounts = new HashMap<AxiomType, Integer>();
		
		// Add all unused axiom types to map and initialize their counts.
		typeCounts.put(AxiomType.EQUIVALENT_OBJECT_PROPERTIES, 0);
		typeCounts.put(AxiomType.DISJOINT_OBJECT_PROPERTIES, 0);
		typeCounts.put(AxiomType.REFLEXIVE_OBJECT_PROPERTY, 0);
		typeCounts.put(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY, 0);
		typeCounts.put(AxiomType.ASYMMETRIC_OBJECT_PROPERTY, 0);
		typeCounts.put(AxiomType.SUB_DATA_PROPERTY, 0);
		typeCounts.put(AxiomType.EQUIVALENT_DATA_PROPERTIES, 0);
		typeCounts.put(AxiomType.DISJOINT_DATA_PROPERTIES, 0);
		typeCounts.put(AxiomType.DISJOINT_UNION, 0);
		typeCounts.put(AxiomType.CLASS_ASSERTION, 0);
		typeCounts.put(AxiomType.OBJECT_PROPERTY_ASSERTION, 0);
		typeCounts.put(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION, 0);
		typeCounts.put(AxiomType.SAME_INDIVIDUAL, 0);
		typeCounts.put(AxiomType.DATA_PROPERTY_ASSERTION, 0);
		typeCounts.put(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION, 0);
						
		return typeCounts;
	}

}
