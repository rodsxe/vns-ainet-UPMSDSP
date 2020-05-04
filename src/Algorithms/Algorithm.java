package Algorithms;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import instance.loader.SchedulingInstancesConfig;
import model.Cell;
import operators.function.evaluator.ObjectFunctionEvaluator;


public interface Algorithm {
	
	public void setInstanceConf(SchedulingInstancesConfig config, ObjectFunctionEvaluator evaluator, Random rand, HashMap<String, String> params);
		
	public List<Cell> loadInitialMemory();
	
	public List<Cell> runOneGeneration(List<Cell> solucoes);
	
	public String getParameters();
	
	public List<Cell> updateMemory(List<Cell> Ab);
	
	public Cell getBest();
}
