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
import smile.data.AttributeDataset;
import smile.data.NumericAttribute;
import io.scorecard4j.dataset.DelimitedTextParser;
import io.scorecard4j.dataset.abnormal.detector.MissingAbnormalDetector;
import io.scorecard4j.dataset.abnormal.detector.RangeAbnormalDetector;
import io.scorecard4j.report.metrics.KSValue;
import io.scorecard4j.selection.iv.IvSelector;

/**
 * unit test for {@link Scorer} with dataset:
 * <a href="https://www.kaggle.com/c/GiveMeSomeCredit/data">GiveMeSomeCredit</a>
 */
public class GiveMeSomeCreditTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public GiveMeSomeCreditTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(GiveMeSomeCreditTest.class);
    }

    /**
     * <table>
     * <tr>
     * <th>Variable Name</th>
     * <th>Description</th>
     * <th>Type</th>
     * </tr>
     * <tr>
     * <td>SeriousDlqin2yrs</td>
     * <td>Person experienced 90 days past due delinquency or worse</td>
     * <td>Y/N</td>
     * <tr>
     * <tr>
     * <td>RevolvingUtilizationOfUnsecuredLines</td>
     * <td>Total balance on credit cards and personal lines of credit except real
     * estate and no installment debt like car loans divided by the sum of credit
     * limits</td>
     * <td>percentage</td>
     * <tr>
     * <tr>
     * <td>age</td>
     * <td>Age of borrower in years</td>
     * <td>integer</td>
     * <tr>
     * <tr>
     * <td>NumberOfTime30-59DaysPastDueNotWorse</td>
     * <td>Number of times borrower has been 30-59 days past due but no worse in the
     * last 2 years</td>
     * <td>integer</td>
     * <tr>
     * <tr>
     * <td>DebtRatio</td>
     * <td>Monthly debt payments, alimony,living costs divided by monthy gross
     * income</td>
     * <td>percentage</td>
     * <tr>
     * <tr>
     * <td>MonthlyIncome</td>
     * <td>Monthly income</td>
     * <td>real</td>
     * <tr>
     * <tr>
     * <td>NumberOfOpenCreditLinesAndLoans</td>
     * <td>Number of Open loans (installment like car loan or mortgage) and Lines of
     * credit (e.g. credit cards)</td>
     * <td>integer</td>
     * <tr>
     * <tr>
     * <td>NumberOfTimes90DaysLate</td>
     * <td>Number of times borrower has been 90 days or more past due</td>
     * <td>integer</td>
     * <tr>
     * <tr>
     * <td>NumberRealEstateLoansOrLines</td>
     * <td>Number of mortgage and real estate loans including home equity lines of
     * credit</td>
     * <td>integer</td>
     * <tr>
     * <tr>
     * <td>NumberOfTime60-89DaysPastDueNotWorse</td>
     * <td>Number of times borrower has been 60-89 days past due but no worse in the
     * last 2 years</td>
     * <td>integer</td>
     * <tr>
     * <tr>
     * <td>NumberOfDependents</td>
     * <td>Number of dependents in family excluding themselves (spouse, children
     * etc.)</td>
     * <td>integer</td>
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
            parser.setResponseIndex(new NumericAttribute("SeriousDlqin2yrs"), 1);
            
            // add abnormal detector during parser, feature index including ignored index and response
            parser.addDectector(3, new RangeAbnormalDetector(1, 100));//age
            parser.addDectector(4, new RangeAbnormalDetector(0, 80));//NumberOfTime30-59DaysPastDueNotWorse
            parser.addDectector(6, new MissingAbnormalDetector());//MonthlyIncome
            parser.addDectector(8, new RangeAbnormalDetector(0, 80));//NumberOfTimes90DaysLate
            parser.addDectector(10, new RangeAbnormalDetector(0, 80));//NumberOfTime60-89DaysPastDueNotWorse
            
            //
            // parse data
            //
            InputStream file = GiveMeSomeCreditTest.class.getClassLoader().getResourceAsStream("data/csv/givemesomecredit.csv");
            AttributeDataset raw = parser.parse("givemesomecredit", file);
            assertNotNull(raw);

            int[] yInt = new int[raw.size()];
            double[] y = new double[raw.size()];
            ResponseUtil.transformResponse(raw, yInt, y);
            //
            // binning -> woe -> model -> report, with following columns:
            // RevolvingUtilizationOfUnsecuredLines, age, NumberOfTime30-59DaysPastDueNotWorse, NumberOfTimes90DaysLate, NumberOfTime60-89DaysPastDueNotWorse
            //
            int goodLabel = 1;//the target bad customer
            Map<Integer, FeatureBinning> binnings = new HashMap<Integer, FeatureBinning>();
            binnings.put(0, new EqualFrequencyBinning<Double>(4));//RevolvingUtilizationOfUnsecuredLines
            binnings.put(1, new EqualFrequencyBinning<Double>(9));//age
            binnings.put(2, new EqualDistanceBinning<Double>(5));//NumberOfTime30-59DaysPastDueNotWorse 
            
            binnings.put(3, new EqualFrequencyBinning<Double>(5));//DebtRatio
            binnings.put(4, new EqualFrequencyBinning<Double>(5));//MonthlyIncome
            binnings.put(5, ProvidedBinning.numericBuilder(new double[] {0, 1, 3, 5}, raw.column(5).vector(), yInt, goodLabel));//NumberOfOpenCreditLinesAndLoans
            
            binnings.put(6, ProvidedBinning.numericBuilder(new double[] {0, 1, 3, 5}, raw.column(6).vector(), yInt, goodLabel));//NumberOfTimes90DaysLate

            binnings.put(7, ProvidedBinning.numericBuilder(new double[] {0, 1, 3, 5}, raw.column(7).vector(), yInt, goodLabel));//NumberRealEstateLoansOrLines
            
            binnings.put(8, ProvidedBinning.numericBuilder(new double[] {0, 1, 3}, raw.column(8).vector(), yInt, goodLabel));//NumberOfTime60-89DaysPastDueNotWorse

            binnings.put(9, ProvidedBinning.numericBuilder(new double[] {0, 1, 3, 5}, raw.column(9).vector(), yInt, goodLabel));//NumberOfDependents
            
            double[] ivs = IvSelector.checkIv4Features(raw, binnings, goodLabel, new HashMap<Integer, FeatureWoe>(binnings.size()), yInt);
            assertTrue(ivs.length == binnings.size());  
            //
            // weak features
            // 
            assertTrue(ivs[3] < 0.1); 
            assertTrue(ivs[4] < 0.1); 
            assertTrue(ivs[5] < 0.1); 
            assertTrue(ivs[7] < 0.1); 
            assertTrue(ivs[9] < 0.1);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    

    public void testScorer() {
        System.out.println("testing Scorer....");
        DelimitedTextParser parser = new DelimitedTextParser();
        try {
            parser.setMissingValuePlaceholder("NA");
            parser.setDelimiter(",");
            parser.setColumnNames(true);   
            parser.addIgnoredColumn(0);//skip index
            parser.setResponseIndex(new NumericAttribute("SeriousDlqin2yrs"), 1);

            //
            // skip following columns:
            // DebtRatio, MonthlyIncome, NumberOfOpenCreditLinesAndLoans, NumberRealEstateLoansOrLines, NumberOfDependents
            // 
            parser.addIgnoredColumn(5);
            parser.addIgnoredColumn(6);
            parser.addIgnoredColumn(7);
            parser.addIgnoredColumn(9);
            parser.addIgnoredColumn(11); 
            
            // add abnormal detector during parser
            parser.addDectector(3, new RangeAbnormalDetector(1, 100));//age
            parser.addDectector(4, new RangeAbnormalDetector(0, 80));//NumberOfTime30-59DaysPastDueNotWorse
            parser.addDectector(8, new RangeAbnormalDetector(0, 80));//NumberOfTimes90DaysLate
            parser.addDectector(10, new RangeAbnormalDetector(0, 80));//NumberOfTime60-89DaysPastDueNotWorse
            
            //
            // parse data
            //
            InputStream file = GiveMeSomeCreditTest.class.getClassLoader().getResourceAsStream("data/csv/givemesomecredit.csv");
            AttributeDataset raw = parser.parse("givemesomecredit", file);
            assertNotNull(raw);

            int[] yInt = new int[raw.size()];
            double[] y = new double[raw.size()];
            ResponseUtil.transformResponse(raw, yInt, y);
            //
            // binning -> woe -> model -> report, with following columns:
            // RevolvingUtilizationOfUnsecuredLines, age, NumberOfTime30-59DaysPastDueNotWorse, NumberOfTimes90DaysLate, NumberOfTime60-89DaysPastDueNotWorse
            //
            
            int goodLabel = 1;//the target bad customer
            Map<Integer, FeatureBinning> binnings = new HashMap<Integer, FeatureBinning>();
            binnings.put(0, new EqualFrequencyBinning<Double>(4));
            binnings.put(1, new EqualFrequencyBinning<Double>(9));
            binnings.put(2, new EqualDistanceBinning<Double>(5));            
            binnings.put(3, ProvidedBinning.numericBuilder(new double[] {0, 1, 3, 5}, raw.column(3).vector(), yInt, goodLabel));              
            binnings.put(4, ProvidedBinning.numericBuilder(new double[] {0, 1, 3}, raw.column(4).vector(), yInt, goodLabel));
            
            LRModeler modeler = new LRModeler(raw, binnings, goodLabel);
            modeler.predict(raw, goodLabel);

            Scorer scorer = new Scorer(600, 20, 20, modeler);
            System.out.println(scorer.toString());
            
            double ks = KSValue.ks(raw.x(), yInt, scorer, goodLabel, raw.attributes());
            System.out.println("Kolmogorov-Smirnov chart measure=" + NumberFormatUtil.formatTo2DigitsAfterDecimal(ks));            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
