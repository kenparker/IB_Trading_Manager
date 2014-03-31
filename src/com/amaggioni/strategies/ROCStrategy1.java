package com.amaggioni.strategies;

import com.amaggioni.datastructure.RequestHistoricalMktDta;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.indicators.PercentRank;
import core.PriceBar;
import com.amaggioni.indicators.QuoteHistory;
import com.amaggioni.indicators.ROC;
import com.amaggioni.myutilities.MyMath;
import com.amaggioni.myutilities.WriteToFile;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 07.06.2013
 * change date :
 *
 * description:
 *
 * flow:
 * <p/>
 *
 * Change Log:
 * 07.06.2013 first Version
 * <p/>
 */
public class ROCStrategy1
{

    private int lookbackPeriod;
    private ROC roc;
    private double[] ROCRank;
    private boolean[] ROCFlag;
    private boolean LOG_ROCLow = true;
        
    private QuoteHistory qh;

    public ROCStrategy1(RequestQuotesHistorical m_qh){
        
        final int qhsize = m_qh.getQh().getSize();
        this.ROCFlag = new boolean[qhsize];
        this.ROCRank = new double[qhsize];
  
        this.qh = m_qh.getQh();

        this.roc = new ROC(this.qh);
        
        
    }
    
    //<editor-fold defaultstate="collapsed" desc="Getter Setter">
    /**
     * Get the value of ROCFlag
     *
     * @return the value of ROCFlag
     */
    public boolean[] isROCFlag()
    {
        return ROCFlag;
    }
    
    /**
     * Set the value of ROCFlag
     *
     * @param ROCFlag new value of ROCFlag
     */
    public void setROCFlag(boolean[] ROCFlag)
    {
        this.ROCFlag = ROCFlag;
    }
    
    /**
     * Get the value of ROCFlag at specified index
     *
     * @param index
     * @return the value of ROCFlag at specified index
     */
    public boolean isROCFlag(int index)
    {
        return this.ROCFlag[index];
    }
    
    /**
     * Set the value of ROCFlag at specified index.
     *
     * @param index
     * @param newROCFlag new value of ROCFlag at specified index
     */
    public void setROCFlag(int index, boolean newROCFlag)
    {
        this.ROCFlag[index] = newROCFlag;
    }
    
    /**
     * Get the value of ROCRank
     *
     * @return the value of ROCRank
     */
    public double[] getROCRank()
    {
        return ROCRank;
    }
    
    /**
     * Set the value of ROCRank
     *
     * @param ROCRank new value of ROCRank
     */
    public void setROCRank(double[] ROCRank)
    {
        this.ROCRank = ROCRank;
    }
    
    /**
     * Get the value of ROCRank at specified index
     *
     * @param index
     * @return the value of ROCRank at specified index
     */
    public double getROCRank(int index)
    {
        return this.ROCRank[index];
    }
    
    /**
     * Set the value of ROCRank at specified index.
     *
     * @param index
     * @param newROCRank new value of ROCRank at specified index
     */
    public void setROCRank(int index, double newROCRank)
    {
        this.ROCRank[index] = newROCRank;
    }
    //</editor-fold>

    /**
     * ---------------------------------------------------------------
     * <p/>
     * M A I N Method
     * <p/>
     * <
     */
    public void evalStrategy(RequestHistoricalMktDta histreq, TradeOrder to,
            RequestQuotesHistorical m_qh)
    {

        //ROC roc = new ROC(m_qh.getQh());
        roc.setSymbol(histreq.getSymbol());
        roc.setBarsize(histreq.getBarsize());
        roc.setPeriod(80);
        roc.calculate();

        PercentRank pr = new PercentRank(roc.getROC());
        pr.setPeriod(this.lookbackPeriod); // Relative Index needed
        pr.setVt(PriceBar.ValueType.Other);
        pr.calculate();
        ROCRank = pr.getPercrank();

        for (int i = 5; i < ROCFlag.length; i++) {
            ROCFlag[i] = this.ROCRank[i] <= 10 || this.ROCRank[i] >= 90;
        }
        
        if (LOG_ROCLow)
            {
                this.printROCLow(0);
            }

    }

    public void printROCLow(int period)
    {

        for (int i = ROCFlag.length - period - 1; i < ROCFlag.length; i++)
        {
            ROClowSingleToPrint2_1(i, "ROCStrategy1");
        }
        

    }
    
    private void ROClowSingleToPrint2_1(final int lastindex, String methodname)
    {
        if (this.ROCFlag[lastindex])
        {
            
            
            String outString = WriteToFile.msgHeaderBld("Info", methodname, 
                    roc.getSymbol(), 
                    roc.getBarsize(),
                    this.getLookbackPeriod());
           
            outString = outString 
                    + " Lookbackdate :" + this.qh.getLookbackDate(lastindex-lookbackPeriod)
                   // + " Lookbackdate Nr :" + lookbackPeriod
                   // + " ROC Flag :" + (this.ROCFlag[lastindex] ? " True " : " false ")
                    + " ROC :" + MyMath.round(this.roc.getROC()[lastindex], 3)
                    + " ROC rank :" + MyMath.round(this.getROCRank()[lastindex], 3)
                    + this.qh.toStringOneBar(lastindex);
            
            WriteToFile.logAll(outString, roc.getSymbol());
        }

        
    }
    public boolean checkROCLowPeriod(int period)
    {
        boolean ROCLowOk = false;

        for (int i = ROCFlag.length - period - 1; i < ROCFlag.length; i++)
        {
            ROCLowOk = (this.ROCFlag[i] ? true : ROCLowOk);
        }

        return ROCLowOk;
    }
    /**
     * Get the value of lookbackPeriod
     *
     * @return the value of lookbackPeriod
     */
    public int getLookbackPeriod()
    {
        return lookbackPeriod;
    }

    /**
     * Set the value of lookbackPeriod
     *
     * @param lookbackPeriod new value of lookbackPeriod
     */
    public void setLookbackPeriod(int LookbackPeriod)
    {
        this.lookbackPeriod = LookbackPeriod;
    }

    
    
}
