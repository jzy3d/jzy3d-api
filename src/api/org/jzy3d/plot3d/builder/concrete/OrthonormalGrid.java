package org.jzy3d.plot3d.builder.concrete;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Grid;
import org.jzy3d.plot3d.builder.Mapper;


public class OrthonormalGrid extends Grid{

	public OrthonormalGrid(Range xyrange, int xysteps){
		super(xyrange, xysteps);
	}
	
	public OrthonormalGrid(Range xrange, int xsteps, Range yrange, int ysteps){
		super(xrange, xsteps, yrange, ysteps);
	}
	
	public List<Coord3d> apply(Mapper mapper) {
        double xstep = xrange.getRange() / (double)(xsteps-1);
        double ystep = yrange.getRange() / (double)(ysteps-1);
       
        List<Coord3d> output = new ArrayList<Coord3d>(xsteps*ysteps);

        for(int xi=0; xi<xsteps; xi++){
            for(int yi=0; yi<ysteps; yi++){
                double x = xrange.getMin() + xi * xstep;
                double y = yrange.getMin() + yi * ystep;
                output.add( new Coord3d(x, y, mapper.f(x, y) ) );
            }
        }
        return output;
    }
	
	/* The former method that implied an ever centered surface.
	  
	public List<Coord3d> apply(Mapper mapper) {
	double xstep = xrange.getRange() / (double)xsteps;
	double ystep = yrange.getRange() / (double)ysteps;
	
	List<Coord3d> output = new ArrayList<Coord3d>((xsteps-1)*(ysteps-1));
	
	for(int xi=-(xsteps-1)/2; xi<=(xsteps-1)/2; xi++){
		for(int yi=-(ysteps-1)/2; yi<=(ysteps-1)/2; yi++){
			output.add( new Coord3d(xi*xstep, yi*ystep, mapper.f(xi*xstep, yi*ystep) ) );
		}
	}
	return output;
}*/
}
