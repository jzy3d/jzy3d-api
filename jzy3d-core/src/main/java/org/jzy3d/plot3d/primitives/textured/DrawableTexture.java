package org.jzy3d.plot3d.primitives.textured;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox2d;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.rendering.textures.SharedTexture;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * A {@link DrawableTexture} can only mount its texture while the GL2 thread is
 * current, so the best is to let draw() automount texture file the first time
 * the resource is required. When a texture is loaded for the first time, it
 * updates the current view bounds since the texture bounds where up to now
 * unknown and fixed to origin with no width.
 * 
 * A {@link DrawableTexture} requires a color filter (default is white), and a
 * set of coordinates defining the polygon on which the texture should be drawn.
 * 
 * This texture implementation impose the texture to be perpendicular to a
 * {@link PlaneAxis}. It is thus defined with 2d Coordinates in the plane
 * defined by the {{@link PlaneAxis}, axisValue}
 * 
 * @author Martin
 * 
 */
public class DrawableTexture extends AbstractDrawable implements ITranslucent {
    static Logger logger = Logger.getLogger(DrawableTexture.class);
    
    public DrawableTexture(SharedTexture resource) {
        this(resource, PlaneAxis.Z, 0, null, null);
    }

    public DrawableTexture(SharedTexture resource, PlaneAxis orientation) {
        this(resource, orientation, 0, null, null);
    }

    public DrawableTexture(SharedTexture resource, PlaneAxis orientation, float axisValue) {
        this(resource, orientation, axisValue, null, null);
    }

    public DrawableTexture(SharedTexture resource, PlaneAxis orientation, float axisValue, Color filter) {
        this(resource, orientation, axisValue, null, filter);
    }

    public DrawableTexture(SharedTexture resource, PlaneAxis orientation, float axisValue, List<Coord2d> coords) {
        this(resource, orientation, axisValue, coords, null);
    }

    public DrawableTexture(SharedTexture resource, PlaneAxis orientation, float axisValue, List<Coord2d> coords, Color filter) {
        this.alpha = 1.0f;
        this.resource = resource;
        this.axisValue = axisValue;
        this.orientation = orientation;
        if (filter == null)
            this.filter = Color.WHITE.clone();
        else
            this.filter = filter;
        if (coords != null) {
            mapping = coords;
            initBoundsWithMapping();
        } else {
            mapping = getDefaultTextureMapping();
            initBoundsWithMapping();
        }
    }

    public Color getColorFilter() {
        return filter;
    }

    public void setColorFilter(Color filter) {
        this.filter = filter;
    }

    @Override
    public void setAlphaFactor(float a) {
        alpha = a;
    }

    protected void initBoundsWithMapping() {
        BoundingBox2d box = new BoundingBox2d(mapping);
        float enlarge = 1;
        if (orientation == PlaneAxis.X)
            bbox = new BoundingBox3d(axisValue - enlarge, axisValue + enlarge, box.xmin(), box.xmax(), box.ymin(), box.ymax());
        else if (orientation == PlaneAxis.Y)
            bbox = new BoundingBox3d(box.xmin(), box.xmax(), axisValue - enlarge, axisValue + enlarge, box.ymin(), box.ymax());
        else if (orientation == PlaneAxis.Z)
            bbox = new BoundingBox3d(box.xmin(), box.xmax(), box.ymin(), box.ymax(), axisValue - enlarge, axisValue + enlarge);

    }

    protected void initBoundsWithResources() {
        float enlarge = 1;
        if (orientation == PlaneAxis.X)
            bbox = new BoundingBox3d(axisValue - enlarge, axisValue + enlarge, -resource.getHalfWidth(), resource.getHalfWidth(), -resource.getHalfHeight(), resource.getHalfHeight());
        else if (orientation == PlaneAxis.Y)
            bbox = new BoundingBox3d(-resource.getHalfWidth(), resource.getHalfWidth(), axisValue - enlarge, axisValue + enlarge, -resource.getHalfHeight(), resource.getHalfHeight());
        else if (orientation == PlaneAxis.Z)
            bbox = new BoundingBox3d(-resource.getHalfWidth(), resource.getHalfWidth(), -resource.getHalfHeight(), resource.getHalfHeight(), axisValue - enlarge, axisValue + enlarge);

    }

    protected List<Coord2d> getDefaultTextureMapping() {
        List<Coord2d> mapping = new ArrayList<Coord2d>(4);
        mapping.add(new Coord2d(-resource.getHalfWidth(), -resource.getHalfHeight()));
        mapping.add(new Coord2d(+resource.getHalfWidth(), -resource.getHalfHeight()));
        mapping.add(new Coord2d(+resource.getHalfWidth(), +resource.getHalfHeight()));
        mapping.add(new Coord2d(-resource.getHalfWidth(), +resource.getHalfHeight()));
        return mapping;
    }

    /**
     * Must supply the expected size of texture in 3d coordinates.
     * 
     * @return
     */
    public static List<Coord2d> getManualTextureMapping(float width, float height, float xoffset, float yoffset) {
        List<Coord2d> mapping = new ArrayList<Coord2d>(4);
        mapping.add(new Coord2d(xoffset - width / 2, yoffset - height / 2));
        mapping.add(new Coord2d(xoffset + width / 2, yoffset - height / 2));
        mapping.add(new Coord2d(xoffset + width / 2, yoffset + height / 2));
        mapping.add(new Coord2d(xoffset - width / 2, yoffset + height / 2));
        return mapping;
    }

