package operators.function.evaluator;

import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import instance.loader.SchedulingInstancesConfig;
import model.Cell;
import model.MachineContainer;
import model.base.Container;

public interface ObjectFunctionEvaluator {
	
	public float getObjectFunctionValue(Cell cell);
	
	public float getObjectFunctionValue(Cell cell, Container container);
	
	public void setSchedulingConfig(SchedulingInstancesConfig schedulingConfig);
	
	public SchedulingInstancesConfig getSchedulingConfig();
		
	public boolean isValidCell(Cell cell);
	
	public void setCONSTRAINT_PENALTY(float cONSTRAINT_PENALTY);
	
	public void writeResult(Cell cell, PrintWriter printWriter);
	
	public int getLSMachineIndex(Random rand, Cell cell, List<MachineContainer> machines);
	
	public float getLSCost(float costM1, float costM2);
	
	public void resetNumberOfObjectFunctionAval() ;
	
	public long getNumberOfObjectFunctionAval() ;
	
}
