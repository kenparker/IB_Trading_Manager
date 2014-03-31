package com.amaggioni.strategies;

import com.amaggioni.datastructure.RequestHistoricalMktDta;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.indicators.AutoEnvelope;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 31.05.2013
 * change date : 
 *
 * description: The class manage a trading strategy for multiple symbols
 *
 * flow:
 
 *
 * Change Log:
 * 31.05.2013 first Version
 
 */
public class AutoEnvelopeStrategy1 {
/**
     * ---------------------------------------------------------------
     * <p/>
     * M A I N Method
     * <p/>
     * <
          */
    public static void evalStrategy(RequestHistoricalMktDta histreq, 
            TradeOrder to, 
            RequestQuotesHistorical m_qh, 
            int lookbackperiod)
    {
        
        AutoEnvelope envelope = new AutoEnvelope(m_qh.getQh());
        envelope.setSymbol(histreq.getSymbol());
        envelope.setBarsize(histreq.getBarsize());
        envelope.setLookbackperiod(lookbackperiod);
        envelope.calculate();
        
        //envelope.logEnvelFactor(0);
        envelope.checkPeriod(10);

    }
}
