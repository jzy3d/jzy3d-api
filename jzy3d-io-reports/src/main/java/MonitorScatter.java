import java.util.List;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.monitor.IMonitorable;
import org.jzy3d.monitor.Measure;
import org.jzy3d.monitor.Measure.CanvasPerfMeasure;
import org.jzy3d.monitor.Monitor;
import org.jzy3d.plot3d.primitives.Scatter;

public class MonitorScatter extends Scatter{
  protected Monitor monitor;
  protected IMonitorable monitorable;
  
  
  public MonitorScatter(Monitor monitor, IMonitorable monitorable) {
    super();
    this.monitor = monitor;
    this.monitorable = monitorable;
  }
  
  public Coord3d[] getData() {
    List<Measure> mm = monitor.getObservations().get(monitorable);

    Coord3d[] data = new Coord3d[mm.size()];
    
    int k = 0;
    for(Measure m: mm) {
      if(m instanceof CanvasPerfMeasure) {
        CanvasPerfMeasure cpm = (CanvasPerfMeasure)m;
        data[k++] = new Coord3d(cpm.getPixels(), cpm.getMili(), 0);
      }
    }

    
    return coordinates;
  }
  
  public BoundingBox3d getBounds() {
    BoundingBox3d bbox = new BoundingBox3d();
    for(Coord3d d: getData()) {
      bbox.add(d);
    }
    return bbox;
  }


}
