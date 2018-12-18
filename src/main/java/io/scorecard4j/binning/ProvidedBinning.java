package io.scorecard4j.binning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.scorecard4j.binning.bin.Bin;
import io.scorecard4j.binning.bin.CategoryBin;
import io.scorecard4j.binning.bin.NumericBin;
import smile.sort.QuickSort;

/**
 * Binning according to given criterion.
 * @author rayeaster
 *
 */
public class ProvidedBinning<T> implements FeatureBinning<T>{
    
    private int bins = 0;
    private List<NumericBin> numericBins = null;
    private List<CategoryBin> categoryBins = null;

    /**
     * constructor for category value binning
     * 
     * @param catBins
     *            provided category bins
     * @param clzz
     *            provided class labels of samples
     */
    public ProvidedBinning(int[][] catBins, int[][] clzz) {
        this.bins = catBins.length;
        categoryBins = new ArrayList<CategoryBin>(this.bins);
        for (int i = 0; i < catBins.length; i++) {
            int[] cats = catBins[i];
            CategoryBin bin = new CategoryBin(cats);
            int[] clz = clzz[i];
            for (int j = 0; j < clz.length; j++) {
                bin.addToBin();
                bin.addClassToBin(clz[j]);
            }
            categoryBins.add(bin);
        }
    }

    /**
     * constructor for numeric value binning
     * 
     * @param numSplits
     *            provided numerical splits
     * @param clzz
     *            provided class labels of samples
     */
    public ProvidedBinning(double[] numSplits, int[][] clzz) {
        this.bins = numSplits.length + 1;
        numericBins = new ArrayList<NumericBin>(this.bins);
        int[] sortedIdx = QuickSort.sort(numSplits);
        for (int i = 0; i <= sortedIdx.length; i++) {
            double split = numSplits[sortedIdx[i == sortedIdx.length? i - 1 : i]];
            NumericBin bin = null;
            if (i == 0) {
                bin = new NumericBin(Double.NEGATIVE_INFINITY, (Double) split);
            } else if (i == sortedIdx.length) {
                bin = new NumericBin((Double) split, Double.POSITIVE_INFINITY);
            } else {
                bin = new NumericBin((Double) numSplits[sortedIdx[i - 1]], (Double) split);
            }
            int[] clz = clzz[i];
            for (int j = 0; j < clz.length; j++) {
                bin.addToBin();
                bin.addClassToBin(clz[j]);
            }
            numericBins.add(bin);
        }
    }
    
    public boolean findBinning(T[] values, int[] clzz, boolean numeric) {
        throw new RuntimeException("ProvidedBinning is initialized via constructor");
    }

    public Bin getBinning(T value, boolean numeric) {
        if (numeric) {
            if (value != null && value instanceof Double) {
                for (NumericBin bin : numericBins) {
                    if (bin.inThisBin((Double) value)) {
                        return bin;
                    }
                }
            }
            throw new IllegalArgumentException("unsupported numeric value:" + value);
        } else {
            if (value != null && value instanceof Integer) {
                for (CategoryBin bin : categoryBins) {
                    if (bin.inThisBin((Integer) value)) {
                        return bin;
                    }
                }
            } 
            throw new IllegalArgumentException("unsupported categorical value:" + value);
        }
    }
    
}