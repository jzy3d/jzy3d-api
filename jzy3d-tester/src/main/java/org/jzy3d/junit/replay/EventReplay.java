package org.jzy3d.junit.replay;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.junit.replay.events.IComponentEventLog;
import org.jzy3d.junit.replay.events.IComponentEventLog.ComponentEventType;
import org.jzy3d.junit.replay.events.IEventLog;
import org.jzy3d.junit.replay.events.IKeyEventLog;
import org.jzy3d.junit.replay.events.IKeyEventLog.KeyEventType;
import org.jzy3d.junit.replay.events.IMouseEventLog;
import org.jzy3d.junit.replay.events.IMouseEventLog.MouseEventType;
import org.jzy3d.junit.replay.events.IWindowEventLog;

public class EventReplay extends Timestamped {
  static Logger logger = LogManager.getLogger(EventReplay.class);

  protected Component component;
  protected Frame frame;
  protected Robot robot;
  protected Scenario currentScenario;
  protected boolean mute = false;
  protected boolean mustStop = false;

  public EventReplay(final Component component, Frame frame) {
    /*
     * component.addMouseListener(new MouseAdapter(){ public void mouseDragged(MouseEvent e){
     * logger.info("DRAGGING"); } });
     */
    this.component = component;
    this.frame = frame;
    configureFocus(component, frame);
  }

  protected void configureFocus(Component component, Frame frame) {
    component.addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent arg0) {
        mute();
        logger.info("mute event replay as component lost focus");
      }

