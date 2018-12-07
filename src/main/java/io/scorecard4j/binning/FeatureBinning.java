package io.scorecard4j.binning;

import io.scorecard4j.binning.bin.Bin;

/**
 * Interface for feature binning 
 * 
 * @author rayeaster
 *
 */
public interface FeatureBinning <T> {
    
    /**
     * find binning for given feature values
     * @param values feature values to be binned
     * @param clzz class labels for each feature values
     * @param numeric true if numeric feature otherwise false, i.e.,categorical 
     * @return true if binning successfully found otherwise false
     */
    boolean findBinning(T[] values, int[] clzz, boolean numeric);
    
    /**
     * get the binned result for given feature value
     * @param value feature value to be binned
     * @param numeric true if numeric feature otherwise false, i.e.,categorical 
     * @return the appropriate bin for the given value
     */
    Bin getBinning(T value, boolean numeric);
}