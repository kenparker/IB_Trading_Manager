package com.amaggioni.strategies;

import com.amaggioni.datastructure.RequestData;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.indicators.MovingAverage;
import com.amaggioni.indicators.PeriodHigh;
import com.amaggioni.indicators.PeriodLow;
import core.PriceBar;
import core.PriceBar.ValueType;
import com.amaggioni.myutilities.WriteToFile;
import java.util.Date;

public class CongestionStrategy1
{

    private int periodRangeLong;
    private int periodRangeShort;
    private double volamult;
    private boolean congestion;
    private double hhv;
    private double llv;
    private double rangeAvg;
    private double rangeCurrent;

    public CongestionStrategy1()
    {

        this.periodRangeLong = 0;
        this.periodRangeShort = 0;
        this.volamult = 0;
        this.congestion = false;
    }

    public boolean evalStrategy(int m_periodRangeLong, int m_periodRangeShort, double m_volamult,
            TradeOrder m_symboldata,
            RequestQuotesHistorical m_quoteshist,
            RequestData m_requestdata)
    {

        
        this.periodRangeLong = m_periodRangeLong;
        this.volamult = m_volamult;
        this.periodRangeShort = m_periodRangeShort;

                
        MovingAverage ma = new MovingAverage(m_quoteshist.getQh());
        rangeAvg = volamult * ma.calculate(periodRangeLong, 0, ValueType.HighminusLow);
        rangeAvg = Math.round(rangeAvg * 100) / 100.0;


        PeriodHigh ph = new PeriodHigh(m_quoteshist.getQh());
        hhv = ph.calculate(periodRangeShort, PriceBar.ValueType.High);
        
        PeriodLow pl = new PeriodLow(m_quoteshist.getQh());
        llv = pl.calculate(periodRangeShort, PriceBar.ValueType.Low);
        
        rangeCurrent = Math.round((hhv - llv) * 100) / 100.0;
        
        

        if (rangeAvg > rangeCurrent && m_requestdata.getClose() >= llv && m_requestdata.getClose() <= hhv)
        {
            this.congestion = true;
            
            double checkprice = Math.round(((hhv - llv) / 2 + llv) * 100) / 100.0;

            m_symboldata.setCheckPrice(checkprice);
            m_symboldata.setOrderPrice(llv);
            m_symboldata.Calculate();

        }

        return congestion;
    }

    /**
     * @return the periodRangeLong
     */
    public int getPeriodRangeLong()
    {
        return periodRangeLong;
    }

    /**
     * @param periodRangeLong the periodRangeLong to set
     */
    public void setPeriodRangeLong(int periodRangeLong)
    {
        this.periodRangeLong = periodRangeLong;
    }

    /**
     * @return the periodRangeShort
     */
    public int getPeriodRangeShort()
    {
        return periodRangeShort;
    }

    /**
     * @param periodRangeShort the periodRangeShort to set
     */
    public void setPeriodRangeShort(int periodRangeShort)
    {
        this.periodRangeShort = periodRangeShort;
    }

    /**
     * @return the volamult
     */
    public double getVolamult()
    {
        return volamult;
    }

    /**
     * @param volamult the volamult to set
     */
    public void setVolamult(double volamult)
    {
        this.volamult = volamult;
    }

    /**
     * @return the congestion
     */
    public boolean isCongestion()
    {
        return congestion;
    }

    /**
     * @param congestion the congestion to set
     */
    public void setCongestion(boolean congestion)
    {
        this.congestion = congestion;
    }

    /**
     * @return the hhv
     */
    public double getHhv()
    {
        return hhv;
    }

    /**
     * @param hhv the hhv to set
     */
    public void setHhv(double hhv)
    {
        this.hhv = hhv;
    }

    /**
     * @return the llv
     */
    public double getLlv()
    {
        return llv;
    }

    /**
     * @param llv the llv to set
     */
    public void setLlv(double llv)
    {
        this.llv = llv;
    }

    /**
     * @return the rangeAvg
     */
    public double getRangeAvg()
    {
        return rangeAvg;
    }

    /**
     * @param rangeAvg the rangeAvg to set
     */
    public void setRangeAvg(double rangeAvg)
    {
        this.rangeAvg = rangeAvg;
    }

    /**
     * @return the rangeCurrent
     */
    public double getRangeCurrent()
    {
        return rangeCurrent;
    }

    /**
     * @param rangeCurrent the rangeCurrent to set
     */
    public void setRangeCurrent(double rangeCurrent)
    {
        this.rangeCurrent = rangeCurrent;
    }

    public void CongestionToPrint(String methodname, String symbol)
    {
        String outString;
        outString = new Date() + " " + "[Info] [" + methodname + "] [" + symbol + "] " + " RangeAvg :" + 
                this.getRangeAvg() + " RangeCurrent :" + this.getRangeCurrent() + " High :" + 
                this.getHhv() + " low :" + this.getLlv();
        System.out.println(outString);
        WriteToFile.writeF(outString, symbol);
    }
}
