package com.amaggioni.strategies;

import com.amaggioni.datastructure.RequestHistoricalMktDta;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.indicators.StandardErrorBands;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 12.07.2013
 * change date :
 *
 * description: ...
 *
 * flow:
 * <p/>
 *
 * Change Log:
 * 12.07.2013 first Version
 * <p/>
 */
public class StandardErrorBandsStrategy1
{

    /**
     * ---------------------------------------------------------------
     * <p/>
     * M A I N Method
     * <p/>
     * <
     */
    public static void evalStrategy(RequestHistoricalMktDta histreq, TradeOrder to,
            RequestQuotesHistorical m_qh, int par)
    {

        StandardErrorBands seb = new StandardErrorBands(m_qh.getQh());
        seb.setSymbol(histreq.getSymbol());
        seb.setBarsize(histreq.getBarsize());
        seb.setLookbackperiod(par);
        seb.setBandfactor(2);
        seb.setSmoothingperiod(5);
        seb.calculate();

        seb.checkPeriod(10);
        seb.checkPeriod3(10);
        //seb.printPeriod(10);

    }
}
