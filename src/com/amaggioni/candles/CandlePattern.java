package com.amaggioni.candles;

import com.amaggioni.indicators.MovingAverage;
import core.PriceBar;
import com.amaggioni.indicators.QuoteHistory;
import com.amaggioni.myutilities.WriteToFile;
import java.util.Date;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : xx.04.2013
 * change date : 12.05.2013
 *
 * d
 * Change Log:
 * <p/>
 * 03.05.2013 bearish pattern adding
 * 04.05.2013 bearish pattern
 * 12.05.2013 'inverted hammer' added
 */
public class CandlePattern
{

  private QuoteHistory qh;
  private double[] candleBody;
  private boolean[] longBody;
  private boolean[] smallBody;
  private boolean[] whiteBody;
  private boolean[] doji;
  private boolean[] hammer;
  private boolean[] morningStar;
  private boolean[] hangingMan;
  private boolean[] bullishEngulfing;
  private boolean[] bullishHarami;
  private boolean[] bearishHarami;
  private boolean[] bullishPiercing;
  private boolean[] bearishPiercing;
  private boolean[] bullishThreeWhiteSoldiers;
  private boolean[] bullishKicking;
  private boolean[] bullishAbandonedBaby;
  private int trendperiod;
  private MovingAverage volma;

  public CandlePattern(QuoteHistory m_qh)
  {
    this.qh = m_qh;

    candleBody = new double[qh.size()];
    longBody = new boolean[qh.size()];
    smallBody = new boolean[qh.size()];
    whiteBody = new boolean[qh.size()];

    bullishHarami = new boolean[qh.size()];
    bearishHarami = new boolean[qh.size()];

    bullishEngulfing = new boolean[qh.size()];

    bullishPiercing = new boolean[qh.size()];
    bearishPiercing = new boolean[qh.size()];

    bullishThreeWhiteSoldiers = new boolean[qh.size()];
    bullishKicking = new boolean[qh.size()];
    doji = new boolean[qh.size()];
    hammer = new boolean[qh.size()];
    hangingMan = new boolean[qh.size()];

    bullishAbandonedBaby = new boolean[qh.size()];

    volma = new MovingAverage(this.qh);
  }

  /*--
   *  M A I N Method
   * 
   * 
   * 
   * 
   */
  public boolean calculate(int m_trendperiod)
  {
    boolean okFlag = true;
    this.trendperiod = m_trendperiod;

    for (int a = trendperiod - 1; a < qh.getSize(); a++)
    {
      candleBody[a] = calcCandleBody(a);
      whiteBody[a] = checkWhiteBody(a);
      this.doji[a] = this.calcDoji(a);
    }

    MovingAverage emaclose = calcCloseAvg();// Calculate MA for Trend definition
    MovingAverage maBody = calcCandleBodyAvg(); // Calculate average Body

    for (int a = trendperiod; a < qh.getSize(); a++)
    {
      longBody[a] = checkLongBody(a, maBody);
      smallBody[a] = checkSmallBody(a, maBody);

      bullishHarami[a] = calcBullishHarami(a, emaclose);
      bearishHarami[a] = calcBearishHarami(a, emaclose);

      bullishEngulfing[a] = calcBullishEngulfing(a, emaclose);

      bullishPiercing[a] = calcBullishPiercing(a, emaclose);
      bearishPiercing[a] = calcBearishPiercing(a, emaclose);

      bullishThreeWhiteSoldiers[a] = calcThreeWhiteSoldiers(a, emaclose);
      bullishKicking[a] = calcBullishKicking(a);
      bullishAbandonedBaby[a] = calcBullishAbandonedBaby(a, emaclose);

      hammer[a] = calcHammer(a, emaclose);
      hangingMan[a] = calcHangingMan(a, emaclose);
    }



    volma.calculate(3, 0, PriceBar.ValueType.Volume);

    return okFlag;
  }

