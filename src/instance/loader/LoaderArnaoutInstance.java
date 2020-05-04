package instance.loader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import operators.function.evaluator.ObjectFunctionEvaluator;
import operators.function.evaluator.scheduling.MakespanEvaluator;
import operators.function.evaluator.scheduling.TardinessEvaluator;
import operators.function.evaluator.scheduling.WeightCompletionTimeEvaluator;


public class LoaderArnaoutInstance implements LoaderInstance {

	private static final MakespanEvaluator evaluator = new MakespanEvaluator();
	
	public ObjectFunctionEvaluator getEvaluator(String arqPath) {
		
		return evaluator;
		
	}
	
	@Override
	public int[][] loadDueDate(String arqPath) throws FileNotFoundException, IOException {
		
		BufferedReader in = new BufferedReader(new FileReader(arqPath));
        String line;
        line = in.readLine();
        while((line = in.readLine()) != null && line.equals(""));
        
        Integer numeroDeTarefas = new Integer(in.readLine());
        
		int[][] dueDates = new int[numeroDeTarefas][5];
		
		in.close();
		 
		return dueDates;
	}

	@Override
	public int[][] loadProcessTime(String arqPath)throws FileNotFoundException, IOException {
		
		BufferedReader in = new BufferedReader(new FileReader(arqPath));
        String line;
        line = in.readLine();
        while((line = in.readLine()) != null && line.equals(""));
        Integer numeroDeMaquinas = new Integer(line);
        Integer numeroDeTarefas = new Integer(in.readLine());
        
        int[][] custos = new int[numeroDeMaquinas][numeroDeTarefas];
        while((line = in.readLine()) != null && line.equals(""));
        int contTarefas = 0;
        do{
           	StringTokenizer st = new StringTokenizer(line, " ");
            int contMaquinas = 0;
            while (st.hasMoreElements()){
                String valueAttribute = ((String)st.nextToken());
                custos[contMaquinas][contTarefas] = new Integer(valueAttribute);
                contMaquinas++;
            }
            contTarefas++;
        }while((line = in.readLine()).length() != 0);
        in.close();
        return custos;
        
	}

	@Override
	public int[][][] loadSetupTime(String arqPath)throws FileNotFoundException, IOException {
		BufferedReader in = new BufferedReader(new FileReader(arqPath));
        String line;
        line = in.readLine();
        while((line = in.readLine()) != null && line.equals(""));
        Integer numeroDeMaquinas = new Integer(line);
        Integer numeroDeTarefas = new Integer(in.readLine());
        while((line = in.readLine()) != null && line.equals(""));
        while((line = in.readLine()).length() != 0);
        int contTarefas = 0;
        int maquina = 0;
        int contTarefas2 = 0;
        while((line = in.readLine()) != null && line.equals(""));
        boolean isNewMachine = true;
        int[][][] custoDeSetup = new int[numeroDeMaquinas][numeroDeTarefas][numeroDeTarefas];
        do{
        	if(line.length() == 0 && isNewMachine){
        		maquina ++;
        		contTarefas2 = 0;
        		isNewMachine = false;
        	}else{
        		isNewMachine = true;
        		 contTarefas = 0;
        		 StringTokenizer st = new StringTokenizer(line, " ");
        		 while (st.hasMoreElements()){
        			 String valueAttribute = ((String)st.nextToken());
        			 custoDeSetup[maquina][contTarefas2][contTarefas] = new Integer(valueAttribute);
        			 contTarefas++;
        		 }
        		 contTarefas2++;
        	}
        	
        }while((line = in.readLine()) != null);
        in.close();
        return custoDeSetup; 
	}
	
	public HashMap<String, BestSol> loadBestSol(String arqPath)throws FileNotFoundException, IOException{
		
		HashMap<String, BestSol> result = new HashMap<String, BestSol>();
		BufferedReader in = new BufferedReader(new FileReader(arqPath));
        String line;
        while((line = in.readLine()) != null) {
        	
        	StringTokenizer st = new StringTokenizer(line, ";");
        	result.put(st.nextToken(), new BestSol().setBestSol(new Integer(st.nextToken())));
        	
        }
				
		return result;
		
	}
}
