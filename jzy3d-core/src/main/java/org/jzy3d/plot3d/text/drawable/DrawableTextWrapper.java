package org.jzy3d.plot3d.text.drawable;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.text.ITextRenderer;
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
  protected float rotation = 0;

  protected ITextRenderer renderer;

  protected AxisLayout axisLayout;
  protected Font defaultFont = Font.Helvetica_12;
  
  protected boolean pointDisplayed = false;
  protected Point point;

  protected boolean flipHorizontalAlignWithViewpoint = false;


  public DrawableTextWrapper(ITextRenderer renderer) {
    this("", new Coord3d(), Color.BLACK, renderer);
  }

  public DrawableTextWrapper(String txt, Coord3d position, Color color, ITextRenderer renderer) {
    super();
    this.renderer = renderer;
    this.point = new Point();
    
    configure(txt, position, color, Horizontal.CENTER, Vertical.CENTER);
    

  }
  
  

  /*******************************************************************************************/

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);

    Font font = defaultFont;
    if (axisLayout != null) {
      font = axisLayout.getFont();
    }
    
    
    Horizontal fixedHAlign = flipHorizontalAlignIfEnabled(painter);
    //Coord3d fixedSceneOffset = flipHorizontalAlignWithViewpoint?sceneOffset/*.mul(-1)*/:sceneOffset;
    
    BoundingBox3d box = renderer.drawText(painter, font, txt, position, rotation, fixedHAlign, valign,
        color, screenOffset, sceneOffset);
    if (box != null)
      bbox = box.scale(new Coord3d(1 / 10, 1 / 10, 1 / 10));
    else
      bbox = null;
    
    point.draw(painter);
  }

  public Horizontal flipHorizontalAlignIfEnabled(IPainter painter) {
    boolean left = painter.getCamera().side(position);
    
    Horizontal fixedHAlign = halign;
    if(left && flipHorizontalAlignWithViewpoint) {
      if(halign==Horizontal.LEFT) {
        fixedHAlign = Horizontal.RIGHT;
      }
      else if(halign==Horizontal.RIGHT) {
        fixedHAlign = Horizontal.LEFT;
      }

    }
    return fixedHAlign;
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
    this.point.setCoord(position);
  }

  public Coord3d getPosition() {
    return this.position;
  }

  public void setColor(Color color) {
    this.color = color;
  }
  
  public boolean isFlipHorizontalAlignWithViewpoint() {
    return flipHorizontalAlignWithViewpoint;
  }

  public void setFlipHorizontalAlignWithViewpoint(boolean flipHorizontalAlignWithViewpoint) {
    this.flipHorizontalAlignWithViewpoint = flipHorizontalAlignWithViewpoint;
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

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }



  public AxisLayout getAxisLayout() {
    return axisLayout;
  }

  /**
   * The axis layout acts as main font provider, to allow all text renderer to use the same current
   * font than the one selected for axis and colorbar texts.
   * 
   * To for this drawable text to use a given font, set this to null and configure
   * {@link #setDefaultFont(Font)}
   */
  public void setAxisLayout(AxisLayout axisLayout) {
    this.axisLayout = axisLayout;
  }

  public Font getDefaultFont() {
    return defaultFont;
  }

  public void setDefaultFont(Font defaultFont) {
    this.defaultFont = defaultFont;
  }
  
  public Point getPoint() {
    return point;
  }
  
  public boolean isPointDisplayed() {
    return pointDisplayed;
  }

  public void setPointDisplayed(boolean pointDisplayed) {
    this.pointDisplayed = pointDisplayed;
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
