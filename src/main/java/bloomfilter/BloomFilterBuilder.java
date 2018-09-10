package bloomfilter;

import java.util.Set;
import java.util.Map.Entry;
import orestes.bloomfilter.FilterBuilder;
import orestes.bloomfilter.HashProvider;
import orestes.bloomfilter.HashProvider.HashFunction;
import orestes.bloomfilter.HashProvider.HashMethod;


public class BloomFilterBuilder {
	private FilterBuilder builder;
	
	public FilterBuilder getBuilder() {	
		return builder;
	}
	
	public BloomFilterBuilder(int expectedElements, double falsePositiveProbability) {
		this.builder = new FilterBuilder(expectedElements,falsePositiveProbability);
	}
	
	
	/**
     * Sets the number of expected elements. In combination with the tolerable false positive probability, this is used
     * to infer the optimal size and optimal number of hash functions of the filter.
     *
     * @param expectedElements number of expected elements.
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder expectedElements(int expectedElements) {      
        		builder.expectedElements(expectedElements);
        		return this;
    }

    /**
     * Sets the size of the filter in bits.
     *
     * @param size size of the filter in bits
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder size(int size) {
        builder.size(size);
        return this;
    }
    
    /**
     * Sets a password authentication for Redis.
     *
     * @param password The Redis PW
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder password(String password) {
        builder.password(password);
        return this;
    }
    
    /**
     * Enables or disables SSL connection to Redis. <p><b>Default</b>: false</p>
     *
     * @param ssl enables or disables SSL connection to Redis
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder redisSsl(boolean ssl) {
        builder.redisSsl(ssl);
        return this;
    }

    /**
     * Sets the tolerable false positive probability. In combination with the number of expected elements, this is used
     * to infer the optimal size and optimal number of hash functions of the filter.
     *
     * @param falsePositiveProbability the tolerable false
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder falsePositiveProbability(double falsePositiveProbability) {
        
        builder.falsePositiveProbability(falsePositiveProbability);
        return this;
    }

    /**
     * Set the number of hash functions to be used.
     *
     * @param numberOfHashes number of hash functions used by the filter.
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder hashes(int numberOfHashes) {
        
        builder.hashes(numberOfHashes);
        return this;
    }

    /**
     * Sets the number of bits used for counting in case of a counting Bloom filter. For non-counting Bloom filters this
     * setting has no effect. <p><b>Default</b>: 16</p>
     *
     * @param countingBits Number of counting bits used by the counting Bloom filter
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder countingBits(int countingBits) {
        
        builder.countingBits(countingBits);
        return this;
    }

    /**
     * Sets the name of the Bloom filter. If a redis-backed Bloom filter with the provided name exists and it is
     * compatible to this FilterBuilder configuration, it will be loaded and used. This behaviour can be changed by
     * {@link #overwriteIfExists(boolean)}. <p><b>Default</b>: ""</p>
     *
     * @param name The name of the filter
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder name(String name) {
        
        builder.name(name);
        return this;
    }


    /**
     * Instructs the FilterBuilder to build a Redis-Backed Bloom filters. <p><b>Default</b>: <tt>false</tt></p>
     *
     * @param redisBacked a boolean indicating whether redis should be used
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder redisBacked(boolean redisBacked) {
        
        builder.redisBacked(redisBacked);
        return this;
    }

    /**
     * Sets the host of the backing Redis instance. <p><b>Default</b>: localhost</p>
     *
     * @param host the Redis host
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder redisHost(String host) {

        builder.redisHost(host);
        return this;
    }

    /**
     * Sets the port of the backing Redis instance. <p><b>Default</b>: 6379</p>
     *
     * @param port the Redis port
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder redisPort(int port) {
        
        builder.redisPort(port);
        return this;
    }


    /**
     * Sets the number of connections to use for Redis. <p><b>Default</b>: 10</p>
     *
     * @param numConnections the number of connections to use for Redis
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder redisConnections(int numConnections) {

        builder.redisConnections(numConnections);
        return this;
    }


    /**
     * Sets whether any existing Bloom filter with same name should be overwritten in Redis. <p><b>Default</b>:
     * <tt>false</tt></p>
     *
     * @param overwrite boolean indicating whether to overwrite any existing filter with the same name
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder overwriteIfExists(boolean overwrite) {

        builder.overwriteIfExists(overwrite);
        return this;
    }

    /**
     * Adds a read slave to speed up reading access (e.g. contains or getEstimatedCount) to normal and counting
     * Redis-backed Bloom filters. The read slave has to be a slave of the main Redis instance (this can be done in the
     * redis-cli using the SLAVEOF command). This setting might cause stale reads since Redis replication is
     * asynchronous. However anecdotally, in our experiments, we were unable to read any stale data - the replication
     * lag between both Redis instances was small than one round-trip time to Redis.
     *
     * @param host host of the Redis read slave
     * @param port port of the Redis read slave
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder addReadSlave(String host, int port) {

        builder.addReadSlave(host, port);
        return this;
    }

    /**
     * Sets the method used to generate hash values. Possible hash methods are documented in the corresponding enum
     * {@link HashProvider.HashMethod}. <p><b>Default</b>: MD5</p>
     * <p>
     * For the generation of hash values the String representation of objects is used.
     *
     * @param hashMethod the method used to generate hash values
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder hashFunction(HashMethod hashMethod) {

        builder.hashFunction(hashMethod);
        return this;
    }

    /**
     * Uses a given custom hash function.
     *
     * @param hf the custom hash function
     * @return the modified FilterBuilder (fluent interface)
     */
    public BloomFilterBuilder hashFunction(HashFunction hf) {

        builder.hashFunction(hf);
        return this;
    }

    
    /**
     * Constructs a Bloom filter using the specified parameters and computing missing parameters if possible (e.g. the
     * optimal Bloom filter bit size).
     *
     * @param <T> the type of element contained in the Bloom filter.
     * @return the constructed Bloom filter
     */
    public BloomFilterBuilder buildBloomFilter() {

        builder.buildBloomFilter();
        return this;
    }

