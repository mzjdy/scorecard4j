package io.scorecard4j.binning;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import io.scorecard4j.binning.bin.Bin;
import io.scorecard4j.binning.bin.CategoryBin;
import io.scorecard4j.binning.bin.NumericBin;
import io.scorecard4j.transformation.woe.WoeIvCalculator;

/**
 * Abstract class for {@link FeatureBinning}
 * @author rayeaster
 *
 */
public abstract class AbstractBinning<T> implements FeatureBinning<T>{
    
    protected int bins = 0;
    protected List<NumericBin> numericBins = null;
    protected List<CategoryBin> categoryBins = null;
   
    @Override
    public boolean findBinning(T[] values, int[] clzz, int goodLabel, boolean numeric) {
        throw new RuntimeException("Not supporeted. Initialized via constructor");
    }

    @Override
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
    
    protected void finalizeBinning(T[] values, int[] clzz, int goodLabel, boolean numeric) {

        //
        // merge and update binning statistic
        //
        updateStatistic(values, clzz, numeric);
        checkBinValidity(numeric, goodLabel);
    }
    
    /**
     * ensure each bin has both good and bad samples
     * @param numeric if this for numeric attribute
     * @param goodLabel good sample class label
     */
    protected void checkBinValidity(boolean numeric, int goodLabel) {
        if(numeric) {
            int[] goodBadTotals = getGoodBadTotal(numericBins, goodLabel);
            int goodTotal = goodBadTotals[0];
            int badTotal = goodBadTotals[1];
            for(int i = 0;i < numericBins.size();i++) {
                NumericBin bin = numericBins.get(i); 
                if(!checkMeaningfulRatio(bin, goodLabel, goodTotal, badTotal)) {
                    NumericBin mergedBin = numericBins.remove(i == 0? i + 1 : i - 1); 
                    bin.merge(mergedBin);
                    i--;//ensure we check this bin again
                }
            }
            this.bins = numericBins.size();
        }else {
            int[] goodBadTotals = getGoodBadTotal(numericBins, goodLabel);
            int goodTotal = goodBadTotals[0];
            int badTotal = goodBadTotals[1];
            for(int i = 0;i < categoryBins.size();i++) {
                CategoryBin bin = categoryBins.get(i); 
                if(!checkMeaningfulRatio(bin, goodLabel, goodTotal, badTotal)) {
                    CategoryBin mergedBin = categoryBins.remove(i == 0? i + 1 : i - 1); 
                    bin.merge(mergedBin);
                    i--;//ensure we check this bin again
                }
            }
            this.bins = categoryBins.size();            
        }
        if(bins <= 1) {
            throw new RuntimeException("meaningless binning with only one bin...");
        }
    }
    
    protected int[] getGoodBadTotal(List<? extends Bin> bins, int goodLabel) {
        int goodTotal = 0;
        int badTotal = 0;
        
        for(Bin bin : bins) {
            Map<Integer, AtomicInteger> sampleCounts = bin.getSampleClassCounts();
            for(Entry<Integer, AtomicInteger> ent : sampleCounts.entrySet()) {
                Integer clz = ent.getKey();
                if(clz == goodLabel) {
                    goodTotal += ent.getValue().intValue();
                }else {
                    badTotal += ent.getValue().intValue();
                }
            } 
        }
        return new int[] {goodTotal, badTotal};
    }
    
    protected boolean checkMeaningfulRatio(Bin bin, int goodLabel, int goodTotal, int badTotal) {
        if((bin.getSampleClassCounts() == null || bin.getSampleClassCounts().size() < 2)) {
            return false;
        }
        Map<Integer, AtomicInteger> sampleCounts = bin.getSampleClassCounts();
        int goodCount = 0;
        int badCount = 0;
        for(Entry<Integer, AtomicInteger> ent : sampleCounts.entrySet()) {
            Integer clz = ent.getKey();
            if(clz == goodLabel) {
                goodCount += ent.getValue().intValue();
            }else {
                badCount += ent.getValue().intValue();
            }
        }
        boolean meaningful = WoeIvCalculator.validRatio(goodCount, goodTotal, badCount, badTotal);
        return meaningful;
    }
    
    protected void updateStatistic(T[] values, int[] clzz, boolean numeric) {
        //
        // update binning statistic
        //
        for (int i = 0; i < values.length; i++) {
            T t = values[i];
            Bin bin = getBinning(t, numeric);
            bin.addToBin();
            bin.addClassToBin(clzz[i]);
        }
    }

    @Override
    public List<NumericBin> getNumericBins() {
        return numericBins;  
    }

    @Override
    public List<CategoryBin> getCategoryBins() {
        return categoryBins;  
    }
}