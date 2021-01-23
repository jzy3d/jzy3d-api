package org.jzy3d.plot3d.rendering.canvas;

import org.jzy3d.chart.IAnimator;

/**
 * An {@link IScreenCanvas} defines a panel into which rendering occurs, and that can be integrated
 * in an existing GUI. An {@link IScreenCanvas} implementation allows to render 3d content for a
 * given Windowing toolkit:
 * <ul>
 * <li>{@link CanvasAWT} is a base implementation suitable for AWT, Swing or SWT applications.</li>
 * <li>{@link CanvasNewtAwt} is a new implementation (since 0.9.1) using JOGL's recommended
 * windowing toolkit.</li>
 * <li>{@link CanvasSwing} is a deprecated implementation for Swing (since 0.9.1)</li>
 * </ul>
 * 
 * An {@link IScreenCanvas} provides access to an {@link IAnimator} instance. This offers the
 * alternative of repaint-on-demand-model based on Controllers, and repaint-continuously model based
 * on the Animator (default behaviour).
 * 
 * The actual {@link IScreenCanvas} initialize by a given chart will depend on:
 * <ul>
 * <li>The Windowing toolkit parameter of the chart (new Chart("awt"))
 * <li>The Windowing toolkit parameters supported by
 * {@link ChartComponentFactory.initializeCanvas(...)}
 * </ul>
 * 
 * In other word, if creating another canvas implementation is required, simply extends the
 * component factory.
 * 
 * @author Martin Pernollet
 */
public interface IScreenCanvas extends ICanvas {
  public void display();

  public IAnimator getAnimation();
}
