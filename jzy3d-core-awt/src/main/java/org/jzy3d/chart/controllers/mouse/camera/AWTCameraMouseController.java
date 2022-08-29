package org.jzy3d.chart.controllers.mouse.camera;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.AWTMouseUtilities;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.AbstractAWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;


public class AWTCameraMouseController extends AbstractCameraController
    implements MouseListener, MouseWheelListener, MouseMotionListener {

  protected RateLimiter rateLimiter;
  
  protected Coord2d prevMouse = Coord2d.ORIGIN;
  protected Coord2d startMouse = Coord2d.ORIGIN;
  
  MouseMoveRenderer moveRenderer = new MouseMoveRenderer();
  MouseDragRenderer dragRenderer = new MouseDragRenderer();
  
  MouseSelection mouseSelection = new MouseSelection();
  MousePosition mousePosition = new MousePosition();
  
  MouseSelectionSettings selectionSettings = new MouseSelectionSettings();
  

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


    if(chart.getView() instanceof AWTView) {
      ((AWTView)chart.getView()).addRenderer2d(moveRenderer);
      ((AWTView)chart.getView()).addRenderer2d(dragRenderer);
    }

  }
  
  public void unregister(Chart chart) {
    super.unregister(chart);
    
    if(chart.getView() instanceof AWTView) {
      ((AWTView)chart.getView()).removeRenderer2d(moveRenderer);
      ((AWTView)chart.getView()).removeRenderer2d(dragRenderer);
    }
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
    //System.out.println("Pressed " + e);

    prevMouse.x = x(e);
    prevMouse.y = y(e);
    startMouse = prevMouse.clone();

    boolean is3D = true;
    
	if(getChart()!=null) {
	  is3D = !getChart().getView().is2D();
	}

	if(is3D) {
	    if (handleSlaveThread(e)) {
	      return;
	    }	  
	}
	else {
	  // stop displaying mouse position on roll over
	  mousePosition = new MousePosition();
	  
	  // start creating a selection
	  mouseSelection.start2D = startMouse;
      mouseSelection.start3D = screenToModel(e);
	}
    

  }
  
  protected Coord3d screenToModel(MouseEvent e) {
    // Flip the Y axis
    int y = getChart().getCanvas().getRendererHeight() - y(e);
    Coord3d mouse = new Coord3d(x(e), y, 0);

    // Project to 3D
    return screenToModel(mouse);
  }
  
  protected Coord3d screenToModel(Coord3d mouse) {
    IPainter painter = getChart().getPainter();
    
    IViewportLayout layout = getChart().getView().getLayout();
    
    if(layout instanceof ViewAndColorbarsLayout) {
      ViewAndColorbarsLayout viewLayout = (ViewAndColorbarsLayout)layout;
      ViewportConfiguration viewport = viewLayout.getSceneViewport();
      
      if(viewport.getHeight()<mouse.y || viewport.getWidth()<mouse.x)
        return null;
    }
    
    painter.acquireGL();
    Coord3d model = painter.screenToModel(mouse);
    painter.releaseGL();
    return model;
  }



  /** Compute shift or rotate */
  @Override
  public void mouseDragged(MouseEvent e) {
	boolean is3D = true;
    
    if(getChart()!=null) {
      is3D = !getChart().getView().is2D();
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
      View view = getChart().getView();

      mouseSelection.stop2D = xy(e);
      mouseSelection.stop3D = screenToModel(e);
      
      view.shoot();
      
    }
	
    prevMouse = mouse;
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
    //System.out.println("Release " + e);
    
    boolean is2D = true;
    
    if(getChart()!=null) {
      is2D = getChart().getView().is2D();
    }
    
    if(is2D) {
      View view = getChart().getView();
      //IPainter painter = view.getPainter();
      
      if(!mouseSelection.complete()) {
        return;
      }
      
      
      // Reset selection
      if(!mouseSelection.growing()) {
        //getChart().getScene().getGraph().setClipBox(null);

        view.setBoundMode(ViewBoundMode.AUTO_FIT);
      }
      // Or apply selection
      else {
        BoundingBox3d bounds = view.getBounds().clone();

        if(view.is2D_XY()) {
          if(view.get2DLayout().isHorizontalAxisFlip()) {
            bounds.setXmin(mouseSelection.stop3D.x);
            bounds.setXmax(mouseSelection.start3D.x);                    
          }
          else {
            bounds.setXmin(mouseSelection.start3D.x);
            bounds.setXmax(mouseSelection.stop3D.x);          
          }
          
          if(view.get2DLayout().isVerticalAxisFlip()) {
            bounds.setYmin(mouseSelection.stop3D.y);
            bounds.setYmax(mouseSelection.start3D.y);
          }
          else {
            bounds.setYmin(mouseSelection.start3D.y);
            bounds.setYmax(mouseSelection.stop3D.y);
          }
        }
        
        //System.out.println("2D select on " + bounds);
        mouseSelection = new MouseSelection();
        view.setBoundsManual(bounds);
        
        //getChart().getScene().getGraph().setClipBox(bounds);
      }

      // Update display

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
  public void mouseClicked(MouseEvent e) {
    System.out.println("Clicked " + e);

    
  }

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {
    
    // Reset mouse position memory as mouse exit the canvas
    mousePosition = new MousePosition();
    
    // Update display
    if(getChart()!=null) {
      getChart().getView().shoot();
      
    }
  }


  @Override
  public void mouseMoved(MouseEvent e) {
    View view = getChart().getView();
    
    
    Coord2d mouse = new Coord2d(x(e), getChart().getCanvas().getRendererHeight()-y(e));
    
    
    //mouseSelection.stop2D = xy(e);
    
    mousePosition.e = e;
    mousePosition.projection = screenToModel(new Coord3d(mouse));
    view.shoot();
    
  }
  
  

  
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
  
  //
  
  class MouseMoveRenderer extends AbstractAWTRenderer2d{
    //MouseEvent e;
    //Coord3d projection;
    
    class MouseProjection{
      
    }
    
    @Override
    public void paint(Graphics g, int canvasWidth, int canvasHeight) {
      Graphics2D g2d = (Graphics2D)g;
      
      //g2d.setColor(java.awt.Color.BLACK);

      selectionSettings.apply(g2d);

      
      if(mousePosition.e!=null) {
        g2d.drawLine(mousePosition.e.getX()-1, mousePosition.e.getY(), mousePosition.e.getX()+1, mousePosition.e.getY());
        g2d.drawLine(mousePosition.e.getX(), mousePosition.e.getY()-1, mousePosition.e.getX(), mousePosition.e.getY()+1);

        if(mousePosition.projection!=null) {
          int interline = 2;
          int space = interline + g2d.getFont().getSize();
          //g2d.drawString(projection.toString(), e.getX(), e.getY());
          g2d.drawString("x=" + mousePosition.projection.x, mousePosition.e.getX(), mousePosition.e.getY());
          g2d.drawString("y=" + mousePosition.projection.y, mousePosition.e.getX(), mousePosition.e.getY() + space);
          //g2d.drawString("z=" + mousePosition.projection.z, mousePosition.e.getX(), mousePosition.e.getY() + space * 2);

        }
      }
    }
    
  }
  
  class MousePosition{
    MouseEvent e;
    Coord3d projection;
  }

  
  // MODEL AND RENDERER FOR RECTANGULAR SELECTION IN 2D
  
  class MouseDragRenderer extends AbstractAWTRenderer2d{
    
    @Override
    public void paint(Graphics g, int canvasWidth, int canvasHeight) {
      if(mouseSelection==null) {
        return;
      }
      // Do not render selection if start or stop point is missing
      if(!mouseSelection.complete()) {
        return;
      }
      // Do not render selection if a diagonal was drawn
      if(!mouseSelection.growing()) {
        return;
      }

      
      Graphics2D g2d = (Graphics2D)g;
      
      // COnfigure G2D with selection drawing settings
      selectionSettings.apply(g2d);

      // Draws rectangular selection      
      int x = (int)mouseSelection.start2D.x;
      int y = (int)mouseSelection.start2D.y;
      int w = (int)(mouseSelection.stop2D.x-x);
      int h = (int)(mouseSelection.stop2D.y-y);
      
      g2d.drawRect(x, y, w, h);

      // Draw start coordinates on the top left corner
      if(mouseSelection.start3D!=null) {
        
        
        String d1 = "x=" + mouseSelection.start3D.x;
        String d2 = "y=" + mouseSelection.start3D.y;
        
        drawCoord(g2d, mouseSelection.start2D, selectionSettings.interline, d1, d2, true);
      }

      // Draw stop coordinates on the bottom right corner
      if(mouseSelection.stop3D!=null) {
        
        String d1 = "x=" + mouseSelection.stop3D.x;
        String d2 = "y=" + mouseSelection.stop3D.y;
        
        drawCoord(g2d, mouseSelection.stop2D, selectionSettings.interline, d1, d2, false);
      }
    }

    private void drawCoord(Graphics2D g2d, Coord2d screenPosition, int interline, String d1, String d2, boolean leftAlign) {
      int d1W = AWTGraphicsUtils.stringWidth(g2d, d1);
      int d2W = AWTGraphicsUtils.stringWidth(g2d, d2);

      int offsetX = 0;
      
      if(leftAlign)
        offsetX = Math.max(d1W, d2W);
      
      int space = interline + g2d.getFont().getSize();


      g2d.drawString(d1, screenPosition.x-offsetX, screenPosition.y);
      g2d.drawString(d2, screenPosition.x-offsetX, screenPosition.y + space);
    }
    
  }
  
  class MouseSelection{
    Coord3d start3D;
    Coord3d stop3D;
    Coord2d start2D;
    Coord2d stop2D;
    
    /** return true if start 2D is smaller than stop 2D */
    boolean growing() {
      if(complete())
        return start2D.x<stop2D.x && start2D.y<=stop2D.y;
      else
        return true;
    }
    
    boolean complete() {
      return start2D!=null && stop2D!=null;// && start3D!=null && stop3D!=null;
    }
  }
  
  class MouseSelectionSettings{
    Color color = Color.GRAY.clone();
    float fontSizeFactor = 1f;
    
    float[] dash1 = { 2f, 0f, 2f };
    
    BasicStroke stroke = new BasicStroke(1, 
        BasicStroke.CAP_BUTT, 
        BasicStroke.JOIN_ROUND, 
        1.0f, 
        dash1,
        2f);
    
    int interline = 2;
    
    public void apply(Graphics2D g2d) {
      // COnfigure G2D with selection drawing settings
      g2d.setColor(AWTColor.toAWT(color));

      String name = g2d.getFont().getFontName();
      int size = g2d.getFont().getSize();
      Font f = new Font(name, Font.PLAIN, (int)(size*fontSizeFactor));
      g2d.setFont(f);
      g2d.setStroke(stroke);

    }


  }
  
}
