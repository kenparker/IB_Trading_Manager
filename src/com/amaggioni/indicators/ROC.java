package com.amaggioni.indicators;

import core.PriceBar;
import core.PriceBar.ValueType;
import com.amaggioni.myutilities.MyMath;
import com.amaggioni.myutilities.WriteToFile;
import java.util.Date;
import java.util.List;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 06.06.2013
 * change date :
 *
 * d
 * Change Log:
 * <p/>
 * 06.06.2013 first Version
 * <p/>
 */
public class ROC extends Indicator
{

    private int size;
    private int period;
    private String symbol;
    private String barsize;
    private ValueType valuetype;
    private double[] ROC;
    private List<IndicatorValue> externalindicator;
    private double[] externalvalue;

    public ROC(QuoteHistory qh)
    {
        this(qh.getSize());

        this.qh = qh;
    }

    public ROC(List<IndicatorValue> m_externalindicator)
    {
        this(m_externalindicator.size());
        this.externalindicator = m_externalindicator;
    }

    public ROC(double[] m_externalvalue)
    {
        this(m_externalvalue.length);
        this.externalvalue = m_externalvalue;
    }

    public ROC(int size)
    {

        this.size = size;
        this.ROC = new double[this.size];
        this.period = 100;
        this.valuetype = ValueType.Close;
        this.symbol = "";
        this.barsize = "";
    }

    @Override
    public double calculate()
    {

        if (this.size - 2 < this.getPeriod()) {
            return 0.0;
        }

        for (int i = this.getPeriod(); i < size; i++) {

            this.getROC()[i] = this.getLocalValue(i, this.getValuetype()) 
                    - this.getLocalValue(i-this.getPeriod(), this.getValuetype());
        }

        return 0.0;
    }

    private int calcIndex(int in)
    {
        int index = size - in - 1;
        return index;
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
     * @return the valuetype
     */
    public ValueType getValuetype()
    {
        return valuetype;
    }

    /**
     * @param valuetype the valuetype to set
     */
    public void setValuetype(ValueType valuetype)
    {
        this.valuetype = valuetype;
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
     * @return the ROC
     */
    public double[] getROC()
    {
        return ROC;
    }

    
    private double getLocalValue(int a, ValueType vt)
    {
        double localValue = 0.0;
        if (this.externalindicator == null && this.externalvalue == null) {
            PriceBar pb = qh.getPriceBar(a);
            localValue = pb.getValue(vt);
        }

        if (this.externalindicator != null && this.externalvalue == null) {
            localValue = this.externalindicator.get(a).getValue();
        }

        if (this.externalindicator == null && this.externalvalue != null) {
            localValue = this.externalvalue[a];
        }

        return localValue;
    }
}
