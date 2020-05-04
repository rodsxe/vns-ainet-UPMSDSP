package Algorithms;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import operators.function.evaluator.ObjectFunctionEvaluator;
import operators.localsearch.ExternalInsertion;
import operators.localsearch.InternalInsertion;
import operators.localsearch.ExternalSwap;
import operators.localsearch.IGSInternalLocalSearch;
import operators.localsearch.InternalSwap;
import instance.loader.SchedulingInstancesConfig;
import model.Cell;


public class VNSaiNET implements Algorithm {
	
	private static final StandardDeviation std = new StandardDeviation(); 
	private static final Mean mean = new Mean();
	
	private Random random;
	private BoneMarrowOp boneMarrow;
	private VND vnd;
	
	private SchedulingInstancesConfig schedulingInstancesConfig;
	private ObjectFunctionEvaluator evaluator;
	
	private List<Cell> memory;
	
	private int N = 20;
	private int N_CLONES = 4;
	private float SUPRESS_THRESHOLD = 0.00001f;
	private float CONCENTRATION_DEC_RATE = 0.00f;
	private float ALPHA = (float)0.4f;
	public static float MAX_NUM_MUTACOES = 4.f;	
	private float BETA_MUT = (float)0.2f;
	private float BETA_CLONES = (float)0.2f;
	private float LEARNING_RATE = 0.008f;
	
	private int[] N_DESIGN = {10, 15, 20, 25};
	private float[] N_ALFA = {0.35f, 0.39f, 0.43f, 0.47f, 0.51f};
	private int[] N_CLONES_DESIGN = {4, 6, 8, 10};
	private float[] MUT_RATE_DESIGN = {1.f, 2.f, 3.f, 4.f};
	private float[] SUPRESS_THRESHOLD_DESIGN = {0.07f, 0.09f, 0.11f, 0.13f};;
	private float[] CONCENTRATION_DEC_RATE_DESIGN = {0.03f, 0.05f, 0.07f, 0.09f};;
	private float[] LEARNING_RATE_DESIGN = {0.008f, 0.01f, 0.012f, 0.014f};;
	
	public VNSaiNET() {
		
		super();
		boneMarrow = new BoneMarrowOp();
		
	}
	
	@Override
	public Cell getBest() {
		// TODO Auto-generated method stub
		return (memory.isEmpty())?null:memory.get(0);
	}

	@Override
	public String getParameters() {
		return "";
	}

	@Override
	public void setInstanceConf(SchedulingInstancesConfig config, ObjectFunctionEvaluator evaluator, Random rand, HashMap<String, String> params) {
		
		this.random = rand;
		this.schedulingInstancesConfig = config;
		this.evaluator = evaluator;
		this.evaluator.resetNumberOfObjectFunctionAval();
		
		if(params!= null){
			
			if(params.containsKey("--n")) N = N_DESIGN[new Integer(params.get("--n")) - 1];
			if(params.containsKey("--nC"))N_CLONES = N_CLONES_DESIGN[new Integer(params.get("--nC")) - 1];
			if(params.containsKey("--alfa")) ALPHA = N_ALFA[new Integer(params.get("--alfa")) - 1];
			if(params.containsKey("--mutRate"))MAX_NUM_MUTACOES = MUT_RATE_DESIGN[new Integer(params.get("--mutRate")) - 1];
			if(params.containsKey("--supTs"))SUPRESS_THRESHOLD = SUPRESS_THRESHOLD_DESIGN[new Integer(params.get("--supTs")) - 1];
			if(params.containsKey("--dec_rate"))CONCENTRATION_DEC_RATE = CONCENTRATION_DEC_RATE_DESIGN[new Integer(params.get("--dec_rate")) - 1];
			if(params.containsKey("--l_rate"))LEARNING_RATE = LEARNING_RATE_DESIGN[new Integer(params.get("--l_rate")) - 1];
					
		}
		
		Cell.CONCENTRATION_DEC_RATE = CONCENTRATION_DEC_RATE;
		Cell.SUPRESS_THRESHOLD = SUPRESS_THRESHOLD;
		Cell.MAX_MUTATIONS = (int)MAX_NUM_MUTACOES;
		Cell.N_CLONES = (int)N_CLONES;
		Cell.BETA_MUT = BETA_MUT;
		Cell.BETA_CLONES = BETA_CLONES;
		Cell.LEARNING_RATE = LEARNING_RATE;

		this.vnd = new VND();
		
		//vnd.addLocalSearch(new IGSInternalLocalSearch(evaluator, 3));
		vnd.addLocalSearch(new InternalInsertion(evaluator));
		vnd.addLocalSearch(new InternalSwap(evaluator));		
		vnd.addLocalSearch(new ExternalInsertion(evaluator));
		vnd.addLocalSearch(new ExternalSwap(evaluator));
		//vnd.addLocalSearch(new IGSInternalLocalSearch(evaluator, 3));

	}
	
