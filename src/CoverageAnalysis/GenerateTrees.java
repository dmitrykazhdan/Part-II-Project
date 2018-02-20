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
		
		String explanationDirPathStr = "";
		String outputFilePathStr = "";
		String failedExplDirPathStr = "";
		
		if (args.length == 3) {
			explanationDirPathStr = args[0];
			outputFilePathStr = args[1];
			failedExplDirPathStr = args[2];
			
		} else {		
			System.out.println("Input the following arguments: ");
			System.out.println("1) Path to folder containing (entailment, justification) data.");
			System.out.println("2) Path where the output.txt statistics file should be created.");
			System.out.println("3) Path to where failed explanation files should be copied.");
			return;
		}
			
		// Declare path to the folder containing the explanation data set.
		Path explanationDirPath = Paths.get(explanationDirPathStr);
		File explanationDir = new File(explanationDirPath.toString());
		
		// Declare a path to the folder that will contain all failed explanations.
		Path failedExplanationsDirPath = Paths.get(failedExplDirPathStr);
		
		// Extract all explanations and evaluate the algorithm coverage.
		File[] explanationFiles = extractExplanationFiles(explanationDir);		
		evaluateCoverage(explanationFiles, failedExplanationsDirPath, outputFilePathStr);
	}
	
	
	// Method measures the number of explanations from which at least one proof tree has
	// been computed successfully.
	private static void evaluateCoverage(File[] explanationFiles, Path failedExplanationsDirPath, String outputFilePath) throws IOException, InterruptedException, ExecutionException {
		
		CorpusStatistics corpusStats = new CorpusStatistics();
	
		for (int i = 0; i < explanationFiles.length; i++) {
			
			// Increment the justifications counter.
			corpusStats.incrementTotalJustifications();
			System.out.println("Justification number: "+ corpusStats.getTotalJustifications() + " Thread count: " + Thread.activeCount());
			
			// Compute proof trees for the next explanation.
			Path explanationFilePath = Paths.get(explanationFiles[i].getAbsolutePath());		
			List<ProofTree> proofTrees = computeProofTree(explanationFilePath, corpusStats);

			// If at least one proof tree has been computed, increment the appropriate counter.
			// Otherwise copy the failed explanation to the appropriate folder.
			if (proofTrees != null && proofTrees.size() > 0) {
							
				for (ProofTree tree : proofTrees) {
					corpusStats.incrementTotalComputedTrees();
					corpusStats.updateComputedTreeStatistics(tree);
				}
				
			} else  {
				copyFile(explanationFilePath, failedExplanationsDirPath.resolve(explanationFilePath.getFileName()));
			}				
		}
		
		// Output the computed statistics.
		corpusStats.writeStatisticsToFile(outputFilePath);
		double coverage = (corpusStats.getComputedJustifications() * 100.0f)/corpusStats.getTotalJustifications();	
		System.out.println("Coverage is: " + coverage);	
	}
	
	
	
	private static List<ProofTree> computeProofTree(Path explanationFilePath, CorpusStatistics corpusStats) throws InterruptedException, ExecutionException, IOException {

		// Load the next explanation from the file.
		InputStream fileInputStream = new FileInputStream(explanationFilePath.toString());
		Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);
		
		ExecutorService executor = Executors.newCachedThreadPool();
		TreeGeneratorThread treeThread = new TreeGeneratorThread(explanation);
		Future<List<ProofTree>> futureCall = executor.submit(treeThread);		
		
		List<ProofTree> proofTrees = null;
		boolean timeout = false;

		try {
			proofTrees = futureCall.get(10,TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			timeout = true;
			
		}	finally {
			futureCall.cancel(true);
			executor.shutdownNow();
			executor.awaitTermination(15, TimeUnit.SECONDS);
		}
		
		if (proofTrees == null || proofTrees.size() == 0) {
			if (timeout) {
				corpusStats.updateFailsByTimeout(explanation.getEntailment());;
			} else {
				corpusStats.updateFailsByRuleCoverage(explanation.getEntailment());
			}
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
