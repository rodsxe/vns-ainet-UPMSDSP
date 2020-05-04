package operators.mutations;

import model.Cell;

public class InsertionMutation implements Mutation {

	@Override
	public void apply(Cell cell) {
		
		cell.randomInsertionMoviment();
		
	}

}
