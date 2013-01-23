package org.jzy3d.plot3d.rendering.view.annotation;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * Record the camera position at regular interval and draws
 * the path based on these point accumulation.
 * 
 * @author Martin
 */
public class CameraPathAnnotation extends LineStrip{
    public CameraPathAnnotation(View view, Color color) {
        super();
        this.view = view;
        setWireframeColor(color);
        setWidth(1);
        timer.tic();
    }
    
    @Override
    public void draw(GL2 gl, GLU glu, Camera cam) {
        updateCameraPath();
        
        super.draw(gl, glu, cam);
    }

    public void updateCameraPath() {
        if(timer.toc()>0.1){
            Coord3d scaling = view.getLastViewScaling();
            Coord3d xyz = view.getCamera().getEye().div(scaling);
            Point p = new Point(xyz, getWireframeColor());
            add(p); // should synchronize
            timer.tic();
        }
    }
    
    protected View view;
    protected TicToc timer = new TicToc();
}
