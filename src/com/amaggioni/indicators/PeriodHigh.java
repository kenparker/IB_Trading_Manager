/**---------------------------
 * 
 * Author: Angelo Maggioni
 * created: 
 * changed:
 * 
 * change log:
 * 
 */
package com.amaggioni.indicators;

import core.PriceBar;
import core.PriceBar.ValueType;

public class PeriodHigh extends Indicator
{

    //private QuoteHistory qh;
    private double[] periodHigh;

    public PeriodHigh(QuoteHistory qh)
    {
        this.qh = qh;
        this.periodHigh = new double[qh.getSize()];
    }

    public double calculate(int period, PriceBar.ValueType vt)
    {
        for (int i = 1; i < (qh.size() - period + 1); i++)
        {
            int periodStart = qh.size() - period - (i -1);
            int periodEnd = qh.size() - i;
            double high = qh.getPriceBar(periodStart).getValue(vt);

            for (int bar = periodStart; bar <= periodEnd; bar++)
            {
                double barHigh = qh.getPriceBar(bar).getValue(vt);
                if (barHigh > high)
                {
                    high = barHigh;
                }
            }

            periodHigh[qh.size() - i] = high;
        }
        
        return periodHigh[qh.size() - 1];
    }

    /**
     * @return the periodHigh
     */
    public double[] getPeriodHigh()
    {
        return periodHigh;
    }

    /**
     * @param periodHigh the periodHigh to set
     */
    public void setPeriodHigh(double[] periodHigh)
    {
        this.periodHigh = periodHigh;
    }

    public double getperiodHighIndex(int index)
    {

        return periodHigh[qh.size() - 1 + index];
    }
    
    public String printAll(ValueType vt) {
       
          StringBuilder sb = new StringBuilder();
          
        for (int i = 0; i < qh.size(); i++) {
            
            sb.append("Index :");
            sb.append(i);
            sb.append(" Value ");
            sb.append(qh.getPriceBar(i).getValue(vt));
            sb.append(" High ");
            sb.append(this.periodHigh[i]);
            sb.append("\n");
          
        }
        
        return sb.toString();
    }

  @Override
  public double calculate()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
    
}