	@Override
	public List<Cell> loadInitialMemory() {
		
		this.memory = new ArrayList<Cell>();
		
		List<Cell> result = new ArrayList<Cell>(N);
		
		for (int i = 0; i < N; i++){
			
			Cell s = new_cell();
			result.add(s);
		
		}
		
		result = sortByObjectiveFunction(result);
		
		return result;
	}

	public Cell new_cell() {
		
		Cell s = boneMarrow.newRandomCell(random, this.evaluator, this.schedulingInstancesConfig);
		this.vnd.run(this.random, s);
		return s;
	
	}
	
	private void updateParameters(List<Cell> Ab) {
		
		/*double[] mut = new double[Ab.size()];
		double[] mutMax = new double[Ab.size()];
		
		for (int i = 0; i < Ab.size(); i++) {
			
			Cell cell = Ab.get(i);
			mutMax[i] = cell.getMaxNumberOfMutations();
			mut[i] = cell.getMutationRate();
			
		}
		
		float mMutMax = (float)mean.evaluate(mutMax);
		float stdMutMax = (float)std.evaluate(mutMax, mMutMax);
		float mMut = (float)mean.evaluate(mut);
		float stdMut = (float)std.evaluate(mut, mMut);
		System.out.println("MulMax:" + mMutMax + "; Desv:" + stdMutMax);
		//System.out.println("Mut:" + mMut + "; Desv:" + stdMut);*/
		
		for (Cell cell : Ab) {
			
			cell.setMaxNumberOfMutations(calcParameter(cell.getFmat(), cell.getMaxNumberOfMutations(), 0, 0, 1, MAX_NUM_MUTACOES));
			cell.setMutationRate(calcParameter(cell.getFmat(), cell.getMutationRate(), 0, 0, 0, 1));
			
		}
		
	}
	
	private float calcParameter(float fmat, float actualParameter, float mean, float std, float min, float max) {
		
		return Math.min(max, Math.max(min, (fmat * actualParameter) + ((1 - fmat) * ((float)random.nextFloat() * max))));
		//float r = random.nextFloat();
		/*eturn Math.min(max, Math.max(min, (fmat * actualParameter) + 
											((1 - fmat) * (((float)random.nextGaussian() * std + mean)  
														   //+ ((1 - r) * random.nextFloat()* max)
														  )
											)
									  )
						);*/
		//return Math.min(max, Math.max(min, actualParameter + 0.1f * ((float)random.nextGaussian() * std + mean)));
		
	}
	
	private List<Cell> sortByObjectiveFunction(List<Cell> Ab){
		Collections.sort(Ab, new Comparator<Cell>() {
			@Override
			public int compare(Cell s1, Cell s2) {
				
				if(s1.getObjectiveFunction() < s2.getObjectiveFunction())return -1;
				else if(s1.getObjectiveFunction() > s2.getObjectiveFunction())return 1;
				else if(s1.getFmat() < s2.getFmat())return 1; 
				else if(s1.getFmat() > s2.getFmat())return -1; 
				else return 0;
				
			}
		});
		return Ab;
	}
	
	private List<Cell> sortByFitness(List<Cell> Ab){
		Collections.sort(Ab, new Comparator<Cell>() {
			@Override
			public int compare(Cell s1, Cell s2) {
				
				if (s1.getFitness() > s2.getFitness()) return -1;
				else if (s1.getFitness() < s2.getFitness())return 1;
				else if(s1.getObjectiveFunction() < s2.getObjectiveFunction())return -1;
				else if(s1.getObjectiveFunction() > s2.getObjectiveFunction())return 1;
				else return 0;
				
			}
		});
		return Ab;
	}
	
	private void updateFitness(List<Cell> pop){
		
		pop = sortByObjectiveFunction(pop);
		
		float minMake = 1;
		float maxMake = pop.size();
		
		for (int i = 0; i < pop.size(); i++) {
			
			Cell cell = pop.get(i);
			float funcPercent = 1 - ((new Float(i + 1) - minMake)/(maxMake - minMake));
			cell.setFitness((1-ALPHA) * funcPercent + ALPHA * cell.getFmat());
			
		}
		
		
	}

