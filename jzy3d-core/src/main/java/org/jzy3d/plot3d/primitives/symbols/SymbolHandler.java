package org.jzy3d.plot3d.primitives.symbols;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * {@link SymbolHandler}s are used to configure a symbol when rendering a {@link LineStrip}.
 * 
 * @author martin
 *
 */
public abstract class SymbolHandler {
  protected List<Drawable> symbols = null;
  protected SpaceTransformer spaceTransformer;

  public SymbolHandler() {
    this(10);
  }

  public SymbolHandler(int n) {
    symbols = new ArrayList<Drawable>(n);
  }


  public void clear() {
    symbols.clear();

  }

  public abstract void addSymbolOn(Point point);


  public void drawSymbols(IPainter painter) {
    for (Drawable d : symbols) {
      d.setSpaceTransformer(spaceTransformer);
      d.draw(painter);
    }
  }


  public SpaceTransformer getSpaceTransformer() {
    return spaceTransformer;
  }


  public void setSpaceTransformer(SpaceTransformer spaceTransformer) {
    this.spaceTransformer = spaceTransformer;
  }
}
