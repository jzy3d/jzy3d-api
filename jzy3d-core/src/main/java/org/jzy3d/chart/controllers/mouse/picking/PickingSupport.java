package org.jzy3d.chart.controllers.mouse.picking;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.RenderMode;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.pickable.Pickable;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.transform.Scale;
import org.jzy3d.plot3d.transform.Transform;

/**
 * @see: http://www.opengl.org/resources/faq/technical/selection.htm
 * 
 * @author Martin Pernollet
 *
 */
public class PickingSupport {
  public static int BRUSH_SIZE = 10;
  public static int BUFFER_SIZE = 2048;
  
  protected static int pickId = 0;
  protected Map<Integer, Pickable> pickables = new HashMap<>();
  protected List<IObjectPickedListener> verticesListener = new ArrayList<>(1);
  protected Map<Pickable, Object> pickableTargets = new HashMap<>();
  protected int brushSize;
  protected int bufferSize;

  public PickingSupport() {
    this(BRUSH_SIZE);
  }

  public PickingSupport(int brushSize) {
    this(brushSize, BUFFER_SIZE);
  }

  public PickingSupport(int brushSize, int bufferSize) {
    this.brushSize = brushSize;
    this.bufferSize = bufferSize;
  }

  /*************************/

  public boolean addObjectPickedListener(IObjectPickedListener listener) {
    return verticesListener.add(listener);
  }

  public boolean removeObjectPickedListener(IObjectPickedListener listener) {
    return verticesListener.remove(listener);
  }

  protected void fireObjectPicked(List<? extends Object> v) {
    for (IObjectPickedListener listener : verticesListener) {
      listener.objectPicked(v, this);
    }
  }

  /*************************/

  public void registerDrawableObject(Drawable drawable, Object model) {
    if (drawable instanceof Pickable) {
      registerPickableObject((Pickable) drawable, model);
    }
  }

  public synchronized void registerPickableObject(Pickable pickable, Object model) {
    pickable.setPickingId(pickId++);
    pickables.put(pickable.getPickingId(), pickable);
    pickableTargets.put(pickable, model);
  }

  public synchronized void getPickableObject(int id) {
    pickables.get(id);
  }

  /*************************/

  protected TicToc perf = new TicToc();

  public void pickObjects(IPainter painter, View view, Graph graph, IntegerCoord2d pickPoint) {
    perf.tic();

    IntBuffer selectBuffer = newDirectIntBuffer(bufferSize);

    // Prepare selection data
    int[] viewport = painter.getViewPortAsInt();
    painter.glSelectBuffer(bufferSize, selectBuffer);
    painter.glRenderMode(RenderMode.SELECT);
    painter.glInitNames();
    painter.glPushName(0);

    // Retrieve view settings
    Camera camera = view.getCamera();
    CameraMode cMode = view.getCameraMode();
    Coord3d viewScaling = view.getLastViewScaling();
    Transform viewTransform = new Transform(new Scale(viewScaling));
    IntegerCoord2d pickPointHiDPI = pickPoint;//.div(painter.getCanvas().getPixelScale());
    double xpick = pickPointHiDPI.x;
    double ypick = pickPointHiDPI.y;
    
    //System.out.println(pickPoint + " " + pickPointHiDPI);
    //System.out.println(painter.getCanvas().getPixelScale());
    
    painter.acquireGL();

    
    // Setup projection matrix
    painter.glMatrixMode_Projection();
    painter.glPushMatrix();
    {
      painter.glLoadIdentity();

      
      // Setup picking matrix, and update view frustrum
      painter.gluPickMatrix(xpick, ypick, brushSize, brushSize, viewport, 0);
      camera.doShoot(painter, cMode);

      // Draw each pickable element in select buffer
      painter.glMatrixMode_ModelView();

      synchronized (this) {
        for (Pickable pickable : pickables.values()) {
          setCurrentName(painter, pickable);
          pickable.setTransform(viewTransform);
          pickable.draw(painter);
          releaseCurrentName(painter);
        }
      }


      // Back to projection matrix
      painter.glMatrixMode_Projection();
    }
    painter.glPopMatrix();
    painter.glFlush();

    // Process hits
    int hits = painter.glRenderMode(RenderMode.RENDER);
    
    int[] selectBuf = new int[bufferSize]; 
    selectBuffer.get(selectBuf);
    List<Pickable> picked = processHits(hits, selectBuf);

    // Trigger an event
    List<Object> clickedObjects = new ArrayList<>(hits);
    for (Pickable pickable : picked) {
      Object vertex = pickableTargets.get(pickable);
      clickedObjects.add(vertex);
    }
    perf.toc();
    
    fireObjectPicked(clickedObjects);
    
    //painter.releaseGL();

  }

  /** Picked from JOGL Buffers class. */
  public static final int SIZEOF_INT = 4;

  /** Picked from JOGL Buffers class. */
  public static IntBuffer newDirectIntBuffer(final int numElements) {
    return newDirectByteBuffer(numElements * SIZEOF_INT).asIntBuffer();
  }

  /** Picked from JOGL Buffers class. */
  public static ByteBuffer newDirectByteBuffer(final int numElements) {
    return nativeOrder(ByteBuffer.allocateDirect(numElements));
  }

  /** Picked from JOGL Buffers class. */
  public static ByteBuffer nativeOrder(final ByteBuffer buf) {
    return buf.order(ByteOrder.nativeOrder());
  }



  public double getLastPickPerfMs() {
    return perf.elapsedMilisecond();
  }

  protected void setCurrentName(IPainter painter, Pickable pickable) {
    if (method == 0)
      painter.glLoadName(pickable.getPickingId());
    else
      painter.glPushName(pickable.getPickingId());
  }

  protected void releaseCurrentName(IPainter painter) {
    if (method == 0)
      ;
    else
      painter.glPopName();
  }

  protected static int method = 0;

  /*********************/

  /** Provides the number of picked object by a click. */
  @SuppressWarnings("unused")
  protected List<Pickable> processHits(int hits, int buffer[]) {
    int names, ptr = 0;
    int z1, z2 = 0;

    List<Pickable> picked = new ArrayList<Pickable>();

    for (int i = 0; i < hits; i++) {
      names = buffer[ptr];
      ptr++;
      z1 = buffer[ptr];
      ptr++;
      z2 = buffer[ptr];
      ptr++;

      for (int j = 0; j < names; j++) {
        int idj = buffer[ptr];
        ptr++;
        if (!pickables.containsKey(idj))
          throw new RuntimeException("internal error: pickable id not found in registry!");
        picked.add(pickables.get(idj));
      }
    }
    return picked;
  }

  public synchronized void unRegisterAllPickableObjects() {
    pickables.clear();
    pickableTargets.clear();
  }

}
