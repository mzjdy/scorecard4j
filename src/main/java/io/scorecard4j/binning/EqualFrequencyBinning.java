package io.scorecard4j.binning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import io.scorecard4j.binning.bin.Bin;
import io.scorecard4j.binning.bin.CategoryBin;
import io.scorecard4j.binning.bin.NumericBin;
import smile.sort.QuickSort;

/**
 * Another naive binning to divide the feature to make each bin containing
 * equal-frequency of sample data.
 * 
 * @author rayeaster
 *
 */
public class EqualFrequencyBinning<T> implements FeatureBinning<T> {

    private int bins = 0;
    private List<NumericBin> numericBins = null;
    private List<CategoryBin> categoryBins = null;

    /**
     * constructor
     * 
     * @param bins
     *            number of target bins
     */
    public EqualFrequencyBinning(int bins) {
        this.bins = bins;
    }

    public boolean findBinning(T[] values, int[] clzz, boolean numeric) {
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
                int binSize = values.length / bins;
                int completeBin = 0;
                double[] unsortedValues = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null && values[i] instanceof Double) {
                        unsortedValues[i] = (double) values[i];
                    } else {
                        throw new IllegalArgumentException("unsupported numeric value:" + values[i]);
                    }
                }
                int[] sortedIndex = QuickSort.sort(unsortedValues);
                Double min = null;
                Double max = null;
                int curBinSize = 0;
                for (int i = 0; i < sortedIndex.length; i++) {
                    min = min == null ? unsortedValues[i] : min;
                    max = unsortedValues[i];
                    curBinSize++;
                    if (completeBin == bins - 1) {
                        numericBins.add(new NumericBin((Double) min, Double.POSITIVE_INFINITY));
                        break;
                    } else if (curBinSize >= binSize) {
                        if (completeBin == 0) {
                            numericBins.add(new NumericBin(Double.NEGATIVE_INFINITY, (Double) unsortedValues[i + 1]));
                        }else {
                            numericBins.add(new NumericBin((Double) min, (Double) unsortedValues[i + 1]));                            
                        }
                        completeBin++;
                        min = null;
                        curBinSize = 0;
                    }
                }
            }
        } else {
            categoryBins = new ArrayList<CategoryBin>();
            Map<Integer, AtomicInteger> categoriesCount = new HashMap<Integer, AtomicInteger>();
            for (T t : values) {
                if (t != null && t instanceof Integer) {
                    AtomicInteger count = categoriesCount.get(t);
                    if (count == null) {
                        count = new AtomicInteger();
                        categoriesCount.put((Integer) t, count);
                    }
                    count.incrementAndGet();
                } else {
                    throw new IllegalArgumentException("unsupported categorical value:" + t);
                }
            }
            int categorySize = categoriesCount.size();
            if (categorySize <= bins) {
                for (Entry<Integer, AtomicInteger> catEnt : categoriesCount.entrySet()) {
                    Integer cat = catEnt.getKey();
                    categoryBins.add(new CategoryBin(new int[] { cat }));
                }
            } else {
                int binSize = values.length / bins;
                int[] categoryCounts = new int[categorySize];
                List<Entry<Integer, AtomicInteger>> unsortedCategories = new ArrayList<Entry<Integer, AtomicInteger>>(
                        categorySize);

                int idx = 0;
                for (Entry<Integer, AtomicInteger> catEnt : categoriesCount.entrySet()) {
                    categoryCounts[idx++] = catEnt.getValue().intValue();
                    unsortedCategories.add(catEnt);
                }

                int[] sortedCategoryIndex = QuickSort.sort(categoryCounts);
                List<Entry<Integer, AtomicInteger>> sortedCategories = new ArrayList<Entry<Integer, AtomicInteger>>(
                        categorySize);
                for (int i : sortedCategoryIndex) {
                    sortedCategories.add(unsortedCategories.get(i));
                }

                List<Integer> cats = new ArrayList<Integer>();
                int curBinSize = 0;
                int binnedCandidates = 0;
                int completeBin = 0;
                for (int i = sortedCategories.size() - 1; i >= 0; i--) {
                    Entry<Integer, AtomicInteger> catEnt = sortedCategories.get(i);
                    cats.add(catEnt.getKey());

                    curBinSize += catEnt.getValue().intValue();
                    binnedCandidates += cats.size();
                    if (curBinSize >= binSize) {
                        categoryBins.add(new CategoryBin(cats));
                        cats = new ArrayList<Integer>();
                        curBinSize = 0;
                        completeBin++;
                    }
                    int restBin = bins - completeBin;
                    if (categorySize - binnedCandidates == restBin) {
                        if(restBin == 1) {
                            for (int j = i - 1; j >= 0; j--) {
                                Entry<Integer, AtomicInteger> catEntRest = sortedCategories.get(j);
                                cats.add(catEntRest.getKey());
                            }
                            categoryBins.add(new CategoryBin(cats));                            
                        }else {
                            categoryBins.add(new CategoryBin(cats)); 
                            for (int j = i - 1; j >= 0; j--) {
                                Entry<Integer, AtomicInteger> catEntRest = sortedCategories.get(j);
                                categoryBins.add(new CategoryBin(new int[] { catEntRest.getKey() }));
                            }                            
                        }
                        break;
                    }
                }
            }
        }
        //
        // update binning statistic
        //
        for (int i = 0; i < values.length; i++) {
            T t = values[i];
            Bin bin = getBinning(t, numeric);
            bin.addToBin();
            bin.addClassToBin(clzz[i]);
        }

        return true;
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