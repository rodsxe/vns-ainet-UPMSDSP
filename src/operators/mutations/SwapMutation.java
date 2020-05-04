package operators.mutations;

import model.Cell;

public class SwapMutation implements Mutation {

	@Override
	public void apply(Cell cell) {
		
		cell.randomSwapMoviment();

	}

}
