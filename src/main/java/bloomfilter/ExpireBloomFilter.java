package bloomfilter;
import orestes.bloomfilter.*;
import orestes.bloomfilter.cachesketch.*;
import redis.clients.jedis.Jedis;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

import java.util.concurrent.TimeUnit;
public class ExpireBloomFilter implements RedisBloomFilter<String> {
	private ExpireBloomFilterRedis bfc;
	
	public ExpireBloomFilter(FilterBuilder builder) {
		//bfc=new ExpiringBloomFilterPureRedis(builder);
		bfc=new ExpireBloomFilterRedis(builder);
		
	}
	
	public FilterBuilder getBuilder() {
		return bfc.config();
	}
	
	
	public TimeMap<String> getTimeToLiveMap(){
		return bfc.getTimeToLiveMap();
	}
	
	public void add(String item,long ttl) {
		if(contains(item))
			return;
		bfc.reportRead(item, ttl,TimeUnit.SECONDS );
		bfc.reportWrite(item,TimeUnit.SECONDS );
	}
	
	
	public boolean contains(String item) {
		bfc.getRemainingTTL(item, TimeUnit.SECONDS);
		return bfc.contains(item);
	}
	
	
	public boolean isCached(String item) {
		return bfc.isCached(item);
	}
	
	
	public Long getRemainingTTL(String element) {
		return bfc.getRemainingTTL(element, TimeUnit.SECONDS);
	}
	
	
	public boolean remove(String item) {
		return bfc.remove(item);
	}

	public void removeExpires(String key) {
		bfc.removeExpires(key);
		if(bfc.isEmpty())
			bfc.clear();
	}

	
	
	public void clear() {	
		bfc.clear();
	}
	
	public void remove() {
		bfc.remove();
	}
	
	
	
	public boolean isEmpty() {
		return bfc.isEmpty();
	}
	
	
	public int getHashes() {
		return bfc.getHashes();
	}
	
	
	public double getEstimatedFalsePositiveProbability() {	
		return bfc.getEstimatedFalsePositiveProbability();
	}
	
	
	public double getFalsePositiveProbability() {
		return bfc.getFalsePositiveProbability();
	}
	
	
	public int getSize() {
		return bfc.getSize();
	}
	
	
	public long getEstimatedCount(String item) {
		return bfc.getEstimatedCount(item);
	}
	
	
	public int getCountingBits() {
		return bfc.getCountingBits();
	}
	
	public void close() {
		try {
			Jedis jedis = bfc.config().pool().getResource();    
            jedis.disconnect();
            jedis.close();
        }catch(Exception e) {
        		System.out.println(e);
        }
	}
	public void destory() {
		bfc.config().pool().destroy();   
	}
	
}
