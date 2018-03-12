package CorpusAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import OWLExpressionTemplates.SubClassStr;

public class ExpressionDepthEstimator {

	public static void main(String args[]) throws IOException {

		Path justificationFolderPath = Paths.get("/Users/AdminDK/Desktop/Refined (06.03.2018)/NonTrivialComputedExplanations");
		File justificationFiles = new File(justificationFolderPath.toString());

		File[] allFiles = justificationFiles.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});	

		Map<Integer, Integer> depthCounts = new HashMap<Integer, Integer>();

		int counter = 0; // DELETE 
		for (File explanationFile : allFiles) {

			InputStream fileInputStream;
			fileInputStream = new FileInputStream(explanationFile.getAbsolutePath());
			Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
			Set<OWLAxiom> justification = explanation.getAxioms();

			int maxDepth = maximumDepth(justification);

			if (maxDepth <= 0) {
				continue;
			}
			
			// DELETE
			counter++;
			System.out.println(counter);

			if (!depthCounts.keySet().contains(maxDepth)) {
				depthCounts.put(maxDepth, 0);
			}
			depthCounts.put(maxDepth, depthCounts.get(maxDepth)+1);

			fileInputStream.close();
		}		
		
		for (Integer depth : depthCounts.keySet()) {
			System.out.println("Depth: " + depth + " 	Count: " + depthCounts.get(depth));
		}
	}


	private static int maximumDepth(Set<OWLAxiom> justification) {

		List<Integer> depths = new ArrayList<Integer>();

		for (OWLAxiom axiom : justification) {
			depths.add(getAxiomDepth(axiom));
		}
		return Collections.max(depths);
	}


	// Assume you are only interested in class expressions.
	private static int getAxiomDepth(OWLAxiom axiom) {

		if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {
			return Math.max(getClassExpDepth(((OWLSubClassOfAxiom) axiom).getSubClass()), getClassExpDepth(((OWLSubClassOfAxiom) axiom).getSuperClass()));

		} else if (axiom.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
			return getMaximumClassExpDepth(((OWLEquivalentClassesAxiom) axiom).getClassExpressions());

		} else if (axiom.isOfType(AxiomType.DISJOINT_CLASSES)){
			return getMaximumClassExpDepth(((OWLDisjointClassesAxiom) axiom).getClassExpressions());

		} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
			return getClassExpDepth(((OWLObjectPropertyDomainAxiom) axiom).getDomain());

		} else if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_RANGE)) {
			return getClassExpDepth(((OWLObjectPropertyRangeAxiom) axiom).getRange());			
		} 
		return Integer.MIN_VALUE;
	}


	private static int getMaximumClassExpDepth(Set<OWLClassExpression> clsExps) {
		
		List<Integer> depths = new ArrayList<Integer>();

		for (OWLClassExpression clsExp : clsExps) {
			depths.add(getClassExpDepth(clsExp));
		}
		return Collections.max(depths);
	}

	
	private static int getClassExpDepth(OWLClassExpression clsExp) {

		ClassExpressionType classExpType = clsExp.getClassExpressionType();

		if (classExpType.equals(ClassExpressionType.OBJECT_INTERSECTION_OF) || 
			classExpType.equals(ClassExpressionType.OBJECT_UNION_OF)) {

			return getMaximumClassExpDepth(((OWLNaryBooleanClassExpression) clsExp).getOperands()) + 1;

		} else if (classExpType.equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)) {
			return getClassExpDepth(((OWLObjectComplementOf) clsExp).getOperand()) + 1;

		}  else if (classExpType.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM) ||
				classExpType.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {

			return getClassExpDepth(((OWLQuantifiedObjectRestriction) clsExp).getFiller()) + 1;

		} else if (classExpType.equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)  ||
				classExpType.equals(ClassExpressionType.OBJECT_MAX_CARDINALITY) ||
				classExpType.equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {

			return getClassExpDepth(((OWLObjectCardinalityRestriction) clsExp).getFiller()) + 1;				
		} else {
			return 1;
		}
	}
}
