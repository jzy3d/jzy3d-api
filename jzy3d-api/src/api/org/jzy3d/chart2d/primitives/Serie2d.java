package org.jzy3d.chart2d.primitives;

import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;

public interface Serie2d {
    public void clear();
    public void add(float x, float y);
    public void add(double x, double y);
    public void add(Coord2d c);
    public void add(List<Coord2d> c);
    public String getName();
    
    public void setColor(Color color);
    public Color getColor();
    
    public AbstractDrawable getDrawable();
    
    public enum Type{
        LINE, SCATTER
    }
}
