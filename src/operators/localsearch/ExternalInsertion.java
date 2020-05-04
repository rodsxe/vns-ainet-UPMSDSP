package operators.localsearch;

import java.util.List;
import java.util.Random;

import com.sun.tools.javac.util.Pair;

import model.MachineContainer;
import model.Cell;
import operators.function.evaluator.ObjectFunctionEvaluator;
import model.Job;

public class ExternalInsertion implements LocalSearch {
	
	private ObjectFunctionEvaluator evaluator;
	
	public ExternalInsertion(ObjectFunctionEvaluator evaluator) {
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
				m1.removerJob(indice.fst);
				m2.addJob(t1, indice.snd);
				foundNewBest = true;
			
 			} 
			
		}
		
		return foundNewBest;
		
	}
	
	private Pair<Integer, Integer> getBestImprovement(Cell cell, MachineContainer m1, MachineContainer m2, Random rand, float costM1, float costM2) {
		
		float actualCost = evaluator.getLSCost(costM1, costM2);//Math.max(costM1, costM2);// costM1 + costM2;
						
		Pair<Integer, Integer> indice = null;
		
		Job jobM1; 
				
		for (int i = 0; i < m1.size(); i++) {
			
			jobM1 = m1.removerJob(i);
			costM1 = evaluator.getObjectFunctionValue(cell, m1);
						
			for (int j = 0; j <= m2.size(); j++) {
				
				m2.addJob(jobM1, j);
				costM2 = evaluator.getObjectFunctionValue(cell, m2);
				
				if (evaluator.getLSCost(costM1, costM2) < actualCost) {
					
					actualCost = evaluator.getLSCost(costM1, costM2);
					indice = new Pair<Integer, Integer>(i, j);
					
				}
								
				m2.removerJob(j);
							
			}
			
			m1.addJob(jobM1, i);
			
		}
		
		return indice;
		
	}
		
}
