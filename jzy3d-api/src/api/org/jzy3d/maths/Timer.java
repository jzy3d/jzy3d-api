package org.jzy3d.maths;

public class Timer {
    public static double TEN_POW_9 = 1000000000.0;
    public static double TEN_POW_6 = 1000000.0;

    protected long start;
    
    public void start() {
        System.out.println("timer start");
        start = System.nanoTime();
    }

    public double elapsed() {
        return (System.nanoTime() - start) / TEN_POW_9;
    }
}
