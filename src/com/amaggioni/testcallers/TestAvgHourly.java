/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.testcallers;

import com.amaggioni.indicators.AvgHourlyVol;

/**
 *
 * @author magang
 */
public class TestAvgHourly
{
    
    AvgHourlyVol av = new AvgHourlyVol();
    
    public  static void main(String[] arg)
    {
        
        new TestAvgHourly().runns();
        
    }
    
    public void runns()
    {
       // av.genTestData();
        
        av.calculate();
        
        av.printAll();
    }
}
