package org.jzy3d.plot3d.primitives.axis;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.contour.ContourLevel;
import org.jzy3d.plot3d.primitives.contour.ContourMesh;
import org.jzy3d.plot3d.primitives.textured.NativeDrawableImage;
import org.jzy3d.plot3d.rendering.textures.BufferedImageTexture;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;


/**
 * An axe box for displaying a contour map that remains on bottom of the box, whatever the box scale
 * is.
 * 
 * @author Martin Pernollet
 */
public class ContourAxisBox extends AxisBox {
  static Logger logger = LogManager.getLogger(ContourAxisBox.class);

  public ContourAxisBox(BoundingBox3d bbox, IAxisLayout layout) {
    super(bbox, layout);
  }

  public ContourAxisBox(BoundingBox3d bbox) {
    super(bbox);
  }

  public BufferedImage getContourImage() {
    return contourImg;
  }

  public void setContourImg(BufferedImage contourImage, Range xrange, Range yrange) {
    this.contourImg = contourImage;
    rebuildTexture();
  }

  public ContourMesh getContourMesh() {
    return mesh;
  }

  public void setContourMesh(ContourMesh contour) {
    this.mesh = contour;
  }

  protected void rebuildTexture() {
    BufferedImageTexture contourResource = new BufferedImageTexture(contourImg);
    float zrange = zmax - zmin; // avoids rendering problems with the tick lines on the bottom part
                                // of the cube
    this.contourTexture = new NativeDrawableImage(contourResource, PlaneAxis.Z,
        zmin - zrange / 1000, getDefaultTextureMapping());
  }

  protected List<Coord2d> getDefaultTextureMapping() {
    List<Coord2d> mapping = new ArrayList<Coord2d>(4);
    mapping.add(new Coord2d(xmin, ymin));
    mapping.add(new Coord2d(xmax, ymin));
    mapping.add(new Coord2d(xmax, ymax));
    mapping.add(new Coord2d(xmin, ymax));
    return mapping;
  }

  @Override
  protected void setAxeBox(float xmin, float xmax, float ymin, float ymax, float zmin, float zmax) {
    super.setAxeBox(xmin, xmax, ymin, ymax, zmin, zmax);
    this.xmin = xmin;
    this.xmax = xmax;
    this.ymin = ymin;
    this.ymax = ymax;
    this.zmin = zmin;
    this.zmax = zmax;

    if (contourImg != null)
      rebuildTexture();
    // TODO: could avoid rebuilding if the texture object was able
    // to reset its axisValue

    if (mesh != null)
      for (ContourLevel line : mesh.lines.getContourLevels()) {
        line.fixZ(zmin);
      }
  }

  @Override
  public void draw(IPainter painter) {
    super.draw(painter);

    // gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

    // Render the contour texture if available
    if (contourTexture != null) {
      contourTexture.draw(painter);
    }

    if (mesh != null) {
      drawMesh(painter, mesh);
    }

  }

  public void drawMesh(IPainter painter, ContourMesh mesh) {
    // draw contour lines
    for (ContourLevel line : mesh.lines.getContourLevels()) {
      line.draw(painter);
      logger.info("Contour level '" + line.getValue() + "' has " + line.getLines());
    }

    // draw text
    for (Double level : mesh.getLevels()) {
      String label = mesh.getLevelLabel(level);
      ContourLevel contour = mesh.lines.getContourLevel(level);
      if (label != null && level != null) {
        for (LineStrip strip : contour.getLines()) {
          Coord3d position = strip.get(strip.size() / 2).xyz;
          textRenderer.drawText(painter, getLayout().getFont(), label, position,
              Horizontal.CENTER, Vertical.CENTER, Color.BLACK);
        }
      }
    }
  }


  protected float xmin;
  protected float xmax;
  protected float ymin;
  protected float ymax;
  protected float zmin;
  protected float zmax;
  protected BufferedImage contourImg;
  protected NativeDrawableImage contourTexture;
  protected ContourMesh mesh;
}
