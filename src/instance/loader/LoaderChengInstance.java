package instance.loader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import operators.function.evaluator.ObjectFunctionEvaluator;
import operators.function.evaluator.scheduling.TardinessEvaluator;
import operators.function.evaluator.scheduling.WeightCompletionTimeEvaluator;


public class LoaderChengInstance implements LoaderInstance {
	
	private boolean loadWeight;
	private ObjectFunctionEvaluator evaluator;
	
	public LoaderChengInstance(boolean loadWeight) {
		
		super();
		this.loadWeight = loadWeight;
		evaluator = (this.loadWeight)? new WeightCompletionTimeEvaluator(): new TardinessEvaluator();
				
	}

	public LoaderChengInstance() {
		super();
		loadWeight = false;
		evaluator = (this.loadWeight)? new WeightCompletionTimeEvaluator(): new TardinessEvaluator();
	}

	public ObjectFunctionEvaluator getEvaluator(String arqPath) {
		
		return evaluator;
		
	}
	
	public int[][] loadDueDate(String arqPath)throws FileNotFoundException, IOException {
		
		BufferedReader in = new BufferedReader(new FileReader(arqPath));
        
		String line = in.readLine();
        StringTokenizer st = new StringTokenizer(line, " ");
        Integer numeroDeTarefas = new Integer((String)st.nextToken());
        
        int[][] dueDates = new int[numeroDeTarefas][5];
        int contTarefas = 0;
        
        do{
        	line = in.readLine();
            st = new StringTokenizer(line, "[ +][\t]");
           	Integer dueDate = new Integer((String)st.nextToken());
           	Integer isRestrictDueDate = new Integer((String)st.nextToken());
        	Integer weight = (loadWeight)? new Integer((String)st.nextToken()) : 1;
        	
           	dueDates[contTarefas][LoaderInstance.DUE_DATE] = dueDate;
           	dueDates[contTarefas][LoaderInstance.IS_RESTRICT] = isRestrictDueDate;
           	dueDates[contTarefas][LoaderInstance.EARLINESS_WEIGHT] = 0;
           	dueDates[contTarefas][LoaderInstance.TARDINESS_WEIGHT] = weight;
           	dueDates[contTarefas][LoaderInstance.RELEASE_DATE] = 0;
           	contTarefas++;
        }while(contTarefas < numeroDeTarefas);
        
        return dueDates;
        
	}
	
	@Override
	public int[][] loadProcessTime(String arqPath)throws FileNotFoundException, IOException {
		
		BufferedReader in = new BufferedReader(new FileReader(arqPath));
        String line = in.readLine();
        StringTokenizer st = new StringTokenizer(line, " ");
        
        Integer numeroDeTarefas = new Integer((String)st.nextToken());
        Integer numeroDeMaquinas = new Integer((String)st.nextToken());
        
        int[][] custos = new int[numeroDeMaquinas][numeroDeTarefas];
        
        
		pularDueDates(in, numeroDeTarefas);
        
        int contTarefas = 0;
        do{
        	line = in.readLine();
           	st = new StringTokenizer(line, "[ +][\t]");
            int contMaquinas = 0;
            while (st.hasMoreElements()){
                String valueAttribute = ((String)st.nextToken());
                custos[contMaquinas][contTarefas] = new Integer(valueAttribute);
                contMaquinas++;
            }
            contTarefas++;
        }while(contTarefas < numeroDeTarefas);
        in.close();
        return custos;
		
	}

	public void pularDueDates(BufferedReader in, Integer numeroDeTarefas) throws IOException {
		
		String line;
		int contTarefas = 0;
        do{line = in.readLine();contTarefas++;}while(contTarefas < numeroDeTarefas);
        line = in.readLine();
        
	}
	
	

	@Override
	public int[][][] loadSetupTime(String arqPath)throws FileNotFoundException, IOException {
		
		BufferedReader in = new BufferedReader(new FileReader(arqPath));
        String line = in.readLine();
        StringTokenizer st = new StringTokenizer(line, " ");
        
        Integer numeroDeTarefas = new Integer((String)st.nextToken());
        Integer numeroDeMaquinas = new Integer((String)st.nextToken());
        
        int[][][] setupTimes = new int[numeroDeMaquinas][numeroDeTarefas][numeroDeTarefas];
       
        pularDueDates(in, numeroDeTarefas);
        pularDueDates(in, numeroDeTarefas);
        
        for (int i = 0; i < numeroDeMaquinas; i++) {
        	int contTarefas = 0;
            do{
            	line = in.readLine();
               	st = new StringTokenizer(line, "[ +][\t]");
                int cont = 0;
                while (st.hasMoreElements()){
                    String valueAttribute = ((String)st.nextToken());
                    setupTimes[i][contTarefas][cont] = new Integer(valueAttribute);
                    cont++;
                }
                contTarefas++;
            }while(contTarefas < numeroDeTarefas);
            line = in.readLine();
        }
		
		return setupTimes;
	}

	@Override
	public HashMap<String, BestSol> loadBestSol(String arqPath)
			throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void printDueDate(int[][] dueDates){
		for (int i = 0; i < dueDates.length; i++) {
			int[] js = dueDates[i];
			for (int j = 0; j < js.length; j++) {
				System.out.print(js[j] +" ");
			}
			System.out.println();
		}
	}
	
	
	public static void main(String[] args) {
		LoaderChengInstance loaderChengInstance = new LoaderChengInstance();
		
		try {
			int[][][] setupTimes = loaderChengInstance.loadSetupTime("C:\\Projetos\\tardiness\\instances\\A1.txt");
			
			for (int i = 0; i < setupTimes.length; i++) {
				loaderChengInstance.printDueDate(setupTimes[i]);
				System.out.println("=================");
			}
			
			
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
