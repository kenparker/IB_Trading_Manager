/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.indicators;

import core.PriceBar;
import com.amaggioni.myutilities.WriteToFile;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author magang
 */
public class AvgHourlyVol
{

  private double[][] volhour;
  private double[] volsum;
  private boolean volAboveAvg;
  private double volumemult;
  protected QuoteHistory qh;
  private boolean LOG_MSG = false;

  public AvgHourlyVol()
  {
    this(null, 1.0);
  }

  public AvgHourlyVol(QuoteHistory qh, double m_volumemult)
  {
    this.qh = qh;
    this.volAboveAvg = false;
    this.volumemult = m_volumemult;
    int lines = countlines();
    volhour = new double[lines][];
    volsum = new double[lines];
    initialize();
    load();

    /*System.out.println(" x : " + this.volhour.length);
     System.out.println(" y : " + this.volhour[0].length);
     System.out.println(" y : " + this.volhour[1].length);
     System.out.println(" y : " + this.volhour[2].length);*/
  }

  private int countlines()
  {

    int i = 0;

    String prevBar;
    prevBar = "";

    for (Iterator<PriceBar> iterator = qh.getAll().iterator(); iterator.hasNext();)
    {
      PriceBar cb = iterator.next();

      String[] currBar = cb.getDate().split(" ");
      //System.out.println("pb :" + prevBar + " curr bar :" + currBar[0]);
      if (!currBar[0].equals(prevBar))
      {
        prevBar = currBar[0];
        i++;

      }

    }
    //System.out.println("lines " + i);
    return i;
  }

  private void initialize()
  {
    int i = 0, j = 0, c = 0;

    String prevBar = "";

    for (Iterator<PriceBar> iterator = qh.getAll().iterator(); iterator.hasNext();)
    {
      PriceBar cb = iterator.next();

      c++;

      String[] currBar = cb.getDate().split(" ");
      if (currBar[0].equals(prevBar))
      {
        j++;
      } else
      {
        if (c != 1)
        {

          //System.out.println("i" + i +" J: " + j);
          this.volhour[i] = new double[j];

          i++;
        }
        prevBar = currBar[0];
        j = 1;
      }

    }

    //System.out.println("I " + i +" J last: " + j);
    this.volhour[i] = new double[j];
  }

  /**
   * load the Quotes in the Matrix
   */
  private void load()
  {
    int i = 0, j = 0, c = 0;

    String prevBar = "";

    for (Iterator<PriceBar> iterator = qh.getAll().iterator(); iterator.hasNext();)
    {
      PriceBar cb = iterator.next();

      c++;

      String[] currBar = cb.getDate().split(" ");
      if (currBar[0].equals(prevBar))
      {
        j++;
      } else
      {
        if (c != 1)
        {
          i++;
        }

        prevBar = currBar[0];
        j = 0;
      }

      volhour[i][j] = cb.getVolume();

    }
  }

  /**
   *
   * @return
   */
  public void calculate()
  {

    int i, j;

    // Number of Bars in the Current-Day
    int volend = volhour[volhour.length - 1].length;

    // sum volumes for each Day
    for (i = 0; i < volhour.length; i++)
    {

      for (j = 0; j < volend; j++)
      {
        this.volsum[i] += this.volhour[i][j];
      }

    }

    // Calculate average volume
    double volsumtot = 0;
    //System.out.println("volhour.length " + volhour.length);
    for (i = 0; i < volhour.length - 1; i++)
    {
      //System.out.println(" volhrs " + this.volsum[i]);
      volsumtot += this.volsum[i];
    }
    volsumtot = volsumtot / (volhour.length - 1);

    // Check whether the current  volume is above the average
    if (this.volsum[volhour.length - 1] > volsumtot * this.volumemult)
    {
      AvgHourlyVolToLog(volsumtot);

      this.setVolaboveavg(true);
    }


  }

  public void printAll()
  {


    for (int i = 0; i < volhour.length; i++)
    {
      for (int j = 0; j < volhour[i].length; j++)
      {
        System.out.print(volhour[i][j]);
        System.out.print(" ");
      }
      System.out.print(this.volsum[i]);
      System.out.print("\n");

    }
  }

  /**
   * @return the volAboveAvg
   */
  public boolean isVolaboveavg()
  {
    return volAboveAvg;
  }

  /**
   * @param volAboveAvg the volAboveAvg to set
   */
  public void setVolaboveavg(boolean volaboveavg)
  {
    this.volAboveAvg = volaboveavg;
  }

  private void AvgHourlyVolToLog(double volsumtot)
  {
    if (LOG_MSG)
    {
      String outString;
      outString = new Date()
              + " " + "[Info] [AvgHourlyVolToLog]"
              + "Volume :" + this.volsum[volhour.length - 1] + " is above average :" + volsumtot;
      System.out.println(outString);
      WriteToFile.writeF(outString, "");
    }


  }
}
