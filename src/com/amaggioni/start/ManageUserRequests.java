package com.amaggioni.start;

import com.amaggioni.datastructure.TradeOrder;
import com.amaggioni.IBordermanagement.TradesCSV;
import com.amaggioni.XML.ReadConfiguration;
import com.amaggioni.candles.CandlePattern;
import com.amaggioni.concurrentqueue.CurrDataConcQueue;
import com.amaggioni.concurrentqueue.CurrDataPriceBar;
import com.amaggioni.concurrentqueue.HistDataConcQueue;
import com.amaggioni.concurrentqueue.ReqIdPriceBar;
import com.amaggioni.myutilities.WaitTillTime;

import com.amaggioni.datastructure.RequestQuotesHistorical;

import com.amaggioni.datastructure.RequestHistoricalMktDta;
import com.amaggioni.datastructure.RequestData;
import com.amaggioni.datastructure.TradeOrder.StopType;
import com.amaggioni.indicators.AvgHourlyVol;
import com.amaggioni.strategies.ChannelStrategy;
import com.amaggioni.indicators.MovingAverage;
import core.PriceBar;
import com.amaggioni.myutilities.QuoteDB;
import com.amaggioni.myutilities.WriteToFile;
import com.amaggioni.myutilities.MyMath;
import com.amaggioni.strategies.AutoEnvelopeStrategy1;
import com.amaggioni.strategies.ChannelConvergenceStrategy1;
import com.amaggioni.strategies.ChannelConvergenceStrategy2;
import com.amaggioni.strategies.CongestionStrategy1;
import com.amaggioni.strategies.CongestionStrategy2;
import com.amaggioni.strategies.DynamicTradeStrategy1;
import com.amaggioni.strategies.EODBigVolumeStrategy1;
import com.amaggioni.strategies.MACDLowStrategy2;
import com.amaggioni.strategies.ROCStrategy1;
import com.amaggioni.strategies.StandardErrorBandsStrategy1;
import com.ib.client.EWrapperMsgGenerator;
import com.ib.client.Order;
import com.ib.client.TickType;
import com.ib.client.examples.ExampleBase;
import com.ib.client.examples.util.RequestIDManager;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
 * 1. connect to TWS
 * 2. request market data
 * 3. wait for lastprices
 * 4. check lastprice against strategy parameters
 * 5. if 4 is ok * generate an order
 *
 * 6. disconnect to TWS whereby this part is not reached at the moment, the application loop between 3 an 5, to stop is only manual
 *
 * Change Log:
 * 28.01.2013 OCAGroup added by Orders
 * 30.01.2013 Output to File addes
 * 31.01.2013 Order Tif changed to GTC
 * 01.02.2012 Wait-till-time added
 * 01.02.2012 Read CSV IBOrder File added
 * 05.02.2013 Low-Price added / Removed log prices
 * 07.02.2013 read trades from csv file into List<String[]> , change Trade status and write back to csv
 * 13.02.2013 mangement of 'AlertPrice' added
 * 24.02.2013 management of 'Historical requests' for HistoricalVolume added
 * 04.03.2013 Refactoring von HashMaps, new RequestTyp 'GetHourlyData' logic added
 * 06.03.2013 Refactoring 'RequestQuotesHistorical' add Indicators
 * 08.03.2013 Indicator ADX added
 * 08.03.2013 Message management changed
 * 11.03.2013 Indicator 'Average Hourly Volume' added
 * 11.03.2013 'RequestData' refactored
 * 13.03.2012 'hisReqHash' refactored
 * 13.03.2012 'WriteToFile' refactored
 * 17.03.2013 'HistoryDataConcurrentQueue' added
 * 22.03.2013 'CurrDataConcQueue' added
 * 22.03.2013 doTrade refactored
 * 27.03.2013 'CalculateTrade' added
 * 04.04.2013 'SymboldData' refactored
 * 04.04.2013 args added as parameter
 * 16.04.2013 'Alert' Methods removed
 * 02.05.2013 'OrderID' and 'StopLoss' Number stored
 * 16.05.2013 'MACDLow' Strategy added
 * 20.05.2013 refactored 'Check' Methods
 * 23.05.2013 'orderStatus' management added
 * 23.05.2013 'LogAll' added and refactored
 * 31.05.2013 Get COnfiguration from XML File
 * 06.06.2013 End Application after EndTime
 * 29.06.2013 Start and Endtime for EOD Strategy
 * 12.07.2013 'Standard Error Bands' added
 * 09.08.2013 various 'lookback'period
 * 16.04.2014 Logic trade submission changed
 * <p/>
 * <
 * p/>
 * <
 * p/>
 * To Dos:
 * <p/>
 * 1. [API.msg2] Historical Market Data Service error message:Historical data request pacing violation {25, 162}
 * 2. Start Entry in Logfile
 * 3. Format Performance figures
 * 3.1 Mon Mar 04 13:32:54 CET 2013 [Info] [doVolumeCheck] [SPY] Volume above average : 2521.802325581395
 * <p/>
 */
public class ManageUserRequests extends ExampleBase
{

    public static final String x1_HOUR = "1 hour";
    public static final String x15_MINS = "15 mins";
    public static final String x1_DAY = "1 day";
    public static final String VOLUME_CHECK = "VolumeCheck";
    public static final String HOURLY_DATA = "GetHourlyData";
    public static final String TRADE = "Trade";
    public static final String BREAKOUT = "Breakout";
    public static final String DYNAMIC_TRADE = "DynamicTrade";
    public static final String EOD_TRADE = "EODTrade";

    /*
     * *********************************************************
     * 
     * the Class is using two HasMaps to store symbols information, strategy and order parameters (tradesHash)
     * 
     * and the low for each requestID (requestHash)
     * 
     * Two HasMaps are needed as we need two keys: symbol and requestID
     * 
     */
    //--------------------------------------------------------------------------------
    // HashMap where symbols and related datas as requestID, checkPrice, orderPrice are stored
    private HashMap<String, TradeOrder> tradesHash;
    // HistoryRequests HashMap
    private HashMap<String, RequestHistoricalMktDta> histReqHash;
    //
    //--------------------------------------------------------------
    // HashMap where for each requestID the low is stored
    protected HashMap<Integer, RequestData> requestHash;
    //
    private HashMap<Integer, RequestQuotesHistorical> reqQuoHistHash;
    //---------------------------------------------------------------
    // volume percentage limit
    private static final int VOLPERCLIMIT = 120;
    // CSV File Variables
    private static final String FILE_PATH = "C:/Users/magang/Documents/Logs/";
    private static String FILE_NAME;
    private static final char SEPARATOR = ';';
    private TradesCSV tradesCSV;
    // HistoryData ConcurrentQueue
    HistDataConcQueue hdcq;
    // Current Data ConcurrentQueue
    CurrDataConcQueue cdcq;
    // LOGs Constants
    private boolean LOG_CANDLE = false;
    private boolean LOG_HISTORICALDATA = false;
    private boolean LOG_REQHISTDATA = false;
    private boolean LOG_CONVERGENCE = false;
    private GregorianCalendar startdate;
    private int startHour = 12;
    private int startMinute = 15;

