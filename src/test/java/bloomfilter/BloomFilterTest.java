package bloomfilter;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import org.junit.Test;

import JobQueue.JobQueue;

/**
 * Unit test for simple App.
 */
public class BloomFilterTest
{
	public void writeFile() throws IOException {
		Random random=new Random();
		FileWriter fw=new FileWriter("/Users/yuliang.liu/Desktop/record",true);
		for(int i=0;i<1000;i++) {
			String item="";
			for(int j=0;j<500;j++) {
				item+=String.valueOf((char)('A'+random.nextInt(58)));
			}
			fw.write(item);
			fw.write("\n");
		}
		fw.flush();
		fw.close();
	}
	@Test
	public void testExpireBefore() throws InterruptedException, IOException {		
		writeFile();
		String host="127.0.0.1";
		int port=6379;
		String filterName="countingbloomfilter";
		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		BufferedReader fr=new BufferedReader(new FileReader("/Users/yuliang.liu/Desktop/record"));
		String line=fr.readLine();
		while(line!=null) {

			bfc.add(line,100);
			line=fr.readLine();
		}
		fr.close();
	}
	@Test
	public void testExpireAfter() throws InterruptedException, IOException {		
		String host="127.0.0.1";
		int port=6379;
		String filterName="countingbloomfilter";
		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		//bfc.removeExpires();
		//read each record to check if it had been deleted from bloom filter
		BufferedReader fr=new BufferedReader(new FileReader("/Users/yuliang.liu/Desktop/record"));
		String line=fr.readLine();
		while(line!=null) {
			System.out.println(bfc.getRemainingTTL(line));
			assertFalse(bfc.contains(line));
			line=fr.readLine();
		}
		
		System.out.println(bfc.getCountingBits());
		System.out.println(bfc.getSize());
		System.out.println("estimated false positive:"+bfc.getEstimatedFalsePositiveProbability());
	}

	@Test
	public void testMemory() {
		String host="127.0.0.1";
		int port=6379;
		String filterName="countingbloomfilter";
		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.redisConnections(20)
				.getBuilder());
		
