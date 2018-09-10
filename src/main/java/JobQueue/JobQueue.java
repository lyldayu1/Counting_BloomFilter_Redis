package JobQueue;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import bloomfilter.RedisBloomFilter;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import org.quartz.JobDetail;


public class JobQueue {
	private Scheduler scheduler;
	private String host;
	private int port;
	public JobQueue(String host,int port) {
		try {
			this.host=host;
			this.port=port;
			SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
			scheduler = schedFact.getScheduler();
			scheduler.getListenerManager().addJobListener(new BJobListener());
			scheduler.getListenerManager().addTriggerListener(new BTriggerListener());
			scheduler.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.clearjob=clearjob;
	}
	public void start() throws InterruptedException {
		try {
			 scheduler.getContext().put("host", this.host);
			 scheduler.getContext().put("port", this.port);
			JobDetail job = newJob(Task.class)
				      .withIdentity("cleanJob", "group1")
				      .build();
			Trigger trigger = newTrigger()
				      .withIdentity("cleanTrigger", "group1")
				      .withSchedule(
				    		  simpleSchedule().simpleSchedule()
									.withIntervalInSeconds(5).repeatForever())
							.build();
			 scheduler.scheduleJob(job, trigger);
                       
             
		}catch (SchedulerException se) {
            se.printStackTrace();
        }
	}
	public void shutdown() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
