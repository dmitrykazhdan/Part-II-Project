package InferenceRules;

import java.util.ArrayList;
import java.util.List;

public class PermutationGenerator<T> {
	
	
	public  List<List<T>> generatePermutations (List<T> inputList) {
		
		List<List<T>> perms = new ArrayList<List<T>>();
		
		if (inputList.size() == 0) {
			perms.add(new ArrayList<T>());
			return perms;
		}
		
		
		for (T item : inputList) {
			List<T> copy = new ArrayList<T>(inputList);
			copy.remove(item);
			List<List<T>> subPermsList = generatePermutations(copy);
			
			for (List<T> subPerm : subPermsList) {
				subPerm.add(item);			
			}
			
			perms.addAll(subPermsList);			
		}
		
		return perms;
	}
	
	
	

}
