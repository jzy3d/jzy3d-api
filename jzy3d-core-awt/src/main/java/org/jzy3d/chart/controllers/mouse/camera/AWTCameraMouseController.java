package org.jzy3d.chart.controllers.mouse.camera;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.AWTMouseUtilities;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.view.View;


public class AWTCameraMouseController extends AbstractCameraController
    implements MouseListener, MouseWheelListener, MouseMotionListener {

  protected RateLimiter rateLimiter;
  
  protected Coord2d prevMouse = Coord2d.ORIGIN;
  protected Coord2d startMouse = Coord2d.ORIGIN;
  

  public AWTCameraMouseController() {}

  public AWTCameraMouseController(Chart chart) {
    register(chart);
    addThread(chart.getFactory().newCameraThreadController(chart));
  }
  
  public AWTCameraMouseController(Chart chart, RateLimiter limiter) {
    this(chart);
    setRateLimiter(limiter);
  }
  
  public RateLimiter getRateLimiter() {
    return rateLimiter;
  }

  public void setRateLimiter(RateLimiter rateLimiter) {
    this.rateLimiter = rateLimiter;
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
    chart.getCanvas().addMouseController(this);
  }

  @Override
  public void dispose() {
    for (Chart chart : targets) {
      chart.getCanvas().removeMouseController(this);
    }
    super.dispose();
  }

  /**
   * Handles toggle between mouse rotation/auto rotation: double-click starts the animated rotation,
   * while simple click stops it.
   */
  @Override
  public void mousePressed(MouseEvent e) {
	if(getChart()!=null) {
	  if(getChart().getView().is2D()) {
	    return;
	  }
	}
    
    if (handleSlaveThread(e))
      return;

    prevMouse.x = x(e);
    prevMouse.y = y(e);
    startMouse = prevMouse.clone();
    
    System.out.println("Pressed " + e);

  }



  /** Compute shift or rotate */
  @Override
  public void mouseDragged(MouseEvent e) {
	boolean is3D = true;
    
    if(getChart()!=null) {
      if(getChart().getView().is2D()) {
        is3D = false;
      }
	}
    

    // Check if mouse rate limiter wish to forbid this mouse drag instruction
    if(rateLimiter!=null && !rateLimiter.rateLimitCheck()) {
      return;
    }

    // Apply mouse drag
    Coord2d mouse = xy(e);

    // 3D mode
    if(is3D) {
      // Rotate if left button down
      if (AWTMouseUtilities.isLeftDown(e)) {
        Coord2d move = mouse.sub(prevMouse).div(100);
        rotate(move);
      }

      // Shift if right button down
      else if (AWTMouseUtilities.isRightDown(e)) {
        Coord2d move = mouse.sub(prevMouse);
        if (move.y != 0)
          shift(move.y / 500);
      }
      
    }
    
    // 2D mode
    else {
    }
	
    prevMouse = mouse;
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
    System.out.println("Release " + e);
    
    boolean is2D = true;
    
    if(getChart()!=null) {
      if(getChart().getView().is2D()) {
        is2D = true;
      }
    }
    
    if(is2D) {
      View view = getChart().getView();
      IPainter painter = view.getPainter();
      
      Coord3d start = painter.screenToModel(new Coord3d(startMouse));
      Coord3d stop = painter.screenToModel(new Coord3d(xy(e)));
      BoundingBox3d bounds = view.getBounds();
      
      System.out.println(start);
      System.out.println(stop);
      System.out.println(bounds);
      
      if(view.is2D_XY()) {
        if(view.get2DLayout().isHorizontalAxisFlip()) {
          bounds.setXmin(stop.x);
          bounds.setXmax(start.x);                    
        }
        else {
          bounds.setXmin(start.x);
          bounds.setXmax(stop.x);          
        }
        
        if(view.get2DLayout().isVerticalAxisFlip()) {
          bounds.setYmin(stop.y);
          bounds.setYmax(start.y);
        }
        else {
          bounds.setYmin(start.y);
          bounds.setYmax(stop.y);
        }
        
      }
      
      System.out.println(bounds);

      view.setBoundsManual(bounds);
      view.shoot();
      
    }
  }



  /** Compute zoom */
  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
	if(getChart()!=null) {
      if(getChart().getView().is2D()) {
        return;
      }
	}
	
    // Check if mouse rate limiter wish to forbid this mouse drag instruction
    if(rateLimiter!=null && !rateLimiter.rateLimitCheck()) {
      return;
    }
    
    stopThreadController();
    float factor = 1 + (e.getWheelRotation() / 10.0f);
    zoomZ(factor);
  }

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}


  @Override
  public void mouseMoved(MouseEvent e) {}

  protected boolean handleSlaveThread(MouseEvent e) {
    if (AWTMouseUtilities.isDoubleClick(e)) {
      if (threadController != null) {
        threadController.start();
        return true;
      }
    }
    if (threadController != null)
      threadController.stop();
    return false;
  }

  protected Coord2d xy(MouseEvent e) {
    return new Coord2d(x(e), y(e));
  }

  protected int y(MouseEvent e) {
    return e.getY();
  }

  protected int x(MouseEvent e) {
    return e.getX();
  }
}
