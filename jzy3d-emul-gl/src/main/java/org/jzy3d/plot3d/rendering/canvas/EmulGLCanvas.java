package org.jzy3d.plot3d.rendering.canvas;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.monitor.IMonitorable;
import org.jzy3d.monitor.Measure.CanvasPerfMeasure;
import org.jzy3d.monitor.Monitor;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import jgl.GL;
import jgl.GL.PixelScaleListener;
import jgl.GLCanvas;
import jgl.GLUT;
import jgl.context.gl_pointer;

/**
 * This canvas allows rendering charts with jGL as OpenGL backend which perform in CPU.
 * 
 * The below schema depicts how this canvas does painting :
 * 
 * <img src="doc-files/emulgl-canvas.png"/>
 * 
 * @author Martin Pernollet
 */
public class EmulGLCanvas extends GLCanvas implements IScreenCanvas, IMonitorable {
  Logger log = Logger.getLogger(EmulGLCanvas.class);

  private static final long serialVersionUID = 980088854683562436L;

  /**
   * if true if false : call full component.resize to force resize + view.render + glFlush + swap
   * image
   */
  public static final boolean TO_BE_CHOOSEN_REPAINT_WITH_FLUSH = false;


  // Fields used by the canvas to work
  protected View view;
  protected EmulGLPainter painter;
  protected IAnimator animator;

  protected List<ICanvasListener> canvasListeners = new ArrayList<>();

  protected AtomicBoolean isRenderingFlag = new AtomicBoolean(false);


  // Profiling (display perf on screen)
  /** set to TRUE to show in console events of the component (to debug GLUT) */
  protected boolean debugEvents = false;
  /** set to TRUE to overlay performance info on top left corner */
  protected boolean profileDisplayMethod = false;
  protected TicToc profileDisplayTimer = new TicToc();
  protected Font profileDisplayFont = new Font("Helvetica", Font.PLAIN, 12);
  protected int profileDisplayCount = 0;
  protected List<ProfileInfo> profileInfo = new ArrayList<>();

  // Monitor (export perf to something else, e.g. an XLS file)
  protected Monitor monitor;


  public EmulGLCanvas(IChartFactory factory, Scene scene, Quality quality) {
    super();
    view = scene.newView(this, quality);
    painter = (EmulGLPainter) view.getPainter();
    painter.setCanvas(this);

    init(getWidth(), getHeight());

    animator = factory.getPainterFactory().newAnimator(this);

    if (quality.isHiDPIEnabled()) {
      myGL.setAutoAdaptToHiDPI(true);
    } else {
      myGL.setAutoAdaptToHiDPI(false);
    }

    myGL.addPixelScaleListener(new PixelScaleListener() {
      @Override
      public void pixelScaleChanged(double pixelScaleX, double pixelScaleY) {
        firePixelScaleChanged(pixelScaleX, pixelScaleY);
      }
    });
  }

  @Override
  public void setPixelScale(float[] scale) {
    Logger.getLogger(EmulGLCanvas.class)
        .info("Not implemented. Pixel scale is driven by AWT Canvas itself and jGL adapts to it");
  }

  @Override
  public Coord2d getPixelScale() {
    Graphics2D g2d = (Graphics2D) getGraphics();
    AffineTransform globalTransform = g2d.getTransform();
    return new Coord2d(globalTransform.getScaleX(), globalTransform.getScaleY());
  }

  @Override
  public IAnimator getAnimation() {
    return animator;
  }


  @Override
  public void processEvent(AWTEvent e) {
    if (debugEvents && shouldPrintEvent(e)) {
      System.err.println("EmulGLCanvas.processEvent:" + e);
    }
    super.processEvent(e);
  }

  protected boolean shouldPrintEvent(AWTEvent e) {
    return !(e.getID() == MouseEvent.MOUSE_MOVED);
  }

  /* *********************************************************************** */
  /* ******************************* INIT ********************************** */
  /* *********************************************************************** */

