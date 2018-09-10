# Counting_BloomFilter_Redis
This is counting bloom filters with Redis-backing which support the operation of deleting expire tokens. This library takes inspiration from Orestes-Bloomfilter. 
Example:
1. initial bloomfilter:
```java
    RedisBloomFilter<String> bfc=new ExpireBloomFilter(new BloomFilterBuilder(100000, 0.01)
				.name(filterName)
				.redisBacked(true)
				.redisHost(host)
				.redisPort(port)
				.getBuilder());
```	

2. add key:
	bfc.add(key);
3. remove key:
	bfc.remove(key);
4. judge if it contains keys:
	bfc.contains(key);


There is another quartz job to delete all expire tokens from all bloom filter in a regular time.
example:
1. inital the job:
	JobQueue queue=new JobQueue(host,port);// host and port are redis's host and port
2. start the job:
	queue.start();
3. shutdown the job:
	queue.shutdown();
