package org.jzy3d.chart2d.primitives;

import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.ConcurrentLineStrip;

public class LineSerie2d implements Serie2d {
    protected ConcurrentLineStrip line = new ConcurrentLineStrip();

    protected String name;
    
    public LineSerie2d(String name){
        this.name = name;
    }
    
    @Override
    public void add(float x, float y) {
        line.add(new Point(new Coord3d(x, y, 0)));
    }

    @Override
    public void add(double x, double y) {
        line.add(new Point(new Coord3d(x, y, 0)));
    }

    @Override
    public void add(Coord2d c) {
        line.add(new Point(new Coord3d(c.x, c.y, 0)));
    }

    @Override
    public void add(List<Coord2d> c) {
        for(Coord2d c2: c){
            line.add(new Point(new Coord3d(c2.x, c2.y, 0)));
        }
    }
    
    @Override
    public void setColor(Color color){
        line.setWireframeColor(color);
    }
    
    @Override
    public Color getColor(){
        return line.getWireframeColor();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ConcurrentLineStrip getDrawable() {
        return line;
    }
}

