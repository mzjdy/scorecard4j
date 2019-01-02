package io.scorecard4j.training;

import java.util.HashMap;
import java.util.Map;

import io.scorecard4j.binning.FeatureBinning;
import io.scorecard4j.report.metrics.KSValue;
import io.scorecard4j.transformation.woe.WoeDatasetBuilder;
import io.scorecard4j.transformation.woe.WoeIvCalculator.FeatureWoe;
import io.scorecard4j.util.NumberFormatUtil;
import io.scorecard4j.util.ResponseUtil;
import io.scorecard4j.training.models.LogisticRegression;
import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.validation.AUC;
import smile.data.Attribute.Type;

/**
 * Logistic Regression modeler
 * @author rayeaster
 *
 */
public class LRModeler{
    
    private LogisticRegression lrModel;
    private Map<Integer, FeatureWoe> woes;
    private Map<Integer, FeatureBinning> binnings;
    
    /**
     * constructor
     * 
     * @param raw
     *            dataset containing original or after imputation data 
     * @param binnings
     *            {@link FeatureBinning} mechanisms for each attribute
     * @param goodLabel
     *            class label as good sample
     */
    public LRModeler(AttributeDataset raw, Map<Integer, FeatureBinning> binnings, int goodLabel) {
        woes = new HashMap<Integer, FeatureWoe>(binnings.size());
        AttributeDataset dataset = WoeDatasetBuilder.convert2Woe(raw, binnings, goodLabel, woes);
        
        this.binnings = binnings;
        
        int[] yInt = new int[raw.size()];
        double[] y = new double[raw.size()];
        ResponseUtil.transformResponse(raw, yInt, y);
        
        lrModel = new LogisticRegression(dataset.x(), yInt);
    }
    
    /**
     * prediction against given dataset
     * 
     * @param raw
     *            dataset containing original or after imputation data
     * @param goodLabel
     *            class label as good sample
     */
    public void predict(AttributeDataset raw, int goodLabel) {
        AttributeDataset dataset = WoeDatasetBuilder.convert2Woe(raw, binnings, goodLabel, woes);

        double[][] x = dataset.x();

        int[] truth = new int[raw.size()];
        double[] y = new double[raw.size()];
        ResponseUtil.transformResponse(raw, truth, y);
        //
        // AUC
        //
        double[] probability = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            double[] postp = new double[2];
            lrModel.predict(x[i], postp);
            probability[i] = postp[1];
        }
        double auc = AUC.measure(truth, probability);
        System.out.println("AUC on given dataset is " + NumberFormatUtil.formatTo2DigitsAfterDecimal(auc));
    }
    
    /**
     * feature weight from model training
     * @return feature weights
     */
    public double[] getWeights() {
        return lrModel.getW();
    }
    
    /**
     * 
     * @return {@link FeatureBinning} from the training data of this model
     */
    public Map<Integer, FeatureBinning> getBinnings() {
        return binnings;
    }

    /**
     * @return {@link FeatureWoe} calculator from the training data of this model
     */
    public Map<Integer, FeatureWoe> getWoes() {
        return woes;
    }
    
}