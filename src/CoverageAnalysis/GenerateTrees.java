package CoverageAnalysis;

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

		// Declare path to the folder containing the explanation data set.
		Path explanationDirPath = Paths.get("/Users/AdminDK/Desktop/TestExplanations/");
		File explanationDir = new File(explanationDirPath.toString());
		
		// Declare a path to the folder that will contain all failed explanations.
		Path failedExplanationsDirPath = Paths.get("/Users/AdminDK/Desktop/FailedExplanations/");
		
		// Extract all explanations and evaluate the algorithm coverage.
		File[] explanationFiles = extractExplanationFiles(explanationDir);		
		evaluateCoverage(explanationFiles, failedExplanationsDirPath);
	}
	
	// Method measures the number of explanations from which at least one proof tree has
	// been computed successfully.
	private static void evaluateCoverage(File[] explanationFiles, Path failedExplanationsDirPath) throws IOException, InterruptedException, ExecutionException {
		
		CorpusStatistics corpusStats = new CorpusStatistics();
	
		for (int i = 0; i < explanationFiles.length; i++) {
			
			// Increment the justifications counter.
			corpusStats.incrementTotalJustifications();
			System.out.println("Justification number: "+ corpusStats.getTotalJustifications());
			
			// Compute proof trees for the next explanation.
			Path explanationFilePath = Paths.get(explanationFiles[i].getAbsolutePath());		
			List<ProofTree> proofTrees = computeProofTree(explanationFilePath);

			// If at least one proof tree has been computed, increment the appropriate counter.
			// Otherwise copy the failed explanation to the appropriate folder.
			if (proofTrees != null && proofTrees.size() > 0) {
				corpusStats.incrementTotalComputedTrees();
				
				for (ProofTree tree : proofTrees) {
					corpusStats.updateStatistics(tree);
				}
				
			} else {
				System.out.println("Could not compute Proof Tree." +" Filename " + explanationFilePath.toString() + " (Total: " + corpusStats.getTotalJustifications() + ")");
				copyFile(explanationFilePath, failedExplanationsDirPath.resolve(explanationFilePath.getFileName()));
			}				
		}
		
		// Output the computed statistics.
		corpusStats.writeStatisticsToFile();
		double coverage = (corpusStats.getTotalTreesComputed() * 100.0f)/corpusStats.getTotalJustifications();	
		System.out.println("Coverage is: " + coverage);	
	}
	
	
	
	private static List<ProofTree> computeProofTree(Path explanationFilePath) throws InterruptedException, ExecutionException, IOException {

		// Load the next explanation from the file.
		InputStream fileInputStream = new FileInputStream(explanationFilePath.toString());
		Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
		
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<List<ProofTree>> futureCall = executor.submit(new TreeGeneratorThread(explanation));				
		List<ProofTree> proofTrees = null;

		try {
			proofTrees = futureCall.get(10,TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			System.out.println("TIMEOUT " +"Filename" + explanationFilePath.toString());
		}		
		return proofTrees;
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
	
	private static File[] extractExplanationFiles(File explanationsDir) {
		
		// Extract all files with ".xml" extension from the specified directory.
		File[] explanations = explanationsDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".xml");
		    }
		});		
		return explanations;		
	}
	
}
