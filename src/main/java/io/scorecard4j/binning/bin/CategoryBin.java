package io.scorecard4j.binning.bin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Category feature bin 
 * 
 * @author rayeaster
 *
 */
public class CategoryBin extends Bin{
    
    /**
     * categories for this bin
     */
    Set<Integer> categories;

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
    }

    /**
     * constructor
     * @param categories of the feature to be added
     */
    public CategoryBin(List<Integer> categories) {
        super();
        this.categories = new HashSet<Integer>();
        this.categories.addAll(categories);
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
        return "categoryBin[" + categories.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")) + "]";
    }

}