    /**
     * ***********************************************************************************************
     * constructor
     * <p/>
     * It just generate the instances
     * <p/>
     */
    public ManageUserRequests(String[] args)
    {
        tradesHash = new HashMap();

        requestHash = new HashMap();
        histReqHash = new HashMap();
        reqQuoHistHash = new HashMap();

        try {
            ReadConfiguration.main(args);
        } catch (Exception ex) {
            String outString = new Date() + " " + "[Err] [ManageUserRequests]  File not found :";
            WriteToFile.logAll(outString, "");
        }

        TWS_PORT = ReadConfiguration.getTWSPort();
        //TWS_PORT = Integer.valueOf(args[0]);  // 7499 Paper-Trading for 7496 Live-Trading
        //FILE_NAME = args[1];                  // File with UserRequests
        //FILE_NAME = ReadConfiguration.getInputfile();

        //WriteToFile.setFile(args[2]);         // File where to write Logs
        WriteToFile.setFile(ReadConfiguration.getLogfile());
        WriteToFile.setPath(ReadConfiguration.getLogfilepath());

        tradesCSV = new TradesCSV(ReadConfiguration.getInputfilepath(),
                ReadConfiguration.getInputfile(), SEPARATOR);

        hdcq = new HistDataConcQueue();
        cdcq = new CurrDataConcQueue();

        getStartHourMinute();
    }

    /**
     * *******************************************************************************************
     * main method
     *
     * flow:
     * 1. connect to TWS
     * 2. request market data for each symbols
     * 3. check the strategy
     *
     * 4. disconnect TWS whereby this part is not done at the moment
     */
    public void runx()
    {
        try {

            connectToTWS();

            // Wait untill Markets open
            new WaitTillTime().doWait(this.startHour, this.startMinute, 00);

            WriteToFile.logAll("Start", "");
            generateRequestData();

            loop();

        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println(" Throwable :" + t);
            WriteToFile.writeF(t.getStackTrace().toString(), "");
        } finally {
            disconnectFromTWS();
        }
    }

    /**
     * *******************************************************************************************
     * setRequestId method
     *
     * Setup the HashMap with the requestID number of the Market Data Request
     * the low field is set to 0
     *
     */
    public void setRequestId(String symbol, int requestId)
    {
        requestHash.put(requestId, new RequestData(symbol));
    }

    public void setHistoricalHashId(int requestId, String symbol)
    {
        reqQuoHistHash.put(requestId, new RequestQuotesHistorical(symbol));
    }

    /**
     ********************************************************************************************
     * setTradeId method
     * <p/>
     * Setup the HashMap with the symbol and all information needed to check the strategy
     * and to create an order
     * <p/>
     * <
     * p/>
     */
    public void setTradeId(String symbol, String userReqTyp, String userReqNr, int requestId,
            int quantity, double checkPrice,
            double orderPrice, double stopPrice,
            String ocaGroup, double riskprotrade, StopType stoptype, double percentstop,
            String parentUserReqNr)
    {

        tradesHash.put(userReqNr,
                new TradeOrder(symbol, userReqTyp, requestId, "BUY", quantity, checkPrice,
                orderPrice,
                stopPrice,
                createContract(symbol, "STK", "SMART", "USD"), ocaGroup,
                riskprotrade, stoptype, percentstop, parentUserReqNr));
    }

    /**
     *
     * @param symbol
     * @param status
     * @param requestId
     * @param requestIdHst
     */
    public void sethistReqHash(String userReqTyp, String userReqNr, String symbol, int requestId,
            int requestIdHst, String parentUserReqNr, String barsize, GregorianCalendar datex)
    {



        histReqHash.put(userReqNr, new RequestHistoricalMktDta(userReqTyp, userReqNr, symbol,
                requestId, false,
                barsize, // backfilldurationtime
                (barsize.equals(x1_DAY) ? "1 M" : // Duration
                (barsize.equals(x1_HOUR) ? "1 M"
                : (barsize.equals(x15_MINS) ? "1 W" : ""))),
                barsize, // bar size
                1,
                1,
                "TRADES",
                createContract(symbol,
                "STK", "SMART", "USD"),
                requestIdHst,
                parentUserReqNr,
                barsize,
                datex));

    }

    /**
     **********************************************************************************************
     * @param tickerId
     * @return
     * <p/>
     * isDataReady method
     * <p/>
     * the method just check whether there is a new low for the symbol
     * <p/>
     */
    public boolean isDataReady_old(int tickerId)
    {

        if (requestHash.containsKey(tickerId)) {

            if (requestHash.get(tickerId).getLow() == 0.0) {
                return false;
            } else {
                return true;
            }

        }

        return false;
    }

    /**
     ***************************************************************************************************
     * <p/>
     * doTrades method
     * <p/>
     * the method check the strategy and submit accordingly the orders
     * <p/>
     * flow:
     * <p/>
     * 1. read symbols and related lastPrices
     * 2. check the strategy
     * 3. submit the orders
     * 4. set flag and reset low
     * <p/>
     * <
     * p/>
     */
    public void doTrades()
    {

        //System.out.println("[Debug] [doTrades] Thread Name: " + Thread.currentThread().getName() + " lock :" + Thread.holdsLock(requestHash));
        // read symbols
        for (String userReqNr : tradesHash.keySet()) {
            TradeOrder to = tradesHash.get(userReqNr);
            String symbol = to.getSymbol();

            // read low / close

            RequestData rq = requestHash.get(to.getRequestId());
            double low = rq.getLow();
            double close = rq.getClose();
            double pricediff = MyMath.round(Math.abs(to.getCheckPrice() - to.getOrderPrice()),2);
            double closediff = MyMath.round(Math.abs(close - to.getCheckPrice()),2);

            // check strategy
            if ((   low != 0 && to.getCheckPrice() != 0
                    && to.getStopPrice() != 0
                    && (/*(Math.abs(to.getCheckPrice() - to.getOrderPrice()) != 0.01 && to.getCheckPrice() > low && to.getOrderPrice() > close) 
                        || */ to.getCheckPrice() == to.getOrderPrice()
                        || (pricediff == 0.01 && closediff <= 0.01 ))
                    && !to.isOrderSubmitted())
                    && (to.getuserReqTyp().equals(TRADE) || to.getuserReqTyp().equals(DYNAMIC_TRADE) || to.getuserReqTyp().equals(EOD_TRADE))) {
                String outString = new Date() + " " + "[Info] [doTrades] [" + symbol + "] Low :" + low + " CheckPrice :"
                        + to.getCheckPrice() + " Close :" + close + " Diff :" + closediff;
                WriteToFile.logAll(outString, symbol);

                // Check whether the quantitty need to be calculated
                if (to.getQuantity() == 0 && to.getOrderPrice() != 0 && to.getStopPrice() != 0) {
                    to.Calculate();
                }
                // Submit Trade and update status files
                submitTrade(userReqNr, to);

            }
        }
    }

    /**
     *
     * @param userReqNr
     * @param to
     */
    public void submitTrade(String userReqNr, TradeOrder to)
    {
        // Set Order to Status Submitted 
        to.setOrderSubmitted(true);

        // Place Orders
        createOrderBracket(userReqNr);

        chgTradesCSV(userReqNr, to, "Submitted");
    }

