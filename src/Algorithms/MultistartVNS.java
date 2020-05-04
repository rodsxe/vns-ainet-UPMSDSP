package Algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import instance.loader.SchedulingInstancesConfig;
import model.Cell;
import operators.function.evaluator.ObjectFunctionEvaluator;
import operators.localsearch.ExternalInsertion;
import operators.localsearch.ExternalSwap;
import operators.localsearch.InternalInsertion;
import operators.localsearch.InternalSwap;


public class MultistartVNS implements Algorithm {

	private Cell best;
	
	private Random random;
	private BoneMarrowOp boneMarrow = new BoneMarrowOp();
	
	private SchedulingInstancesConfig config;
	private ObjectFunctionEvaluator evaluator;
	
	private VND vnd;
	
	private int IT_MAX = 200;
	
	long tempoInicial;
	
	@Override
	public void setInstanceConf(SchedulingInstancesConfig config, ObjectFunctionEvaluator evaluator, Random rand, HashMap<String, String> params) {
			
		best = null;
		this.random = rand;
		this.config = config;
		this.evaluator = evaluator;
		this.evaluator.resetNumberOfObjectFunctionAval();
		
		float N = config.getNumberOfJobs();
		float M = config.getNumberOfMachines();
		
		IT_MAX = (int)((N+M-1));
		
		this.vnd = new VND();
		
		vnd.addLocalSearch(new InternalInsertion(evaluator));
		vnd.addLocalSearch(new InternalSwap(evaluator));		
		vnd.addLocalSearch(new ExternalInsertion(evaluator));
		vnd.addLocalSearch(new ExternalSwap(evaluator));
		
	}

	@Override
	public List<Cell> loadInitialMemory() {
		
		tempoInicial = System.currentTimeMillis();
		
		List<Cell> pop = new ArrayList<Cell>(1);
		
		pop.add(0, new_cell());
		
		best = pop.get(0);
		
		return pop;
		
	}

	
	public long getLocalSearchTime() {return 0;}
	
	private Cell new_cell() {
		
		Cell s = boneMarrow.newRandomCell(random, this.evaluator, this.config);
		localSearch(s);
		return s;
	
	}
	
	private void shake(Cell solution, int k) {
		
		switch (k) {
		case 1:
			
			solution.randomInsertionMoviment();
			break;
		
		case 2:
			
			solution.randomSwapMoviment();
			break;
		
		case 3:
			
			solution.randomInsertionMoviment();
			solution.randomSwapMoviment();
			break;

		default:
			break;
		}
		
		solution.setObjectiveFunction();
		
	}
	
	private void localSearch(Cell newSolution) {
		
		this.vnd.run(random, newSolution);
		
	}
	
	private int neighbourhoodChange(Cell solution, Cell newSolution, int k) {
		
		if(newSolution.getObjectiveFunction() < solution.getObjectiveFunction()) k = 1;
		else k++;
		
		return k;	
		
	}
	
	@Override
	public List<Cell> runOneGeneration(List<Cell> solucoes) {
		
		int IT_S = 0;	
		int k;
		Cell solution = new_cell(), newSolution;
		
		do {
			
			k = 1;
			
			do {
				
				newSolution = solution.clone();
				
				shake(newSolution, k);
				localSearch(newSolution);
				k = neighbourhoodChange(solution, newSolution, k);
				
				if(newSolution.getObjectiveFunction() < solution.getObjectiveFunction()) {
					
					solution = newSolution;
					
					if(solution.getObjectiveFunction() < best.getObjectiveFunction()) {
						
						best = solution;
						solucoes.set(0, newSolution);			
						
					}
					
				} 
				
			} while (k <= 3);
			
			IT_S++;
			
		} while (IT_S < IT_MAX);
		
		return solucoes;
	}

		
	@Override
	public String getParameters() {return null;}

	@Override
	public List<Cell> updateMemory(List<Cell> Ab) {
		Ab.set(0, best);
		return Ab;
	}

	@Override
	public Cell getBest() {
		return best;
	}

}
