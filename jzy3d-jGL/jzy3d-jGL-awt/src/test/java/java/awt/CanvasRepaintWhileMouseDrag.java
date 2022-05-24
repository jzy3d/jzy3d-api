package java.awt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Show that a repaint task required too often by a mouse drag behaviour will not lead to continuous
 * updates.
 * 
 * @author martin
 *
 */
public class CanvasRepaintWhileMouseDrag {
  public static void main(String[] args) {

    Canvas canvas = new Canvas() {
      public void paint(Graphics g) {
        g.drawString("paint(Graphics g) : " + kPaint, 10, 10);
        kPaint++;
      }

      int kPaint = 0;
    };

    canvas.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseMoved(MouseEvent e) {}

      boolean DO_HARD_WORK = true;
      int HARD_WORK_DURATION = 50;//

      @Override
      public void mouseDragged(MouseEvent e) {
        try {
          // This shows that when processing is too slow in the mouse listener,
          // then canvas won't repaint immediately while exiting, but rather wait that
          // multiple
          // call to repaint really finished

          if (DO_HARD_WORK)
            Thread.sleep(HARD_WORK_DURATION);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
        canvas.repaint();
      }
    });

    Frame f = new Frame();
    f.add(canvas);
    f.pack();
    // f.setSize(bounds.width, bounds.height);
    f.setBounds(10, 10, 300, 100);

    f.setVisible(true);

    f.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        f.dispose();
      }
    });
  }

}
