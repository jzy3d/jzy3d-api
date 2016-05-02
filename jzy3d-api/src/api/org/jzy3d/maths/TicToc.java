package org.jzy3d.maths;

/**
 * {@link TicToc} allows measuring elapsed time between a call to {@link tic()}
 * and a call to {@link toc()}. Retrieving elapsed time is done by calling
 * either: {@link elapsedNanosecond()}, {@link elapsedMilisecond()} or {@link
 * elapsedSecond()}.
 * 
 * @see  {@link Timer}
 * 
 * @author Martin Pernollet
 */
public class TicToc {
    public static TicToc T = new TicToc();

    public TicToc() {
        start = 0;
        stop = 0;
    }

    public void tic() {
        start = System.nanoTime();
    }

    /** return time in second */
    public double toc() {
        stop = System.nanoTime();
        return elapsedSecond();
    }

    public double tocShow(String message) {
        double stop = toc();
        System.out.println(message + " " + elapsedSecond() + "s\t" + elapsedMilisecond() + "ms\t" + elapsedMicrosecond() + "micro");
        return stop;
    }

    public long rawToc() {
        stop = System.nanoTime();
        return stop;
    }

    public long elapsedNanosecond() {
        return stop - start;
    }

    public double elapsedMicrosecond() {
        return elapsedNanosecond() / 1000;
    }

    public double elapsedMilisecond() {
        return elapsedMicrosecond() / 1000;
    }

    public double elapsedSecond() {
        return elapsedMilisecond() / 1000;
    }

    public long getStart() {
        return start;
    }

    public long getStop() {
        return stop;
    }

    /**********************************************/

    protected long start;
    protected long stop;
}
