package org.jzy3d.chart.controllers.mouse;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

/**
 * Utility helpers mirroring {@code AWTMouseUtilities} for SWT mouse events.
 *
 * SWT button numbering: {@code event.button} 1 = left, 2 = middle, 3 = right. While dragging,
 * SWT does not re-populate {@code event.button}; the held buttons are reported in
 * {@code event.stateMask} via the {@link SWT#BUTTON1}/{@link SWT#BUTTON3} bits.
 */
public class SWTMouseUtilities {

  public static boolean isDoubleClick(MouseEvent e) {
    return e.count > 1;
  }

  public static boolean isLeftDown(MouseEvent e) {
    return (e.stateMask & SWT.BUTTON1) != 0 || e.button == 1;
  }

  public static boolean isRightDown(MouseEvent e) {
    return (e.stateMask & SWT.BUTTON3) != 0 || e.button == 3;
  }

  public static boolean isRightClick(MouseEvent e) {
    return e.button == 3;
  }
}
