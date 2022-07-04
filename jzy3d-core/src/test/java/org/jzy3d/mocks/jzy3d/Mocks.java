package org.jzy3d.mocks.jzy3d;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.os.OperatingSystem;
import org.jzy3d.os.WindowingToolkit;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.primitives.axis.IAxis;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.ICanvasListener;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.mockito.Mockito;

public class Mocks {

  public static Chart Chart(int width, int height, float viewScale) {
    // Scene graph mocks
    Graph graph = mock(Graph.class);

    Scene scene = mock(Scene.class);
    when(scene.getGraph()).thenReturn(graph);
    
    
    // View, canvas and painter mocks
    View view = Mocks.ViewAndPainter(viewScale);
    
    ICanvas canvas = view.getCanvas();
    when(canvas.getRendererWidth()).thenReturn(width);
    when(canvas.getRendererHeight()).thenReturn(height);
    
    IPainter painter = view.getPainter();
    
    
    //Chart mock
    Chart chart = Mockito.mock(Chart.class);
    
    when(chart.getView()).thenReturn(view);
    when(chart.getScene()).thenReturn(scene);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getPainter()).thenReturn(painter);
    
    return chart;
  }

  
  // ---------------------------------------
  
  public static AxisLayout AxisLayout() {
    return mock(AxisLayout.class);
  }

  public static AxisBox Axis() {
    return mock(AxisBox.class);
  }

  public static AxisBox Axis(AxisLayout layout) {
    AxisBox axis = Axis();
    when(axis.getLayout()).thenReturn(layout);
    return axis;
  }

  public static View View2D() {
    View view = mock(View.class);
    when(view.is2D()).thenReturn(true);
    return view;
  }

  public static View View() {
    return Mockito.mock(View.class);
  }

  public static View View(AxisBox axis, IPainter painter) {
    return View(axis, painter, null);
  }


  public static View View(AxisBox axis, IPainter painter, ICanvas canvas) {
    View view = View();
    when(view.getAxis()).thenReturn(axis);
    when(view.getPainter()).thenReturn(painter);
    when(view.getCanvas()).thenReturn(canvas);
    return view;
  }

  public static View ViewAndPainter() {
    return ViewAndPainter(1);
  }

  public static View ViewAndPainter(float viewScale) {
    return ViewAndPainter(viewScale, "macos", "10", null, null);
  }

  public static View ViewAndPainter(float viewScale, String os, String version, Coord2d jvmScale,
      Coord2d gpuScale) {

    // Axis
    AxisLayout axisLayout = new AxisLayout();
    
    IAxis axis = mock(IAxis.class);
    when(axis.getLayout()).thenReturn(axisLayout);
    
    
    // Camera
    Camera camera = mock(Camera.class);
    
    // View
    View view = mock(View.class);

    when(view.getAxis()).thenReturn(axis);
    when(view.getCamera()).thenReturn(camera);
    when(view.getPixelScale()).thenReturn(new Coord2d(viewScale, viewScale));

    // Painter
    IPainter painter = Painter(os, version);

    // Canvas
    ICanvas canvas = mock(ICanvas.class);

    if (gpuScale != null) {
      when(canvas.getPixelScale()).thenReturn(gpuScale);
    }
    else {
      when(canvas.getPixelScale()).thenReturn(new Coord2d(viewScale, viewScale));
    }
    
    if (jvmScale != null)
      when(canvas.getPixelScaleJVM()).thenReturn(jvmScale);

    // All cross references
    when(painter.getView()).thenReturn(view);
    when(painter.getCanvas()).thenReturn(canvas);
    
    when(view.getCanvas()).thenReturn(canvas);
    when(view.getPainter()).thenReturn(painter);
    
    return view;
  }

  public static IPainter Painter(String os, String version) {
    IPainter painter = Painter();

    when(painter.getOS()).thenReturn(new OperatingSystem(os, version));
    when(painter.getWindowingToolkit()).thenReturn(WindowingToolkit.AWT);
    return painter;
  }

  public static IPainter Painter() {
    IPainter painter = mock(IPainter.class);

    when(painter.getOS()).thenReturn(new OperatingSystem("macos", "10.12"));
    when(painter.getWindowingToolkit()).thenReturn(WindowingToolkit.AWT);

    return painter;
  }

  public static ICanvas Canvas(boolean isNative) {
    ICanvas canvas = mock(ICanvas.class);
    when(canvas.isNative()).thenReturn(isNative);
    return canvas;
  }

  public static ICanvas Canvas(Coord2d scaleHard, Coord2d scaleVM) {
    return new ICanvas() {
      @Override
      public View getView() {
        return null;
      }

      @Override
      public int getRendererWidth() {
        return 0;
      }

      @Override
      public int getRendererHeight() {
        return 0;
      }
      
      @Override
      public Dimension getDimension() {
        return new Dimension(0, 0);
      }


      @Override
      public void screenshot(File file) throws IOException {}

      @Override
      public Object screenshot() {
        return null;
      }

      @Override
      public void forceRepaint() {}

      @Override
      public void dispose() {}

      @Override
      public void addMouseController(Object o) {}

      @Override
      public void addKeyController(Object o) {}

      @Override
      public void removeMouseController(Object o) {}

      @Override
      public void removeKeyController(Object o) {}

      @Override
      public String getDebugInfo() {
        return null;
      }

      @Override
      public void setPixelScale(float[] scale) {}

      @Override
      public Coord2d getPixelScale() {
        return scaleHard;
      }

      @Override
      public Coord2d getPixelScaleJVM() {
        return scaleVM;
      }

      @Override
      public double getLastRenderingTimeMs() {
        return 0;
      }

      @Override
      public void addCanvasListener(ICanvasListener listener) {}

      @Override
      public void removeCanvasListener(ICanvasListener listener) {}

      @Override
      public List<ICanvasListener> getCanvasListeners() {
        return null;
      }

      @Override
      public boolean isNative() {
        return true;
      }
    };
  }


}
