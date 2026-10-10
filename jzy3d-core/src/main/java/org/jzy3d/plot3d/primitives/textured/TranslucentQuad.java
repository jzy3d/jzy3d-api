package org.jzy3d.plot3d.primitives.textured;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Quad;

public class TranslucentQuad extends Quad implements ITranslucent {
  @Override
  public void draw(IPainter painter) {
    // Execute transformation
    doTransform(painter);

    // Draw content of polygon
    if (faceDisplayed) {
      painter.glPolygonMode(GLConstants.GL_FRONT_AND_BACK, GLConstants.GL_FILL);
      if (wireframeDisplayed) {
        painter.glEnable(GLConstants.GL_POLYGON_OFFSET_FILL);
        painter.glPolygonOffset(1.0f, 1.0f);
      }
      painter.glBegin_Quad(); // <<<
      for (Point p : points) {
        if (mapper != null) {
          Color c = mapper.getColor(p.xyz);
          painter.colorAlphaFactor(c, alpha);
        } else
          painter.colorAlphaFactor(p.rgb, alpha);
        painter.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
      }
      painter.glEnd();
      if (wireframeDisplayed)
        painter.glDisable(GLConstants.GL_POLYGON_OFFSET_FILL);
    }

    // Draw edge of polygon
    if (wireframeDisplayed) {
      painter.glPolygonMode(GLConstants.GL_FRONT_AND_BACK, GLConstants.GL_LINE);

      painter.glEnable(GLConstants.GL_POLYGON_OFFSET_FILL);
      painter.glPolygonOffset(1.0f, 1.0f);

      painter.colorAlphaFactor(wireframeColor, alpha);
      painter.glLineWidth(wireframeWidth);

      painter.glBegin_Quad();
      for (Point p : points) {
        painter.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
      }
      painter.glEnd();

      painter.glDisable(GLConstants.GL_POLYGON_OFFSET_FILL);
    }
  }

  @Override
  public void setAlphaFactor(float a) {
    alpha = a;
  }

  protected float alpha = 1;
}
