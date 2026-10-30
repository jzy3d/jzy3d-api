package org.jzy3d.plot3d.primitives.selectable;

import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Polygon2d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;


/**
 * An {@link Selectable} object is supposed to be able to compute its projection on the screen
 * according to the {@link Camera} settings (viewport, viewpoint, etc).
 * 
 * The {@link View} provides a mean to update the projection of all {@link Selectable} held by the
 * scene's {@link Graph}. Indeed, interaction sources such as an {@link AWTAbstractMouseSelector}
 * are supposed to call {@link view.project()} to query an update of all {@link Selectable} objects'
 * projections.
 * 
 * Projection implementation is rather fast but one should consider projecting only when required,
 * i.e. when the actual scene's Graph projection changes, which is: - when view point changes - when
 * view scaling changes - when the objects data (shape) changes
 * 
 * So the user is responsible of handling appropriate calls to view.project()
 * 
 * @author Martin Pernollet
 */
public interface Selectable {
  public void project(IPainter painter, Camera cam);

  public Polygon2d getHull2d();

  public List<Coord3d> getLastProjection();
}
