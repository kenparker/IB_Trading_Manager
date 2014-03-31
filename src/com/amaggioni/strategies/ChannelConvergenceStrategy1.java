package com.amaggioni.strategies;

// http://blogs.stockcharts.com/scanning/2011/08/scan-coding-sample-blocks.html?st=channel+convergence+scan

import com.amaggioni.datastructure.RequestData;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.indicators.PeriodHigh;
import com.amaggioni.indicators.PeriodLow;
import core.PriceBar;
import com.amaggioni.myutilities.WriteToFile;
import java.util.Date;

public class ChannelConvergenceStrategy1
{

    private int period;
    private double highperiod;
    private double lowperiod;
    private double close;
    private boolean convergence;

    public ChannelConvergenceStrategy1()
    {

        this.period = 0;
        this.highperiod = 0;
        this.lowperiod = 0;
    }

    public boolean evalStrategy(int m_period,
            TradeOrder m_tradeorder,
            RequestQuotesHistorical m_quoteshist,
            RequestData m_requestdata)
    {


        this.period = m_period;


        PeriodHigh ph = new PeriodHigh(m_quoteshist.getQh());
        this.setHighperiod(ph.calculate(period, PriceBar.ValueType.Close));

        PeriodLow pl = new PeriodLow(m_quoteshist.getQh());
        this.setLowperiod(pl.calculate(period, PriceBar.ValueType.Close));
        
        this.setClose(m_requestdata.getClose());

        if (m_requestdata.getClose() * 0.99 < this.getLowperiod()
                && m_requestdata.getClose() * 1.01 > this.getHighperiod())
        {
            this.setConvergence(true);

        }

        return isConvergence();
    }

    public void ConvergenceToLog(String methodname, String symbol)
    {
        String outString;
        outString = new Date()
                + " " + "[Info] [" + methodname + "] [" + symbol + "] "
                + " Close :" + this.getClose()
                + " High Close :" + this.getHighperiod()
                + " Low Close :" + this.getLowperiod();
        System.out.println(outString);
        WriteToFile.writeF(outString, symbol);
    }

    /**
     * @return the highperiod
     */
    public double getHighperiod()
    {
        return highperiod;
    }

    /**
     * @param highperiod the highperiod to set
     */
    public void setHighperiod(double highperiod)
    {
        this.highperiod = highperiod;
    }

    /**
     * @return the lowperiod
     */
    public double getLowperiod()
    {
        return lowperiod;
    }

    /**
     * @param lowperiod the lowperiod to set
     */
    public void setLowperiod(double lowperiod)
    {
        this.lowperiod = lowperiod;
    }

    /**
     * @return the convergence
     */
    public boolean isConvergence()
    {
        return convergence;
    }

    /**
     * @param convergence the convergence to set
     */
    public void setConvergence(boolean convergence)
    {
        this.convergence = convergence;
    }

    
  /**
   * @return the close
   */
  public double getClose()
  {
    return close;
  }

  /**
   * @param close the close to set
   */
  public void setClose(double close)
  {
    this.close = close;
  }
}
