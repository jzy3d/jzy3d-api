package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;

public class Surface {
    public static Shape shape(Mapper f1, Range range, int steps, IColorMap colormap, float alpha) {
        final Shape surface = Builder.buildOrthonormal(f1, range, steps);
        surface.setColorMapper(new ColorMapper(colormap, surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1,1, 1,alpha)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);
        return surface;
    }
}
