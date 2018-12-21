package io.scorecard4j.util;

import smile.data.Attribute;
import smile.data.AttributeDataset;
import smile.data.Attribute.Type;

/**
 * 
 * @author rayeaster
 *
 */
public class ResponseUtil{
    
    public static void transformResponse(AttributeDataset raw, int[] yInt, double[] y) {
        Attribute ya = raw.responseAttribute();
        if(ya.getType() == Type.NOMINAL) {
            raw.toArray(yInt);
            for(int i = 0;i < yInt.length;i++) {
                y[i] = (double)yInt[i];
            }            
        }else if(ya.getType() == Type.NUMERIC) {
            y = raw.y();
            for(int i = 0;i < yInt.length;i++) {
                yInt[i] = (int)y[i];
            }            
        }else {
            throw new RuntimeException("wrong response type:" + ya.getType());
        }
    }
}