package com.amaggioni.datastructure;

import com.amaggioni.myutilities.WriteToFile;
import com.ib.client.Contract;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 03.01.2013
 * change date : 26.04.2013
 *
 * description:
 *
 * flow:
 * <p/>
 * Change Log:
 * 17.04.2013 new logic for calculation of backfillendtime
 * 26.04.2013 change logic for backfillendtime and nexthours
 * * */
public class RequestHistoricalMktDta extends BasicUserRequest
{

    private String backfillEndTime;
    private String backfillDuration;
    private String barSizeSetting;
    private int useRTH;
    private int formatDate;
    private String whatToShow;
    private Contract contract;
    private int requestIdHist;
    private GregorianCalendar nexthours;
    private String parentUserReqNr;
    private String barsize;
    public static final String x1_HOUR = "1 hour";
    public static final String x15_MINS = "15 mins";
    public static final String x1_DAY = "1 day";
    private static final int MINUTES = 0;

    /**
     * Standard constructor
     */
    public RequestHistoricalMktDta()
    {

        this("", "", "", 0, false, "", "1 M", "1 day", 1, 1, "TRADES", null, 0, null, null, null);

        //System.out.println("RequestHistoricalMktDta default start");



    }

    /**
     *
     * @param userReqTyp
     * @param symbol
     * @param requestId
     * @param requeststatus
     * @param contract
     * @param requestIdHist
     */
    private RequestHistoricalMktDta(String userReqTyp, String userReqNr, String symbol,
            int requestId, boolean requeststatus,
            Contract contract, int requestIdHist, String parentUserReqNr, String m_barsize)
    {

        this(userReqTyp, userReqNr, symbol, requestId, requeststatus, "", "1 M", "1 day", 1, 1,
                "TRADES",
                contract, requestIdHist, parentUserReqNr, m_barsize, null);


    }

    /**
     *
     * @param backfillEndTime
     * @param backfillDuration
     * @param barSizeSetting
     * @param useRTH
     * @param formatDate
     * @param whatToShow
     */
    private RequestHistoricalMktDta(String backfillEndTime, String backfillDuration,
            String barSizeSetting, int useRTH, int formatDate,
            String whatToShow)
    {

        this("", "", "", 0, false, backfillEndTime, backfillDuration, barSizeSetting, useRTH,
                formatDate, whatToShow,
                null, 0, null, null, null);

        //System.out.println("RequestHistoricalMktDta 4 start");
    }

    /**
     *
     * @param userReqTyp
     * @param symbol
     * @param requestId
     * @param requeststatus
     * @param backfillEndTime
     * @param backfillDuration
     * @param barSizeSetting
     * @param useRTH
     * @param formatDate
     * @param whatToShow
     * @param contract
     * @param requestIdHist
     */
    public RequestHistoricalMktDta(String userReqTyp, String userReqNr, String symbol, int requestId,
            boolean requeststatus, String backfillEndTime,
            String backfillDuration, String barSizeSetting, int useRTH, int formatDate,
            String whatToShow, Contract contract, int requestIdHist, String parentUserReqNr,
            String m_barsize, final GregorianCalendar gc)
    {
        super(userReqTyp, userReqNr, symbol, requestId, requeststatus);

        /*String outString = new Date() + " RequestHistoricalMktDta 3 start " + userReqTyp;
         * WriteToFile.writeF(outString, this.getSymbol());*/


        this.backfillEndTime = backfillEndTime;
        this.backfillDuration = backfillDuration;
        this.barSizeSetting = barSizeSetting;
        this.useRTH = useRTH;
        this.formatDate = formatDate;
        this.whatToShow = whatToShow;
        this.contract = contract;
        this.requestIdHist = requestIdHist;
        this.parentUserReqNr = parentUserReqNr;
        this.barsize = m_barsize;
        this.nexthours = new GregorianCalendar();
        //this.nexthours.setTimeZone(TimeZone.getTimeZone("EST"));

        calcBackFillEndTime(gc);
        calcNexthours(gc);

    }

    private static String pad(int val)
    {
        return val < 10 ? "0" + val : "" + val;
    }

