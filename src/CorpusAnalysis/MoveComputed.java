package CorpusAnalysis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class MoveComputed {

	public static void main(String args[]) throws IOException {
		
		// source
		Path computedExplanationsDirPath = Paths.get("/Users/AdminDK/Desktop/NonTrivialTimedOut");
		
		// dest
		Path failedExplanationsDirPath =  Paths.get("/Users/AdminDK/Desktop/FailedExplanations");
		
		File computedExplanationsDir = new File(computedExplanationsDirPath.toString());
		
		
		File[] explanations = computedExplanationsDir.listFiles(new FilenameFilter() {
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
				Files.move(explanationFile.toPath(), computedFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Moving...");
			}
			
		}	
	}
}
