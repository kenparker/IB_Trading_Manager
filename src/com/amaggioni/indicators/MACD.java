package com.amaggioni.indicators;

import core.PriceBar.ValueType;
import com.amaggioni.myutilities.MyMath;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : xx.03.2013
 * change date : 12.05.2013
 *
 * d
 * Change Log:
 * <p/>
 * 
 * 
 */
public class MACD extends Indicator
{

    private int qhsize;
    //private QuoteHistory qh;
    private double[] MACD;

    public MACD(QuoteHistory qh)
    {
        this.qh = qh;
        this.qhsize = qh.getSize();
        this.MACD = new double[this.qhsize];
    }

    public double calculate(int fastLength, int slowLength, ValueType vt)
    {
        MovingAverage fastEMA = new MovingAverage(qh);
        fastEMA.calculate(fastLength, 1, vt);
        MovingAverage slowEMA = new MovingAverage(qh);
        slowEMA.calculate(slowLength, 1, vt);


        for (int a = 0; a < qh.getSize(); a++)
        {

            //System.out.println("Index " + a + " Value fast " + fastEMA.getMaIndex(- a) + " Value slow " + slowEMA.getMaIndex(- a));
            MACD[a] = fastEMA.getMaAll()[a] - slowEMA.getMaAll()[a];
            //System.out.println("Index " + a + " value : " + MACD[qh.getSize() - a - 1]);
        }




        return getMACDIndex(0);
    }

    /**
     * @return the ma
     */
    public double[] getMACDAll()
    {
        return MACD;
    }

    public double getMACDIndex(int index)
    {

        return MyMath.round(getMACDAll()[this.qhsize - 1 + index],2);
    }

  @Override
  public double calculate()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
