package org.jzy3d.plot3d.text.drawable;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.text.renderers.TextRenderer;

public class DrawableText extends DrawableTextWrapper {
  public DrawableText(String txt, Coord3d position, Color color) {
    super(txt, position, color, new TextRenderer());
    setPositionColor(color);
  }
  
  public TextRenderer getRenderer() {
    return (TextRenderer) renderer;
  }
  
  public boolean isShowPosition() {
    return getRenderer().isShowPosition();
  }

  public void setShowPosition(boolean showPosition) {
    getRenderer().setShowPosition(showPosition);
  }

  public Color getPositionColor() {
    return getRenderer().getPositionColor();
  }

  public void setPositionColor(Color positionColor) {
    getRenderer().setPositionColor(positionColor);
  }

  public float getPositionWidth() {
    return getRenderer().getPositionWidth();
  }

  public void setPositionWidth(float positionWidth) {
    getRenderer().setPositionWidth(positionWidth);
  }
}
