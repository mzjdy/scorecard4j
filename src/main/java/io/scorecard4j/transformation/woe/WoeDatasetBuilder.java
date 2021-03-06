package io.scorecard4j.transformation.woe;

import java.util.Map;

import io.scorecard4j.binning.FeatureBinning;
import io.scorecard4j.binning.ProvidedBinning;
import io.scorecard4j.binning.bin.Bin;
import io.scorecard4j.transformation.woe.WoeIvCalculator.FeatureWoe;
import io.scorecard4j.util.ResponseUtil;
import smile.data.Attribute;
import smile.data.Attribute.Type;
import smile.data.AttributeDataset;
import smile.data.AttributeVector;

/**
 * Builder to transform raw data into WOE for model training.
 * 
 * @author rayeaster
 *
 */
public class WoeDatasetBuilder {
    
    /**
     * calculate WOE for given data
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
     * @return dataset with WOE feature values
     */
    public static void calculateWoe(AttributeDataset raw, Map<Integer, FeatureBinning> binnings, int goodLabel,
            Map<Integer, FeatureWoe> calculators, int[] yInt) {

        Attribute[] rawFeatures = raw.attributes();

        //
        // binning and WOE calculation
        //
        if (calculators.size() == 0) {
            for (int i = 0; i < rawFeatures.length; i++) {
                Attribute attr = rawFeatures[i];
                AttributeVector column = raw.column(i);
                boolean numeric = true;
                if (attr.getType() == Type.NUMERIC || attr.getType() == Type.NOMINAL) {
                    numeric = attr.getType() == Type.NUMERIC ? true : false;
                    double[] vector = column.vector();
                    FeatureBinning binning = binnings.get(i);
                    if (binning instanceof ProvidedBinning) {
                        // skip binning process
                    } else {
                        if (numeric) {
                            Double[] values = new Double[vector.length];
                            for (int j = 0; j < vector.length; j++) {
                                values[j] = vector[j];
                            }
                            binning.findBinning(values, yInt, goodLabel, numeric);
                        } else {
                            Integer[] values = new Integer[vector.length];
                            for (int j = 0; j < vector.length; j++) {
                                values[j] = (int) vector[j];
                            }
                            binning.findBinning(values, yInt, goodLabel, numeric);
                        }
                    }
                    FeatureWoe fw = WoeIvCalculator.calculation(binning, goodLabel, numeric);
                    calculators.put(i, fw);
                } else {
                    throw new RuntimeException(
                            "unsupport attribute: name=" + attr.getName() + ", type=" + attr.getType());
                }
            }
        }
    }

    /**
     * Transform raw dataset into WOE valued dataset
     * 
     * @param raw
     *            dataset containing original or after imputation data
     * @param binnings
     *            {@link FeatureBinning} mechanisms for each attribute
     * @param goodLabel
     *            class label as good sample
     * @param calculators
     *            {@link FeatureWoe} calculator for given binnings
     * 
     * @return dataset with WOE feature values
     */
    public static AttributeDataset convert2Woe(AttributeDataset raw, Map<Integer, FeatureBinning> binnings,
            int goodLabel, Map<Integer, FeatureWoe> calculators) {

        Attribute[] rawFeatures = raw.attributes();

        int[] yInt = new int[raw.size()];
        double[] y = new double[raw.size()];
        ResponseUtil.transformResponse(raw, yInt, y);

        calculateWoe(raw, binnings, goodLabel, calculators, yInt);

        //
        // get WOE transformation
        //
        Attribute response = raw.responseAttribute();
        Attribute[] woeFeatures = new Attribute[rawFeatures.length];
        
        double[][] x = raw.x();
        double[][] xwoe = new double[x.length][x[0].length];

        for (int i = 0; i < x.length; i++) {
            double[] data = x[i];
            double[] datawoe = convert2Woe(data, rawFeatures, binnings, calculators);
            xwoe[i] = datawoe;
        }

        AttributeDataset ret = new AttributeDataset(raw.getName() + "_WOE", woeFeatures, xwoe, response, y);
        return ret;
    }
    
    /**
     * Transform raw datum into WOE valued datum
     * 
     * @param raw
     *            datum containing original or after imputation data 
     * @param rawFeatures
     *            datum features
     * @param binnings
     *            {@link FeatureBinning} mechanisms for each attribute
     * @param calculators
     *            {@link FeatureWoe} calculator for given binnings
     *            
     * @return datum with WOE feature values
     */
    public static double[] convert2Woe(double[] raw, Attribute[] rawFeatures, Map<Integer, FeatureBinning> binnings, Map<Integer, FeatureWoe> calculators) {
        double[] data = raw;
        double[] datawoe = new double[data.length];
        for (int j = 0; j < data.length; j++) {
            Attribute attr = rawFeatures[j];
            FeatureWoe fw  = calculators.get(j);
            if (attr.getType() == Type.NUMERIC) {
                FeatureBinning<Double> binning = binnings.get(j);
                Bin bin = binning.getBinning(data[j], true);
                datawoe[j] = fw.woe(bin);
            } else if (attr.getType() == Type.NOMINAL) {
                FeatureBinning<Integer> binning = binnings.get(j);
                Bin bin = binning.getBinning((int)data[j], false);
                datawoe[j] = fw.woe(bin);
            } else {
                throw new RuntimeException(
                        "unsupport attribute: name=" + attr.getName() + ", type=" + attr.getType());
            }
        }
        return datawoe;
    }
    
    /**
     * Transform raw datum into WOE valued datum
     * 
     * @param raw
     *            datum containing original or after imputation data 
     * @param rawFeatures
     *            datum features
     * @param binnings
     *            {@link FeatureBinning} mechanisms for each attribute
     * @param calculators
     *            {@link FeatureWoe} calculator for given binnings
     *            
     * @return datum with WOE feature values
     */
    public static Bin[] getBin4Data(double[] raw, Attribute[] rawFeatures, Map<Integer, FeatureBinning> binnings, Map<Integer, FeatureWoe> calculators) {
        double[] data = raw;
        Bin[] bins = new Bin[data.length];
        for (int j = 0; j < data.length; j++) {
            Attribute attr = rawFeatures[j];
            FeatureWoe fw  = calculators.get(j);
            if (attr.getType() == Type.NUMERIC) {
                FeatureBinning<Double> binning = binnings.get(j);
                Bin bin = binning.getBinning(data[j], true);
                bins[j] = bin;
            } else if (attr.getType() == Type.NOMINAL) {
                FeatureBinning<Integer> binning = binnings.get(j);
                Bin bin = binning.getBinning((int)data[j], false);
                bins[j] = bin;
            } else {
                throw new RuntimeException(
                        "unsupport attribute: name=" + attr.getName() + ", type=" + attr.getType());
            }
        }
        return bins;
    }

}