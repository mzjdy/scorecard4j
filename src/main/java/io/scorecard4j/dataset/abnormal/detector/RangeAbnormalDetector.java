package io.scorecard4j.dataset.abnormal.detector;

import io.scorecard4j.dataset.abnormal.AbnormalDetector;

/**
 * 
 * @author lma
 *
 */
public class RangeAbnormalDetector implements AbnormalDetector<Double>{
    
    private double low = Double.NEGATIVE_INFINITY;
    private double high = Double.POSITIVE_INFINITY;
    
    /**
     * constructor
     * @param low range low limit, inclusively
     * @param high range high limit, exclusively
     */
    public RangeAbnormalDetector(double low, double high){
        this.low = low;
        this.high = high;
    }

    @Override
    public double isAbnormal(Double value) {
        return (value >= high || value < low)? 1 : 0;
    }
    
    @Override
    public String toString() {
        return "[" + low + "," + high + ")";
    }
    
}