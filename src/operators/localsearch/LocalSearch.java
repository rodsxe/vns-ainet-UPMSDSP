package operators.localsearch;
import java.util.Random;

import model.Cell;

public interface LocalSearch {
	
	public boolean run(Random rand, Cell s, Cell bestSol);
	
}
