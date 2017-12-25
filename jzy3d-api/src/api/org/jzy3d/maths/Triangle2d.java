package org.jzy3d.maths;

public class Triangle2d {
    public Coord2d a;
    public Coord2d b;
    public Coord2d c;
    
    public Triangle2d() {
    }
    
    public Triangle2d(Coord2d a, Coord2d b, Coord2d c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    public Coord2d getMedianAB(){
        return a.mean(b);
    }
    
    public Coord2d getMedianBC(){
        return b.mean(c);
    }
    
    public Coord2d getMedianCA(){
        return c.mean(a);
    }
}
