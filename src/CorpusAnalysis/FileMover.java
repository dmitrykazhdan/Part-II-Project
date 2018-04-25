package CorpusAnalysis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMover {

	
	
	public static void main(String args[]) throws IOException {
		
		Path partitionFolder = Paths.get("/Users/AdminDK/Desktop/PartitionedTimeout");
		Path filesDirPath = Paths.get("/Users/AdminDK/Desktop/NonTrivialTimedOut");	
		File filesDir = new File(filesDirPath.toString());
		
		int partitions = 19;
		int partitionSize = 70;
		
		// Create directories
		for (int i = 0; i < partitions; i++) {
			Path partitionPath = partitionFolder.resolve("set" + (i+40+1));
			File newPartition = new File(partitionPath.toAbsolutePath().toString());
			newPartition.mkdir();
			System.out.println(i);
		}
		
		
		File[] allFiles = filesDir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".xml");
		    }
		});	
		

		for (int i = 0; i < partitions; i++) {
			Path partitionPath = partitionFolder.resolve("set" + (i+40+1));
			
			for (int j = 0; j < partitionSize; j++) {
							
				File file = allFiles[i*partitionSize + j];
				Path newPath = partitionPath.resolve(file.getName());
				Files.move(file.toPath(), newPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			}			
		}		
	}
}
