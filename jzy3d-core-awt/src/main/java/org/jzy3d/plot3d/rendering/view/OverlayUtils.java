package org.jzy3d.plot3d.rendering.view;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.maths.PolygonArray;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Statistics;


/** Helps understanding how overlay is actually performed by JOGL2. */
public class OverlayUtils {
  static Logger logger = LogManager.getLogger(OverlayUtils.class);

  public void drawSelection(Graphics2D g2d, IntegerCoord2d in, int width, int height) {
    // shows mouse pointer
    // IntegerCoord2d trans = viewportToCanvas( in );
    // drawPixel(g2d, java.awt.Color.RED, trans.x, trans.y, 6); // the canvas coordinates are
    // stretched by overlay afterward
    // drawCanvasAndViewportDiagonalComparison(g2d);
  }

  public static void drawCanvasAndViewportDiagonalComparison(Chart chart, Graphics2D g2d) {
    // chart diagonal displayed on viewport diagonal!!
    int wmax = chart.getCanvas().getRendererWidth();
    int hmax = chart.getCanvas().getRendererHeight();
    drawDiagonal(g2d, java.awt.Color.RED, wmax, hmax, true);

    // viewport diagonal badly displayed!!
    Rectangle r = chart.getView().getSceneViewportRectangle();
    drawDiagonal(g2d, java.awt.Color.BLUE, r.width, r.height, false);
    logger.info(" BUT viewport.dim (" + r.width + "," + r.height + ")");

    /// chart.getCanvas().getRendererWidth()
    // g2d.drawRect(r.x, r.y, r.width, r.height);
    drawChartBorder(g2d, java.awt.Color.GREEN, chart); // chart border is drawn on viewport border!
  }

  public static void drawPixel(Graphics2D g2d, java.awt.Color c, int x, int y, int width) {
    g2d.setColor(c);
    if (width >= 2)
      g2d.fillRect(x - width / 2, y - width / 2, width, width);
    else
      g2d.fillRect(x, y, 1, 1);
  }

  public static void drawPixel(Graphics2D g2d, java.awt.Color c, int x, int y) {
    drawPixel(g2d, c, x, y, 1);
  }

  public static void drawText(Graphics2D g2d, java.awt.Color c, int x, int y, String txt) {
    g2d.setColor(c);
    g2d.drawString(txt, x, y);
  }

  /**
   * This shows a strange behaviour in the Overlay: when trying to draw a rectangle based on the
   * CANVAS size (i.e. the window size), the border occupies the CHART (i.e. its actual viewport).
   */
  public static void drawChartBorder(Graphics2D g2d, java.awt.Color c, Chart chart) {
    g2d.setColor(c);
    g2d.drawRect(1, 1, chart.getCanvas().getRendererWidth() - 2,
        chart.getCanvas().getRendererHeight() - 2);
    // encadre le viewport du chart, ce qui montre que les coordonnees sont relative au viewport et
    // pas a la fenetre
  }

  /** Diagonal made of points from (0,0) to (wmax, hmax). */
  public static void drawDiagonal(Graphics2D g2d, java.awt.Color c, int wmax, int hmax,
      boolean sysout) {
    int dmax = Math.max(wmax, hmax);

    for (int i = 0; i < dmax; i++) { // try a diagonal
      float r = ((float) i) / ((float) dmax);
      int wcur = (int) (r * wmax);
      int hcur = (int) (r * hmax);
      // logger.info(wcur + " " + hcur);
      drawPixel(g2d, c, wcur, hcur);

      if (sysout) {
        if (i == 0)
          logger.info(" diagonal from (" + wcur + "," + hcur + ")");
        else if (i == (dmax - 1))
          logger.info(" to (" + wcur + "," + hcur + ")");
      }
    }
  }

  /******************** CONVERSIONS **************************/

  /**
   * Needed when wishing to adapt a coordinate relative to the canvas to the viewport frame. If the
   * mouse does not stand on top of the viewport (meaning the viewport is smaller than the canvas),
   * then the output coordinate is (-1,-1).
   */
  protected IntegerCoord2d canvasToViewport(MouseEvent e, Rectangle viewport) {
    if (viewport.x <= e.getX() && e.getX() <= (viewport.x + viewport.width)
        && viewport.y <= e.getY() && e.getY() <= (viewport.y + viewport.height)) {
      IntegerCoord2d translation = new IntegerCoord2d(e.getX() - viewport.x, e.getY() - viewport.y);
      return translation;
    } else {
      return new IntegerCoord2d(-1, -1);
    }
  }

  protected IntegerCoord2d canvasToViewport(IntegerCoord2d c, Rectangle viewport) {
    return canvasToViewport(viewport, c);
  }

  protected IntegerCoord2d canvasToViewport(Rectangle viewport, IntegerCoord2d c) {
    if (viewport.x <= c.x && c.x <= (viewport.x + viewport.width) && viewport.y <= c.y
        && c.y <= (viewport.y + viewport.height)) {
      IntegerCoord2d translation = new IntegerCoord2d(c.x - viewport.x, c.y - viewport.y);
      return translation;
    } else {
      return new IntegerCoord2d(-1, -1);
    }
  }

  protected PolygonArray canvasToViewport(PolygonArray p, Rectangle viewport) {
    int len = p.length();
    float[] x = new float[len];
    float[] y = new float[len];

    for (int i = 0; i < len; i++)
      x[i] = p.x[i] - viewport.x;
    for (int j = 0; j < len; j++)
      y[j] = p.y[j] - viewport.y;

    return new PolygonArray(x, y, p.z);
  }

  /**
   * Needed when wishing to display a coordinate relative to the viewport.
   * 
   * Expect a coordinate in viewport frame to be converted to the canvas frame, since Overlay expect
   * coordinates in the canvas frame and then stretch it to be displayed in viewport frame!
   */
  protected IntegerCoord2d viewportToCanvas(Chart chart, IntegerCoord2d input, Rectangle viewport) {
    int canvasWidth = chart.getCanvas().getRendererWidth();
    int canvasHeight = chart.getCanvas().getRendererHeight();

    IntegerCoord2d output = new IntegerCoord2d();
    output.x = (int) ((((float) input.x) / ((float) viewport.width)) * canvasWidth);
    output.y = (int) ((((float) input.y) / ((float) viewport.height)) * canvasHeight);
    return output;
  }

  protected void projectionStat(PolygonArray[][] array) {
    float min = Float.POSITIVE_INFINITY;
    float max = Float.NEGATIVE_INFINITY;

    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < array[i].length; j++) {
        PolygonArray p = array[i][j];

        float tmax = Statistics.max(p.x);
        if (tmax > max)
          max = tmax;
        float tmin = Statistics.min(p.x);
        if (tmin < min)
          min = tmin;
      }
    }
    logger.info("xmin=" + min + " xmax=" + max);
  }
}
