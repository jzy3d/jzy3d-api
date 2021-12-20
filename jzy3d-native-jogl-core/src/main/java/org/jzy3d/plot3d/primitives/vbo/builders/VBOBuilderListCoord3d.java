package org.jzy3d.plot3d.primitives.vbo.builders;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.vbo.buffers.FloatVBO;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;

/**
 * A simple loader loading an existing collection of coordinates into a Vertex Buffer Objects once
 * GL initialization stage requires it to be loaded.
 * 
 * If a colormapper is given, color buffer will be filled according to coloring policy.
 * 
 * @author martin
 */
public class VBOBuilderListCoord3d extends VBOBuilder implements IGLLoader<DrawableVBO> {
  protected List<Coord3d> coordinates = null;
  protected ColorMapper coloring = null;

  public VBOBuilderListCoord3d(List<Coord3d> coordinates) {
    this.coordinates = coordinates;
  }

  public VBOBuilderListCoord3d(List<Coord3d> coordinates, ColorMapper coloring) {
    this.coordinates = coordinates;
    this.coloring = coloring;
  }

  // @Override
  // @SuppressWarnings("unchecked")
  @Override
  public void load(IPainter painter, DrawableVBO drawable) throws Exception {
    FloatVBO vbo = initFloatVBO(drawable, coloring != null, coordinates.size());
    fillWithCollection(drawable, coordinates, vbo, coloring);
    drawable.setData(((NativeDesktopPainter) painter).getGL(), vbo);
    LogManager.getLogger(VBOBuilderListCoord3d.class)
        .info("done loading " + coordinates.size() + " coords");
  }
}
