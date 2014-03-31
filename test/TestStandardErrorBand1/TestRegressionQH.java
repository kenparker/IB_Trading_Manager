/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestStandardErrorBand1;

import com.amaggioni.testcallers.*;
import com.amaggioni.IBordermanagement.TradesCSV;
import com.amaggioni.datastructure.RequestQuotesHistorical;
import com.amaggioni.indicators.ADX;
import com.amaggioni.indicators.AvgHourlyVol;
import com.amaggioni.indicators.MACD;
import com.amaggioni.indicators.MovingAverage;
import com.amaggioni.indicators.PeriodHigh;
import core.PriceBar;
import core.PriceBar.ValueType;
import com.amaggioni.indicators.QuoteHistory;
import com.amaggioni.indicators.StandardErrorBands;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author magang
 */
public class TestRegressionQH
{

    PriceBar pb = new PriceBar();
    QuoteHistory qh = new QuoteHistory();
    
    private static final String FILE_PATH = "C:/Users/magang/Mediencenter/Standard_Error_Band/";
    private static final String FILE_NAME = "Standard_Error_Band.csv";
    private static final char SEPARATOR = ';';
    private TradesCSV tradesCSV = new TradesCSV(FILE_PATH, FILE_NAME, SEPARATOR);

    public static void main(String[] args) throws FileNotFoundException, IOException
    {

        try
        {
            new TestRegressionQH().runn2();
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

            PriceBar pb = new PriceBar();
            pb.setDate(s[0]);
            pb.setClose(Double.valueOf(s[1]));
          //  pb.setHigh(Double.valueOf(s[2]));
            //System.out.println("s[0] :"+ s[0] + " s[1] :"+s[1]);
            qh.addHistoricalPriceBar(pb);
        }
        
        StandardErrorBands seb = new StandardErrorBands(qh);
        seb.setBandfactor(2);
        seb.setLookbackperiod(21);
        seb.setSmoothingperiod(3);
        seb.calculate();
        seb.checkPeriod(100);
        
    }
}
