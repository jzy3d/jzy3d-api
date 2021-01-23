package org.jzy3d.maths;

/**
 * {@link TicToc} allows measuring elapsed time between a call to {@link tic()} and a call to
 * {@link toc()}. Retrieving elapsed time is done by calling either: {@link elapsedNanosecond()},
 * {@link elapsedMilisecond()} or {@link elapsedSecond()}.
 * 
 * @see {@link Timer}
 * 
 * @author Martin Pernollet
 */
public class TicToc {
  public static TicToc T = new TicToc();

  public static void tick() {
    T.tic();
  }

  public static void tock() {
    T.toc();
  }

  public static void tockShow(String message) {
    T.tocShow(message);
  }


  public TicToc() {
    start = System.nanoTime();
    stop = System.nanoTime();
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
    System.out.println(tocString(message));
    return stop;
  }

  private String tocString(String message) {
    toc();
    return message + " " + elapsedSecond() + "s\t" + elapsedMilisecond() + "ms\t"
        + elapsedMicrosecond() + "micro";
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
