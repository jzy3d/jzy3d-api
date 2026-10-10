package org.jzy3d.plot3d.primitives.contour;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.contour.MapperContourPictureGenerator;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;


/**
 * A {@link ContourLevel} is an {@link Composite} gathering a collection of {@link LineStrip}s for a
 * given contour level.
 * 
 * @author Martin
 */
public class ContourLevel extends Composite {
  public ContourLevel() {
    super();
    lines = new ArrayList<LineStrip>();
  }

  public ContourLevel(float value) {
    super();
    this.value = value;
    lines = new ArrayList<LineStrip>();
  }

  public ContourLevel(int id, float value, List<LineStrip> lines) {
    super();
    this.id = id;
    this.value = value;
    this.lines = lines;

    updateComponents();
  }

  public int getId() {
    return id;
  }

  public float getValue() {
    return value;
  }

  public List<LineStrip> getLines() {
    return lines;
  }

  public void appendLine(LineStrip strip) {
    LineStrip friend = policy.mostMergeableIfAny(strip, lines);
    if (friend != null) {
      int fid = lines.indexOf(friend);
      friend = LineStrip.merge(strip, friend);
      lines.set(fid, friend);
    } else { // no one to connect
      lines.add(strip);
    }
    updateComponents();
  }

  public void fixZ(float value) {
    for (Drawable d : components) {
      LineStrip line = (LineStrip) d;
      for (Point point : line.getPoints()) {
        point.xyz.z = value;
      }
    }
  }

  protected void updateComponents() {
    components.clear();
    for (LineStrip strip : lines)
      components.add(strip);
  }

  protected ILineStripMergePolicy policy =
      new DefaultLineStripMergePolicy(MapperContourPictureGenerator.MERGE_STRIP_DIST);

  protected int id;
  protected float value;
  protected List<LineStrip> lines = new ArrayList<LineStrip>();
}
