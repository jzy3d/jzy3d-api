package org.jzy3d.plot3d.rendering.canvas;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Whether a Quality is animated decides if a screen canvas starts a thread repainting it
 * continuously : keep the documented defaults.
 */
public class TestQuality_Animated {
  @Test
  public void whenQualityIsBuiltWithConstructor_thenItIsAnimated() {
    assertTrue(new Quality(true, false, false, false, false, false, true).isAnimated());
  }

  @Test
  public void whenQualityIsFastest_thenItIsAnimated() {
    assertTrue(Quality.Fastest().isAnimated());
  }

  @Test
  public void whenQualityIsIntermediateAdvancedOrNicest_thenItRepaintsOnDemand() {
    assertFalse(Quality.Intermediate().isAnimated());
    assertFalse(Quality.Advanced().isAnimated());
    assertFalse(Quality.Nicest().isAnimated());
  }

  @Test
  public void whenQualityIsCloned_thenAnimationIsKept() {
    assertFalse(Quality.Advanced().clone().isAnimated());
    assertTrue(Quality.Advanced().setAnimated(true).clone().isAnimated());
  }
}
