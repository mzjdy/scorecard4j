package io.scorecard4j.binning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.scorecard4j.binning.bin.CategoryBin;
import io.scorecard4j.binning.bin.NumericBin;
import smile.sort.QuickSort;

/**
 * Binning according to given criterion.
 * @author rayeaster
 *
 */
public class ProvidedBinning<T> extends AbstractBinning<T> implements FeatureBinning<T>{
    

    /**
     * constructor for category value binning
     * 
     * @param catBins
     *            provided category bins
     * @param clzz
     *            provided class labels of samples
     * @param goodLabel
     *            good sample class label
     */
    public ProvidedBinning(int[][] catBins, int[][] clzz, int goodLabel) {
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
        checkBinValidity(false, goodLabel);
    }
    
    /**
     * constructor for category value binning
     * 
     * @param catBins
     *            provided category bins
     * @param clzz
     *            provided class labels of samples
     * @param goodLabel
     *            good sample class label
     */
    public ProvidedBinning(int[][] catBins, List<List<Integer>> clzz, int goodLabel) {
        this.bins = catBins.length;
        categoryBins = new ArrayList<CategoryBin>(this.bins);
        for (int i = 0; i < catBins.length; i++) {
            int[] cats = catBins[i];
            CategoryBin bin = new CategoryBin(cats);
            List<Integer> clz = clzz.get(i);
            for (int j = 0; j < clz.size(); j++) {
                bin.addToBin();
                bin.addClassToBin(clz.get(j));
            }
            categoryBins.add(bin);
        }
        checkBinValidity(false, goodLabel);
    }

    /**
     * constructor for numeric value binning
     * 
     * @param numSplits
     *            provided numerical splits
     * @param clzz
     *            provided class labels of samples
     * @param goodLabel
     *            good sample class label
     */
    public ProvidedBinning(double[] numSplits, int[][] clzz, int goodLabel) {
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
        checkBinValidity(true, goodLabel);
    }

    /**
     * constructor for numeric value binning
     * 
     * @param numSplits
     *            provided numerical splits
     * @param clzz
     *            provided class labels of samples
     * @param goodLabel
     *            good sample class label
     */
    public ProvidedBinning(double[] numSplits, List<List<Integer>> clzz, int goodLabel) {
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
            List<Integer> clz = clzz.get(i);
            for (int j = 0; j < clz.size(); j++) {
                bin.addToBin();
                bin.addClassToBin(clz.get(j));
            }
            numericBins.add(bin);
        }
        checkBinValidity(true, goodLabel);
    }
    
    /**
     * Builder for {@link ProvidedBinning} with numeric feature
     * 
     * @param numSplits
     *            provided numerical splits
     * @param vals
     *            feature values
     * @param y
     *            provided class labels of samples
     * @param goodLabel
     *            good sample class label
     *            
     * @return {@link ProvidedBinning} instance built from given parameters
     */
    public static ProvidedBinning<Double> numericBuilder(double[] numSplits, double[] vals, int[] y, int goodLabel) {  
        int[] sortedIdx = QuickSort.sort(numSplits);
        
        List<List<Integer>> clzz = new ArrayList<List<Integer>>(numSplits.length + 1);
        for(int j = 0;j <= numSplits.length;j++) {
            clzz.add(new ArrayList<Integer>());
        }
        
        for(int i = 0;i < vals.length;i++) {
            double val = vals[i];
            int j = 0;
            for(;j <= sortedIdx.length;j++) {
                if(j == sortedIdx.length || val < numSplits[sortedIdx[j]]) {
                    clzz.get(j).add(y[i]);
                    break;
                }
            }
        }        
        
        return new ProvidedBinning<Double>(numSplits, clzz, goodLabel);
    }
    
    /**
     * Builder for {@link ProvidedBinning} with numeric feature
     * 
     * @param catBins
     *            provided category bins
     * @param vals
     *            feature values
     * @param y
     *            provided class labels of samples
     * @param goodLabel
     *            good sample class label
     *            
     * @return {@link ProvidedBinning} instance built from given parameters
     */
    public static ProvidedBinning<Integer> cateoryBuilder(int[][] catBins, int[] vals, int[] y, int goodLabel) {        
        List<List<Integer>> clzz = new ArrayList<List<Integer>>(catBins.length);
        List<Set<Integer>> catBinSets = new ArrayList<Set<Integer>>(catBins.length);
        for(int j = 0;j <= catBins.length;j++) {
            clzz.add(new ArrayList<Integer>());
            Set<Integer> binSet = new HashSet<Integer>();
            for(int binCat : catBins[j]) {
                binSet.add(binCat);
            }
            catBinSets.add(binSet);            
        }
        
        for(int i = 0;i < vals.length;i++) {
            int val = vals[i];
            for(int j = 0;j < catBins.length;j++) {
                if(catBinSets.get(j).contains(val)) {
                    clzz.get(j).add(y[i]);
                    break;
                }
            }
        }        
        
        return new ProvidedBinning<Integer>(catBins, clzz, goodLabel);
    }
    
}