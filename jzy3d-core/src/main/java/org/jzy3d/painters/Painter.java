package org.jzy3d.painters;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/** 
 * 1.0 way of drawing : 
 * - isolation of actual engine
 * - no dependency off API on third party
 * - reverse dependency of delaunay
 * 
 * - enrichir IAxe avec toutes les méthodes de axebox
 * 
 * implementation embed
 * - GL
 * - GLU
 * - View
 * - Camera
*/
public interface Painter {
    enum Geometry{
        POINT, LINE, POLYGON
    }
    
    public void begin(Geometry geometry);
    public void end();
    public void coord(Coord3d coord, SpaceTransformer transform);

    public void color(Color color);
    
    public void transform(Transform transform, boolean loadIdentity);
    
    
    // technical
    public void culling(boolean status);
    public void lights(boolean status);
    public void polygonOffset(boolean status);
    
    
    // ease
    public Camera getCamera();
    public void setCamera(Camera camera);

    public View getView();
    public IScreenCanvas getCanvas();
    public Scene getScene();
    public IAxe getAxe();
    
    
}
