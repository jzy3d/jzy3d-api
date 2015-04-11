package org.jzy3d.plot3d.primitives.vbo.buffers;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class FloatVBO {
    FloatBuffer vertices;
    IntBuffer indices;
    
    public FloatVBO(int verticeBufferSize, int indexBufferSize){
        vertices = FloatBuffer.allocate(verticeBufferSize);
        indices = IntBuffer.allocate(indexBufferSize);
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
}