	private void updateFitnessObj(List<Cell> pop){
		
		pop = sortByObjectiveFunction(pop);
		
		float minMake = pop.get(0).getObjectiveFunction();
		float maxMake = pop.get(pop.size() - 1).getObjectiveFunction();
		
		for (int i = 0; i < pop.size(); i++) {
			
			Cell cell = pop.get(i);
			float funcPercent = 1 - ((cell.getObjectiveFunction() - minMake)/(maxMake - minMake));
			cell.setFitness((1-ALPHA) * funcPercent + ALPHA * cell.getFmat());
			
		}
		
		
	}

	private List<Cell> clonalExpansion(List<Cell> cellsPop){
		
		List<Cell> pop = new ArrayList<Cell>();
		
		int n_clones_by_cell;
		double affinity;
		for (int i = 0; i < cellsPop.size(); i++) {
			Cell cell = cellsPop.get(i);
			affinity = cell.getFitness();
			n_clones_by_cell  = N_CLONES;
			
			//n_clones_by_cell  = Math.max((int)(affinity * N_CLONES), 1);
			//n_clones_by_cell  = Math.max(Math.round(cell.getnCRate()), 1);
			
			for(int c = 0; c < n_clones_by_cell; c++) 
				pop.add(cellsPop.get(i).clone());
		
		}
		
		return pop;
	}
	
	private List<Cell> maturate(List<Cell> pop){
		int i = 0;
		for (Cell solucao : pop) {
			
			//pop.set(i, VNS(solucao));
			//i++;
			hypermutation(random, solucao);
			vnd.run(this.random, solucao);
			
		}
		return pop;
	}
	
	private void hypermutation(Random random, Cell cell) {
		
		//double exp = Math.max(Math.exp(-cell.getFitness()) * MAX_NUM_MUTACOES, 1);
		double exp = Math.max(Math.exp(-cell.getFitness()) * (float)cell.getMaxNumberOfMutations(), 1);
		
		//double exp = Math.max(Math.exp(-cell.getFitness()) * MAX_NUM_MUTACOES, 1);
		
		double taxaInsercaoExterna = Math.max(1.0, exp);
		
		//cell.applyMutations((int)taxaInsercaoExterna);
		int operacoesExatas = (int)taxaInsercaoExterna;
		for (int i = 0; i < operacoesExatas; i++) {
			float r = random.nextFloat();
			if(r < cell.getMutationRate()) cell.randomInsertionMoviment();
			else cell.randomSwapMoviment();
			/*int r = random.nextInt(2);
			switch (r) {
			case 0:
				cell.randomInsertionMoviment();
				break;
			case 1:
				cell.randomSwapMoviment();
				break;
			default:
				break;
			}*/
				
		};
							
	}
	
	private boolean cellMustBeSupressed(List<Cell> memoria, Cell s2){
		
		boolean toSupress = false;
		float cell_distance;
		for (Cell s1 : memoria) {
			
			cell_distance = s1.getDistanceFromC2(s2);
			
			if (cell_distance <= s1.getSupRate()) {
				toSupress = true;
				break;
			}
		
		}
		
		return toSupress;
	}
	
	private float getDistance(List<Cell> memoria, Cell s2){
		
		float min_distance = Float.MAX_VALUE;
		float cell_distance;
		for (Cell s1 : memoria) {
			
			cell_distance = s1.getDistanceFromC2(s2);
			if (cell_distance < min_distance) min_distance = cell_distance;
		
		}
		
		return min_distance;
	}
	
	private void removeNullAgeCells(List<Cell> Ab){
		
		List<Cell> newMemory = new ArrayList<Cell>();
		
		boolean updateMemory = false;
		
		Iterator it = Ab.iterator();
		
		while (it.hasNext()) {
			
			Cell cell = (Cell) it.next();
			
			if (cell.getFmat() < 0.001){
				it.remove();
				if (evaluator.isValidCell(cell)){
					
					this.memory.add(cell);
					updateMemory = true;
				
				}
				
			}
					
		}
		
		if(updateMemory){
			
			this.memory = sortByObjectiveFunction(this.memory);
			if (!this.memory.isEmpty()) newMemory.add(this.memory.remove(0));
			supressAb2(this.memory, newMemory, N, SUPRESS_THRESHOLD);
			this.memory = newMemory;
		
		}
				
	}
	
