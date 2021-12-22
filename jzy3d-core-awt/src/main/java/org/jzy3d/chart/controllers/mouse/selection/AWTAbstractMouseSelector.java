package org.jzy3d.chart.controllers.mouse.selection;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.AbstractViewportManager;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;

public abstract class AWTAbstractMouseSelector implements MouseListener, MouseMotionListener {
  static Logger LOGGER = LogManager.getLogger(AWTAbstractMouseSelector.class);
  
  protected Chart chart;
  protected ICanvas canvas;

  protected boolean dragging = false;
  protected IntegerCoord2d in;
  protected IntegerCoord2d out;
  protected IntegerCoord2d last;
  protected AWTRenderer2d selectionRenderer;

  public AWTAbstractMouseSelector() {
    in = new IntegerCoord2d(-1, -1);
    last = new IntegerCoord2d(-1, -1);
    out = new IntegerCoord2d(-1, -1);
  }

  public void dispose() {
    unregister();
  }

  public void register(Chart chart) {
    this.chart = chart;
    this.chart.getCanvas().addMouseController(this);
    this.canvas = chart.getCanvas();
    
    if (chart.getView() instanceof AWTView) {
      this.selectionRenderer = initRenderer2d(canvas);
      ((AWTView) this.chart.getView()).addRenderer2d(selectionRenderer);
    }
  }

  public void unregister() {
    if (chart != null) {
      chart.getCanvas().removeMouseController(this);
      if (chart.getView() instanceof AWTView)
        ((AWTView) this.chart.getView()).removeRenderer2d(selectionRenderer);
    }
  }

  protected AWTRenderer2d initRenderer2d(final ICanvas c) {
    return new AWTRenderer2d() {
      @Override
      public void paint(Graphics g, int canvasWidth, int canvasHeight) {
        drawSelection((Graphics2D) g, c.getRendererWidth(), c.getRendererHeight());
        updateLast();
      }
    };
  }

  /*****************************************/

  protected abstract void processSelection(Scene scene, View view, int width, int height);

  /**
   * Drawing occurs in the selection renderer which as the dimension of the GL2 scene viewport. In
   * other words, one should not expect to draw on the entire canvas if the GL2 scene viewport only
   * covers a slice of the screen. As an example, the following piece of code will draw a border
   * around the GL2 scene:
   * 
   * g2d.drawRect(1, 1, chart.getCanvas().getRendererWidth()-2,
   * chart.getCanvas().getRendererHeight()-2);
   * 
   * @see {@link AbstractViewportManager} and {@link Camera}
   */
  protected abstract void drawSelection(Graphics2D g, int width, int height);

  public abstract void clearLastSelection();

  protected boolean matchRectangleSelection(IntegerCoord2d in, IntegerCoord2d out,
      Coord3d projection, int width, int height) {
    return matchRectangleSelection(in, out, projection.x, projection.y, width, height);
  }

  /**
   * @param in The mouse selection start point
   * @param out The mouse selection end point
   * @param px A projected point x value
   * @param py A projected point y value
   * @param width The canvas dimension
   * @param height The canvas dimension
   * @return true if the point (px,py) is inside the (in,out) mouse selection of a canvas having
   *         dimensions (width, height)
   */
  protected boolean matchRectangleSelection(IntegerCoord2d in, IntegerCoord2d out, float px,
      float py, int width, int height) {
    float flipYProjection = height - py;

    // 4|3
    // ---
    // 2|1
    if (in.y < out.y) {
      if (in.x < out.x) {
        // ("1");
        if (in.x <= px && px <= out.x && in.y <= flipYProjection && flipYProjection <= out.y)
          return true;
      } else {
        // ("2");
        if (out.x <= px && px <= in.x && in.y <= flipYProjection && flipYProjection <= out.y)
          return true;
      }

    } else {
      if (in.x < out.x) {
        // ("3");
        if (in.x <= px && px <= out.x && out.y <= flipYProjection && flipYProjection <= in.y)
          return true;
      } else {
        // ("4");
        if (out.x <= px && px <= in.x && out.y <= flipYProjection && flipYProjection <= in.y) // buggy
          return true;
      }
    }
    return false;
  }

  protected void drawRectangle(Graphics2D g2d, IntegerCoord2d in, IntegerCoord2d out) {
    // LOGGER.info(" " + in + " " + out);

    g2d.setColor(java.awt.Color.RED);
    if (in.y < out.y) {
      if (in.x < out.x)
        g2d.drawRect(in.x, in.y, out.x - in.x, out.y - in.y);
      else
        g2d.drawRect(out.x, in.y, in.x - out.x, out.y - in.y);
    } else {
      if (in.x < out.x)
        g2d.drawRect(in.x, out.y, out.x - in.x, in.y - out.y);
      else
        g2d.drawRect(out.x, out.y, in.x - out.x, in.y - out.y);
    }
  }

  /*****************************************/

  protected void startSelection(MouseEvent e) {
    in = xy(e);
    last = xy(e);
    out = xy(e);
  }

  public IntegerCoord2d xy(MouseEvent e) {
    return new IntegerCoord2d(x(e), y(e));
  }

  public int x(MouseEvent e) {
    return e.getX();
  }

  public int y(MouseEvent e) {
    return e.getY();
  }

  protected void dragSelection(MouseEvent e) {
    out.x = x(e);
    out.y = y(e);
    chart.render();
  }

  protected void releaseSelection(MouseEvent e) {
    out.x = x(e);
    out.y = y(e);

    processSelection(chart.getScene(), chart.getView(), chart.getCanvas().getRendererWidth(),
        chart.getCanvas().getRendererHeight());
    chart.render(); // calls draw selection
  }

  protected void rollOver(MouseEvent e) {

  }

  protected void updateLast() {
    last.x = out.x;
    last.y = out.y;
  }

  /*****************************************/

  @Override
  public void mousePressed(MouseEvent e) {
    dragging = true;
    startSelection(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (dragging)
      releaseSelection(e);
    dragging = false;
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (dragging)
      dragSelection(e);
    // else
    // rollOver(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    rollOver(e);
  }

  /*****************************************/

  public void mouseWheelMoved(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseClicked(MouseEvent e) {}
}
