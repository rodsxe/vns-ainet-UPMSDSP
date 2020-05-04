package operators.localsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Cell;
import model.Job;
import model.MachineContainer;
import operators.function.evaluator.ObjectFunctionEvaluator;

public class IGSInternalLocalSearch implements LocalSearch {
	
	private ObjectFunctionEvaluator evaluator;
	
	private int K;
	
	public IGSInternalLocalSearch(ObjectFunctionEvaluator evaluator, int K) {
		super();
		this.evaluator = evaluator;
		this.K = K;
	}
	
	public void setK(int k) {this.K = k;}
	
	@Override
	public boolean run(Random rand, Cell s, Cell bestSol) {
		
		//if(!true) return false; 
		Cell clone = s.clone();
		MachineContainer[] maquinas = clone.getMachines();
		
		MachineContainer sortedMachine;
		
		int bestM = -1;
		int bestJ = -1;
		float newCost, machineCost, minCost;
		
		Job job;
		Job[] sortedJobs = new Job[K];
		
		List<MachineContainer> notExploredMachines = clone.arrayToList(maquinas);
		
		int indexMachine = evaluator.getLSMachineIndex(rand, s, notExploredMachines);
		
		for (int i = 0; i < K; i++){
			
			sortedMachine = maquinas[indexMachine];
			sortedJobs[i] = sortedMachine.removerJob(rand.nextInt(sortedMachine.size()));
		
		}
		
		for (int i = 0; i < K; i++) {
				
			minCost = Float.MAX_VALUE;
			job = sortedJobs[i];
			
			for (int m = 0; m < maquinas.length; m++) {
				
				sortedMachine = maquinas[m];
				machineCost = evaluator.getObjectFunctionValue(clone, sortedMachine);
				
				for (int j = 0; j <= sortedMachine.size(); j++) {
					
					sortedMachine.addJob(job, j);
					newCost = evaluator.getObjectFunctionValue(clone, sortedMachine);
					
					if(newCost - machineCost < minCost){
						
						minCost = newCost - machineCost;
						bestJ = j;
						bestM = m;
						
					}
					
					sortedMachine.removerJob(j);
				
				}
				
			}
			
			maquinas[bestM].addJob(job, bestJ);
			
		}
		
		if (clone.setObjectiveFunction() < s.setObjectiveFunction()) {
			
			s.setMachines(clone.getMachines());
			s.setObjectiveFunction();
			return true;
			
		}
		
		
		return false;
		
	}

}
