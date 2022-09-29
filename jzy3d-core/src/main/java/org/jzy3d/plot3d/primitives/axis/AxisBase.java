package org.jzy3d.plot3d.primitives.axis;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.annotations.AxeAnnotation;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.text.renderers.TextRenderer;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * An AxeBase provide a simple 3-segment object which is configured by a BoundingBox.
 * 
 * @author Martin Pernollet
 */
public class AxisBase implements IAxis {
  protected Coord3d scale;
  protected BoundingBox3d boundingBox;
  protected AxisLayout layout;
  protected SpaceTransformer spaceTransformer;

  protected ITextRenderer textRenderer = new TextRenderer();

  protected float exceedFactor = 0.1f;
  protected float textShiftFactor = 0.1f;

  enum AxePassThrough {
    ZERO, MIN;
  }

  protected AxePassThrough passThrough = AxePassThrough.ZERO;


  /** Create a simple axe centered on (0,0,0), with a dimension of 1. */
  public AxisBase() {
    this(new BoundingBox3d(0, 1, 0, 1, 0, 1));
  }

  /** Create a simple axe centered on (box.xmin, box.ymin, box.zmin) */
  public AxisBase(BoundingBox3d box) {
    this(box, new AxisLayout());
  }

  public AxisBase(BoundingBox3d box, AxisLayout layout) {
    setAxe(box);
    setScale(new Coord3d(1.0f, 1.0f, 1.0f));
    this.layout = layout;
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
    painter.glLineWidth(1);


    float x = 0, y = 0, z = 0;
    if (AxePassThrough.MIN.equals(passThrough)) {
      x = boundingBox.getXmin();
      y = boundingBox.getYmin();
      z = boundingBox.getZmin();
    }

    float xExceed = exceedFactor * boundingBox.getRange().x;
    float yExceed = exceedFactor * boundingBox.getRange().y;
    float zExceed = exceedFactor * boundingBox.getRange().z;

    painter.glBegin_Line();

    // X AXIS
    painter.color(layout.getGridColor());
    painter.glVertex3f(boundingBox.getXmin() - xExceed, y, z);
    painter.glVertex3f(boundingBox.getXmax() + xExceed, y, z);

    // painter.color(Color.GREEN);
    painter.glVertex3f(x, boundingBox.getYmin() - yExceed, z);
    painter.glVertex3f(x, boundingBox.getYmax() + yExceed, z);

    // painter.color(Color.BLUE);
    painter.glVertex3f(x, y, boundingBox.getZmin() - zExceed);
    painter.glVertex3f(x, y, boundingBox.getZmax() + zExceed);
    painter.glEnd();


    // Label
    Coord3d xLabel = new Coord3d(boundingBox.getXmax() + xExceed * 2, y, z);
    Coord3d yLabel = new Coord3d(x, boundingBox.getYmax() + yExceed * 2, z);
    Coord3d zLabel = new Coord3d(x, y, boundingBox.getZmax() + zExceed * 2);

    Horizontal h = Horizontal.CENTER;
    Vertical v = Vertical.CENTER;

    Font f = layout.getFont();

    Color xcolor = layout.getXTickColor();
    Color ycolor = layout.getYTickColor();
    Color zcolor = layout.getZTickColor();

    if (layout.isXAxisLabelDisplayed())
      textRenderer.drawText(painter, f, layout.getXAxisLabel(), xLabel, h, v, xcolor);

    if (layout.isYAxisLabelDisplayed())
      textRenderer.drawText(painter, f, layout.getYAxisLabel(), yLabel, h, v, ycolor);

    if (layout.isZAxisLabelDisplayed())
      textRenderer.drawText(painter, f, layout.getZAxisLabel(), zLabel, h, v, zcolor);

    
    if (layout.isXTickLabelDisplayed()) {
      String xmax = layout.getXTickRenderer().format(boundingBox.getXmax());
      xLabel = new Coord3d(boundingBox.getXmax(), y, z);
      
      Coord2d offset = new Coord2d(-5, 0);

      textRenderer.drawText(painter, f, xmax, xLabel, Horizontal.LEFT, v, xcolor, offset);

    }

    if (layout.isYTickLabelDisplayed()) {
      String ymax = layout.getYTickRenderer().format(boundingBox.getYmax());
      yLabel = new Coord3d(x, boundingBox.getYmax(), z);
      
      Coord2d offset = new Coord2d(-5, 0);

      textRenderer.drawText(painter, f, ymax, yLabel, Horizontal.LEFT, v, ycolor, offset);

    }

    if (layout.isZTickLabelDisplayed()) {
      String zmax = layout.getZTickRenderer().format(boundingBox.getZmax());
      zLabel = new Coord3d(x, y, boundingBox.getZmax());
      
      Coord2d offset = new Coord2d(0, -5);

      textRenderer.drawText(painter, f, zmax, zLabel, Horizontal.LEFT, v, ycolor, offset);

    }

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
  public AxisLayout getLayout() {
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
