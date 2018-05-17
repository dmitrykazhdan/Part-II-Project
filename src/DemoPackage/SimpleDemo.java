package DemoPackage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

import CoverageAnalysis.TreeGeneratorThread;
import ProofTreeComputation.ProofTree;

public class SimpleDemo {

	
	public static void main(String args[]) throws IOException, InterruptedException, ExecutionException {
		
		String explanationFilePath = "/Users/AdminDK/Desktop/Demo/sample3.xml";
	
		// Load the next explanation from the file.
		InputStream fileInputStream = new FileInputStream(explanationFilePath.toString());
		Explanation<OWLAxiom> explanation = Explanation.load(fileInputStream);

		// Submit thread to execution environment.
		ExecutorService executor = Executors.newCachedThreadPool();
		TreeGeneratorThread treeThread = new TreeGeneratorThread(explanation);
		Future<List<ProofTree>> futureCall = executor.submit(treeThread);		
		
		List<ProofTree> proofTrees = null;

		// Run thread with timeout limit.
		try {
			proofTrees = futureCall.get(10,TimeUnit.SECONDS);
		} catch (OutOfMemoryError | TimeoutException e) {
			System.out.println("Timed out.");
			
		}	finally {
			futureCall.cancel(true);
			executor.shutdownNow();
		}
	
		
		if (proofTrees == null || proofTrees.size() == 0) {
			System.out.println("Could not compute tree");
		} else {		

			for (ProofTree t : proofTrees) {
				print(t);
				System.out.println("");
				System.out.println("");
			}			
		}		
	}
	
	
	
	
	// Print tree.
    private static void print(ProofTree tree) {
        print("", true, tree);
    }

    private static void print(String prefix, boolean isTail, ProofTree tree) {
    		
    		String ruleID = "";
    	
    		if (tree.getInferenceRule() != null) {
    			ruleID = " -- " + tree.getInferenceRule().getRuleID() + " -- " + tree.getInferenceRule().getRuleName();
    		}
    	
        System.out.println(prefix + (isTail ? "└── " : "├── ") + tree.getAxiom() + ruleID);
     
        List<ProofTree> subTrees = tree.getSubTrees();
        
        if (subTrees == null) {
        		return;
        }
        
        for (int i = 0; i < subTrees.size() - 1; i++) {
        		print(prefix + (isTail ? "    " : "│   "), false, subTrees.get(i));
        }
        
        if (subTrees.size() > 0) {       	
        		print(prefix + (isTail ?"    " : "│   "), true, subTrees.get(subTrees.size() - 1));
        }
    }
    	
}
