package org.jzy3d.plot3d.primitives.axis;

import java.util.List;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.annotations.AxeAnnotation;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * An AxeBase provide a simple 3-segment object which is configured by a BoundingBox.
 * 
 * @author Martin Pernollet
 */
public class AxisBase implements IAxis {
  protected Coord3d scale;
  protected BoundingBox3d boundingBox;
  protected IAxisLayout layout;
  protected SpaceTransformer spaceTransformer;

  protected ITextRenderer textRenderer = new TextBitmapRenderer();


  /** Create a simple axe centered on (0,0,0), with a dimension of 1. */
  public AxisBase() {
    setAxe(new BoundingBox3d(0, 1, 0, 1, 0, 1));
    setScale(new Coord3d(1.0f, 1.0f, 1.0f));
  }

  /** Create a simple axe centered on (box.xmin, box.ymin, box.zmin) */
  public AxisBase(BoundingBox3d box) {
    setAxe(box);
    setScale(new Coord3d(1.0f, 1.0f, 1.0f));
  }

  @Override
  public void dispose() {}

  /******************************************************************/

  @Override
  public void setAxe(BoundingBox3d box) {
    boundingBox = box;
  }

  @Override
  public void draw(IPainter painter) {
    painter.glLoadIdentity();
    painter.glScalef(scale.x, scale.y, scale.z);
    painter.glLineWidth(2);

    painter.glBegin_Line();
    painter.glColor3f(1.0f, 0.0f, 0.0f); // R
    painter.glVertex3f(boundingBox.getXmin(), boundingBox.getYmin(), boundingBox.getZmin());
    painter.glVertex3f(boundingBox.getXmax(), 0, 0);
    painter.glColor3f(0.0f, 1.0f, 0.0f); // G
    painter.glVertex3f(boundingBox.getXmin(), boundingBox.getYmin(), boundingBox.getZmin());
    painter.glVertex3f(0, boundingBox.getYmax(), 0);
    painter.glColor3f(0.0f, 0.0f, 1.0f); // B
    painter.glVertex3f(boundingBox.getXmin(), boundingBox.getYmin(), boundingBox.getZmin());
    painter.glVertex3f(0, 0, boundingBox.getZmax());
    painter.glEnd();
  }

  /**
   * Set the scaling factor that are applied on this object before GL2 commands.
   */
  @Override
  public void setScale(Coord3d scale) {
    this.scale = scale;
  }

  @Override
  public BoundingBox3d getBounds() {
    return boundingBox;
  }

  /** Get the minimum values of the bounding box for each dimension. */
  @Override
  public Coord3d getCenter() {
    return new Coord3d(boundingBox.getXmin(), boundingBox.getYmin(), boundingBox.getZmin());
  }

  @Override
  public IAxisLayout getLayout() {
    return layout;
  }

  @Override
  public SpaceTransformer getSpaceTransformer() {
    return spaceTransformer;
  }

  @Override
  public void setSpaceTransformer(SpaceTransformer spaceTransformer) {
    this.spaceTransformer = spaceTransformer;
  }

  @Override
  public Coord3d getScale() {
    return scale;
  }


  @Override
  public List<AxeAnnotation> getAnnotations() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setAnnotations(List<AxeAnnotation> annotations) {
    // TODO Auto-generated method stub

  }

  @Override
  public BoundingBox3d getWholeBounds() {
    return boundingBox;
  }

  @Override
  public ITextRenderer getTextRenderer() {
    return textRenderer;
  }

  @Override
  public void setTextRenderer(ITextRenderer renderer) {
    textRenderer = renderer;
  }
}
