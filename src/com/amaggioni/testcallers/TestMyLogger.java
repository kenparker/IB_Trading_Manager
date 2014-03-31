/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.testcallers;

import com.amaggioni.myutilities.MyLoggerToFile;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maggioni
 */
public class TestMyLogger
{

  public static void main(String[] args)
  {
    try
    {
      new TestMyLogger().doSomething();
    } catch (IOException ex)
    {
      Logger.getLogger(TestMyLogger.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void doSomething() throws IOException
  {
  
   MyLoggerToFile log = new MyLoggerToFile("C:/Users/maggioni/Java_Aufgaben/Test_WriterToFile.txt",false);
   log.doLog(new Date() + " Test Message 1");
   log.doLog("Test Message 2");
   log.Close();
  
  }
}
