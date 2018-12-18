package io.scorecard4j.transformation.woe;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import io.scorecard4j.binning.FeatureBinning;
import io.scorecard4j.binning.bin.Bin;
import io.scorecard4j.binning.bin.CategoryBin;
import io.scorecard4j.binning.bin.NumericBin;

/**
 * Weight Of Evidence (WOE) and Information Value (IV) calculator
 * 
 * @author rayeaster
 *
 */
public class WoeIvCalculator {

    /**
     * Threshold for a bin to have meaningful good or bad ratio
     */
    private static final double ratioAlert = 1E-4;

    /**
     * Calculate WOE and IV for given feature binning and good labels
     * 
     * @param binning
     *            feature binning from {@link io.scorecard4j.binning.FeatureBinning}
     * @param goodLabel
     *            class label as good sample
     * @param numeric
     *            if this is a numeric label
     * @return WOE and IV calculator
     */
    public static FeatureWoe calculation(FeatureBinning binning, int goodLabel, boolean numeric) {
        if (numeric) {
            List<NumericBin> bins = binning.getNumericBins();
            return new FeatureWoe(bins, goodLabel);
        } else {
            List<CategoryBin> bins = binning.getCategoryBins();
            return new FeatureWoe(bins, goodLabel);
        }
    }

    /**
     * WOE and IV wrapper for a feature
     * 
     * @author lma
     *
     */
    public static class FeatureWoe {

        private List<? extends Bin> bins;
        private int[] goodCounts;
        private int[] badCounts;
        private int goodTotal;
        private int badTotal;

        private double[] woes;
        private double iv;

        /**
         * constructor
         * 
         * @param bins
         *            sorted binning from {@link io.scorecard4j.binning.FeatureBinning}
         *            to calculate the WOE
         * @param goodLabel
         *            class label as good sample
         */
        public FeatureWoe(List<? extends Bin> bins, int goodLabel) {
            this.bins = bins;
            goodCounts = new int[bins.size()];
            badCounts = new int[bins.size()];
            woes = new double[bins.size()];

            for (int i = 0; i < bins.size(); i++) {
                Bin bin = bins.get(i);
                Map<Integer, AtomicInteger> sampleClassCnts = bin.getSampleClassCounts();

                int gc = 0;
                int bc = 0;
                for (Entry<Integer, AtomicInteger> ent : sampleClassCnts.entrySet()) {
                    int label = ent.getKey();
                    int cnt = ent.getValue().intValue();
                    if (label == goodLabel) {
                        gc += cnt;
                    } else {
                        bc += cnt;
                    }
                }

                goodCounts[i] = gc;
                badCounts[i] = bc;
                goodTotal += gc;
                badTotal += bc;
            }

            iv = 0;
            for (int i = 0; i < bins.size(); i++) {
                double goodRatio = (double) goodCounts[i] / (double) goodTotal;
                double badRatio = (double) badCounts[i] / (double) badTotal;
                if (goodRatio <= ratioAlert) {
                    throw new RuntimeException("zero good ratio in the bin, try merge or re-binning: " + bins.get(i));
                }
                if (badRatio <= ratioAlert) {
                    throw new RuntimeException("zero bad ratio in the bin, try merge or re-binning: " + bins.get(i));
                }
                woes[i] = Math.log(goodRatio / badRatio);
                iv += (goodRatio - badRatio) * woes[i];
            }
        }

        public List<? extends Bin> bins() {
            return bins;
        }

        public double[] woe() {
            return woes;
        }

        public double iv() {
            return iv;
        }

    }
}
