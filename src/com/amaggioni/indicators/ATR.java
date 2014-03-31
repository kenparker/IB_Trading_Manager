package com.amaggioni.indicators;

import core.PriceBar;
import core.PriceBar.ValueType;
import com.amaggioni.myutilities.MyMath;
import java.text.DecimalFormat;


public class ATR extends Indicator
{

  private int period;
  private int qhsize;
  private double[] tr = null;
  private double[] atr = null;

  public ATR(QuoteHistory qh, int period)
  {
    super(qh);
    qhsize = qh.size();
    this.period = period;
    this.tr = new double[qhsize];

    this.atr = new double[qhsize];
  }

  @Override
  public double calculate()
  {
    int periodEnd;
    double high;
    double low;
    double close_1;

    // int periodStart = qh.size() - period;
    periodEnd = qh.size();

    // not enough data
    if (this.period > periodEnd * 2)
    {
      return 0.0;
    }

    QuoteHistory trqh = new QuoteHistory();

    // First TR Value
    tr[0] = qh.getPriceBar(0).getHigh() - qh.getPriceBar(0).getLow();

    addTR(trqh,0);
    for (int i = 1; i <= periodEnd - 1; i++)
    {


      // the first calculation for ADX is the true range value (TR)

      high = qh.getPriceBar(i).getHigh();
      low = qh.getPriceBar(i).getLow();
      close_1 = qh.getPriceBar(i - 1).getClose();

      tr[i] = Math.max(high - low, Math.max(Math.abs(high
              - close_1), Math.abs(low - close_1)));

      if (i < period - 1)
      {
        addTR(trqh, i);
      }
      if (i == period - 1)
      {
        // Calculate Moving Average
        MovingAverage ma = new MovingAverage(trqh);
        ma.calculate(period, 0, ValueType.Value);
      }
      if (i >= period)
      {
        atr[i] = (atr[i - 1] * (period - 1) + tr[i]) / period;
      }


    }
    return atr[qh.size()-1];
  }

  public double getATR(int index)
  {
    double atrv = atr[this.qhsize + index - 1];
    return MyMath.round(atrv, 2);
  }

  @Override
  public String toString()
  {
    DecimalFormat df = new DecimalFormat("00.00");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.qhsize; i++)
    {
      sb.append("Index :");
      sb.append(i);
      sb.append(" tr ");
      sb.append(df.format(this.tr[i]));

      sb.append(" atr :");
      sb.append(df.format(atr[i])).append("\n");
    }

    return sb.toString();

  }

  
  private void addTR(QuoteHistory trqh, int i)
  {
    trqh.addHistoricalPriceBar(new PriceBar(null, tr[i], tr[i], tr[i], tr[i], tr[i], 0, tr[i]));
  }
}
