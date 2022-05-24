package org.jzy3d.demos.volume;

import java.io.File;
import java.nio.ByteBuffer;
import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapGrayscale;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.volume.Texture3D;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLNumericArray;
import com.jogamp.opengl.util.GLBuffers;

/**
 * Get lizard file from https://download.jzy3d.org/objfiles/lizard.mat
 * 
 * @author Jacok Filik
 *
 */
public class LizardVolumeDemo extends AWTAbstractAnalysis {
  public static void main(String[] args) throws Exception {
    AnalysisLauncher.open(new LizardVolumeDemo());
  }

  @SuppressWarnings("unchecked")
  @Override
  public void init() {

    ByteBuffer buffer = null;
    int[] shape = null;
    float max = Float.NEGATIVE_INFINITY;
    float min = Float.POSITIVE_INFINITY;

    try {
      String fileName = "data/lizard.mat";
      if (!new File(fileName).exists()) {
        System.err.println(
            "You need to download http://download.jzy3d.org/objfiles/lizard.mat and save it as "
                + fileName);
        System.exit(1);
      }

      MatFileReader mfr = new MatFileReader(fileName);
      MLNumericArray<Integer> data = (MLNumericArray<Integer>) mfr.getMLArray("data");
      shape = data.getDimensions();
      int size = data.getSize();
      data.toString();

      buffer = GLBuffers.newDirectByteBuffer(size * 4);

      for (int i = 0; i < size; i++) {
        float f = data.get(i).floatValue();
        buffer.putFloat(f);

        if (f < min) {
          min = f;
        }

        if (f > max) {
          max = f;
        }
      }


    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    int temp = shape[0];
    shape[0] = shape[2];
    shape[2] = temp;

    ColorMapper colorMapper =
        new ColorMapper(new ColorMapGrayscale(), min, max, new Color(1, 1, 1, .5f));

    Texture3D volume = new Texture3D(buffer, shape, (float) min + ((max - min) / 10),
        (float) max - ((max - min) / 10), colorMapper,
        new BoundingBox3d(0, shape[2], 0, shape[1], 0, shape[0]));

    // Transform transform = new Transform();
    // transform.add(new Rotate(90, new Coord3d(0,1,0)));
    // volume.setTransformBefore(transform);

    // Create a chart
    chart = AWTChartFactory.chart(Quality.Intermediate());
    chart.getScene().getGraph().add(volume);
    chart.getView().setSquared(false);

    // Keep former text renderer as the new one does not work properly with shaders
    chart.getView().getAxis().setTextRenderer(new TextBitmapRenderer());

  }
}
