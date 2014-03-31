package com.amaggioni.indicators;

import core.PriceBar;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Humberto Rocha Loureiro (humbertorocha@gmail.com)
 * @modify:
 */
/**
 * Holds and validates the priceBar history for a strategy.
 */
public class QuoteHistory
{

    //private PriceBar nextBar;
    private List<PriceBar> priceBars = new ArrayList<PriceBar>();

    //private final String strategyName;

    /*public QuoteHistory(String strategyName) {
     this.strategyName = strategyName;
     }*/
    public QuoteHistory()
    {
    }

    public QuoteHistory(List<PriceBar> pb)
    {
        priceBars.addAll(pb);
    }

    public void addHistoricalPriceBar(PriceBar priceBar)
    {
        if (!existPriceBar(priceBar.getDate()))
        {
            priceBars.add(priceBar);
        }
    }
    
    public void delLastPriceBar()
    {
        
            priceBars.remove(this.priceBars.size()-1);
        
    }

    private boolean existPriceBar(String date)
    {
        boolean result = false;
        /*for (PriceBar price : priceBars) {
         if (price.getDate() == date) {
         result = true;
         }
         }*/
        return result;
    }

    public List<PriceBar> getAll()
    {
        return priceBars;
    }

    public PriceBar getFirstPriceBar()
    {
        return priceBars.get(0);
    }

    public PriceBar getLastPriceBar()
    {
        return priceBars.get(priceBars.size() - 1);
    }

    public PriceBar getPriceBar(int index)
    {
        return priceBars.get(index);
    }

    public int getSize()
    {
        return priceBars.size();
    }

    /*public String getStrategyName() {
     return strategyName;
     }*/
    public boolean isValid()
    {
        // todo: da fare
        return false;
    }

    public int size()
    {
        return priceBars.size();
    }

    public List<PriceBar> getPriceBars()
    {
        return priceBars;
    }

    public void setPriceBars(
            List<PriceBar> priceBars)
    {
        this.priceBars = priceBars;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (PriceBar priceBar : priceBars)
        {
            sb.append("Date :");
            sb.append(priceBar.getDate());
            sb.append(" Close :");
            sb.append(priceBar.getClose());
            sb.append(" High :");
            sb.append(priceBar.getHigh());
            sb.append(" Low :");
            sb.append(priceBar.getLow());
            sb.append("\n");
        }

        return sb.toString();
    }
    
    public String toStringOneBar(int index)
    {
        StringBuilder sb = new StringBuilder();
        
            sb.append(" Date :");
            sb.append(this.getPriceBar(index).getDate());
            sb.append(" Close :");
            sb.append(this.getPriceBar(index).getClose());
            sb.append(" High :");
            sb.append(this.getPriceBar(index).getHigh());
            sb.append(" Low :");
            sb.append(this.getPriceBar(index).getLow());
            
        

        return sb.toString();
    }

    public String getLookbackDate(int lookback){
        return this.getPriceBar(lookback).getDate();
    }
    
    /*public synchronized void update(double open, double high, double low,
     double close, long volume) {
     if (nextBar == null) {
     nextBar = new PriceBar(open, high, low, close, volume);
     } else {
     nextBar.setClose(close);
     nextBar.setLow(Math.min(low, nextBar.getLow()));
     nextBar.setHigh(Math.max(high, nextBar.getHigh()));
     nextBar.setVolume(nextBar.getVolume() + volume);
     }

     }*/
}
