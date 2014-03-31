package com.amaggioni.strategies;

import com.amaggioni.candles.CandlePattern;
import com.amaggioni.datastructure.RequestData;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import core.PriceBar;
import com.amaggioni.indicators.QuoteHistory;
import com.amaggioni.myutilities.WriteToFile;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 19.05.2013
 * change date : xx.xx.2013
 *
 * description:
 *
 * flow:
 * <p/>
 *
 * Change Log:
 * 19.05.2013 First Version
 * 12.06.2013 checkprice logic added
 * <p/>
 */
public class EODBigVolumeStrategy1
{

  private GregorianCalendar timefrom;
  private GregorianCalendar timeto;
  
  private String symbol;
  private boolean[] EODBigVolume;
  private QuoteHistory qh;
  private boolean LOG_EODBigVolume = false;
  StringBuffer outString = new StringBuffer();

  public EODBigVolumeStrategy1(RequestQuotesHistorical m_quoteshist)
  {


    final int qhsize = m_quoteshist.getQh().getSize();
    this.EODBigVolume = new boolean[qhsize];

    this.symbol = m_quoteshist.getSymbol();
    this.qh = m_quoteshist.getQh();


  }

  /**
   * ---------------------------------------------------------------
   * <p/>
   * M A I N Method
   * <p/>
   * <
   */
  public void evalStrategy(GregorianCalendar m_timefrom, GregorianCalendar m_timeto,
          TradeOrder to,
          RequestQuotesHistorical m_qh,
          RequestData m_rd)
  {
    this.timefrom = m_timefrom;
    this.timeto = m_timeto;
    
    
    //log1("evalStrategy");

    evalEODBigVolume(to, m_rd);

    calcTradeOrder(to, m_qh);

  }

  //<editor-fold defaultstate="collapsed" desc="Getter Setter">
  /**
   * @return the EODBigVolume
   */
  public boolean[] isEODBigVolume()
  {
    return EODBigVolume;
  }

  /**
   * @param EODBigVolume the EODBigVolume to set
   */
  public void setEODBigVolume(boolean[] congestion)
  {
    this.EODBigVolume = congestion;
  }

    

  //</editor-fold>
  private void evalEODBigVolume(TradeOrder to,  RequestData m_rd)
  {

    GregorianCalendar gc = new GregorianCalendar();
    if ((this.timefrom.compareTo(gc) < 0 && this.timeto.compareTo(gc) > 0) 
         && (to.getCheckPrice() == 0 
            || (to.getCheckPrice() != 0 && m_rd.getLow() <= to.getCheckPrice())))
    {
      //log2("evalEODBigVolume");
      
      CandlePattern cd = new CandlePattern(this.qh);
      cd.calculate(5);
      final int lastindex = this.qh.getSize() - 1;
      this.EODBigVolume[lastindex] = cd.isBigVolume(lastindex) && cd.isWhiteCandle(lastindex);
    }
  }

  public void printEODBigVolume(int period)
  {

    for (int i = EODBigVolume.length - period - 1; i < EODBigVolume.length; i++)
    {
      EODSingleToPrint2(i, "EODBigVolumeStrategy1", this.symbol);
    }

  }

  private void calcTradeOrder(TradeOrder to, RequestQuotesHistorical m_quoteshist)
  {

    if (this.EODBigVolume[this.qh.size() - 1])
    {
      PriceBar lastPriceBar = this.qh.getLastPriceBar();

      to.setCheckPrice(999.99);
      to.setOrderPrice(lastPriceBar.getHigh() + 0.02);
      to.setQh(this.qh);
      to.Calculate();
    }
  }

  private void EODSingleToPrint2(final int lastindex, String methodname, String symbol)
  {
    if (this.EODBigVolume[lastindex])
    {
      outString.delete(0, outString.length());
      outString.append(new Date());
      outString.append(" [Info] [").append( methodname).append( "] [").append( symbol).append( "] ");
      outString.append(" EODBigVolume ");
      outString.append(" Date ");
      outString.append(this.qh.getPriceBar(lastindex).getDate());
      outString.append(" High ");
      outString.append(this.qh.getPriceBar(lastindex).getHigh());
      outString.append(" Low  ");
      outString.append(this.qh.getPriceBar(lastindex).getLow());
      outString.append(" Close  ");
      outString.append(this.qh.getPriceBar(lastindex).getClose());
      
       WriteToFile.logAll(outString.toString(), symbol);
    }
  }

  private void log1(String methodname)
  {
    outString.delete(0, outString.length());
    outString.append(new Date());
    outString.append(" [Info] [").append( methodname).append( "] [").append( symbol).append( "] ");
    outString.append("Start");
    WriteToFile.logAll(outString.toString(), symbol);
  }
  
  private void log2(String methodname)
  {
    outString.delete(0, outString.length());
    outString.append(new Date());
    outString.append(" [Info] [").append( methodname).append( "] [").append( symbol).append( "] ");
    outString.append("Time Check ok");
    WriteToFile.logAll(outString.toString(), symbol);
  }
}
