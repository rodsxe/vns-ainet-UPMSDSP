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

public class WeightCompletionTimeEvaluator implements ObjectFunctionEvaluator {
	
	private float CONSTRAINT_PENALTY = 1000;
	
	private SchedulingInstancesConfig schedulingConfig;
	
	private long numberOfObjectFunctionAval;
	private boolean isToContAval = true;
	
	public WeightCompletionTimeEvaluator() {
		super();
	}

	public SchedulingInstancesConfig getSchedulingConfig() {
		return schedulingConfig;
	}

	public void setSchedulingConfig(SchedulingInstancesConfig schedulingConfig) {
		this.schedulingConfig = schedulingConfig;
	}
	
	public void setCONSTRAINT_PENALTY(float cONSTRAINT_PENALTY) {
		CONSTRAINT_PENALTY = cONSTRAINT_PENALTY;
	}
	
	public void resetNumberOfObjectFunctionAval() {
		
		this.numberOfObjectFunctionAval = 0;
	
	}
	
	public long getNumberOfObjectFunctionAval() {return this.numberOfObjectFunctionAval;}
	

	@Override
	public float getObjectFunctionValue(Cell cell, Container container) {
		
		long weightCompletionTime = 0;
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
			
			long taskDiff = completationTime - job.getDueDate();
			
			long taskTardiness = Math.max(0, taskDiff);
			job.setHaAtraso(taskTardiness > 0);
			
			weightCompletionTime += completationTime * job.getTardinessWeight() + ((job.isHaAtraso() && job.isRestrict())? completationTime * CONSTRAINT_PENALTY : 0); 
			
			previousJob = job;
									
		}
		
		if (isToContAval) this.numberOfObjectFunctionAval++;
				
		return weightCompletionTime;
			
	}

	
	
	@Override
	public int getLSMachineIndex(Random rand, Cell cell, List<MachineContainer> machines) {
		return rand.nextInt(machines.size());
	}

	@Override
	public float getLSCost(float costM1, float costM2) {
		return costM1 + costM2;
	}

	public float getObjectFunctionValue(Cell cell) {
		
		float custoTotal = 0;
		MachineContainer[] maquinas = cell.getMachines();
		this.isToContAval = false;
		
		for(int i = 0; i < maquinas.length; i++){
			
			MachineContainer maquina = maquinas[i];
			custoTotal += getObjectFunctionValue(cell, maquina);
		
		}
		
		this.isToContAval = true;
		this.numberOfObjectFunctionAval++;
		
		return custoTotal;
	
	}

	@Override
	public boolean isValidCell(Cell cell) {
		
		MachineContainer[] machines = cell.getMachines();
		
		for (MachineContainer machineContainer : machines) {
			
			List<Job> jobs = machineContainer.getJobs();
			
			if (jobs.isEmpty()) continue;
						
			for (Job job : jobs) 
				
				if (job.isRestrict() && job.isHaAtraso()) return false;
				
		}
		
		return true;
		
	}

	

	@Override
	public void writeResult(Cell cell, PrintWriter printWriter) {
		
		printWriter.print(";" + ((long)cell.getObjectiveFunction()));

	}

}
