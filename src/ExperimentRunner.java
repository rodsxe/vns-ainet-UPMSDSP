import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import com.sun.tools.javac.util.Pair;

import Algorithms.Algorithm;
import instance.loader.BestSol;
import instance.loader.LoaderInstance;
import instance.loader.SchedulingInstancesConfig;
import model.Cell;
import operators.function.evaluator.ObjectFunctionEvaluator;

public class ExperimentRunner {
	
	public static final int STOPPPING_CRITERIA_ITERATIONS_WITHOUT_IMPROVEMENT = 1;
	public static final int STOPPPING_CRITERIA_TIME = 2;
	public static final int STOPPPING_CRITERIA_TARGET = 3;
	public static final int STOPPPING_CRITERIA_ITERATIONS = 4;
	public static final int STOPPPING_CRITERIA_NUMBER_EVAL = 5;
	
	public static final int EXECUTION_TYPE_DEFAULT = 1;
	public static final int EXECUTION_TYPE_PARAMETER_CALIBRATION = 2;
	public static final int EXECUTION_TYPE_PARAMETER_ANALYSIS = 3;
	public static final int EXECUTION_TYPE_DESIGN_BY_INSTANCE = 4;
	
	private static int[][][] setupTimes;
	private static int[][] processingCosts;
	private HashMap<String, BestSol> bestSolutions;
	
	private Random rand;
	private int stoppingCriteria;
	private int executionType;
	private long number_of_eval;
	private Algorithm algorithm;
	private LoaderInstance loader;
	private ObjectFunctionEvaluator evaluator;	
	
	/*Path to the files*/
	private String solPath;
	private String mainDir;
	private String writeSolDir;
	private String resultArqName;
	private String[] fileDir;
	private String FILE_TYPE;
	private String[] levels;
	private String design_conf;
	
	
	private int RUNS_NUMBER;
	
	private double PERCENTUAL_ALVO;
	private int IT_MAX;
	private long MAX_NUM_EVAL;
	private int[] millis;
	
	
	public ExperimentRunner() {
		
		super();
		this.algorithm = ExperimentConfig.algoritmo;
		this.loader = ExperimentConfig.loader;
		this.rand = ExperimentConfig.rand;
		this.stoppingCriteria = ExperimentConfig.stopping_criteria;
		this.mainDir = ExperimentConfig.main_dir;
		this.solPath = ExperimentConfig.main_dir + ExperimentConfig.file_of_target_value_of_instance;
		this.writeSolDir = ExperimentConfig.dir_to_write_the_best_solutions;
		this.resultArqName = ExperimentConfig.result_file_name;
		this.fileDir = ExperimentConfig.dir_instances;
		this.FILE_TYPE = ExperimentConfig.file_instance_type;
		this.RUNS_NUMBER = ExperimentConfig.number_of_experiments_per_instance;
		this.PERCENTUAL_ALVO = ExperimentConfig.percentual_alvo;
		this.IT_MAX = ExperimentConfig.iterations_without_improvement;
		this.millis = ExperimentConfig.tempo_millis;
		this.levels = ExperimentConfig.levels_parameters;
		this.design_conf = ExperimentConfig.parameter_calibration_design_conf;
		this.executionType = ExperimentConfig.execution_type;
		this.number_of_eval = ExperimentConfig.number_of_eval;
		
	}
	
	public ExperimentRunner(Random rand) {
		
		super();
		this.rand = rand;
		this.algorithm = ExperimentConfig.algoritmo;
		this.loader = ExperimentConfig.loader;
		this.stoppingCriteria = ExperimentConfig.stopping_criteria;
		this.mainDir = ExperimentConfig.main_dir;
		this.solPath = ExperimentConfig.main_dir + ExperimentConfig.file_of_target_value_of_instance;
		this.writeSolDir = ExperimentConfig.dir_to_write_the_best_solutions;
		this.resultArqName = ExperimentConfig.result_file_name;
		this.fileDir = ExperimentConfig.dir_instances;
		this.FILE_TYPE = ExperimentConfig.file_instance_type;
		this.RUNS_NUMBER = ExperimentConfig.number_of_experiments_per_instance;
		this.PERCENTUAL_ALVO = ExperimentConfig.percentual_alvo;
		this.IT_MAX = ExperimentConfig.iterations_without_improvement;
		this.millis = ExperimentConfig.tempo_millis;
		this.levels = ExperimentConfig.levels_parameters;
		this.design_conf = ExperimentConfig.parameter_calibration_design_conf;
		this.executionType = ExperimentConfig.execution_type;
		this.number_of_eval = ExperimentConfig.number_of_eval;
		
	}

