package org.jzy3d.plot3d.builder;

import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.builder.concrete.RingTessellator;
import org.jzy3d.plot3d.builder.delaunay.DelaunayTessellator;
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.Shape;


public class Builder {
	public static Shape buildOrthonormal(OrthonormalGrid grid, Mapper mapper) {
		OrthonormalTessellator tesselator = new OrthonormalTessellator();
		return (Shape) tesselator.build(grid.apply(mapper));
	}
	
	public static Shape buildRing(OrthonormalGrid grid, Mapper mapper, float ringMin, float ringMax) {
        RingTessellator tesselator = new RingTessellator(ringMin, ringMax, new ColorMapper(new ColorMapRainbow(), 0, 1), Color.BLACK);
        return (Shape) tesselator.build(grid.apply(mapper));
    }

	public static Shape buildRing(OrthonormalGrid grid, Mapper mapper, float ringMin, float ringMax, ColorMapper cmap, Color factor) {
        RingTessellator tesselator = new RingTessellator(ringMin, ringMax, cmap, factor);
        return (Shape) tesselator.build(grid.apply(mapper));
    }
	
	public static Shape buildDelaunay(List<Coord3d> coordinates) {
	    DelaunayTessellator tesselator = new DelaunayTessellator();
	    return (Shape) tesselator.build(coordinates);
    }
		
	/* BIG SURFACE */
	
	public static CompileableComposite buildOrthonormalBig(OrthonormalGrid grid, Mapper mapper) {
        Tessellator tesselator = new OrthonormalTessellator();
        Shape s1 = (Shape) tesselator.build(grid.apply(mapper));
        return buildComposite(applyStyling(s1));
    }
	
	public static Shape applyStyling(Shape s) {
        s.setColorMapper(new ColorMapper(colorMap, s.getBounds().getZmin(), s.getBounds().getZmax()));
        s.setFaceDisplayed(faceDisplayed);
        s.setWireframeDisplayed(wireframeDisplayed);
        s.setWireframeColor(wireframeColor);
        return s;
    }

    protected static CompileableComposite buildComposite(Shape s) {
        CompileableComposite sls = new CompileableComposite();
        sls.add(s.getDrawables());
        sls.setColorMapper(new ColorMapper(colorMap, sls.getBounds().getZmin(), sls.getBounds().getZmax(), colorFactor));
        sls.setFaceDisplayed(s.getFaceDisplayed());
        sls.setWireframeDisplayed(s.getWireframeDisplayed());
        sls.setWireframeColor(s.getWireframeColor());
        return sls;
    }
    
    protected static IColorMap colorMap = new ColorMapRainbow();
    protected static Color colorFactor = new Color(1, 1, 1, 1f);
    protected static boolean faceDisplayed = true;
    protected static boolean wireframeDisplayed = false;
    protected static Color wireframeColor = Color.BLACK;
}
