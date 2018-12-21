package io.scorecard4j.binning.bin;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.scorecard4j.util.NumberFormatUtil;

/**
 * Numeric feature bin 
 * 
 * @author rayeaster
 *
 */
public class NumericBin extends Bin{
    
    private String s;
    
    /**
     * inclusive lower bound of this bin
     */
    private double low = Double.NEGATIVE_INFINITY;
    
    /**
     * exclusive upper bound of this bin 
     */
    private double high = Double.NEGATIVE_INFINITY;

    /**
     * constructor
     * @param low inclusive lower bound of this numeric bin
     * @param high exclusive upper bound of this numeric bin
     */
    public NumericBin(double low, double high) {
        super();
        this.low = low;
        this.high = high;
        this.s = "numericBin[" + NumberFormatUtil.formatTo2DigitsAfterDecimal(low) + "," + NumberFormatUtil.formatTo2DigitsAfterDecimal(high) + ")";
    }
    
    /**
     * check if given value in this bin
     * @param value to be check
     * @return if given value in this bin
     */
    public boolean inThisBin(double val) {
        return val >= low && val < high;
    }

    /**
     * get inclusive lower bound of this numeric bin
     * @return inclusive lower bound of this numeric bin
     */
    public double getLow() {
        return low;
    }

    /**
     * get exclusive upper bound of this numeric bin
     * @return exclusive upper bound of this numeric bin
     */
    public double getHigh() {
        return high;
    }
    
    @Override
    public String toString() {
        return s;
    }
        
    /**
     * merge with another {@link NumericBin}
     * @param bin {@link NumericBin} to be merged
     * @return {@link NumericBin} after merge
     */
    public NumericBin merge(NumericBin bin) {
        this.low = (this.low <= bin.low)? this.low : bin.low;
        this.high = (this.high >= bin.high)? this.high : bin.high;
        
        this.sampleCount += bin.sampleCount;
        for(Entry<Integer, AtomicInteger> ent : bin.sampleClassCounts.entrySet()) {
            Integer key = ent.getKey();
            if(this.sampleClassCounts.containsKey(key)) {
                this.sampleClassCounts.get(key).addAndGet(ent.getValue().intValue());
            }else {
                this.sampleClassCounts.put(key, ent.getValue()); 
            }
        }
        
        this.s = "numericBin[" + NumberFormatUtil.formatTo2DigitsAfterDecimal(low) + "," + NumberFormatUtil.formatTo2DigitsAfterDecimal(high) + ")";
        return this;
    }
}
