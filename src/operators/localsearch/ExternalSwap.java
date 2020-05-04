package operators.localsearch;

import java.util.List;
import java.util.Random;

import com.sun.tools.javac.util.Pair;

import model.MachineContainer;
import model.Cell;
import operators.function.evaluator.ObjectFunctionEvaluator;
import model.Job;

public class ExternalSwap implements LocalSearch {
	
	private ObjectFunctionEvaluator evaluator;
	
	public ExternalSwap(ObjectFunctionEvaluator evaluator) {
		super();
		this.evaluator = evaluator;
		
	}

	@Override
	public boolean run(Random rand, Cell s, Cell bestSol) {
		
		boolean foundNewBest = false;
		List<MachineContainer> notExploredMachines = s.arrayToList(s.getMachines());
		
		int indexMachine = evaluator.getLSMachineIndex(rand, s, notExploredMachines);
		if(indexMachine < 0)return false;
		
		MachineContainer m1 = notExploredMachines.remove(indexMachine);
				
		while(!foundNewBest && notExploredMachines.size() > 0){
				
			indexMachine = rand.nextInt(notExploredMachines.size());
			MachineContainer m2 = notExploredMachines.remove(indexMachine);
 			
			float costM1 = evaluator.getObjectFunctionValue(s, m1);
			float costM2 =  evaluator.getObjectFunctionValue(s, m2);
			
			Pair<Integer, Integer> indice = getBestImprovement(s, m1, m2, rand, costM1, costM2);
			
			if (indice != null) {
				
				Job t1 = m1.getJob(indice.fst);
				Job t2 = m2.getJob(indice.snd);
				m1.replaceJob(indice.fst, t2);
				m2.replaceJob(indice.snd, t1);
				foundNewBest = true;
				
			}
			
		}
		
		return foundNewBest;
		
	}
	
	private Pair<Integer, Integer> getBestImprovement(Cell cell, MachineContainer m1, MachineContainer m2, Random rand, float costM1, float costM2) {
		
		float actualCost = evaluator.getLSCost(costM1, costM2);//costM1 + costM2;
		Pair<Integer, Integer> indice = null;
		Job jobM1, jobM2;
		
		for (int i = 0; i < m1.size(); i++) {
			
			jobM1 = m1.getJob(i);
			
			for (int j = 0; j < m2.size(); j++) {
				
				jobM2 = m2.getJob(j);
				m1.replaceJob(i, jobM2);
				m2.replaceJob(j, jobM1);
				
				costM1 = evaluator.getObjectFunctionValue(cell, m1);
				costM2 = evaluator.getObjectFunctionValue(cell, m2);				
				
				if (evaluator.getLSCost(costM1, costM2) < actualCost) {
					actualCost = evaluator.getLSCost(costM1, costM2);
					indice = new Pair<Integer, Integer>(i, j);
				}
				
				m1.replaceJob(i, jobM1);
				m2.replaceJob(j, jobM2);
							
			}
			
		}
		
		return indice;
		
	}	
			
}
