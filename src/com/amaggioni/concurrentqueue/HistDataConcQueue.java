/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amaggioni.concurrentqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author magang
 */
public class HistDataConcQueue
{

    private ConcurrentLinkedQueue<ReqIdPriceBar> concurrentLinkedQueue = new ConcurrentLinkedQueue<ReqIdPriceBar>();

    public ReqIdPriceBar dequeueItem()
    {
        if (!concurrentLinkedQueue.isEmpty())
        {
            //System.out.println(" Queue Size : " + concurrentLinkedQueue.size());
            return concurrentLinkedQueue.remove();
        } else
        {
            return null;
        }
    }

    public int getQueueSize()
    {
        if (!concurrentLinkedQueue.isEmpty())
        {
            return concurrentLinkedQueue.size();
        } else
        {
            return 0;
        }
    }

    public void enqueueItem(ReqIdPriceBar ripb)
    {
        /*System.out.println(" Enqueue item ");*/
        concurrentLinkedQueue.add(ripb);
    }

    
}
