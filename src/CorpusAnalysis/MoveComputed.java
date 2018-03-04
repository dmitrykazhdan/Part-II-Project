package CorpusAnalysis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class MoveComputed {

	public static void main(String args[]) throws IOException {
		
		Path explanationDirPath = Paths.get("/Users/AdminDK/Desktop/PartitionedExplanations/set4");
		Path failedExplanationsDirPath =  Paths.get("/Users/AdminDK/Desktop/FailedExplanations");
		Path computedExplanationsDirPath = Paths.get("/Users/AdminDK/Desktop/ComputedExplanations");
				
		File explanationsDir = new File(explanationDirPath.toString());
		
		
		File[] explanations = explanationsDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".xml");
		    }
		});	
		
		for (File explanationFile : explanations) {
				
			Path explanationFilePath = Paths.get(explanationFile.getAbsolutePath());
			Path computedFilePath = failedExplanationsDirPath.resolve(explanationFilePath.getFileName());
			File computedFile = new File(computedFilePath.toString());
			
			
			if (computedFile.exists()) {
//				explanationFile.renameTo(new File());
				Files.move(explanationFile.toPath(), computedExplanationsDirPath.resolve(explanationFile.getName()));
				System.out.println("Moving...");
			}
			
		}
		
		
		
		
		
			
	}
}
