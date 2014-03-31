/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 07.02.2013
 * change date : 02.05.2013
 *
 * description: The class contains methods to
 * 1. read the trades records from a CSV File,
 * 2. change the Status field
 * 3. and write the records back
 *
 * flow:
 * <p/>
 * Change Log:
 * 02.05.2013 'OrderIds' stored in CSV File
 * <p/>
 * <
 * p/>
 */
package com.amaggioni.IBordermanagement;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.amaggioni.datastructure.TradeOrder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;


public class TradesCSV
{

  private String FILE_PATH;
  private String FILE_NAME;
  private char SEPARATOR;
  private List<String[]> allTrades;

  /**
   * Standard Constructor
   */
  public TradesCSV()
  {
    this("C:/Users/maggioni/Mediencenter/Java_Libs/opencsv-2.3-src-with-libs/opencsv-2.3/examples/",
            "TestTicket.csv", ';');
  }

  /**
   * Constructor with parameters
   * 
   * @param file_path
   * @param file_name
   * @param separator 
   */
  public TradesCSV(String file_path, String file_name, char separator)
  {
    this.FILE_PATH = file_path;
    this.FILE_NAME = file_name;
    this.SEPARATOR = separator;
  }

  /*
   * read all Trades records from CSV File and store into List Object
   * 
   * 
   */
  public void getTrades() throws FileNotFoundException, IOException
  {

    CSVReader reader = new CSVReader(new FileReader(FILE_PATH + FILE_NAME), SEPARATOR);
    allTrades = reader.readAll();

  }

  /**
   * Change Status of Trade Record into Submitted
   * 
   * @param st 
   */
  public void changeStatus(String userReqNr, String status, TradeOrder to)
  {
    for (Iterator<String[]> iterator = getAllTrades().iterator(); iterator.hasNext();)
    {
      String[] s = iterator.next();

      if (s[1].equals(userReqNr))
      {
        s[0] = status;
        s[3] = Integer.toString(to.getQuantity());
        s[4] = Double.toString(to.getCheckPrice());
        s[5] = Double.toString(to.getOrderPrice());
        s[6] = Double.toString(to.getStopPrice());
        s[13] = Integer.toString(to.getOrderIdParent());
        s[14] = Integer.toString(to.getOrderIdStopLoss());
      }
    }

  }

  /**
   * Write all Trades back to CSV File
   * 
   * @throws FileNotFoundException
   * @throws IOException 
   */
  public void writeTrades() throws FileNotFoundException, IOException
  {
    FileOutputStream fo = new FileOutputStream(FILE_PATH + FILE_NAME);
    OutputStreamWriter sw = new OutputStreamWriter(fo);
    CSVWriter writer = new CSVWriter(sw, SEPARATOR, '\u0000');

    writer.writeAll(getAllTrades());
    sw.close();
  }

  /**
   * @return the allTrades
   */
  public List<String[]> getAllTrades()
  {
    return allTrades;
  }
}
