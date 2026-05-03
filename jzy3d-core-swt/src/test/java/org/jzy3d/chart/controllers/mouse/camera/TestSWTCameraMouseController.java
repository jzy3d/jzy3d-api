package org.jzy3d.chart.controllers.mouse.camera;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;

/**
 * Tests for {@link SWTCameraMouseController}'s slave-thread toggle behavior, mirroring the
 * AWT contract: a single click stops the auto-rotation, a double click starts it.
 */
public class TestSWTCameraMouseController {

  Display display;
  Shell shell;

  @Before
  public void setup() {
    display = Display.getCurrent();
    if (display == null) {
      display = new Display();
    }
    shell = new Shell(display);
  }

  @After
  public void teardown() {
    if (shell != null && !shell.isDisposed()) {
      shell.dispose();
    }
    if (display != null && !display.isDisposed()) {
      display.dispose();
    }
  }

  /**
   * Build a minimal {@link MouseEvent}. SWT's MouseEvent ctor is package-private so we go
   * through a {@link Event} populated by hand.
   */
  private MouseEvent newMouseEvent(int button, int count) {
    Event raw = new Event();
    raw.widget = shell;
    raw.display = display;
    raw.button = button;
    raw.count = count;
    raw.stateMask = 0;
    return new MouseEvent(raw);
  }

  /**
   * Regression guard for the bug "single click does not interrupt the auto-rotation".
   *
   * <p>{@code SWTCameraMouseController.handleSlaveThread} must mirror
   * {@code AWTCameraMouseController.handleSlaveThread}: any non-double click stops the slave
   * thread; a double click starts it. The previous SWT-only implementation only handled the
   * start path, so a simple click left the rotation running.
   */
  @Test
  public void singleClickStopsSlaveThread_doubleClickStartsIt() {
    SWTCameraMouseController c = new SWTCameraMouseController();
    CameraThreadController thread = mock(CameraThreadController.class);
    c.addThread(thread);

    // Simulate a single click (count=1): should request stop, never start.
    c.handleSlaveThread(newMouseEvent(SWT.BUTTON1, 1));
    verify(thread, times(1)).stop();
    verify(thread, never()).start();

    // Simulate a double click (count=2): should request start, no extra stop.
    c.handleSlaveThread(newMouseEvent(SWT.BUTTON1, 2));
    verify(thread, times(1)).start();
    verify(thread, times(1)).stop();

    // Another single click after double-click must stop the rotation again.
    c.handleSlaveThread(newMouseEvent(SWT.BUTTON1, 1));
    verify(thread, times(2)).stop();
    verify(thread, times(1)).start();
  }

  /**
   * If no thread controller is bound (e.g. controller used standalone), the toggle is a no-op
   * and must not throw.
   */
  @Test
  public void handleSlaveThread_withoutThreadController_isNoOp() {
    SWTCameraMouseController c = new SWTCameraMouseController();
    // No addThread call.
    c.handleSlaveThread(newMouseEvent(SWT.BUTTON1, 1));
    c.handleSlaveThread(newMouseEvent(SWT.BUTTON1, 2));
    // Reaching this point without throwing is the assertion.
  }
}
