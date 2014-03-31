package com.amaggioni.indicators;

import core.PriceBar;
import core.PriceBar.ValueType;
import java.util.List;

/**
 *
 * @author magang
 */
public class MovingAverage extends Indicator
{

    private int localsize;
    // private QuoteHistory qh;
    private double[] ma;
    private List<IndicatorValue> externalindicator;
    private double[] externalvalue;

    public MovingAverage(QuoteHistory qh)
    {
        this.qh = qh;
        this.localsize = qh.getSize();
        this.ma = new double[this.localsize];
    }

    public MovingAverage(List<IndicatorValue> m_externalindicator)
    {
        this.externalindicator = m_externalindicator;
        this.localsize = externalindicator.size();
        this.ma = new double[this.localsize];
    }

    public MovingAverage(double[] m_externalvalue)
    {
        this.externalvalue = m_externalvalue;
        this.localsize = m_externalvalue.length;
        this.ma = new double[this.localsize];
    }

    public double calculate(int period, int maType, ValueType vt)
    {
        if (maType == 0) // Type = 0 -> moving average
        {
            //<editor-fold defaultstate="collapsed" desc="Moving Average">
            if (period < this.localsize)
            {
                double ma_value = 0.00000;
                for (int a = 0; a < period; a++)
                {
                    ma_value += getLocalValue(a, vt);
                    ma[a] = ma_value / (a + 1);
                }
                //ma[period - 1] = ma_value / period;
                int index = period;
                while (index < this.localsize)
                {
                    ma_value += getLocalValue(index, vt);
                    ma_value -= getLocalValue(index - period, vt);
                    ma[index++] = ma_value / period;
                }
            } else
            {
                for (int a = 0; a < this.localsize; a++)
                {
                    ma[a] = 0.00000;
                }
            }
            //</editor-fold>
        } else // Type = 1 -> Exponential moving average
        {
            //<editor-fold defaultstate="collapsed" desc="Exponential Moving Average">
            if (period < this.localsize)
            {
                double ma_value = 0.00000;
                ma_value += getLocalValue(0, vt);
                ma[0] = ma_value;

                // X = (K x (C - P)) + P (X = Current EMA, C = Current Price, P = Previous period's EMA*, K = Smoothing constant)
                int index = 1;
                double K = 2 / (double) (1 + period);
                while (index < this.localsize)
                {
                    double C = getLocalValue(index, vt);
                    double P = ma[index - 1];
                    //System.out.println("K=" + K + " C=" + C + " P=" + P + " days=" + days);
                    ma[index++] = (K * (C - P)) + P;
                }
            }
            //</editor-fold>
        }

        return getMaIndex(0);
    }

    /**
     * @return the ma
     */
    public double[] getMaAll()
    {
        return ma;
    }

    public double getMaIndex(int index)
    {

        return Math.round(getMaAll()[this.localsize - 1 + index] * 100) / 100.0;
    }

    @Override
    public double calculate()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private double getLocalValue(int a, ValueType vt)
    {
        double localValue = 0.0;
        if (this.externalindicator == null && this.externalvalue == null)
        {
            PriceBar pb = qh.getPriceBar(a);
            localValue = pb.getValue(vt);
        } 
        
        if (this.externalindicator != null && this.externalvalue == null)
        {
            localValue = this.externalindicator.get(a).getValue();
        }
        
        if (this.externalindicator == null && this.externalvalue != null)
        {
            localValue = this.externalvalue[a];
        }
        
        return localValue;
    }

    /**
     * @param ma the ma to set
     */
    public void setExternalValue(double[] externalvalue)
    {
        this.externalvalue = externalvalue;
    }
}