    public RequestHistoricalMktDta searchReqHistMktDta(int requestId)
    {
        for (String userReqNr : histReqHash.keySet()) {
            RequestHistoricalMktDta histreq = histReqHash.get(userReqNr);
            if (histreq.getRequestIdHist() == requestId || histreq.getRequestId() == requestId) {
                return histreq;
            }
        }

        return null;
    }

    /**
     *
     */
    public void doVolumeCheck()
    {

        //System.out.println("Test doVolumeCheck");
        //System.out.println("[Debug] [doVolumeCheck] Thread Name: " + Thread.currentThread().getName() + " lock :" + Thread.holdsLock(requestHash));
        // read symbols
        for (String userReqNr : histReqHash.keySet()) {
            RequestHistoricalMktDta histreq = histReqHash.get(userReqNr);
            String symbol = histreq.getSymbol();

            if (histreq.getUserReqType().equals(VOLUME_CHECK)) {
                int requestId = histreq.getRequestId();
                int requestIdHst = histreq.getRequestIdHist();

                // read volume
                double volume = requestHash.get(requestId).getVolume();

                // all Historical data are readed and the volume is still under the limit
                if (reqQuoHistHash.get(requestIdHst).isHistorie()
                        && !reqQuoHistHash.get(requestIdHst).isVolumeLimit()
                        && volume > 0) {
                    //try
                    //{
                    MovingAverage ma = new MovingAverage(reqQuoHistHash.get(requestIdHst).getQh());
                    double volhist = ma.calculate(10, 0, PriceBar.ValueType.Volume);

                    //double volhist = reqQuoHistHash.get(requestIdHst).avgVolume(10);
                    double volperc = volume / volhist * 100;

                    String outString = new Date() + " " + "[Fine] [doVolumeCheck] [" + symbol + "] "
                            + "Volume is :" + volperc + " of Average /  Volume value :" + volume
                            + " Volume Historical average : " + volhist;
                    //System.out.println(outString);
                                /*writeToFile(outString, symbol);*/

                    if (volperc > VOLPERCLIMIT) {

                        outString = new Date() + " " + "[Info] [doVolumeCheck] [" + symbol + "] "
                                + " Volume above average : " + volperc;
                        WriteToFile.logAll(outString, symbol);

                        reqQuoHistHash.get(requestIdHst).setVolumeLimit(true);

                        // Check whether a Trade has to be submitted
                        TradeOrder symbolData = tradesHash.get(symbol);
                        if (symbolData != null && !symbolData.isOrderSubmitted()) {
                            submitTrade(userReqNr, symbolData);
                        }

                    }
                    /*} catch (Exception ex)
                     {
                     ex.printStackTrace();
                     }*/
                }
            }
        }
    }

    /**
     *
     */
    public void doHistoricalDataCheck()
    {

        // read symbols
        for (String userReqNr : histReqHash.keySet()) {
            RequestHistoricalMktDta histreq = histReqHash.get(userReqNr);
            int requestIdHist = histreq.getRequestIdHist();
            RequestQuotesHistorical rqh = reqQuoHistHash.get(requestIdHist);

            if (histreq.getUserReqType().equals(HOURLY_DATA)
                    && rqh.isHistorie()) {
                final String parentUserReqNr = histreq.getParentUserReqNr();
                final TradeOrder to = this.tradesHash.get(parentUserReqNr);

                //<editor-fold defaultstate="collapsed" desc="Hourly Historical Data">
                if (histreq.getBarsize() != null && histreq.getBarsize().equals(x1_HOUR)) {


                   // checkChannelConvergenceStrategy2(histreq, rqh, to);

                    // checkMACDLowStrategy1(histreq, rqh, to, parentUserReqNr);

                    // checkROCStrategy1(histreq, rqh, to, parentUserReqNr);

                    checkAutoEnvelopeStrategy1(histreq, rqh, to);

                    // checkStandardErrorBandsStrategy1(histreq, rqh, to);
                }
                //</editor-fold>


                //<editor-fold defaultstate="collapsed" desc="15 mins Historical Data">
                if (histreq.getBarsize() != null && histreq.getBarsize().equals(x15_MINS)) {
                    if (to != null && to.getuserReqTyp().equals(DYNAMIC_TRADE)) {
                        checkCongestionStrategy2(histreq, rqh);

                        checkMACDLowStrategy1(histreq, rqh, to, parentUserReqNr);

                        checkROCStrategy1(histreq, rqh, to, parentUserReqNr);

                        checkDynamicTradeStrategy1(histreq, rqh, to);
                    }

                    if (to != null && to.getuserReqTyp().equals(EOD_TRADE)) {
                        checkEODStrategy1(histreq, rqh, to);
                    }
                }
                //</editor-fold>

                checkDate(histreq, userReqNr, requestIdHist);

            }


        }
    }

    private void calcStartDate()
    {
        this.startdate = new GregorianCalendar();
        //this.startdate.setTimeZone(TimeZone.getTimeZone("EST"));
        this.startdate.set(Calendar.HOUR_OF_DAY, startHour);
        this.startdate.set(Calendar.MINUTE, startMinute);
        this.startdate.set(Calendar.SECOND, 0);

        //System.out.println("Time Zone" + startdate.getTimeZone());
    }

    private void getStartHourMinute()
    {

        //this.startdate = ReadConfiguration.getStarttime();
        startHour = ReadConfiguration.getStarttime().get(Calendar.HOUR_OF_DAY);
        startMinute = ReadConfiguration.getStarttime().get(Calendar.MINUTE);
        this.calcStartDate();
    }

    /**
     *************************************************************************************************
     * @param symbol
     * @param quantity
     * @param checkPrice
     * @param orderPrice
     * @param stopPrice
     * @throws InterruptedException
     * <p/>
     * rqstMktDtaTrade method
     * <p/>
     * <
     * p/>
     */
    public void rqstMktDtaTrade(String userReqTyp, String userReqNr, String symbol, int quantity,
            double checkPrice, double orderPrice,
            double stopPrice, String ocaGroup, double riskprotrade, StopType stoptype,
            double percentstop, String parentUserReqNr)
            throws InterruptedException
    {

        // Request next requestId number
        int requestId = RequestIDManager.singleton().getNextRequestId();

        // Store information in HashMaps
        setRequestId(symbol, requestId);
        setTradeId(symbol, userReqTyp, userReqNr, requestId, quantity, checkPrice, orderPrice,
                stopPrice, ocaGroup,
                riskprotrade, stoptype, percentstop, parentUserReqNr);

        // Requests  market data
        eClientSocket.reqMktData(requestId, tradesHash.get(userReqNr).getContract(), "233", false);


    }

    /**
     *
     * @param usrReqTyp
     * @param userReqNr
     * @param symbol
     * @param parentUserReqNr
     * @param barsize
     * @param datex
     */
    public void rqstHistoryMktDta(String usrReqTyp, String userReqNr, String symbol,
            String parentUserReqNr, String barsize, GregorianCalendar datex)
    {

        // Request next requestId number
        int requestId = RequestIDManager.singleton().getNextRequestId();
        // Request next requestId number
        int requestIdHst = RequestIDManager.singleton().getNextRequestId();

        // Store information in HashMaps
        setRequestId(symbol, requestId);
        sethistReqHash(usrReqTyp, userReqNr, symbol, requestId, requestIdHst, parentUserReqNr,
                barsize, datex);

        // Requests  market data
        RequestHistoricalMktDta hr = histReqHash.get(userReqNr);
        eClientSocket.reqMktData(requestId, hr.getContract(), "233", false);

        setHistoricalHashId(requestIdHst, symbol);
        reqHistData(requestIdHst, hr);


    }

