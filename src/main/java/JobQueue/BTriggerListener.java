package JobQueue;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

public class BTriggerListener implements TriggerListener {
	public String getName() {
        return "SimpleTriggerListener";
    }
 
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        System.out.println("triggerFired ");
    }
 
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        //return true when need veto job
    		System.out.println("begin");
        return false;
    }
 
    public void triggerMisfired(Trigger trigger) {
        System.out.println("triggerMisfired ");
    }
 
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        System.out.println("triggerComplete ");
    }
}
