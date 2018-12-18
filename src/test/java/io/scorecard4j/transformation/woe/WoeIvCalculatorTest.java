package io.scorecard4j.transformation.woe;

import java.util.List;

import io.scorecard4j.binning.ProvidedBinning;
import io.scorecard4j.binning.bin.Bin;
import io.scorecard4j.binning.bin.CategoryBin;
import io.scorecard4j.binning.bin.NumericBin;
import io.scorecard4j.transformation.woe.WoeIvCalculator.FeatureWoe;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * unit test for {@link WoeIvCalculator}.
 */
public class WoeIvCalculatorTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public WoeIvCalculatorTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(WoeIvCalculatorTest.class);
    }

    /**
     * numeric data test
     */
    public void testNumeric() {

        ProvidedBinning<Double> binning = new ProvidedBinning<Double>(new double[] {4.0, 7.0, 10.0}, new int[][] {{0, 1}, {0, 1}, {0, 1, 1}, {0, 1, 1}});

        FeatureWoe fw = WoeIvCalculator.calculation(binning, 1, true);         
        List<? extends Bin> bins = fw.bins();
        assertTrue(bins != null && bins.size() == 4);
        assertTrue(bins.get(0) instanceof NumericBin);
        
        double[] woes = fw.woe();
        assertTrue(woes != null && woes.length == 4);
        assertTrue(Math.abs(Math.abs(woes[0]) - 0.4) < 0.01);
        assertTrue(Math.abs(Math.abs(woes[1]) - 0.4) < 0.01);
        assertTrue(Math.abs(Math.abs(woes[2]) - 0.28) < 0.01);
        assertTrue(Math.abs(Math.abs(woes[3]) - 0.28) < 0.01);
        
        double iv = fw.iv();
        assertTrue(Math.abs(Math.abs(iv) - 0.12) < 0.01);        
    }

    /**
     * category data test
     */
    public void testCategory() {
        ProvidedBinning<Integer> binning = new ProvidedBinning<Integer>(new int[][] {{1}, {2}, {3}}, new int[][] {{0, 1}, {0, 1}, {0, 1, 1}});

        FeatureWoe fw = WoeIvCalculator.calculation(binning, 1, false);         
        List<? extends Bin> bins = fw.bins();
        assertTrue(bins != null && bins.size() == 3);
        assertTrue(bins.get(0) instanceof CategoryBin);
        
        double[] woes = fw.woe();
        assertTrue(woes != null && woes.length == 3);
        assertTrue(Math.abs(Math.abs(woes[0]) - 0.28) < 0.01);
        assertTrue(Math.abs(Math.abs(woes[1]) - 0.28) < 0.01);
        assertTrue(Math.abs(Math.abs(woes[2]) - 0.4) < 0.01);
        
        double iv = fw.iv();
        assertTrue(Math.abs(Math.abs(iv) - 0.12) < 0.01);     
    }

}
