package org.jzy3d.plot3d.primitives.vbo.drawable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLPointerFunc;
import javax.media.opengl.glu.GLU;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.vbo.buffers.FloatVBO;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Rotator;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.common.nio.Buffers;

/**
 * A {@link DrawableVBO} is able to efficiently draw a large collection of
 * geometries.
 * 
 * The user must provide a loader that will be later called when the GL context
 * requires loading the VBO data in GPU memory.
 * 
 * The loader might freely make settings on the drawable as it is called with a
 * reference to the {@link DrawableVBO} is will be loading data into.
 * 
 * One can separate data and appearance by setting geometry settings out of
 * loading process.
 * 
 * DrawableVBO shape1 = new DrawableVBO(new MemoryVBOLoader(getScatter(size)));
 * shape1.setGeometry(GL.GL_POINTS); shape1.setColor(Color.WHITE);
 * 
 * @author Martin Pernollet
 */
public class DrawableVBO extends AbstractDrawable implements IGLBindedResource {
    protected int geometry = GL.GL_TRIANGLES;
    protected float width = 1;
    protected Quality quality = Quality.Nicest;
    
    protected int colorChannelNumber = 3;

    public DrawableVBO(IGLLoader<DrawableVBO> loader) {
        this.loader = loader;
    }

    @Override
    public boolean hasMountedOnce() {
        return hasMountedOnce;
    }
    
    @Override
    public void mount(GL gl) {
        try {
            loader.load(gl, this);
            hasMountedOnce = true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(DrawableVBO.class).error(e, e);
        }
    }

