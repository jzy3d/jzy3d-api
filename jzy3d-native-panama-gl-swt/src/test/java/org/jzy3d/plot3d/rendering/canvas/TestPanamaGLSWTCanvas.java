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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jzy3d.chart.factories.PanamaGLSWTChartFactory;
import org.jzy3d.chart.factories.PanamaGLSWTPainterFactory;
import panamagl.canvas.swt.GLCanvasSWT;
import panamagl.factory.PanamaGLFactory;
import panamagl.offscreen.AOffscreenRenderer;
import panamagl.offscreen.FBO;
import panamagl.offscreen.FBOReader_SWT;
import panamagl.opengl.GL;
import panamagl.opengl.GLContext;

/**
 * Tests for {@link PanamaGLSWTCanvas}.
 *
 * Mirrors {@code TestPanamaGLCanvas} (Swing) and {@code TestPanamaGLJavaFXCanvas} but locks in
 * SWT-specific re-entrancy guarantees: SWT's {@link panamagl.offscreen.ThreadRedirect_SWT} runs
 * the GL init synchronously when the caller is already on the display thread, so any canvas
 * accessor reachable from {@code Renderer3D.init -> View.init} must work <i>during</i>
 * {@link PanamaGLCanvasSupport}'s constructor — i.e. before the canvas's {@code support} field has
 * been assigned, and with the SWT widget still at its 0x0 pre-layout size.
 *
 * VM ARGS (macOS): -XstartOnFirstThread --enable-native-access=ALL-UNNAMED
 */
public class TestPanamaGLSWTCanvas {

  Display display;
  Shell shell;

  @Before
  public void setup() {
    display = Display.getCurrent();
    if (display == null) {
      display = new Display();
    }
    shell = new Shell(display);
    shell.setLayout(new FillLayout());
  }

  @After
  public void teardown() {
    // SWT widget tear-down can re-fire offscreen.onDestroy on the underlying GLCanvasSWT.
    // With a mocked PanamaGLFactory, the offscreen renderer's destroyContext() may NPE on
    // mock state — that's a teardown-only artifact, never reproduced in production where the
    // factory is real. Swallow it so it does not mask test failures.
    try {
      if (shell != null && !shell.isDisposed()) {
        shell.dispose();
      }
    } catch (Throwable ignored) {
      // teardown-only
    }
    if (display != null && !display.isDisposed()) {
      display.dispose();
    }
  }

  /** @return a PanamaGLFactory whose offscreen/GL/context/FBO returns are mocked. */
  private static PanamaGLFactory newMockPanamaGLFactory() {
    PanamaGLFactory f = mock(PanamaGLFactory.class);
    when(f.newOffscreenRenderer(any())).thenReturn(new AOffscreenRenderer(f, new FBOReader_SWT()));
    when(f.newGL()).thenReturn(mock(GL.class));
    when(f.newGLContext()).thenReturn(mock(GLContext.class));
    when(f.newFBO(anyInt(), anyInt())).thenReturn(mock(FBO.class));
    return f;
  }

  /**
   * Counts {@link #display()} invocations without going through Mockito.spy(): on SWT, spying
   * a constructed widget breaks the dispose listener (the lambda captures the original
   * {@code GLCanvasSWT.this} but {@code setGLEventListener} writes to the spy instance, so
   * widget tear-down NPEs on a null {@code listener} field).
   *
   * <p>Also exposes {@link #setRendering(boolean)} so tests can emulate the post-construction
   * state where the asynchronous Paint event hasn't yet cleared the {@code rendering} flag.
   */
  private static class CountingGLCanvasSWT extends GLCanvasSWT {
    int displayCalls = 0;

    CountingGLCanvasSWT(Composite parent, int style, PanamaGLFactory factory) {
      super(parent, style, factory);
    }

    @Override
    public void display() {
      displayCalls++;
      super.display();
    }

    @Override
    public void setRendering(boolean value) {
      super.setRendering(value);
    }
  }

  /** Drain pending SWT events so {@code Display.asyncExec} runnables get a chance to run. */
  private void drainSwtEvents() {
    while (display.readAndDispatch()) {
      // keep dispatching
    }
  }

