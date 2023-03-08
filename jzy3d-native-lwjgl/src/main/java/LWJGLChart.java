import org.jzy3d.chart.Chart;
import org.jzy3d.factories.LWJGLChartFactory;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;

public class LWJGLChart {

  public static void main(String[] args) {
    LWJGLChartFactory f = new LWJGLChartFactory();
    
    Shape surface = SampleGeom.surface();
    
    Chart c = f.newChart();
    c.add(surface);
    c.open();
    c.addMouse();
    
    AWTColorbarLegend colorbar = new AWTColorbarLegend(surface, c);
    surface.setLegend(colorbar);

  }

}
