package io.scorecard4j.binning;

import io.scorecard4j.binning.bin.Bin;

/**
 * Merge adjacent interval which has minimum chi-squared test.
 * @author rayeaster
 *
 */
public class ChiMergeBinning<T> extends AbstractBinning<T> implements FeatureBinning<T>{

    public ChiMergeBinning() {
        
    }
    
    public boolean findBinning(T[] values, int[] clzz, boolean numeric) {
        return false;
    }

    public Bin getBinning(T value, boolean numeric) {
        return null;
    }
    
}