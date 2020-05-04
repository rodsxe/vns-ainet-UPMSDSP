package operators.localsearch;

import java.util.List;
import java.util.Random;

import model.MachineContainer;
import model.Cell;
import operators.function.evaluator.ObjectFunctionEvaluator;

public class InternalSwap implements LocalSearch {
	
	private ObjectFunctionEvaluator evaluator;
	
	public InternalSwap(ObjectFunctionEvaluator evaluator) {
		super();
		this.evaluator = evaluator;
	}
	
	@Override
	public boolean run(Random random, Cell s, Cell bestSol) {
		
		List<MachineContainer> machines = s.arrayToList(s.getMachines());
		
		int indexMachine = evaluator.getLSMachineIndex(random, s, machines);
		if(indexMachine < 0)return false;
		
		MachineContainer machine = machines.get(indexMachine);
		
		int numberOfJobs = machine.size();
		
		int bestJ = -1;
		int bestW = -1;
		float newCost, minCost = evaluator.getObjectFunctionValue(s, machine);
				
		for (int j = 0; j < numberOfJobs; j++) {
			
			for (int w = j + 1; w < numberOfJobs; w++) {
				
				machine.swapJobs(j, w);
				newCost = evaluator.getObjectFunctionValue(s, machine);
				
				if(newCost < minCost){
					
					minCost = newCost;
					bestJ = j;
					bestW = w;
					
				}
				
				machine.swapJobs(j, w);
										
			}
			
		}
		
		if(bestJ != -1){
			
			machine.swapJobs(bestJ, bestW);
			return true;
		
		}
		
		return false;

	}
	
}
