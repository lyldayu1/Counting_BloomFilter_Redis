package bloomfilter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Map;
import java.util.Random;

import JobQueue.JobQueue;

import java.io.*;


public class Demo {
	public void writeFile() throws IOException {
		Random random=new Random();
		FileWriter fw=new FileWriter("~/Desktop/record",true);
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
		BufferedReader fr=new BufferedReader(new FileReader("~/Desktop/record"));
		String line=fr.readLine();
		int count=0;
		while(line!=null) {
			System.out.println(count++);
			bfc.add(line,100);
			line=fr.readLine();
		}
		fr.close();
	}

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
		//read each record to check if it had been deleted from bloom filter
		BufferedReader fr=new BufferedReader(new FileReader("~/Desktop/record"));
		String line=fr.readLine();
		while(line!=null) {
			System.out.println(bfc.getRemainingTTL(line));
			System.out.println(bfc.contains(line));
			line=fr.readLine();
		}
		
		System.out.println(bfc.getCountingBits());
		System.out.println(bfc.getSize());
		System.out.println("estimated false positive:"+bfc.getEstimatedFalsePositiveProbability());
	}

	
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
		RedisBloomFilter<String> bfc_same=new ExpireBloomFilter(new BloomFilterBuilder(1000, 0.01)
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
		
		
		System.out.println("output should be false: "+bfc.contains("www.centrify.com"));
		System.out.println("output should be false: "+bfc.contains("www.test.com"));
		bfc.add("www.centrify.com",10);
		bfc.add("www.test.com",10);
		System.out.println("output should be true: "+bfc.contains("www.centrify.com"));
		System.out.println("output should be true: "+bfc.contains("www.test.com"));
		

		//test if same filtername's bloom filter share the data
		System.out.println("output should be true: "+bfc_same.contains("www.centrify.com"));
		System.out.println("output should be true: "+bfc_same.contains("www.test.com"));
		
		//test if one remove the data, samename bloomfilter could not access 
		bfc_same.remove("www.centrify.com");
		bfc_same.remove("www.test.com");
		System.out.println("output should be false: "+bfc.contains("www.centrify.com"));
		System.out.println("output should be false: "+bfc.contains("www.test.com"));
		
		//test if different bloom filter have seperate bloom filter
		System.out.println("output should be false: "+bfc_diff.contains("www.linkedin.com"));
		System.out.println("output should be false: "+bfc_diff.contains("www.youtube.com"));
		bfc_diff.add("www.linkedin.com",10);
		bfc_diff.add("www.youtube.com",10);
		System.out.println("output should be true: "+bfc_diff.contains("www.linkedin.com"));
		System.out.println("output should be true: "+bfc_diff.contains("www.youtube.com"));
		System.out.println("output should be false: "+bfc.contains("www.linkedin.com"));
		System.out.println("output should be false: "+bfc.contains("www.youtube.com"));
		System.out.println("output should be false: "+bfc_same.contains("www.linkedin.com"));
		System.out.println("output should be false: "+bfc_same.contains("www.youtube.com"));
		
	}
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
		System.out.println(bfc.contains("www.google.com"));
		System.out.println(bfc.contains("www.facebook.com"));
		bfc.add("www.google.com", 20);
		bfc.add("www.facebook.com",20);
		System.out.println(bfc.contains("www.google.com"));
		System.out.println(bfc.contains("www.facebook.com"));
		bfc.close();
		bfc.destory();
	}
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
		System.out.println("output should be true: "+(bfc.contains("douban.com")));
		bfc.remove("douban.com");
		System.out.println("output should be false: "+(bfc.contains("douban.com")));
	}
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
		System.out.println(bfc.contains(s1));
		System.out.println(bfc.contains(s2));
		System.out.println(bfc.contains(s3));
		System.out.println(bfc.contains(s4));
		bfc.add(s1,5);
		bfc.add(s2,5);
		bfc.add(s3,5);
		bfc.add(s4,5);
		System.out.println(bfc.getRemainingTTL(s1));
		System.out.println(bfc.getRemainingTTL(s2));
		System.out.println(bfc.getRemainingTTL(s3));
		System.out.println(bfc.getRemainingTTL(s4));
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
		System.out.println(bfc.contains(s1));
		System.out.println(bfc.contains(s2));
		System.out.println(bfc.contains(s3));
		System.out.println(bfc.contains(s4));
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
		JobQueue queue=new JobQueue(host,port);
		queue.start();
		//queue.start(bfc);
		
	}
	
	public static void main(String[] args) throws InterruptedException, IOException  {


//		String host="127.0.0.1";
//		int port=6379;
//		String filterName="countingbloomfilter";
//		// TODO Auto-generated method stub
//		RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
//				.name(filterName)
//				.redisBacked(true)
//				.redisHost(host)
//				.redisPort(port)
//				.getBuilder());
//		JobQueue queue=new JobQueue();
//		queue.start(bfc);
		Demo demo=new Demo();
		//demo.testQuartz();
		demo.testfilterName();
	}

}
