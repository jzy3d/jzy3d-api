package org.jzy3d.plot3d.primitives.volume;

import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.opengl.util.GLBuffers;

/**
 * Requires Java > 8 to be able to use ByteBuffer.limit(...)
 * @author martin
 *
 */
public class TestTexture3D {
  @Test
  public void givenAVolume_whenOpeningChart_thenBufferIsSizedAppropriately() {

    // -------------------------------
    // Given a volume defined as a Texture3D object
    
    ByteBuffer buffer = GLBuffers.newDirectByteBuffer(10 * 10 * 10 * 4);

    for (float x = 0; x < 2; x += 0.2) {
      for (float y = 0; y < 2; y += 0.2) {
        for (float z = 0; z < 2; z += 0.2) {
          buffer.putFloat((float) Math.sin(x * y * z));
        }
      }
    }

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), 0, 1, new Color(1, 1, 1, .5f));

    Texture3D volume = new Texture3D(buffer, new int[] {10, 10, 10}, (float) 0, (float) 1,
        colorMapper, new BoundingBox3d(1, 10, 1, 10, 1, 10));
    
    
    Assert.assertFalse(volume.shapeVBO.hasMountedOnce());
    Assert.assertNull(volume.shaderProgram);
    Assert.assertNull(volume.colormapTexure);

    
    // -------------------------------
    // When loading with a chart
    
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());
    
    chart.add(volume);
    chart.open();

    
    // -------------------------------
    // Then
    
    Assert.assertTrue(volume.shapeVBO.hasMountedOnce());
    Assert.assertNotNull(volume.shaderProgram);
    Assert.assertNotNull(volume.colormapTexure);
    
  }
}