  /** Equivalent to registering a Renderer3d in native canvas. */
  protected void init(int width, int height) {
    updatePainterWithGL(); // painter can call this canvas GL
    initGLUT(width, height);
    initView();
  }

  protected void initView() {
    view.init();
  }

  protected void initGLUT(int width, int height) {
    myUT.glutInitWindowSize(width, height);
    myUT.glutInitWindowPosition(getX(), getY());

    myUT.glutCreateWindow(this); // this canvas GLUT register this canvas
    myUT.glutDisplayFunc("doRender"); // on this canvas GLUT register this display method
    myUT.glutReshapeFunc("doReshape"); // on ComponentEvent.RESIZE TODO: double render car
                                       // GLUT.resize invoque
                                       // reshape + display
    // myUT.glutMotionFunc("doMotion"); // TODO : double render car GLUT.motion invoque display PUIS
    // le listener
    // TODO : RESIZED semble emis par le composant quand on fait un mouse dragg!!


    // myUT.glutMainLoop();

    // CLARIFIER comment le composant se met à jour :
    // en autonome sur paint (quel cycle de vie?)
    // sur demande quand on fait "updateView" dans les mouse / thread controller etc
    // pourquoi est il nécessaire de le faire pendant mouse dragged?
  }

  /* *********************************************************************** */
  /* ***************************** DISPLAY ********************************* */
  /* *********************************************************************** */

  /**
   * This overrides the {@link GLCanvas} hence {@link Canvas} methods to copy the image of the 3D
   * scene as generated while {@link GL#glFlush()}.
   * 
   * It is called when the application needs to paint the canvas, which assume a rendering has
   * already been process by {@link #doRender()} which produce an image that the canvas can use for
   * fast pixel swap.
   * 
   * {@link #doRender()} on its side is triggered when {@link GLUT} thinks it is relevant. This may
   * occur because {@link EmulGLCanvas} triggered a {@link ComponentEvent.COMPONENT_RESIZED} event.
   */
  @Override
  public void paint(Graphics g) {
    if (profileDisplayMethod) {
      // Overrides GL swapping to retrieve the image and print performance info inside
      BufferedImage glImage = myGL.getRenderedImage();
      paintProfileInfo(glImage);
      g.drawImage(glImage, myGL.getStartX(), myGL.getStartY(), myGL.getDesiredWidth(),
          myGL.getDesiredHeight(), this);
    }
    // If not profiling invoke the default swapping method implemented in GLCanvas
    else {
      super.paint(g);
    }
  }


  @Override
  public void display() {
    forceRepaint();
  }

  /**
   * Can be used to update image if camera has changed position. (usually called by
   * {@link View#shoot()})
   * 
   * Warning if this is invoked by a thread external to AWT, this may redraw GL while GL is already
   * used by AWT, which would turn GL into an inconsistent state.
   */
  @Override
  public void forceRepaint() {
    // This makes GLUT invoke the myReshape function

    // SHOULD NOT BE CALLED IF ANIMATOR IS ACTIVE
    if (TO_BE_CHOOSEN_REPAINT_WITH_FLUSH) {
      painter.getGL().glFlush();

      // This triggers copy of newly generated picture to the GLCanvas
      // repaint();
    } else {
      processEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
      // equivalent to view.clear(), view.render(), glFlush(), glXSwapBuffers
    }
    // INTRODUCE A UNDESIRED RESIZE EVENT (WE ARE NOT RESHAPING VIEWPORT
    // WAS JUST USED TO FORCE REPAINT
  }

