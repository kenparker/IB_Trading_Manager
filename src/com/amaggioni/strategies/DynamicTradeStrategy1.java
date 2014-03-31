package com.amaggioni.strategies;

import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import core.PriceBar;



public class DynamicTradeStrategy1
{


    /**
     * ---------------------------------------------------------------
     * <p/>
     * M A I N Method
     * <p/>
     * <
          */
    public static void evalStrategy(TradeOrder to,
            RequestQuotesHistorical m_qh
           )
    {
        
        calcTradeOrder(to, m_qh);

    }

   
    

   

    private static void calcTradeOrder(TradeOrder to, RequestQuotesHistorical m_quoteshist)
    {
        PriceBar lastPriceBar = m_quoteshist.getQh().getLastPriceBar();

            if (lastPriceBar.getLow() <= to.getCheckPrice()
                    && to.getCheckPrice() != 0 && to.getOrderPrice() != 0)
          {
            
            to.setQh(m_quoteshist.getQh());
            to.Calculate();
          }
       
    }

    

}
