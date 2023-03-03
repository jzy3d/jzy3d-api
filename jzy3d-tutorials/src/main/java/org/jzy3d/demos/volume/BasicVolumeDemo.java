package org.jzy3d.demos.volume;

import java.nio.ByteBuffer;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.volume.Texture3D;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;
import com.jogamp.opengl.util.GLBuffers;

/**
 * 
 * @author Jacok Filik
 * 
 * Initially https://github.com/jzy3d/jzy3d-api/commit/3e60619ea519b9062e24cac2f0963a1d64757600#diff-73f8bb09427965a3c8c5e9bc658f29bde9b677e45c850404f03cfff0c998b435
 *
 */
public class BasicVolumeDemo  {
  public static void main(String[] args) throws Exception {

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), 0, 1, new Color(1, 1, 1, .5f));

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
    ChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setDebugGL(true);

    Chart chart = f.newChart(Quality.Intermediate());

    // Keep former text renderer as the new one does not work properly with shaders
    chart.getView().getAxis().setTextRenderer(new TextBitmapRenderer());

    chart.getScene().getGraph().add(volume);
    

    chart.open();
    chart.addMouse();

  }
}