  private double bodyMax(int index)
  {
    if (checkIndex(index))
    {
      return 0.0;
    }
    final PriceBar priceBar = qh.getPriceBar(index);
    return Math.max(priceBar.getClose(), priceBar.getOpen());

  }

  private double bodyMin(int index)
  {
    if (checkIndex(index))
    {
      return 0.0;
    }
    final PriceBar priceBar = qh.getPriceBar(index);
    return Math.min(priceBar.getClose(), priceBar.getOpen());

  }

  private double calcCandleBody(int index)
  {
    if (checkIndex(index))
    {
      return 0.0;
    }

    return bodyMax(index) - bodyMin(index);

  }

  private double checkRangeMedian(int index)
  {
    if (checkIndex(index))
    {
      return 0.0;
    }
    final PriceBar priceBar = qh.getPriceBar(index);
    return (priceBar.getHigh() - priceBar.getLow()) / 0.5;

  }

  private double calcBodyMedian(int index)
  {
    if (checkIndex(index))
    {
      return 0.0;
    }

    return (bodyMax(index) - bodyMin(index)) / 0.5;

  }

  private double calcUpShadow(int index)
  {
    if (checkIndex(index))
    {
      return 0.0;
    }
    final PriceBar priceBar = qh.getPriceBar(index);
    return (priceBar.getHigh() - bodyMax(index));

  }

  private double calcDownShadow(int index)
  {
    if (checkIndex(index))
    {
      return 0.0;
    }
    final PriceBar priceBar = qh.getPriceBar(index);
    return (bodyMin(index) - priceBar.getLow());

  }

  private double calcRngy(int index)
  {
    if (checkIndex(index))
    {
      return 0.0;
    }
    final PriceBar priceBar = qh.getPriceBar(index);
    return priceBar.getHigh() - priceBar.getLow();

  }

  private boolean isUmbrellaLine(int index)
  {

    return this.calcUpShadow(index) < this.calcRngy(index) * 0.1
            && this.calcDownShadow(index) > this.candleBody[index] * 2.5;
  }

  private boolean calcHammer(int index, MovingAverage volma)
  {

    return isUmbrellaLine(index) && this.isDownTrend(volma, index);
  }

  private boolean calcHangingMan(int index, MovingAverage volma)
  {

    return isUmbrellaLine(index)
            && isUpTrend(volma, index) && isUpTrend(volma, index - 1)
            && qh.getPriceBar(index).getClose() > qh.getPriceBar(index - 1).getHigh();

  }

  private boolean checkLongBody(int index, MovingAverage candleBodyAvg)
  {
    if (checkIndex(index))
    {
      return false;
    }

    if (this.candleBody[index] > candleBodyAvg.getMaAll()[index])
    {
      return true;
    } else
    {
      return false;
    }


  }

  private boolean checkSmallBody(int index, MovingAverage candleBodyAvg)
  {
    if (checkIndex(index))
    {
      return false;
    }

    if (this.candleBody[index] < (candleBodyAvg.getMaAll()[index] * 0.75))
    {
      return true;
    } else
    {
      return false;
    }


  }

  private boolean checkWhiteBody(int index)
  {
    if (checkIndex(index))
    {
      return false;
    }

    final PriceBar priceBar = qh.getPriceBar(index);
    return priceBar.getClose() > priceBar.getOpen();
  }

  private boolean checkIndex(int index)
  {
    if (index - 1 > qh.size() || index < 0)
    {
      return true;
    }
    return false;
  }

  public boolean isLongBody(int index)
  {
    return this.longBody[calcIndex(index)];
  }

  public boolean isSmallBody(int index)
  {
    return this.smallBody[calcIndex(index)];
  }

