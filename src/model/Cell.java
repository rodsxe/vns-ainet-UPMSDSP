package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Algorithms.VNSaiNET;
import operators.function.evaluator.ObjectFunctionEvaluator;
import operators.mutations.InsertionMutation;
import operators.mutations.Mutation;
import operators.mutations.SwapMutation;

public class Cell {
	
	private ObjectFunctionEvaluator evaluator;
	private Random rand;
	
	private MachineContainer[] machines;
	private Integer nJobs;
		
	private float maturationFactor;
	private float objectiveFunction;
	private float previusObjectiveFunction;
	private double fitness;
	private float initialObjectiveFunction;
	
	public float mutationRate;
	public float supRate;
	public float crRate;
	public float nCRate;
	
	private Mutation[] mutations;
	private int maxNumberOfMutations;
	
	public static float LEARNING_RATE = 0.01f;
	
	public static float CONCENTRATION_DEC_RATE = 0.035f;
	public static float MAX_MUTATIONS = 4;
	public static float SUPRESS_THRESHOLD = 4.f;
	public static float N_CLONES = 6.f;
	public static float BETA_MUT = 0.25f;
	public static float BETA_CLONES = 0.25f;
	
	private Edge[] edges;
	
	private long time;
	private List<Float> timeSolutions;
	
	public Cell(ObjectFunctionEvaluator evaluator, Random rand, MachineContainer[] machines){
		
		this.evaluator = evaluator;
		this.machines = machines;
		this.objectiveFunction = calcObjectiveFunction();
		this.previusObjectiveFunction = objectiveFunction;
		this.maturationFactor = 0;
		timeSolutions = new ArrayList<Float>();
		this.rand = rand;
		mutationRate = rand.nextFloat();
		this.supRate = rand.nextFloat() * MAX_MUTATIONS; 
		this.crRate = CONCENTRATION_DEC_RATE; 
		//this.crRate = rand.nextFloat() * CONCENTRATION_DEC_RATE; 
		//this.nCRate = rand.nextInt(N_CLONES) + 1;
		this.nCRate = rand.nextFloat() * N_CLONES; 
		this.maxNumberOfMutations = rand.nextInt((int)MAX_MUTATIONS) + 1;
		mutations = new Mutation[maxNumberOfMutations];
		for (int i = 0; i < maxNumberOfMutations; i++) mutations[i] = getRandomMutationOp();			
		
	}
	
	private Cell(ObjectFunctionEvaluator evaluator, 
				Random rand, 
				MachineContainer[] maquinas, 
				float objectiveFunction, 
				float previusObjectiveFunction,
				float maturationFactor,
				double fitness,
				float initialSolution, 
				float parentMutationRate,
				float parentSupRate,
				float parentCrRate,
				float parentNCRate,
				Mutation[] mutations){
		
		this.rand = rand;
		this.evaluator = evaluator;
		this.machines = maquinas;
		this.objectiveFunction = objectiveFunction;
		this.previusObjectiveFunction = previusObjectiveFunction;
		this.maturationFactor = maturationFactor;
		timeSolutions = new ArrayList<Float>();
		this.fitness = fitness;
		this.initialObjectiveFunction = initialSolution;
		this.mutations = mutations;
		this.maxNumberOfMutations = mutations.length;
		this.edges = null;
		
		//this.crRate = (this.getFmat() * parentCrRate) + (1 - this.getFmat()) * rand.nextFloat() * CONCENTRATION_DEC_RATE; 
		this.supRate = parentSupRate;
		this.nCRate = parentNCRate;
		this.mutationRate = parentMutationRate;
		this.crRate = parentCrRate; 
		//this.nCRate = (this.getFmat() * parentNCRate) + (1 - this.getFmat()) * rand.nextFloat() * N_CLONES; 
		
		//this.nCRate = parentNCRate;
		//float learning_rate = 0.01f/(float)Math.exp(-VNSaiNET.it/100);
		float learning_rate = LEARNING_RATE;
		
		//this.mutationRate = (this.getFmat() * parentMutationRate) + (1 - this.getFmat()) * rand.nextFloat(); 
		//this.supRate = (this.getFmat() * parentSupRate) + (1 - this.getFmat()) * rand.nextFloat() * MAX_MUTATIONS; 
		
		/*this.crRate =  Math.max(0.f, calcMult(parentCrRate, learning_rate, this.rand));
		this.supRate = Math.max(1.f,calcMult(parentSupRate, learning_rate, this.rand));
		this.nCRate = Math.max(1.f,calcMult(parentNCRate, learning_rate, this.rand));
		this.mutationRate = Math.max(0.f, calcMult(parentMutationRate, learning_rate, this.rand));
		*/
		
		
		//this.supRate = (float)((this.getFmat() * parentSupRate) + (1 - this.getFmat()) * calcMult(parentSupRate, (float)this.getFitness(), MAX_MUTATIONS, this.rand));
		//this.nCRate = (float)((this.getFmat() * parentNCRate) + (1 - this.getFmat()) * calcMult(parentNCRate, (float)this.getFitness(), N_CLONES, this.rand));
				
		//this.supRate = (float)((this.getFmat() * parentSupRate) + (1 - this.getFmat()) * rand.nextFloat() * MAX_MUTATIONS); 
		//this.nCRate = (this.getFmat() * parentNCRate) + (1 - this.getFmat()) * rand.nextFloat() * N_CLONES; 
	
	}
	