  /**
   * Regression guard #1 — NPE chain
   * <pre>
   *   PanamaGLSWTCanvas.&lt;init&gt;
   *     -&gt; PanamaGLCanvasSupport.&lt;init&gt;
   *       -&gt; glCanvas.setGLEventListener(renderer)
   *         -&gt; AOffscreenRenderer.onInit
   *           -&gt; ThreadRedirect_SWT.run            (synchronous on display thread)
   *             -&gt; Renderer3D.init -&gt; View.init
   *               -&gt; PanamaGLPainter.configureGL
   *                 -&gt; canvas.getGLCanvas()       // BUG #1 if support is null
   *               -&gt; View.initResources -&gt; updateBounds -&gt; shoot
   *                 -&gt; canvas.forceRepaint()       // BUG #2 if support is null
   * </pre>
   *
   * Both accessors must therefore go through the {@code glCanvas} field (set before the support
   * is built), not through the not-yet-assigned {@code support}. The constructor must complete
   * without throwing.
   */
  @Test
  public void constructor_doesNotNPEEvenThoughInitFiresSynchronouslyOnSWTDisplayThread() {
    PanamaGLFactory panamaGL = newMockPanamaGLFactory();

    PanamaGLSWTChartFactory chartFactory = new PanamaGLSWTChartFactory(shell);
    ((PanamaGLSWTPainterFactory) chartFactory.getPainterFactory()).setPanamaGLFactory(panamaGL);

    PanamaGLSWTCanvas canvas = new PanamaGLSWTCanvas(chartFactory,
        chartFactory.newScene(false), Quality.Advanced(), shell, panamaGL);

    // If we reached this point without NPE, the bug is fixed. The canvas must expose a non-null
    // GLCanvas (used by PanamaGLPainter.configureGL during init) and a non-null view (used by
    // Chart.<init> right after newCanvas returns).
    assertNotNull(canvas.getGLCanvas());
    assertNotNull(canvas.getView());
  }

  /**
   * Regression guard #2 — FBO must not be sized to zero on construction.
   *
   * <p>A freshly-created SWT widget sits at 0x0 until its parent's layout pass runs. The
   * synchronous init chain reaches {@code AOffscreenRenderer.renderGLToImage(canvas, listener,
   * canvas.getWidth(), canvas.getHeight())}, which on macOS aborts with
   * {@code GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT (36054)} when either dimension is zero.
   *
   * <p>{@link PanamaGLSWTCanvas} pre-sizes its {@link GLCanvasSWT} to 1x1 before wiring the
   * GL listener so the construction-time render gets a strictly-positive FBO. This test locks
   * that precondition in place.
   */
  @Test
  public void glCanvasIsPreSizedToAtLeastOnePixelInEachDimension() {
    PanamaGLFactory panamaGL = newMockPanamaGLFactory();

    PanamaGLSWTChartFactory chartFactory = new PanamaGLSWTChartFactory(shell);
    ((PanamaGLSWTPainterFactory) chartFactory.getPainterFactory()).setPanamaGLFactory(panamaGL);

    PanamaGLSWTCanvas canvas = new PanamaGLSWTCanvas(chartFactory,
        chartFactory.newScene(false), Quality.Advanced(), shell, panamaGL);

    int w = canvas.getGLCanvas().getWidth();
    int h = canvas.getGLCanvas().getHeight();

    assertTrue("Inner GLCanvasSWT width must be >= 1 right after construction to prevent "
        + "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT during the synchronous SWT init chain; got " + w,
        w >= 1);
    assertTrue("Inner GLCanvasSWT height must be >= 1 right after construction to prevent "
        + "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT during the synchronous SWT init chain; got " + h,
        h >= 1);
  }