  /**
   * Amibroker formula
   * <p/>
   * @param index
   * @return
   */
  private boolean isBigWhite(int index)
  {
    double close = this.qh.getPriceBar(calcIndex(index)).getClose();
    double open = this.qh.getPriceBar(calcIndex(index)).getOpen();
    double high = this.qh.getPriceBar(calcIndex(index)).getHigh();
    double low = this.qh.getPriceBar(calcIndex(index)).getLow();

    return (close - open) / open > 0.015 && (close - open) * 2 > (high - low);
  }

  /**
   * Amibroker formula
   * <p/>
   * @param index
   * @return
   */
  private boolean isBigBlack(int index)
  {
    double close = this.qh.getPriceBar(calcIndex(index)).getClose();
    double open = this.qh.getPriceBar(calcIndex(index)).getOpen();
    double high = this.qh.getPriceBar(calcIndex(index)).getHigh();
    double low = this.qh.getPriceBar(calcIndex(index)).getLow();

    return (open - close) / open > 0.015 && (open - close) * 2 > (high - low);
  }

  private boolean calcBullishAbandonedBaby(int index, MovingAverage emaclose)
  {
    return isMorningDojiStar(index, emaclose) && isGapDown(index - 1) && isGapUp(index);
  }

  private boolean isGapDown(int index)
  {
    double open = this.qh.getPriceBar(calcIndex(index)).getOpen();
    double close_1 = this.qh.getPriceBar(calcIndex(index - 1)).getClose();
    return open < close_1;
  }

  private boolean isGapUp(int index)
  {
    double open = this.qh.getPriceBar(calcIndex(index)).getOpen();
    double close_1 = this.qh.getPriceBar(calcIndex(index - 1)).getClose();
    return open > close_1;
  }

  private boolean isMorningDojiStar(int index, MovingAverage emaclose)
  {
    double close = this.qh.getPriceBar(calcIndex(index)).getClose();
    double close_2 = this.qh.getPriceBar(calcIndex(index - 2)).getClose();
    double open_2 = this.qh.getPriceBar(calcIndex(index - 2)).getOpen();
    return isDojiStarDown(index - 1, emaclose)
            && isWhiteCandle(index)
            // && isBig(index)
            && this.isLongBody(index)
            && close > (open_2 + close_2) / 2;
  }

  private boolean isDojiStarDown(int index, MovingAverage emaclose)
  {
    return isDoji(index) && isGapDownFromBlack(index, emaclose);

  }

  private boolean isGapDownFromBlack(int index, MovingAverage emaclose)
  {
    return isRealBodyGapDown(index)
            && isBlackCandle(index - 1)
            //&& isBig(index - 1)
            && this.isLongBody(index - 1)
            && isDownTrend(emaclose, index - 1);
  }

  private boolean isRealBodyGapDown(int index)
  {
    double o = this.qh.getPriceBar(calcIndex(index)).getOpen();
    double o_1 = this.qh.getPriceBar(calcIndex(index - 1)).getOpen();
    double c = this.qh.getPriceBar(calcIndex(index)).getClose();
    double c_1 = this.qh.getPriceBar(calcIndex(index - 1)).getClose();
    return Math.max(o, c) < Math.min(o_1, c_1);
  }

  /**
   * Amibroker formula
   * <p/>
   * @param index
   * @return
   */
  private boolean isBig(int index)
  {

    double close = this.qh.getPriceBar(calcIndex(index)).getClose();
    double open = this.qh.getPriceBar(calcIndex(index)).getOpen();
    return Math.abs((close - open) / open) > 0.014;
  }

  /**
   * Amibroker formula
   * <p/>
   * @param index
   * @return
   */
  private boolean calcThreeWhiteSoldiers(int index, MovingAverage emaclose)
  {
    if (checkIndex(index - 3))
    {
      return false;
    }

    double o = this.qh.getPriceBar(calcIndex(index)).getOpen();
    double o_1 = this.qh.getPriceBar(calcIndex(index - 1)).getOpen();
    double o_2 = this.qh.getPriceBar(calcIndex(index - 2)).getOpen();
    return (isDownTrend(emaclose, index - 3)
            && isBigWhite(calcIndex(index)))
            && isBigWhite(calcIndex(index - 1))
            && isBigWhite(calcIndex(index - 2))
            && o > o_1
            && o_1 > o_2;


  }