  /**
   * Triggers an atomic rendering of a frame, measure rendering performance and update the status of
   * rendering (active or not). This method is callback registered in with
   * {@link GLUT#glutDisplayFunc(String)} which will be called when OpenGL need to update display.
   * OpenGL updates as soon as the component that GLUT listen to (which is this {@link EmulGLCanvas}
   * triggers a {@link ComponentEvent.COMPONENT_RESIZED} event.
   * 
   * Performance measurement can be seen on screen if {@link #setProfileDisplayMethod(boolean)} was
   * set to true OR can be collected by a {@link Monitor} defined by {@link #add(Monitor)}.
   * 
   * This method is synchronized to prevent multiple concurrent calls to doDisplay which might make
   * jGL get crazy with GL state consistency : GL states must be consistent during a complete
   * rendering pass, and should not be modified by a second rendering pass in the middle of the
   * first one. Consistency may be on drawing a complete geometry in appropriate order (glBegin,
   * glVertex, glEnd) or in the way OpenGL 1.0 fixed pipeline is cleanly handled.
   * 
   * In addition, the display method has a {@link #isRenderingFlag} so that external components may
   * known that the canvas is currently rendering or not. This allows ignoring a rendering query in
   * case the canvas is not ready for working. This is different from making use of
   * <code>synchronized</code> (which lead to a queue of calls to be resolved) in that one may
   * simply not append work to do according to the status of the canvas.
   */
  public synchronized void doRender() {
    isRenderingFlag.set(true);

    profileDisplayTimer.tic();

    if (view != null) {

      if (profileDisplayMethod) {
        resetCountGLBegin();
      }

      view.clear();
      view.render();

      // Ask opengl to provide an image for display
      myGL.glFlush();

      // Ask the GLCanvas to SWAP current image with
      // the latest built with glFlush
      repaint();


      // checkAlphaChannelOfColorBuffer(painter);

      // -------------------------------
      // PROFILE
      profileDisplayTimer.toc();

      lastRenderingTimeMs = profileDisplayTimer.elapsedMilisecond();

      if (profileDisplayMethod) {
        profile(lastRenderingTimeMs);

      }
      if (monitor != null) {
        monitorRenderingTime(monitor, lastRenderingTimeMs);
      }

      profileDisplayCount++;
    }


    isRenderingFlag.set(false);
    // System.out.println("DONE RENDERING");

  }

  protected double lastRenderingTimeMs = LAST_RENDER_TIME_UNDEFINED;

  @Override
  public double getLastRenderingTimeMs() {
    return lastRenderingTimeMs;
  }

  /*
   * @Override public void paint(Graphics g) { synchronized(this) {
   * System.out.println("IS RENDERING"); isRenderingFlag.set(true); //super.paint(g);
   * myGL.glXSwapBuffers(g, this); isRenderingFlag.set(false); System.out.println("DONE RENDERING");
   * } }
   */

  public AtomicBoolean getIsRenderingFlag() {
    return isRenderingFlag;
  }



  /* *********************************************************************** */
  /* ****************************** RESIZE ********************************* */
  /* *********************************************************************** */

  /**
   * Handle resize events emitted by GLUT.
   * 
   * {@link GLUT#processComponentEvent(ComponentEvent)} is calling reshape handler (this method)
   * THEN the display handler ({@link #doDisplay()), so this method will only inform {@link GLUT}
   * and {@link View} that window size changed.
   */
  public synchronized void doReshape(int w, int h) {
    // System.out.println("doReshape " + w);
    myUT.glutInitWindowSize(w, h);

    if (view != null) {
      view.markDimensionDirty();
      // then doDisplay will be called by glutReshapeFunc
    }

  }

  /* *************************** MOUSE MOTION ***************************** */

  /**
   * Handle mouse events emitted by GLUT. Most probably not registered as mouse already handled by
   * Jzy3D.
   */
  public synchronized void doMotion(int x, int y) {
    doRender();
    System.out.println("EmulGLCanvas.doMotion!" + profileDisplayCount);
  }

  /* *************************** SCREENSHOTS ***************************** */

  @Override
  public BufferedImage screenshot() {
    EmulGLPainter painter = (EmulGLPainter) getView().getPainter();
    return (BufferedImage) painter.getGL().getRenderedImage();
  }

  @Override
  public void screenshot(File file) throws IOException {
    if (!file.getParentFile().exists())
      file.mkdirs();
    ImageIO.write(screenshot(), "png", file);
  }

