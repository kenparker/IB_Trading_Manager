package com.amaggioni.datastructure;

import core.PriceBar;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 03.01.2013
 * change date : 11.03.2013
 *
 * Change Log:
 * 11.03.2013 add 'PriceBar'
 * 11.03.2013 refactoring
 *
 */
public class RequestData
{

    private String symbol;
    //private double lastPrice;
    //private double volume;
    private PriceBar pb;

      
    public RequestData(String symbol)
    {
        this.symbol = symbol;
        this.pb = new PriceBar();
    }
    
    /*public RequestData(String symbol, double lastPrice, double volume)
    {
        this.symbol = symbol;
        this.lastPrice = lastPrice;
        this.volume = volume;
    }*/

    
        
    public double getLow()
    {
        return this.pb.getLow();
    }
    
    public double getClose()
    {
        return this.pb.getClose();
    }

    
    public void setClose(double close)
    {
        this.pb.setClose(close);
    }
        
    public void setLow(double low)
    {
        this.pb.setLow(low);
    }
    
    public void setHigh(double high)
    {
        this.pb.setHigh(high);
    }

    /**
     * @return the volume
     */
    public double getVolume()
    {
        return this.pb.getVolume();
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(double volume)
    {
        this.pb.setVolume(volume);
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
     * @return the pb
     */
    public PriceBar getPb()
    {
        return pb;
    }

    /**
     * @param pb the pb to set
     */
    public void setPb(PriceBar pb)
    {
        this.pb = pb;
    }
}