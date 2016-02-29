package org.jzy3d.maths;

import org.apache.log4j.Logger;

/**
 * 
 * @see {@link TicToc}
 * 
 * @author Martin Pernollet
 */
public class Timer {
    static Logger logger = Logger.getLogger(Timer.class);
    
    public static double TEN_POW_9 = 1000000000.0;
    public static double TEN_POW_6 = 1000000.0;

    protected long start;
    
    public void start() {
        logger.info("timer start");
        start = System.nanoTime();
    }

    public double elapsed() {
        return (System.nanoTime() - start) / TEN_POW_9;
    }
}
