package com.amaggioni.myutilities;


/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 03.04.2013
 * change date : 03.04.2013
 *
 * description: My Math functions
 *
 * flow:
 * <p/>
 *
 * Change Log:
 * <p/>
 * <p/>
 */
public class MyMath
{

    public static double round(double val, int sca)
    {

        double s = Math.pow(10, sca);
        return Math.round(val * s) / s;
    }
    
    public static double[] round(double val[], int sca)
    {

        double s = Math.pow(10, sca);
        for (int idx = 0; idx < val.length; idx++)
        {
            
            val[idx] = Math.round(val[idx] * s) / s;
        }
        
        return val;
    }
    
    public static double[] arrayMultValue(double[] arr, double val)
    {
        
        for (int i = 0; i < arr.length; i++)
        {
            
            arr[i] = arr[i] * val;
        }
        
        return arr;
             
    }
    
    public static double[] arrayMinArray(double[] arr1, double[] arr2)
    {
        double[] arr3 = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++)
        {
            
            arr3[i] = arr1[i] - arr2[i];
        }
        
        return arr3;
             
    }
    
    
}