  /**
   * Mirrors {@code TestPanamaGLCanvas.forceRepaint} (Swing): {@link PanamaGLSWTCanvas#forceRepaint()}
   * must trigger a {@code display()} on the underlying {@link GLCanvasSWT}.
   */
  @Test
  public void forceRepaint_callsGLCanvasDisplay() {
    PanamaGLFactory panamaGL = newMockPanamaGLFactory();

    PanamaGLSWTChartFactory chartFactory = new PanamaGLSWTChartFactory(shell);
    ((PanamaGLSWTPainterFactory) chartFactory.getPainterFactory()).setPanamaGLFactory(panamaGL);

    CountingGLCanvasSWT glCanvas = new CountingGLCanvasSWT(shell, SWT.NONE, panamaGL);

    PanamaGLSWTCanvas c = new PanamaGLSWTCanvas(chartFactory, chartFactory.newScene(false),
        Quality.Advanced(), shell, glCanvas);

    // Construction may already have triggered display() through the View.init chain (e.g.
    // View.initResources -> updateBounds -> shoot -> canvas.forceRepaint()). Snapshot the
    // counter before the explicit call so we measure only the new invocation.
    int before = glCanvas.displayCalls;

    c.forceRepaint();

    assertEquals(before + 1, glCanvas.displayCalls);
  }

  /**
   * Regression guard #3 — the chart must render at the real layout size, not just stay stuck
   * at the 1x1 construction-time render.
   *
   * <p>{@link GLCanvasSWT}'s built-in {@code ResizeHandler} bails out early when
   * {@code isRendering()} is {@code true}. The construction-time 1x1 render leaves
   * {@code rendering=true} until its asynchronous SWT.Paint event fires, which is typically
   * <i>after</i> the {@code shell.open()} layout pass has already fired (and dropped) its
   * SWT.Resize. Without intervention the chart stays blank until the user manually resizes.
   *
   * <p>{@link PanamaGLSWTCanvas} compensates by installing its own one-shot resize listener
   * that schedules a deferred {@code display()} on the first non-trivial resize. This test
   * emulates the dropped-resize scenario by setting {@code rendering=true} (mirroring the
   * post-construction state), firing a layout-style resize, draining the SWT event queue, and
   * asserting that {@code display()} ran.
   */
  @Test
  public void firstRealLayoutTriggersDisplayEvenIfBuiltInResizeHandlerWouldDropIt() {
    PanamaGLFactory panamaGL = newMockPanamaGLFactory();

    PanamaGLSWTChartFactory chartFactory = new PanamaGLSWTChartFactory(shell);
    ((PanamaGLSWTPainterFactory) chartFactory.getPainterFactory()).setPanamaGLFactory(panamaGL);

    CountingGLCanvasSWT glCanvas = new CountingGLCanvasSWT(shell, SWT.NONE, panamaGL);

    PanamaGLSWTCanvas c = new PanamaGLSWTCanvas(chartFactory, chartFactory.newScene(false),
        Quality.Advanced(), shell, glCanvas);

    // Snapshot after construction: any displays already counted are not what we measure.
    int displaysBeforeLayout = glCanvas.displayCalls;

    // Simulate the broken state: the construction-time render's redraw() is still pending,
    // so rendering stays true and GLCanvasSWT.ResizeHandler would bail out early.
    glCanvas.setRendering(true);

    // Simulate the parent layout pass that resizes the canvas to a real, non-trivial size.
    glCanvas.setSize(400, 300);

    // The deferred display() is scheduled via Display.asyncExec — drain the SWT queue so it runs.
    drainSwtEvents();

    assertTrue(
        "Expected our recovery listener to schedule at least one display() after the first "
            + "real layout (was " + displaysBeforeLayout + ", now " + glCanvas.displayCalls + ")",
        glCanvas.displayCalls > displaysBeforeLayout);
  }

