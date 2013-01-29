package org.jzy3d.plot3d.rendering.view.annotation;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.AbstractGeometry;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

/**
 * Draws all point distance to camera eye.
 * 
 * The camera is represented as a red point.
 * 
 * @author Martin
 */
public class CameraDistanceAnnotation extends Point{
    public CameraDistanceAnnotation(View view, Color color) {
        super();
        this.view = view;
        setColor(color);
        setWidth(5);
    }
    
    @Override
    public void draw(GL2 gl, GLU glu, Camera cam) {
        computeCameraPosition();
        doTransform(gl, glu, cam);
        
        doDrawCamera(gl, glu, cam);
        
        Halign h = Halign.RIGHT;
        Valign v = Valign.CENTER;
        Coord2d screenOffset = new Coord2d(10,0);
        Color colorBary = Color.BLACK;
        Color colorPt = Color.GRAY.clone();
        colorPt.alphaSelf(0.5f);
        
        Graph graph = view.getScene().getGraph();
        AbstractOrderingStrategy strat = graph.getStrategy();
        for(AbstractDrawable drawables: graph.getDecomposition()){
            double d = strat.score(drawables);
            txt.drawText(gl, glu, view.getCamera(), Utils.num2str(d, 4), drawables.getBarycentre(), h, v, colorBary, screenOffset);
            
            if(drawables instanceof AbstractGeometry){
                Polygon p = (Polygon)drawables;
                for(Point pt: p.getPoints()){
                    //Point pt2 = pt.clone();
                    d = strat.score(pt);
                    txt.drawText(gl, glu, view.getCamera(), Utils.num2str(d, 4), pt.getCoord(), h, v, colorPt, screenOffset);
                }
            }
        }
    }

    public void computeCameraPosition() {
        Coord3d scaling = view.getLastViewScaling().clone();
        xyz = view.getCamera().getEye().clone();
        xyz = xyz.div(scaling);
    }
    
    public void doDrawCamera(GL2 gl, GLU glu, Camera cam){
        gl.glPointSize(width);
        gl.glBegin(GL2.GL_POINTS);
        gl.glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
        gl.glVertex3f(xyz.x, xyz.y, xyz.z);
        gl.glEnd();
    }

    protected View view;    
    protected ITextRenderer txt = new TextBitmapRenderer();
}
