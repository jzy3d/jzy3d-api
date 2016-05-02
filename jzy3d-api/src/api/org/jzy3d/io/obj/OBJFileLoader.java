package org.jzy3d.io.obj;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.apache.log4j.Logger;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;

public class OBJFileLoader implements IGLLoader<DrawableVBO>{
    static Logger logger = Logger.getLogger(OBJFileLoader.class);
    
    protected String filename;
    protected OBJFile obj;
    
    public OBJFileLoader(String filename) {
        this.filename = filename;
    }

    @Override
    public void load(GL gl, DrawableVBO drawable) {
        obj = new OBJFile();
        
        logger.info("Start loading OBJ file '" + filename + "'");
        obj.loadModelFromFilename(filename);
        
        logger.info("Start compiling mesh");
        obj.compileModel();
        
        logger.info(obj.getPositionCount() + " vertices");
        logger.info((obj.getIndexCount() / 3) + " triangles");
        
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
        drawable.doLoadArrayFloatBuffer(gl, vertexSize, vertices);
        drawable.doLoadElementIntBuffer(gl, indexSize, indices);
        drawable.doSetBoundingBox(bounds);
    }

}
