package org.jzy3d.plot3d.primitives.axis.layout.fonts;

import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;

public interface IFontSizePolicy {
  public Font apply(AxisLayout layout);
}
