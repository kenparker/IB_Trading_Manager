package com.amaggioni.indicators;

import core.PriceBar.ValueType;
import com.amaggioni.myutilities.MyMath;
import com.amaggioni.myutilities.WriteToFile;


/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 30.05.2013
 * change date :
 *
 * d
 * Change Log:
 * <p/>
 * 30.05.2013 first Version
 * 08.07.2013 Check x number of periods
 * <p/>
 */
public class AutoEnvelope extends Indicator
{

    private int qhsize;
    private double[] envelopepercent;
    private double[] evelopefactor;
    private double[] envelopehigh;
    private double[] envelopelow;
    private MovingAverage ema;
    private int emaperiod;
    private double factorstart;
    private double factorend;
    private int lookbackperiod;
    private double percentlimit;
    private String symbol;
    private String barsize;

    public AutoEnvelope(QuoteHistory qh)
    {

        super(qh);
        this.emaperiod = 20;
        this.qhsize = qh.getSize();
        this.envelopepercent = new double[qhsize];
        this.evelopefactor = new double[qhsize];
        this.envelopehigh = new double[qhsize];
        this.envelopelow = new double[qhsize];
        this.ema = new MovingAverage(qh);

        this.factorstart = 0.01;
        this.factorend = 0.07;
        this.lookbackperiod = 100;
        this.percentlimit = 95;
        this.symbol = "";
        this.barsize = "";

    }

    @Override
    public double calculate()
    {

        ema.calculate(emaperiod, 1, ValueType.Close);



        if (this.qhsize - 2 < this.getLookbackperiod()) {
            String outString = WriteToFile.msgHeaderBld("Err", "AutoEnvelope", getSymbol(),
                    getBarsize(),this.getLookbackperiod())
                    + " qhsize " + qhsize
                    + " less then lookback " + getLookbackperiod();
            WriteToFile.logAll(outString, getSymbol());
            return 0.0;
        }

        for (int i = getLookbackperiod() + 1; i < qhsize; i++) {
            final double emav = ema.getMaAll()[i];

            int periodStart = i - getLookbackperiod() + 1;
            int periodEnd = i;

            for (double j = getFactorstart(); j <= getFactorend(); j = j + 0.001) {

                /*String outString = new Date() + " [Fine] [AutoEnvelope] [" + getSymbol() + "] "
                 * + " factor :" + j;
                 * WriteToFile.logAll(outString, getSymbol());*/

                envelopelow[i] = emav - (emav * j);
                envelopehigh[i] = emav + (emav * j);

                //log1(i);

                this.getEvelopefactor()[i] = j;
                this.envelopepercent[i] = 0;

                for (int bar = periodStart; bar <= periodEnd; bar++) {

                    final double close = qh.getPriceBar(bar).getClose();

                    if (close >= envelopelow[i] && close <= envelopehigh[i]) {
                        this.envelopepercent[i]++;
                    }

                }

                this.envelopepercent[i] = this.envelopepercent[i] / getLookbackperiod() * 100;
                //log2(i);
                if (this.envelopepercent[i] >= getPercentlimit()) {

                    break;
                }

            }
        }

        return this.getEnvelopepercent()[qhsize - 1];
    }

    public void logEnvelFactor(int ind)
    {
        int index = calcIndex(ind);


        String outString = WriteToFile.msgHeaderBld("Fine", "AutoEnvelope", getSymbol(),
                getBarsize(), this.getLookbackperiod())
                + " factor :" + MyMath.round(this.evelopefactor[index], 3) * 100
                + " % in Envelope :" + MyMath.round(this.envelopepercent[index], 2)
                + " %b :" + this.percBEnv(index)
                + " Envelope High :" + MyMath.round(this.envelopehigh[index], 2)
                + " Envelope Low :" + MyMath.round(this.envelopelow[index], 2);
        WriteToFile.logAll(outString, getSymbol());
    }

