package CorpusAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class SimpleJustCounter {

	
	public static void main(String args[]) {
				
		String explanationDirPathStr = "";
		String nonTrivialExplanationsDirStr = "";

		if (args.length == 1) {
			explanationDirPathStr = args[0];
			nonTrivialExplanationsDirStr = args[1];
		} else {		
			System.out.println("Input the following arguments: ");
			System.out.println("1) Path to folder containing (entailment, justification) data.");
			System.out.println("2) Path to where the non-trivial data should be moved to.");
			return;
		}
		
		Path explanationDirPath = Paths.get(explanationDirPathStr);
		Path nonTrivialExplanationsDirPath =  Paths.get(nonTrivialExplanationsDirStr);
		File explanationsDir = new File(explanationDirPath.toString());
		
		File[] explanations = explanationsDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".xml");
		    }
		});	
		
		
		for (File explanationFile : explanations) {
			InputStream fileInputStream;
			try {
				
				Path explanationFilePath = Paths.get(explanationFile.getAbsolutePath());
				fileInputStream = new FileInputStream(explanationFile.getAbsolutePath());
				Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
				Set<OWLAxiom> justification = explanation.getAxioms();
				OWLAxiom entailment = explanation.getEntailment();
				
				if (!isTransitiveCase(justification, entailment)) {
					Files.move(explanationFile.toPath(), nonTrivialExplanationsDirPath.resolve(explanationFilePath.getFileName()), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				}
				fileInputStream.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}	
	}	
	
	
	
	private static boolean subClassAxiomsOnly(Set<OWLAxiom> justification) {
				
		for (OWLAxiom ax : justification) {
			if (ax.isLogicalAxiom() && !ax.isOfType(AxiomType.SUBCLASS_OF)) {
				return false;
			}
		}
		return true;
	}
	
	
	private static boolean isTransitiveCase(Set<OWLAxiom> justification, OWLAxiom entailment) {
		
		Set<OWLAxiom> logicalSet = getLogicalAxioms(justification);
		
		if (!subClassAxiomsOnly(logicalSet)) {
			return false;
		}
		
		OWLSubClassOfAxiom subClsEntailment = (OWLSubClassOfAxiom) entailment;
		OWLClassExpression start = subClsEntailment.getSubClass();
		
		List<OWLClassExpression> chain = new ArrayList<OWLClassExpression>();
		chain.add(start);
		
		boolean added = true;
		
		while (added) {
			added = false;
			OWLSubClassOfAxiom subClsAx = null;
			
			for (OWLAxiom ax : logicalSet) {
				subClsAx = (OWLSubClassOfAxiom) ax;
				if (subClsAx.getSubClass().equals(chain.get(chain.size()-1))) {
					chain.add(subClsAx.getSuperClass());
					added = true;
					break;
				}
			}			
			
			if (added) {
				logicalSet.remove(subClsAx);
			}
		}
		
		if (logicalSet.size() == 0 && chain.get(chain.size() - 1).equals(subClsEntailment.getSuperClass())) {
			return true;
		}		
		return false;
	}
	
	
	
	private static Set<OWLAxiom> getLogicalAxioms(Set<OWLAxiom> justification) {
		
		Set<OWLAxiom> logicalSet = new HashSet<OWLAxiom>();
		
		for (OWLAxiom ax : justification) {
			if (ax.isLogicalAxiom()) {
				logicalSet.add(ax);
			}			
		}
		return logicalSet;
	}	
}
