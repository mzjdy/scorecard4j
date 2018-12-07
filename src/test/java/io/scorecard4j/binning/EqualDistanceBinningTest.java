package io.scorecard4j.binning;

import io.scorecard4j.binning.bin.Bin;
import io.scorecard4j.binning.bin.CategoryBin;
import io.scorecard4j.binning.bin.NumericBin;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * unit test for {@link EqualDistanceBinning}.
 */
public class EqualDistanceBinningTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public EqualDistanceBinningTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(EqualDistanceBinningTest.class);
    }

    /**
     * numeric data test
     */
    public void testNumeric() {
        Double[] values = new Double[] { 1.0, 2.0, 4.0, 10.0 };
        int[] clzz = new int[] { 0, 0, 1, 1 };
        int bins = 3;

        EqualDistanceBinning<Double> binning = new EqualDistanceBinning<Double>(bins);
        binning.findBinning(values, clzz, true);

        Bin bin = null;
        bin = binning.getBinning(1.5, true);
        assertTrue(((NumericBin) bin).getLow() == Double.NEGATIVE_INFINITY);
        assertTrue(((NumericBin) bin).getSampleCount() == 2);
        
        bin = binning.getBinning(5.5, true);
        assertTrue(((NumericBin) bin).getLow() == 4.0);
        assertTrue(((NumericBin) bin).getHigh() == 7.0);
        assertTrue(((NumericBin) bin).getSampleCount() == 1);

        bin = binning.getBinning(10.5, true);
        assertTrue(((NumericBin) bin).getHigh() == Double.POSITIVE_INFINITY);
        assertTrue(((NumericBin) bin).getSampleCount() == 1);
    }

    /**
     * category data test
     */
    public void testCategory() {
        Integer[] values = new Integer[] { 1, 2, 2, 3 };
        int[] clzz = new int[] { 0, 1, 1, 2 };
        int bins = 3;

        EqualDistanceBinning<Integer> binning = new EqualDistanceBinning<Integer>(bins);
        binning.findBinning(values, clzz, false);

        Bin bin = null;
        bin = binning.getBinning(1, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(0) == 1);

        bin = binning.getBinning(2, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(1) == 2);
        assertTrue(((CategoryBin) bin).getSampleCount() == 2);

        bin = binning.getBinning(3, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(2) == 1);
    }

    /**
     * category data test2
     */
    public void testCategory2() {
        Integer[] values = new Integer[] { 1, 2, 2, 3, 4, 4};
        int[] clzz = new int[] { 0, 1, 1, 2, 2, 2 };
        int bins = 2;

        EqualDistanceBinning<Integer> binning = new EqualDistanceBinning<Integer>(bins);
        binning.findBinning(values, clzz, false);

        Bin bin = null;
        bin = binning.getBinning(1, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(0) == 1);
        assertTrue(((CategoryBin) bin).getSampleCount() == 3);

        bin = binning.getBinning(2, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(1) == 2);
        assertTrue(((CategoryBin) bin).getSampleCount() == 3);

        bin = binning.getBinning(3, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(2) == 3);
        assertTrue(((CategoryBin) bin).getSampleCount() == 3);
        
        bin = binning.getBinning(4, false);
        assertTrue(((CategoryBin) bin).getSampleClassCounts(2) == 3);
        assertTrue(((CategoryBin) bin).getSampleCount() == 3);
    }
}
