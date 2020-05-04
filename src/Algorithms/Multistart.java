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

public class Multistart implements Algorithm {

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

	
	private Cell new_cell() {
		
		Cell s = boneMarrow.newRandomCell(random, this.evaluator, this.config);
		localSearch(s);
		return s;
	
	}
	
	
	private void localSearch(Cell newSolution) {
		
		this.vnd.run(random, newSolution);
		
	}
	
	
	
	@Override
	public List<Cell> runOneGeneration(List<Cell> solucoes) {
		
		Cell solution = new_cell();
		
		if(solution.getObjectiveFunction() < best.getObjectiveFunction()) {
			
			best = solution;
			solucoes.set(0, solution);			
			
		}
		
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