  private boolean calcBullishKicking(int index)
  {
    if (checkIndex(index - 2))
    {
      return false;
    }

    double o = this.qh.getPriceBar(calcIndex(index)).getOpen();
    double o_1 = this.qh.getPriceBar(calcIndex(index - 1)).getOpen();

    return isBigBlack(calcIndex(index - 1)) && o >= o_1 && isBigWhite(calcIndex(index));
  }

  public boolean isWhiteCandle(int index)
  {
    return this.whiteBody[calcIndex(index)];
  }

  public boolean isBlackCandle(int index)
  {
    return !this.whiteBody[calcIndex(index)];
  }

  public boolean isDoji(int index)
  {
    return this.doji[index];
  }

  public boolean isHammer(int index)
  {
    return this.hammer[index];
  }

  public boolean isCandleBullishEngulfing(int index)
  {
    return this.bullishEngulfing[calcIndex(index)];

  }

  public boolean isCandleBullishHarami(int index)
  {
    return this.bullishHarami[calcIndex(index)];

  }

  public boolean isCandleBullishPiercing(int index)
  {
    return this.bullishPiercing[calcIndex(index)];

  }

  public String isBullishReversalCandle2(int index)
  {
    if (checkIndex(index))
    {
      return "";
    }

    StringBuffer sb = new StringBuffer();

    if (this.isBigVolume(calcIndex(index)))
    {
      if (isCandleBullishHarami(calcIndex(index)))
      {
        sb.append(" Bullish Harami ");
      }

      if (isCandleBullishPiercing(calcIndex(index)))
      {
        sb.append(" Bullish Piercing ");
      }

      if (isCandleBullishEngulfing(calcIndex(index)))
      {
        sb.append(" Bullish Engulfing ");
      }

      if (isBullishThreeWhiteSoldiers(calcIndex(index)))
      {
        sb.append(" Bullish Three White Soldiers ");
      }

      if (isBullishKicking(calcIndex(index)))
      {
        sb.append(" Bullish Kicking ");
      }

      if (isBullishAbandonedBaby(calcIndex(index)))
      {
        sb.append(" Bullish Abandoned Baby ");
      }

      if (this.isHammer(calcIndex(index)))
      {
        sb.append(" Hammer ");
      }
    }
    return sb.toString();
  }

  private MovingAverage calcCandleBodyAvg()
  {
    MovingAverage ma = new MovingAverage(this.candleBody); // instantiate Moving Average
    ma.calculate(5, 0, PriceBar.ValueType.Other);
    return ma;
  }

  public String candleToString_old(int index)
  {

    StringBuffer sb = new StringBuffer();

    sb.append("Index :");
    sb.append(index);
    sb.append(" Datum :");
    sb.append(qh.getPriceBar(calcIndex(index)).getDate());
    if (isWhiteCandle(calcIndex(index)))
    {
      sb.append(" is white candle");
    }
    if (isBlackCandle(calcIndex(index)))
    {
      sb.append(" is black candle");
    }
    if (isSmallBody(calcIndex(index)))
    {
      sb.append(" is small body");
    }
    if (isLongBody(calcIndex(index)))
    {
      sb.append(" is long body");
    }
    if (isCandleBullishEngulfing(calcIndex(index)))
    {
      sb.append(" is bullish engulfing");
    }
    if (isCandleBullishHarami(calcIndex(index)))
    {
      sb.append(" is bullish harami");
    }
    if (isCandleBullishPiercing(calcIndex(index)))
    {
      sb.append(" is bullish piercing");
    }
    if (isBullishKicking(calcIndex(index)))
    {
      sb.append(" is bullish Kicking");
    }
    if (isBullishThreeWhiteSoldiers(calcIndex(index)))
    {
      sb.append(" is bullish Three White Soldiers");
    }
    return sb.toString();
  }