	private List<Cell> supress(List<Cell> Ab){
		
		List<Cell> memoriaImunologica = new ArrayList<Cell>();
		
		Ab = sortByObjectiveFunction(Ab);
		//Ab = sortByFitness(Ab);
		
		memoriaImunologica.add(Ab.remove(0));
		Ab = sortByFitness(Ab);
		
		List<Cell> excludedCells = supressAb(Ab, memoriaImunologica, N, SUPRESS_THRESHOLD);
		//List<Cell> excludedCells = supressAb3(Ab, memoriaImunologica, N);
		
		updateMemory(excludedCells);
				
		return memoriaImunologica;
		
	}
	
	private void supressAb2(List<Cell> Ab, List<Cell> newAb, int N, float SUPRESS_THRESHOLD) {
		
		float distance;
		for (int i = 0; i < Ab.size() && newAb.size() < N; i++) {
			
			Cell cell = Ab.get(i);
							
			distance = getDistance(newAb, cell);
			
			if (distance >= SUPRESS_THRESHOLD)
				
				newAb.add(cell);
					
		}
		
	}
	
	private List<Cell> supressAb3(List<Cell> Ab, List<Cell> newAb, int N) {
		
		List<Cell> supressedCells = new ArrayList<>();
		
		int i;
		
		for (i = 0; i < Ab.size() && newAb.size() < N; i++) {
			
			Cell cell = Ab.get(i);
			
			if (!cellMustBeSupressed(newAb, cell)) newAb.add(cell);
			
			else supressedCells.add(cell);
			
		}
		
		for ( ; i < Ab.size(); i++) { supressedCells.add(Ab.get(i));}
		
		return supressedCells;
		
	}
	
	private List<Cell> supressAb(List<Cell> Ab, List<Cell> newAb, int N, float SUPRESS_THRESHOLD) {
		
		List<Cell> excludedCells = new ArrayList<>();
		
		float distance;
		int i;
		
		for (i = 0; i < Ab.size() && newAb.size() < N; i++) {
			
			Cell cell = Ab.get(i);
							
			distance = getDistance(newAb, cell);
			
			if (distance >= SUPRESS_THRESHOLD)
				
				newAb.add(cell);
			
			else excludedCells.add(cell);
			
		}
		
		for ( ; i < Ab.size(); i++) { excludedCells.add(Ab.get(i));}
		
		return excludedCells;
		
	}
	
	private void updateCellsMaturationFactor(List<Cell> cells){
		
		for (Cell cell : cells) {
			
			if (cell.getObjectiveFunction() >= (cell.getPreviusObjectiveFunction())) {
				cell.increaseMaturationFactor();
			} else {
				cell.resetMaturationFactor();
				cell.setPreviusObjectiveFunction(cell.getObjectiveFunction());
			}
			
		}
		
	}
	
	public void newCells(List<Cell> cellsPop, int pop_size){
		
		while(cellsPop.size() < pop_size){
			
			Cell s = new_cell();
			cellsPop.add(s);
			
		}
		
	}
	
	public void removeConstrainsValidationCells(List<Cell> bcellPop){
		
		Iterator<Cell> it = bcellPop.iterator();
		while (it.hasNext()) {
			
			Cell cell = (Cell) it.next();
			if (!this.evaluator.isValidCell(cell)) {
				
				it.remove();
				
			}
			
		}
		
	}
	
	@Override
	public List<Cell> runOneGeneration(List<Cell> bcellPop){
		
		updateFitness(bcellPop);
		
		List<Cell> clones = clonalExpansion(bcellPop);
		
		clones = maturate(clones);
		
		bcellPop.addAll(clones);
		
		for (Cell cell : bcellPop) cell.setObjectiveFunction();		
		
		updateCellsMaturationFactor(bcellPop);
		
		updateParameters(bcellPop);
		
		removeNullAgeCells(bcellPop);
		
		if (!bcellPop.isEmpty()){
			
			updateFitness(bcellPop);
			bcellPop = supress(bcellPop);
		
		}
		
		newCells(bcellPop, N);
		
		return bcellPop;
		
	}
	
	public List<Cell> updateMemory(List<Cell> Ab){
		
		
		
		List<Cell> newMemory = new ArrayList<Cell>();
		
		Iterator it = Ab.iterator();
		
		while (it.hasNext()) {
			
			Cell cell = (Cell) it.next();
			
			if (evaluator.isValidCell(cell)){
				
				this.memory.add(cell);
				
			}
				
		}
		
		this.memory = sortByObjectiveFunction(this.memory);
		
		if (!this.memory.isEmpty()) newMemory.add(this.memory.remove(0));
		
		supressAb2(this.memory, newMemory, N, SUPRESS_THRESHOLD);
		this.memory = newMemory;
		
		return this.memory;
		
	}
	
}