    /**
     *
     * @param status
     * @param symbol
     */
    public void updMktDtaHourly(String usrReqTyp, String userReqNr, String symbol,
            String parentUserReqNr, String barsize, GregorianCalendar datex)
    {

        // Request next requestId number
        int requestIdHst = RequestIDManager.singleton().getNextRequestId();



        RequestHistoricalMktDta hr = histReqHash.get(userReqNr);
        sethistReqHash(usrReqTyp, userReqNr, symbol, hr.getRequestId(), requestIdHst,
                parentUserReqNr, barsize, datex);


        hr = histReqHash.get(userReqNr);
        setHistoricalHashId(requestIdHst, symbol);
        reqHistData(requestIdHst, hr);



    }

    /**
     **************************************************************************************************
     * @throws InterruptedException
     */
    public void loop() throws InterruptedException
    {


        while (ReadConfiguration.getEndtime().compareTo(new GregorianCalendar()) > 0) {

            //while (true) {

            // System.out.println("[Debug] [loop] Thread Name: " + Thread.currentThread().getName());

            getQueue();
            // System.out.println("[listMarketData]" + waitCount);

            doTrades();

            doVolumeCheck();

            doHistoricalDataCheck();

            sleep(WAIT_TIME); // Pause for 1 sec

        }

        // Cancel market data
        //   eClientSocket.cancelMktData(requestId);


    }

    /**
     *
     */
    private void getQueue()
    {
        boolean stopCondition = (hdcq.getQueueSize() == 0);

        while (!stopCondition) {

            for (int i = 0; i < hdcq.getQueueSize(); i++) {
                //System.out.println("Client dequeue item " + hdcq.dequeueItem().toString());
                //<editor-fold defaultstate="collapsed" desc="read Queue Items">
                ReqIdPriceBar dequeueItem = hdcq.dequeueItem();
                //
                final int requestId = dequeueItem.getRequestId();
                RequestHistoricalMktDta rhmd = searchReqHistMktDta(requestId);
                String outString = new Date() + " [Fine] [getQueue] "
                        + "[" + this.reqQuoHistHash.get(requestId).getSymbol() + "] "
                        + "[" + (rhmd.getBarsize() == null ? "null" : rhmd.getBarsize()) + "] "
                        //+ "[" + reqQuoHistHash.get(reqId). + "]"
                        + EWrapperMsgGenerator.historicalData(requestId,
                        dequeueItem.getDate(),
                        dequeueItem.getOpen(),
                        dequeueItem.getHigh(), dequeueItem.getLow(), dequeueItem.getClose(),
                        (int) dequeueItem.getVolume(),
                        dequeueItem.getCount(), dequeueItem.getWAP(), false);

                if (!dequeueItem.getDate().startsWith("finished")) {

                    //System.out.println(outString);

                    //writeToFile(outString, this.reqQuoHistHash.get(reqId).getSymbol());

                    // String date, double open, double close, double high, double low,  double WAP, int count, double volume
                    this.reqQuoHistHash.get(requestId).addList(
                            dequeueItem.getDate(), dequeueItem.getOpen(),
                            dequeueItem.getClose(), dequeueItem.getHigh(), dequeueItem.getLow(),
                            dequeueItem.getWAP(), dequeueItem.getCount(), dequeueItem.getVolume());
                } else {


                    //System.out.println(outString);
                    if (LOG_HISTORICALDATA) {
                        final RequestQuotesHistorical rqh = this.reqQuoHistHash.get(requestId);
                        final String symbol = rqh.getSymbol();
                        WriteToFile.writeF(rqh.toStringx(1), symbol);
                        WriteToFile.writeF(outString, symbol);
                    }
                    // this.reqQuoHistHash.get(requestId).getQh().delLastPriceBar();
                    this.reqQuoHistHash.get(requestId).setHistorie(true);
                }
                //</editor-fold>


            }
            stopCondition = (hdcq.getQueueSize() == 0);
        }


        // Read current data concurrent queue
        stopCondition = (cdcq.getQueueSize() == 0);

        while (!stopCondition) {

            for (int i = 0; i < cdcq.getQueueSize(); i++) {

                CurrDataPriceBar dequeueItem = cdcq.dequeueItem();

                //<editor-fold defaultstate="collapsed" desc="Read current data">
                if (dequeueItem.getField() == TickType.LOW) {
                    //System.out.println(outString);
                    //writeToFile(outString, this.requestHash.get(tickerId).getSymbol());
                    requestHash.get(dequeueItem.getTickerId()).setLow(dequeueItem.getDoubleField());
                }

                if (dequeueItem.getField() == TickType.LAST) {

                    //System.out.println(outString);
                    //writeToFile(outString, this.requestHash.get(tickerId).getSymbol());
                    requestHash.get(dequeueItem.getTickerId()).setClose(dequeueItem.getDoubleField());
                }

                if (dequeueItem.getField() == TickType.HIGH) {
                    //System.out.println(outString);
                    //writeToFile(outString, this.requestHash.get(tickerId).getSymbol());
                    requestHash.get(dequeueItem.getTickerId()).setHigh(dequeueItem.getDoubleField());
                }

                if (dequeueItem.getField() == 48) {
                    //System.out.println(outString);
                    //writeToFile(outString, this.requestHash.get(tickerId).getSymbol());
                    requestHash.get(dequeueItem.getTickerId()).setVolume(
                            dequeueItem.getDoubleField());
                }
                //</editor-fold>

            }
            stopCondition = (cdcq.getQueueSize() == 0);
        }

        //System.out.println("get Queue Client Thread exiting");
    }

    /**
     ***********************************************************************************************
     * @param symbol
     * <p/>
     * <
     * p/>
     */
    public void createOrderBracket(String userReqNr)
    {
        if (RequestIDManager.singleton().isOrderIdInitialized()) {
            final TradeOrder to = tradesHash.get(userReqNr);

            // adapt transmit = 0 m_lmtPrice m_auxPrice
            Order BuyOrder = createOrder(to.getAction(), to.getQuantity(), "STPLMT",
                    to.getOrderPrice(),
                    to.getOrderPrice(),
                    false, 0, to.getOcaGroup());

            int parent = RequestIDManager.singleton().getNextOrderId();
            // Place Stop Limit Order transmit = 0 m_lmtPrice m_auxPrice
            eClientSocket.placeOrder(parent, to.getContract(), BuyOrder);
            to.setOrderIdParent(parent);


            // Stop at 50% of the planned stoploss
            double newstopprice = to.getStopPrice();
            //<editor-fold defaultstate="collapsed" desc="Stopprice / 2">
            if (false) {
                newstopprice = (to.getOrderPrice() - to.getStopPrice()) / 2
                        + to.getStopPrice();
                newstopprice = MyMath.round(newstopprice, 2);
            }
            //</editor-fold>

            // adapt transmit = 1  m_auxPrice and m_parentId
            Order StopLossOrder = createOrder((to.getAction().equals("BUY") ? "SELL" : "BUY"),
                    to.getQuantity(), "STP",
                    newstopprice, 0, true, parent, "");
            final int nextOrderId = RequestIDManager.singleton().getNextOrderId();
            // Place StopLoss Order 
            eClientSocket.placeOrder(nextOrderId, to.getContract(), StopLossOrder);
            to.setOrderIdStopLoss(nextOrderId);

            String outString = new Date() + " [Info] [createOrderBracket] [" + to.getSymbol() + "] OrderPrice "
                    + to.getOrderPrice()
                    + " StopPrice " + to.getStopPrice();
            WriteToFile.logAll(outString, to.getSymbol());


        } else {
            System.out.println("[Debug] [createOrderBracket] OrderID not initialized");
        }
    }

