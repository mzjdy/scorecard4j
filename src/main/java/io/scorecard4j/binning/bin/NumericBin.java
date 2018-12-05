package io.scorecard4j.binning.bin;

/**
 * Numeric feature bin 
 * 
 * @author rayeaster
 *
 */
public class NumericBin extends Bin{
    
    /**
     * inclusive lower bound of this bin
     */
    double low;
    
    /**
     * exclusive upper bound of this bin 
     */
    double high;

    /**
     * constructor
     * @param low inclusive lower bound of this numeric bin
     * @param high exclusive upper bound of this numeric bin
     */
    public NumericBin(double low, double high) {
        super();
        this.low = low;
        this.high = high;
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
    
}
