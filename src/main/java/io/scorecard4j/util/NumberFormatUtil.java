package io.scorecard4j.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 
 * @author rayeaster
 *
 */
public class NumberFormatUtil{

    private static NumberFormat formatter = new DecimalFormat("#0.00");  
    
    /**
     * format double value to have 2 digits after decimal point.
     * @param val double value to be formatted
     * @return double value after formatted
     */
    public static String formatTo2DigitsAfterDecimal(double val) {   
        return formatter.format(val); 
    }
}