    public static List<Coord2d> getManualTextureMapping(float width, float height) {
        return getManualTextureMapping(width, height, 0, 0);
    }

    public void debugMapping() {
        logger.info("mapping");
        for (Coord2d c : mapping) {
            logger.info(c);
        }
    }

    @Override
    public BoundingBox3d getBounds() {
        return bbox.shift(new Coord3d(planePosition, 0));
    }

    public Coord2d getPlanePosition() {
        return planePosition;
    }

    public void setPlanePosition(Coord2d planePosition) {
        this.planePosition = planePosition;
    }

    public Transform getTextureScale() {
        return textureScale;
    }

    public void setTextureScale(Transform textureScale) {
        this.textureScale = textureScale;
    }

    protected Transform textureScale;

    @Override
    public void draw(Painter painter, GL gl, GLU glu, Camera cam) {
        doTransform(painter, cam);
        if (textureScale != null)
            textureScale.execute(painter, false);

        // Retrieve resource content
        Texture texture = resource.getTexture(gl);
        TextureCoords coords = resource.getCoords();

        // Bind texture & set color filter
        texture.bind(gl);
        painter.colorAlphaFactor(filter, alpha);

        // Draw
        before(painter, gl);

        painter.glBegin(GL2GL3.GL_QUADS);

        if (orientation == PlaneAxis.X) {
        	painter.glTexCoord2f(coords.left(), coords.bottom());
            painter.glVertex3f(axisValue, mapping.get(0).x, mapping.get(0).y);
            painter.glTexCoord2f(coords.right(), coords.bottom());
            painter.glVertex3f(axisValue, mapping.get(1).x, mapping.get(1).y);
            painter.glTexCoord2f(coords.right(), coords.top());
            painter.glVertex3f(axisValue, mapping.get(2).x, mapping.get(2).y);
            painter.glTexCoord2f(coords.left(), coords.top());
            painter.glVertex3f(axisValue, mapping.get(3).x, mapping.get(3).y);
        } else if (orientation == PlaneAxis.Y) {
            painter.glTexCoord2f(coords.left(), coords.bottom());
            painter.glVertex3f(mapping.get(0).x, axisValue, mapping.get(0).y);
            painter.glTexCoord2f(coords.right(), coords.bottom());
            painter.glVertex3f(mapping.get(1).x, axisValue, mapping.get(1).y);
            painter.glTexCoord2f(coords.right(), coords.top());
            painter.glVertex3f(mapping.get(2).x, axisValue, mapping.get(2).y);
            painter.glTexCoord2f(coords.left(), coords.top());
            painter.glVertex3f(mapping.get(3).x, axisValue, mapping.get(3).y);
        } else if (orientation == PlaneAxis.Z) {
            painter.glTexCoord2f(coords.left(), coords.bottom());
            painter.glVertex3f(planePosition.x + mapping.get(0).x, planePosition.y + mapping.get(0).y, axisValue);
            painter.glTexCoord2f(coords.right(), coords.bottom());
            painter.glVertex3f(planePosition.x + mapping.get(1).x, planePosition.y + mapping.get(1).y, axisValue);
            painter.glTexCoord2f(coords.right(), coords.top());
            painter.glVertex3f(planePosition.x + mapping.get(2).x, planePosition.y + mapping.get(2).y, axisValue);
            painter.glTexCoord2f(coords.left(), coords.top());
            painter.glVertex3f(planePosition.x + mapping.get(3).x, planePosition.y + mapping.get(3).y, axisValue);
        }

        painter.glEnd();
        
        after(painter, gl);
    }

    protected void before(Painter painter, GL gl) {
    	painter.glPushMatrix();
        // gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT,GL2.GL_NICEST);
        painter.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

        // gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        // gl.glPolygonOffset(1.0f, 1.0f);

        // gl.glEnable(GL2.GL_BLEND);
        // gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // gl.glEnable(GL2.GL_ALPHA_TEST);
        // gl.glAlphaFunc(GL2.GL_GREATER, 0);

        painter.glEnable(GL.GL_TEXTURE_2D);
        painter.glTexEnvf(GL.GL_TEXTURE_2D, GL2ES1.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
    }

    protected void after(Painter painter, GL gl) {
        painter.glDisable(GL.GL_TEXTURE_2D);
        painter.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);
        // gl.glDisable(GL2.GL_ALPHA);
        // gl.glDisable(GL2.GL_BLEND);
        painter.glPopMatrix();
    }

    public SharedTexture getResource() {
        return resource;
    }

    public void setResource(SharedTexture resource) {
        this.resource = resource;
    }

    @Override
    public void applyGeometryTransform(Transform transform) {
        Logger.getLogger(DrawableTexture.class).warn("not implemented");
    }

    @Override
    public void updateBounds() {
        Logger.getLogger(DrawableTexture.class).warn("not implemented");
    }

    protected SharedTexture resource;
    protected PlaneAxis orientation;
    protected float texMatMix[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    protected Color filter;
    protected float axisValue;
    protected List<Coord2d> mapping;
    protected float alpha;
    protected Coord2d planePosition = new Coord2d();
}
