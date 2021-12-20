package org.jzy3d.plot3d.primitives.vbo.drawable;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.jzy3d.colors.Color;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;

/**
 * 
 * 
 * 
 * 
 * Warning! Observed that the sphere will render weirdly for rare viewpoint when following GL setting is applied.
 * <code>gl.glDisable(GL.GL_DEPTH_TEST)</code>
 * 
 * This is true when Quality.setAlphaActivated(false), in other word for Quality.Advanced.
 * 
 * @uthor David Eck inspired this class with http://math.hws.edu/graphicsbook/source/jogl/ColorCubeOfSpheres.java
 * @author Martin Pernollet
 */
public class SphereVBO extends DrawableVBO {
  VBOSphereMeshBuilder builder;

  Coord3d position;
  float radius;
  int stacks;
  int slices;

  enum Mode {
    ARRAY, VBO
  }

  Mode mode = Mode.VBO;


  public SphereVBO(Coord3d position, float radius, int stacks, int slices, Color color) {
      
    super(getBuilder(radius, stacks, slices));

    this.geometry = GL2.GL_QUAD_STRIP;

    // keep typed instance
    this.builder = (VBOSphereMeshBuilder) loader;

    // keep parameters to retrieve loader
    this.stacks = stacks;
    this.slices = slices;


    setColor(color);
    this.bbox = new BoundingBox3d();
    this.position = position;
    this.radius = radius;
    updateBounds();
  }

  @Override
  public void updateBounds() {
    bbox.reset();
    bbox.add(position.x + radius, position.y + radius, position.z + radius);
    bbox.add(position.x - radius, position.y - radius, position.z - radius);
  }

  @Override
  public void mount(IPainter painter) {
    try {
      if (!builder.hasMountedOnce())
        builder.load(painter, this);

      hasMountedOnce = true;
    } catch (Exception e) {
      e.printStackTrace();
      LogManager.getLogger(DrawableVBO.class).error(e, e);
    }
  }

  // element array buffer is an index:
  // @see
  // http://www.opengl-tutorial.org/intermediate-tutorials/tutorial-9-vbo-indexing/
  @Override
  public void draw(IPainter painter) {
    if (hasMountedOnce) {
      GL2 gl = ((NativeDesktopPainter) painter).getGL().getGL2();

      // We need to enable the vertex and normal arrays, and set
      // the vertex and normal points for these modes.

      if (Mode.VBO.equals(mode)) {

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        // if (mode == 4) {
        // When using VBOs, the vertex and normal pointers
        // refer to data in the VBOs.
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, builder.vertexVboId);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, builder.normalVboId);
        gl.glNormalPointer(GL.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
      } else if (Mode.ARRAY.equals(mode)) {
        // When not using VBOs, the points point to the FloatBuffers.
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, builder.sphereVertexBuffer);
        gl.glNormalPointer(GL.GL_FLOAT, 0, builder.sphereNormalBuffer);
      }


      doTransform(painter);
      /*
       * configure(painter, gl); doDrawElements(gl); doDrawBoundsIfDisplayed(painter);
       */

      painter.color(color);

      //painter.glPushMatrix();
      painter.glTranslatef(position.x, position.y, position.z);// i-5,j-5,k-5);


    //gl.glEnable(GL.GL_BLEND);
      //gl.glEnable(GL2.GL_ALPHA_TEST);
      //gl.glDisable(GL.GL_DEPTH_TEST);

      //painter.glDisable_CullFace();

      if (Mode.VBO.equals(mode)) {
        // FASTER!!
        drawSphereWithDrawArrays(gl, builder.slices, builder.stacks); // Draw using DrawArrays
      } else if (Mode.ARRAY.equals(mode)) {
        drawSphereDirectWithDataFromArrays(gl);
      }
      
      gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
      gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
      //painter.glPopMatrix();
      
      
    }

  }



  protected void drawSphereDirectWithDataFromArrays(GL2 gl) {
    int i, j;
    int vertices = (builder.slices + 1) * 2;

    for (i = 0; i < builder.stacks; i++) {
      int pos = i * (builder.slices + 1) * 2 * 3;

      gl.glBegin(geometry);
      for (j = 0; j < vertices; j++) {
        // gl.glNormal3fv(sphereNormalArray, pos+3*j); /* This worked but took 3 times as long!!! */
        // gl.glVertex3fv(sphereVertexArray, pos+3*j);
        gl.glNormal3f(builder.sphereNormalArray[pos + 3 * j],
            builder.sphereNormalArray[pos + 3 * j + 1], builder.sphereNormalArray[pos + 3 * j + 2]);
        gl.glVertex3f(builder.sphereVertexArray[pos + 3 * j],
            builder.sphereVertexArray[pos + 3 * j + 1], builder.sphereVertexArray[pos + 3 * j + 2]);
      }
      gl.glEnd();
    }
  }


  /**
   * Draw one sphere. The VertexPointer and NormalPointer must already be set to point to the data
   * for the sphere, and they must be enabled.
   */
  protected void drawSphereWithDrawArrays(GL2 gl, int slices, int stacks) {
    int vertices = (slices + 1) * 2;

    for (int i = 0; i < stacks; i++) {
      int pos = i * (slices + 1) * 2;
      gl.glDrawArrays(geometry, pos, vertices);
    }
  }


  // ----------------- for glDrawArrays and Vertex Buffer Object ----------------------------------

  /**
   * This load the definition of a VBO sphere object and set arrays for later rendering.
   */
  public static class VBOSphereMeshBuilder implements IGLLoader<DrawableVBO> {
    double radius;
    int stacks;
    int slices;

    protected FloatBuffer sphereVertexBuffer; // Holds the vertex coords, for use with glDrawArrays
    protected FloatBuffer sphereNormalBuffer; // Holds the normal vectors, for use with glDrawArrays

    protected float[] sphereVertexArray; // The same data as in sphereVertexBuffer, stored in an
                                         // array.
    float[] sphereNormalArray; // The same data as in sphereNormalBuffer, stored in an array.

    protected int vertexVboId; // identifier for the Vertex Buffer Object to hold the vertex coords
    protected int normalVboId; // identifier for the Vertex Buffer Object to hold the normla vectors

    protected boolean hasMountedOnce = false;


    public VBOSphereMeshBuilder(double radius, int stacks, int slices) {
      this.radius = radius;
      this.stacks = stacks;
      this.slices = slices;
    }

    @Override
    public void load(IPainter painter, DrawableVBO drawable) throws Exception {
      GL gl = ((NativeDesktopPainter) painter).getGL();
      createSphereArraysAndVBOs(gl.getGL2());
    }

    /**
     * Creates the vertex coordinate and normal vectors for a sphere.
     * 
     * The data is stored in the FloatBuffers sphereVertexBuffer and sphereNormalBuffer.
     * 
     * In addition, VBOs are created to hold the data and the data is copied from the FloatBuffers
     * into the VBOs.
     * 
     * (Note: The VBOs are used for render mode 4; the FloatBuffers are used for render mode 3.)
     */
    private void createSphereArraysAndVBOs(GL2 gl) {
      int size = stacks * (slices + 1) * 2 * 3;
      sphereVertexBuffer = GLBuffers.newDirectFloatBuffer(size);
      sphereNormalBuffer = GLBuffers.newDirectFloatBuffer(size);
      sphereVertexArray = new float[size];
      sphereNormalArray = new float[size];
      for (int j = 0; j < stacks; j++) {
        double latitude1 = (Math.PI / stacks) * j - Math.PI / 2;
        double latitude2 = (Math.PI / stacks) * (j + 1) - Math.PI / 2;
        double sinLat1 = Math.sin(latitude1);
        double cosLat1 = Math.cos(latitude1);
        double sinLat2 = Math.sin(latitude2);
        double cosLat2 = Math.cos(latitude2);
        for (int i = 0; i <= slices; i++) {
          double longitude = (2 * Math.PI / slices) * i;
          double sinLong = Math.sin(longitude);
          double cosLong = Math.cos(longitude);
          double x1 = cosLong * cosLat1;
          double y1 = sinLong * cosLat1;
          double z1 = sinLat1;
          double x2 = cosLong * cosLat2;
          double y2 = sinLong * cosLat2;
          double z2 = sinLat2;

          sphereNormalBuffer.put((float) x2);
          sphereNormalBuffer.put((float) y2);
          sphereNormalBuffer.put((float) z2);

          sphereVertexBuffer.put((float) (radius * x2));
          sphereVertexBuffer.put((float) (radius * y2));
          sphereVertexBuffer.put((float) (radius * z2));

          sphereNormalBuffer.put((float) x1);
          sphereNormalBuffer.put((float) y1);
          sphereNormalBuffer.put((float) z1);

          sphereVertexBuffer.put((float) (radius * x1));
          sphereVertexBuffer.put((float) (radius * y1));
          sphereVertexBuffer.put((float) (radius * z1));
        }
      }
      for (int i = 0; i < size; i++) {
        sphereVertexArray[i] = sphereVertexBuffer.get(i);
        sphereNormalArray[i] = sphereNormalBuffer.get(i);
      }

      sphereVertexBuffer.rewind();
      sphereNormalBuffer.rewind();

      int[] bufferIDs = new int[2];
      gl.glGenBuffers(2, bufferIDs, 0);
      vertexVboId = bufferIDs[0];
      normalVboId = bufferIDs[1];

      //System.out.println("VertexVBOid : " + vertexVboId);
      //System.out.println("normalVboId : " + normalVboId);

      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexVboId);
      gl.glBufferData(GL2.GL_ARRAY_BUFFER, size * 4, sphereVertexBuffer, GL2.GL_STATIC_DRAW);
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, normalVboId);
      gl.glBufferData(GL2.GL_ARRAY_BUFFER, size * 4, sphereNormalBuffer, GL2.GL_STATIC_DRAW);
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

      hasMountedOnce = true;

    }

    public boolean hasMountedOnce() {
      return hasMountedOnce;
    }
  }

  public static class SphereKey {
    double radius;
    int stacks;
    int slices;

    public SphereKey(double radius, int stacks, int slices) {
      this.radius = radius;
      this.stacks = stacks;
      this.slices = slices;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(radius);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + slices;
      result = prime * result + stacks;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      SphereKey other = (SphereKey) obj;
      if (Double.doubleToLongBits(radius) != Double.doubleToLongBits(other.radius))
        return false;
      if (slices != other.slices)
        return false;
      if (stacks != other.stacks)
        return false;
      return true;
    }
  }

  /**
   * Allows retrieving or creating a sphere builder for this combination of parameters.
   * 
   * The goal is to share a common mesh definition - hence a single pair of array - for a sphere
   * parameter combination.
   * 
   * @param radius sphere radius
   * @param stacks number of latitudes
   * @param slices number of longitudes
   * @return
   */
  public static VBOSphereMeshBuilder getBuilder(double radius, int stacks, int slices) {
    SphereKey key = new SphereKey(radius, stacks, slices);

    VBOSphereMeshBuilder builder = builders.get(key);

    if (builder == null) {
      //System.out.println("new for " + radius + " " + stacks);
      builder = new VBOSphereMeshBuilder(radius, stacks, slices);
      builders.put(key, builder);
    }
    return builder;
  }

  protected static Map<SphereKey, VBOSphereMeshBuilder> builders = new HashMap<>();

}
