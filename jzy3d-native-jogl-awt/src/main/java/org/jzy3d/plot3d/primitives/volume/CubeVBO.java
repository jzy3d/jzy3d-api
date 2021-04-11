package org.jzy3d.plot3d.primitives.volume;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class CubeVBO extends DrawableVBO {

  boolean disposed = false;

  public CubeVBO(CubeVBOBuilder builder) {
    super(builder);
    this.setGeometry(GL2.GL_QUADS);
    this.setColor(new Color(1f, 0f, 1f, 1f));
    this.doSetBoundingBox(new BoundingBox3d(0, 1, 0, 1, 0, 1));
  }

  @Override
  public void draw(IPainter painter) {

    doTransform(painter);

    if (!hasMountedOnce) {
      mount(painter);
    }

    super.draw(painter);

    if (disposed) {
      GL gl = ((NativeDesktopPainter) painter).getGL();

      gl.glDeleteBuffers(1, arrayName, 0);
      gl.glDeleteBuffers(1, elementName, 0);
      return;
    }

  }

  @Override
  public void mount(IPainter painter) {
    try {
      loader.load(painter, this);
      hasMountedOnce = true;
    } catch (Exception e) {
      e.printStackTrace();
      // Logger.getLogger(DrawableVBO.class).error(e, e);
    }
  }

  @Override
  public void dispose() {
    disposed = true;
  }
}
