/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.myutilities;

import Entities.Stock;
import Service.StockService;
import com.amaggioni.datastructure.RequestHistoricalMktDta;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.datastructure.TradeOrder;
import core.PriceBar;
import java.util.Iterator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 30.07.2013
 * change date :
 *
 * description: 
 *
 * flow:
 * <p/>
 *
 * Change Log:
 * 30.07.2013 First version
 * 07.08.2013 get Quotes from dB
 * <p/>
 */
public class QuoteDB
{

    private static final String PERSISTENCE_UNIT_NAME = "JPA_Angelo_5PU";
    private static EntityManagerFactory factory;

    public static void addQuotes(RequestHistoricalMktDta histreq, TradeOrder to,
            RequestQuotesHistorical m_qh)
    {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();

        StockService stse = new StockService();

        
        Stock st = stse.createStock(em, m_qh.getSymbol(), "");
        stse.addIntervalType(em, st, histreq.getBarsize());
        //System.out.println("Symbol : " + m_qh.getSymbol() + " Total number of quotes :" + m_qh.getQh().getAll().size());
        /*for (PriceBar pb : m_qh.getQh().getAll()) {
         * stse.addQuote(em, st, histreq.getBarsize(), pb.getDate(),
         * pb.getOpen(),
         * pb.getHigh(),
         * pb.getLow(),
         * pb.getClose(),
         * pb.getVolume(),
         * pb.getCount(),
         * pb.getWAP(),
         * pb.getValueext());
         * 
         * 
         * }*/
        
        stse.addQuotesList(em, st, histreq.getBarsize(), m_qh.getQh().getAll());
               
       em.close();
    }
    
    public static void setQuotes(RequestHistoricalMktDta histreq, TradeOrder to,
            RequestQuotesHistorical m_qh)
    {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();

        StockService stse = new StockService();
               
        m_qh.getQh().setPriceBars(stse.getQuotes(em, m_qh.getSymbol(), histreq.getBarsize()));

        em.close();
    }
}
