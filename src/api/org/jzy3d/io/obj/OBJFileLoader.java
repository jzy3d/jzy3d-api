package org.jzy3d.io.obj;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.vbo.DrawableVBO;

import com.jogamp.common.nio.Buffers;

public class OBJFileLoader implements IGLLoader<DrawableVBO>{
    protected String filename;
    protected OBJFile obj;
    
    public OBJFileLoader(String filename) {
        this.filename = filename;
    }

    @Override
    public void load(GL2 gl, DrawableVBO drawable) {
        System.err.println("loading OBJ...\n");
        obj = new OBJFile();
        obj.loadModelFromFile(filename);
        System.err.println("compiling mesh...\n");
        obj.compileModel();
        System.err.println(obj.getPositionCount() + " vertices");
        System.err.println((obj.getIndexCount() / 3) + " triangles");
        
        int size = obj.getIndexCount();
        int indexSize = size * Buffers.SIZEOF_INT;
        int vertexSize = obj.getCompiledVertexCount() * Buffers.SIZEOF_FLOAT;
        int byteOffset = obj.getCompiledVertexSize() * Buffers.SIZEOF_FLOAT;
        int normalOffset = obj.getCompiledNormalOffset() * Buffers.SIZEOF_FLOAT;
        int dimensions = obj.getPositionSize(); 
        
        int pointer = 0;
        
        FloatBuffer vertices = obj.getCompiledVertices();
        IntBuffer indices = obj.getCompiledIndices();
        BoundingBox3d bounds = obj.computeBoundingBox();
        
        drawable.doConfigure(pointer, size, byteOffset, normalOffset, dimensions);
        drawable.doLoadArrayBuffer(gl, vertexSize, vertices);
        drawable.doLoadElementBuffer(gl, indexSize, indices);
        drawable.doSetBoundingBox(bounds);
    }

}
