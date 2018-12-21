package io.scorecard4j.dataset.abnormal;


/**
 * Detector which aims to pinpoint the unusual values in original dataset per domain knowledge.
 * @author rayeaster
 *
 */
public interface AbnormalDetector <T>{
    
    /**
     * Check given value is abnormal
     * @param value the feature to be checked
     * @return probability that the value is abnormal, typically should be [0, 1]
     */
    public double isAbnormal(T value);
}