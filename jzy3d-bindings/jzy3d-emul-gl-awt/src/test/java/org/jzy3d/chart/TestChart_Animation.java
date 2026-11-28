package org.jzy3d.chart;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * A chart must not keep a rendering thread once its animation is stopped or once it is disposed :
 * such a thread renders continuously and holds the chart in memory, as reported with an application
 * that stopped the animation right after the chart creation.
 */
public class TestChart_Animation {
  static final long JOIN_TIMEOUT_MS = 2000;

  @Test
  public void whenAnimationIsStoppedRightAfterItStarted_thenRenderingThreadEnds()
      throws InterruptedException {
    for (int i = 0; i < 20; i++) {
      Chart chart = new EmulGLChartFactory().newChart(Quality.Advanced());

      chart.startAnimation();
      Thread thread = animator(chart).t;
      assertNotNull(thread);

      chart.setAnimated(false);

      thread.join(JOIN_TIMEOUT_MS);
      assertFalse("Rendering thread still alive at iteration " + i, thread.isAlive());
      assertFalse(animator(chart).isAnimating());
      assertFalse(chart.getQuality().isAnimated());
    }
  }

  @Test
  public void whenAnimationIsStartedTwice_thenOnlyOneRenderingThreadRemains()
      throws InterruptedException {
    Chart chart = new EmulGLChartFactory().newChart(Quality.Advanced());

    chart.startAnimation();
    Thread first = animator(chart).t;
    chart.startAnimation();
    Thread second = animator(chart).t;

    first.join(JOIN_TIMEOUT_MS);
    assertFalse("First rendering thread still alive", first.isAlive());
    assertTrue(second.isAlive());

    chart.stopAnimation();
    second.join(JOIN_TIMEOUT_MS);
    assertFalse(second.isAlive());
  }

  @Test
  public void whenAnimatedChartIsDisposed_thenRenderingThreadEnds() throws InterruptedException {
    Chart chart = new EmulGLChartFactory().newChart(Quality.Advanced());

    chart.startAnimation();
    Thread thread = animator(chart).t;

    chart.dispose();

    thread.join(JOIN_TIMEOUT_MS);
    assertFalse("Rendering thread still alive after dispose", thread.isAlive());
  }

  protected EmulGLAnimator animator(Chart chart) {
    return (EmulGLAnimator) ((org.jzy3d.plot3d.rendering.canvas.IScreenCanvas) chart.getCanvas())
        .getAnimation();
  }
}
