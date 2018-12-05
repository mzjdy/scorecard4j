package io.scorecard4j.binning;

import io.scorecard4j.binning.bin.Bin;

/**
 * Another naive binning to divide the feature
 * to make each bin containing equal-frequency of sample data.
 * @author rayeaster
 *
 */
public class EqualFrequencyBinning<T> implements FeatureBinning<T>{

    public EqualFrequencyBinning() {
        
    }
    
    public boolean findBinning(T[] values, int[] clzz, boolean numeric) {
        return false;
    }

    public Bin getBinning(T value, int clz, boolean numeric) {
        return null;
    }
    
}