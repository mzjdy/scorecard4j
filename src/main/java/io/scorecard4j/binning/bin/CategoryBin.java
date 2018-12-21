package io.scorecard4j.binning.bin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Category feature bin 
 * 
 * @author rayeaster
 *
 */
public class CategoryBin extends Bin{
    
    private String s;
    
    /**
     * categories for this bin
     */
    private Set<Integer> categories;

    /**
     * constructor
     * @param categories of the feature to be added
     */
    public CategoryBin(int[] categories) {
        super();
        this.categories = new HashSet<Integer>();
        for(int cat : categories) {
            this.categories.add(cat);            
        }
        s = "categoryBin[" + this.categories.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")) + "]";
    }

    /**
     * constructor
     * @param categories of the feature to be added
     */
    public CategoryBin(List<Integer> categories) {
        super();
        this.categories = new HashSet<Integer>();
        this.categories.addAll(categories);
        this.s = "categoryBin[" + this.categories.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")) + "]";
    }
    
    /**
     * check if given value in this bin
     * @param value to be check
     * @return if given value in this bin
     */
    public boolean inThisBin(int value) {
        return this.categories.contains(value);
    }

    /**
     * Get categories for this bin
     * @return categories for this bin
     */
    public Set<Integer> getCategories() {
        return categories;
    }
    
    @Override
    public String toString() {
        return s;
    }
    
    /**
     * merge with another {@link CategoryBin}
     * @param bin {@link CategoryBin} to be merged
     * @return {@link CategoryBin} after merge
     */
    public CategoryBin merge(CategoryBin bin) {
        this.categories.addAll(bin.getCategories());
        
        this.sampleCount += bin.sampleCount;
        for(Entry<Integer, AtomicInteger> ent : bin.sampleClassCounts.entrySet()) {
            Integer key = ent.getKey();
            if(this.sampleClassCounts.containsKey(key)) {
                this.sampleClassCounts.get(key).addAndGet(ent.getValue().intValue());
            }else {
                this.sampleClassCounts.put(key, ent.getValue()); 
            }
        }
        
        this.s = "categoryBin[" + this.categories.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")) + "]";
        return this;
    }

}