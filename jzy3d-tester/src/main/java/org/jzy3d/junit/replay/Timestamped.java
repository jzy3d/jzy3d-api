package org.jzy3d.junit.replay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.junit.replay.events.IEventLog;
import org.jzy3d.maths.TicToc;

/** To be enhanced (wrapper tictoc) */
public class Timestamped {
  static Logger logger = LogManager.getLogger(Timestamped.class);

  protected TicToc t = new TicToc();

  public Timestamped() {
    super();
  }

  public void start() {
    t.tic();
  }

  public long startup() {
    return t.getStart();
  }

  public long now() {
    long now = t.rawToc();
    return now;
  }

  public long since() {
    t.toc();
    return (long) t.elapsedMilisecond();
  }

  public long since(long now) {
    return now - startup();
  }

  public long elapsedMs() {
    return (now() - startup()) / (1000 * 1000);
  }

  public boolean elapsedMs(IEventLog event) {
    return elapsedMs(event.since());
  }

  public boolean elapsedMs(long time) {
    return elapsedMs() > time;
  }

  public void debugMs(IEventLog event) {
    debugMs(event.since());
  }

  public void debugMs(long time) {
    long elapsed = elapsedMs();
    logger.info("-> @[" + time / 1000 + " s] (" + time + " ms): elapsed:" + elapsed);
    // logger.info("-> @[" + time/1000 + " s] (" + time + " ms): elapsed:" + elapsed + ", now:" +
    // now + ", start:" + startup());
  }
}