  public void toPrintBullishReversal(String methodname, String symbol, int index)
  {
    for (int i = index; i < candleBody.length; i++)
    {
      final String bullishReversalPatter = isBullishReversalCandle2(i);
      if (!bullishReversalPatter.equals(""))
      {

        String outString;
        outString = new Date() + " " + "[Fine] [" + methodname + "] [" + symbol + "] "
                + " Date " + qh.getPriceBar(calcIndex(i)).getDate()
                + bullishReversalPatter // + (this.isDoji(i) ? " Doji " : " ")
                ;
        WriteToFile.logAll(outString, symbol);
      }
    }
  }

  public void toPrintBearishCandles(String methodname, String symbol, int index)
  {
    for (int i = index; i < candleBody.length; i++)
    {

      boolean candleFlag = false;
      StringBuffer outString = new StringBuffer();
      outString.append(new Date());
      outString.append(" [Fine] [");
      outString.append(methodname);
      outString.append("] [");
      outString.append(symbol);
      outString.append("] ");
      outString.append("Date :");
      outString.append(qh.getPriceBar(calcIndex(i)).getDate());

      if (isBigBlack2(i))
      {
        candleFlag = true;
        outString.append(" Big Black Candle ");

      }

      if (this.isBearishHarami(i))
      {
        candleFlag = true;
        outString.append(" Bearish Harami ");

      }

      if (this.isBearishPiercing(i))
      {
        candleFlag = true;
        outString.append(" Bearish Piercing ");

      }

      if (this.isHangingMan(i))
      {
        candleFlag = true;
        outString.append(" Hanging Man ");

      }

      if (this.isBigVolume(i))
      {
        candleFlag = true;
        outString.append(" BIG Volume ");

        if (!this.isDoji(index)){
        outString.append(this.isWhiteCandle(i) ? " White Candle " : " Black Candle ");}

      }

      if (candleFlag)
      {
        WriteToFile.logAll(outString.toString(), symbol);
      }
    }
  }

  private boolean calcBullishHarami(int index, MovingAverage emaclose)
  {

    if (isDownTrend(emaclose, index)
            && isBlackCandle(index)
            && isWhiteCandle(index - 1)
            && isLongBody(index - 1)
            //&& isBig(index - 1)
            && bodyMax(index) <= bodyMax(index - 1)
            && bodyMin(index) >= bodyMin(index - 1))
    {
      return true;
    } else
    {
      return false;
    }
  }

  private boolean calcBearishHarami(int index, MovingAverage emaclose)
  {

    return isUpTrend(emaclose, index)
            && isWhiteCandle(index)
            && isBlackCandle(index - 1)
            && isLongBody(index - 1)
            && bodyMax(index) <= bodyMax(index - 1)
            && bodyMin(index) >= bodyMin(index - 1);

  }

  private boolean calcBullishPiercing(int index, MovingAverage emaclose)
  {

    return (isDownTrend(emaclose, index) // Down Trend

            && (isBlackCandle(index - 1) && isLongBody(index - 1)) // previous day big black candle
            && (isWhiteCandle(index) && isLongBody(index)) // current day big white candle

            && qh.getPriceBar(index).getOpen() < qh.getPriceBar(index - 1).getLow() // open below previous day low
            && (qh.getPriceBar(index - 1).getLow() - qh.getPriceBar(index).getOpen())
            > (calcRngy(index) * 0.005)
            && (qh.getPriceBar(index).getClose() > bodyMin(index - 1) // close between the previous day body
            && qh.getPriceBar(index).getClose() < bodyMax(index - 1))
            && (qh.getPriceBar(index).getClose() > // close above the prev day body mid point
            (qh.getPriceBar(index - 1).getOpen() + calcBodyMedian(index - 1))));


  }

