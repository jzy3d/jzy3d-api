/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.plot3d.rendering.canvas;

import org.jzy3d.maths.TicToc;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.PanamaGLPainter;
import org.jzy3d.plot3d.rendering.view.View;
import panamagl.GLEventListener;
import panamagl.canvas.GLCanvas;
import panamagl.opengl.GL;

public class Renderer3D implements GLEventListener{
  protected View view;
  protected int width = 0;
  protected int height = 0;

  protected boolean doScreenshotAtNextDisplay = false;
  //protected TextureData image = null;

  protected boolean traceGL = false;
  protected boolean debugGL = false;

  protected TicToc profileDisplayTimer = new TicToc();
  protected double lastRenderingTimeMs;

  
  /** Initialize a Renderer attached to the given View. */
  public Renderer3D(View view) {
    this(view, false, false);
  }

  public Renderer3D() {
    this(null, false, false);
  }

  /**
   * Initialize a Renderer attached to the given View, and activate GL trace and errors to console.
   */
  public Renderer3D(View view, boolean traceGL, boolean debugGL) {
    this.view = view;
    this.traceGL = traceGL;
    this.debugGL = debugGL;
  }

  /**
   * Called when the {@link GLCanvas} is rendered for the first time. When one calls
   * Scene.init() function, this function is called and makes the OpenGL buffers initialization.
   * 
   * Note: in this implementation, GL Exceptions are not triggered. To do so, make te following call
   * at the beginning of the init() body: <code>
   * canvas.setGL( new DebugGL(canvas.getGL()) );
   * </code>
   */
  @Override
  public void init(GL canvas) {
    //if (canvas != null && canvas.getGL() != null && view != null) {

      /*GLContext context = canvas.getGL().getContext();
      
      if (debugGL)
        context.setGL(GLPipelineFactory.create("com.jogamp.opengl.Debug", null, canvas.getGL(), null));
      if (traceGL)
        context.setGL(GLPipelineFactory.create("com.jogamp.opengl.Trace", null, canvas.getGL(), new Object[] {System.err}));

      updatePainterWithGL(canvas);*/

      view.init();
    //}
  }

  /**
   * Called when the {@link GLCanvas} requires a rendering. All call to rendering methods
   * should appear here.
   */
  @Override
  public void display(GL canvas) {
    profileDisplayTimer.tic();

    if (view != null) {
      if(canvas!=null) {
  
        //updatePainterWithGL(canvas);
  
        if (view != null) {
          view.clear();
          view.render();
  
          //exportImageIfRequired(canvas.getGL());

          //renderScreenshotIfRequired(canvas.getGL());
        }
      }
    }

    profileDisplayTimer.toc();
    lastRenderingTimeMs = profileDisplayTimer.elapsedMilisecond();
  }
  

  /** Called when the {@link GLCanvas} is resized. */
  @Override
  public void reshape(GL canvas, int x, int y, int width, int height) {
    this.width = width;
    this.height = height;

    if (view != null) {
      //view.dimensionDirty = true;

      if (canvas != null) {

        //updatePainterWithGL(canvas);

        view.clear();
        view.render();
      }
    }
  }

  /**
   * This method allows configuring the {@link IPainter} with the current {@link GL} context
   * provided by the {@link GLCanvas}. This may be usefull to override in case of a mocking GL
   * (to avoid having the mock replaced by a real GL Context).
   * 
   * @param canvas
   */
  protected void updatePainterWithGL(GL canvas) {
    PanamaGLPainter painter = ((PanamaGLPainter) view.getPainter());
    painter.setGL(canvas);
  }

  /********************* SCREENSHOTS ***********************/

  public void nextDisplayUpdateScreenshot() {
    doScreenshotAtNextDisplay = true;
  }

  /*public TextureData getLastScreenshot() {
    return image;
  }

  protected void renderScreenshotIfRequired(GL gl) {
    if (doScreenshotAtNextDisplay) {
      GLReadBufferUtil screenshot = new GLReadBufferUtil(false, false);
      screenshot.readPixels(gl, true);
      image = screenshot.getTextureData();
      doScreenshotAtNextDisplay = false;
    }
  }
  
  protected void exportImageIfRequired(GL gl) {
    
  }*/




  /** Return the width that was given after the last resize event. */
  public int getWidth() {
    return width;
  }

  /** Return the height that was given after the last resize event. */
  public int getHeight() {
    return height;
  }

  public double getLastRenderingTimeMs() {
    return lastRenderingTimeMs;
  }

  @Override
  public void dispose(GL gl) {
    // TODO Auto-generated method stub
    
  }
}
