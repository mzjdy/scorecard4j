package io.scorecard4j.report;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.scorecard4j.binning.EqualDistanceBinning;
import io.scorecard4j.binning.EqualFrequencyBinning;
import io.scorecard4j.binning.FeatureBinning;
import io.scorecard4j.binning.ProvidedBinning;
import io.scorecard4j.training.LRModeler;
import io.scorecard4j.transformation.woe.WoeIvCalculator.FeatureWoe;
import io.scorecard4j.util.NumberFormatUtil;
import io.scorecard4j.util.ResponseUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.NominalAttribute;
import smile.data.NumericAttribute;
import io.scorecard4j.dataset.DelimitedTextParser;
import io.scorecard4j.dataset.abnormal.detector.MissingAbnormalDetector;
import io.scorecard4j.dataset.abnormal.detector.RangeAbnormalDetector;
import io.scorecard4j.report.metrics.KSValue;
import io.scorecard4j.selection.iv.IvSelector;

/**
 * unit test for {@link Scorer} with dataset:
 * <a href="https://www.kaggle.com/kabure/predicting-credit-risk-model-pipeline/data">GermanCreditRisk</a>
 */
public class GermanCreditRiskTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public GermanCreditRiskTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(GermanCreditRiskTest.class);
    }

    /**
     * <table>
     * <tr>
     * <th>Variable Name</th>
     * <th>Description</th>
     * <th>Type</th>
     * </tr>
     * <tr>
     * <td>Age</td>
     * <td>how old are you</td>
     * <td>numeric</td>
     * <tr>
     * <tr>
     * <td>Sex</td>
     * <td>male/female</td>
     * <td>text</td>
     * <tr>
     * <tr>
     * <td>Job</td>
     * <td>0 - unskilled and non-resident, 1 - unskilled and resident, 2 - skilled, 3 - highly skilled</td>
     * <td>numeric</td>
     * <tr>
     * <tr>
     * <td>Housing</td>
     * <td>own/rent/free</td>
     * <td>text</td>
     * <tr>
     * <tr>
     * <td>Saving accounts</td>
     * <td>little/moderate/quite rich/rich</td>
     * <td>text</td>
     * <tr>
     * <tr>
     * <td>Checking account</td>
     * <td>little/moderate/quite rich/rich</td>
     * <td>text</td>
     * <tr>
     * <tr>
     * <td>Credit amount</td>
     * <td>balance denominated in Deutsch Mark</td>
     * <td>numeric</td>
     * <tr>
     * <tr>
     * <td>Duration</td>
     * <td>in month</td>
     * <td>integer</td>
     * <tr>
     * <tr>
     * <td>Purpose</td>
     * <td>car|furniture/equipment|radio/TV|domestic appliances|repairs|education|business|vacation/others</td>
     * <td>text</td>
     * <tr>
     * </table>
     */    
    public void testIvSelector() {
        System.out.println("testing IV selector....");
        DelimitedTextParser parser = new DelimitedTextParser();
        try {
            parser.setMissingValuePlaceholder("NA");
            parser.setDelimiter(",");
            parser.setColumnNames(true);   
            parser.addIgnoredColumn(0);//skip index
            parser.setResponseIndex(new NominalAttribute("Risk"), 10);
            
            // add abnormal detector during parser, feature index including ignored index and response
            parser.addDectector(1, new RangeAbnormalDetector(1, 100));//age
            
            //
            // parse data
            //
            InputStream file = GermanCreditRiskTest.class.getClassLoader().getResourceAsStream("data/csv/german_credit_data.csv");
            Attribute[] attributes = new Attribute[] {
              new NumericAttribute("Age"),
              new NominalAttribute("Sex"),
              new NominalAttribute("Job"),
              new NominalAttribute("Housing"),
              new NominalAttribute("Saving accounts"),
              new NominalAttribute("Checking account"),
              new NumericAttribute("Credit amount"),
              new NumericAttribute("Duration"),
              new NominalAttribute("Purpose")
            };
            AttributeDataset raw = parser.parse("germancredit", attributes, file);
            assertNotNull(raw);

            int[] yInt = new int[raw.size()];
            double[] y = new double[raw.size()];
            ResponseUtil.transformResponse(raw, yInt, y);
            //
            // binning -> woe -> model -> report, with following columns:
            //
            int goodLabel = 1;//the target bad customer
            Map<Integer, FeatureBinning> binnings = new HashMap<Integer, FeatureBinning>();
            binnings.put(0, new EqualFrequencyBinning<Double>(9));//age
            binnings.put(1, new EqualDistanceBinning<Integer>(2));//sex, shortcut for category binning
            binnings.put(2, new EqualDistanceBinning<Integer>(4));//job, shortcut for category binning            
            binnings.put(3, new EqualDistanceBinning<Integer>(3));//housing, shortcut for category binning
            binnings.put(4, new EqualFrequencyBinning<Double>(4));//saving account, shortcut for category binning
            binnings.put(5, new EqualFrequencyBinning<Double>(4));//checking account, shortcut for category binning
            binnings.put(6, new EqualDistanceBinning<Double>(10));//Credit amount
            binnings.put(7, new EqualDistanceBinning<Double>(5));//duration          
            binnings.put(8, new EqualFrequencyBinning<Double>(8));//purpose, shortcut for category binning
            
            double[] ivs = IvSelector.checkIv4Features(raw, binnings, goodLabel, new HashMap<Integer, FeatureWoe>(binnings.size()), yInt);
            assertTrue(ivs.length == binnings.size());  
            //
            // weak features TODO
            // 
//            assertTrue(ivs[3] < 0.1); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
