package bloomfilter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import orestes.bloomfilter.FilterBuilder;
import orestes.bloomfilter.TimeMap;
import orestes.bloomfilter.cachesketch.*;
import redis.clients.jedis.Jedis;
public class ExpireBloomFilterRedis extends AbstractExpiringBloomFilterRedis<String> {
	public ExpireBloomFilterRedis(FilterBuilder builder) {
		super(builder);
	}
	// write to the bloomfilter and set
	public Long reportWrite(String element, TimeUnit unit) {
        Long remaining = getRemainingTTL(element, unit);
        if ((remaining == null) || (remaining <= 0)) {
            return null;
        }
        writeToSet(element);
        add(element);
        return remaining;
    }
	
	
	public void writeToSet(String element) {
		try{
			Jedis jedis = pool.getResource();
            //jedis.sadd(config.name()+":tokens", element);
            jedis.sadd("total:tokens", config.name()+" "+element);
            jedis.close();
        }catch(Exception e) {
        		System.out.println(e);
        }
	}
	
	
	public void deleteFromSet(String element){
		try{
			Jedis jedis = pool.getResource();
            jedis.srem("total:tokens", element);
            jedis.close();
        }catch(Exception e) {
        		System.out.println(e);
        }
	}

	
	//remove all expired token from bloom filter and set
	public void removeExpires(String key) {
		if(getRemainingTTL(key,TimeUnit.SECONDS)==null) {
			remove(key);
			deleteFromSet(key);
		}
	}
	
//	public Set<String> getFromSet(){
//	Set<String> tokens=new HashSet<String>();
//	try{
//		Jedis jedis = pool.getResource();
//        tokens=jedis.smembers(config.name()+":tokens");
//        jedis.close();
//    }catch(Exception e) {
//    		System.out.println(e);
//    }
//	return tokens;
//}
	
	protected void addToQueue(String element, long remaining, TimeUnit timeUnit) {
    }
	public boolean setExpirationEnabled(boolean arg0) {
		return false;
	}
	public TimeMap<String> getExpirationMap() {
		return null;
	}
	public void setExpirationMap(TimeMap<String> arg0) {
	}
	
}
