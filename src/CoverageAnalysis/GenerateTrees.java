package CoverageAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import ProofTreeComputation.ProofTree;
import ProofTreeComputation.ProofTreeGenerator;

public class GenerateTrees {
	
	public static void main(String args[]) throws IOException, InterruptedException, ExecutionException {

		String explanationDirName = "/Users/AdminDK/Desktop/Explanations/";
		File explanationDir = new File(explanationDirName);
		
		File[] explanationFiles = explanationDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".xml");
		    }
		});
		
		float totalJustifications = 0;
		float totalTreesComputed = 0;
		
		for (int i = 0; i < explanationFiles.length; i++) {
			
			totalJustifications++;
			String explanationFilename = explanationFiles[i].getAbsolutePath();		
			InputStream fileInputStream = new FileInputStream(explanationFilename);
			System.out.println(totalJustifications + " " + explanationFilename);
			Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
			
			ExecutorService executor = Executors.newCachedThreadPool();
			Future<List<ProofTree>> futureCall = executor.submit(new TreeGeneratorThread(explanation));
						
			List<ProofTree> proofTrees = null;

			try {
				proofTrees = futureCall.get(10,TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				System.out.println("TIMEOUT " +"Filename" + explanationFilename + " (Total: " + totalJustifications + ")");
			}
			
			if (proofTrees != null && proofTrees.size() > 0) {
				totalTreesComputed++;
			} else {
				System.out.println("Could not compute Proof Tree." +"Filename" + explanationFilename + " (Total: " + totalJustifications + ")");
			}				
		}
		double coverage = (totalTreesComputed * 100.0f)/totalJustifications;
		
		System.out.println("Coverage is: " + coverage);
		
	}
	
}