	private void loadInstance(String path, HashMap<String, String> params){
		
		try{
			
			processingCosts = loader.loadProcessTime(path);
			setupTimes = loader.loadSetupTime(path);
			bestSolutions = loader.loadBestSol(solPath);
			SchedulingInstancesConfig config = new SchedulingInstancesConfig(setupTimes, processingCosts, loader.loadDueDate(path));
			this.evaluator = loader.getEvaluator(path);
			this.evaluator.setSchedulingConfig(config);
			algorithm.setInstanceConf(config,
										evaluator,
										rand, 
										params);
			
        }
        catch(FileNotFoundException e){
        	e.printStackTrace();
            System.out.println("Arquivo n�o encontrado.");
        }
        catch(IOException o){
        	o.printStackTrace();
            System.out.println("ERRO");
        }
	}
	
	private void setMaxEval() {
		
		this.MAX_NUM_EVAL = ((processingCosts[0].length + processingCosts.length - 1) / (processingCosts.length - 1)) * this.number_of_eval;
		System.out.println(processingCosts[0].length + ":" + processingCosts.length);	
	}
	
	private long getProcessTime(int milli) {
		
		long processTime = ((processingCosts[0].length + processingCosts.length - 1) / (processingCosts.length - 1)) * milli;
		return processTime;
		
	}
	
	private boolean stoppingCriteria(int it, int itWithoutImproviment, long processTime, long totalTime, long target, float objFValue, long numEval){
		
		if(stoppingCriteria == STOPPPING_CRITERIA_TIME)
			return (totalTime > processTime)?false:true;
		else if(stoppingCriteria == STOPPPING_CRITERIA_TARGET)
			return (objFValue > target && totalTime > processTime)?false:true;
		else if(stoppingCriteria == STOPPPING_CRITERIA_ITERATIONS_WITHOUT_IMPROVEMENT)
			return (itWithoutImproviment < IT_MAX)?false:true;
		else if(stoppingCriteria == STOPPPING_CRITERIA_ITERATIONS)
			return (it < IT_MAX)?false:true;
		else if(stoppingCriteria == STOPPPING_CRITERIA_NUMBER_EVAL)
			return (numEval < MAX_NUM_EVAL)? false : true;
		
		return true;
		
	}
	
	public Long getTarget(String instance){
		
		String instanciaName = instance.substring(0, instance.indexOf(FILE_TYPE));
		BestSol bestSol = bestSolutions.get(instanciaName);
		return (long)(bestSol.getBestSol() * PERCENTUAL_ALVO);
		
	}
	
	public Cell getBestValidCell(List<Cell> cells){
		
		cells = sortObjFunction(cells);
		for (Cell cell : cells) {
			if(evaluator.isValidCell(cell)) return cell;
		}
		
		return null;
		
	}
	
