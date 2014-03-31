package com.amaggioni.indicators;

import java.text.DecimalFormat;

/**
 * @author: Humberto Rocha Loureiro (humbertorocha@gmail.com)
 * @modify: Angelo Maggioni
 */
/**
 * Average Directional Index. Based on
 * http://technical.traders.com/tradersonline/display.asp?art=278
 * <p/>
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:average_directional_
 * <p/>
 * @author Carlos Aza Villarrubia
 * @version 1.1
 * @date 25/05/2008
 */
public class ADX extends Indicator
{

    private int period;
    private int qhsize;
    private double[] tr = null;
    private double[] dmPlus = null;
    private double[] dmMinus = null;
    private double[] trN = null;
    private double[] dmPlusN = null;
    private double[] dmMinusN = null;
    private double[] dx = null;
    private double[] adx = null;

    public ADX(QuoteHistory qh, int period)
    {
        super(qh);
        qhsize = qh.size();
        this.period = period;
        this.tr = new double[qhsize];
        this.dmPlus = new double[qhsize];
        this.dmMinus = new double[qhsize];
        this.trN = new double[qhsize];
        this.dmPlusN = new double[qhsize];
        this.dmMinusN = new double[qhsize];
        this.dx = new double[qhsize];
        this.adx = new double[qhsize];
    }

    @Override
    public double calculate()
    {
        int periodEnd;
        double high;
        double low;
        // double close = qh.getLastPriceBar().getClose();
        double high_1;
        double low_1;
        double close_1;

        // int periodStart = qh.size() - period;
        periodEnd = qh.size();

        // not enough data
        if (this.period > periodEnd * 2)
        {
            return 0.0;
        }

        for (int i = 1; i <= periodEnd - 1; i++)
        {


            // the first calculation for ADX is the true range value (TR)

            high = qh.getPriceBar(i).getHigh();
            low = qh.getPriceBar(i).getLow();
            // double close = qh.getLastPriceBar().getClose();
            high_1 = qh.getPriceBar(i - 1).getHigh();
            low_1 = qh.getPriceBar(i - 1).getLow();
            close_1 = qh.getPriceBar(i - 1).getClose();

            tr[i] = Math.max(high - low, Math.max(Math.abs(high
                    - close_1), Math.abs(low - close_1)));

            // determines the positive directional movement or returns zero if there
            // is no positive directional movement.
            dmPlus[i] = high - high_1 > low_1 - low ? Math.max(high
                    - high_1, 0) : 0;

            // calculates the negative directional movement or returns zero if there
            // is no negative directional movement.
            dmMinus[i] = low_1 - low > high - high_1 ? Math.max(
                    low_1 - low, 0) : 0;

            // The daily calculations are volatile and so the data needs to be
            // smoothed. First, sum the last N periods for TR, +DM and - DM

            if (i == period)
            {
                for (int j = 1; j <= period; j++)
                {
                    trN[i] += tr[j];
                    dmPlusN[i] += dmPlus[j];
                    dmMinusN[i] += dmMinus[j];
                }
            }

            if (i > period)
            {
                // The smoothing formula subtracts 1/Nth of yesterday's trN from
                // yesterday's trN and then adds today's TR value
                // The truncating function is used to calculate the indicator as close
                // as possible to the developer of the ADX's original form of
                // calculation (which was done by hand).
                trN[i] = trN[i - 1] - (trN[i - 1] / period) + tr[i];
                dmPlusN[i] = dmPlusN[i - 1] - (dmPlusN[i - 1] / period) + dmPlus[i];
                dmMinusN[i] = dmMinusN[i - 1] - (dmMinusN[i - 1] / period) + dmMinus[i];

            }

            if (i >= period)
            {
                // Now we have a 14-day smoothed sum of TR, +DM and -DM.
                // The next step is to calculate the ratios of +DM and -DM to TR.
                // The ratios are called the +directional indicator (+DI) and
                // -directional indicator (-DI).
                // The integer function (int) is used because the original developer
                // dropped the values after the decimal in the original work on the ADX
                // indicator.
                double diPlus = (100 * dmPlusN[i] / trN[i]);
                double diMinus = (100 * dmMinusN[i] / trN[i]);

                // The next step is to calculate the absolute value of the difference
                // between the +DI and the -DI and the sum of the +DI and -DI.
                double diDiff = Math.abs(diPlus - diMinus);
                double diSum = diPlus + diMinus;

                // The next step is to calculate the DX, which is the ratio of the
                // absolute value of the difference between the +DI and the -DI divided
                // by the sum of the +DI and the -DI.
                dx[i] = (100 * diDiff / diSum);

                // The final step is smoothing the DX to arrive at the value of the ADX.
                // First, average the last N days of DX values
                if (i >= (period * 2 - 1))
                {
                    if (i == (period * 2 - 1))
                    {
                        double dxMedia = 0;
                        for (int j = period; j <= (period * 2 - 1); j++)
                        {
                            dxMedia += dx[j];
                        }
                        adx[i] = dxMedia / period;
                    } else
                    {

                        // The smoothing process uses yesterday's ADX value multiplied by N-1,
                        // and then add today's DX value. Finally, divide this sum by N.

                        adx[i] = (adx[i - 1] * (period - 1) + dx[i]) / period;

                        value = adx[i];
                    }
                }
                
                adx[i] = Math.round(adx[i]*100)/100.0;
            }


        }
        return adx[qh.size()-1];
    }

    public double getADX(int index)
    {
        return adx[this.qhsize + index - 1];
    }

    public double getDNegative(int index)
    {
        return dmMinusN[this.qhsize + index - 1];
    }

    public double getDPositive(int index)
    {
        return dmPlusN[this.qhsize + index - 1];
    }

    @Override
    public String toString()
    {
        DecimalFormat df =   new DecimalFormat  ( "00.00" );
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.qhsize; i++)
        {
            sb.append("Index :" + i);
            sb.append(" tr ");
            sb.append(df.format(this.tr[i]));
            sb.append(" +DM ");
            sb.append(df.format(this.dmPlus[i]));
            sb.append(" -DM ");
            sb.append(df.format(this.dmMinus[i]));
            sb.append(" DX ");
            sb.append(df.format(this.dx[i]));
            sb.append(" adx :");
            sb.append(df.format(adx[i])).append("\n");
        }

        return sb.toString();

    }
}
