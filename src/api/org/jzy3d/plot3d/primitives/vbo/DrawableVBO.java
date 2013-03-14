package org.jzy3d.plot3d.primitives.vbo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.apache.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.common.nio.Buffers;


/**
 * A {@link DrawableVBO} is able to efficiently large collection of
 * geometries.
 * 
 * @author Martin Pernollet
 */
public class DrawableVBO extends AbstractDrawable implements IGLBindedResource{
    public DrawableVBO(IGLLoader<DrawableVBO> loader){
        this.loader = loader;
    }
    
    public boolean hasMountedOnce(){
        return hasMountedOnce;
    }
    
    // element array buffer is an index: 
    // @see http://www.opengl-tutorial.org/intermediate-tutorials/tutorial-9-vbo-indexing/
    public void draw(GL2 gl, GLU glu, Camera cam) {
        if(hasMountedOnce){
            doTransform(gl, glu, cam);
            configure(gl);
            doDrawElements(gl);
            doDrawBounds(gl, glu, cam);
        }
    }
    
    /* An OBJ file appears to be really really slow to
     * render without a FRONT_AND_BACK spec, probably
     * because such a big polygon set has huge cost to have
     * culling status computed (culling enabled by depth peeling).
     */
    protected void configure(GL2 gl){
        //gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        //gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
        //gl.glColor4f(1f,0f,1f,0.6f);
        //gl.glLineWidth(0.00001f);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        call(gl, color);
    }
    
    protected void doDrawElements(GL2 gl) {
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, arrayName[0]);
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, elementName[0]);
        
        gl.glVertexPointer(dimensions, GL2.GL_FLOAT, byteOffset, pointer);
        gl.glNormalPointer(GL2.GL_FLOAT, byteOffset, normalOffset);
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

        gl.glDrawElements(getGeometry(), size, GL2.GL_UNSIGNED_INT, pointer);

        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, elementName[0]);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, arrayName[0]);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
    }
    
    protected int getGeometry(){
        return GL2.GL_TRIANGLES;
    }
    
    public void applyGeometryTransform(Transform transform){
        /*Coord3d c = transform.compute(new Coord3d(x,y, z));
        x = c.x;
        y = c.y;
        z = c.z;*/
        Logger.getLogger(DrawableVBO.class).warn("not implemented");
    }
    
    @Override
    public void updateBounds() { // requires smart reload
        Logger.getLogger(DrawableVBO.class).warn("not implemented");
        /*bbox.reset();
        bbox.add(x+Math.max(radiusBottom, radiusTop), y+Math.max(radiusBottom, radiusTop), z);
        bbox.add(x-Math.max(radiusBottom, radiusTop), y-Math.max(radiusBottom, radiusTop), z);
        bbox.add(x+Math.max(radiusBottom, radiusTop), y+Math.max(radiusBottom, radiusTop), z+height);
        bbox.add(x-Math.max(radiusBottom, radiusTop), y-Math.max(radiusBottom, radiusTop), z+height);*/
    }
    
    /* IO */
    
    public void mount(GL2 gl){
        try {
            loader.load(gl, this);
            hasMountedOnce = true;
        } catch (Exception e) {
            Logger.getLogger(DrawableVBO.class).error(e,e);
        }
    }

    public void setData(GL2 gl, IntBuffer indices, FloatBuffer vertices, BoundingBox3d bounds) {
        setData(gl, indices, vertices, bounds, 0);
    }
    
    public void setData(GL2 gl, IntBuffer indices, FloatBuffer vertices, BoundingBox3d bounds, int pointer) {
        doConfigure(pointer, indices.capacity());
        doLoadArrayBuffer(gl, vertices);
        doLoadElementBuffer(gl, indices);
        doSetBoundingBox(bounds);
    }
    
    public void doConfigure(int pointer, int size){
        int dimensions = 3;
        int byteOffset = (dimensions*2) * Buffers.SIZEOF_FLOAT; // (coord+normal)
        int normalOffset = dimensions * Buffers.SIZEOF_FLOAT; 
        doConfigure(pointer, size, byteOffset, normalOffset, dimensions);
    }
    
    public void doConfigure(int pointer, int size, int byteOffset, int normalOffset, int dimensions){
        this.byteOffset = byteOffset;
        this.normalOffset = normalOffset;
        this.dimensions = dimensions;
        this.size = size;
        this.pointer = pointer;
    }

    public void doLoadArrayBuffer(GL2 gl, FloatBuffer vertices) {
        doLoadArrayBuffer(gl, vertices.capacity()*Buffers.SIZEOF_FLOAT, vertices);
    }
    
    public void doLoadArrayBuffer(GL2 gl, int vertexSize, FloatBuffer vertices) {
        gl.glGenBuffers(1, arrayName, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, arrayName[0]);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexSize, vertices, GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, pointer);
    }

    public void doLoadElementBuffer(GL2 gl, IntBuffer indices) {
        doLoadElementBuffer(gl, indices.capacity()*Buffers.SIZEOF_INT, indices);
    }
    
    public void doLoadElementBuffer(GL2 gl, int indexSize, IntBuffer indices) {
        gl.glGenBuffers(1, elementName, 0);
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, elementName[0]);
        gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indexSize, indices, GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, pointer);
    }

    public void doSetBoundingBox(BoundingBox3d bounds){
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
