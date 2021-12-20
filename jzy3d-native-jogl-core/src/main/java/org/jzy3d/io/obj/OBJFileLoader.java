package org.jzy3d.io.obj;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import com.jogamp.common.nio.Buffers;

public class OBJFileLoader implements IGLLoader<DrawableVBO> {
  static Logger logger = LogManager.getLogger(OBJFileLoader.class);

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
    drawable.doLoadArrayFloatBuffer(((NativeDesktopPainter) painter).getGL(), vertexSize, vertices);
    drawable.doLoadElementIntBuffer(((NativeDesktopPainter) painter).getGL(), indexSize, indices);
    drawable.doSetBoundingBox(bounds);
  }

}
