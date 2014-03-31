package com.amaggioni.datastructure;

import com.amaggioni.indicators.ATR;
import com.amaggioni.indicators.PeriodLow;
import core.PriceBar;
import com.amaggioni.indicators.QuoteHistory;
import com.amaggioni.myutilities.MyMath;
import com.amaggioni.myutilities.WriteToFile;
import com.ib.client.Contract;
import java.util.Date;

/**
 *
 * @author magang
 */
public class TradeOrder
{

    private String symbol;
    private String userReqTyp;
    private int requestId;
    private int quantity;
    private String action; // BUY or SELL
    private double checkPrice;
    private double orderPrice;
    private double stopPrice;
    private Contract contract;
    private boolean orderSubmitted;
    private String ocaGroup;
    private double riskprotrade;
    private StopType stoptype;
    private double percentstop;
    private String parentUserReqNr;
    private QuoteHistory qh;
    private int orderIdParent;
    private int orderIdStopLoss;

    public void symboldataToLog(String methodname, String symbol)
    {
        String outString;
        outString = new Date() + " " + "[Info] [" + methodname + "] [" + symbol + "] " + " quantity :" + this.getQuantity() + " checkprice :" 
                + this.getCheckPrice() + " orderprice " + this.getOrderPrice() + " stopprice :" + this.getStopPrice();
        System.out.println(outString);
        WriteToFile.writeF(outString, symbol);
    }

    /**
     * @param qh the qh to set
     */
    public void setQh(QuoteHistory qh)
    {
        this.qh = qh;
    }

  /**
   * @return the action
   */
  public String getAction()
  {
    return action;
  }

  /**
   * @param action the action to set
   */
  public void setAction(String action)
  {
    this.action = action;
  }

    public enum StopType
    {

        Percent, ATR, Value, lowValue
    }

    
    public TradeOrder(String symbol, String userReqTyp, int requestId, String action, int quantity, double checkPrice, double orderPrice,
            double stopPrice, Contract contract, String ocaGroup, double riskprotrade, StopType stoptype, double percentstop,
            String parentUserReqNr)
    {
        this.symbol = symbol;
        this.userReqTyp = userReqTyp;
        this.requestId = requestId;
        this.action = action;
        this.quantity = quantity;
        this.checkPrice = checkPrice;
        this.orderPrice = orderPrice;
        this.stopPrice = stopPrice;
        this.riskprotrade = riskprotrade;
        this.stoptype = stoptype;
        this.percentstop = percentstop;
        this.contract = contract;
        this.orderSubmitted = false;
        this.ocaGroup = ocaGroup;
        this.parentUserReqNr =  parentUserReqNr;
        this.qh = null;
        this.orderIdParent = 0;
        this.orderIdStopLoss = 0;
    }

    public boolean Calculate()
    {
        boolean okflag = true;

        String outString = null;

        if (this.quantity == 0)
        {

            //<editor-fold defaultstate="collapsed" desc="Checks">
            if (symbol == null || symbol.equals(""))
            {
                // error
                outString = new Date() + " " + "[Err] [Calculate] [" + symbol + "] "
                        + "symbol is empty";
                okflag = false;
            }
            if (okflag && orderPrice == 0)
            {
                // error
                outString = new Date() + " " + "[Err] [Calculate] [" + symbol + "] "
                        + "entryprice is 0";
                okflag = false;
            }
            
            if (okflag && riskprotrade == 0)
            {
                // error
                outString = new Date() + " " + "[Err] [Calculate] [" + symbol + "] "
                        + "risk-pro-trade is 0";
                okflag = false;
            }
            
            if (okflag && (stoptype.equals(StopType.Value)) && stopPrice == 0)
            {
                // error
                outString = new Date() + " " + "[Err] [Calculate] [" + symbol + "] "
                        + "stopprice is 0";
                okflag = false;
            }
            //</editor-fold>
           
        } else
        {

            outString = new Date() + " " + "[Fine] [Calculate] [" + symbol + "] "
                    + "quantity is not empty, calculate() works only with quantity = 0";
            okflag = false;
        }

        if (!okflag)
        {
            WriteToFile.logAll(outString, "");
        } else
        {

            if ((stoptype.equals(StopType.Percent) 
                    || stoptype.equals(StopType.ATR)
                    || stoptype.equals(StopType.lowValue)) && percentstop != 0)
            {
                setStopPrice(CalcStopprice());
            }
            
            setQuantity(CalcQuantity());

        }


        return okflag;
    }

