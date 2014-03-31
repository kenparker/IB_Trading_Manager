/**
 * ***********************************************************************************************
 *
 * @author magang
 * create date : 31.01.2013
 * change date : 01.02.2013
 *
 * description: Wait until Time
 *
 * flow:
 * <p/>
 *
 * Change Log:01.02.2013 Info Messages added, code cleaned up
 * <p/>
 * <
 * p/>
 */
package com.amaggioni.myutilities;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author maggioni
 */
public class WaitTillTime
{

    public void doWait(int hours, int minutes, int seconds) throws InterruptedException
    {

        Calendar calendar = new GregorianCalendar();
        long curTime = calendar.getTimeInMillis();

        Calendar calendarTime = new GregorianCalendar();
        calendarTime.set(calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE), hours, minutes, seconds);



        if (calendar.get(calendar.HOUR_OF_DAY) > hours)
        {
            
            System.out.println("[info] [doWait] do not wait as Hour is less the actual Hour");
            
            return;
        }

        //calendarTime.getTimeInMillis();
        long nextRunTime = calendarTime.getTimeInMillis();
        long waitTime = nextRunTime - curTime;

        System.out.println("[info] [doWait] wait until " + hours + " " + minutes + " " + seconds);

        Thread.sleep(waitTime);

        System.out.println("[info] [doWait] end of wait .  .");


    }
}
