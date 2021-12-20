package org.jzy3d.junit.replay;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.jzy3d.chart.Chart;
import org.jzy3d.junit.replay.events.ComponentEventLog;
import org.jzy3d.junit.replay.events.IComponentEventLog.ComponentEventType;
import org.jzy3d.junit.replay.events.IEventLog;
import org.jzy3d.junit.replay.events.IKeyEventLog.KeyEventType;
import org.jzy3d.junit.replay.events.IMouseEventLog.MouseEventType;
import org.jzy3d.junit.replay.events.IWindowEventLog.WindowEventType;
import org.jzy3d.junit.replay.events.KeyEventLog;
import org.jzy3d.junit.replay.events.MouseEventLog;
import org.jzy3d.junit.replay.events.WindowEventLog;

public class EventRecorder extends Timestamped implements MouseListener, MouseMotionListener,
    MouseWheelListener, KeyListener, ComponentListener, WindowListener {
  protected Component awt;
  protected Scenario scenario;
  protected Chart chart;
  protected int nScreenshot = 0;

  public EventRecorder(String scenario, Component awt) {
    this(scenario, awt, null);
  }

  public EventRecorder(String scenario, Component awt, Frame frame) {
    this(scenario, awt, frame, null);
  }

  public EventRecorder(String scenario, Component awt, Frame frame, Chart chart) {
    this.chart = chart;
    this.awt = awt;
    this.scenario = new Scenario(scenario);
    addListeners(awt);
    if (frame != null)
      addWindowListeners(frame);
    t.tic();
  }

  protected void addListeners(Component awt) {
    awt.addMouseListener(this);
    awt.addMouseMotionListener(this);
    awt.addMouseWheelListener(this);
    awt.addKeyListener(this);
    awt.addComponentListener(this);
  }

  protected void addWindowListeners(Frame frame) {
    frame.addWindowListener(this);
  }

  protected void register(IEventLog event) {
    debugMs(event);
    scenario.register(event);
  }

  protected void onExit() {
    try {
      scenario.save();
    } catch (Exception e) {
      logger.error(e);
    }
  }

  public Chart getChart() {
    return chart;
  }

  public void setChart(Chart chart) {
    this.chart = chart;
  }

  protected boolean isScreenshotKey(KeyEvent e) {
    return e.getKeyChar() == 's';
  }

  protected boolean isExit(KeyEvent e) {
    return e.getKeyChar() == 'q';
  }

  protected String screenshotFile(int n) {
    return ScenarioFiles.SCENARIO_FOLDER + "/" + scenario.getName() + "/" + scenario.getName() + "-"
        + n + ".png";
  }

  protected void screenshot(Chart chart, String filename) throws IOException {
    chart.screenshot(new File(filename));
    LogManager.getLogger(EventRecorder.class).info("screenshot:" + filename);
  }

  /* MOUSE */

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    register(
        new MouseEventLog(MouseEventType.MOUSE_WHEEL, e.getWheelRotation(), getButton(e), since()));
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    register(
        new MouseEventLog(MouseEventType.MOUSE_DRAGGED, e.getX(), e.getY(), getButton(e), since()));
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    register(
        new MouseEventLog(MouseEventType.MOUSE_MOVED, e.getX(), e.getY(), getButton(e), since()));
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    register(
        new MouseEventLog(MouseEventType.MOUSE_CLICKED, e.getX(), e.getY(), getButton(e), since()));
  }

  @Override
  public void mousePressed(MouseEvent e) {
    register(
        new MouseEventLog(MouseEventType.MOUSE_PRESSED, e.getX(), e.getY(), getButton(e), since()));
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    register(new MouseEventLog(MouseEventType.MOUSE_RELEASED, e.getX(), e.getY(), getButton(e),
        since()));
  }

  protected int getButton(MouseEvent e) {
    int button = 0;
    if (e.getButton() == MouseEvent.BUTTON1)
      button = InputEvent.BUTTON1_MASK;
    if (e.getButton() == MouseEvent.BUTTON2)
      button = InputEvent.BUTTON2_MASK;
    if (e.getButton() == MouseEvent.BUTTON3)
      button = InputEvent.BUTTON3_MASK;
    return button;
  }

  /* COMPONENT */

  @Override
  public void componentHidden(ComponentEvent e) {
    register(new ComponentEventLog(ComponentEventType.COMPONENT_HIDDEN, since()));
  }

  @Override
  public void componentMoved(ComponentEvent e) {
    register(new ComponentEventLog(ComponentEventType.COMPONENT_MOVED, e.getComponent().getSize(),
        e.getComponent().getBounds(), since()));
  }

  @Override
  public void componentResized(ComponentEvent e) {
    register(new ComponentEventLog(ComponentEventType.COMPONENT_RESIZED, e.getComponent().getSize(),
        e.getComponent().getBounds(), since()));
  }

  @Override
  public void componentShown(ComponentEvent e) {
    register(new ComponentEventLog(ComponentEventType.COMPONENT_SHOWN, e.getComponent().getSize(),
        e.getComponent().getBounds(), since()));
  }

  /* KEY */

  @Override
  public void keyTyped(KeyEvent e) {
    // handle screenshot query
    if (isScreenshotKey(e)) {
      try {
        screenshot(chart, screenshotFile(nScreenshot));
      } catch (IOException e1) {
        LogManager.getLogger(EventRecorder.class).error(screenshotFile(nScreenshot), e1);
      }
      nScreenshot++;
    } else if (isExit(e)) {
      onExit();
    }
    // or register standard event
    else {
      register(new KeyEventLog(KeyEventType.KEY_TYPED, e.getKeyCode(), since()));
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    register(new KeyEventLog(KeyEventType.KEY_PRESS, e.getKeyCode(), since()));
  }

  @Override
  public void keyReleased(KeyEvent e) {
    register(new KeyEventLog(KeyEventType.KEY_RELEASE, e.getKeyCode(), since()));
  }

  // protected String s

  /* WINDOW */

  @Override
  public void windowClosing(WindowEvent e) {
    register(new WindowEventLog(WindowEventType.WINDOW_CLOSING, e.getWindow().getSize(), since()));

    onExit();
  }

  @Override
  public void windowOpened(WindowEvent e) {
    register(new WindowEventLog(WindowEventType.WINDOW_OPENED, e.getWindow().getSize(), since()));
  }

  @Override
  public void windowClosed(WindowEvent e) {
    register(new WindowEventLog(WindowEventType.WINDOW_CLOSED, since()));
  }

  /* UNUSED */

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void windowActivated(WindowEvent arg0) {}

  @Override
  public void windowDeactivated(WindowEvent arg0) {}

  @Override
  public void windowDeiconified(WindowEvent arg0) {}

  @Override
  public void windowIconified(WindowEvent arg0) {}
}
