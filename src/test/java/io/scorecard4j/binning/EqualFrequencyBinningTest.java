package io.scorecard4j.binning;

import io.scorecard4j.binning.bin.Bin;
import io.scorecard4j.binning.bin.CategoryBin;
import io.scorecard4j.binning.bin.NumericBin;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * unit test for {@link EqualFrequencyBinning}.
 */
public class EqualFrequencyBinningTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public EqualFrequencyBinningTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(EqualFrequencyBinningTest.class);
    }

    /**
     * numeric data test
     */
    public void testNumeric() {
        Double[] values = new Double[] { 1.0, 2.0, 2.0, 3.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0 };
        int[] clzz = new int[] { 0, 1, 1, 2, 2, 2, 2, 2, 2, 2 };
        int bins = 3;

        EqualFrequencyBinning<Double> binning = new EqualFrequencyBinning<Double>(bins);
        binning.findBinning(values, clzz, true);

        Bin bin = null;
        bin = binning.getBinning(1.0, true);        
        assertTrue(((NumericBin) bin).getSampleClassCounts(0) == 1);
        assertTrue(((NumericBin) bin).getSampleCount() == 3);
        assertTrue(((NumericBin) bin).getLow() == Double.NEGATIVE_INFINITY);

        bin = binning.getBinning(4.0, true);
        assertTrue(((NumericBin) bin).getSampleClassCounts(0) == 0);
        assertTrue(((NumericBin) bin).getSampleClassCounts(1) == 0);
        assertTrue(((NumericBin) bin).getSampleCount() == 3);
        assertTrue(((NumericBin) bin).getLow() == 3.0);
        assertTrue(((NumericBin) bin).getHigh() == 6.0);

        bin = binning.getBinning(10.0, true);
        assertTrue(((NumericBin) bin).getSampleClassCounts(2) == 4);
        assertTrue(((NumericBin) bin).getSampleCount() == 4);
        assertTrue(((NumericBin) bin).getLow() == 6.0);
    }

    /**
     * category data test
     */
    public void testCategory() {
        Integer[] values = new Integer[] { 1, 2, 2, 3, 3, 3, 3, 3, 3, 3 };
        int[] clzz = new int[] { 0, 1, 1, 2, 2, 2, 2, 2, 2, 2 };
        int bins = 3;

        EqualFrequencyBinning<Integer> binning = new EqualFrequencyBinning<Integer>(bins);
        binning.findBinning(values, clzz, false);

        Bin bin = null;
        bin = binning.getBinning(1, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(0) == 1);
        assertTrue(((CategoryBin) bin).getSampleCount() == 1);

        bin = binning.getBinning(2, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(1) == 2);
        assertTrue(((CategoryBin) bin).getSampleCount() == 2);

        bin = binning.getBinning(3, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(2) == 7);
        assertTrue(((CategoryBin) bin).getSampleCount() == 7);
    }

    /**
     * category data test
     */
    public void testCategory2() {
        Integer[] values = new Integer[] { 1, 2, 2, 3, 3, 3, 3, 3, 3, 3 };
        int[] clzz = new int[] { 0, 1, 1, 2, 2, 2, 2, 2, 2, 2 };
        int bins = 2;

        EqualFrequencyBinning<Integer> binning = new EqualFrequencyBinning<Integer>(bins);
        binning.findBinning(values, clzz, false);

        Bin bin = null;
        bin = binning.getBinning(1, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(0) == 1);
        assertTrue(((CategoryBin) bin).getSampleCount() == 3);

        bin = binning.getBinning(2, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(1) == 2);
        assertTrue(((CategoryBin) bin).getSampleCount() == 3);

        bin = binning.getBinning(3, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(2) == 7);
        assertTrue(((CategoryBin) bin).getSampleCount() == 7);
    }
}
