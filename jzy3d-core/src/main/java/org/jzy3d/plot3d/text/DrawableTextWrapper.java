package org.jzy3d.plot3d.text;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.transform.Transform;

/**
 * A {@link DrawableTextWrapper} wraps any text rendered by an {@link ITextRenderer} into an
 * {@link Drawable}, meaning it can be injected in the scene graph, and be transformed.
 * 
 * @author Martin Pernollet
 */
public class DrawableTextWrapper extends Drawable {
  protected String txt;
  protected Coord3d position;
  protected Horizontal halign;
  protected Vertical valign;
  protected Color color;
  protected Coord2d screenOffset = new Coord2d();
  protected Coord3d sceneOffset = new Coord3d();

  protected ITextRenderer renderer;

  public DrawableTextWrapper(ITextRenderer renderer) {
    this("", new Coord3d(), Color.BLACK, renderer);
  }

  public DrawableTextWrapper(String txt, Coord3d position, Color color, ITextRenderer renderer) {
    super();
    this.renderer = renderer;
    configure(txt, position, color, Horizontal.CENTER, Vertical.CENTER);
  }

  /*******************************************************************************************/

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);
    BoundingBox3d box =
        renderer.drawText(painter, Font.Helvetica_12, txt, position, 0, halign, valign, color, screenOffset, sceneOffset);
    if (box != null)
      bbox = box.scale(new Coord3d(1 / 10, 1 / 10, 1 / 10));
    else
      bbox = null;
  }

  @Override
  public BoundingBox3d getBounds() {
    return bbox;
  }

  public void configure(String txt, Coord3d position, Color color, Horizontal ha, Vertical va) {
    setText(txt);
    setPosition(position);
    setColor(color);
    setHalign(ha);
    setValign(va);
  }

  public String getText() {
    return txt;
  }

  public void setText(String txt) {
    this.txt = txt;
  }

  public void setPosition(Coord3d position) {
    this.position = position;
  }

  public Coord3d getPosition() {
    return this.position;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Horizontal getHalign() {
    return halign;
  }

  public void setHalign(Horizontal halign) {
    this.halign = halign;
  }

  public Vertical getValign() {
    return valign;
  }

  public void setValign(Vertical valign) {
    this.valign = valign;
  }

  public Coord2d getScreenOffset() {
    return screenOffset;
  }

  public void setScreenOffset(Coord2d screenOffset) {
    this.screenOffset = screenOffset;
  }

  public Coord3d getSceneOffset() {
    return sceneOffset;
  }

  public void setSceneOffset(Coord3d sceneOffset) {
    this.sceneOffset = sceneOffset;
  }

  @Override
  public String toString() {
    return "(TextBitmapDrawable) \"" + txt + "\" at " + position.toString() + " halign=" + halign
        + " valign=" + valign;
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    position.set(transform.compute(position));
    updateBounds();
  }

  @Override
  public void updateBounds() {
    // given after drawing
  }
}
