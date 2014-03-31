package com.amaggioni.indicators;

import core.PriceBar;
import com.amaggioni.myutilities.MyMath;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 12.05.2013
 * change date : 12.05.2013
 *
 * description: The class manage a trading strategy for multiple symbols
 *
 * flow:
 * <p/>
 *
 * Change Log:
 * 12.05.2013 first version
 * <p/>
 */
public class MACDHist extends Indicator
{

    private int qhsize;
    private int lineperiod;
    private double[] MACDVal;
    private double[] MACDHist;

    public MACDHist(QuoteHistory qh)
    {
        this.qh = qh;
        this.qhsize = qh.getSize();
        this.MACDHist = new double[this.qhsize];
    }
    
    public MACDHist(MACD macd)
    {
       
        this.qh = macd.qh;
        this.qhsize = qh.getSize();
        this.MACDVal = macd.getMACDAll();
        this.MACDHist = new double[this.qhsize];
    }

    @Override
    public double calculate()
    {
        MovingAverage ma = new MovingAverage(this.MACDVal);
        ma.calculate(getLineperiod(), 1, PriceBar.ValueType.Other);
        MACDHist = MyMath.arrayMinArray(MACDVal, ma.getMaAll());
        return this.getMACDHist()[this.qhsize-1];
    }

    /**
     * @return the MACDHist
     */
    public double[] getMACDHist()
    {
        return MACDHist;
    }
    
    public double getMACDHistIndex(int index)
    {

        return MyMath.round(getMACDHist()[this.qhsize - 1 + index],2);
    }

  /**
   * @return the lineperiod
   */
  public int getLineperiod()
  {
    return lineperiod;
  }

  /**
   * @param lineperiod the lineperiod to set
   */
  public void setLineperiod(int lineperiod)
  {
    this.lineperiod = lineperiod;
  }
}