    private double CalcStopprice()
    {
        double stopprice = 0;

        if (stoptype.equals(StopType.Percent))
        {
            stopprice = orderPrice - (orderPrice * percentstop / 100);
            
        }
        
        if (stoptype.equals(StopType.ATR))
        {
            ATR atr = new ATR(this.qh,5);
            double atrvalue = atr.calculate();
            
            stopprice = orderPrice -  (atrvalue * percentstop);
            
        }
        
        if (stoptype.equals(StopType.lowValue))
        {
            PeriodLow low = new PeriodLow(this.qh);
            double lowval = low.calculate(5, PriceBar.ValueType.Low);
            
            stopprice = (lowval - 0.01);
            
        }

        stopprice = MyMath.round( stopprice ,2);
        return stopprice;
    }

    private double CalcRiskAbs(double entryprice, double stopprice)
    {

        return entryprice - stopprice;
    }

    private double CalcTranCosts(int quantity)
    {
        int transcost = 1;


        return transcost;
    }

    private int CalcQuantity()
    {

        int quantity = (int) ((riskprotrade - CalcTranCosts(0) * 2) / CalcRiskAbs(orderPrice, stopPrice));

        return quantity;

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
     * @return the userReqTyp
     */
    public String getuserReqTyp()
    {
        return userReqTyp;
    }

    /**
     * @param userReqTyp the userReqTyp to set
     */
    public void setuserReqTyp(String userReqTyp)
    {
        this.userReqTyp = userReqTyp;
    }

    /**
     * @return the requestId
     */
    public int getRequestId()
    {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(int requestId)
    {
        this.requestId = requestId;
    }

    /**
     * @return the quantity
     */
    public int getQuantity()
    {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    /**
     * @return the checkPrice
     */
    public double getCheckPrice()
    {
        return checkPrice;
    }

    /**
     * @param checkPrice the checkPrice to set
     */
    public void setCheckPrice(double checkPrice)
    {
        this.checkPrice = checkPrice;
    }

    /**
     * @return the orderPrice
     */
    public double getOrderPrice()
    {
        return orderPrice;
    }

    /**
     * @param orderPrice the orderPrice to set
     */
    public void setOrderPrice(double orderPrice)
    {
        this.orderPrice = orderPrice;
    }

    /**
     * @return the stopPrice
     */
    public double getStopPrice()
    {
        return stopPrice;
    }

    /**
     * @param stopPrice the stopPrice to set
     */
    public void setStopPrice(double stopPrice)
    {
        this.stopPrice = stopPrice;
    }

    /**
     * @return the contract
     */
    public Contract getContract()
    {
        return contract;
    }

    /**
     * @param contract the contract to set
     */
    public void setContract(Contract contract)
    {
        this.contract = contract;
    }

    /**
     * @return the orderSubmitted
     */
    public boolean isOrderSubmitted()
    {
        return orderSubmitted;
    }

    /**
     * @param orderSubmitted the orderSubmitted to set
     */
    public void setOrderSubmitted(boolean orderSubmitted)
    {
        this.orderSubmitted = orderSubmitted;
    }

    /**
     * @return the ocaGroup
     */
    public String getOcaGroup()
    {
        return ocaGroup;
    }

    /**
     * @param ocaGroup the ocaGroup to set
     */
    public void setOcaGroup(String ocaGroup)
    {
        this.ocaGroup = ocaGroup;
    }

  /**
   * @return the orderIdParent
   */
  public int getOrderIdParent()
  {
    return orderIdParent;
  }

  /**
   * @param orderIdParent the orderIdParent to set
   */
  public void setOrderIdParent(int orderIdParent)
  {
    this.orderIdParent = orderIdParent;
  }

  /**
   * @return the orderIdStopLoss
   */
  public int getOrderIdStopLoss()
  {
    return orderIdStopLoss;
  }

  /**
   * @param orderIdStopLoss the orderIdStopLoss to set
   */
  public void setOrderIdStopLoss(int orderIdStopLoss)
  {
    this.orderIdStopLoss = orderIdStopLoss;
  }

    /**
     * @return the riskprotrade
     */
    public double getRiskprotrade()
    {
        return riskprotrade;
    }

    /**
     * @param riskprotrade the riskprotrade to set
     */
    public void setRiskprotrade(double riskprotrade)
    {
        this.riskprotrade = riskprotrade;
    }

    /**
     * @return the stoptype
     */
    public StopType getStoptype()
    {
        return stoptype;
    }

    /**
     * @param stoptype the stoptype to set
     */
    public void setStoptype(StopType stoptype)
    {
        this.stoptype = stoptype;
    }
}
