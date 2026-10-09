package org.jzy3d.io.obj;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OBJFileLoader implements IGLLoader<DrawableVBO> {
  static Logger logger = LoggerFactory.getLogger(OBJFileLoader.class);

  protected File file;
  protected OBJFile obj;

  public OBJFileLoader(File file) {
    this.file = file;
  }

  @Override
  public void load(IPainter painter, DrawableVBO drawable) {
    obj = new OBJFile();

    logger.info("Start loading OBJ file '" + file.getAbsolutePath() + "'");
    obj.loadModelFromFile(file);

    logger.info("Start compiling mesh");
    obj.compileModel();

    logger.info(obj.getPositionCount() + " vertices");
    logger.info((obj.getIndexCount() / 3) + " triangles");
    
    

    int size = obj.getIndexCount();
    int indexSize = size * Integer.BYTES;
    int vertexSize = obj.getCompiledVertexCount() * Float.BYTES;
    int byteOffset = obj.getCompiledVertexSize() * Float.BYTES;
    int normalOffset = obj.getCompiledNormalOffset() * Float.BYTES;
    int dimensions = obj.getPositionSize();

    int pointer = 0;

    FloatBuffer vertices = obj.getCompiledVertices();
    IntBuffer indices = obj.getCompiledIndices();
    BoundingBox3d bounds = obj.computeBoundingBox();

    drawable.doConfigure(pointer, size, byteOffset, normalOffset, dimensions);
    drawable.doLoadArrayFloatBuffer(painter, vertexSize, vertices);
    drawable.doLoadElementIntBuffer(painter, indexSize, indices);
    drawable.doSetBoundingBox(bounds);
  }

}
