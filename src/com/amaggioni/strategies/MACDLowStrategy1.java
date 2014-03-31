package com.amaggioni.strategies;

import com.amaggioni.datastructure.RequestData;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.indicators.MACD;
import com.amaggioni.indicators.MACDHist;
import com.amaggioni.indicators.PeriodLow;
import core.PriceBar;
import core.PriceBar.ValueType;
import com.amaggioni.indicators.QuoteHistory;
import com.amaggioni.myutilities.MyMath;
import com.amaggioni.myutilities.WriteToFile;
import java.util.Date;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 15.05.2013
 * change date : xx.xx.xxxx
 *
 * description:
 *
 * flow:
 * <p/>
 * Change Log:
 * 15.05.2013 first version
 * xx.xx.xxxx ...
 *
 */
public class MACDLowStrategy1
{

    private int MACDPeriodLong = 0;
    private int MACDPeriodShort = 0;
    private int MACDLookbackPeriod = 0;
    private MACD macd;
    private MACDHist macdhist;
    private double[] MACDLow;
    private double[] MACDHistLow;
    private String symbol;
    private boolean[] MACDLowFlag;
    private boolean[] MACDHistFlag;
    private QuoteHistory qh;
    private boolean LOG_MACDLow = true;

    public MACDLowStrategy1(RequestQuotesHistorical m_qh)
    {

        final int qhsize = m_qh.getQh().getSize();
        this.MACDLowFlag = new boolean[qhsize];
        this.MACDHistFlag = new boolean[qhsize];

        this.MACDLow = new double[qhsize];
        this.MACDHistLow = new double[qhsize];

        this.symbol = m_qh.getSymbol();
        this.qh = m_qh.getQh();

        this.macd = new MACD(this.qh);
        this.macdhist = new MACDHist(this.macd);

    }

    /**
     * ---------------------------------------------------------------
     * <p/>
     * M A I N Method
     * <p/>
     * <
     * p/>
     */
    public void evalStrategy(int m_MACDLong, int m_MACDShort, int m_macdline, int m_lookbackperiod,
            TradeOrder to,
            RequestQuotesHistorical m_qh,
            RequestData m_rd)
    {
        //if (m_qh.getSymbol().equals("SPY"))
        //{
            evalMACD(m_MACDLong, m_MACDShort, m_lookbackperiod, m_macdline, m_qh);
            evalMACDHist(m_MACDLong, m_MACDShort, m_lookbackperiod, m_macdline, m_qh);
            if (LOG_MACDLow)
            {
                this.printMACDLow(5);
            }
       // }

    }

    //<editor-fold defaultstate="collapsed" desc="Getter Setter">
    /**
     * @return the MACDPeriodLong
     */
    public int getMACDPeriodLong()
    {
        return MACDPeriodLong;
    }

    /**
     * @param MACDPeriodLong the MACDPeriodLong to set
     */
    public void setMACDPeriodLong(int MACDPeriodLong)
    {
        this.MACDPeriodLong = MACDPeriodLong;
    }

    /**
     * @return the MACDPeriodShort
     */
    public int getMACDPeriodShort()
    {
        return MACDPeriodShort;
    }

    /**
     * @param MACDPeriodShort the MACDPeriodShort to set
     */
    public void setMACDPeriodShort(int MACDPeriodShort)
    {
        this.MACDPeriodShort = MACDPeriodShort;
    }

    /**
     * @return the MACDLowFlag
     */
    public boolean[] isMACDLowFlag()
    {
        return MACDLowFlag;
    }

    /**
     * @param MACDLowFlag the MACDLowFlag to set
     */
    public void setMACDLowFlag(boolean[] MACDLowFlag)
    {
        this.MACDLowFlag = MACDLowFlag;
    }

    /**
     * @return the MACDLow
     */
    public double[] getMACDLow()
    {
        return MACDLow;
    }

    /**
     * @param MACDLow the MACDLow to set
     */
    public void setMACDLow(double[] MACDLow)
    {
        this.MACDLow = MACDLow;
    }

    //</editor-fold>
    

    public void congLastSupportBasicToPrint(String methodname, String symbol)
    {
    }

    private void evalMACD(int m_macdperiodLong, int m_macdperiodShort, int m_lookbackperiod,
            int m_macdline,
            RequestQuotesHistorical m_qh)
    {

        this.MACDPeriodLong = m_macdperiodLong;
        this.MACDPeriodShort = m_macdperiodShort;
        this.MACDLookbackPeriod = m_lookbackperiod;


        macd.calculate(MACDPeriodShort, MACDPeriodLong, ValueType.Close);

        PeriodLow pl = new PeriodLow(macd.getMACDAll());
        pl.calculate(this.MACDLookbackPeriod, PriceBar.ValueType.Other);
        MACDLow = pl.getPeriodLow();

        for (int i = 5; i < MACDLowFlag.length; i++)
        {
            MACDLowFlag[i] = this.MACDLow[i] < 0
                    && this.MACDLow[i] > this.MACDLow[i-1]
                    && this.MACDLow[i-1] == macd.getMACDAll()[i-1];
        }

    }

