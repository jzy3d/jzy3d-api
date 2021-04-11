package org.jzy3d.tests.frameCheck;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.Chart;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;

public class Frame {
  /**
   * Print an AWT chart frame content.
   * 
   * NB : does not activate pixel ratio!
   * 
   * @param chart
   * @param frame
   * @param image
   * @param file
   * @throws IOException
   */
  public static void print(Chart chart, FrameAWT frame, String file) throws IOException {
    BufferedImage image =
        new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);

    // TODO : move setupPrint/releasePrint to ICanvas to avoid casting to GLCAnvas and then allow
    // move
    // this method to FrameAWT (which is in a package not seeing JOGL)
    GLCanvas canvas = (GLCanvas) chart.getCanvas();
    // custom multisampling value: < 0 turns off, == 0 leaves as-is, > 0 enables using given num
    // samples
    int numSamples = 100;
    canvas.setupPrint(1, 1, numSamples, canvas.getWidth(), canvas.getHeight());
    frame.printAll(image.getGraphics());
    canvas.releasePrint();

    ImageIO.write(image, "png", new File(file));
    System.out.println("Print frame in " + file);
  }

  public static void print(Chart chart, FrameSwing frame, String file) throws IOException {
    BufferedImage image =
        new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);


    // TODO : move setupPrint/releasePrint to ICanvas to avoid casting to GLCAnvas and then allow
    // move
    // this method to FrameAWT (which is in a package not seeing JOGL)
    GLJPanel canvas = (GLJPanel) chart.getCanvas();
    // custom multisampling value: < 0 turns off, == 0 leaves as-is, > 0 enables using given num
    // samples
    int numSamples = 100;
    canvas.setupPrint(1, 1, numSamples, canvas.getWidth(), canvas.getHeight());
    frame.printAll(image.getGraphics());
    canvas.releasePrint();

    ImageIO.write(image, "png", new File(file));
    System.out.println(file);
  }

}
