package com.amaggioni.indicators;

import core.PriceBar.ValueType;
import com.amaggioni.myutilities.MyMath;
import com.amaggioni.myutilities.WriteToFile;
import static com.amaggioni.testcallers.StatisticsUtil.getMean;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * ***********************************************************************************************
 *
 * @author magang create date : 11.07.2013 change date :
 *
 * d
 * Change Log:
 * <p/>
 * 11.07.2013 first Version
 *
 * <p/>
 */
public class StandardErrorBands extends Indicator
{

    private int qhsize;
    private double[] linearregression;
    private double[] linearregressionsmoothed;
    private double[] standarderror;
    private double[] standarderrorsmoothed;
    private double[] upperband;
    private double[] lowerband;
    private double[] bandwidth;
    private double[] percA;
    private double[] slope;
    private double[] RSquare;
    private double[] RSquaresmoothed;
    private double[] regressionslope;
    private int smoothingperiod;
    private double bandfactor;
    private int lookbackperiod;
    private String symbol;
    private String barsize;

    public StandardErrorBands(QuoteHistory qh)
    {

        super(qh);

        this.qhsize = qh.getSize();
        this.linearregression = new double[qhsize];
        this.linearregressionsmoothed = new double[qhsize];
        this.standarderror = new double[qhsize];
        this.standarderrorsmoothed = new double[qhsize];
        this.upperband = new double[qhsize];
        this.lowerband = new double[qhsize];
        this.bandwidth = new double[qhsize];
        this.percA = new double[qhsize];
        this.slope = new double[qhsize];
        this.RSquare = new double[qhsize];
        this.RSquaresmoothed = new double[qhsize];
        this.regressionslope = new double[qhsize];



        this.smoothingperiod = 6;
        this.lookbackperiod = 150;
        this.bandfactor = 2;

        this.symbol = "";
        this.barsize = "";

    }

    @Override
    public double calculate()
    {


        SimpleRegression regression = new SimpleRegression();
        DescriptiveStatistics stats = new DescriptiveStatistics();
        stats.setWindowSize(this.lookbackperiod);


        if (this.qhsize - 2 < this.getLookbackperiod()) {
            String outString = WriteToFile.msgHeaderBld("Err", "StandardErrorBands", getSymbol(),
                    getBarsize(),this.getLookbackperiod())
                    + " qhsize " + qhsize
                    + " less then lookback " + getLookbackperiod();
            WriteToFile.logAll(outString, getSymbol());
            return 0.0;
        }
        regression.clear();

        for (int i = 0; i < qhsize; i++) {

            stats.addValue(qh.getPriceBar(i).getClose());
            regression.addData(i + 1, qh.getPriceBar(i).getClose());


            if (i >= this.lookbackperiod - 1) {

                // calculate Linear Regression, Slope and RSquare
                this.slope[i] = regression.getSlope();
                this.RSquare[i] = regression.getRSquare();
                linearregression[i] = slope[i] * (i + 1) + regression.getIntercept();


                // calculate Standard Error
                //this.standarderror[i] = (stats.getStandardDeviation() / Math.sqrt(this.lookbackperiod));
                this.standarderror[i] = (stats.getStandardDeviation() );
                
                // Remove previous value for rolling calculation
                regression.removeData((i - (this.lookbackperiod - 1)) + 1, qh.getPriceBar(
                        i - (this.lookbackperiod - 1)).getClose());
            } else {
               /* this.slope[i] = Double.NaN;
                this.RSquare[i] = Double.NaN;
                this.linearregression[i] = Double.NaN;
                this.standarderror[i] = Double.NaN; */
                this.slope[i] = 0;
                this.RSquare[i] = 0;
                this.linearregression[i] = 0;
                this.standarderror[i] = 0;
            }
        }


        // Smooth Regression
        MovingAverage malr = new MovingAverage(linearregression);
        malr.calculate(this.getSmoothingperiod(), 0, ValueType.Other);
        this.linearregressionsmoothed = malr.getMaAll();

        // Smooth standard error
        MovingAverage masr = new MovingAverage(standarderror);
        masr.calculate(this.getSmoothingperiod(), 0, ValueType.Other);
        this.standarderrorsmoothed = masr.getMaAll();

        // Smooth RSquare
        MovingAverage marq = new MovingAverage(RSquare);
        marq.calculate(this.getSmoothingperiod(), 0, ValueType.Other);
        this.RSquaresmoothed = marq.getMaAll();

        for (int i = 0; i < qhsize; i++) {
            if (i >= this.lookbackperiod - 1) {
                // calculate Upper and Lower Bands
                this.upperband[i] = this.linearregressionsmoothed[i] + this.getBandfactor() * this.standarderrorsmoothed[i];
                this.lowerband[i] = this.linearregressionsmoothed[i] - this.getBandfactor() * this.standarderrorsmoothed[i];
                this.bandwidth[i] = (this.upperband[i] - this.lowerband[i]) / this.linearregressionsmoothed[i] * 100;
                // calculate %a
                this.percA[i] = (qh.getPriceBar(i).getClose() - this.lowerband[i])
                        / (this.upperband[i] - this.lowerband[i]) * 100;
            } else {
                this.upperband[i] = Double.NaN;
                this.lowerband[i] = Double.NaN;
                this.bandwidth[i] = Double.NaN;
                this.percA[i] = Double.NaN;
            }
        }

        return this.getPercA()[qhsize - 1];
    }