	public Cell execOneTime(String arqPath, String instance, PrintWriter printWriter, HashMap<String, String> params) throws IOException, ParseException{
		
		this.loadInstance(arqPath, params);
		
		long maxExecutionTime = getProcessTime(millis[millis.length - 1]);
		setMaxEval();
		
		float lastObjFuncValue = Float.MAX_VALUE;
		long target = (stoppingCriteria == STOPPPING_CRITERIA_TARGET)?((instance != null)? getTarget(instance): 0) : 0;
		
		int itWithoutImprovement = 0;
		
		long initialTime = System.currentTimeMillis();
		long processTime = System.currentTimeMillis() - initialTime;
		
		List<Cell> activePopulation = algorithm.loadInitialMemory();
		
		if (executionType == EXECUTION_TYPE_PARAMETER_ANALYSIS) writeResultInstance(instance, printWriter, getBestValidCell(activePopulation), 0, params);
		
		int it = 1;
		
		while(!stoppingCriteria(it, itWithoutImprovement, processTime, maxExecutionTime, target, lastObjFuncValue, this.evaluator.getNumberOfObjectFunctionAval())){
			
			activePopulation = algorithm.runOneGeneration(activePopulation);
			
			Cell bestSol = getBestValidCell(activePopulation);
			Cell bestSol2 = algorithm.getBest();
			if (bestSol == null && bestSol2 != null) bestSol = bestSol2;
			else if(bestSol == null && bestSol2 == null) bestSol = null;
			else if(bestSol2 != null && bestSol!= null) bestSol = (bestSol.getObjectiveFunction() < bestSol2.getObjectiveFunction())?bestSol:bestSol2;
						
			if (bestSol == null || lastObjFuncValue <= bestSol.getObjectiveFunction()){
				
				itWithoutImprovement++;
				if (bestSol != null && bestSol.getObjectiveFunction() == 0) break;
				
			} else {
				
				lastObjFuncValue = bestSol.getObjectiveFunction();
				//System.out.println("v:" + lastObjFuncValue);
				itWithoutImprovement = 0;
				if (executionType == EXECUTION_TYPE_PARAMETER_ANALYSIS) 
					writeResultInstance(instance, printWriter, bestSol, it, params);
											
			}
			
			processTime = System.currentTimeMillis() - initialTime;
			it++;
			//writePar(activePopulation);
		}
		
		processTime = System.currentTimeMillis() - initialTime;
		
		activePopulation = algorithm.updateMemory(activePopulation);
		
		if (activePopulation.isEmpty()) return this.execOneTime(arqPath, instance, printWriter, params);
				
		Cell result = activePopulation.get(0);
		
		result.setTime(processTime);
		
		
		if (printWriter != null){
			if (executionType == EXECUTION_TYPE_PARAMETER_ANALYSIS) writeResultInstance(instance, printWriter, getBestValidCell(activePopulation), it, params);
			else if (stoppingCriteria != STOPPPING_CRITERIA_TARGET) writeResultInstance(instance, printWriter, activePopulation);
			else writeTargetResultInstance(instance, printWriter, result);
		}
		
		System.out.println("NEval:" + this.evaluator.getNumberOfObjectFunctionAval());
		
		return result;
	}
	
	public void writePar(List<Cell> activePopulation){
		
		for (Cell cell : activePopulation) {
			System.out.print(cell.getMaxNumberOfMutations() +" : ");
		}
		System.out.println();
		
	}
	
	public void run()throws Exception{
		
		if (executionType == EXECUTION_TYPE_PARAMETER_CALIBRATION) this.runParameterCalibrationExp();
		
		else if (executionType == EXECUTION_TYPE_PARAMETER_ANALYSIS) runParameterAnalysis();
		else if (executionType == EXECUTION_TYPE_DESIGN_BY_INSTANCE) runDesignExpByInstance();
		
			
		else runDefalt();
		
	}
	
