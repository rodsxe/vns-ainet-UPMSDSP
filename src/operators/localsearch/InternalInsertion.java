package operators.localsearch;

import java.util.List;
import java.util.Random;

import model.MachineContainer;
import model.Cell;
import operators.function.evaluator.ObjectFunctionEvaluator;
import model.Job;

public class InternalInsertion implements LocalSearch {

	private ObjectFunctionEvaluator evaluator;
	
	public InternalInsertion(ObjectFunctionEvaluator evaluator) {
		
		super();
		this.evaluator = evaluator;
		
	}
	
	@Override
	public boolean run(Random rand, Cell s, Cell bestSol) {
		
		List<MachineContainer> machines = s.arrayToList(s.getMachines());
		
		int indexMachine = evaluator.getLSMachineIndex(rand, s, machines);
		if(indexMachine < 0)return false;
		
		MachineContainer machine = machines.get(indexMachine);
		
		int numberOfJobs = machine.size();
		int bestJob = -1;
		int bestPosition = -1;
		float newCost, actualCost = evaluator.getObjectFunctionValue(s, machine);
		
		for (int i = 0; i < numberOfJobs; i++) {
			
			Job t1 = machine.removerJob(i);
			
			for (int j = 0; j < numberOfJobs; j++) {
					
				machine.addJob(t1, j);
				
				newCost = evaluator.getObjectFunctionValue(s, machine);
				if (newCost < actualCost) {
					
					actualCost = newCost;
					bestJob = i;
					bestPosition = j;
				
				}
				
				machine.removerJob(j);
				
			}
			
			machine.addJob(t1, i);
		}
		
		
		if (bestJob != -1) {
			
			Job t = machine.getJob(bestJob);
			machine.removerJob(bestJob);
			machine.addJob(t, bestPosition);
			return true;
		
		}
		
		return false;
		
	}
	
}
