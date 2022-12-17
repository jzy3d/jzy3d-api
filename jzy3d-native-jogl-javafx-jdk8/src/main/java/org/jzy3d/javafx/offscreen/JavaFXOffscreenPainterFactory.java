package org.jzy3d.javafx.offscreen;

import org.jzy3d.javafx.AbstractJavaFXPainterFactory;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

public class JavaFXOffscreenPainterFactory extends AbstractJavaFXPainterFactory {
  @Override
  public Renderer3d newRenderer3D(View view) {
    return new JavaFXOffscreenRenderer3d(view, traceGL, debugGL);
  }
}