    /**
     * @return the backfillEndTime
     */
    public String getBackfillEndTime()
    {
        return backfillEndTime;
    }

    /**
     * @param backfillEndTime the backfillEndTime to set
     */
    public void setBackfillEndTime(String backfillEndTime)
    {
        this.backfillEndTime = backfillEndTime;
    }

    /**
     * @return the backfillDuration
     */
    public String getBackfillDuration()
    {
        return backfillDuration;
    }

    /**
     * @param backfillDuration the backfillDuration to set
     */
    public void setBackfillDuration(String backfillDuration)
    {
        this.backfillDuration = backfillDuration;
    }

    /**
     * @return the barSizeSetting
     */
    public String getBarSizeSetting()
    {
        return barSizeSetting;
    }

    /**
     * @param barSizeSetting the barSizeSetting to set
     */
    public void setBarSizeSetting(String barSizeSetting)
    {
        this.barSizeSetting = barSizeSetting;
    }

    /**
     * @return the useRTH
     */
    public int getUseRTH()
    {
        return useRTH;
    }

    /**
     * @param useRTH the useRTH to set
     */
    public void setUseRTH(int useRTH)
    {
        this.useRTH = useRTH;
    }

    /**
     * @return the formatDate
     */
    public int getFormatDate()
    {
        return formatDate;
    }

    /**
     * @param formatDate the formatDate to set
     */
    public void setFormatDate(int formatDate)
    {
        this.formatDate = formatDate;
    }

    /**
     * @return the whatToShow
     */
    public String getWhatToShow()
    {
        return whatToShow;
    }

