/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.strategies;

import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.indicators.ADX;
import com.amaggioni.indicators.PeriodHigh;
import com.amaggioni.indicators.PeriodLow;
import core.PriceBar;

/**
 *
 * @author magang
 */
public class ChannelStrategy
{

    private int periodADX;
    private int periodHL;
    private double adx;
    private double high;
    private double low;

    public ChannelStrategy()
    {

        this.periodADX = 0;
        this.periodHL = 0;
    }

    public boolean evalStrategy(int m_peradx, int m_perhl,
            TradeOrder m_symboldata,
            RequestQuotesHistorical m_quoteshist)
    {

        this.periodADX = m_peradx;
        this.periodHL = m_perhl;

        ADX adxinst = new ADX(m_quoteshist.getQh(), periodADX);
        this.adx = adxinst.calculate();

        if (this.getAdx() < 20 && m_symboldata.getCheckPrice() == 0)
        {

            PeriodHigh ph = new PeriodHigh(m_quoteshist.getQh());
            this.high = ph.calculate(periodHL, PriceBar.ValueType.High);

            PeriodLow pl = new PeriodLow(m_quoteshist.getQh());
            this.low = pl.calculate(periodHL, PriceBar.ValueType.Low);
            System.out.println(pl.printLows(periodHL, PriceBar.ValueType.Low));

            if (getHigh() != 0 && getLow() != 0)
            {
                double checkprice = Math.round(((getHigh() - getLow()) / 2 + getLow()) * 100) / 100.0;

                m_symboldata.setCheckPrice(checkprice);
                m_symboldata.setOrderPrice(getLow());
                m_symboldata.Calculate();

            } else
            {
            }

        }


        return true;
    }

    /**
     * @return the adx
     */
    public double getAdx()
    {
        return adx;
    }

    /**
     * @return the high
     */
    public double getHigh()
    {
        return high;
    }

    /**
     * @return the low
     */
    public double getLow()
    {
        return low;
    }
}
