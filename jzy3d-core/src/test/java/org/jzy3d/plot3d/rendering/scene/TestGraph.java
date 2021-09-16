package org.jzy3d.plot3d.rendering.scene;

import org.junit.Test;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;
import org.mockito.Mockito;

public class TestGraph {
  @Test
  public void clipping() {
    IPainter p = Mockito.spy(IPainter.class);
    
    // Given a graph 
    Graph g = new Graph();
    g.setTransform(new Transform());
    
    // ---------------------------------
    // When drawing with a clipping box
    BoundingBox3d clipBox = new BoundingBox3d(0,100,0,101,0,102);
    g.setClipBox(clipBox);
    
    g.draw(p);
    
    // Clipping is called with a margin to show the values AT the bounding box and then disabled
    BoundingBox3d expectClipBox = clipBox.marginRatio(Graph.CLIP_MARGIN_RATIO);
    Mockito.verify(p).clip(expectClipBox);
    Mockito.verify(p).clipOn();
    Mockito.verify(p).clipOff();
    
    // ---------------------------------
    // When drawing with a clipping box
    g.setClipBox(clipBox, false);
    
    g.draw(p);
    
    // Clipping is called with a margin to show the values AT the bounding box and then disabled
    expectClipBox = clipBox.clone();
    Mockito.verify(p).clip(expectClipBox);
  }

}
