/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 11.03.2013
 * change date : 
 * 6. disconnect to TWS whereby this part is not reached at the moment, the application loop between 3 an 5, to stop is only manual
 *
 * Change Log:
 * 06.06.2013 Setter for path
 * <p/>
 */
package com.amaggioni.myutilities;

import java.io.IOException;
import java.util.Date;

public class WriteToFile
{

    private static String path = "C:/Users/magang/Documents/Logs/";
    private static String file = "Log_04_03_1_";

    public static synchronized void writeF(String outString, String symbol)
    {
        // Write Message to File 
        try {
            String filenew = getPath() + getFile();
            if (!symbol.equals("")) {
                // "C:/Users/magang/Documents/Submitted_Orders.txt";
                filenew = filenew + symbol;
            }
            MyLoggerToFile log = new MyLoggerToFile(filenew + ".txt");
            log.doLog(outString);
            log.Close();

        } catch (IOException ex) {
            System.out.print("[Error] [writeToFile] File Output error :");
            ex.printStackTrace();

        }
    }

    /**
     * @return the file
     */
    public static String getFile()
    {
        return file;
    }

    /**
     * @param aFile the file to set
     */
    public static void setFile(String aFile)
    {
        file = aFile;
    }

  /**
   * @return the path
   */
  public static String getPath()
  {
    return path;
  }

  /**
   * @param aPath the path to set
   */
  public static void setPath(String aPath)
  {
    path = aPath;
  }

    public static void logAll(String outString, String symbol)
    {
        System.out.println(outString);
        writeF(outString, "");
        if (!symbol.equals("")) {
            writeF(outString, symbol);
        }
    }

    public static String msgHeaderBld(String errortype, 
            String methodname, 
            String symbol,
            String barsize,
            int lookback)
    {
        return new Date() + " [" + errortype + "] " 
                + "[" + methodname 
                + "] [" + symbol 
                + "] [" + barsize 
                + "] [" + lookback
                + "] ";
           }
}
