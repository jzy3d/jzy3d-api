package org.jzy3d.demos.volume;

import java.nio.ByteBuffer;
import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.volume.Texture3D;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.opengl.util.GLBuffers;

/**
 * 
 * @author Jacok Filik
 *
 */
public class BasicVolumeDemo extends AWTAbstractAnalysis {
  public static void main(String[] args) throws Exception {
    AnalysisLauncher.open(new BasicVolumeDemo());
  }

  @Override
  public void init() {

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), 0, 1, new Color(1, 1, 1, 1.5f));

    ByteBuffer buffer = GLBuffers.newDirectByteBuffer(10 * 10 * 10 * 4);
    // make some kind of volume
    for (float x = 0; x < 2; x += 0.2) {
      for (float y = 0; y < 2; y += 0.2) {
        for (float z = 0; z < 2; z += 0.2) {
          buffer.putFloat((float) Math.sin(x * y * z));
        }
      }
    }

    Texture3D volume = new Texture3D(buffer, new int[] {10, 10, 10}, (float) 0, (float) 1,
        colorMapper, new BoundingBox3d(1, 10, 1, 10, 1, 10));

    // Create a chart
    chart = new AWTChartFactory().newChart(Quality.Intermediate());
    chart.getScene().getGraph().add(volume);
    chart.getView();
  }
}
