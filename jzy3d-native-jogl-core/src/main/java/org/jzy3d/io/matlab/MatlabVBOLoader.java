package org.jzy3d.io.matlab;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.apache.logging.log4j.LogManager;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLNumericArray;

/**
 * Load a Matlab (TM) .mat file, assuming it contains at least three arrays named "X", "Y" and "Z".
 * 
 * Will fill buffers used to setup a {@link DrawableVBO} which efficiently load geometries in GPU
 * memory once program starts.
 * 
 * @author Martin Pernollet
 */
public class MatlabVBOLoader implements IGLLoader<DrawableVBO> {
  protected String filename;

  public MatlabVBOLoader(String filename) {
    this.filename = filename;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void load(IPainter painter, DrawableVBO drawable) throws Exception {
    MatFileReader mfr = new MatFileReader(filename);
    MLNumericArray<Float> x = (MLNumericArray<Float>) mfr.getMLArray("X");
    MLNumericArray<Float> y = (MLNumericArray<Float>) mfr.getMLArray("Y");
    MLNumericArray<Float> z = (MLNumericArray<Float>) mfr.getMLArray("Z");

    int n = x.getN(); // number of triangles
    int components = 3; // x, y, z
    int geomsize = 3; // triangle

    // Allocate content
    FloatBuffer vertices = FloatBuffer.allocate(n * (components * 2) * geomsize); // *2 normals
    IntBuffer indices = IntBuffer.allocate(n * geomsize);
    BoundingBox3d bounds = new BoundingBox3d();

    // Feed buffers
    int size = 0;
    for (int i = 0; i < n; i++) {
      Coord3d c1 = new Coord3d(x.getReal(C1, i), y.getReal(C1, i), z.getReal(C1, i));
      Coord3d c2 = new Coord3d(x.getReal(C2, i), y.getReal(C2, i), z.getReal(C2, i));
      Coord3d c3 = new Coord3d(x.getReal(C3, i), y.getReal(C3, i), z.getReal(C3, i));
      Coord3d no = Normal.compute(c1, c2, c3);

      indices.put(size++);
      vertices.put(c1.x);
      vertices.put(c1.y);
      vertices.put(c1.z);
      vertices.put(no.x);
      vertices.put(no.y);
      vertices.put(no.z);
      bounds.add(c1);

      indices.put(size++);
      vertices.put(c2.x);
      vertices.put(c2.y);
      vertices.put(c2.z);
      vertices.put(no.x);
      vertices.put(no.y);
      vertices.put(no.z);
      bounds.add(c2);

      indices.put(size++);
      vertices.put(c3.x);
      vertices.put(c3.y);
      vertices.put(c3.z);
      vertices.put(no.x);
      vertices.put(no.y);
      vertices.put(no.z);
      bounds.add(c3);
    }
    vertices.rewind();
    indices.rewind();

    // Store in GPU
    drawable.setData(((NativeDesktopPainter) painter).getGL(), indices, vertices, bounds);

    LogManager.getLogger(MatlabVBOLoader.class).info("done loading " + filename);
  }


  protected static int C1 = 0;
  protected static int C2 = 1;
  protected static int C3 = 2;
}
