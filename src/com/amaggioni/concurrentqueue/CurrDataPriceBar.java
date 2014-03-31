
package com.amaggioni.concurrentqueue;

import com.ib.client.EWrapperMsgGenerator;

/**
 *
 * @author magang
 */
public class CurrDataPriceBar
{
    private int tickerId;
    private int field;
    private double doubleField;
    private int canAutoExecute;
        
    public CurrDataPriceBar(int ticketId, int field, double doubleField, int canAutoExecute)
    {
        this.tickerId = ticketId;
        this.field = field;
        this.doubleField = doubleField;
        this.canAutoExecute = canAutoExecute;
    }

    /**
     * @return the tickerId
     */
    public int getTickerId()
    {
        return tickerId;
    }

    /**
     * @param tickerId the tickerId to set
     */
    public void setTickerId(int tickerId)
    {
        this.tickerId = tickerId;
    }

    /**
     * @return the field
     */
    public int getField()
    {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(int field)
    {
        this.field = field;
    }

    /**
     * @return the doubleField
     */
    public double getDoubleField()
    {
        return doubleField;
    }

    /**
     * @param doubleField the doubleField to set
     */
    public void setDoubleField(double doubleField)
    {
        this.doubleField = doubleField;
    }

    /**
     * @return the canAutoExecute
     */
    public int getCanAutoExecute()
    {
        return canAutoExecute;
    }

    /**
     * @param canAutoExecute the canAutoExecute to set
     */
    public void setCanAutoExecute(int canAutoExecute)
    {
        this.canAutoExecute = canAutoExecute;
    }
    
    @Override
    public String toString(){
        
        return EWrapperMsgGenerator.tickPrice(tickerId, field, doubleField,
                canAutoExecute);
    }
}
