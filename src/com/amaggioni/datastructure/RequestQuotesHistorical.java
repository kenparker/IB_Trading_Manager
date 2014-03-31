/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.datastructure;

import core.PriceBar;
import com.amaggioni.indicators.QuoteHistory;
import com.ib.client.EWrapperMsgGenerator;
import java.util.List;

/**
 *
 * @author magang
 */
public class RequestQuotesHistorical
{

    private String symbol;
    private boolean historieCompleted;
    private boolean volumeLimitReached;
    private boolean congestionstrategy;
    private boolean convergencestrategy;
    private boolean convergencestrategy2;
    private boolean MACDlowstrategy1;
    private boolean EODstrategy1;
    private boolean dynamictradestrategy1;
    private boolean autoenvelopestrategy1;
    private boolean standarderrorbands1;
       private boolean ROCStrategy1;

    
 
    private boolean avgvolcalculated;
    private QuoteHistory qh;
    //ArrayList<PriceBar> quotehistory;

    public RequestQuotesHistorical(String symbol)
    {
        
        this.symbol = symbol;
        this.historieCompleted = false;
        this.volumeLimitReached = false;
        this.congestionstrategy = false;
        this.convergencestrategy2 = false;
        this.standarderrorbands1 = false;
        this.EODstrategy1 = false;
        
        this.qh = new QuoteHistory();
    }

    /**
     * @return the historieCompleted
     */
    public boolean isHistorie()
    {
        return historieCompleted;
    }

    // 
    public boolean addList(String date, double open, double close, double high, double low,
            double WAP, int count, double volume)
    {

        PriceBar pricebar = new PriceBar(date, open, close, high, low, WAP, count, volume);
        //boolean b = this.quotehistory.add(histdata);
        getQh().addHistoricalPriceBar(pricebar);

        return true;
    }

    /**
     * @param historieCompleted the historieCompleted to set
     */
    public void setHistorie(boolean historieCompleted)
    {
        this.historieCompleted = historieCompleted;
    }

    /**
     *
     * @param period
     * @return
     * <p/>
     * @throws Exception
     */
    public double avgVolume(int period)
    {
        List<PriceBar> allpricebars = getQh().getAll();

        double sum = 0.0;

        for (int i = allpricebars.size() - 1; i > allpricebars.size() - period - 1; i--)
        {
            //System.out.println("index " + i + " volume" + volume.get(i).getVolume());
            sum += allpricebars.get(i).getVolume();
        }

        return sum / period;


    }

    /**
     * @return the volumeLimitReached
     */
    public boolean isVolumeLimit()
    {
        return volumeLimitReached;
    }

    /**
     * @param volumeLimitReached the volumeLimitReached to set
     */
    public void setVolumeLimit(boolean volumePerLimitReached)
    {
        this.volumeLimitReached = volumePerLimitReached;
    }

    /**
     * @return the symbol
     */
    public String getSymbol()
    {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    /**
     * @return the qh
     */
    public QuoteHistory getQh()
    {
        return qh;
    }

    /**
     * @param qh the qh to set
     */
    public void setQh(QuoteHistory qh)
    {
        this.qh = qh;
    }

    /**
     * @return the congestionstrategy
     */
    public boolean isCongestionstrategy()
    {
        return congestionstrategy;
    }

    /**
     * @param congestionstrategy the congestionstrategy to set
     */
    public void setCongestionstrategy(boolean congestionstrategy)
    {
        this.congestionstrategy = congestionstrategy;
    }

    
    /**
     * @return the avgvolcalculated
     */
    public boolean isAvgvolcalculated()
    {
        return avgvolcalculated;
    }

    /**
     * @param avgvolcalculated the avgvolcalculated to set
     */
    public void setAvgvolcalculated(boolean avgvolcalculated)
    {
        this.avgvolcalculated = avgvolcalculated;
    }

    /**
     * Get the value of ROCStrategy1
     *
     * @return the value of ROCStrategy1
     */
    public boolean isROCStrategy1()
    {
        return ROCStrategy1;
    }

    /**
     * Set the value of ROCStrategy1
     *
     * @param ROCStrategy1 new value of ROCStrategy1
     */
    public void setROCStrategy1(boolean ROCStrategy1)
    {
        this.ROCStrategy1 = ROCStrategy1;
    }
    
    public String toStringx(int period)
    {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < period; i++)
        {

            int index = qh.size() - i - 1;
         
            sb.append(
                    EWrapperMsgGenerator.historicalData(i, 
                    qh.getPriceBar(index).getDate(),
                    qh.getPriceBar(index).getOpen(),
                    qh.getPriceBar(index).getHigh(), 
                    qh.getPriceBar(index).getLow(), 
                    qh.getPriceBar(index).getClose(),
                    (int) qh.getPriceBar(index).getVolume(),
                    qh.getPriceBar(index).getCount(), 
                    qh.getPriceBar(index).getWAP(), 
                    false));

            //sb.append("\n");

        }

        return sb.toString();
    }

    /**
     * @return the convergencestrategy
     */
    public boolean isConvergencestrategy()
    {
        return convergencestrategy;
    }

    /**
     * @param convergencestrategy the convergencestrategy to set
     */
    public void setConvergencestrategy(boolean convergencestrategy)
    {
        this.convergencestrategy = convergencestrategy;
    }

    /**
     * @return the convergencestrategy2
     */
    public boolean isConvergencestrategy2()
    {
        return convergencestrategy2;
    }

    /**
     * @param convergencestrategy2 the convergencestrategy2 to set
     */
    public void setConvergencestrategy2(boolean convergencestrategy2)
    {
        this.convergencestrategy2 = convergencestrategy2;
    }

  /**
   * @return the MACDlowstrategy1
   */
  public boolean isMACDlowstrategy1()
  {
    return MACDlowstrategy1;
  }

  /**
   * @param MACDlowstrategy1 the MACDlowstrategy1 to set
   */
  public void setMACDlowstrategy1(boolean MACDlowstrategy1)
  {
    this.MACDlowstrategy1 = MACDlowstrategy1;
  }

  /**
   * @return the EODstrategy1
   */
  public boolean isEODstrategy1()
  {
    return EODstrategy1;
  }

  /**
   * @param EODstrategy1 the EODstrategy1 to set
   */
  public void setEODstrategy1(boolean EODstrategy1)
  {
    this.EODstrategy1 = EODstrategy1;
  }

    /**
     * @return the dynamictradestrategy1
     */
    public boolean isDynamictradestrategy1()
    {
        return dynamictradestrategy1;
    }

    /**
     * @param dynamictradestrategy1 the dynamictradestrategy1 to set
     */
    public void setDynamictradestrategy1(boolean dynamictradestrategy1)
    {
        this.dynamictradestrategy1 = dynamictradestrategy1;
    }

    /**
     * @return the autoenvelopestrategy1
     */
    public boolean isAutoEnvelopestrategy1()
    {
        return autoenvelopestrategy1;
    }

    /**
     * @param autoenvelopestrategy1 the autoenvelopestrategy1 to set
     */
    public void setAutoEnvelopestrategy1(boolean autoenvelopestrategy1)
    {
        this.autoenvelopestrategy1 = autoenvelopestrategy1;
    }

    /**
     * @return the standarderrorbands1
     */
    public boolean isStandardErrorBandsstrategy1()
    {
        return standarderrorbands1;
    }

    /**
     * @param standarderrorbands1 the standarderrorbands1 to set
     */
    public void setStandardErrorBandsstrategy1(boolean standarderrorbands1)
    {
        this.standarderrorbands1 = standarderrorbands1;
    }
}
