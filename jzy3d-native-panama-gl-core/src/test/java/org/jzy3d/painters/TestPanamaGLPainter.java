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
package org.jzy3d.painters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import org.junit.Test;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;

/**
 * Covers all branches of {@link PanamaGLPainter#getTextLengthInPixels(Font, String)}:
 * <ul>
 * <li>null font short-circuits to 0</li>
 * <li>empty string measures 0</li>
 * <li>canvas is an AWT {@link Component} with a live {@link Graphics}: the Graphics
 *     of the canvas is used</li>
 * <li>canvas is an AWT {@link Component} but {@code getGraphics()} returns null: the
 *     headless BufferedImage fallback is used</li>
 * <li>canvas is a non-AWT {@link ICanvas} (JavaFX / SWT backend): fallback is used</li>
 * <li>canvas is null: fallback is used</li>
 * <li>width scales with font size and with string length</li>
 * </ul>
 */
public class TestPanamaGLPainter {

  private static final String WORD = "Hello";

  /** @return a painter with no GL attached — getTextLengthInPixels never touches GL. */
  private PanamaGLPainter newPainter() {
    return new PanamaGLPainter();
  }

  /** @return an ICanvas that is also an AWT Component, with a stubbed Graphics. */
  private Component newAwtCanvas(Graphics graphics) {
    Component canvas = mock(Component.class, withSettings().extraInterfaces(IScreenCanvas.class));
    when(canvas.getGraphics()).thenReturn(graphics);
    return canvas;
  }

  /** @return a real headless Graphics context usable to measure text. */
  private Graphics newHeadlessGraphics() {
    return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
  }

  // ---------------------------------------------------------------------------------------------
  // Edge cases

  @Test
  public void returnsZeroWhenFontIsNull() {
    PanamaGLPainter p = newPainter();
    assertEquals(0, p.getTextLengthInPixels((Font) null, WORD));
  }

  @Test
  public void returnsZeroForEmptyString() {
    PanamaGLPainter p = newPainter();
    assertEquals(0, p.getTextLengthInPixels(new Font("Dialog", 12), ""));
  }

  // ---------------------------------------------------------------------------------------------
  // Swing / AWT branch

  @Test
  public void measuresViaCanvasGraphicsWhenCanvasIsAwtComponent() {
    PanamaGLPainter p = newPainter();
    Graphics canvasGraphics = newHeadlessGraphics();
    Component canvas = newAwtCanvas(canvasGraphics);
    p.setCanvas((ICanvas) canvas);

    int width = p.getTextLengthInPixels(new Font("Dialog", 12), WORD);

    assertTrue("Expected a positive text width, got " + width, width > 0);
    // Ensure the AWT branch was taken (component Graphics was consulted).
    verify(canvas, atLeastOnce()).getGraphics();
  }

  @Test
  public void fallsBackWhenAwtComponentGraphicsIsNull() {
    // Reproduces the case of a Swing canvas that has not been shown yet (getGraphics()==null).
    PanamaGLPainter p = newPainter();
    Component canvas = newAwtCanvas(null);
    p.setCanvas((ICanvas) canvas);

    int width = p.getTextLengthInPixels(new Font("Dialog", 12), WORD);

    assertTrue("Fallback should still return a positive width, got " + width, width > 0);
    verify(canvas, atLeastOnce()).getGraphics();
  }

  // ---------------------------------------------------------------------------------------------
  // Non-AWT backends (JavaFX, SWT) — canvas is an ICanvas but NOT a java.awt.Component

  @Test
  public void fallsBackWhenCanvasIsNotAwtComponent() {
    PanamaGLPainter p = newPainter();
    p.setCanvas(mock(ICanvas.class));

    int width = p.getTextLengthInPixels(new Font("Dialog", 12), WORD);

    assertTrue("Fallback should return a positive width for a non-AWT canvas, got " + width,
        width > 0);
  }

  @Test
  public void fallsBackWhenCanvasIsNull() {
    PanamaGLPainter p = newPainter();
    // no canvas set

    int width = p.getTextLengthInPixels(new Font("Dialog", 12), WORD);

    assertTrue("Fallback should return a positive width when canvas is null, got " + width,
        width > 0);
  }

  // ---------------------------------------------------------------------------------------------
  // Monotonicity — same string+font must give identical widths whichever branch runs

  @Test
  public void componentBranchAndFallbackAgreeOnWidth() {
    PanamaGLPainter withAwt = newPainter();
    withAwt.setCanvas((ICanvas) newAwtCanvas(newHeadlessGraphics()));

    PanamaGLPainter withoutAwt = newPainter();
    withoutAwt.setCanvas(mock(ICanvas.class));

    Font font = new Font("Dialog", 12);
    int awtWidth = withAwt.getTextLengthInPixels(font, WORD);
    int fallbackWidth = withoutAwt.getTextLengthInPixels(font, WORD);

    assertEquals("AWT branch and headless fallback must measure identically when both rely on "
        + "AWT FontMetrics", awtWidth, fallbackWidth);
  }

  @Test
  public void largerFontYieldsLargerWidth() {
    PanamaGLPainter p = newPainter();
    p.setCanvas(mock(ICanvas.class)); // force fallback path

    int small = p.getTextLengthInPixels(new Font("Dialog", 10), WORD);
    int big = p.getTextLengthInPixels(new Font("Dialog", 30), WORD);

    assertTrue("Larger font size must produce a wider measurement: small=" + small + ", big="
        + big, big > small);
  }

  @Test
  public void longerStringYieldsLargerWidth() {
    PanamaGLPainter p = newPainter();
    p.setCanvas(mock(ICanvas.class)); // force fallback path
    Font font = new Font("Dialog", 12);

    int shortW = p.getTextLengthInPixels(font, "hi");
    int longW = p.getTextLengthInPixels(font, "Hello, world!");

    assertTrue("Longer string must produce a wider measurement: short=" + shortW + ", long="
        + longW, longW > shortW);
  }
}
