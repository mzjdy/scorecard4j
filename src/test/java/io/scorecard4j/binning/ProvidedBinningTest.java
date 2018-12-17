package io.scorecard4j.binning;

import io.scorecard4j.binning.bin.Bin;
import io.scorecard4j.binning.bin.CategoryBin;
import io.scorecard4j.binning.bin.NumericBin;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * unit test for {@link ProvidedBinning}.
 */
public class ProvidedBinningTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public ProvidedBinningTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ProvidedBinningTest.class);
    }

    /**
     * numeric data test
     */
    public void testNumeric() {

        ProvidedBinning<Double> binning = new ProvidedBinning<Double>(new double[] {4.0, 7.0, 10.0});

        Bin bin = null;
        bin = binning.getBinning(1.5, true);
        assertTrue(((NumericBin) bin).getLow() == Double.NEGATIVE_INFINITY);
        
        bin = binning.getBinning(5.5, true);
        assertTrue(((NumericBin) bin).getLow() == 4.0);
        assertTrue(((NumericBin) bin).getHigh() == 7.0);

        bin = binning.getBinning(10.5, true);
        assertTrue(((NumericBin) bin).getHigh() == Double.POSITIVE_INFINITY);
    }

    /**
     * category data test
     */
    public void testCategory() {
        ProvidedBinning<Integer> binning = new ProvidedBinning<Integer>(new int[][] {{1}, {2}, {3}});

        Bin bin = null;
        bin = binning.getBinning(1, false);
        assertTrue(((CategoryBin) bin).getCategories().contains(1));

        bin = binning.getBinning(2, false);
        assertTrue(((CategoryBin) bin).getCategories().contains(2));

        bin = binning.getBinning(3, false);
        assertTrue(((CategoryBin) bin).getCategories().contains(3));
    }

}
