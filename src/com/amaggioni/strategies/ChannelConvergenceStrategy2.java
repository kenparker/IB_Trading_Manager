package com.amaggioni.strategies;

// http://blogs.stockcharts.com/scanning/2011/08/scan-coding-sample-blocks.html?st=channel+convergence+scan
import com.amaggioni.datastructure.RequestData;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.indicators.PeriodHigh;
import com.amaggioni.indicators.PeriodLow;
import core.PriceBar;
import com.amaggioni.indicators.QuoteHistory;
import com.amaggioni.myutilities.WriteToFile;
import java.util.Date;

public class ChannelConvergenceStrategy2
{

    private int period;
    private double[] highperiod;
    private double[] lowperiod;
    private boolean[] convergence;
    private String symbol;
    private SupportBullBasicStrategy1 sbs;
    QuoteHistory qh;
    private boolean LOG_CONVERGENCE = false;

    public ChannelConvergenceStrategy2(RequestQuotesHistorical m_quoteshist)
    {

        this.period = 0;
        final int size = m_quoteshist.getQh().size();
        this.highperiod = new double[size];
        this.lowperiod = new double[size];

        this.convergence = new boolean[size];
        this.qh = m_quoteshist.getQh();
        this.symbol = m_quoteshist.getSymbol();
        this.sbs = new SupportBullBasicStrategy1(m_quoteshist);
    }

    public void evalStrategy(int m_periodRange,
            TradeOrder to,
            RequestQuotesHistorical m_qh,
            RequestData m_rd)
    {
        evalConvergence(m_periodRange, m_qh, m_rd);
        evalLLV(m_periodRange, m_qh, this.lowperiod);
        calcTradeOrder(to, m_qh);

    }

    public void evalConvergence(int m_period,
            RequestQuotesHistorical m_quoteshist,
            RequestData m_requestdata)
    {

        this.period = m_period;

        PeriodHigh ph = new PeriodHigh(this.qh);
        ph.calculate(period, PriceBar.ValueType.Close);
        this.setHighperiod(ph.getPeriodHigh());

        PeriodLow pl = new PeriodLow(this.qh);
        pl.calculate(period, PriceBar.ValueType.Close);
        this.setLowperiod(pl.getPeriodLow());

        for (int i = 0; i < qh.size(); i++)
        {

            final double close = this.qh.getPriceBar(i).getClose();
            if (close * 0.99 < this.lowperiod[i]
                    && close * 1.01 > this.highperiod[i])
            {
                this.convergence[i] = true;
            }
        }
    }

    private void evalLLV(int m_periodRangeShort,
            RequestQuotesHistorical m_quoteshist,
            double[] supportVal)
    {
        if (checkConvergencePeriod(10) && !checkSupportPeriod(2))
        {
            if (LOG_CONVERGENCE)
            {
                this.printCongestionPeriod(10);
            }
            sbs.evalSupport(m_periodRangeShort, supportVal);
        }
    }

    public boolean checkConvergencePeriod(int period)
    {
        boolean congestionOk = false;

        for (int i = this.convergence.length - period - 1; i < this.convergence.length; i++)
        {
            if (this.convergence[i])
            {
                congestionOk = true;
            }
        }
        return congestionOk;
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
            convSingleSupportBasicToPrint2(i, "ConvergenceStrategy2", this.symbol);
        }

    }

    public void printCongestionPeriod(int period)
    {

        for (int i = this.convergence.length - period - 1; i < this.convergence.length; i++)
        {
            convSingleToPrint2(i, "ConvergenceStrategy2", this.symbol);
        }

    }

    private void calcTradeOrder(TradeOrder to, RequestQuotesHistorical m_quoteshist)
    {
        if (checkConvergencePeriod(10) && checkSupportPeriod(2))
        {

            if (!LOG_CONVERGENCE)
            {
                this.printCongestionPeriod(10);
            }
            this.printSupportPeriod(2);

            if (false) {
                PriceBar lastPriceBar = m_quoteshist.getQh().getLastPriceBar();
                
                to.setCheckPrice(999.99);
                to.setOrderPrice(lastPriceBar.getHigh());
                to.setQh(m_quoteshist.getQh());
                to.Calculate();
            }
        }
    }

    private void convSingleToPrint2(final int lastindex, String methodname, String symbol)
    {
        if (this.convergence[lastindex])
        {
            String outString;
            outString = new Date() + " " + "[Info] [" + methodname + "] [" + symbol + "] "
                    + " Period High :" + this.getHighperiod()[lastindex]
                    + " Period Low :" + this.getLowperiod()[lastindex]
                    + " Date " + this.qh.getPriceBar(lastindex).getDate()
                    + " High " + this.qh.getPriceBar(lastindex).getHigh()
                    + " Low  " + this.qh.getPriceBar(lastindex).getLow()
                    + " Close  " + this.qh.getPriceBar(lastindex).getClose();
            WriteToFile.logAll(outString, symbol);
        }
    }

    private void convSingleSupportBasicToPrint2(final int lastindex, String methodname,
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

    //<editor-fold defaultstate="collapsed" desc="Getter und Setter">
    /**
     * @return the highperiod
     */
    public double[] getHighperiod()
    {
        return highperiod;
    }

    /**
     * @param highperiod the highperiod to set
     */
    public void setHighperiod(double[] highperiod)
    {
        this.highperiod = highperiod;
    }

    /**
     * @return the lowperiod
     */
    public double[] getLowperiod()
    {
        return lowperiod;
    }

    /**
     * @param lowperiod the lowperiod to set
     */
    public void setLowperiod(double[] lowperiod)
    {
        this.lowperiod = lowperiod;
    }

    /**
     * @return the convergence
     */
    public boolean[] getConvergence()
    {
        return convergence;
    }

    /**
     * @param convergence the convergence to set
     */
    public void setConvergence(boolean[] convergence)
    {
        this.convergence = convergence;
    }
    //</editor-fold>
}
