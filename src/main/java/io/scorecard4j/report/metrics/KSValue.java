package io.scorecard4j.report.metrics;

import io.scorecard4j.report.Scorer;
import smile.data.Attribute;
import smile.sort.QuickSort;

/**
 * Kolmogorov-Smirnov chart measures performance of classification models. More
 * accurately, K-S is a measure of the degree of separation between the positive
 * and negative distributions.
 * 
 * @author rayeaster
 *
 */
public class KSValue {

    /** we split the score into equal-distanced ranges */
    private static final int scoreIntervals = 10;

    /**
     * Calculate the Kolmogorov-Smirnov value for given data
     * 
     * @param raw
     *            original datums
     * @param truth
     *            original data labels
     * @param scorer
     *            {@link Scorer} scorer for given data
     * @param goodLabel
     *            class label as good sample
     * @param rawFeatures
     *            feature metadata
     * 
     * @return Kolmogorov-Smirnov value for given data
     */
    public static double ks(double[][] raw, int[] truth, Scorer scorer, int goodLabel, Attribute[] rawFeatures) {
        double ks = Double.MIN_VALUE;

        int goodTotalCnt = 0;
        int badTotalCnt = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double[] scoreSplitPoints = new double[scoreIntervals - 1];
        int[] goodCnts = new int[scoreIntervals];
        int[] badCnts = new int[scoreIntervals];
        double[] goodCumulatives = new double[scoreIntervals];
        double[] badCumulatives = new double[scoreIntervals];
        double[] scores = new double[raw.length];

        for (int i = 0; i < raw.length; i++) {
            if (truth[i] == goodLabel) {
                goodTotalCnt++;
            } else {
                badTotalCnt++;
            }

            scores[i] = scorer.score(raw[i], rawFeatures);
            if (scores[i] > max) {
                max = scores[i];
            }
            if (scores[i] < min) {
                min = scores[i];
            }
        }

        double interval = (max - min) / scoreIntervals;
        for (int i = 0; i < scoreIntervals - 1; i++) {
            scoreSplitPoints[i] = min + (i + 1) * interval;
        }

        int[] sortedIndexes = QuickSort.sort(scores);
        for (int i = 0; i < sortedIndexes.length; i++) {
            double score = scores[i];
            int y = truth[sortedIndexes[i]];
            if (score >= scoreSplitPoints[scoreSplitPoints.length - 1]) {
                if (y == goodLabel) {
                    goodCnts[scoreIntervals - 1]++;
                } else {
                    badCnts[scoreIntervals - 1]++;
                }
            }else {
                for (int j = 0;j < scoreSplitPoints.length;j++) {
                    if (score < scoreSplitPoints[j]) {
                        if (y == goodLabel) {
                            goodCnts[j]++;
                        } else {
                            badCnts[j]++;
                        }
                        break;
                    }
                }                
            }
        }

        for (int i = 0; i < goodCnts.length; i++) {
            int goodSum = 0;
            int badSum = 0;
            for (int j = 0; j <= i; j++) {
                goodSum += goodCnts[j];
                badSum += badCnts[j];
            }
            goodCumulatives[i] = (double)goodSum / (double)goodTotalCnt;
            badCumulatives[i] = (double)badSum / (double)badTotalCnt;
            double diff = Math.abs(goodCumulatives[i] - badCumulatives[i]);
            if (diff > ks) {
                ks = diff;
            }
        }

        return ks;
    }
}