package com.amaggioni.strategies;

import com.amaggioni.datastructure.RequestData;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.indicators.MovingAverage;
import com.amaggioni.indicators.PeriodHigh;
import com.amaggioni.indicators.PeriodLow;
import core.PriceBar;
import core.PriceBar.ValueType;
import com.amaggioni.indicators.QuoteHistory;
import com.amaggioni.myutilities.MyMath;
import com.amaggioni.myutilities.WriteToFile;
import java.util.Date;

public class CongestionStrategy2
{

    private int periodRangeLong;
    private int periodRangeShort;
    private double volamult;
    private double[] hhv;
    private double[] llv;
    private double[] rangeAvg;
    private double[] rangeCurrent;
    private String symbol;
    private boolean[] congestion;
    private QuoteHistory qh;
    private SupportBullBasicStrategy1 sbs;
    private boolean LOG_CONGESTION = false;

    public CongestionStrategy2(RequestQuotesHistorical m_quoteshist)
    {

        this.periodRangeLong = 0;
        this.periodRangeShort = 0;
        this.volamult = 0;
        final int qhsize = m_quoteshist.getQh().getSize();
        this.congestion = new boolean[qhsize];

        this.llv = new double[qhsize];
        this.hhv = new double[qhsize];
        this.rangeAvg = new double[qhsize];
        this.rangeCurrent = new double[qhsize];

        this.symbol = m_quoteshist.getSymbol();
        this.qh = m_quoteshist.getQh();

        this.sbs = new SupportBullBasicStrategy1(m_quoteshist);
    }

    /**
     * ---------------------------------------------------------------
     * <p/>
     * M A I N Method
     * <p/>
     * <
     * p/> @param m_periodRangeLong
     * @param m_periodRangeShort
     * @param m_volamult
     * @param to
     * @param m_qh
     * @param m_rd
     */
    public void evalStrategy(int m_periodRangeLong, int m_periodRangeShort, double m_volamult,
            TradeOrder to,
            RequestQuotesHistorical m_qh,
            RequestData m_rd)
    {
        evalCongestion(m_periodRangeLong, m_volamult, m_periodRangeShort, m_qh);
        evalLLV(m_periodRangeShort, m_qh, this.llv);
        calcTradeOrder(to, m_qh);

    }

    //<editor-fold defaultstate="collapsed" desc="Getter Setter">
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
    public boolean[] isCongestion()
    {
        return congestion;
    }

    /**
     * @param congestion the congestion to set
     */
    public void setCongestion(boolean[] congestion)
    {
        this.congestion = congestion;
    }

    /**
     * @return the hhv
     */
    public double[] getHhv()
    {
        return hhv;
    }

    /**
     * @param hhv the hhv to set
     */
    public void setHhv(double[] hhv)
    {
        this.hhv = hhv;
    }

    /**
     * @return the llv
     */
    public double[] getLlv()
    {
        return llv;
    }

    /**
     * @param llv the llv to set
     */
    public void setLlv(double[] llv)
    {
        this.llv = llv;
    }

    /**
     * @return the rangeAvg
     */
    public double[] getRangeAvg()
    {
        return rangeAvg;
    }

    /**
     * @param rangeAvg the rangeAvg to set
     */
    public void setRangeAvg(double[] rangeAvg)
    {
        this.rangeAvg = rangeAvg;
    }

    /**
     * @return the rangeCurrent
     */
    public double[] getRangeCurrent()
    {
        return rangeCurrent;
    }

    /**
     * @param rangeCurrent the rangeCurrent to set
     */
    public void setRangeCurrent(double[] rangeCurrent)
    {
        this.rangeCurrent = rangeCurrent;
    }
    //</editor-fold>

    public void congLastToPrint(String methodname, String symbol)
    {
        final int lastindex = congestion.length - 1;
        congSingleToPrint2(lastindex, methodname, this.symbol);
    }

    public void congLastSupportBasicToPrint(String methodname, String symbol)
    {
        final int lastindex = this.sbs.isSupportBasic().length - 1;
        congSingleSupportBasicToPrint2(lastindex, methodname, this.symbol);
    }