    /**
     * @param whatToShow the whatToShow to set
     */
    public void setWhatToShow(String whatToShow)
    {
        this.whatToShow = whatToShow;
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
     * @return the requestIdHist
     */
    public int getRequestIdHist()
    {
        return requestIdHist;
    }

    /**
     * @param requestIdHist the requestIdHist to set
     */
    public void setRequestIdHist(int requestIdHist)
    {
        this.requestIdHist = requestIdHist;
    }

    /**
     * @return the nexthours
     */
    public GregorianCalendar getNexthours()
    {
        return nexthours;
    }

    private void calcNexthours(GregorianCalendar gc)
    {

        if (this.barsize.equals(x1_HOUR)) {
            //<editor-fold defaultstate="collapsed" desc="comment">

            this.nexthours.set(gc.get(Calendar.YEAR),
                    gc.get(Calendar.MONTH),
                    gc.get(Calendar.DAY_OF_MONTH),
                    gc.get(Calendar.HOUR_OF_DAY),
                    0,
                    0);


            this.nexthours.add(Calendar.HOUR_OF_DAY, +1);

            //</editor-fold>

        }

        if (this.barsize.equals(x15_MINS)) {
            //<editor-fold defaultstate="collapsed" desc="comment">

            this.nexthours.set(gc.get(Calendar.YEAR),
                    gc.get(Calendar.MONTH),
                    gc.get(Calendar.DAY_OF_MONTH),
                    gc.get(Calendar.HOUR_OF_DAY),
                    gc.get(Calendar.MINUTE),
                    0);

            this.nexthours.add(Calendar.MINUTE, +15);


            //</editor-fold>
        }

        writeToFilenextHours();
    }

    /**
     * @return the parentUserReqNr
     */
    public String getParentUserReqNr()
    {
        return parentUserReqNr;
    }

    /**
     * @param parentUserReqNr the parentUserReqNr to set
     */
    public void setParentUserReqNr(String parentUserReqNr)
    {
        this.parentUserReqNr = parentUserReqNr;
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

    private void calcBackFillEndTime(GregorianCalendar gc)
    {
        //Date date = gc.getTime();
        //TimeZone tz = gc.getTimeZone();
        //long msFromEpochGmt = date.getTime();
        //int offsetFromUTC = tz.getOffset(msFromEpochGmt);

        GregorianCalendar endtime = new GregorianCalendar();

        /*endtime.set(gc.get(Calendar.YEAR),
         * gc.get(Calendar.MONTH),
         * gc.get(Calendar.DAY_OF_MONTH),
         * gc.get(Calendar.HOUR_OF_DAY),
         * gc.get(Calendar.MINUTE),
         * gc.get(Calendar.SECOND));*/

        endtime.setTimeZone(TimeZone.getTimeZone("EST"));
        endtime.setTimeInMillis(gc.getTimeInMillis());
        endtime.add(Calendar.HOUR_OF_DAY, +1);

        if (this.backfillEndTime.equals(x1_DAY)) {

            //<editor-fold defaultstate="collapsed" desc="Backfillenddate logic">
            endtime.add(Calendar.DAY_OF_MONTH, -1);
            String dateTime = ""
                    + endtime.get(Calendar.YEAR)
                    + pad(endtime.get(Calendar.MONTH) + 1)
                    + pad(endtime.get(Calendar.DAY_OF_MONTH)) + " "
                    + pad(22) + ":"
                    + pad(00) + ":"
                    + pad(00) + " "
                    + gc.getTimeZone().getDisplayName(false, TimeZone.SHORT);

            this.backfillEndTime = dateTime;
            //</editor-fold>
        }

        if (this.backfillEndTime.equals(x1_HOUR) || this.backfillEndTime.equals(x15_MINS)) {

            calcBackFillEndTimeL1(endtime);

        }


    }

    private void writeToFileBackFillEndTime()
    {
        if (false) {
            String outString = new Date()
                    + " " + "[Fine] [writeToFileBackFillEndTime] [" + this.getSymbol() + "] "
                    + "UserRequestNr :"
                    + this.getUserReqNr()
                    + " Type :"
                    + this.getUserReqType()
                    + " BarSize :"
                    + this.getBarsize()
                    + " backfillEndTime :"
                    + this.backfillEndTime;
            WriteToFile.writeF(outString, this.getSymbol());
        }
    }

    private GregorianCalendar calcMinutesPrev(GregorianCalendar gc)
    {
        int minutes = (((int) (gc.get(Calendar.MINUTE) / 15)) * 15);
        gc.set(Calendar.MINUTE, minutes + MINUTES);
        gc.set(Calendar.SECOND, 0);

        return gc;
    }

    private void calcMinutesNext(GregorianCalendar gc)
    {
        int minutes = (((int) (gc.get(Calendar.MINUTE) / 15) + 1) * 15);
        if (minutes >= 60) {
            gc.add(Calendar.HOUR, 1);
            gc.set(Calendar.MINUTE, MINUTES);

        } else {
            gc.set(Calendar.MINUTE, minutes + MINUTES);

        }

        gc.set(Calendar.SECOND, 0);

    }

    private void writeToFilenextHours()
    {

        if (false) {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String outString = new Date()
                    + " " + "[Fine] [writeToFilenextHours] [" + this.getSymbol() + "] "
                    + " SetNextHours :"
                    + "UserRequestNr :"
                    + this.getUserReqNr()
                    + " Type :"
                    + this.getUserReqType()
                    + " BarSize :"
                    + this.getBarsize()
                    + " next Hour :"
                    + df.format(this.getNexthours().getTime());
            WriteToFile.writeF(outString, this.getSymbol());
        }

    }

    private void calcBackFillEndTimeL1(GregorianCalendar gc)
    {

        String dateTime = ""
                + gc.get(Calendar.YEAR)
                + pad(gc.get(Calendar.MONTH) + 1)
                + pad(gc.get(Calendar.DAY_OF_MONTH)) + " "
                + pad(gc.get(Calendar.HOUR_OF_DAY)) + ":"
                + pad(gc.get(Calendar.MINUTE)) + ":"
                + pad(gc.get(Calendar.SECOND)) + " "
                + gc.getTimeZone().getDisplayName(false, TimeZone.SHORT);

        this.backfillEndTime = dateTime;

        writeToFileBackFillEndTime();

    }

    private void calcBackFillEndTimeL2(GregorianCalendar gc)
    {

        gc = calcMinutesPrev(gc);

        calcBackFillEndTimeL1(gc);

    }
}