    /**
     * Constructs a Counting Bloom filter using the specified parameters and by computing missing parameters if possible
     * (e.g. the optimal Bloom filter bit size).
     *
     * @param <T> the type of element contained in the Counting Bloom filter.
     * @return the constructed Counting Bloom filter
     */
    public BloomFilterBuilder buildCountingBloomFilter() {
        builder.buildCountingBloomFilter();
        return this;
    }

    /**
     * Checks if all necessary parameters were set and tries to infer optimal parameters (e.g. size and hashes from
     * given expectedElements and falsePositiveProbability). This is done automatically.
     *
     * @return the completed FilterBuilder
     */
    public BloomFilterBuilder complete() {
        builder.complete();
        return this;
    }


    @Override
    public BloomFilterBuilder clone() {
        builder.clone();
        return this;
    }


    /**
     * @return {@code true} if the Bloom Filter will be Redis-backed
     */
    public boolean redisBacked() {
        return builder.redisBacked();
    }

    /**
     * @return the number of expected elements for the Bloom filter
     */
    public int expectedElements() {
        return builder.expectedElements();
    }

    /**
     * @return the size of the Bloom filter in bits
     */
    public int size() {
        return builder.size();
    }

    /**
     * @return the number of hashes used by the Bloom filter
     */
    public int hashes() {
        return builder.hashes();
    }

    /**
     * @return The number of bits used for counting in case of a counting Bloom filter
     */
    public int countingBits() {
        return builder.countingBits();
    }

    /**
     * @return the tolerable false positive probability of the Bloom filter
     */
    public double falsePositiveProbability() {
        return builder.falsePositiveProbability();
    }

    /**
     * @return the name of the Bloom filter
     */
    public String name() {
        return builder.name();
    }

    /**
     * @return the host name of the Redis server backing the Bloom filter
     */
    public String redisHost() {
        return builder.redisHost();
    }

    /**
     * @return the port used by the Redis server backing the Bloom filter
     */
    public int redisPort() {
        return builder.redisPort();
    }

    /**
     * @return the number of connections used by the Redis Server backing the Bloom filter
     */
    public int redisConnections() {
        return builder.redisConnections();
    }


    /**
     * @return The hash method to be used by the Bloom filter
     */
    public HashMethod hashMethod() {
        return builder.hashMethod();
    }

    /**
     * @return the actual hash function to be used by the Bloom filter
     */
    public HashFunction hashFunction() {
        return builder.hashFunction();
    }


    /**
     * @return {@code true} if the Bloom filter that is to be built should overwrite any existing Bloom filter with the
     * same name
     */
    public boolean overwriteIfExists() {
        return builder.overwriteIfExists();
    }

    /**
     * @return return the list of all read slaves to be used by the Redis-backed Bloom filter
     */
    public Set<Entry<String, Integer>> getReadSlaves() {
        return builder.getReadSlaves();
    }
    
    /**
     * @return if SSL is enabled for Redis connection
     */
    public boolean redisSsl() {
        return builder.redisSsl();
    }

    /**
     * Checks whether a configuration is compatible to another configuration based on the size of the Bloom filter and
     * its hash functions.
     *
     * @param other the other configuration
     * @return {@code true} if the configurations are compatible
     */
    public boolean isCompatibleTo(FilterBuilder other) {
        return builder.isCompatibleTo(other);
    }

}