	private float calcMult(float actual, float beta, Random rand) {
		
		//return actual + (float)((beta * Math.exp(-fitness)) * rand.nextGaussian());
		//return (float)(actual) + (float)(beta * actual * rand.nextGaussian());
		//float direction = (float)((this.getFmat() * actual) + (1 - this.getFmat()) * best);
		//return (float)(actual) + (float)(beta * direction * rand.nextGaussian());
		float direction =  (float)(actual) + (float)(beta * actual * rand.nextGaussian());
		return (float)((this.getFmat() * actual) + (1 - this.getFmat()) * direction);
		
	}
	
	public void setMutationRate(float mutationRate) {
		this.mutationRate = mutationRate;
	}

	public void setCrRate(float crRate) {
		this.crRate = crRate;
	}

	public void setnCRate(float nCRate) {
		this.nCRate = nCRate;
	}

	public void setMaxNumberOfMutations(float maxNumberOfMutations) {
		this.supRate = maxNumberOfMutations;
	}

	public float getCrRate() {
		return crRate;
	}

	public float getSupRate() {
		return supRate;
	}

	public float getnCRate() {
		return nCRate;
	}

	public float getMaxNumberOfMutations() {
		return this.supRate;//maxNumberOfMutations;
	}

	private Mutation getRandomMutationOp() {
		
		int r = rand.nextInt(2);
		switch (r) {
		case 0:
			return new SwapMutation();
		case 1:
			return new InsertionMutation();
		}
		return null;
	}
		
	public Integer getNumberOfJobs() {
		
		if(nJobs != null) return nJobs;
		
		nJobs = 0;
		for (MachineContainer maquina : machines) {
			nJobs += maquina.size();
		}
		
		return nJobs;
	}
	
