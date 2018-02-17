package InferenceRules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;

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
				perms.add(subPerm);
			}					
		}	
		return perms;
	}
	
	
	public Set<Set<T>> generateStrictNonEmptyPowerSet(Set<T> originalSet) {
		
		Set<Set<T>> allSets = generatePowerSet(originalSet);
		Set<Set<T>> strictNonEmptySets = new HashSet<Set<T>>();
		
		for (Set<T> set : allSets) {
			if (set.size() > 0 && set.size() < originalSet.size()) {
				strictNonEmptySets.add(set);
			}
		}
		return strictNonEmptySets;
	}
	
	public Set<Set<T>> generatePowerSet(Set<T> originalSet) {
		
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : generatePowerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }
	
	
	

}