    public void checkPeriod(int period)
    {
        for (int i = this.qhsize - period - 1; i < this.qhsize; i++) {

            if (this.isNearEnvelope(i)) {
                logNearEnvelope(i);
                log2(i);
                String outString = WriteToFile.msgHeaderBld("Fine", "AutoEnvelope", getSymbol(),
                getBarsize(), this.getLookbackperiod()) +
                this.qh.toStringOneBar(i);
                WriteToFile.logAll(outString, getSymbol());
            }
            

        }
        
       // log2(this.qhsize-1);


    }

    public void logNearEnvelope(int ind)
    {
        
        if (this.isNearEnvelope(ind)) {
            String outString = WriteToFile.msgHeaderBld("Info", "AutoEnvelope", getSymbol(),
                    getBarsize(),this.getLookbackperiod())
                    + " Close is near to envelope";
            WriteToFile.logAll(outString, getSymbol());
        }
    }

    public boolean isNearEnvelope(int ind)
    {
        final double percBEnv = percBEnv(ind);

        if (percBEnv >= 80 || percBEnv <= 20) {
            return true;
        }
        return false;
    }

    public double percBEnv(int index)
    {
        double percB = (this.qh.getPriceBar(index).getClose() - this.envelopelow[index])
                / (this.envelopehigh[index] - this.envelopelow[index]) * 100;
        return MyMath.round(percB, 2);
    }

    //<editor-fold defaultstate="collapsed" desc="Getter Setter">
    /**
     * @return the percentlimit
     */
    public double getPercentlimit()
    {
        return percentlimit;
    }

    /**
     * @param percentlimit the percentlimit to set
     */
    public void setPercentlimit(double percentlimit)
    {
        this.percentlimit = percentlimit;
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
     * @return the factorstart
     */
    public double getFactorstart()
    {
        return factorstart;
    }

    /**
     * @param factorstart the factorstart to set
     */
    public void setFactorstart(double factorstart)
    {
        this.factorstart = factorstart;
    }

    /**
     * @return the factorend
     */
    public double getFactorend()
    {
        return factorend;
    }

    /**
     * @param factorend the factorend to set
     */
    public void setFactorend(double factorend)
    {
        this.factorend = factorend;
    }

    /**
     * @return the envelopepercent
     */
    public double[] getEnvelopepercent()
    {
        return envelopepercent;
    }

    /**
     * @param envelopepercent the envelopepercent to set
     */
    public void setEnvelopepercent(double[] envelopepercent)
    {
        this.envelopepercent = envelopepercent;
    }

    /**
     * @return the evelopefactor
     */
    public double[] getEvelopefactor()
    {
        return evelopefactor;
    }

    /**
     * @param evelopefactor the evelopefactor to set
     */
    public void setEvelopefactor(double[] evelopefactor)
    {
        this.evelopefactor = evelopefactor;
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
    //</editor-fold>

    private int calcIndex(int in)
    {
        int index = qhsize - in - 1;
        return index;
    }

    private void log1(int index)
    {
        String outString = WriteToFile.msgHeaderBld("Fine", "AutoEnvelope", getSymbol(),
                getBarsize(),this.getLookbackperiod())
                + " envelope high :" + envelopehigh[index];
        WriteToFile.logAll(outString, getSymbol());
        outString = WriteToFile.msgHeaderBld("Fine", "AutoEnvelope", getSymbol(), getBarsize(),this.getLookbackperiod())
                + " envelope low :" + envelopelow[index];
        WriteToFile.logAll(outString, getSymbol());
    }

    private void log2(int index)
    {

        String outString = WriteToFile.msgHeaderBld("Fine", "AutoEnvelope", getSymbol(),
                getBarsize(),this.getLookbackperiod())
                + " factor :" + MyMath.round(this.evelopefactor[index], 3)
                + " % in Envelope :" + MyMath.round(this.envelopepercent[index], 2)
                + " Envelope High :" + MyMath.round(this.envelopehigh[index], 2)
                + " Envelope Low :" + MyMath.round(this.envelopelow[index], 2)
                + " % b :" + this.percBEnv(index)
                + this.qh.toStringOneBar(index);
        

        WriteToFile.logAll(outString, "AutoEnvelope");

    }
}
