package com.amaggioni.strategies;

import com.amaggioni.candles.CandlePattern;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.indicators.QuoteHistory;

/**
 * ***********************************************************************************************
 *
 * @author magang create date : 17.04.2013 change date : 17.04.2013
 *
 * description:
 *
 * flow:
 * <p/>
 * Change Log: 17.04.2013 first version 18.04.2013 arrays added
 *
 */
public class SupportBullBasicStrategy1
{

    private boolean[] supportBasic;
    private String[] supportCandlePattern;
    private QuoteHistory qh;

    public SupportBullBasicStrategy1(RequestQuotesHistorical m_quoteshist)
    {
        int size = m_quoteshist.getQh().getSize();
        supportBasic = new boolean[size];
        this.qh = m_quoteshist.getQh();
        this.supportCandlePattern = new String[size];
    }

    public void evalSupport(int m_periodRangeShort, double[] supportVal)
    {
        
        CandlePattern cp = new CandlePattern(qh);
        cp.calculate(m_periodRangeShort);

        for (int i = m_periodRangeShort; i < supportVal.length; i++)
        {


            if (qh.getPriceBar(i).getLow() <= supportVal[i]
                    || qh.getPriceBar(i - 1).getLow() <= supportVal[i]
                    || qh.getPriceBar(i - 2).getLow() <= supportVal[i])
            {

                this.supportBasic[i] = false;
                this.supportCandlePattern[i] = cp.isBullishReversalCandle2(i);
                if (!supportCandlePattern[i].equals(""))
                {
                    this.supportBasic[i] = true;
                }
            }
        }
    }

    /**
     * @return the supportBasic
     */
    public boolean[] isSupportBasic()
    {
        return supportBasic;
    }

    /**
     * @return the supportCandlePattern
     */
    public String[] getSupportCandlePattern()
    {
        return supportCandlePattern;
    }

   
}
