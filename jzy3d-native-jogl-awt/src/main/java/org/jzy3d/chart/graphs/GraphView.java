package org.jzy3d.chart.graphs;

import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import com.jogamp.opengl.glu.GLU;

public class GraphView extends ChartView {
  public GraphView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    super(factory, scene, canvas, quality);
  }


  @Override
  protected void correctCameraPositionForIncludingTextLabels(IPainter painter,
      ViewportConfiguration viewport) {
    /*
     * cam.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(), left, right);
     * cam.shoot(gl, glu, cameraMode); axe.draw(gl, glu, cam); clear(gl);
     * 
     * AxeBox abox = (AxeBox)axe; BoundingBox3d newBounds = abox.getWholeBounds().scale(scaling);
     * 
     * if (viewmode == CameraMode.TOP){ float radius = Math.max(newBounds.getXmax() -
     * newBounds.getXmin(), newBounds.getYmax() - newBounds.getYmin()) / 2; radius += (radius *
     * STRETCH_RATIO); cam.setRenderingSphereRadius(radius); }else
     * cam.setRenderingSphereRadius((float) newBounds.getRadius());
     * 
     * Coord3d target = newBounds.getCenter(); Coord3d eye = viewpoint.cartesian().add(target);
     * cam.setTarget(target); cam.setEye(eye);
     */
  }

  protected GLU glu = new GLU();
}
