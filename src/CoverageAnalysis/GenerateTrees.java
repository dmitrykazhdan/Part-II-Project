package CoverageAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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

		
		Path failedExplanationDirPath = Paths.get("/Users/AdminDK/Desktop/FailedExplanations/");
		Path explanationDirPath = Paths.get("/Users/AdminDK/Desktop/TestExplanations/");
		File explanationDir = new File(explanationDirPath.toString());
		
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
			Path explanationFilePath = Paths.get(explanationFiles[i].getAbsolutePath());		
			InputStream fileInputStream = new FileInputStream(explanationFilePath.toString());
			System.out.println(totalJustifications + " " + explanationFilePath.toString());
			Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
			
			ExecutorService executor = Executors.newCachedThreadPool();
			Future<List<ProofTree>> futureCall = executor.submit(new TreeGeneratorThread(explanation));
						
			List<ProofTree> proofTrees = null;

			try {
				proofTrees = futureCall.get(10,TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				System.out.println("TIMEOUT " +"Filename" + explanationFilePath.toString() + " (Total: " + totalJustifications + ")");
			}
			
			if (proofTrees != null && proofTrees.size() > 0) {
				totalTreesComputed++;
			} else {
				System.out.println("Could not compute Proof Tree." +" Filename " + explanationFilePath.toString() + " (Total: " + totalJustifications + ")");
	//			copyFile(explanationFilePath, failedExplanationDirPath.resolve(explanationFilePath.getFileName()));
			}				
		}
		double coverage = (totalTreesComputed * 100.0f)/totalJustifications;
		
		System.out.println("Coverage is: " + coverage);
		
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
