package com.amaggioni.indicators;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Humberto Rocha Loureiro (humbertorocha@gmail.com)
 * @modify:
 */
/**
 * Base class for all classes implementing technical indicators.
 */
public abstract class Indicator
{

    protected double value;
    protected QuoteHistory qh;
    private List<IndicatorValue> indicatorHistory;
    //protected Indicator parent;

    public Indicator()
    {
        indicatorHistory = new ArrayList<IndicatorValue>();
    }

    /*public Indicator(Indicator parent) {
     this();
     this.parent = parent;
     }*/
    public Indicator(QuoteHistory qh)
    {
        this();
        this.qh = qh;
    }

    public void addToHistory(String date, double value) {
     indicatorHistory.add(new IndicatorValue(date, value));
     }
    
    public abstract double calculate();// must be implemented in subclasses.

    /*public long getDate() {
     if (qh != null) {
     return qh.getLastPriceBar().getDate();
     } else {
     List<IndicatorValue> parentHistory = parent.getHistory();
     return parentHistory.get(parentHistory.size() - 1).getDate();
     }
     }*/
    public List<IndicatorValue> getIndicatorHistory()
    {
        return indicatorHistory;
    }

    public double getValue()
    {
        return value;
    }

    public void init(QuoteHistory qh)
    {
        this.qh = qh;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" value: ").append(value);
        return sb.toString();
    }
}
