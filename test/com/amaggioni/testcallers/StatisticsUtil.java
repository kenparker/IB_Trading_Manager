package com.amaggioni.testcallers;

import com.amaggioni.IBordermanagement.TradesCSV;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 03.01.2013
 * change date : 11.03.2013
 *
 * description: The class manage a trading strategy for multiple symbols
 *
 * flow:
 
 *
 * Change Log:
 * 28.01.2013 OCAGroup added by Orders
 
 */
public class StatisticsUtil {
    private static final String FILE_PATH = "C:/Users/magang/Mediencenter/Standard_Error_Band/";
    private static final String FILE_NAME = "Standard_Error_Band.csv";
    private static final char SEPARATOR = ';';
    private static TradesCSV tradesCSV = new TradesCSV(FILE_PATH, FILE_NAME, SEPARATOR);
    
public static void main(String[] args) throws FileNotFoundException, IOException {
        List<Double> values = new ArrayList<Double>();
        
               
        tradesCSV.getTrades();
        List<String[]> allElements = tradesCSV.getAllTrades();
        int i = 0;
        for (Iterator<String[]> iterator = allElements.iterator(); iterator.hasNext();) {

            String[] s = iterator.next();
                
            values.add(Double.valueOf(s[1]));
            i++;
            
            System.out.println("i :" + i + "Value : "+ Double.valueOf(s[1]));
            
            
        }
        
        System.out.println(String.format("mean   : %10.4f", getMean(values)));
        System.out.println(String.format("median : %10.4f", getMedian(values)));
        System.out.println(String.format("std dev: %10.4f", getStandardDeviation(values)));
    }

    public static double getMean(List<Double> values) {
        double mean = 0.0;
        if ((values != null) && (values.size() > 0)) {
            for (double value : values) {
                mean += value;
            }
            mean /= values.size();
        }
        return mean;
    }

    public static double getStandardDeviation(List<Double> values) {
        double deviation = 0.0;
        if ((values != null) && (values.size() > 1)) {
            double mean = getMean(values);
            for (double value : values) {
                double delta = value-mean;
                deviation += delta*delta;
            }
            deviation = Math.sqrt(deviation/values.size());
        }
        return deviation;
    }

    public static double getMedian(List<Double> values) {
        double median = 0.0;
        if (values != null) {
            int numValues = values.size();
            if (numValues > 0) {
                Collections.sort(values);
                if ((numValues%2) == 0) {
                    median = (values.get((numValues/2)-1)+values.get(numValues/2))/2.0;
                } else {
                    median = values.get(numValues/2);
                }
            }
        }
        return median;
    }

    public static double getMean(double [] values) {
        double mean = 0.0;
        if ((values != null) && (values.length > 0)) {
            for (double value : values) {
                mean += value;
            }
            mean /= values.length;
        }
        return mean;
    }

    public static double getStandardDeviation(double [] values) {
        double deviation = 0.0;
        if ((values != null) && (values.length > 1)) {
            double mean = getMean(values);
            for (double value : values) {
                double delta = value-mean;
                deviation += delta*delta;
            }
            deviation = Math.sqrt(deviation/values.length);
        }
        return deviation;
    }

    public static double getMedian(double [] values) {
        double median = 0.0;
        if (values != null) {
            int numValues = values.length;
            if (numValues > 0) {
                Arrays.sort(values);
                if ((numValues%2) == 0) {
                    median = (values[(numValues/2)-1]+values[numValues/2])/2.0;
                } else {
                    median = values[numValues/2];
                }
            }
        }
        return median;
    }
}
