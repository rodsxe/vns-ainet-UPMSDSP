import java.util.Random;

import Algorithms.Algorithm;
import Algorithms.Multistart;
import Algorithms.MultistartVNS;
import Algorithms.VNSaiNET;
import instance.loader.LoaderArnaoutInstance;
import instance.loader.LoaderChengInstance;
import instance.loader.LoaderInstance;
import instance.loader.LoaderLinInstance;
import instance.loader.TunningLoaderInstance;
import operators.function.evaluator.ObjectFunctionEvaluator;
import operators.function.evaluator.scheduling.MakespanEvaluator;
import operators.function.evaluator.scheduling.TardinessEvaluator;
import operators.function.evaluator.scheduling.WeightCompletionTimeEvaluator;

public class ExperimentConfig {
	
	/**The class used to load the instances. 
	 * LoaderArnaoutInstance() for makespan criterion  
	 * LoaderLinInstance() for TWT criterion  
	 * LoaderChengInstance() for TT criterion 
	 * LoaderChengInstance(true) for TWCT criterion 
	 * */
	public static final LoaderInstance loader = new TunningLoaderInstance();
	
	/**The algorithm used*/
	public static final Algorithm algoritmo = new VNSaiNET();
	
	/**The execution type, it may be default, or calibration of the parameters.*/
	public static final int execution_type = ExperimentRunner.EXECUTION_TYPE_DEFAULT;
	
	/**Number of experiments will be runned by instance.*/
	public static final int number_of_experiments_per_instance = 100;
		
	/**Type of stopping criteria used*/
	public static final int stopping_criteria = ExperimentRunner.STOPPPING_CRITERIA_NUMBER_EVAL;
	
	/**if used stopping criteria iterations without improvement, 
	 * this parameter represents the number of iterations without improvement.*/
	public static final int iterations_without_improvement = 100;
	
	/**if used stopping criteria of time or target, this parameter represents the maximum time expend in milliseconds.
	 * The time used is given by the equation tempo_millis * (n + m -1)/(m-1), in which n is the number of jobs and m the number of machines.*/
	public static final int[] tempo_millis = {50000};
	
	public static final long number_of_eval = 2 * (long)Math.pow(10, 7);
			
	/**The main directory, in which the other files and directories must be subdirectory of it.*/
	public static final String main_dir = "/home/rodney/programas/projetos/robust.upmp/instances/";
	
	/**The list of directories of the instances processed in the experiment. This directories must be subdirectories of main_dir.*/
	public static final String[] dir_instances = {"analise"}; 
	
	/**Type of the files to read in dir_instances.*/
	public static final String file_instance_type = ".txt";
		
	/**Directory where the complete solution will be saved. 
	 * This solution is saved with the complete structure of the scheduling, the order of each job and the machine in which their are assigned.*/
	public static final String dir_to_write_the_best_solutions = "s";
	
	/**The file where is saved only the cpu time and the value of the object function to each instance.*/
	public static final String result_file_name = "new-vns-ainet-w-sup-cr";
		
	/**if the stoppping criteria is target, it file contains the target fo each instance.*/
	public static final String file_of_target_value_of_instance = "target.txt";
	
	/**if the stoppping criteria is target, the percent of the target used.*/
	public static final double percentual_alvo = 1.00;
		
	/**if used the execution_type to calibrate the parameters, it is the file which contains the of the design of experiments.*/
	public static final String parameter_calibration_design_conf = "design.conf"; 
	
	/**Name of each parameter used in the calibration experiment*/
	public static final String[] levels_parameters = {"--n", "--supTs", "--l_rate"}; 
	
	/**The random variable used by the experiments.*/
	public static final Random rand = new Random();
	
}


