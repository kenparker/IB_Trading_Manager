/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.amaggioni.testcallers;

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
/*
        Get classpath using System class
        This example shows how to get the classpath using
        System class.
*/
 
public class PrintClassPath {
 
        public static void main(String[] args) {
       
                /*
                 * Get java.class.path system property using
                 * public static String getProperty(String name) method of
                 * System class.
                 */
                 
                 String strClassPath = System.getProperty("java.class.path");
                 String workingdirectory = System.getProperty("user.dir");
                 System.out.println("Classpath is " + strClassPath);
                 System.out.println("Working Directory is " + workingdirectory);
 
        }
}
