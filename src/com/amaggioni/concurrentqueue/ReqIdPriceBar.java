/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.concurrentqueue;

import core.PriceBar;

/**
 *
 * @author magang
 */
public class ReqIdPriceBar extends PriceBar
{

    private int requestId;

    public ReqIdPriceBar()
    {
        
    }
    
    // Date, open, close, high, low, WAP, count, volume
    public ReqIdPriceBar(int RqId, String Date, double open, double close, double high, double low, 
                                                double WAP, int count, double volume)
    {
        super(Date, open, close, high, low, WAP, count, volume);
        this.requestId = RqId;
    }

    /**
     * @return the requestId
     */
    public int getRequestId()
    {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(int requestId)
    {
        this.requestId = requestId;
    }
    
    @Override
    public String toString(){
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("ReqId : ");
        sb.append(this.requestId);
        sb.append(" Date : ");
        sb.append(this.getDate());
        sb.append(" Close : ");
        sb.append(this.getClose());
        sb.append(" High : ");
        sb.append(this.getHigh());
        sb.append(" Low : ");
        sb.append(this.getLow());
        sb.append(" Volume : ");
        sb.append(this.getVolume());
        
        
        return sb.toString();
    }
}
