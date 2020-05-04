package Algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Cell;
import operators.localsearch.LocalSearch;

public class VND {

	private List<LocalSearch> seekers = new ArrayList<LocalSearch>();
	
	public void addLocalSearch(LocalSearch localSearch) { 
		seekers.add(localSearch);
	}
	
	public void run(Random rand, Cell s){
		
		List<LocalSearch> removedLSOp = new ArrayList<LocalSearch>();
		while (!seekers.isEmpty()) {
			
			LocalSearch ls = seekers.remove(0);
			removedLSOp.add(ls);
						
			if (ls.run(rand, s, null)) {
				seekers.addAll(removedLSOp);
				removedLSOp.clear();
			} 
			
		}
		
		seekers.addAll(removedLSOp);
		s.setObjectiveFunction();
	}
	
	
}
