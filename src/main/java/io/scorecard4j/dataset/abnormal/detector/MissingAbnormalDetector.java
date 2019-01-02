package io.scorecard4j.dataset.abnormal.detector;

import io.scorecard4j.dataset.abnormal.AbnormalDetector;

/**
 * 
 * @author lma
 *
 */
public class MissingAbnormalDetector implements AbnormalDetector<Double>{
    
    private Double missing = Double.NaN;
    
    /**
     * constructor
     * @param missing value indicating a missing sample
     */
    public MissingAbnormalDetector(Double missing){
        this.missing = missing;
    }
    
    /**
     * constructor
     * @param missing value indicating a missing sample
     */
    public MissingAbnormalDetector(){
        
    }

    @Override
    public double isAbnormal(Double value) {
        return (value.equals(missing))? 1 : 0;
    }
    
    @Override
    public String toString() {
        return missing.toString();
    }
    
}