    public void logDetail(int index)
    {

        String outString = WriteToFile.msgHeaderBld("Fine", "StandardErrorBands", getSymbol(),
                getBarsize(),this.getLookbackperiod())
                + String.format(" Regression : %5.2f", this.linearregression[index])
                + String.format(" Standard Error : %5.2f", this.standarderror[index])
                + String.format(" percA : %5.2f", this.getPercA()[index])
                + String.format(" Band Upper : %5.2f", this.upperband[index])
                + String.format(" Band Lower : %5.2f", this.lowerband[index])
                + String.format(" Slope : %5.3f", this.slope[index])
                + String.format(" RSquare : %5.4f", this.RSquare[index])
                + this.qh.toStringOneBar(index);
        WriteToFile.logAll(outString, getSymbol());
    }
    
    public void logDetail2(int index)
    {

        String outString = WriteToFile.msgHeaderBld("Fine", "StandardErrorBands", getSymbol(),
                getBarsize(),this.getLookbackperiod())
                
                + String.format(" percA : %5.2f", this.getPercA()[index])
                
                + String.format(" Slope : %5.3f", this.slope[index])
                + String.format(" RSquare : %5.4f", this.RSquare[index])
                + this.qh.toStringOneBar(index);
        WriteToFile.logAll(outString, getSymbol());
    }

    public void printPeriod(int period)
    {
        for (int i = this.qhsize - period; i < this.qhsize; i++) {
            logDetail(i);
        }
    }

    public void checkPeriod(int period)
    {
        for (int i = this.qhsize - period; i < this.qhsize; i++) {

            if (this.isNearBand(i) && this.linearregression[i] != 0) {
               // logNearBand(i);
                logDetail2(i);
            }
        }

        //logDetail(this.qhsize - 1);


    }
    
    public void checkPeriod2(int period)
    {
        for (int i = this.qhsize - period; i < this.qhsize; i++) {

            if (this.isNearBand(i) 
                    && this.isRSSquare(i)
                    && this.linearregression[i] != 0) {
               // logNearBand(i);
                logDetail2(i);
            }
        }

        //logDetail(this.qhsize - 1);


    }
    
    public void checkPeriod3(int period)
    {
        for (int i = this.qhsize - period; i < this.qhsize; i++) {

            if (
                     this.isRSSquare(i)
                    && this.linearregression[i] != 0) {
               // logNearBand(i);
                logDetail2(i);
            }
        }

        //logDetail(this.qhsize - 1);


    }

    public void logNearBand(int ind)
    {

        if (this.isNearBand(ind)) {
            String outString = WriteToFile.msgHeaderBld("Info", "StandardErrorBands", getSymbol(),
                    getBarsize(),this.getLookbackperiod())
                    + " Close is near to Standard Error Band";
            WriteToFile.logAll(outString, getSymbol());
        }
    }

    public boolean isNearBand(int index)
    {

        if (this.getPercA()[index] >= 95 || this.getPercA()[index] <= 5) {
            return true;
        }
        return false;
    }
    
    public boolean isRSSquare(int index)
    {

        if (this.RSquare[index] > 0.90) {
            return true;
        }
        return false;
    }

    private int calcIndex(int in)
    {
        int index = qhsize - in - 1;
        return index;
    }

    /**
     * @return the percA
     */
    public double[] getPercA()
    {
        return percA;
    }

    /**
     * @return the RSquaresmoothed
     */
    public double[] getRSquaresmoothed()
    {
        return RSquaresmoothed;
    }

    /**
     * @return the regressionslope
     */
    public double[] getRegressionslope()
    {
        return regressionslope;
    }

    /**
     * @return the smoothingperiod
     */
    public int getSmoothingperiod()
    {
        return smoothingperiod;
    }

    /**
     * @param smoothingperiod the smoothingperiod to set
     */
    public void setSmoothingperiod(int smoothingperiod)
    {
        this.smoothingperiod = smoothingperiod;
    }

    /**
     * @return the bandfactor
     */
    public double getBandfactor()
    {
        return bandfactor;
    }

    /**
     * @param bandfactor the bandfactor to set
     */
    public void setBandfactor(double bandfactor)
    {
        this.bandfactor = bandfactor;
    }

    /**
     * @return the lookbackperiod
     */
    public int getLookbackperiod()
    {
        return lookbackperiod;
    }

    /**
     * @param lookbackperiod the lookbackperiod to set
     */
    public void setLookbackperiod(int lookbackperiod)
    {
        this.lookbackperiod = lookbackperiod;
    }

    /**
     * @return the symbol
     */
    public String getSymbol()
    {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    /**
     * @return the barsize
     */
    public String getBarsize()
    {
        return barsize;
    }

    /**
     * @param barsize the barsize to set
     */
    public void setBarsize(String barsize)
    {
        this.barsize = barsize;
    }
}