      @Override
      public void focusGained(FocusEvent arg0) {
        unmute();
        logger.info("unmute event replay as component gained focus");
      }
    });
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent arg0) {
        stop();
        logger.info("stop replay as window closes");
      }
    });
  }

  /* REPLAY */

  public void replay(String scenario) throws Exception {
    currentScenario = new Scenario(scenario);
    currentScenario.load();
    replay(currentScenario);
  }

  public void replay(Scenario scenario) {
    // component.requestFocusInWindow();

    getRobot().setAutoWaitForIdle(false);
    start();
    while (scenario.getEvents().size() > 0) {
      Iterator<IEventLog> events = scenario.getEvents().iterator();
      while (events.hasNext()) {
        IEventLog event = events.next();
        if (elapsedMs(event)) {
          if (!mute)
            replay(event);
          // debug(event);
          events.remove();
        }
        if (mustStop) {
          scenario.info("done replay after " + elapsedMs() / 1000 + " s");
          scenario.info("exiting before end");
          return;
        }
      }
    }
    scenario.info("done replay after " + elapsedMs() / 1000 + " s");
    /*
     * for(IEventLog event: scenario.getEvents()){ replay(event); }
     */
  }

  public void replay(IEventLog event) {
    if (event instanceof IMouseEventLog)
      replay((IMouseEventLog) event);
    else if (event instanceof IKeyEventLog)
      replay((IKeyEventLog) event);
    else if (event instanceof IComponentEventLog)
      replay((IComponentEventLog) event);
    else if (event instanceof IWindowEventLog)
      replay((IWindowEventLog) event);
  }


  public void replay(IMouseEventLog mouse) {
    Insets insets = frame.getInsets();

    MouseEventType type = mouse.getType();
    if (type == MouseEventType.MOUSE_CLICKED)
      getRobot().mousePress(mouse.getButton());

    else if (type == MouseEventType.MOUSE_PRESSED) {
      getRobot().mousePress(mouse.getButton());
    } else if (type == MouseEventType.MOUSE_RELEASED) {
      getRobot().mouseRelease(mouse.getButton());
    } else if (type == MouseEventType.MOUSE_DRAGGED) {
      getRobot().mouseMove(moveX(mouse, insets), moveY(mouse, insets));
    } else if (type == MouseEventType.MOUSE_MOVED) {
      getRobot().mouseMove(moveX(mouse, insets), moveY(mouse, insets));
    } else if (type == MouseEventType.MOUSE_WHEEL) {
      getRobot().mouseWheel(mouse.getValue());
    }

    // if(type!=MouseEventType.MOUSE_DRAGGED && type!=MouseEventType.MOUSE_MOVED)
    // log(mouse);
  }

  protected int moveY(IMouseEventLog mouse, Insets insets) {
    return mouse.getCoord().y + frame.getBounds().y + insets.top;
  }

  protected int moveX(IMouseEventLog mouse, Insets insets) {
    return mouse.getCoord().x + frame.getBounds().x + insets.left;
  }

  public void replay(IKeyEventLog key) {
    try {
      if (key.getKeyCode() != 0) {
        if (key.getType() == KeyEventType.KEY_PRESS)
          doKeyPress(key);
        else if (key.getType() == KeyEventType.KEY_RELEASE)
          doKeyRelease(key);
        else
          logger.warn("ignore key event " + key);
      } else
        logger.warn("ignore key event " + key + " because keycode=0");
    } catch (Exception e) {
      log(key);
      e.printStackTrace();
    }
  }

  protected void doKeyPress(IKeyEventLog key) {
    getRobot().keyPress(key.getKeyCode());
    // keys[key.getKeyCode()]++;
  }

  protected void doKeyRelease(IKeyEventLog key) {
    getRobot().keyRelease(key.getKeyCode());
    // keys[key.getKeyCode()]--;
  }

  protected void mute() {
    mute = true;
  }

  protected void unmute() {
    mute = false;
  }

  protected void stop() {
    mustStop = true;
  }

  protected int[] keys = new int[256];

  public void replay(IWindowEventLog window) {
    log(window, " not supported");
  }

  public void replay(IComponentEventLog component) {
    if (component.getType().equals(ComponentEventType.COMPONENT_RESIZED)) {
      Dimension size = component.getSize();
      frame.setSize(size);
    }
    // log(component, " not supported");
  }

  protected void log(IEventLog event) {
    logger.info("replay: " + event);
  }

  protected void log(IEventLog event, String info) {
    logger.info("replay: " + event + " " + info);
  }

  public Robot getRobot() {
    if (robot == null)
      try {
        robot = new Robot();
      } catch (AWTException e) {
        throw new RuntimeException(e);
      }

    robot.setAutoWaitForIdle(true);
    return robot;
  }

  /* UTILS 2 */

  /**
   * MOUSE_PRESSED, x:170, y:154, bt:0, since:925 MOUSE_DRAGGED, x:149, y:171, bt:0, since:1428
   * MOUSE_RELEASED, x:125, y:56, bt:0, since:3898 MOUSE_MOVED, x:107, y:55, bt:0, since:3899
   * 
   * @param mouse
   */
  protected boolean detectDragEnd(IMouseEventLog mouse) {
    MouseEventType type = mouse.getType();
    if (type == MouseEventType.MOUSE_PRESSED) {
      drag = new IMouseEventLog[3];
      drag[0] = mouse;
    } else if (type == MouseEventType.MOUSE_DRAGGED)
      drag[1] = mouse;
    else if (type == MouseEventType.MOUSE_RELEASED) {
      drag[2] = mouse;
      boolean f = full(drag);
      drag = new IMouseEventLog[3];
      if (f)
        return true;
    }
    return false;
  }

  protected boolean full(IMouseEventLog[] drag) {
    return drag != null && drag[0] != null && drag[1] != null && drag[2] != null;
  }

  protected IMouseEventLog[] drag = new IMouseEventLog[3];

  /* UTILS */

  protected void debug(IEventLog event) {
    if (event instanceof IMouseEventLog) {
      IMouseEventLog mouse = (IMouseEventLog) event;
      // if(isMouseMoveOrDrag(mouse)){
      debugMs(event);
      // }
    } else
      debugMs(event);
  }

  protected boolean isMouseMoveOrDrag(IMouseEventLog mouse) {
    return mouse.getType() != MouseEventType.MOUSE_DRAGGED
        && mouse.getType() != MouseEventType.MOUSE_MOVED;
  }

  protected void addVerifyingListeners(final Component component) {
    component.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        logger.info("ASSERT DRAGGED CALLED ON COMPONENT");
      }
    });
    component.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        logger.info("ASSERT MOVED CALLED ON COMPONENT");
      }
    });
  }

  protected void addFocusListener(final Component component, Frame frame) {
    frame.addWindowFocusListener(new WindowAdapter() {
      @Override
      public void windowGainedFocus(WindowEvent e) {
        component.requestFocusInWindow();
      }
    });
  }

}
