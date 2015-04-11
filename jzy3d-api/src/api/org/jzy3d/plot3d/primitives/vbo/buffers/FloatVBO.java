package org.jzy3d.plot3d.primitives.vbo.buffers;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jzy3d.maths.BoundingBox3d;

public class FloatVBO {
    FloatBuffer vertices;
    IntBuffer indices;
    BoundingBox3d bounds;

    
    public FloatVBO(int verticeBufferSize, int indexBufferSize){
        vertices = FloatBuffer.allocate(verticeBufferSize);
        indices = IntBuffer.allocate(indexBufferSize);
        bounds = new BoundingBox3d();
    }

    public FloatBuffer getVertices() {
        return vertices;
    }

    public void setVertices(FloatBuffer vertices) {
        this.vertices = vertices;
    }

    public IntBuffer getIndices() {
        return indices;
    }

    public void setIndices(IntBuffer indices) {
        this.indices = indices;
    }

    public BoundingBox3d getBounds() {
        return bounds;
    }

    public void setBounds(BoundingBox3d bounds) {
        this.bounds = bounds;
    }
    
    
}
