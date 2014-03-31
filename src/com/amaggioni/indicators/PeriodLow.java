/* ---------------------------------------------------
 * 
 * author: Angelo Maggioni
 * 
 * created:
 * changed:
 * 
 * 
 * change log:
 * 15.05.2013 added 'external value'
 * 
 * 
 */
package com.amaggioni.indicators;

import core.PriceBar;
import java.text.DecimalFormat;

public class PeriodLow extends Indicator
{

    //private QuoteHistory qh;
    private double[] periodLow;
    private double[] externalvalue;

    public PeriodLow(QuoteHistory qh)
    {
        this.qh = qh;
        this.periodLow = new double[qh.getSize()];
    }
    
    public PeriodLow(double[] externalvalue)
    {
        this.externalvalue = externalvalue;
        this.periodLow = new double[this.externalvalue.length];
    }

    public double calculate(int period, PriceBar.ValueType vt)
    {
        final int size = this.periodLow == null ? qh.size() : this.periodLow.length;
        //System.out.println(" Size :" + size + " period :" + period);
        period = period > size ? size : period;
        
        for (int i = 1; i < (size - period + 1); i++)
        {
            int periodStart = size - period - (i - 1);
            int periodEnd = size - i;
            double low = getLocalValue(periodStart, vt);

            for (int bar = periodStart; bar <= periodEnd; bar++)
            {
                double barLow = getLocalValue(bar, vt);
                if (barLow < low)
                {
                    low = barLow;
                }
            }

            periodLow[size - i] = low;
        }

        return periodLow[size - 1];
    }

    /**
     * @return the periodLow
     */
    public double[] getPeriodLow()
    {
        return periodLow;
    }
    
    public double getperiodLowIndex(int index)
    {

        return periodLow[qh.size() - 1 + index];
    }
    
    public String printLows(int period, PriceBar.ValueType vt)
    {
        StringBuilder sb = new StringBuilder();
        DecimalFormat df =   new DecimalFormat  ( "00.00" );
        
        for (int i = 1; i <= period; i++)
        {
            sb.append("Index :" );
            sb.append((qh.size() - i));
            sb.append(" value ");
            sb.append(qh.getPriceBar(qh.size() - i).getValue(vt));
            sb.append(" low ");
            sb.append(df.format(this.periodLow[(qh.size() - i)])).append("\n");
        }
        
        return sb.toString();
    }

    private double getLocalValue(int a, PriceBar.ValueType vt)
    {
        double localValue = 0.0;
        if (this.externalvalue == null)
        {
            PriceBar pb = qh.getPriceBar(a);
            localValue = pb.getValue(vt);
        } 
       
        if (this.externalvalue != null)
        {
            localValue = this.externalvalue[a];
        }
        
        return localValue;
    }
  @Override
  public double calculate()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