    private void evalCongestion(int m_periodRangeLong, double m_volamult, int m_periodRangeShort,
            RequestQuotesHistorical m_qh)
    {

        this.periodRangeLong = m_periodRangeLong;
        this.volamult = m_volamult;
        this.periodRangeShort = m_periodRangeShort;


        MovingAverage ma = new MovingAverage(m_qh.getQh());
        ma.calculate(periodRangeLong, 0, ValueType.HighminusLow);

        rangeAvg = MyMath.arrayMultValue(ma.getMaAll(), volamult);
        rangeAvg = MyMath.round(rangeAvg, 2);

        PeriodHigh ph = new PeriodHigh(m_qh.getQh());
        ph.calculate(periodRangeShort, PriceBar.ValueType.High);
        hhv = ph.getPeriodHigh();

        PeriodLow pl = new PeriodLow(m_qh.getQh());
        pl.calculate(periodRangeShort, PriceBar.ValueType.Low);
        llv = pl.getPeriodLow();



        for (int i = 0; i < congestion.length; i++)
        {
            this.congestion[i] = false;
            rangeCurrent[i] = MyMath.round((hhv[i] - llv[i]), 2);
            PriceBar priceBar = m_qh.getQh().getPriceBar(i);
            if (rangeAvg[i] > rangeCurrent[i]
                    && priceBar.getClose() >= llv[i] && priceBar.getClose() <= hhv[i])
            //if (rangeAvg > rangeCurrent)
            {
                this.congestion[i] = true;
            }
        }


    }

    public boolean checkCongestionPeriod(int period)
    {
        boolean congestionOk = false;

        for (int i = congestion.length - period - 1; i < congestion.length; i++)
        {
            if (this.congestion[i])
            {
                congestionOk = true;
            }
        }
        return congestionOk;
    }

    public void printCongestionPeriod(int period)
    {

        for (int i = congestion.length - period - 1; i < congestion.length; i++)
        {
            congSingleToPrint2(i, "CongestionStrategy2", this.symbol);
        }

    }

    public boolean checkSupportPeriod(int period)
    {
        boolean supportbasicOk = false;

        for (int i = this.sbs.isSupportBasic().length - period - 1; i < this.sbs.isSupportBasic().length; i++)
        {
            if (this.sbs.isSupportBasic()[i])
            {
                supportbasicOk = true;
            }
        }
        return supportbasicOk;
    }

    public void printSupportPeriod(int period)
    {

        for (int i = this.sbs.isSupportBasic().length - period - 1; i < this.sbs.isSupportBasic().length; i++)
        {
            congSingleSupportBasicToPrint2(i, "CongestionStrategy2", this.symbol);
        }

    }

    private void evalLLV(int m_periodRangeShort,
            RequestQuotesHistorical m_quoteshist,
            double[] supportVal)
    {
        if (checkCongestionPeriod(10) && !checkSupportPeriod(2))
        {
            if (LOG_CONGESTION)
            {
                this.printCongestionPeriod(10);
            }
            sbs.evalSupport(m_periodRangeShort, supportVal);
        }
    }

    private void calcTradeOrder(TradeOrder to, RequestQuotesHistorical m_quoteshist)
    {
        if (checkCongestionPeriod(10) && checkSupportPeriod(2))
        {

            if (!LOG_CONGESTION)
            {
                this.printCongestionPeriod(10);
            }
            this.printSupportPeriod(2);

            if (false)
          {
            PriceBar lastPriceBar = m_quoteshist.getQh().getLastPriceBar();
            
            to.setCheckPrice(999.99);
            to.setOrderPrice(lastPriceBar.getHigh() + 0.02);
            to.setQh(m_quoteshist.getQh());
            to.Calculate();
          }
        }
    }

    private void congSingleToPrint2(final int lastindex, String methodname, String symbol)
    {
        if (this.congestion[lastindex])
        {
            String outString;
            outString = new Date() + " " + "[Info] [" + methodname + "] [" + symbol + "] "
                    + " RangeAvg :" + this.getRangeAvg()[lastindex]
                    + " RangeCurrent :" + this.getRangeCurrent()[lastindex]
                    + " High :" + this.getHhv()[lastindex]
                    + " low :" + this.getLlv()[lastindex]
                    + " Date " + this.qh.getPriceBar(lastindex).getDate()
                    + " High " + this.qh.getPriceBar(lastindex).getHigh()
                    + " Low  " + this.qh.getPriceBar(lastindex).getLow()
                    + " Close  " + this.qh.getPriceBar(lastindex).getClose();
            WriteToFile.logAll(outString, symbol);
        }
    }

    private void congSingleSupportBasicToPrint2(final int lastindex, String methodname,
            String symbol)
    {
        if (this.sbs.isSupportBasic()[lastindex])
        {
            String outString;
            outString = new Date() + " " + "[Info] [" + methodname + "] [" + symbol + "] "
                    + " last Date " + this.qh.getPriceBar(lastindex).getDate()
                    + " candle ok " + this.sbs.getSupportCandlePattern()[lastindex];
            WriteToFile.logAll(outString, symbol);
        }
    }
}
