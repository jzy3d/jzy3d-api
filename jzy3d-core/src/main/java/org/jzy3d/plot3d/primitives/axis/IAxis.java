package org.jzy3d.plot3d.primitives.axis;

import java.util.List;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.annotations.AxeAnnotation;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;


/** Specify services that a concrete Axe must provide. */
public interface IAxis {
  public void dispose();

  public void setAxe(BoundingBox3d box);

  public void draw(IPainter painter);

  public void setScale(Coord3d scale);

  public Coord3d getScale();

  public BoundingBox3d getBounds();

  public Coord3d getCenter();

  public IAxisLayout getLayout();

  public List<AxeAnnotation> getAnnotations();

  public void setAnnotations(List<AxeAnnotation> annotations);

  public SpaceTransformer getSpaceTransformer();

  public void setSpaceTransformer(SpaceTransformer spaceTransformer);

  public BoundingBox3d getWholeBounds();

  public ITextRenderer getTextRenderer();

  public void setTextRenderer(ITextRenderer renderer);
}
