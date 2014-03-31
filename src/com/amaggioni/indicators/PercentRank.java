package com.amaggioni.indicators;

import core.PriceBar;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 18.05.2013
 * change date : xx.xx.2013
 *
 * description:
 *
 * flow:
 * <p/>
 *
 * Change Log:
 * 18.05.2013 First Version
 * <p/>
 */
public class PercentRank extends Indicator
{

    private int period; // Absolute Index
    private int qhsize;
    private double[] externalvalue;
    private double[] percrank = null;
    private PriceBar.ValueType vt;

    public PercentRank(QuoteHistory qh)
    {
        super(qh);
        qhsize = qh.size();
        this.percrank = new double[qhsize];
    }
    
    public PercentRank(double[] externalvalue)
    {
        this.externalvalue = externalvalue;
        this.percrank  = new double[this.externalvalue.length];
    }

    @Override
    public double calculate()
    {
               if (this.getPeriod() > this.percrank.length){
                   this.setPeriod(this.percrank.length);
               } 
        for (int j = this.getPeriod()-1; j < this.percrank.length; j++)
        {
            int Count = 0;
            for (int i = 1; i < this.getPeriod(); i++)
            {
                Count = Count + (getLocalValue(j, vt)
                        > getLocalValue(j-i, vt) ? 1 : 0);
            }

            this.getPercrank()[j] = 100 * Count / this.getPeriod();
        }

        return this.getPercrank()[this.percrank.length-1];
    }

    /**
     * @return the period
     */
    public int getPeriod()
    {
        return period;
    }

    /**
     * @param period the period to set
     */
    public void setPeriod(int period)
    {
        this.period = period;
    }

    /**
     * @return the percrank
     */
    public double[] getPercrank()
    {
        return percrank;
    }

    /**
     * @param percrank the percrank to set
     */
    public void setPercrank_delete(double[] percrank)
    {
        this.percrank = percrank;
    }

    /**
     * @return the vt
     */
    public PriceBar.ValueType getVt()
    {
        return vt;
    }

    /**
     * @param vt the vt to set
     */
    public void setVt(PriceBar.ValueType vt)
    {
        this.vt = vt;
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
}
