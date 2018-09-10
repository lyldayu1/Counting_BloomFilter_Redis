package bloomfilter;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import orestes.bloomfilter.FilterBuilder;
import orestes.bloomfilter.TimeMap;

public interface RedisBloomFilter<T> extends Cloneable, Serializable {
	
	public void add(T item,long ttl);
	
	public boolean contains(T item);
	
	
	/**
     * Return the expiration timestamp of an object
     *
     * @param element the element (or its id)
     * @return the remaining ttl(second)
     */
	public Long getRemainingTTL(T element);
	
	public FilterBuilder getBuilder();
	
	public boolean remove(T item);
	
	public void removeExpires(String key);
	
	
	/**
     * Determines whether a given object is no-expired
     *
     * @param element the element (or its id)
     * @return <code>true</code> if the element is non-expired
     */
	public boolean isCached(T item);
	/**
     * clear all the data in bloom filter
     *
     */
	public void clear();
	/**
     * remove the current job from the queue
     *
     */
	public void remove();
	/**
     * close the connection between jedis and pool
     *
     */
	public void close();
	/**
     * delete pool thread
     *
     */
	public void destory();
	public boolean isEmpty();
	/**
     * Return the size of the Bloom filter, i.e. the number of positions in the underlyling bit vector (called m in the
     * literature).
     *
     * @return the bit vector size
     */
	public int getSize();
	
	
    /**
     * Returns the number of hash functions (called k in the literature)
     *
     * @return the number of hash functions
     */
	public int getHashes();
	
	
	/**
     * @return the number of bits used for counting
     */
	public int getCountingBits();
	

    /**
     * Return the estimated count for an element using the Mininum Selection algorithm (i.e. by choosing the minimum
     * counter for the given element). This estimation is biased, as it doest not consider how full the filter is, but
     * performs best in practice. The underlying theoretical foundation are spectral Bloom filters, see:
     * http://theory.stanford.edu/~matias/papers/sbf_thesis.pdf
     *
     * @param element element to query
     * @return estimated count of the element
     */
	public long getEstimatedCount(T item);
	
	
	/**
     * Returns the probability of a false positive (approximated): <br> <code>(1 - e^(-hashes * insertedElements /
     * size)) ^ hashes</code>
     *
     * @param insertedElements The number of elements already inserted into the Bloomfilter
     * @return probability of a false positive after <i>expectedElements</i> {@link #addRaw(byte[])} operations
     */
	public double getFalsePositiveProbability();
	
	
	/**
     * Returns the probability of a false positive (approximated) using an estimation of how many elements are currently in the filter
     *
     * @return probability of a false positive
     */
	public double getEstimatedFalsePositiveProbability();
	
	public TimeMap<String> getTimeToLiveMap();
}