	public void runComponentWise()throws Exception{
		
		int it, round, n_leves = levels.length;
		String line, componentKey;
		String dirName = mainDir + fileDir[0];
		
		ArrayList<String> instancias;
		BufferedReader in;
		FileWriter fileWriter;
		PrintWriter printWriter;
		StringTokenizer st;
		
		HashMap<String, Float> bestByInstance = new HashMap<String, Float>();
		HashMap<String, Float> worstByInstance = new HashMap<String, Float>();
		Float theBest, worst;
		HashMap<String, Float> executionResultByInstance = new HashMap<String, Float>();
		
		HashMap<String, String> component, selectedComponent;
		List<HashMap<String, String>> listOfComponents = new ArrayList<HashMap<String, String>>();
				
		try {
			
			fileWriter = new FileWriter(mainDir + resultArqName + "_" + fileDir[0] + ".txt", true);
			printWriter = new PrintWriter(fileWriter);
			instancias = loadInstancesNames(dirName); 
			
			in = new BufferedReader(new FileReader(mainDir + design_conf));
			
			while ((line = in.readLine()) != null) {
				
				st = new StringTokenizer(line.trim(), " ");
				component = new HashMap<String, String>();
				
				componentKey = st.nextToken().trim();
				
				component.put("COMPONENT_KEY", componentKey);
				
				for (int i = 0; i < n_leves; i++) component.put(levels[i], st.nextToken().trim());
				
				listOfComponents.add(component);
			
			}					
			
			round = 1;
			
			while (!listOfComponents.isEmpty()) {
			
				for (int i = 0; i < listOfComponents.size(); i++) {
					
					component = listOfComponents.get(i);
					componentKey = component.get("COMPONENT_KEY");
					
					for (String instance : instancias) {
						
						if (bestByInstance.containsKey(instance)) theBest = bestByInstance.get(instance);
						else theBest = Float.MAX_VALUE;
						
						if (worstByInstance.containsKey(instance)) worst = worstByInstance.get(instance);
						else worst = Float.MIN_VALUE;
						
						it = 0;
											
						while (it < RUNS_NUMBER) {
							
							Cell s = execOneTime(dirName + "/" + instance, instance, null, component);
							if (s.setObjectiveFunction() < theBest) theBest = s.getObjectiveFunction();
							if (s.getObjectiveFunction() > worst) worst = s.getObjectiveFunction();
							
							executionResultByInstance.put(componentKey + "_" + instance + "_" + it, s.getObjectiveFunction());
							
							printWriter.println(round + ";" + componentKey + ";" + instance + ";" + s.getObjectiveFunction());
							printWriter.flush();
							it++;
							
						}
						
						bestByInstance.put(instance, theBest);
						worstByInstance.put(instance, worst);
					
					}
					
				}
				
				float value, sum, rpd, rpdSelected = Float.MAX_VALUE;
				int N;
				int componenetSelected = -1;
				
				for (int i = 0; i < listOfComponents.size(); i++) {
					
					component = listOfComponents.get(i);
					componentKey = component.get("COMPONENT_KEY");
					N = 0; sum = 0.f;
					
					for (String instance : instancias) {
						
						theBest = bestByInstance.get(instance);
						worst = worstByInstance.get(instance);
						
						it = 0;
						
						while (it < RUNS_NUMBER) {
							
							value = executionResultByInstance.get(componentKey + "_" + instance + "_" + it);
							sum += (value - theBest)/(worst - theBest);
							it++; N++;
							
						}
						
					}
					
					rpd = sum/N;
					
					if (rpd < rpdSelected) {
						
						rpdSelected = rpd;
						componenetSelected = i;
						
					}
					
				}
				
				selectedComponent = listOfComponents.remove(componenetSelected);
				
				for (int i = 0; i < listOfComponents.size(); i++) {
					
					component = listOfComponents.get(i);
					Set<String> levels = selectedComponent.keySet();
					
					for (String level : levels) {
						
						if (!level.equals("COMPONENT_KEY")) 
							
							if (!selectedComponent.get(level).equals("1")) 
								
								component.put(level, selectedComponent.get(level));
											
					}
								
				}
				
				round++;
			
			}
			
			printWriter.close();
			fileWriter.close();
			in.close();
					
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}

	private void runDefalt() throws ParseException {
		
		for (int j = 0; j < fileDir.length; j++) {
			
			String dirName = mainDir + fileDir[j];
			
			try {
				FileWriter fileWriter = new FileWriter(mainDir + resultArqName + "_" + fileDir[j] + ".txt", true);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				printWriter.println("instance;n;m;obj.func;cputime(ms)");
				
				ArrayList<String> instancias = loadInstancesNames(dirName); 
				for (String instancia : instancias) {
					
					int it = 0;
					while(it < RUNS_NUMBER){
						
						Cell s = execOneTime(dirName + "/" + instancia, instancia, printWriter, null);
						
						if(stoppingCriteria != STOPPPING_CRITERIA_TARGET){
							SolutionWriter vs = new SolutionWriter(s, evaluator, processingCosts, setupTimes);
							vs.writeSolInArq(mainDir + "/" + writeSolDir + "/exec_"+ it + "_"+ instancia, instancia, FILE_TYPE);
						}
													
						it ++;
						
					}
										
				}
				
				printWriter.close();
				fileWriter.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void runParameterAnalysis() throws ParseException {
		
		StringTokenizer st;
		HashMap<String, String> params = new HashMap<String, String>();
		String line;
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader(mainDir + design_conf));
			
			while ((line = in.readLine()) != null) {
				
				params.clear();
				st = new StringTokenizer(line.trim(), " ");
				params.put(st.nextToken().trim(), st.nextToken().trim());
				
				for (int j = 0; j < fileDir.length; j++) {
					String dirName = mainDir + fileDir[j];
											
						FileWriter fileWriter = new FileWriter(mainDir + resultArqName + "_" + fileDir[j] + ".txt", true);
						PrintWriter printWriter = new PrintWriter(fileWriter);
						
						ArrayList<String> instancias = loadInstancesNames(dirName); 
						for (String instancia : instancias) {
							
							int it = 0;
							while(it < RUNS_NUMBER){
								
								Cell s = execOneTime(dirName + "/" + instancia, instancia, printWriter, params);
								it ++;
								
							}
												
						}
						
						printWriter.close();
						fileWriter.close();
						
					
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void runDesignExpByInstance()throws Exception{
		
		int n_leves = levels.length;
		String line;
		StringTokenizer st;
		String dirName = mainDir + fileDir[0];
		BufferedReader in;
		ArrayList<String> instancias;
		FileWriter fileWriter;
		PrintWriter printWriter;
		HashMap<String, String> params = new HashMap<String, String>();
		
		try {
						
			instancias = loadInstancesNames(dirName); 
			
			for (String instance : instancias) {
				
				fileWriter = new FileWriter(mainDir + resultArqName + "_" + instance + "_" + fileDir[0] + ".txt", true);
				printWriter = new PrintWriter(fileWriter);
				
				in = new BufferedReader(new FileReader(mainDir + design_conf));
							
				while ((line = in.readLine()) != null) {
					
					st = new StringTokenizer(line.trim(), " ");
					String number = st.nextToken().trim();
					st.nextToken();
					st.nextToken();
					st.nextToken();
					//instance = instancias.get(new Integer(st.nextToken().trim()) - 1);
					
					for (int i = 0; i < n_leves; i++) params.put(levels[i], st.nextToken().trim());
					
					int it = 0;
					while(it < RUNS_NUMBER){
						
						Cell s = execOneTime(dirName + "/" + instance, instance, null, params);
						printWriter.println(number + ";" + instance + ";" + s.setObjectiveFunction());
						printWriter.flush();
						it++;
						
					}
					
				}
				
				printWriter.close();
				fileWriter.close();
				in.close();
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	public void runParameterCalibrationExp()throws Exception{
		
		int n_leves = levels.length;
		String line, instance;
		StringTokenizer st;
		String dirName = mainDir + fileDir[0];
		BufferedReader in;
		ArrayList<String> instancias;
		FileWriter fileWriter;
		PrintWriter printWriter;
		HashMap<String, String> params = new HashMap<String, String>();
		
		try {
			
			fileWriter = new FileWriter(mainDir + resultArqName + "_" + fileDir[0] + ".txt", true);
			printWriter = new PrintWriter(fileWriter);
			instancias = loadInstancesNames(dirName); 
			
			in = new BufferedReader(new FileReader(mainDir + design_conf));
						
			while ((line = in.readLine()) != null) {
				
				st = new StringTokenizer(line.trim(), " ");
				String number = st.nextToken().trim();
				st.nextToken().trim();
				st.nextToken().trim();
				
				instance = instancias.get(new Integer(st.nextToken().trim()) - 1);
				
				for (int i = 0; i < n_leves; i++) params.put(levels[i], st.nextToken().trim());
				for (String instance1 : instancias) {
					int it = 0;
					while(it < RUNS_NUMBER){
						
						Cell s = execOneTime(dirName + "/" + instance1, instance1, null, params);
						printWriter.println(number + ";" + instance1 + ";" + s.setObjectiveFunction());
						printWriter.flush();
						it++;
						
					}
				}
			}
			
			printWriter.close();
			fileWriter.close();
			in.close();
					
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	private void writeResultInstance(String instancia, PrintWriter printWriter, Cell cell, int it, HashMap<String, String> params) throws ParseException {
		
		String instanciaName = instancia.substring(0, instancia.indexOf(FILE_TYPE));
		
		printWriter.print(instanciaName);
		
		Set<String> keys = params.keySet();
		
		for (String key : keys) 
			
			printWriter.print(";" + key + "_" + params.get(key));
		
		printWriter.print(";" + it);	
		printWriter.print(";" + ((cell == null)? Integer.MAX_VALUE : cell.getObjectiveFunction()));
				
		printWriter.println();		
		printWriter.flush();
		
	}
	
	private void writeResultInstance(String instancia, PrintWriter printWriter, List<Cell> memory) throws ParseException {
		
		String instanciaName = instancia.substring(0, instancia.indexOf(FILE_TYPE));
		Cell cell = memory.get(0);
		
		printWriter.print(instanciaName + ";" + cell.getNumberOfJobs() + ";" + cell.getMachines().length);
		
		printWriter.print(";" + cell.getObjectiveFunction());
			
		printWriter.print(";" + cell.getTime());
		printWriter.println();		
		printWriter.flush();
		
	}
	
	private List<Cell> sortObjFunction(List<Cell> Ab){
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
	
	private float get_distance(List<Cell> memoria, int i){
		
		float menor = 1000000000;
		Cell s2 = memoria.get(i);
		
		for (int j = 0; j < memoria.size(); j++) {
			
			if (j != i){
				
				Cell s1 = memoria.get(j);
				float distancia = s1.getDistanceFromC2(s2);
				if(distancia < menor)menor = distancia;
			
			}
		}
		
		return menor;
	}
	
	public Pair<Float, Float> getAvgDistance(List<Cell> memory, int N) {
		
		float avg = 0, var = 0;
		List<Cell> list = new ArrayList<Cell>();
		float[] distances = new float[N];
		
		for (int i = 0; i < N; i++) list.add(memory.get(i));
				
		for (int i = 0; i < N; i++) {
			
			distances[i] = get_distance(list, i);
			avg += distances[i];
		
		}
		
		avg = avg/(float)N;
		
		for (int i = 0; i < distances.length; i++) {
			
			var += Math.pow(avg - distances[i], 2);
			
		}
		
		var = var/(float)(N - 1);
		
		return new Pair<Float, Float>(avg, var);
		
	}
	
	public float getAvgMakespan(List<Cell> memory, int N) {
		
		float result = 0;
		
		for (int i = 0; i < N; i++) {
			
			result += memory.get(i).getObjectiveFunction();
			
		}
		
		return result/(float)N;
	}
	
	private void writeTargetResultInstance(String instancia, PrintWriter printWriter, Cell s) throws ParseException {
		
		String instanciaName = instancia.substring(0, instancia.indexOf(FILE_TYPE));
		
		//BestSol bestSol = bestSolutions.get(instanciaName);*/
		NumberFormat formater = NumberFormat.getInstance();
		
		printWriter.print(instanciaName +";" + s.getTime());
		for (Float solution : s.getTimeSolutions()) {
			printWriter.print(";"+solution);
			//printWriter.print(";"+solution+";");
		}
		printWriter.println();		
		printWriter.flush();
		
	}


	private ArrayList<String> loadInstancesNames(String dirName) {
		
		ArrayList<String> instancias = new ArrayList<String>();
		File dir = new File(dirName);  
		
		String[] children = dir.list();  
		if (children == null);
		else {  
		    for (int i=0; i < children.length; i++) {  
		        // Get filename of file or directory  
		        String filename = children[i]; 
		        if(!filename.contains(FILE_TYPE)) continue;
		        instancias.add(filename);
		    }  
		}
		
		Collections.sort(instancias, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {return s1.compareTo(s2);}
		});
		
		return instancias;
		
	}
	
	private void loadInstancesNames(HashMap<String, String> map, String dirName, String prefix, String fileType, String replaceType) {
		
		File dir = new File(dirName);  
		
		String[] children = dir.list();  
		if (children == null);
		else {  
		    for (int i=0; i < children.length; i++) {  
		        // Get filename of file or directory  
		        String filename = children[i]; 
		        if(!filename.contains(fileType)) continue;
		        map.put(prefix+ "_" + ((replaceType != null)? filename.replaceAll(fileType, replaceType) : filename), dirName + "/" + filename);
		    }  
		}
		
	}
	
	private void selectRandomInstancesNames() throws Exception{
		
		String tunningPath = "/home/rodney/workspace/tardiness/tunning/instances/";
		String chenPath = "/home/rodney/workspace/tardiness/";
		String linPath = "/home/rodney/workspace/tardiness/EM/instances/10X100 large problem instances/";
		String arnoutPath = "/home/rodney/workspace/tardiness/Arnout/";
		String wctPath = "/home/rodney/workspace/tardiness/completion_time/";
		
		String[] dirsChen = {"30m", "50m","70m", "90m"};
		String[] dirsWCT = {"instances"};
		
		String[] dirsLin100 = {"10X100 setting_1", "10X100 setting_2", "10X100 setting_3", "10X100 setting_4", "10X100 setting_5", "10X100 setting_6", "10X100 setting_7", "10X100 setting_8", "10X100 setting_9", "10X100 setting_10", "10X100 setting_11", "10X100 setting_12", "10X100 setting_13", "10X100 setting_14", "10X100 setting_15", "10X100 setting_16"};
		String[] dirsArnoult = {"4Machines-Balanced", "6Machines-Balanced","8Machines-Balanced","10Machines-Balanced","12Machines-Balanced",
								"4Machines-ProcDomin(done)","6Machines-ProcDomin(done)","8Machines-ProcDomin(done)","10Machines-ProcDomin(done)","12Machines-ProcDomin(done)",
								"4Machines-SetupDomin", "6Machines-SetupDomin","8Machines-SetupDomin","10Machines-SetupDomin","12Machines-SetupDomin"};
		
		
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		for (int i = 0; i < dirsLin100.length; i++) {
			
			loadInstancesNames(map, linPath + dirsLin100[i], "TWT_S" +(i + 1) + "_", ".txt", null);
			
		}
		
		for (int i = 0; i < dirsArnoult.length; i++) {
			
			loadInstancesNames(map, arnoutPath + dirsArnoult[i], "Makespan_S" +(i + 1) + "_", ".dat", ".txt");
			
		}
		
		for (int i = 0; i < dirsChen.length; i++) {
			
			loadInstancesNames(map, chenPath + dirsChen[i], "TT_", ".txt", null);
			
		}
		
		for (int i = 0; i < dirsWCT.length; i++) {
			
			loadInstancesNames(map, wctPath + dirsWCT[i], "WCP_", ".txt", null);
			
		}
		
		
		HashMap<String, String> files = new HashMap<String, String>();
		Random rand = new Random();
		String[] keys = (String[])map.keySet().toArray(new String[map.keySet().size()]);
		int i = 0;
		while (i < 250) {
			
			int sorted = rand.nextInt(keys.length);
			while (files.containsKey(keys[sorted])) sorted = rand.nextInt(keys.length);
			
			files.put(keys[sorted], keys[sorted]);
			copyFileUsingChannel(new File(map.get(keys[sorted])), new File(tunningPath + keys[sorted]));
			i++;
			
		}
				
	}
	
	private static void copyFileUsingChannel(File source, File dest) throws IOException {
	    FileChannel sourceChannel = null;
	    FileChannel destChannel = null;
	    try {
	        sourceChannel = new FileInputStream(source).getChannel();
	        destChannel = new FileOutputStream(dest).getChannel();
	        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
	       }finally{
	    	   System.out.println(source.getAbsolutePath());
	           System.out.println(dest.getAbsolutePath());
	           sourceChannel.close();
	           destChannel.close();
	   }
	}
	
	public static void main(String[] args) {
		
		if (args.length >= 4){
			
			//iRace Tunning
			Random rand = new Random(new Long(args[2]));
			String instance = args[3];
			HashMap<String, String> params = new HashMap<String, String>();
			
			for (int i = 4; i < args.length; i+=2)
				params.put(args[i], args[i + 1]);
						
			ExperimentRunner experiment = new ExperimentRunner(rand);
			try {
				
				Cell s = experiment.execOneTime(instance, null, null, params);
				System.out.println("Best " + ((s == null)? Float.MAX_VALUE : s.getObjectiveFunction()) + "0");
				
			} catch (Exception e) {e.printStackTrace();}
			
		} else {
			
			ExperimentRunner experiment = new ExperimentRunner();
			try {
				experiment.run();
			} catch (Exception e) { e.printStackTrace(); }
			
		}
	}
	
	
}
