package JobQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import java.util.Date;

public class BJobListener implements JobListener {
 Log logger = LogFactory.getLog(BJobListener.class);

 public String getName() {
 return getClass().getSimpleName();
 }

 public void jobToBeExecuted(JobExecutionContext context) {
	 String jobName = context.getJobDetail().getKey().toString();
	 logger.info(jobName + " is about to be executed");
 }
 public void jobExecutionVetoed(JobExecutionContext context) {
	 String jobName = context.getJobDetail().getKey().toString();
	 logger.info(jobName + " was vetoed and not executed()");
 }
 public void jobWasExecuted(JobExecutionContext context,
 JobExecutionException jobException) {

	 String jobName = context.getJobDetail().getKey().toString();
	 logger.info(jobName + " was executed");
 }
 }
