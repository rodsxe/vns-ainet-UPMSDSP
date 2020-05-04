package operators.function.evaluator.scheduling;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sun.tools.javac.util.Pair;

import instance.loader.SchedulingInstancesConfig;
import model.Cell;
import model.Job;
import model.MachineContainer;
import model.base.Container;
import operators.function.evaluator.ObjectFunctionEvaluator;

public class TardinessEvaluator implements ObjectFunctionEvaluator {
	
	private float CONSTRAINT_PENALTY = 1000;
	
	private SchedulingInstancesConfig schedulingConfig;
	
	private long numberOfObjectFunctionAval;
	private boolean isToContAval = true;
	
	public TardinessEvaluator() {
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
		
		long tardiness = 0;
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
			tardiness += (taskTardiness * job.getTardinessWeight()) + ((job.isRestrict() && taskTardiness > 0)? taskTardiness * CONSTRAINT_PENALTY: 0);
			
			previousJob = job;
									
		}
		
		if (isToContAval) this.numberOfObjectFunctionAval++;
		
		return tardiness;
		
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
	
	public float getLSCost(float costM1, float costM2){
		
		return costM1 + costM2;
		
	}
	
	public int getLSMachineIndex(Random rand, Cell cell, List<MachineContainer> machines) {
		
		
		List<MachineContainer> observed = new ArrayList<MachineContainer>();
		float cost = -1;
		int index = -1;
		int cont = 0;
		
		while (cost <= 0 && cont < machines.size()) {
			
			index = rand.nextInt(machines.size());
			MachineContainer choosed = machines.get(index);
			cost = getObjectFunctionValue(cell, choosed);
			cont++;
			
		}
		
		if(cost == 0) return -1;
		
		return index;
		//return rand.nextInt(machines.size());
	}
	
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
	
	
	public void writeResult(Cell cell, PrintWriter printWriter) {
		
		long tardiness = 0;
		long maxTardiness = 0;
		long dueDate = 0;
		int nTaredinessJobs = 0;
		for (MachineContainer machine : cell.getMachines()) {
			
			long completationTime = 0;
			int machineID = machine.getId();
			List<Job> jobs = machine.getJobs();
			Job job, previousJob;
			
			if (jobs.isEmpty()) continue;
			
			previousJob = jobs.get(0);
			
			for (int i = 0; i < jobs.size(); i++){
				
				job = jobs.get(i);
				
				completationTime = Math.max(job.getReleaseDate(), completationTime + schedulingConfig.getSetupTime(machineID, previousJob.getId(), job.getId()));
				
				completationTime += schedulingConfig.getProcessTime(machineID, job.getId());
				
				long taskDiff = completationTime - job.getDueDate();
				
				if (Math.max(0, taskDiff) > 0){
					
					nTaredinessJobs++;
					
					if (Math.max(0, taskDiff) > maxTardiness){
						
						maxTardiness = Math.max(0, taskDiff);
						dueDate = job.getDueDate();
					
					}
					
				}
				previousJob = job;
				
				long taskTardiness = Math.max(0, taskDiff);
				//System.out.print((job.getId() + 1) + ":" + taskTardiness + ":" + job.getTardinessWeight() + ";");
				//job.setHaAtraso(taskTardiness > 0);
				tardiness += (taskTardiness * job.getTardinessWeight()) + 
							((job.isRestrict() && taskTardiness > 0)? taskTardiness * CONSTRAINT_PENALTY: 0);
				
				
										
			}
			//System.out.println(tardiness);
			
			
		}
		
		printWriter.print(";" + cell.getObjectiveFunction() + ";"+ nTaredinessJobs +";" + maxTardiness + ";" + dueDate + ";");
		
				
	}

}
