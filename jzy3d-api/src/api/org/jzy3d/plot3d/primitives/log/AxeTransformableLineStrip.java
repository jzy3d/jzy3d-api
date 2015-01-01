package org.jzy3d.plot3d.primitives.log;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class AxeTransformableLineStrip extends LineStrip {
	

	 public AxeTransformableLineStrip(SpaceTransformer transformers) {
	        this(2, transformers);
	    }

	    public AxeTransformableLineStrip(int n, SpaceTransformer transformers) {
	    	this.transformers = transformers;
	        points = new ArrayList<Point>(n);
	        bbox = new BoundingBox3d();
	        for (Point p : points)
	            bbox.add(p);
	        setWireframeColor(null);
	    }

	    public AxeTransformableLineStrip(List<Coord3d> coords, SpaceTransformer transformers) {
	        this(transformers);
	        for (Coord3d c : coords) {
	            Point p = new Point(c);
	            add(p);
	        }
	    }

	    public AxeTransformableLineStrip(Point c1, Point c2,SpaceTransformer transformers) {
	        this(transformers);
	        add(c1);
	        add(c2);
	    }

	    public void drawLineGLES2() {
	        GLES2CompatUtils.glBegin(GL.GL_LINE_STRIP);

	        if (wfcolor == null) {
	            for (Point p : points) {
	                GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
	                GLES2CompatUtils.glVertex3f(transformers.getX().compute(p.xyz.x), transformers.getY().compute(p.xyz.y), transformers.getZ().compute(p.xyz.z));
	            }
	        } else {
	            for (Point p : points) {
	                GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
	                GLES2CompatUtils.glVertex3f(transformers.getX().compute(p.xyz.x), transformers.getY().compute(p.xyz.y), transformers.getZ().compute(p.xyz.z));
	            }
	        }
	        GLES2CompatUtils.glEnd();
	    }

	    public void drawLineGL2(GL gl) {
	        gl.getGL2().glBegin(GL.GL_LINE_STRIP);

	        if (wfcolor == null) {
	            for (Point p : points) {
	                gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
	                GlVertexExecutor.Vertex(gl, new Coord3d(p.xyz.x, p.xyz.y, p.xyz.z), transformers);
	                //System.out.println(p.xyz + p.rgb.toString());
	            }
	        } else {
	            for (Point p : points) {
	                gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
	                GlVertexExecutor.Vertex(gl, new Coord3d(p.xyz.x, p.xyz.y, p.xyz.z), transformers);
	                //System.out.println(p.xyz + wfcolor.toString());
	            }
	        }
	        gl.getGL2().glEnd();
	    }

	    public void drawPoints(GL gl) {
	        if (showPoints) {
	            if (gl.isGL2()) {
	                drawPointsGL2(gl);
	            } else {
	                drawPointsGLES2();
	            }
	        }
	    }

	    public void drawPointsGLES2() {
	        GLES2CompatUtils.glBegin(GL.GL_POINTS);

	        for (Point p : points) {
	            if (wfcolor == null)
	                GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
	            else
	                GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
	            	GLES2CompatUtils.glVertex3f(transformers.getX().compute(p.xyz.x), transformers.getY().compute(p.xyz.y), transformers.getZ().compute(p.xyz.z));
	        }

	        GLES2CompatUtils.glEnd();
	    }

	    public void drawPointsGL2(GL gl) {
	        gl.getGL2().glBegin(GL.GL_POINTS);

	        for (Point p : points) {
	            if (wfcolor == null)
	                gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
	            else
	                gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
	            	GlVertexExecutor.Vertex(gl, new Coord3d(p.xyz.x, p.xyz.y, p.xyz.z), transformers);
	        }

	        gl.getGL2().glEnd();
	    }
	    
	    
	    public void updateBounds() {
	        bbox.reset();
	        for (Point p : points)
	            bbox.add(transform.compute(p.xyz));
	    }
		SpaceTransformer transformers;
		

}
