import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import model.MachineContainer;
import operators.function.evaluator.ObjectFunctionEvaluator;
import model.Cell;


public class SolutionWriter {
	
	private int[][][] custoDeSetup;
	private int[][] custos;
	private Cell solucao;
	private ObjectFunctionEvaluator evaluator;
	
	public SolutionWriter(Cell solucao, ObjectFunctionEvaluator evaluator, int[][] custos, int[][][] custoDeSetup) {
		super();
		this.solucao = solucao;
		this.custos = custos;
		this.custoDeSetup = custoDeSetup;
		this.evaluator = evaluator;
	}
	
	
	public void writeSolInArq(String arq, String instance, String FILE_TYPE){
		try {
			
			solucao.setObjectiveFunction();
			
			String instanciaName = instance.substring(0, instance.indexOf(FILE_TYPE));
			
			FileWriter fileWriter = new FileWriter(arq, false);
			PrintWriter printWriter = new  	PrintWriter(fileWriter);
			printWriter.println(instanciaName + "\t" + custos[0].length + "\t" + custos.length + "\t" + this.solucao.getObjectiveFunction());
			
			MachineContainer[] maquinas = this.solucao.getMachines();
			for (int i = 0; i < maquinas.length; i++) {
				MachineContainer maquina = maquinas[i];
				printWriter.print(evaluator.getObjectFunctionValue(this.solucao, maquina) + "\t");
				int numTarefas = maquina.size();
				printWriter.print(numTarefas + "\t");
				for (int j = 0; j < numTarefas; j++) {
					printWriter.print((maquina.getJob(j).getId() + 1) + "\t");
				}
				printWriter.println();
			}
			printWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
