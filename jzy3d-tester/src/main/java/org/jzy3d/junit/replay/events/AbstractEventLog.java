package org.jzy3d.junit.replay.events;

public class AbstractEventLog implements IEventLog {

  protected long since;

  @Override
  public long since() {
    return since;
  }

  public AbstractEventLog() {
    super();
  }

}