  /* *************************************************** */


  /**
   * Register {@link EmulGLPainter}.
   */
  protected void updatePainterWithGL() {
    if (view != null && view.getPainter() != null && getGL() != null) {
      EmulGLPainter painter = (EmulGLPainter) view.getPainter();
      painter.setGL(getGL());
    }
  }

  /* ******************************************************* */

  @Override
  public View getView() {
    return view;
  }

  @Override
  public int getRendererWidth() {
    return this.getWidth();
  }

  @Override
  public int getRendererHeight() {
    return this.getHeight();
  }


  @Override
  public void dispose() {

  }

  // ******************* LISTENERS ******************* //

  @Override
  public void addMouseController(Object o) {
    addMouseListener((java.awt.event.MouseListener) o);
    if (o instanceof MouseWheelListener)
      addMouseWheelListener((MouseWheelListener) o);
    if (o instanceof MouseMotionListener)
      addMouseMotionListener((MouseMotionListener) o);
  }

  @Override
  public void removeMouseController(Object o) {
    removeMouseListener((java.awt.event.MouseListener) o);
    if (o instanceof MouseWheelListener)
      removeMouseWheelListener((MouseWheelListener) o);
    if (o instanceof MouseMotionListener)
      removeMouseMotionListener((MouseMotionListener) o);
  }

  @Override
  public void addKeyController(Object o) {
    addKeyListener((java.awt.event.KeyListener) o);
  }

  @Override
  public void removeKeyController(Object o) {
    removeKeyListener((java.awt.event.KeyListener) o);
  }

  @Override
  public void addCanvasListener(ICanvasListener listener) {
    canvasListeners.add(listener);
  }

  @Override
  public void removeCanvasListener(ICanvasListener listener) {
    canvasListeners.remove(listener);
  }

  @Override
  public List<ICanvasListener> getCanvasListeners() {
    return canvasListeners;
  }

  protected void firePixelScaleChanged(double pixelScaleX, double pixelScaleY) {
    for (ICanvasListener listener : canvasListeners) {
      listener.pixelScaleChanged(pixelScaleX, pixelScaleY);
    }
  }

  /* *********************************************************************** */
  /* ************************** PROFILE AND DEBUG ************************** */
  /* *********************************************************************** */

  @Override
  public String getDebugInfo() {
    return null;
  }


  /**
   * Render profile on top of an image (probably the image of the GL scene) previously collected
   * while {@link EmulGLCanvas#doRender().
   * 
   * Painting profile info is synchronized on the profile info list to ensure it is not modified
   * while drawing (which occurs if synchronization is disabled). Despite we did not observed any
   * lag due to such rendering, it is important to keep in mind that displaying profile information
   * requires a synchronized access to this info list which is on the other side synchronized to
   * protect exporting rendering info of the last call to {@link #doRender()}.
   * 
   */
  protected void paintProfileInfo(BufferedImage glImage) {
    Graphics2D g2d = glImage.createGraphics();

    AWTGraphicsUtils.configureRenderingHints(g2d);
    
    int minX = Integer.MAX_VALUE;
    int maxX = 0;
    int minY = Integer.MAX_VALUE;
    int maxY = 0;

    synchronized (profileInfo) {
      // Render a rectangle around profile text
      for (ProfileInfo profile : profileInfo) {
        int stringWidth = AWTGraphicsUtils.stringWidth(g2d, profile.message);
        
        if(minX > profile.x)
          minX = profile.x;
        if(maxX < profile.x + stringWidth)
          maxX = profile.x + stringWidth;        
        if(minY > profile.y)
          minY = profile.y;
        if(maxY < profile.y)
          maxY = profile.y;        
      }
      
      int x = minX;
      int y = minY - profileDisplayFont.getSize();
      int width = maxX-minX;
      int height = maxY-minY + profileDisplayFont.getSize();
      
      g2d.setColor(java.awt.Color.WHITE);
      g2d.fillRect(x, y, width, height);
      g2d.setColor(java.awt.Color.RED);
      g2d.drawRect(x, y, width, height);
      
      // Render all text info
      for (ProfileInfo profile : profileInfo) {
        java.awt.Color awtColor = AWTColor.toAWT(profile.color);
        g2d.setColor(awtColor);

        AWTGraphicsUtils.drawString(g2d, profileDisplayFont, false, profile.message, profile.x,
            profile.y);
      }

    }
  }


