import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.factories.LWJGLChartFactory;
import org.jzy3d.plot3d.primitives.SampleGeom;

public class LWJGLChart {

  public static void main(String[] args) {
    LWJGLChartFactory f = new LWJGLChartFactory();
    
    Chart c = f.newChart();
    c.add(SampleGeom.surface());
    c.open();
    c.addMouse();
  }

}