  /**
   * Regression guard #4 — {@code forceRepaint()} called from a non-SWT thread must not throw
   * {@code SWTException: Invalid thread access}.
   *
   * <p>This reproduces the failure observed when a {@code CameraThreadController} (used by
   * double-click auto-rotate on {@link org.jzy3d.chart.controllers.mouse.camera.SWTCameraMouseController})
   * fires {@code view.shoot() -> canvas.forceRepaint()} from its own background thread:
   * {@code AOffscreenRenderer.onDisplay} calls {@code drawable.getWidth()} on the caller thread
   * <i>before</i> handing off to {@code ThreadRedirect_SWT}, and {@code GLCanvasSWT.getWidth()}
   * walks into {@code Control.getBounds()} which {@code checkWidget()}s.
   *
   * <p>{@link PanamaGLSWTCanvas#forceRepaint()} must dispatch to the SWT display thread before
   * touching {@code glCanvas}.
   */
  @Test
  public void forceRepaint_fromNonSwtThread_doesNotThrowInvalidThreadAccess() throws Exception {
    PanamaGLFactory panamaGL = newMockPanamaGLFactory();

    PanamaGLSWTChartFactory chartFactory = new PanamaGLSWTChartFactory(shell);
    ((PanamaGLSWTPainterFactory) chartFactory.getPainterFactory()).setPanamaGLFactory(panamaGL);

    CountingGLCanvasSWT glCanvas = new CountingGLCanvasSWT(shell, SWT.NONE, panamaGL);

    PanamaGLSWTCanvas c = new PanamaGLSWTCanvas(chartFactory, chartFactory.newScene(false),
        Quality.Advanced(), shell, glCanvas);

    int displaysBefore = glCanvas.displayCalls;

    final Throwable[] err = new Throwable[1];
    Thread t = new Thread(() -> {
      try {
        c.forceRepaint();
      } catch (Throwable th) {
        err[0] = th;
      }
    }, "non-swt-thread");
    t.start();
    t.join(2000);

    // Drain SWT events so the asyncExec scheduled by forceRepaint actually runs.
    drainSwtEvents();

    if (err[0] != null) {
      throw new AssertionError(
          "forceRepaint from a non-SWT thread should not throw, but got: " + err[0], err[0]);
    }
    assertTrue(
        "Expected forceRepaint to schedule a display() on the SWT thread (was "
            + displaysBefore + ", now " + glCanvas.displayCalls + ")",
        glCanvas.displayCalls > displaysBefore);
  }

  /**
   * The accessor exposed via {@link IPanamaGLCanvas#getGLCanvas()} must be the same instance
   * as the underlying SWT canvas, so callers like {@code PanamaGLPainter.configureGL} and
   * {@code PanamaGLCanvasSupport.getGLCanvas()} agree on identity.
   */
  @Test
  public void getGLCanvas_returnsTheSameInstancePassedAtConstruction() {
    PanamaGLFactory panamaGL = newMockPanamaGLFactory();

    PanamaGLSWTChartFactory chartFactory = new PanamaGLSWTChartFactory(shell);
    ((PanamaGLSWTPainterFactory) chartFactory.getPainterFactory()).setPanamaGLFactory(panamaGL);

    GLCanvasSWT glCanvas = new GLCanvasSWT(shell, SWT.NONE, panamaGL);

    PanamaGLSWTCanvas c = new PanamaGLSWTCanvas(chartFactory, chartFactory.newScene(false),
        Quality.Advanced(), shell, glCanvas);

    assertSame(glCanvas, c.getGLCanvas());
  }

  /**
   * Regression guard: {@link PanamaGLSWTCanvas#getRendererWidth} and
   * {@link PanamaGLSWTCanvas#getRendererHeight} must report <b>physical</b> pixels (matching the
   * FBO size) so {@code View.renderScene} configures {@code glViewport} at the FBO's resolution.
   * Returning logical pixels here on a Retina display caused only the bottom-left quarter of the
   * scene to be rendered.
   */
  @Test
  public void getRendererSize_returnsPhysicalPixelsFromGLCanvas() {
    PanamaGLFactory panamaGL = newMockPanamaGLFactory();

    PanamaGLSWTChartFactory chartFactory = new PanamaGLSWTChartFactory(shell);
    ((PanamaGLSWTPainterFactory) chartFactory.getPainterFactory()).setPanamaGLFactory(panamaGL);

    // Subclass to override getPhysicalWidth/Height without spying (which breaks the dispose
    // listener — see CountingGLCanvasSWT for the rationale).
    GLCanvasSWT glCanvas = new GLCanvasSWT(shell, SWT.NONE, panamaGL) {
      @Override public int getPhysicalWidth() { return 400; }
      @Override public int getPhysicalHeight() { return 300; }
    };

    PanamaGLSWTCanvas c = new PanamaGLSWTCanvas(chartFactory, chartFactory.newScene(false),
        Quality.Advanced(), shell, glCanvas);

    assertEquals(400, c.getRendererWidth());
    assertEquals(300, c.getRendererHeight());
  }
}
