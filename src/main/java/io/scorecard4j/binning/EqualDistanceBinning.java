package io.scorecard4j.binning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.scorecard4j.binning.bin.CategoryBin;
import io.scorecard4j.binning.bin.NumericBin;

/**
 * Most naive binning to divide the feature range into equal-spaced interval.
 * But note this binning method is easily subject to abnormal values.
 * 
 * @author rayeaster
 *
 */
public class EqualDistanceBinning<T> extends AbstractBinning<T> implements FeatureBinning<T> {

    /**
     * constructor
     * 
     * @param bins
     *            number of target bins
     */
    public EqualDistanceBinning(int bins) {
        this.bins = bins;
    }

    @Override
    public boolean findBinning(T[] values, int[] clzz, int goodLabel, boolean numeric) {
        //
        // generate appropriate bins
        //
        if (numeric) {
            numericBins = new ArrayList<NumericBin>();
            if (values.length <= bins) {
                for (int i = 0; i < values.length; i++) {
                    T t = values[i];
                    if (t != null && t instanceof Double) {
                        if (i == 0) {
                            numericBins.add(new NumericBin(Double.NEGATIVE_INFINITY, (Double) t));
                        } else if (i == values.length - 1) {
                            numericBins.add(new NumericBin((Double) t, Double.POSITIVE_INFINITY));
                        } else {
                            numericBins.add(new NumericBin((Double) t, (Double) values[i + 1]));
                        }
                    } else {
                        throw new IllegalArgumentException("unsupported numeric value:" + t);
                    }
                }
            } else {
                Double min = Double.POSITIVE_INFINITY;
                Double max = Double.NEGATIVE_INFINITY;
                for (T t : values) {
                    if (t != null && t instanceof Double) {
                        if (((Double) t) > max) {
                            max = (Double) t;
                        }
                        if (((Double) t) < min) {
                            min = (Double) t;
                        }
                    } else {
                        throw new IllegalArgumentException("unsupported numeric value:" + t);
                    }
                }
                double step = (max - min) / bins;
                for (int i = 0; i < bins; i++) {
                    if (i == 0) {
                        numericBins.add(new NumericBin(Double.NEGATIVE_INFINITY, min + step));
                    } else if (i == bins - 1) {
                        numericBins.add(new NumericBin(max - step, Double.POSITIVE_INFINITY));
                    } else {
                        numericBins.add(new NumericBin(min + i * step, min + (i + 1) * step));
                    }
                }
            }
        } else {
            categoryBins = new ArrayList<CategoryBin>();
            Set<Integer> categories = new HashSet<Integer>();
            for (T t : values) {
                if (t != null && t instanceof Integer) {
                    if (!categories.contains(t)) {
                        categories.add((Integer) t);
                    }
                } else {
                    throw new IllegalArgumentException("unsupported categorical value:" + t);
                }
            }

            if (categories.size() <= bins) {
                for (Integer cat : categories) {
                    categoryBins.add(new CategoryBin(new int[] { cat }));
                }
            } else {
                int binSize = categories.size() / bins;
                int binSizeMod = categories.size() % bins;

                Iterator<Integer> iter = categories.iterator();
                List<Integer> cats = new ArrayList<Integer>();

                int curBinSize = 0;
                for (int i = 0, j = 0; i < categories.size(); i++) {
                    Integer cat = iter.next();
                    curBinSize++;
                    cats.add(cat);

                    if (curBinSize == binSize) {
                        if (j < binSizeMod) {
                            cats.add(iter.next());
                            i++;
                            j++;
                        }
                        categoryBins.add(new CategoryBin(cats));
                        cats = new ArrayList<Integer>();
                        curBinSize = 0;
                    } 
                }
            }
        }
        
        finalizeBinning(values, clzz, goodLabel, numeric);

        return true;
    }

}