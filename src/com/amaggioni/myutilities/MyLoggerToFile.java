/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 31.01.2013
 * change date : 01.02.2013
 *
 * description: Generall class to manage Log Outputs to File
 *
 * flow:
 * <p/>
 *
 * Change Log:
 * 01.02.2012 Comments added
 * <p/>
 * <
 * p/>
 */
package com.amaggioni.myutilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



/**
 *
 * @author maggioni
 */
public class MyLoggerToFile
{

    // BufferedWriter to print to file
  private File fileName;
  private BufferedWriter outToFile;

  /**
   * Standard Constructor
   */
  public MyLoggerToFile() throws IOException
  {
    this("C:/Users/magang/Documents/Submitted_Orders.txt");
  }

  /**
   * Constructor with File Name
   * <p/>
   * Append is set to true
   * <p/>
   * @param xFile
   * @throws IOException
   */
  public MyLoggerToFile(String xFile) throws IOException
  {
    this(xFile, true);
  }

  /**
   * Constructor with File Name and Append Flag
   * <p/>
   * @param xFile
   * @param append
   * @throws IOException
   */
  public MyLoggerToFile(String xFile, boolean append) throws IOException
  {
    fileName = new File(xFile);
    // Create File OutputStream 
    outToFile = new BufferedWriter(new FileWriter(fileName, append));
  }

  /**
   * Write Messagelog to File
   * <p/>
   * @param outString
   */
  public void doLog(String outString)
  {
    // Write Message to File 
    try
    {
      outToFile.write(outString);
      outToFile.newLine();

    } catch (IOException ex)
    {
      System.out.print("[Error] [doLog] File Output error :" + ex);
    }
  }

  /**
   * 
   * @throws IOException 
   */
  public void Close() throws IOException
  {

    outToFile.close();

  }

    
}
