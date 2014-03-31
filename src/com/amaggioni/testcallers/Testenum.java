/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.testcallers;

import com.amaggioni.datastructure.TradeOrder.StopType;

/**
 *
 * @author magang
 */
public class Testenum
{
    public static void main(String[] args) {
    StopType[] ee = StopType.values();
    for (int i = 0; i < ee.length; i++) {
      System.out.println(ee[i]);
    }
    
  }
}


