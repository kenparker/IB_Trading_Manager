/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestStandardErrorBand1;

import com.amaggioni.IBordermanagement.TradesCSV;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 03.01.2013
 * change date : 11.03.2013
 *
 * description: The class manage a trading strategy for multiple symbols
 *
 * flow:
 * <p/>
 *
 * Change Log:
 * 28.01.2013 OCAGroup added by Orders
 * <p/>
 */
public class TestRegression
{

    private static final String FILE_PATH = "C:/Users/magang/Mediencenter/Standard_Error_Band/";
    private static final String FILE_NAME = "Standard_Error_Band.csv";
    private static final char SEPARATOR = ';';
    private static TradesCSV tradesCSV = new TradesCSV(FILE_PATH, FILE_NAME, SEPARATOR);
    static double[][] data = new double[21][2];
    static SimpleRegression regression = new SimpleRegression();
    // Get a SummaryStatistics instance
static SummaryStatistics stats_summary = new SummaryStatistics();
static DescriptiveStatistics stats_descript = new DescriptiveStatistics();

    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        tradesCSV.getTrades();
        List<String[]> allElements = tradesCSV.getAllTrades();
        int i = 0;
        for (Iterator<String[]> iterator = allElements.iterator(); iterator.hasNext();) {

            String[] s = iterator.next();
            
            
            data[i][0] = (double) i+1;
            data[i][1] = Double.valueOf(s[1]);
            stats_summary.addValue(data[i][1]);
            stats_descript.addValue(data[i][1]);
            
            System.out.println("i :" + data[i][0] + "Value : "+ data[i][1]);
            
            i++;
        }

        regression.clear();
        regression.addData(data);

        //System.out.println("Intercept :" + regression.getIntercept());
// displays intercept of regression line

        System.out.println("Slope :" + regression.getSlope());
// displays slope of regression line

        System.out.println("y :" + (regression.getSlope() * data[data.length-1][0] + regression.getIntercept()));
        System.out.println(" summary getStandardDeviation() : " + stats_summary.getStandardDeviation());
        System.out.println(" descri getStandardDeviation() : " + stats_descript.getStandardDeviation());
        System.out.println(" getN() : " + stats_summary.getN() + " " + stats_descript.getN());
        System.out.println(" Standard Error : " + ( stats_summary.getStandardDeviation() / Math.sqrt(stats_summary.getN())));
        
        System.out.println(" getRSquare() : " + regression.getRSquare());
        
        //System.out.println("Slope StdErr :" + regression.getSlopeStdErr());
// displays slope standard error
    }
}