  protected void profile(double mili) {
    int x = 10;
    int y = 12;
    
    synchronized (profileInfo) {

      profileClear();

      int line = 1;
      Color c = Color.BLACK;

      // Rendering info
      profile("FrameID    : " + profileDisplayCount, x, y * line++, c);
      profile("Render in  : " + mili + "ms", x, y * line++, c);

      // Drawables size
      profile("Drawables  : " + view.getScene().getGraph().getDecomposition().size(), x, y * line++,
          c);

      // Scatters sizes
      for (Drawable d : view.getScene().getGraph().getAll()) {
        if (d instanceof Scatter) {
          Scatter s = (Scatter) d;
          profile("Scatter    : " + s.coordinates.length + " points", x, y * (line++), c);
        }
      }

      // Canvas size
      profile("Canvas Size : " + getWidth() + "x" + getHeight(), x, y * line++, c);

      // Viewport size
      GL gl = painter.getGL();
      int viewportWidth = gl.getContext().Viewport.Width;
      int viewportHeight = gl.getContext().Viewport.Height;

      profile("Viewport Size : " + viewportWidth + "x" + viewportHeight, x, y * (line++), c);

    }

    
  }

  /** Draw a 2d text at the given position */
  protected void profile(String message, int x, int y, Color c) {
    profileInfo.add(new ProfileInfo(message, x, y, c));
  }

  protected void profileClear() {
    profileInfo.clear();
  }

  class ProfileInfo {
    String message;
    int x;
    int y;
    Color color;

    public ProfileInfo(String message, int x, int y, Color color) {
      super();
      this.message = message;
      this.x = x;
      this.y = y;
      this.color = color;
    }
  }

  public boolean isProfileDisplayMethod() {
    return profileDisplayMethod;
  }

  public void setProfileDisplayMethod(boolean profileDisplayMethod) {
    this.profileDisplayMethod = profileDisplayMethod;
  }

  protected void checkAlphaChannelOfColorBuffer(EmulGLPainter painter) {
    int[] colorBuffer = painter.getGL().getContext().ColorBuffer.Buffer;

    for (int i = 0; i < colorBuffer.length; i++) {
      Color c = jGLUtil.glIntToColor(colorBuffer[i]);

      if (c.a == 0) {
        // check gl_render_pixel.debug
        // il y a des pixel qui ont un alpha 127
        System.err.println("alpha " + c.a + " at " + i);
      }
    }

    System.out.println("AlphaEnable:" + painter.getGL().getContext().ColorBuffer.AlphaEnable);
    System.out.println("AlphaFunc:" + painter.getGL().getContext().ColorBuffer.AlphaFunc);
  }

  protected void printCountGLBegin() {
    gl_pointer pointer = painter.getGL().getPointer();
    System.out.println("#gl_begin() : " + pointer.geometry.countBegin);
  }

  protected void resetCountGLBegin() {
    painter.getGL().getPointer().geometry.countBegin = 0;
  }

  /* ******************* MONITOR ********************* */

  @Override
  public String getFullname() {
    return this.toString();
  }

  @Override
  public String getLabel() {
    return this.getClass().getSimpleName();
  }

  @Override
  public void add(Monitor monitor) {
    this.monitor = monitor;
  }

  protected void monitorRenderingTime(Monitor monitor, double mili) {
    monitor.add(this,
        new CanvasPerfMeasure(getWidth(), getHeight(), getWidth() * getHeight(), mili));
  }


}
