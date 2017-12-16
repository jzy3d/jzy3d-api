package org.jzy3d.painters;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class NativeEmbeddedPainter implements Painter{
    protected GL gl;
    protected GLU glu = new GLU();
    protected GLUT glut = new GLUT();
    protected Camera camera;

    @Override
    public void begin(Geometry geometry) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void end() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void coord(Coord3d coord, SpaceTransformer transform) {
        if(transform==null){
            GLES2CompatUtils.glVertex3f(coord.x, coord.y, coord.z);            
        }
        else{
            GLES2CompatUtils.glVertex3f(transform.getX().compute(coord.x), transform.getY().compute(coord.y),transform.getZ().compute(coord.z));
        }
    }

    @Override
    public void color(Color color) {
        GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
    }

    @Override
    public void culling(boolean status) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void lights(boolean status) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void polygonOffset(boolean status) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public View getView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public IScreenCanvas getCanvas() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Scene getScene() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IAxe getAxe() {
        // TODO Auto-generated method stub
        return null;
    }

    
    /* */
    

    @Override
    public void transform(Transform transform, boolean loadIdentity) {
        transform.execute(gl, loadIdentity);        
    }
}
