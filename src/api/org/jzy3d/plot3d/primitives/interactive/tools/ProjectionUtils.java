package org.jzy3d.plot3d.primitives.interactive.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.scene.Decomposition;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.Camera;



public class ProjectionUtils {
	public static List<PolygonProjection> project(Chart chart){
		return project(chart.getView().getCurrentGL(), new GLU(), chart.getView().getCamera(), chart.getScene().getGraph());
	}
	
	public static List<PolygonProjection> project(GL2 gl, GLU glu, Camera cam, Graph g){
		return project(gl, glu, cam, g.getAll());
	}
	
	public static List<PolygonProjection> project(GL2 gl, GLU glu, Camera cam, List<AbstractDrawable> list){
		return project(gl, glu, cam, Decomposition.getDecomposition(list));
	}
	
	public static List<PolygonProjection> project(GL2 gl, GLU glu, Camera cam, AbstractComposite c){
		ArrayList<AbstractDrawable> monotypes = Decomposition.getDecomposition(c);
		return project(gl, glu, cam, monotypes);
	}
	
	public static List<PolygonProjection> project(GL2 gl, GLU glu, Camera cam, ArrayList<AbstractDrawable> monotypes){
		final TicToc t = new TicToc();
		String report = "";
		
		// prepare a more efficient datastructure
		ArrayList<ArrayList<Coord3d>> polygons = new ArrayList<ArrayList<Coord3d>>(monotypes.size()); 
		ArrayList<ArrayList<Color>> colors = new ArrayList<ArrayList<Color>>(monotypes.size()); 		
		for(AbstractDrawable d: monotypes){
			if( d instanceof Polygon ){
				polygons.add( ProjectionUtils.getCoordinatesAsArrayList( (Polygon)d ) );
				colors.add( ProjectionUtils.getColorsAsArrayList( (Polygon)d ) );
			}
			/*else
				throw new RuntimeException("Only polygons are supported, not:" + d.getClass());*/
		}
		
		// project
		t.tic();
		ArrayList<ArrayList<Coord3d>> projections = cam.modelToScreen(gl, glu, polygons);
		t.toc(); report += " Projections :" + t.elapsedMilisecond();
		
		// gather polygon and its colors in a data structure
		int k=0;
		List<PolygonProjection> polygonProjections = new ArrayList<PolygonProjection>();
		for(ArrayList<Coord3d> p: projections){
			polygonProjections.add( new PolygonProjection(p, colors.get(k++)) );
		}
		
		// sort according to z
		t.tic();
		Collections.sort(polygonProjections, new ProjectionComparator());
		t.toc(); report += " Sort :" + t.elapsedMilisecond();
		System.out.println(report);
		return polygonProjections;
	}
	
	/************************************************/
	
	public static ArrayList<Coord3d> getCoordinatesAsArrayList(Polygon p){
		ArrayList<Coord3d> coords = new ArrayList<Coord3d>(p.size());
		for(int i=0; i<p.size(); i++ )
			coords.add( p.get(i).xyz );
		return coords;
	}
	
	public static List<Coord3d> getCoordinates(Polygon p){
		List<Coord3d> coords = new Vector<Coord3d>(p.size());
		for(int i=0; i<p.size(); i++ )
			coords.add( p.get(i).xyz );
		return coords;
	}
	
	public static ArrayList<Color> getColorsAsArrayList(Polygon p){
		ArrayList<Color> colors = new ArrayList<Color>(4);
		for(int i=0; i<p.size(); i++ ){
			if( p.getColorMapper()!= null )
				colors.add( p.getColorMapper().getColor(p.get(i).xyz) ); // TODO: cache, maybe in polygon itself
			else
				colors.add( p.get(i).rgb );
		}
		return colors;
	}
	
	public static List<Color> getColors(Polygon p){
		List<Color> colors = new Vector<Color>(4);
		for(int i=0; i<p.size(); i++ ){
			if( p.getColorMapper()!= null )
				colors.add( p.getColorMapper().getColor(p.get(i).xyz) ); // TODO: cache, maybe in polygon itself
			else
				colors.add( p.get(i).rgb );
		}
		return colors;
	}
}
