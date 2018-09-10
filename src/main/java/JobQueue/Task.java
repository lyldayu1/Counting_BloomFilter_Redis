package JobQueue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;

import bloomfilter.BloomFilterBuilder;
import bloomfilter.ExpireBloomFilter;
import bloomfilter.RedisBloomFilter;
import redis.clients.jedis.Jedis;

public class Task implements org.quartz.Job{
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		SchedulerContext schedulerContext = null;
	    try {
	        schedulerContext = context.getScheduler().getContext();
	    } catch (SchedulerException e1) {
	        e1.printStackTrace();
	    }
	    Iterator<String> ite=schedulerContext.keySet().iterator();
	    String host=schedulerContext.getString("host");
	    int port=schedulerContext.getInt("port");
	    cleanJob(host,port);
	    
	}
	public void cleanJob(String host,int port) {
		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(10, 0.01)
				.name("cleanTask")
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		// get every tokens and delete 
		try{
			Jedis jedis =bfc.getBuilder().pool().getResource();
	        Iterator<String> ite=jedis.smembers("total:tokens").iterator();
	        while(ite.hasNext()) {
	        		String[] information=ite.next().split(" ");
	        		String BFname=information[0];
	        		String token=information[1];
	        		RedisBloomFilter<String> bfc_sig=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
	        				.name(BFname)
	        				.redisBacked(true)
	        				.redisHost(host)
	        				.redisPort(port)
	        				.getBuilder());
	        		bfc_sig.removeExpires(token);
	        }
	        jedis.close();
		}catch(Exception e) {
    			System.out.println(e);
		}
	}
}
