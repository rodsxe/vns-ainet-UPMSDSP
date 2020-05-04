package operators.function.evaluator.scheduling;

import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import instance.loader.SchedulingInstancesConfig;
import model.Cell;
import model.Job;
import model.MachineContainer;
import model.base.Container;
import operators.function.evaluator.ObjectFunctionEvaluator;

public class MakespanEvaluator implements ObjectFunctionEvaluator {
	
	private long numberOfObjectFunctionAval;
	private boolean isToContAval = true;
	
	private SchedulingInstancesConfig schedulingConfig;
	
	public MakespanEvaluator() {
		super();
	}
	
	public void resetNumberOfObjectFunctionAval() {
	
		this.numberOfObjectFunctionAval = 0;
	
	}
	
	public long getNumberOfObjectFunctionAval() {return this.numberOfObjectFunctionAval;}
	
	public SchedulingInstancesConfig getSchedulingConfig() {
		return schedulingConfig;
	}

	public void setSchedulingConfig(SchedulingInstancesConfig schedulingConfig) {
		this.schedulingConfig = schedulingConfig;
	}
	
	public void setCONSTRAINT_PENALTY(float cONSTRAINT_PENALTY) {}
	
	
	
	@Override
	public int getLSMachineIndex(Random rand, Cell cell, List<MachineContainer> machines) {
		
		float max = 0, actual;
		int choosed = -1;
		
		for (int j = 0; j < machines.size(); j++) {
			
			MachineContainer machineContainer = machines.get(j);
			actual = getObjectFunctionValue(cell, machineContainer);
			if (actual > max) {
				
				max = actual;
				choosed = j;
				
			} else if (actual == max && rand.nextFloat() < 0.5) choosed = j;
			
		}
		//return rand.nextInt(machines.size());
		return choosed;
	}

	@Override
	public float getLSCost(float costM1, float costM2) {
		return Math.max(costM1, costM2);
	}

	public float getObjectFunctionValue(Cell cell) {
		
		float makespan = 0; 
		this.isToContAval = false;
		
		MachineContainer[] maquinas = cell.getMachines();
		
		for(int i = 0; i < maquinas.length; i++){
			
			MachineContainer maquina = maquinas[i];
			makespan = Math.max(makespan , getObjectFunctionValue(cell, maquina));
		
		}
		
		this.isToContAval = true;
		this.numberOfObjectFunctionAval++;
		
		return makespan;
	
	}
	
	@Override
	public float getObjectFunctionValue(Cell cell, Container container) {
		
		//long initialTime = System.nanoTime();
		
		long completationTime = 0;
		
		MachineContainer machine = (MachineContainer)container;
		int machineID = machine.getId();
		
		List<Job> jobs = machine.getJobs();
		Job job, previousJob;
		
		if (jobs.isEmpty()) return 0;
		
		previousJob = jobs.get(0);
		
		for (int i = 0; i < jobs.size(); i++){
			
			job = jobs.get(i);
			
			completationTime = Math.max(job.getReleaseDate(), completationTime + schedulingConfig.getSetupTime(machineID, previousJob.getId(), job.getId()));
			
			completationTime += schedulingConfig.getProcessTime(machineID, job.getId());
			
			previousJob = job;
									
		}
		
		if (isToContAval) this.numberOfObjectFunctionAval++;
		
		//long end = System.nanoTime(); 
		//System.out.println("test:" + (end - initialTime));
		
		return completationTime;
		
	}

	@Override
	public boolean isValidCell(Cell cell) {
		return true;
	}

	
	@Override
	public void writeResult(Cell cell, PrintWriter printWriter) {
		
		printWriter.print(";" + ((long)cell.getObjectiveFunction()));

	}

}
