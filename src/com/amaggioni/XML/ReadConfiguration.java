package com.amaggioni.XML;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 26.05.2013
 * change date : 26.05.2013
 *
 * description: ..
 *
 * flow:
 * <p/>
 *
 * Change Log:
 * 26.05.2013 first version
 * 31.05.2013 add logic for all fields
 * <p/>
 */
public class ReadConfiguration extends DefaultHandler
{

    private static int TWSPort;
    private static String logfile;
    private static String logfilepath;
    private static String inputfile;
    private static String inputfilepath;
    private static boolean fieldlogfile;
    private static boolean fieldTWSport;
    private static boolean fieldlogfilepath;
    private static boolean fieldinputfile;
    private static boolean fieldinputfilepath;
    private static boolean fieldStartTime;
    private static boolean fieldEndTime;
    private static boolean fieldeodStartTime;
    private static boolean fieldeodEndTime;
    private static GregorianCalendar starttime;
    private static GregorianCalendar endtime;
    private static GregorianCalendar EODstarttime;
    private static GregorianCalendar EODendtime;

    public ReadConfiguration()
    {
        super();
    }

    public static void main(String args[])
            throws Exception
    {
        XMLReader xr = XMLReaderFactory.createXMLReader();
        ReadConfiguration handler = new ReadConfiguration();
        xr.setContentHandler(handler);
        xr.setErrorHandler(handler);

        // Parse each file provided on the
        // command line.

        String file = "C:/Users/magang/Documents/Java_Uebungen/IB_API_Pro/myAPIRunner13_08_09/src/com/amaggioni/XML/Configuration.xml";
        FileReader r = new FileReader(file);
        xr.parse(new InputSource(r));

    }

    @Override
    public void startDocument()
    {
        System.out.println("Start document");
    }

    @Override
    public void endDocument()
    {
        System.out.println("End document");
    }

    @Override
    public void startElement(String uri, String name,
            String qName, Attributes atts)
    {
        //System.out.println("Start Element :" + qName);

        if (qName.equalsIgnoreCase("TWSPort")) {
            fieldTWSport = true;
        }

        if (qName.equalsIgnoreCase("Logfilepath")) {
            fieldlogfilepath = true;
        }

        if (qName.equalsIgnoreCase("Inputfilepath")) {
            fieldinputfilepath = true;
        }

        if (qName.equalsIgnoreCase("Logfile")) {
            fieldlogfile = true;
        }

        if (qName.equalsIgnoreCase("Inputfile")) {
            fieldinputfile = true;
        }

        if (qName.equalsIgnoreCase("StartTime")) {
            fieldStartTime = true;
        }
        
        if (qName.equalsIgnoreCase("EndTime")) {
            fieldEndTime = true;
        }
        
        if (qName.equalsIgnoreCase("EODStartTime")) {
            fieldeodStartTime = true;
        }
               
        if (qName.equalsIgnoreCase("EODEndTime")) {
            fieldeodEndTime = true;
        }

    }

    @Override
    public void endElement(String uri, String name, String qName)
    {
        /*if ("".equals (uri))
         * System.out.println("End element: " + qName);
         * else
         * System.out.println("End element:   {" + uri + "}" + name);*/
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException
    {
 SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
 
        if (this.fieldTWSport) {
            this.fieldTWSport = false;
            this.TWSPort = new Integer(new String(ch, start, length));
            System.out.println("TWS Port : " + this.getTWSPort());
        }

        if (this.fieldlogfile) {
            this.fieldlogfile = false;
            this.logfile = new String(ch, start, length);
            System.out.println("logfile : " + this.getLogfile());
        }


        if (this.fieldlogfilepath) {
            this.fieldlogfilepath = false;
            this.logfilepath = new String(ch, start, length);
            System.out.println("logfilepath : " + this.getLogfilepath());
        }

        
        if (this.fieldinputfile) {
            this.fieldinputfile = false;
            this.inputfile = new String(ch, start, length);
            System.out.println("inputfile : " + this.getInputfile());
        }
        
       
        if (this.fieldinputfilepath) {
            this.fieldinputfilepath = false;
            this.inputfilepath = new String(ch, start, length);
            System.out.println("inputfile : " + this.getInputfilepath());
        }
        
        
        if (this.fieldStartTime) {
            this.fieldStartTime = false;
            String starttimestring = new String(ch, start, length);
            starttime = new GregorianCalendar();
            getStarttime().set(getStarttime().get(Calendar.YEAR), 
                    getStarttime().get(Calendar.MONTH), getStarttime().get(Calendar.DAY_OF_MONTH),
                    new Integer(starttimestring.substring(0, 2)),
                    new Integer(starttimestring.substring(3, 5)), 
                    0);
           
            System.out.println("startdate : " + df.format(getStarttime().getTime()));
        }
        
        if (this.fieldEndTime) {
            this.fieldEndTime = false;
            String endtimestring = new String(ch, start, length);
            endtime = new GregorianCalendar();
            getEndtime().set(getEndtime().get(Calendar.YEAR), 
                    getEndtime().get(Calendar.MONTH), getEndtime().get(Calendar.DAY_OF_MONTH),
                    new Integer(endtimestring.substring(0, 2)),
                    new Integer(endtimestring.substring(3, 5)), 
                    0);
           
            System.out.println("enddate : " + df.format(getEndtime().getTime()));
        }
        
        
        if (this.fieldeodEndTime) {
            this.fieldeodEndTime = false;
            String endtimestring = new String(ch, start, length);
            EODendtime = new GregorianCalendar();
            getEODEndtime().set(getEODEndtime().get(Calendar.YEAR), 
                    getEODEndtime().get(Calendar.MONTH), getEODEndtime().get(Calendar.DAY_OF_MONTH),
                    new Integer(endtimestring.substring(0, 2)),
                    new Integer(endtimestring.substring(3, 5)), 
                    0);
           
            System.out.println("EOD enddate : " + df.format(getEODEndtime().getTime()));
        }
        
        
        if (this.fieldeodStartTime) {
            this.fieldeodStartTime = false;
            String starttimestring = new String(ch, start, length);
            EODstarttime = new GregorianCalendar();
            getEODStarttime().set(getEODStarttime().get(Calendar.YEAR), 
                    getEODStarttime().get(Calendar.MONTH), getEODStarttime().get(Calendar.DAY_OF_MONTH),
                    new Integer(starttimestring.substring(0, 2)),
                    new Integer(starttimestring.substring(3, 5)), 
                    0);
           
            System.out.println("EOD Start date : " + df.format(getEODStarttime().getTime()));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
    /**
     * @return the TWSPort
     */
    public static int getTWSPort()
    {
        return TWSPort;
    }
    
    /**
     * @return the logfile
     */
    public static String getLogfile()
    {
        return logfile;
    }
    
    /**
     * @return the logfilepath
     */
    public static String getLogfilepath()
    {
        return logfilepath;
    }
    
    /**
     * @return the inputfile
     */
    public static String getInputfile()
    {
        return inputfile;
    }
    
    /**
     * @return the inputfilepath
     */
    public static String getInputfilepath()
    {
        return inputfilepath;
    }
    
    /**
     * @return the starttime
     */
    public static GregorianCalendar getStarttime()
    {
        return starttime;
    }
    
    public static GregorianCalendar getEODStarttime()
    {
        return EODstarttime;
    }
    
    /**
     * @return the endtime
     */
    public static GregorianCalendar getEndtime()
    {
        return endtime;
    }
    
    public static GregorianCalendar getEODEndtime()
    {
        return EODendtime;
    }
    //</editor-fold>
}
