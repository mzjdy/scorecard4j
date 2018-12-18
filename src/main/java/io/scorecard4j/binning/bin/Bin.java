package io.scorecard4j.binning.bin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Bin{
    
    /**
     * sample count in this bin
     */
    int sampleCount;
    
    /**
     * sample count of each class label in this bin
     */
    Map<Integer, AtomicInteger> sampleClassCounts;
    
    /**
     * constructor
     */
    Bin(){
        sampleClassCounts = new HashMap<Integer, AtomicInteger>();
    }
   
    /**
     * increment sample count in this bin by one 
     * @return sample count after addition
     */
    public int addToBin() {
        return sampleCount++;
    }

    /**
     * increment sample count of given class label in this bin by one
     * @param clz class label to add in this bin
     * @return sample count of given class label after addition
     */
    public int addClassToBin(int clz) {
        AtomicInteger count = sampleClassCounts.get(clz);
        if(count == null) {
            count = new AtomicInteger();
            sampleClassCounts.put(clz, count);
        }
        return count.incrementAndGet();
    }
    
    /**
     * get sample count in this bin
     */
    public int getSampleCount() {
        return sampleCount;
    }
    
    /**
     * get sample count of given class label in this bin
     */
    public int getSampleClassCounts(int clz) {
        return sampleClassCounts.get(clz) == null? 0 : sampleClassCounts.get(clz).intValue();
    }
    
    /**
     * get sample counts in this bin
     */
    public Map<Integer, AtomicInteger> getSampleClassCounts() {
        return sampleClassCounts;
    }
}