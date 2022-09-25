package org.jzy3d.plot3d.rendering.view;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axis.Axis;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

public class TestView {
  @Test
  public void whenViewBoundsMode_ThenCameraHasBounds() {

    // Given
    Graph graph = mock(Graph.class);
    Scene scene = mock(Scene.class);
    when(scene.getGraph()).thenReturn(graph);
    
    View view = new View();
    view.scene = scene;
    view.cam = mock(Camera.class);
    view.axis = new AxisBox(); 
    // bounds are stored in axisbox 
    // so we don't mock it
    
    // -------------------------------------
    // When setting bounds to AUTO_FIT
    
    BoundingBox3d bounds = new BoundingBox3d(0,1,0,1,0,1);
    when(graph.getBounds()).thenReturn(bounds);
    
    view.setBoundMode(ViewBoundMode.AUTO_FIT);

    // Then bounds are those the whole scene graph
    Assert.assertEquals(bounds, view.getBounds());
    

    // -------------------------------------
    // When setting bounds to MANUAL
    
    BoundingBox3d boundsManual = new BoundingBox3d(0,10,0,10,0,10);
    when(graph.getBounds()).thenReturn(null); // ensure graph can't be invoked
    
    view.setBoundsManual(boundsManual);
    //view.updateBounds();
    
    // Then bounds are those the whole scene graph
    Assert.assertEquals(boundsManual, view.getBounds());

  }
  
  @Test
  public void whenUpVectorChange_ThenViewpointRotationChange() {
    double H_SHIFT = Math.PI/6;
    double V_SHIFT = Math.PI/4;
    
    View view = new View();
    view.scene = mock(Scene.class);
    view.cam = mock(Camera.class);
    view.axis = mock(AxisBox.class); 

    // ---------------------------------------
    // Given a default view with Z up vector
    Coord3d viewpoint = new Coord3d(0, 0, 10);
    view.setViewPoint(viewpoint);

    // When
    view.rotate(new Coord2d(H_SHIFT, V_SHIFT));
    
    // Then
    Coord3d expectViewpoint = new Coord3d(-H_SHIFT, V_SHIFT, 10);
    Assert.assertEquals(expectViewpoint, view.getViewPoint());
    Assert.assertEquals(View.UP_VECTOR_Z, view.getUpVector());
    
    // ---------------------------------------
    // Given a default view with X up vector
    viewpoint = new Coord3d(0, 0, 10);
    view.setViewPoint(viewpoint);
    view.setUpVector(Axis.X);
    
    // When
    view.rotate(new Coord2d(H_SHIFT, V_SHIFT));
    
    // Then
    expectViewpoint = new Coord3d(V_SHIFT, H_SHIFT, 10);
    Assert.assertEquals(expectViewpoint, view.getViewPoint());
    Assert.assertEquals(View.UP_VECTOR_X, view.getUpVector());
    
    // ---------------------------------------
    // Given a default view with Y up vector
    viewpoint = new Coord3d(0, 0, 10);
    view.setViewPoint(viewpoint);
    view.setUpVector(Axis.Y);
    
    // When
    view.rotate(new Coord2d(-H_SHIFT, -V_SHIFT));
    
    // Then
    expectViewpoint = new Coord3d(V_SHIFT, H_SHIFT, 10);
    Assert.assertEquals(expectViewpoint, view.getViewPoint());
    Assert.assertEquals(View.UP_VECTOR_Y, view.getUpVector());

  }


}
