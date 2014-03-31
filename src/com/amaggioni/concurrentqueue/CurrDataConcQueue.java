
package com.amaggioni.concurrentqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author magang
 */
public class CurrDataConcQueue
{
    private ConcurrentLinkedQueue<CurrDataPriceBar> concurrentLinkedQueue = new ConcurrentLinkedQueue<CurrDataPriceBar>();

    public CurrDataPriceBar dequeueItem()
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

    public void enqueueItem(CurrDataPriceBar currData)
    {
        /*System.out.println(" Enqueue item ");*/
        concurrentLinkedQueue.add(currData);
    }
}
