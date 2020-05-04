package instance.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import operators.function.evaluator.ObjectFunctionEvaluator;


public class TunningLoaderInstance implements LoaderInstance {
	
	private LoaderInstance makespan = new LoaderArnaoutInstance(); 
	private LoaderInstance tt = new LoaderChengInstance(); 
	private LoaderInstance wcp = new LoaderChengInstance(true); 
	private LoaderInstance twt = new LoaderLinInstance(); 
	
	
	public TunningLoaderInstance() {}

	public ObjectFunctionEvaluator getEvaluator(String arqPath) {
		
		if (arqPath.contains("TWT_")) return twt.getEvaluator(arqPath);
		else if (arqPath.contains("Makespan_S")) return makespan.getEvaluator(arqPath);
		else if (arqPath.contains("TT_")) return tt.getEvaluator(arqPath);
		else if (arqPath.contains("WCP_")) return wcp.getEvaluator(arqPath);
			
		throw new RuntimeException();
		
	}
	
	@Override
	public int[][] loadDueDate(String arqPath) throws FileNotFoundException, IOException {
		
		if (arqPath.contains("TWT_")) return twt.loadDueDate(arqPath);
		else if (arqPath.contains("Makespan_S")) return makespan.loadDueDate(arqPath);
		else if (arqPath.contains("TT_")) return tt.loadDueDate(arqPath);
		else if (arqPath.contains("WCP_")) return wcp.loadDueDate(arqPath);
			
		throw new RuntimeException();
		
	}

	@Override
	public int[][] loadProcessTime(String arqPath) throws FileNotFoundException, IOException {
		
		if (arqPath.contains("TWT_")) return twt.loadProcessTime(arqPath);
		else if (arqPath.contains("Makespan_S")) return makespan.loadProcessTime(arqPath);
		else if (arqPath.contains("TT_")) return tt.loadProcessTime(arqPath);
		else if (arqPath.contains("WCP_")) return wcp.loadProcessTime(arqPath);
			
		throw new RuntimeException();
		
	}

	@Override
	public int[][][] loadSetupTime(String arqPath) throws FileNotFoundException, IOException {
		
		if (arqPath.contains("TWT_")) return twt.loadSetupTime(arqPath);
		else if (arqPath.contains("Makespan_S")) return makespan.loadSetupTime(arqPath);
		else if (arqPath.contains("TT_")) return tt.loadSetupTime(arqPath);
		else if (arqPath.contains("WCP_")) return wcp.loadSetupTime(arqPath);
			
		throw new RuntimeException();
		
	}

	@Override
	public HashMap<String, BestSol> loadBestSol(String arqPath) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