    /** *****************************************************************************************
     * description: read IB Orders from CSV File and generate rqstMktDtaTrade
     * <p/>
     * @return
     * <p/>
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     */
    public void generateRequestData() throws FileNotFoundException, IOException, InterruptedException
    {


        tradesCSV.getTrades();
        List<String[]> allElements = tradesCSV.getAllTrades();

        for (Iterator<String[]> iterator = allElements.iterator(); iterator.hasNext();) {
            String[] s = iterator.next();

            if ((s[0].equals(TRADE)
                    || s[0].equals(VOLUME_CHECK)
                    || s[0].equals(BREAKOUT)
                    || s[0].equals(HOURLY_DATA)
                    || s[0].equals(DYNAMIC_TRADE)
                    || s[0].equals(EOD_TRADE)) && !s[2].equals("")) {


                if (s[0].equals(TRADE)
                        || s[0].equals(BREAKOUT)
                        || s[0].equals(DYNAMIC_TRADE)
                        || s[0].equals(EOD_TRADE)) {
                    /*System.out.println(
                     * "Req. Type :" + s[0]
                     * + " Req. Nummer " + s[1]
                     * + " Ticket  :" + s[2]
                     * + " Quantity  :" + s[3]
                     * + " checkPrice  :" + s[4]
                     * + " entryPrice  :" + s[5]
                     * + " stoppPrice  :" + s[6]
                     * + " OCAGroup  :" + s[7]
                     * + " risiko  :" + s[8]
                     * + " stoptype  :" + s[9]
                     * + " percstop  :" + s[10]
                     * + " parent  :" + s[11]);*/


                    rqstMktDtaTrade(
                            s[0],
                            s[1],
                            s[2],
                            !s[3].equals("") ? Integer.valueOf(s[3]) : 0,
                            !s[4].equals("") ? Double.valueOf(s[4]) : 0,
                            !s[5].equals("") ? Double.valueOf(s[5]) : 0,
                            !s[6].equals("") ? Double.valueOf(s[6]) : 0,
                            s[7],
                            !s[8].equals("") ? Double.valueOf(s[8]) : 0,
                            !s[9].equals("") ? StopType.valueOf(s[9]) : null,
                            !s[10].equals("") ? Double.valueOf(s[10]) : 0,
                            s[11]);

                }



                if (s[0].equals(VOLUME_CHECK) || s[0].equals(HOURLY_DATA)) {
                    /*System.out.println(
                     * "Req. Type :" + s[0]
                     * + " Req. Nummer " + s[1]
                     * + " Ticket  :" + s[2]);*/

                    rqstHistoryMktDta(
                            s[0],
                            s[1],
                            s[2],
                            s[11],
                            s[12],
                            startdate);
                }


            }

        }

    }

    /**
     ***************************************************************************************************
     * @param action
     * @param quantity
     * @param orderType
     * @param auxPrice
     * @param lmtPrice
     * @param transmit
     * @param parent
     * @return
     */
    public Order createOrder(String action, int quantity, String orderType, double auxPrice,
            double lmtPrice, boolean transmit, int parent,
            String ocaGroup)
    {
        Order order = new Order();

        order.m_tif = "GTC";
        order.m_action = action;
        order.m_totalQuantity = quantity;
        order.m_orderType = orderType;
        order.m_lmtPrice = lmtPrice;
        order.m_auxPrice = auxPrice;
        order.m_transmit = transmit;
        order.m_parentId = parent;
        order.m_ocaGroup = ocaGroup;
        order.m_ocaType = 1;

        return order;
    }