		Random random=new Random();
	    System.out.println("counting bit:"+bfc.getCountingBits());
	    System.out.println("estimated false positive:"+bfc.getEstimatedFalsePositiveProbability());
	    System.out.println("false positive:"+bfc.getFalsePositiveProbability());
	    System.out.println("hash size"+bfc.getHashes());
	    System.out.println("total"+bfc.getSize());
		for(int i=0;i<30;i++) {
			System.out.println(i);
			String item="";
			for(int j=0;j<10;j++) {
				item+=String.valueOf((char)random.nextInt(256));
			}
			bfc.add(item,20000);
		}
	    System.out.println("counting bit:"+bfc.getCountingBits());
	    System.out.println("estimated false positive:"+bfc.getEstimatedFalsePositiveProbability());
	    System.out.println("false positive:"+bfc.getFalsePositiveProbability());
	    System.out.println("hash size"+bfc.getHashes());
	    System.out.println("total"+bfc.getSize());
	}

	
	@Test
	public void testfilterName() {
		String host="127.0.0.1";
		int port=6379;
		String filterName="countingbloomfilter";

		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		
		//same filtername
		String filterName1="countingbloomfilter";
		RedisBloomFilter<String> bfc_same=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName1)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		
		//differet filtername
		String filterName2="countingbloomfilter1";
		RedisBloomFilter<String> bfc_diff=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName2)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		
		
		assertFalse(bfc.contains("www.centrify.com"));
		assertFalse(bfc.contains("www.test.com"));
		bfc.add("www.centrify.com",10);
		bfc.add("www.test.com",10);
		assertTrue(bfc.contains("www.centrify.com"));
		assertTrue(bfc.contains("www.test.com"));
		

		//test if same filtername's bloom filter share the data
		assertTrue(bfc_same.contains("www.centrify.com"));
		assertTrue(bfc_same.contains("www.test.com"));
		
		//test if one remove the data, samename bloomfilter could not access 
		bfc_same.remove("www.centrify.com");
		bfc_same.remove("www.test.com");
		assertFalse(bfc.contains("www.centrify.com"));
		assertFalse(bfc.contains("www.test.com"));
		
		//test if different bloom filter have seperate bloom filter
		assertFalse(bfc_diff.contains("www.linkedin.com"));
		assertFalse(bfc_diff.contains("www.youtube.com"));
		bfc_diff.add("www.linkedin.com",10);
		bfc_diff.add("www.youtube.com",10);
		assertTrue(bfc_diff.contains("www.linkedin.com"));
		assertTrue(bfc_diff.contains("www.youtube.com"));
		assertFalse(bfc.contains("www.linkedin.com"));
		assertFalse(bfc.contains("www.youtube.com"));
		assertFalse(bfc_same.contains("www.linkedin.com"));
		assertFalse(bfc_same.contains("www.youtube.com"));
		//bfc.removeExpires();
		//bfc_diff.removeExpires();
	}
	@Test
	public void testDestoryBefore() {
		String host="127.0.0.1";
		int port=6379;
		String filterName="countingbloomfilter";
		// TODO Auto-generated method stub
		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		assertFalse(bfc.contains("www.google.com"));
		assertFalse(bfc.contains("www.facebook.com"));
		bfc.add("www.google.com", 20);
		bfc.add("www.facebook.com",20);
		assertTrue(bfc.contains("www.google.com"));
		assertTrue(bfc.contains("www.facebook.com"));
		bfc.close();
		bfc.destory();
	}
	@Test
	public void testDestoryAfter() {
		String host="127.0.0.1";
		int port=6379;
		String filterName="countingbloomfilter";
		// TODO Auto-generated method stub
		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		System.out.println(bfc.contains("www.google.com"));
		System.out.println(bfc.contains("www.facebook.com"));
	}
	@Test
	public void testMultipleinsert() {
		String host="127.0.0.1";
		int port=6379;
		String filterName="countingbloomfilter";
		// TODO Auto-generated method stub
		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		
		System.out.println("output should be false: "+(bfc.contains("douban.com")));
		bfc.add("douban.com",90000);
		bfc.add("douban.com",90000);
		bfc.add("douban.com",90000);
		assertTrue(bfc.contains("douban.com"));
		bfc.remove("douban.com");
		assertFalse(bfc.contains("douban.com"));
	}
	
	@Test
	public void testThreadStopBefore() throws InterruptedException {
		String host="127.0.0.1";
		int port=6379;
		String filterName="countingbloomfilter";
		// TODO Auto-generated method stub
		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		String s1="www.google.com";
		String s2="www.facebook.com";
		String s3="www.qq.com";
		String s4="www.mtime.com";
		assertFalse(bfc.contains(s1));
		assertFalse(bfc.contains(s2));
		assertFalse(bfc.contains(s3));
		assertFalse(bfc.contains(s4));
		bfc.add(s1,5);
		bfc.add(s2,5);
		bfc.add(s3,5);
		bfc.add(s4,5);
		System.out.println(bfc.getEstimatedFalsePositiveProbability());
		Thread.sleep(6000);
		System.out.println(bfc.getRemainingTTL(s1));
		System.out.println(bfc.getRemainingTTL(s2));
		System.out.println(bfc.getRemainingTTL(s3));
		System.out.println(bfc.getRemainingTTL(s4));
		System.out.println("counting bit:"+bfc.getCountingBits());
	    System.out.println("estimated false positive:"+bfc.getEstimatedFalsePositiveProbability());
	    System.out.println("false positive:"+bfc.getFalsePositiveProbability());
	    System.out.println("hash size"+bfc.getHashes());
	    System.out.println("total"+bfc.getSize());
	}
	@Test
	public void testThreadStopAfter(){
		String host="127.0.0.1";
		int port=6379;
		String filterName="countingbloomfilter";
		// TODO Auto-generated method stub
		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
		String s1="www.google.com";
		String s2="www.facebook.com";
		String s3="www.qq.com";
		String s4="www.mtime.com";
		//bfc.removeExpires();
		assertFalse(bfc.contains(s1));
		assertFalse(bfc.contains(s2));
		assertFalse(bfc.contains(s3));
		assertFalse(bfc.contains(s4));
		System.out.println(bfc.getEstimatedFalsePositiveProbability());
	}
	public void testQuartz() throws InterruptedException {
		String host="127.0.0.1";
		int port=6379;
		String filterName="countingbloomfilter";
		// TODO Auto-generated method stub
		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
//		JobQueue queue=new JobQueue();
//		queue.start(bfc);
		
	}


}