  private boolean calcBearishPiercing(int index, MovingAverage emaclose)
  {

    return (isUpTrend(emaclose, index) // Up Trend

            && (isWhiteCandle(index - 1) && isLongBody(index - 1)) // previous day big white candle
            && (isBlackCandle(index) && isLongBody(index)) // current day big black candle

            && qh.getPriceBar(index).getOpen() > qh.getPriceBar(index - 1).getHigh() // open above previous day high
            && (qh.getPriceBar(index).getOpen() - qh.getPriceBar(index - 1).getHigh())
            > (calcRngy(index) * 0.005)
            && (qh.getPriceBar(index).getClose() > bodyMin(index - 1) // close between the previous day body
            && qh.getPriceBar(index).getClose() < bodyMax(index - 1))
            && (qh.getPriceBar(index).getClose() < // close below the prev day body mid point
            (qh.getPriceBar(index - 1).getOpen() + calcBodyMedian(index - 1))));
  }

  private boolean calcDoji(int index)
  {
    double upShadow = this.qh.getPriceBar(index).getHigh() - this.bodyMax(index);
    double downShadow = this.bodyMin(index) - this.qh.getPriceBar(index).getLow();
    double priceRange = calcRngy(index);

    return this.candleBody[index] <= priceRange * 0.1
            && upShadow > this.candleBody[index] * 2
            && downShadow > this.candleBody[index] * 2;
  }

  private MovingAverage calcCloseAvg()
  {
    MovingAverage ema = new MovingAverage(qh);
    ema.calculate(this.trendperiod, 1, PriceBar.ValueType.Close);
    return ema;
  }

  private boolean calcBullishEngulfing(int index, MovingAverage emaclose)
  {
    return (isDownTrend(emaclose, index)
            && isWhiteCandle(index)
            && isBlackCandle(index - 1)
            && qh.getPriceBar(index).getOpen() < qh.getPriceBar(index - 1).getClose()
            && qh.getPriceBar(index).getClose() > qh.getPriceBar(index - 1).getOpen()
            && isLongBody(index));
  }

  private int calcIndex(int index)
  {
    //return this.qh.size() + index - 1;
    return index;
  }

  /**
   * @return the bullishThreeWhiteSoldiers
   */
  public boolean isBullishThreeWhiteSoldiers(int index)
  {
    return bullishThreeWhiteSoldiers[calcIndex(index)];
  }

  public boolean isBullishKicking(int index)
  {
    return bullishKicking[calcIndex(index)];
  }

  public boolean isBullishAbandonedBaby(int index)
  {
    return bullishAbandonedBaby[calcIndex(index)];
  }

  private boolean isDownTrend(MovingAverage emaclose, int index)
  {
    return emaclose.getMaAll()[index] > qh.getPriceBar(index).getClose();
  }

  private boolean isUpTrend(MovingAverage emaclose, int index)
  {
    return emaclose.getMaAll()[index] < qh.getPriceBar(index).getClose();
  }

  public boolean isBigVolume(int index)
  {

    return (this.qh.getPriceBar(calcIndex(index)).getVolume()
            > volma.getMaAll()[calcIndex(index - 1)] * 2);


  }

  private boolean isBigBlack2(int index)
  {
    return (this.isBigBlack(calcIndex(index))
            //  && this.isBigVolume(calcIndex(index))
            && this.qh.getPriceBar(calcIndex(index)).getClose() < this.qh.getPriceBar(calcIndex(
            index - 1)).getClose());
  }

  /**
   * @return the bearishHarami
   */
  public boolean isBearishHarami(int index)
  {
    return bearishHarami[calcIndex(index)];
  }

  /**
   * @return the bearishPiercing
   */
  public boolean isBearishPiercing(int index)
  {
    return bearishPiercing[calcIndex(index)];
  }

  /**
   * @return the hangingMan
   */
  public boolean isHangingMan(int index)
  {
    return hangingMan[calcIndex(index)];
  }
}