    /**
     **********************************************************************************************************
     * @param tickerId
     * @param field
     * @param price
     * @param canAutoExecute
     */
    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute)
    {


        //System.out.println("[Debug] [tickPrice] Thread Name: " + Thread.currentThread().getName() + " lock :" + Thread.holdsLock(requestHash));

        String outString;
        outString = new Date() + " [Fine] [API.tickPrice] "
                + "[" + this.requestHash.get(tickerId).getSymbol() + "] "
                + EWrapperMsgGenerator.tickPrice(tickerId, field, price,
                canAutoExecute);

        CurrDataPriceBar currData = new CurrDataPriceBar(tickerId, field, price, canAutoExecute);
        cdcq.enqueueItem(currData);

    }

    /**
     ***************************************************************************************************************
     * @param orderId
     */
    @Override
    public void nextValidId(int orderId)
    {

        System.out.println(" [API.nextValidId] " + EWrapperMsgGenerator.nextValidId(orderId));
        RequestIDManager.singleton().initializeOrderId(orderId);
    }

    /**
     *
     * @param tickerId
     * @param field
     * @param size
     */
    @Override
    public void tickSize(int tickerId, int field, int size)
    {

        if (field == TickType.VOLUME && tickerId == 1) {
            /*String outString = new Date() + " [Fine] [API.tickSize ] "
             + "[" + this.requestHash.get(tickerId).getSymbol() + "] "
             + EWrapperMsgGenerator.tickSize(tickerId, field, size);*/
            //writeToFile(outString, this.requestHash.get(tickerId).getSymbol());
        }

        if (field == TickType.AVG_VOLUME && tickerId == 1) {
            System.out.println("[Info] [API.tickSize ] " + EWrapperMsgGenerator.tickSize(tickerId,
                    field, size));

        }


    }

    /**
     *
     * @param tickerId
     * @param field
     * @param value
     */
    @Override
    public void tickString(int tickerId, int field, String value)
    {
        if (field == 48) {
            //System.out.println("[Info] [API.tickString ] " + EWrapperMsgGenerator.tickString(tickerId, field, value));

            String[] RTValue = value.split(";");
            //String outString = new Date() + " [Fine] [API.tickString ] RTVolume" + RTValue[3];
            //writeToFile(outString, this.requestHash.get(tickerId).getSymbol());

            CurrDataPriceBar currData = new CurrDataPriceBar(tickerId, field,
                    Double.valueOf(RTValue[3]), 0);
            cdcq.enqueueItem(currData);

        }
    }

    @Override
    public void tickGeneric(int tickerId, int field, double generic)
    {

        if (field == 48. && tickerId == 1) {
            System.out.println("[Info] [API.tickGeneric ] " + EWrapperMsgGenerator.tickGeneric(
                    tickerId, field, generic));

        }
    }

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low,
            double close, int volume, int count,
            double WAP, boolean hasGaps)
    {

        //HistoryRequest histreq = histReqHash.get(this.reqQuoHistHash.get(reqId).getSymbol());

        /*String outString = new Date() + " [Fine] [API.historicalData ] "
         * + "[" + this.reqQuoHistHash.get(reqId).getSymbol() + "] "
         * //+ "[" + reqQuoHistHash.get(reqId). + "]"
         * + EWrapperMsgGenerator.historicalData(reqId, date, open,
         * high, low, close,
         * volume,
         * count, WAP, hasGaps);
         * System.out.println(outString);*/
        // add Historical Data to Queue
        ReqIdPriceBar rq = new ReqIdPriceBar(reqId, date, open, close, high, low, WAP, count, volume);
        hdcq.enqueueItem(rq);



    }

    @Override
    public void error(Exception e)
    {
        String outString = " [API.msg3] " + e.getMessage();
        System.out.println(outString);
        WriteToFile.writeF(outString, "");
        e.printStackTrace();
    }

    @Override
    public void error(String str)
    {
        String outString = " [API.msg1] " + str;
        System.out.println(outString);
        WriteToFile.writeF(outString, "");
    }

    @Override
    public void error(int one, int two, String str)
    {
        String outString = " [API.msg2] " + str + " {" + one + ", " + two + "}";
        System.out.println(outString);
        WriteToFile.writeF(outString, "");
    }

    private void checkChannelStrategy(RequestHistoricalMktDta histreq, int requestIdHist,
            String symbol)
    {
        String outString;
        if (histreq.getParentUserReqNr() != null && !histreq.getParentUserReqNr().equals("")) {

            TradeOrder sd = this.tradesHash.get(histreq.getParentUserReqNr());
            /*outString = new Date() + " " + "[Info] [doHistoricalDataCheck] [" + symbol + "] "
             * + " to.getCheckPrice() :" + to.getCheckPrice()
             * ;
             * System.out.println(outString);*/

            if (sd.getCheckPrice() == 0) {
                ChannelStrategy cs = new ChannelStrategy();
                cs.evalStrategy(20, 14, sd, reqQuoHistHash.get(requestIdHist));

                outString = new Date() + " " + "[Info] [doHourlyCheck] [" + symbol + "] "
                        + " quantity :" + sd.getQuantity()
                        + " checkprice :" + sd.getCheckPrice()
                        + " orderprice " + sd.getOrderPrice()
                        + " stopprice :" + sd.getStopPrice();
                WriteToFile.logAll(outString, symbol);

                outString = new Date() + " " + "[Info] [doHourlyCheck] [" + symbol + "] "
                        + " adx :" + cs.getAdx()
                        + " high :" + cs.getHigh()
                        + " low :" + cs.getLow();
                WriteToFile.logAll(outString, symbol);
            }
        }
    }

    private void checkCongestionStrategy1(RequestHistoricalMktDta histreq, String symbol,
            RequestQuotesHistorical rqh)
    {
        if (histreq.getParentUserReqNr() != null && !histreq.getParentUserReqNr().equals("")
                && !rqh.isCongestionstrategy()) {
            String methodname = "checkCongestionStrategy1";
            TradeOrder sd = this.tradesHash.get(histreq.getParentUserReqNr());

            checkCandles(methodname, symbol, rqh);

            if (sd != null && sd.getCheckPrice() == 0) {
                //System.out.println(rqh.toStringx(10)); // Print the last 10 quotes to check

                CongestionStrategy1 cs = new CongestionStrategy1();
                cs.evalStrategy(20, 5, 1.3, sd, rqh, requestHash.get(histreq.getRequestId()));

                if (sd.getCheckPrice() != 0) {
                    sd.symboldataToLog(methodname, symbol);
                }



                cs.CongestionToPrint(methodname, symbol);

            }

            rqh.setCongestionstrategy(true);
        }
    }

    private void checkCongestionStrategy2(RequestHistoricalMktDta histreq,
            RequestQuotesHistorical rqh)
    {
        if (histreq.getParentUserReqNr() != null && !histreq.getParentUserReqNr().equals("")
                && !rqh.isCongestionstrategy()) {
            String methodname = "checkCongestionStrategy2";
            TradeOrder to = this.tradesHash.get(histreq.getParentUserReqNr());
            RequestData rq = requestHash.get(histreq.getRequestId());

            checkCandles(methodname, histreq.getSymbol(), rqh);

            if (to != null && to.getCheckPrice() == 0) {
                //System.out.println(rqh.toStringx(2)); // Print the last 10 quotes to check

                CongestionStrategy2 cs = new CongestionStrategy2(rqh);

                cs.evalStrategy(20, 5, 1.3, to, rqh, rq);

                //cs.congLastToPrint(methodname, symbol);
                //cs.congLastSupportBasicToPrint(methodname, symbol);
            }

            rqh.setCongestionstrategy(true);
        }
    }

    private void checkChannelConvergenceStrategy2(RequestHistoricalMktDta histreq,
            RequestQuotesHistorical rqh, TradeOrder to)
    {
        if (histreq.getParentUserReqNr() != null && !histreq.getParentUserReqNr().equals("")
                && !rqh.isConvergencestrategy2()) {

            QuoteDB.addQuotes(histreq, to, rqh);

            QuoteDB.setQuotes(histreq, to, rqh);

            String methodname = "checkChannelConvergenceStrategy2";
            RequestData rq = requestHash.get(histreq.getRequestId());

            checkCandles(methodname, histreq.getSymbol(), rqh);

            if (to != null && to.getCheckPrice() == 0) {
                //System.out.println(rqh.toStringx(2)); // Print the last 10 quotes to check

                ChannelConvergenceStrategy2 cc = new ChannelConvergenceStrategy2(rqh);

                cc.evalStrategy(26, to, rqh, rq);

            }

            rqh.setConvergencestrategy2(true);
        }
    }

    private void checkMACDLowStrategy1(RequestHistoricalMktDta histreq,
            RequestQuotesHistorical rqh, TradeOrder to, String parentUserReqNr)
    {
        if (histreq.getParentUserReqNr() != null && !histreq.getParentUserReqNr().equals("")
                && !rqh.isMACDlowstrategy1()) {
            String methodname = "checkMACDLowStrategy1";
            RequestData rq = requestHash.get(histreq.getRequestId());

            if (to != null && to.getCheckPrice() == 0) {
                //System.out.println(rqh.toStringx(2)); // Print the last 10 quotes to check

                MACDLowStrategy2 macdst = new MACDLowStrategy2(rqh);

                macdst.setBarsize(histreq.getBarsize());

                int totalquotes = rqh.getQh().size(); // get total amount of Quotes

                int lookbackperiod = 140;
                executeMACDStrategy(lookbackperiod, totalquotes, macdst, to, rqh, rq,
                        parentUserReqNr);

                lookbackperiod = 280;
                executeMACDStrategy(lookbackperiod, totalquotes, macdst, to, rqh, rq,
                        parentUserReqNr);

                lookbackperiod = 420;
                executeMACDStrategy(lookbackperiod, totalquotes, macdst, to, rqh, rq,
                        parentUserReqNr);

            }

            rqh.setMACDlowstrategy1(true);
        }
    }

    private void checkROCStrategy1(RequestHistoricalMktDta histreq,
            RequestQuotesHistorical rqh, TradeOrder to, String parentUserReqNr)
    {
        if (histreq.getParentUserReqNr() != null && !histreq.getParentUserReqNr().equals("")
                && !rqh.isROCStrategy1()) {
            String methodname = "checkROCStrategy1";
            RequestData rq = requestHash.get(histreq.getRequestId());

            if (to != null && to.getCheckPrice() == 0) {
                //System.out.println(rqh.toStringx(2)); // Print the last 10 quotes to check

                ROCStrategy1 roc = new ROCStrategy1(rqh);

                int totalquotes = rqh.getQh().size() - 5;

                int lookbackperiod = 140;
                executeROCStrategy(lookbackperiod, totalquotes, roc, histreq, to, rqh);
                lookbackperiod = 280;
                executeROCStrategy(lookbackperiod, totalquotes, roc, histreq, to, rqh);
                lookbackperiod = 420;
                executeROCStrategy(lookbackperiod, totalquotes, roc, histreq, to, rqh);


            }

            rqh.setROCStrategy1(true);
        }
    }

    private void checkDynamicTradeStrategy1(RequestHistoricalMktDta histreq,
            RequestQuotesHistorical rqh, TradeOrder to)
    {
        if (histreq.getParentUserReqNr() != null && !histreq.getParentUserReqNr().equals("")
                && !rqh.isDynamictradestrategy1()) {
            String methodname = "checkDynamicTradeStrategy1";


            if (to != null && to.getQuantity() == 0) {
                DynamicTradeStrategy1.evalStrategy(to, rqh);
            }

            rqh.setDynamictradestrategy1(true);
        }
    }

    private void checkAutoEnvelopeStrategy1(RequestHistoricalMktDta histreq,
            RequestQuotesHistorical rqh, TradeOrder to)
    {
        if (histreq.getParentUserReqNr() != null && !histreq.getParentUserReqNr().equals("")
                && !rqh.isAutoEnvelopestrategy1()) {
            String methodname = "checkAutoEnvelopeStrategy1";

            int totalquotes = rqh.getQh().size() - 5;

            int lookbackperiod = 140;
            executeAutoEnvelopeStrategy(to, lookbackperiod, totalquotes, histreq, rqh);
            lookbackperiod = 280;
            executeAutoEnvelopeStrategy(to, lookbackperiod, totalquotes, histreq, rqh);
            lookbackperiod = 420;
            executeAutoEnvelopeStrategy(to, lookbackperiod, totalquotes, histreq, rqh);
            lookbackperiod = 35;
            executeAutoEnvelopeStrategy(to, lookbackperiod, totalquotes, histreq, rqh);

            rqh.setAutoEnvelopestrategy1(true);
        }
    }

    private void checkStandardErrorBandsStrategy1(RequestHistoricalMktDta histreq,
            RequestQuotesHistorical rqh, TradeOrder to)
    {
        if (histreq.getParentUserReqNr() != null && !histreq.getParentUserReqNr().equals("")
                && !rqh.isStandardErrorBandsstrategy1()) {
            String methodname = "checkStandardErrorBandsStrategy1";

            int totalquotes = rqh.getQh().size() - 5;

            int lookbackperiod = 140;
            executeStandardErrorBandsStrategy(to, lookbackperiod, totalquotes, histreq, rqh);
            lookbackperiod = 280;
            executeStandardErrorBandsStrategy(to, lookbackperiod, totalquotes, histreq, rqh);
            lookbackperiod = 420;
            executeStandardErrorBandsStrategy(to, lookbackperiod, totalquotes, histreq, rqh);

            rqh.setStandardErrorBandsstrategy1(true);
        }
    }

    private void checkEODStrategy1(RequestHistoricalMktDta histreq,
            RequestQuotesHistorical rqh, TradeOrder to)
    {
        if (histreq.getParentUserReqNr() != null && !histreq.getParentUserReqNr().equals("")
                && !rqh.isEODstrategy1()) {
            String methodname = "checkEODStrategy1";
            RequestData rq = requestHash.get(histreq.getRequestId());

            if (to != null && to.getCheckPrice() == 0) {

                EODBigVolumeStrategy1 eodbv = new EODBigVolumeStrategy1(rqh);

                //<editor-fold defaultstate="collapsed" desc="Set Dates from and to">
        /*GregorianCalendar fromdate = new GregorianCalendar();
                 * fromdate.set(fromdate.get(Calendar.YEAR), fromdate.get(Calendar.MONTH),
                 * fromdate.get(Calendar.DAY_OF_MONTH),
                 * 20, 29, 59);*/

                GregorianCalendar fromdate = ReadConfiguration.getEODStarttime();

                /*GregorianCalendar todate = new GregorianCalendar();
                 * todate.set(todate.get(Calendar.YEAR), todate.get(Calendar.MONTH),
                 * todate.get(Calendar.DAY_OF_MONTH),
                 * 21, 59, 59);*/

                GregorianCalendar todate = ReadConfiguration.getEODEndtime();

                //</editor-fold>

                eodbv.evalStrategy(fromdate, todate, to, rqh, rq);

            }

            rqh.setEODstrategy1(true);
        }
    }

    private void checkChannelConvergenceStrategy1(RequestHistoricalMktDta histreq, String symbol,
            RequestQuotesHistorical rqh)
    {
        if (histreq.getParentUserReqNr() == null || histreq.getParentUserReqNr().equals("")) {
            return;
        }

        TradeOrder sd = this.tradesHash.get(histreq.getParentUserReqNr());
        String methodname = "checkChannelConvergenceStrategy1";
        final RequestData rd = requestHash.get(histreq.getRequestId());

        // not yet convergence

        if (!rqh.isConvergencestrategy() && sd != null && sd.getCheckPrice() == 0) {
            //<editor-fold defaultstate="collapsed" desc="Check convergence">

            //System.out.println(rqh.toStringx(10)); // Print the last 10 quotes to check
            ChannelConvergenceStrategy1 cc = new ChannelConvergenceStrategy1();
            boolean evalStrategy = cc.evalStrategy(10, sd, rqh, rd);

            if (LOG_CONVERGENCE) {
                if (evalStrategy) {
                    cc.ConvergenceToLog(methodname, symbol);
                }
            }

            rqh.setConvergencestrategy(evalStrategy);
            //</editor-fold>
        }

        // is convergence
        if (rqh.isConvergencestrategy() && sd != null && sd.getCheckPrice() == 0) {

            if (checkAvgVol(symbol, rqh)) {
                //<editor-fold defaultstate="collapsed" desc="calculate Trade">
                sd.setCheckPrice(999.99);
                sd.setOrderPrice(rd.getPb().getHigh());
                sd.setQh(rqh.getQh());
                sd.Calculate();
                sd.symboldataToLog(methodname, symbol);
                //</editor-fold>
            }
        }
    }

    private boolean checkAvgVol(String symbol, RequestQuotesHistorical rqh)
    {
        boolean okvol = false;
        if (!rqh.isAvgvolcalculated()) {
            AvgHourlyVol ahv = new AvgHourlyVol(rqh.getQh(), 1.2);
            ahv.calculate();
            if (ahv.isVolaboveavg()) {
                checkAvgVolToLog(symbol);
                okvol = true;
            }
            rqh.setAvgvolcalculated(true);
        }

        return okvol;
    }

    private void checkDate(RequestHistoricalMktDta histreq, String userReqNr,
            int requestIdHist)
    {

        // generate current date
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        GregorianCalendar gc = new GregorianCalendar();
        if (histreq.getNexthours() != null) {
            if (histreq.getNexthours().compareTo(gc) < 0) {

                //logNexthours(symbol, df, histreq);

                updMktDtaHourly(histreq.getUserReqType(), userReqNr, histreq.getSymbol(),
                        histreq.getParentUserReqNr(), histreq.getBarsize(), histreq.getNexthours());
            }
        } else {
            logCheckDateError(histreq, requestIdHist, histreq.getSymbol());
        }
    }

    private void logNexthours(String symbol, SimpleDateFormat df, RequestHistoricalMktDta histreq)
    {
        String outString;
        /*System.out.println(" date now " + df.format(gc.getTime()));
         * System.out.println(" date Nexthours " + df.format(histreq.getNexthours().getTime()));
         * System.out.println(" compare : " + histreq.getNexthours().compareTo(gc));*/
        outString = new Date() + " " + "[Fine] [doHourlyCheck] [" + symbol + "] "
                + "next hour :" + df.format(histreq.getNexthours().getTime());
        WriteToFile.logAll(outString, symbol);
    }

    private void logCheckDateError(RequestHistoricalMktDta histreq, int requestIdHist, String symbol)
    {
        String outString;
        outString = new Date() + " " + "[Error] [doHourlyCheck] [" + histreq.getSymbol() + "] "
                + "UserRequestNr : [" + histreq.getUserReqNr() + "] "
                + "reqId : [" + histreq.getRequestIdHist() + "][" + requestIdHist + "]"
                + "next hour :" + histreq.getNexthours();
        WriteToFile.logAll(outString, symbol);
    }

    private void checkAvgVolToLog(String symbol)
    {
        String outString;
        outString = new Date() + " " + "[Info] [checkAvgVol] [" + symbol + "] "
                + "Volume above average";
        WriteToFile.logAll(outString, symbol);
    }

    private void checkCandles(String methodname, String symbol, RequestQuotesHistorical rqh)
    {
        CandlePattern cp = new CandlePattern(rqh.getQh());
        cp.calculate(5);
        int NumberOfCandles = rqh.getQh().getSize() - 1;
        if (LOG_CANDLE) {
            cp.toPrintBullishReversal(methodname, symbol, NumberOfCandles);

            cp.toPrintBearishCandles(methodname, symbol, NumberOfCandles);
        }


    }

    private void reqHistData(int requestIdHst, RequestHistoricalMktDta hr)
    {

        logreqHistData(requestIdHst, hr);
        // Request Historical Data
        eClientSocket.reqHistoricalData(requestIdHst, hr.getContract(),
                hr.getBackfillEndTime(),
                hr.getBackfillDuration(),
                hr.getBarSizeSetting(),
                hr.getWhatToShow(),
                hr.getUseRTH(),
                hr.getFormatDate());
    }

    private void logreqHistData(int requestIdHst, RequestHistoricalMktDta hr)
    {
        if (LOG_REQHISTDATA) {
            StringBuffer sb = new StringBuffer();

            sb.append(new Date());
            sb.append(" [Fine] [reqHistDataToPrint] [");
            sb.append(hr.getSymbol());
            sb.append("] ");
            sb.append("requestnr ");
            sb.append(requestIdHst);
            sb.append(" BackfillEndTime ");
            sb.append(hr.getBackfillEndTime());
            sb.append(" BackfillDuration ");
            sb.append(hr.getBackfillDuration());
            sb.append(" BarSizeSetting ");
            sb.append(hr.getBarSizeSetting());
            sb.append(" WhatToShow ");
            sb.append(hr.getWhatToShow());
            sb.append(" UseRTH ");
            sb.append(hr.getUseRTH());
            sb.append(" FormatDate ");
            sb.append(hr.getFormatDate());

            WriteToFile.logAll(sb.toString(), hr.getSymbol());

        }
    }

    private void chgTradesCSV(String userReqNr, TradeOrder to, String status)
    {
        // change Trade to Submitted in CSVFile and Update Record
        tradesCSV.changeStatus(userReqNr, status, to);
        try {
            tradesCSV.writeTrades();

        } catch (Exception ex) {
            System.out.print("[Error] [submitTrade] CSV File error :");
            ex.printStackTrace();
        }
    }

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining,
            double avgFillPrice, int permId, int parentId,
            double lastFillPrice, int clientId, String whyHeld)
    {
        // received order status
        String msg = EWrapperMsgGenerator.orderStatus(orderId, status, filled, remaining,
                avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);

        WriteToFile.writeF(msg, "orderStatusMsg");

    }

    private void executeMACDStrategy(int lookbackperiod, int totalquotes, MACDLowStrategy2 macdst,
            TradeOrder to, RequestQuotesHistorical rqh, RequestData rq, String parentUserReqNr)
    {
        lookbackperiod = (lookbackperiod > totalquotes ? totalquotes : lookbackperiod);
        macdst.evalStrategy(26, 12, 9, lookbackperiod, to, rqh, rq, EOD_TRADE);

        if (to.getuserReqTyp().equals(EOD_TRADE)) {
            this.chgTradesCSV(parentUserReqNr, to, EOD_TRADE);
        }
    }

    private void executeROCStrategy(int lookbackperiod, int totalquotes, ROCStrategy1 roc,
            RequestHistoricalMktDta histreq, TradeOrder to, RequestQuotesHistorical rqh)
    {
        lookbackperiod = (lookbackperiod > totalquotes ? totalquotes : lookbackperiod);
        roc.setLookbackPeriod(lookbackperiod); // Relative index number
        // RequestHistoricalMktDta histreq, TradeOrder to,RequestQuotesHistorical m_qh
        roc.evalStrategy(histreq, to, rqh);
    }

    private void executeAutoEnvelopeStrategy(TradeOrder to, int lookbackperiod, int totalquotes,
            RequestHistoricalMktDta histreq, RequestQuotesHistorical rqh)
    {
        if (to != null && to.getQuantity() == 0) {
            lookbackperiod = (lookbackperiod > totalquotes ? totalquotes : lookbackperiod);
            AutoEnvelopeStrategy1.evalStrategy(histreq, to, rqh, lookbackperiod);
        }
    }

    private void executeStandardErrorBandsStrategy(TradeOrder to, int lookbackperiod,
            int totalquotes, RequestHistoricalMktDta histreq, RequestQuotesHistorical rqh)
    {
        if (to != null && to.getQuantity() == 0) {
            lookbackperiod = (lookbackperiod > totalquotes ? totalquotes : lookbackperiod);
            StandardErrorBandsStrategy1.evalStrategy(histreq, to, rqh, lookbackperiod);
        }
    }
}