	public void setEvaluator(ObjectFunctionEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	public MachineContainer[] getMachines() {
		return machines;
	}
	
	public void setMachines(MachineContainer[] machines) {
		
		this.machines = machines;
		this.objectiveFunction = calcObjectiveFunction();
	
	}

	public List<Float> getTimeSolutions() {
		return timeSolutions;
	}

	public void setTimeSolutions(List<Float> timeSolutions) {
		this.timeSolutions = timeSolutions;
	}

	public void addSolutions(){
		timeSolutions.add(this.getObjectiveFunction());
	}
	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public float getPreviusObjectiveFunction() {
		return previusObjectiveFunction;
	}

	public void setPreviusObjectiveFunction(float previusObjectiveFunction) {
		this.previusObjectiveFunction = previusObjectiveFunction;
	}

	public void increaseMaturationFactor(){
		//maturationFactor = Math.min(1, maturationFactor + CONCENTRATION_DEC_RATE);
		maturationFactor = Math.min(1, maturationFactor + this.crRate);
	}
	
	public void resetMaturationFactor() {
		this.maturationFactor = 0.f;
	}
	
	public float getMaturationFactor(){
		return maturationFactor;
	}
	
	public float getFmat() {
		return 1.f - maturationFactor;
	}
	
	public float getInitialObjFunction() {
		return initialObjectiveFunction;
	}
	
	public void setInitialObjFunction(float initialObjFunction) {
		this.initialObjectiveFunction = initialObjFunction;
	}
	
	public MachineContainer getMachine(int index){
		return machines[index];
	}
	
	public float calcObjectiveFunction(){
		
		return evaluator.getObjectFunctionValue(this);
		
	}
	
	public float getObjectiveFunction() {
		
		return objectiveFunction;
	
	}
	
	public float setObjectiveFunction() {
		
		this.objectiveFunction = calcObjectiveFunction();
		return objectiveFunction;
		
	}
	
	public float getMutationRate() {
		return mutationRate;
	}
	
	
	private Mutation[] getMutations() {
		return mutations;
	}

	public Cell clone(){
		
		MachineContainer[] clonedMachines = new MachineContainer[machines.length];
		
		for (int i = 0; i < clonedMachines.length; i++) {
			clonedMachines[i] = machines[i].clone();
		}
		
		Cell s = new Cell(	this.evaluator, 
							this.rand, 
							clonedMachines, 
							this.getObjectiveFunction(), 
							this.getPreviusObjectiveFunction(),
							this.getMaturationFactor(),
							this.getFitness(),
							this.getInitialObjFunction(), 
							this.getMutationRate(),
							this.supRate,
							this.crRate,
							this.nCRate,
							this.getMutations()
							);
		
		s.getTimeSolutions().addAll(this.timeSolutions);
		
		return s;
		
	}
	
	public void applyMutations(int numberOfMutations) {
		
		for (int i = 0; i < numberOfMutations; i++) {
			mutations[i].apply(this);
		}
		
	}
	
	public MachineContainer getRandomMachineWithCost(Random random, List<MachineContainer> machines) {
		
		MachineContainer choosed = machines.remove(random.nextInt(machines.size()));
		List<MachineContainer> observed = new ArrayList<MachineContainer>();
		float cost;
		
		while (((cost = evaluator.getObjectFunctionValue(this, choosed)) < 0.01) && machines.size() > 0) {
			
			observed.add(choosed);
			choosed = machines.remove(random.nextInt(machines.size()));
		
		}
		
		if(cost == 0)return null;
		
		machines.addAll(observed);
		return choosed;
	
	}
	
	public int getNumberOfMachines() {
	
		return this.machines.length;
		
	}

	public List<MachineContainer> arrayToList(MachineContainer[] machines) {
		
		List<MachineContainer> maquinas = new ArrayList<MachineContainer>();
		int maxIndice = machines.length;
		
		for(int i =0; i < maxIndice; i++ )maquinas.add(machines[i]);		
		
		return maquinas;
	
	}
	
	public void randomSwapMoviment(){
		
		MachineContainer[] machines = getMachines();
		
		int indexm1 = evaluator.getLSMachineIndex(rand, this, this.arrayToList(machines));
		if (indexm1 == -1) indexm1 = this.rand.nextInt(this.machines.length);
		MachineContainer m1 = machines[indexm1];
		
		int iM2 = this.rand.nextInt(this.machines.length);
		MachineContainer m2 = this.machines[iM2];
		int numTM1 = m1.size();
		int numTM2 = m2.size();
		if(numTM1 > 0 && numTM2 > 0 ){
			
			int indiceTM1 = this.rand.nextInt(numTM1);
			int indiceTM2 = this.rand.nextInt(numTM2);
			
			Job t1 = m1.getJob(indiceTM1);
			Job t2 = m2.getJob(indiceTM2);
			m1.replaceJob(indiceTM1, t2);
			m2.replaceJob(indiceTM2, t1);
		
		}
		
	}
	
	public void randomInsertionMoviment(){
		
		MachineContainer[] machines = getMachines();
		int indexm1 = evaluator.getLSMachineIndex(rand, this, this.arrayToList(machines));
		if (indexm1 == -1) indexm1 = this.rand.nextInt(this.machines.length);
		MachineContainer m1 = machines[indexm1];
		
		int numTM1 = m1.size();
		if(numTM1 > 0){
			
			int indiceTM1 = this.rand.nextInt(numTM1);
			Job tarefa = m1.getJob(indiceTM1);
			m1.removerJob(indiceTM1);
			
			int iM2 = this.rand.nextInt(this.machines.length);
			MachineContainer m2 = this.machines[iM2];
			int numTM2 = m2.size();
			int indiceTM2 = 0;
			if(numTM2 > 1) indiceTM2 = this.rand.nextInt(numTM2);
			m2.addJob(tarefa, indiceTM2);
			
		}
		
	}
	
	public Edge[] getEdges(){
		
		if(this.edges != null)return this.edges;
		Edge[] result = new Edge[getNumberOfJobs()];
		for (MachineContainer maquina : machines) {
			List<Job> jobs = maquina.getJobs();
			int anterior = -1;
			for (int j = 0; j < jobs.size(); j++) {
				Job job = jobs.get(j);
				result[job.getId()] = new Edge(maquina.getId(), job.getId(), anterior);
				anterior = job.getId();
			}
			
		}
		
		this.edges = result;
		
		return result;
		
	}
		
	public float getDistanceFromC2(Cell c2){
		
		Edge[] vetorArrestasS1 = this.getEdges();
		Edge[] vetorArrestasS2 = c2.getEdges();
		int distancia = 0;
		for (int j = 0; j < vetorArrestasS1.length; j++) {
			Edge arresta = vetorArrestasS1[j];
			int i = arresta.getI();
			int k = arresta.getK();
			arresta = vetorArrestasS2[j];
			int w = arresta.getI();
			int z = arresta.getK();
			if(i != w || k != z)distancia++;
		}
		
		return (float)distancia/(float)vetorArrestasS1.length;
	}
	
	public void printJobs(){
		for (int i = 0; i < machines.length; i++) {
			System.out.println("M" + i + ":");
			List<Job> list = machines[i].getJobs();
			for (Job tarefa : list) {
				System.out.print("T" + tarefa.getId() + "  -----> ");
			}
			System.out.println();
		}
	}
}




