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
		
		Path explanationDirPath = Paths.get("/Users/AdminDK/Desktop/NonTrivialComputedExplanations");
		Path nonTrivialExplanationsDirPath =  Paths.get("/Users/AdminDK/Desktop/NonTrivialComputedExplanations");
		File explanationsDir = new File(explanationDirPath.toString());
		
		File[] explanations = explanationsDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".xml");
		    }
		});	
		
		int nonTrivialCounter = 0;
		
		for (File explanationFile : explanations) {
			InputStream fileInputStream;
			try {
				
				Path explanationFilePath = Paths.get(explanationFile.getAbsolutePath());
				fileInputStream = new FileInputStream(explanationFile.getAbsolutePath());
				Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
				Set<OWLAxiom> justification = explanation.getAxioms();
				OWLAxiom entailment = explanation.getEntailment();
				
//				if (countLogicalAxioms(justification) < 5) {

				if (isRule39Only(justification, entailment)) {
					nonTrivialCounter++;
					System.out.println("counter: " + nonTrivialCounter);
				//	Files.move(explanationFile.toPath(), nonTrivialExplanationsDirPath.resolve(explanationFilePath.getFileName()), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
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
	
	
	private static boolean isRule39Only(Set<OWLAxiom> justification, OWLAxiom entailment) {
		
		Set<OWLAxiom> logicalSet = getLogicalAxioms(justification);
		
		if (!subClassAxiomsOnly(logicalSet)) {
			return false;
		}
		
		OWLSubClassOfAxiom subClsEntailment = (OWLSubClassOfAxiom) entailment;
		OWLClassExpression start = subClsEntailment.getSubClass();
		OWLClassExpression end = subClsEntailment.getSuperClass();
		
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
	
	

	
	
	private static void copyFile(Path source, Path dest) throws IOException {
		
	    InputStream is = null;
	    OutputStream os = null;
	    
	    try {
	        is = new FileInputStream(source.toAbsolutePath().toString());
	        os = new FileOutputStream(dest.toAbsolutePath().toString());
	        byte[] buffer = new byte[1024];
	        int length;
	        
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	        
	    } finally {
	        is.close();
	        os.close();
	    }
	}
}
