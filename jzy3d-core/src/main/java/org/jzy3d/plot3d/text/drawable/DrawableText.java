package org.jzy3d.plot3d.text.drawable;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.text.renderers.TextRenderer;

public class DrawableText extends DrawableTextWrapper {
  public DrawableText(String txt, Coord3d position, Color color) {
    super(txt, position, color, new TextRenderer());
  }
}
