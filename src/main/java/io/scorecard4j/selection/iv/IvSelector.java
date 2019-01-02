package io.scorecard4j.selection.iv;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.scorecard4j.binning.FeatureBinning;
import io.scorecard4j.transformation.woe.WoeDatasetBuilder;
import io.scorecard4j.transformation.woe.WoeIvCalculator.FeatureWoe;
import smile.data.AttributeDataset;

/**
 * Feature Selector using IV value as decision base.
 * 
 * @author lma
 *
 */
public class IvSelector {
    
    /**
     * calculate and return the IV value for given data     
     * 
     * @param raw
     *            dataset containing original or after imputation data
     * @param binnings
     *            {@link FeatureBinning} mechanisms for each attribute
     * @param goodLabel
     *            class label as good sample
     * @param calculators
     *            {@link FeatureWoe} calculator for given binnings
     * @param yInt
     *            class labels for sample
     *            
     * @return IV value for given data and features
     */
    public static double[] checkIv4Features(AttributeDataset raw, Map<Integer, FeatureBinning> binnings, int goodLabel,
            Map<Integer, FeatureWoe> calculators, int[] yInt) {
        WoeDatasetBuilder.calculateWoe(raw, binnings, goodLabel, calculators, yInt);

        double[] ret = new double[binnings.size()];
        int cnt = 0;
        for(Entry<Integer, FeatureWoe> ent : calculators.entrySet()) {
            double iv = ent.getValue().iv();
            ret[cnt++] = iv;
            System.out.println("IV of Feature#" + ent.getKey() + "=" + iv);
        }        
        
        return ret;
    }
}