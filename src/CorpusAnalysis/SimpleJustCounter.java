package CorpusAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;

public class SimpleJustCounter {

	
	public static void main(String args[]) {
		
		Path explanationDirPath = Paths.get("/Users/AdminDK/Desktop/Explanations");
		Path nonTrivialExplanationsDirPath =  Paths.get("/Users/AdminDK/Desktop/NonTrivialExplanations");
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
				fileInputStream = new FileInputStream(explanationFile.getAbsolutePath());
				Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
				Set<OWLAxiom> justification = explanation.getAxioms();
				
				if (isNonTrivialJustification(justification)) {
					nonTrivialCounter++;
					System.out.println("counter: " + nonTrivialCounter);
					copyFile(Paths.get(explanationFile.getAbsolutePath()), nonTrivialExplanationsDirPath);
				}
				fileInputStream.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}	
	}	
	
	
	
	private static boolean isNonTrivialJustification(Set<OWLAxiom> justification) {
				
		for (OWLAxiom ax : justification) {
			if (ax.isLogicalAxiom() && !ax.isOfType(AxiomType.SUBCLASS_OF)) {
				return true;
			}
		}
		return false;
	}
	
	
	private static int countLogicalAxiom(Set<OWLAxiom> justification) {
		
		int logicalCount = 0;
		
		for (OWLAxiom ax : justification) {
			if (ax.isLogicalAxiom()){
				logicalCount++;
			}
		}
		return logicalCount;
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
