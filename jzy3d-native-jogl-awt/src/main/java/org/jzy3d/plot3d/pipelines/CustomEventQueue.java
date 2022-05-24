package org.jzy3d.plot3d.pipelines;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Utility for debugging event queue related to 3d in AWT. */
public class CustomEventQueue extends EventQueue {
  static Logger logger = LogManager.getLogger(CustomEventQueue.class);

  @Override
  protected void dispatchEvent(AWTEvent event) {
    if (event instanceof PaintEvent)
      logger.info("Dispatch [PAINT]: " + event);
    else if (event instanceof MouseEvent)
      logger.info("Dispatch [MOUSE]: " + event);
    else if (event instanceof InvocationEvent) {
      logger.info("Dispatch [INVOC]: " + event);
    } else
      logger.warn("Dispatch [UNKNO]: " + event);

    super.dispatchEvent(event);
  }

  public static void setCustomEventQueue() {
    if (!customQueueSet)
      Toolkit.getDefaultToolkit().getSystemEventQueue().push(new CustomEventQueue());
  }

  /*********************************************************/

  private static boolean customQueueSet = false;
}