    // element array buffer is an index:
    // @see
    // http://www.opengl-tutorial.org/intermediate-tutorials/tutorial-9-vbo-indexing/
    @Override
    public void draw(GL gl, GLU glu, Camera cam) {
        if (hasMountedOnce) {
            doTransform(gl, glu, cam);
            configure(gl);
            doDrawElements(gl);
            doDrawBounds(gl, glu, cam);
        }
    }
    
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
    
    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    protected void doDrawElements(GL gl) {
        if (gl.isGL2()) {
            doBindGL2(gl);
            pointers(gl);
            color(gl);
            enable(gl);
            applyWidth(gl);
            applyQuality(gl);
            applyVertices(gl);
            disable(gl);
            disableColor(gl);
        } else {
            GLES2CompatUtils.glBindBuffer(GL.GL_ARRAY_BUFFER, arrayName[0]);
            GLES2CompatUtils.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementName[0]);
            GLES2CompatUtils.glVertexPointer(dimensions, GL.GL_FLOAT, byteOffset, pointer);
            GLES2CompatUtils.glNormalPointer(GL.GL_FLOAT, byteOffset, normalOffset);
            GLES2CompatUtils.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            GLES2CompatUtils.glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
            GLES2CompatUtils.glDrawElements(getGeometry(), size, GL.GL_UNSIGNED_INT, pointer);
            GLES2CompatUtils.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementName[0]);
            GLES2CompatUtils.glBindBuffer(GL.GL_ARRAY_BUFFER, arrayName[0]);
            GLES2CompatUtils.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            GLES2CompatUtils.glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
        }
    }

    private void disableColor(GL gl) {
        if (hasColorBuffer) {
            gl.getGL2().glDisableClientState(GL2.GL_COLOR_ARRAY);
        }
    }

    private void pointers(GL gl) {
        gl.getGL2().glVertexPointer(dimensions, GL.GL_FLOAT, byteOffset, pointer);
        gl.getGL2().glNormalPointer(GL.GL_FLOAT, byteOffset, normalOffset);
    }

    private void color(GL gl) {
        if (hasColorBuffer) {
            // int bo = 6 * Buffers.SIZEOF_FLOAT;
            int p = 3 * Buffers.SIZEOF_FLOAT;
            gl.getGL2().glEnableClientState(GL2.GL_COLOR_ARRAY);
            gl.getGL2().glColorPointer(colorChannelNumber, GL.GL_FLOAT, byteOffset, p);
        }
    }

    private void enable(GL gl) {
        gl.getGL2().glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
        gl.getGL2().glEnableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
    }

    private void disable(GL gl) {
        gl.getGL2().glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
        gl.getGL2().glDisableClientState(GLPointerFunc.GL_NORMAL_ARRAY);
    }

    private void applyVertices(GL gl) {
        gl.getGL2().glDrawElements(getGeometry(), size, GL.GL_UNSIGNED_INT, pointer);
        doBindGL2(gl);
    }

    private void applyWidth(GL gl) {
        if(geometry==GL.GL_POINTS){
            gl.getGL2().glPointSize(width);
        } else if(geometry==GL.GL_LINES){
            gl.getGL2().glLineWidth(width);
        }
    }

    private void applyQuality(GL gl) {
        if (quality.isSmoothPolygon()) {
            gl.glEnable(GL2GL3.GL_POLYGON_SMOOTH);
            gl.glHint(GL2GL3.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
        } else
            gl.glDisable(GL2GL3.GL_POLYGON_SMOOTH);

        if (quality.isSmoothLine()) {
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
        } else
            gl.glDisable(GL.GL_LINE_SMOOTH);

        if (quality.isSmoothPoint()) {
            gl.glEnable(GL2ES1.GL_POINT_SMOOTH);
            gl.glHint(GL2ES1.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
            // gl.glDisable(GL2.GL_BLEND);
            // gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
        } else
            gl.glDisable(GL2ES1.GL_POINT_SMOOTH);
    }

    protected void doBindGL2(GL gl) {
        gl.getGL2().glBindBuffer(GL.GL_ARRAY_BUFFER, arrayName[0]);
        gl.getGL2().glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementName[0]);
    }
    
    /**
     * Returns a {@link Rotator} that can let the VBO turn around Z axis centered at X=0,Y=0.
     * Must be started by {@link Rotator.start()} if not started explicitely.
     */
    public Rotator rotator(boolean start) {
        final Rotate r = new Rotate(25, new Coord3d(0, 0, 1));
        Transform t = new Transform();
        t.add(r);
        setTransformBefore(t);
        Rotator rotator = new Rotator(10, r);
        if(start)
            rotator.start();
        return rotator;
    }
    
    public Rotator rotator() {
        return rotator(false);
    }


    /*
     * An OBJ file appears to be really really slow to render without a
     * FRONT_AND_BACK spec, probably because such a big polygon set has huge
     * cost to have culling status computed (culling enabled by depth peeling).
     */
    protected void configure(GL gl) {
        // gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        // gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
        // gl.glColor4f(1f,0f,1f,0.6f);
        // gl.glLineWidth(0.00001f);
        if (gl.isGL2()) {
            gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
        } else {
            GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
        }
        call(gl, color);
    }

    boolean hasColorBuffer = false;

    
    
    public boolean isHasColorBuffer() {
        return hasColorBuffer;
    }

    public void setHasColorBuffer(boolean hasColorBuffer) {
        this.hasColorBuffer = hasColorBuffer;
    }

    

    public int getGeometry() {
        return geometry;
    }

    /**
     * Set geometry, use: GL.GL_TRIANGLES (default) ...
     * 
     * @param geometry
     */
    public void setGeometry(int geometry) {
        this.geometry = geometry;
    }

    @Override
    public void applyGeometryTransform(Transform transform) {
        /*
         * Coord3d c = transform.compute(new Coord3d(x,y, z)); x = c.x; y = c.y;
         * z = c.z;
         */
        Logger.getLogger(DrawableVBO.class).warn("not implemented");
    }

    @Override
    public void updateBounds() { // requires smart reload
        Logger.getLogger(DrawableVBO.class).warn("not implemented");
    }

    /** To be called by the VBOBuilder */
    public void setData(GL gl, FloatVBO vbo) {
        setData(gl.getGL2(), vbo.getIndices(), vbo.getVertices(), vbo.getBounds(), 0);
    }


    public void setData(GL gl, IntBuffer indices, FloatBuffer vertices, BoundingBox3d bounds) {
        setData(gl.getGL2(), indices, vertices, bounds, 0);
    }

    public void setData(GL2 gl, IntBuffer indices, FloatBuffer vertices, BoundingBox3d bounds, int pointer) {
        doConfigure(pointer, indices.capacity());
        doLoadArrayFloatBuffer(gl, vertices);
        doLoadElementIntBuffer(gl, indices);
        doSetBoundingBox(bounds);
    }

    public void doConfigure(int pointer, int size) {
        int dimensions = 3;
        int byteOffset = (dimensions * 2) * Buffers.SIZEOF_FLOAT; // (coord+normal)
        int normalOffset = dimensions * Buffers.SIZEOF_FLOAT;
        doConfigure(pointer, size, byteOffset, normalOffset, dimensions);
    }

    public void doConfigure(int pointer, int size, int byteOffset, int normalOffset, int dimensions) {
        this.byteOffset = byteOffset;
        this.normalOffset = normalOffset;
        this.dimensions = dimensions;
        this.size = size;
        this.pointer = pointer;
    }

    public void doLoadArrayFloatBuffer(GL gl, FloatBuffer vertices) {
        doLoadArrayFloatBuffer(gl, vertices.capacity() * Buffers.SIZEOF_FLOAT, vertices);
    }

    public void doLoadArrayFloatBuffer(GL gl, int vertexSize, FloatBuffer vertices) {
        gl.glGenBuffers(1, arrayName, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, arrayName[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexSize, vertices, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, pointer);
    }

    public void doLoadElementIntBuffer(GL gl, IntBuffer indices) {
        doLoadElementIntBuffer(gl, indices.capacity() * Buffers.SIZEOF_INT, indices);
    }

    public void doLoadElementIntBuffer(GL gl, int indexSize, IntBuffer indices) {
        gl.glGenBuffers(1, elementName, 0);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementName[0]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indexSize, indices, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, pointer);
    }

    public void doSetBoundingBox(BoundingBox3d bounds) {
        bbox = bounds;
    }

    /* */

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    protected IGLLoader<DrawableVBO> loader;

    protected int byteOffset;
    protected int normalOffset;
    protected int dimensions;
    protected int size;
    protected int pointer;

    protected int arrayName[] = new int[1];
    protected int elementName[] = new int[1];

    protected boolean hasMountedOnce = false;
    protected Color color = new Color(1f, 0f, 1f, 0.75f);
}
