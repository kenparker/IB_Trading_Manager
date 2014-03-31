/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.testcallers;

import com.amaggioni.myutilities.WaitTillTime;

/**
 *
 * @author maggioni
 */
public class TestWait
{
 
  WaitTillTime t = new WaitTillTime();
  
  public static void main(String[] args) throws InterruptedException {
    
    new TestWait().t.doWait(13,0,0);
  }
}
