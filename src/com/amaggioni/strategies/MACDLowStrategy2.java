package com.amaggioni.strategies;

import com.amaggioni.datastructure.RequestData;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.indicators.MACD;
import com.amaggioni.indicators.MACDHist;
import com.amaggioni.indicators.PercentRank;
import core.PriceBar.ValueType;
import com.amaggioni.indicators.QuoteHistory;
import com.amaggioni.myutilities.MyMath;
import com.amaggioni.myutilities.WriteToFile;


/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 15.05.2013
 * change date : 19.05.2013
 *
 * description:
 *
 * flow:
 * <p/>
 * Change Log:
 * 15.05.2013 first version
 * 19.05.2013 'Percent Rank' added
 * 04.06.2013 'barsize' added
 *
 */
public class MACDLowStrategy2
{

    private int MACDPeriodLong = 0;
    private int MACDPeriodShort = 0;
    private int MACDLookbackPeriod = 0;
    private MACD macd;
    private MACDHist macdhist;
    private double[] MACDRank;
    private double[] MACDHistRank;
    private String symbol;
    private String barsize;
    private boolean[] MACDLowFlag;
    private boolean[] MACDHistFlag;
    private QuoteHistory qh;
    private boolean LOG_MACDLow = true;

    public MACDLowStrategy2(RequestQuotesHistorical m_qh)
    {

        final int qhsize = m_qh.getQh().getSize();
        this.MACDLowFlag = new boolean[qhsize];
        this.MACDHistFlag = new boolean[qhsize];

        this.MACDRank = new double[qhsize];
        this.MACDHistRank = new double[qhsize];

        this.symbol = m_qh.getSymbol();
        this.barsize = "";
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
            RequestData m_rd, String status)
    {
        //if (m_qh.getSymbol().equals("SPY"))
        //{
            evalMACD(m_MACDLong, m_MACDShort, m_lookbackperiod, m_macdline, m_qh);
            evalMACDHist(m_MACDLong, m_MACDShort, m_lookbackperiod, m_macdline, m_qh);
            if (LOG_MACDLow)
            {
                this.printMACDLow(0);
            }
            
            calcTradeOrder(to, m_qh, status);
       // }

    }

    //<editor-fold defaultstate="collapsed" desc="Getter Setter">

    public int getMACDLookbackPeriod()
    {
        return MACDLookbackPeriod;
    }

    public void setMACDLookbackPeriod(int MACDLookbackPeriod)
    {
        this.MACDLookbackPeriod = MACDLookbackPeriod;
    }
    
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
     * @return the MACDRank
     */
    public double[] getMACDRank()
    {
        return MACDRank;
    }

    /**
     * @param MACDRank the MACDRank to set
     */
    public void setMACDRank(double[] MACDRank)
    {
        this.MACDRank = MACDRank;
    }
/**
     * @return the barsize
     */
    public String getBarsize()
    {
        return barsize;
    }

    /**
     * @param barsize the barsize to set
     */
    public void setBarsize(String barsize)
    {
        this.barsize = barsize;
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

        PercentRank pr = new PercentRank(macd.getMACDAll());
        pr.setPeriod(this.MACDLookbackPeriod);
        pr.setVt(ValueType.Other);
        pr.calculate();
        MACDRank = pr.getPercrank();

        for (int i = 5; i < MACDLowFlag.length; i++)
        {
            MACDLowFlag[i] = macd.getMACDAll()[i] < -0.20
                    && macd.getMACDAll()[i] > macd.getMACDAll()[i-1]
                    && this.MACDRank[i-1] <= 10;
        }

    }

    private void evalMACDHist(int m_macdperiodLong, int m_macdperiodShort, int m_lookbackperiod,
            int m_macdline,
            RequestQuotesHistorical m_qh)
    {

        macdhist.setLineperiod(m_macdline);
        macdhist.calculate();

        PercentRank pr = new PercentRank(macdhist.getMACDHist());
        pr.setPeriod(this.MACDLookbackPeriod);
        pr.setVt(ValueType.Other);
        pr.calculate();
        MACDHistRank = pr.getPercrank();
        
        
        
        for (int i = 10; i < MACDHistFlag.length; i++)
        {
            MACDHistFlag[i] = macdhist.getMACDHist()[i] < -0.10
                    && macdhist.getMACDHist()[i] > macdhist.getMACDHist()[i - 1] 
                    && macdhist.getMACDHist()[i - 2] > macdhist.getMACDHist()[i - 1]
                    && macdhist.getMACDHist()[i - 3] > macdhist.getMACDHist()[i - 2]
                    && macdhist.getMACDHist()[i - 4] > macdhist.getMACDHist()[i - 3]
                    && MACDHistRank[i - 1] <= 10
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
            MACDlowSingleToPrint2_1(i, "MACDLowStrategy2", this.getSymbol());
        }
        
        for (int i = MACDLowFlag.length - period - 1; i < MACDLowFlag.length; i++)
        {
            MACDlowSingleToPrint2_2(i, "MACDLowStrategy2", this.getSymbol());
        }

    }

    private void calcTradeOrder(TradeOrder to, RequestQuotesHistorical m_quoteshist, String status)
    {
        if (checkMACDLowPeriod(0))
        {

            if (!LOG_MACDLow)
            {
                this.printMACDLow(0);
            }


            to.setuserReqTyp(status);
        }
    }

    private void MACDlowSingleToPrint2_1(final int lastindex, String methodname, String symbol)
    {
        if (this.MACDLowFlag[lastindex])
        {
           
            String outString = WriteToFile.msgHeaderBld("Info", methodname, 
                    this.getSymbol(), 
                    this.getBarsize(),
                    this.getMACDLookbackPeriod());
           
            outString = outString 
                    + " MACD Flag " + (this.MACDLowFlag[lastindex] ? " True " : " false ")
                    + " MACD :" + MyMath.round(this.macd.getMACDAll()[lastindex], 3)
                    + " MACD lowrank :" + MyMath.round(this.getMACDRank()[lastindex-1], 3)
                    + " Date " + this.qh.getPriceBar(lastindex).getDate()
                    + " High " + this.qh.getPriceBar(lastindex).getHigh()
                    + " Low  " + this.qh.getPriceBar(lastindex).getLow()
                    + " Close  " + this.qh.getPriceBar(lastindex).getClose();
            
            WriteToFile.logAll(outString, symbol);
        }

        
    }
    
    private void MACDlowSingleToPrint2_2(final int lastindex, String methodname, String symbol)
    {
        

        if (this.MACDHistFlag[lastindex])
        {
            String outString = WriteToFile.msgHeaderBld("Info", methodname, 
                    this.getSymbol(), 
                    this.getBarsize(),
                    this.getMACDLookbackPeriod());
            outString = outString
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
