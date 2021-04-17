package org.jzy3d.plot3d.rendering.canvas;

import java.awt.AWTEvent;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.monitor.IMonitorable;
import org.jzy3d.monitor.Measure.CanvasPerfMeasure;
import org.jzy3d.monitor.Monitor;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import jgl.GL;
import jgl.GLCanvas;
import jgl.GLUT;
import jgl.context.gl_pointer;

public class EmulGLCanvas extends GLCanvas implements IScreenCanvas, IMonitorable {
  Logger log = Logger.getLogger(EmulGLCanvas.class);

  private static final long serialVersionUID = 980088854683562436L;

  /**
   * if true if false : call full component.resize to force resize + view.render + glFlush + swap
   * image
   */
  public static final boolean TO_BE_CHOOSEN_REPAINT_WITH_FLUSH = false;

  /** set to TRUE to overlay performance info on top left corner */
  protected boolean profileDisplayMethod = false;
  /** set to TRUE to show in console events of the component (to debug GLUT) */
  protected boolean debugEvents = false;

  protected TicToc profileDisplayTimer = new TicToc();
  protected Font profileDisplayFont = new Font("Arial", Font.PLAIN, 12);
  protected int profileDisplayCount = 0;

  protected Monitor monitor;


  protected View view;
  protected EmulGLPainter painter;
  protected IAnimator animator;

  protected AtomicBoolean isRenderingFlag = new AtomicBoolean(false);

  public EmulGLCanvas(IChartFactory factory, Scene scene, Quality quality) {
    super();
    view = scene.newView(this, quality);
    painter = (EmulGLPainter) view.getPainter();
    painter.setCanvas(this);

    init(getWidth(), getHeight());

    animator = factory.getPainterFactory().newAnimator(this);

    if (quality.isPreserveViewportSize()) {
      myGL.setAutoAdaptToHiDPI(false);
    } else {
      myGL.setAutoAdaptToHiDPI(true);
    }
    // FROM NATIVE
    // renderer = factory.newRenderer(view, traceGL, debugGL);
    // addGLEventListener(renderer);
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
      System.out.println("EmulGLCanvas.processEvent:" + e);
    }
    super.processEvent(e);
  }

  protected boolean shouldPrintEvent(AWTEvent e) {
    return !(e.getID() == MouseEvent.MOUSE_MOVED);
  }

  // ******************* INIT ******************* //

  /** Equivalent to registering a Renderer3d in native canvas. */
  protected void init(int width, int height) {
    updatePainterWithGL(); // painter can call this canvas GL
    initGLUT(width, height);
    view.init();
  }

  protected void initGLUT(int width, int height) {
    myUT.glutInitWindowSize(width, height);
    myUT.glutInitWindowPosition(getX(), getY());

    myUT.glutCreateWindow(this); // this canvas GLUT register this canvas
    myUT.glutDisplayFunc("doDisplay"); // on this canvas GLUT register this display method
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


  // ******************* DISPLAY ******************* //

  /**
   * Triggers an atomic rendering of a frame, measure rendering performance and update the status of
   * rendering (active or not).
   * 
   * Performance measurement can be seen on screen if {@link #setProfileDisplayMethod(boolean)} was
   * set to true OR can be collected by a {@link Monitor} defined by {@link #add(Monitor)}.
   * 
   * Method is synchronized to avoid multiple concurrent calls to doDisplay which might make jGL get
   * crazy with GL state consistency : GL states must be consistent during a complete rendering
   * pass, and should not be modified by a second rendering pass in the middle of the first one.
   * 
   * In addition, the display method has a {@link #isRenderingFlag} so that external components may
   * known that the canvas is working or not. This allows ignoring a rendering query in case the
   * canvas is not ready for working. This is different from making use of <code>synchronized</code>
   * (which lead to a queue of calls to be resolved) in that one may simply not append work to do
   * according to the status of the canvas. This is used by mouse and thread controller which tend
   * to send lot of rendering queries faster than the frame rate.
   */
  public synchronized void doDisplay() {
    //System.out.println("IS RENDERING");
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
        postRenderProfiling(lastRenderingTimeMs);

      }
      if (monitor != null) {
        monitorRenderingTime(monitor, lastRenderingTimeMs);
      }

      profileDisplayCount++;
    }


    isRenderingFlag.set(false);
    //System.out.println("DONE RENDERING");

  }
  
  protected double lastRenderingTimeMs = LAST_RENDER_TIME_UNDEFINED;
  
  public double getLastRenderingTime() {
    return lastRenderingTimeMs;
  }
  
  public static final double LAST_RENDER_TIME_UNDEFINED = -1;

  /*
   * @Override public void paint(Graphics g) { synchronized(this) {
   * System.out.println("IS RENDERING"); isRenderingFlag.set(true); //super.paint(g);
   * myGL.glXSwapBuffers(g, this); isRenderingFlag.set(false); System.out.println("DONE RENDERING");
   * } }
   */

  @Override
  public void display() {
    forceRepaint();
  }

  /**
   * Can be used to update image if camera has changed position. (usually called by
   * {@link View#shoot()})
   * 
   * Warning if this is invoked by a thread external to AWT, this may redraw 
   * GL while GL is already used by AWT, which would turn GL into an inconsistent state.
   */
  @Override
  public void forceRepaint() {
    // This makes GLUT invoke the myReshape function

    // SHOULD NOT BE CALLED IF ANIMATOR IS ACTIVE
    if (TO_BE_CHOOSEN_REPAINT_WITH_FLUSH) {
      painter.getGL().glFlush();

      // This triggers copy of newly generated picture to the GLCanvas
      // repaint();
    }
    else {
      processEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
      // equivalent to view.clear(), view.render(), glFlush(), glXSwapBuffers
    }
    // INTRODUCE A UNDESIRED RESIZE EVENT (WE ARE NOT RESHAPING VIEWPORT
    // WAS JUST USED TO FORCE REPAINT
  }


  public AtomicBoolean getIsRenderingFlag() {
    return isRenderingFlag;
  }

  protected void postRenderProfiling(double mili) {
    int x = 05;
    int y = 12;

    postRenderString("FrameID    : " + profileDisplayCount, x, y, Color.BLACK);
    postRenderString("Render in  : " + mili + "ms", x, y * 2, Color.BLACK);
    postRenderString("Drawables  : " + view.getScene().getGraph().getDecomposition().size(), x,
        y * 3, Color.BLACK);
    postRenderString("Canvas Size : " + getWidth() + "x" + getHeight(), x, y * 4, Color.BLACK);
   
    GL gl = painter.getGL();
    postRenderString("Viewport Size : " + gl.getContext().Viewport.Width + "x" + gl.getContext().Viewport.Height, x, y * 5, Color.BLACK);
  }

  /** Draw a 2d text at the given position */
  protected void postRenderString(String message, int x, int y, Color color) {
    painter.getGL().appendTextToDraw(profileDisplayFont, message, x, y, color.r, color.g, color.b);
  }

  // ******************* RESIZE ******************* //

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

  // ******************* MOUSE MOTION ******************* //

  /**
   * Handle mouse events emitted by GLUT. Most probably not registered as mouse already handled by
   * Jzy3D.
   */
  public synchronized void doMotion(int x, int y) {
    doDisplay();
    System.out.println("EmulGLCanvas.doMotion!" + profileDisplayCount);
  }

  // ******************* SCREENSHOTS ******************* //

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
  public String getDebugInfo() {
    return null;
  }

  /* ******************* DEBUG ********************* */

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