    private void evalMACDHist(int m_macdperiodLong, int m_macdperiodShort, int m_lookbackperiod,
            int m_macdline,
            RequestQuotesHistorical m_qh)
    {

        macdhist.setLineperiod(m_macdline);
        macdhist.calculate();

        PeriodLow pl = new PeriodLow(macdhist.getMACDHist());
        pl.calculate(this.MACDLookbackPeriod/2, PriceBar.ValueType.Other);
        MACDHistLow = pl.getPeriodLow();
        
        for (int i = 10; i < MACDHistFlag.length; i++)
        {
            MACDHistFlag[i] = macdhist.getMACDHist()[i] < 0
                    && macdhist.getMACDHist()[i] > macdhist.getMACDHist()[i - 1] 
                    && macdhist.getMACDHist()[i - 2] > macdhist.getMACDHist()[i - 1]
                    && macdhist.getMACDHist()[i - 3] > macdhist.getMACDHist()[i - 2]
                    && macdhist.getMACDHist()[i - 4] > macdhist.getMACDHist()[i - 3]
                    && macdhist.getMACDHist()[i - 1] == MACDHistLow[i - 1]
                    ;
        }

    }

    public boolean checkMACDLowPeriod(int period)
    {
        boolean MACDLowOk = false;

        for (int i = MACDLowFlag.length - period - 1; i < MACDLowFlag.length; i++)
        {
            MACDLowOk = (this.MACDLowFlag[i] ? true : MACDLowOk);
        }

        return MACDLowOk;
    }

    public void printMACDLow(int period)
    {

        for (int i = MACDLowFlag.length - period - 1; i < MACDLowFlag.length; i++)
        {
            macdlowSingleToPrint2_1(i, "MACDLowStrategy1", this.symbol);
        }
        
        for (int i = MACDLowFlag.length - period - 1; i < MACDLowFlag.length; i++)
        {
            macdlowSingleToPrint2_2(i, "MACDLowStrategy1", this.symbol);
        }

    }

    private void calcTradeOrder(TradeOrder to, RequestQuotesHistorical m_quoteshist)
    {
        if (checkMACDLowPeriod(10))
        {

            if (!LOG_MACDLow)
            {
                this.printMACDLow(10);
            }


            PriceBar lastPriceBar = m_quoteshist.getQh().getLastPriceBar();

            to.setCheckPrice(999.99);
            to.setOrderPrice(lastPriceBar.getHigh() + 0.02);
            to.setQh(m_quoteshist.getQh());
            to.Calculate();
        }
    }

    private void macdlowSingleToPrint2_1(final int lastindex, String methodname, String symbol)
    {
        if (this.MACDLowFlag[lastindex])
        {
            String outString;
            outString = new Date() + " " + "[Info] [" + methodname + "] [" + symbol + "] "
                    + " MACD Flag " + (this.MACDLowFlag[lastindex] ? " True " : " false ")
                    + " MACD :" + MyMath.round(this.macd.getMACDAll()[lastindex], 3)
                    + " MACD low :" + MyMath.round(this.getMACDLow()[lastindex], 3)
                    + " Date " + this.qh.getPriceBar(lastindex).getDate()
                    + " High " + this.qh.getPriceBar(lastindex).getHigh()
                    + " Low  " + this.qh.getPriceBar(lastindex).getLow()
                    + " Close  " + this.qh.getPriceBar(lastindex).getClose();
             WriteToFile.logAll(outString, symbol);
        }

        
    }
    
    private void macdlowSingleToPrint2_2(final int lastindex, String methodname, String symbol)
    {
        

        if (this.MACDHistFlag[lastindex])
        {
            String outString;
            outString = new Date() + " " + "[Info] [" + methodname + "] [" + symbol + "] "
                    + " MACDHist Flag " + (this.MACDHistFlag[lastindex] ? " True " : " false ")
                    + " MACD hist :" + MyMath.round(this.macdhist.getMACDHist()[lastindex], 3)
                    + " Date " + this.qh.getPriceBar(lastindex).getDate()
                    + " High " + this.qh.getPriceBar(lastindex).getHigh()
                    + " Low  " + this.qh.getPriceBar(lastindex).getLow()
                    + " Close  " + this.qh.getPriceBar(lastindex).getClose();
             WriteToFile.logAll(outString, symbol);
        }
    }
}
