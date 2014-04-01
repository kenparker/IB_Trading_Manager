/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.testcallers;

import com.amaggioni.IBordermanagement.TradesCSV;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.indicators.ADX;
import core.PriceBar;
import com.amaggioni.indicators.QuoteHistory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author magang
 */
public class TestRequestHist
{

    RequestQuotesHistorical rh = new RequestQuotesHistorical("");
    private static final String FILE_PATH = "C:/Users/magang/Mediencenter/";
    private static final String FILE_NAME = "cs-adx.csv";
    private static final char SEPARATOR = ';';
    private TradesCSV tradesCSV = new TradesCSV(FILE_PATH, FILE_NAME, SEPARATOR);
    

    public static void main(String[] args) throws FileNotFoundException, IOException
    {

        try
        {
            new TestRequestHist().runn2();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void runn2() throws FileNotFoundException, IOException
    {


        tradesCSV.getTrades();
        List<String[]> allElements = tradesCSV.getAllTrades();

        for (Iterator<String[]> iterator = allElements.iterator(); iterator.hasNext();)
        {
            
            String[] s = iterator.next();
            
            this.rh.addList(s[0], 0.0, Double.valueOf(s[3]), Double.valueOf(s[1]), Double.valueOf(s[2]),
                    0.0, 0, 0.0);
        }

        //List<PriceBar> allbars = rh.getAll();

        //System.out.println(rh.toString());
        //System.out.println(allbars.size());
        //QuoteHistory qh = new QuoteHistory(allbars);
        //System.out.println(rh.getQh());
        ADX adx = new ADX(rh.getQh(),14);
        adx.calculate();
        
        System.out.println(adx);
        
        DecimalFormat df =   new DecimalFormat  ( "00.00" );
        System.out.println(df.format(adx.getADX(-1)));
        System.out.println(adx.getADX(-1));
        /*for (Iterator<PriceBar> iterator = allbars.iterator(); iterator.hasNext();)
        {
            PriceBar pb = iterator.next();
            
            System.out.println(pb.toString());
        }*/
    }
}
