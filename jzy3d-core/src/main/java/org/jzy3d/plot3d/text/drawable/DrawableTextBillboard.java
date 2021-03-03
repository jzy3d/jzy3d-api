package org.jzy3d.plot3d.text.drawable;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.text.DrawableTextWrapper;
import org.jzy3d.plot3d.text.renderers.TextBillboardRenderer;

public class DrawableTextBillboard extends DrawableTextWrapper {
  public DrawableTextBillboard(String txt, Coord3d position, Color color) {
    super(txt, position, color, new TextBillboardRenderer());
  }
}
