package org.jzy3d.plot3d.builder;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot2d.primitives.LineSerie2d;
import org.jzy3d.plot2d.primitives.LineSerie2dSplitted;
import org.jzy3d.plot2d.primitives.ScatterPointSerie2d;
import org.jzy3d.plot2d.primitives.ScatterSerie2d;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot2d.primitives.Serie2d.Type;

public class SerieBuilder {
  
  public Serie2d line(String name, Func2D func, Range xRange, int steps) {
    return line(name, func, xRange, steps, true, true);
  }

  public Serie2d line(String name, Func2D func, Range xRange, int steps, boolean includeXMin, boolean includeXMax) {
    Serie2d serie = newSerie(name, Type.LINE);
    serie.setColor(Color.BLACK);
    return apply(serie, func, xRange, steps, includeXMin, includeXMax);
  }

  public Serie2d apply(Serie2d serie, Func2D func, Range xRange, int steps, boolean includeXMin, boolean includeXMax) {
    double step = xRange.getRange() / steps;
    
    for (double x = xRange.getMin(); x <= xRange.getMax(); x += step) {
      double v;
      
      // shift lower bound 
      if(!includeXMin && x==xRange.getMin()) {
        v = func.f(x+Double.MIN_VALUE);
      }
      // shift upper bound
      else if(!includeXMax && x==xRange.getMax()) {
        v = func.f(x-Double.MIN_VALUE);
      }
      // get raw value
      else {
        v = func.f(x);
      }
      
      //System.out.println(x + ", " + v);
      
      serie.add(x, v);

    }
    return serie;
  }
  
  protected Serie2d newSerie(String name, Serie2d.Type type) {
    if (Serie2d.Type.LINE.equals(type))
      return new LineSerie2d(name);
    else if (Serie2d.Type.LINE_ON_OFF.equals(type))
      return new LineSerie2dSplitted(name);
    else if (Serie2d.Type.SCATTER.equals(type))
      return new ScatterSerie2d(name);
    else if (Serie2d.Type.SCATTER_POINTS.equals(type))
      return new ScatterPointSerie2d(name);
    else
      throw new IllegalArgumentException("Unsupported serie type " + type);
  }

  public List<Coord3d> apply(Func2D func, Range xRange, int steps) {
    return apply(func, xRange, steps, true, true);
  }
  
  public List<Coord3d> apply(Func2D func, Range xRange, int steps, boolean includeXMin, boolean includeXMax) {
    List<Coord3d> out = new ArrayList<>();
    
    double step = xRange.getRange() / steps;

    
    
    for (double x = xRange.getMin(); x <= xRange.getMax(); x += step) {
      // shift lower bound 
      if(!includeXMin && x==xRange.getMin()) {
        out.add(new Coord3d(x+Double.MIN_VALUE, func.f(x+Double.MIN_VALUE)));
      }
      // shift upper bound
      else if(!includeXMax && x==xRange.getMax()) {
        out.add(new Coord3d(x-Double.MIN_VALUE, func.f(x-Double.MIN_VALUE)));
      }
      else {
        out.add(new Coord3d(x, func.f(x)));
      }

    }
    return out;
  }

}
