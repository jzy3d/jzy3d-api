package org.jzy3d.plot3d.primitives.volume;

import java.nio.Buffer;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.vbo.ColormapTexture;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

public class Texture3D extends Drawable implements IGLBindedResource, IMultiColorable {

  /** The GL texture ID. */
  protected int texID;
  protected Buffer buffer;
  protected int[] shape;
  protected boolean mounted = false;
  protected DrawableVBO shapeVBO;
  protected GLSLProgram shaderProgram;
  protected float min;
  protected float max;
  protected ColormapTexture colormapTexure;

  protected boolean disposed;
  protected ColorMapper mapper;

  /**
   * Instanciate a drawable volume.
   * 
   * @param buffer provides (x,y,z,V) tuples where (x,y,z) are voxel index in the volume and V the
   *        value used for coloring voxels
   * @param shape a 3 element array indicating the number of voxels for each dimension
   * @param min the minimum V value provided in the input buffer which must be set consistently with
   *        the colormapper
   * @param max the maximum V value provided in the input buffer which must be set consistently with
   *        the colormapper
   * @param mapper the colormap handler that will apply a colormap to a value range
   * @param bbox the real world range of each axis, since the input buffer provide tuples with index
   *        and not coordinates
   */
  public Texture3D(Buffer buffer, int[] shape, float min, float max, ColorMapper mapper,
      BoundingBox3d bbox) {
    this.buffer = buffer;
    buffer.rewind();
    this.shape = shape;
    this.bbox = bbox;
    this.shapeVBO = new CubeVBO(new CubeVBOBuilder(bbox));
    this.min = min;
    this.max = max;
    this.mapper = mapper;
  }

  /**
   * A convenient constructor that configure the volume value range based on the colormapper
   * settings.
   * 
   * {@link Texture3D#Texture3D(Buffer, int[], float, float, ColorMapper, BoundingBox3d)}
   */
  public Texture3D(Buffer buffer, int[] shape, ColorMapper mapper, BoundingBox3d bbox) {
    this(buffer, shape, (float) mapper.getMin(), (float) mapper.getMax(), mapper, bbox);
  }

  @Override
  public void mount(IPainter painter) {

    if (!mounted) {
      shapeVBO.mount(painter);
      shaderProgram = new GLSLProgram();

      // load shaders handling the volume (a.k.a 3D texture)
      ShaderFilePair sfp = new ShaderFilePair(this.getClass(), "volume.vert", "volume.frag");
      shaderProgram.loadAndCompileShaders(painter, sfp);
      shaderProgram.link(painter, validateAtMount);

      bind(painter);

      // create the colormap as a 1D texture made of 256 pixels
      colormapTexure = new ColormapTexture(mapper, "transfer", shaderProgram.getProgramId());
      colormapTexure.bind(painter);

      // Leave textures disabled until drawing, as fixed function rendering of other drawables
      // (e.g. bitmap text) should not be textured
      colormapTexure.unbind(painter);
      unbind(painter);

      mounted = true;
    }
  }
  
  boolean validateAtMount = false;

  public void setMin(Number min) {
    this.min = min.floatValue();
  }

  public void setMax(Number max) {
    this.max = max.floatValue();
  }

  @Override
  public boolean hasMountedOnce() {
    return mounted;
  }

  public void bind(final IPainter painter)  {

    painter.glEnable(GLConstants.GL_TEXTURE_3D);
    
    //painter.glEnd();

    // Verify a texture can be enabled and mapped to the shader variable name
    validateTexID(painter, true);

    // Declare a 3D texture
    //IntBuffer b = IntBuffer.allocate(32880);
    //gl.glGenTextures(1, b);
    //System.out.println(texID);
    painter.glBindTexture(GLConstants.GL_TEXTURE_3D, texID);
    painter.glActiveTexture(GLConstants.GL_TEXTURE0);

    // Will keep max or min texture value upon overflow on the X dimension
    painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP);

    // Will keep max or min texture value upon overflow on the Y dimension
    painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP);

    // Will keep max or min texture value upon overflow on the Z dimension
    painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_WRAP_R, GLConstants.GL_CLAMP);

    // Will apply linear interpolation when zooming in texture voxels
    painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_LINEAR);

    // Will apply linear interpolation when zooming out texture voxels
    painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_LINEAR);

    // Load buffer data into memory
    setTextureData(painter, buffer, shape);
  }
  
  public void unbind(final IPainter painter) {
    painter.glActiveTexture(GLConstants.GL_TEXTURE0);
    painter.glBindTexture(GLConstants.GL_TEXTURE_3D, 0);
    painter.glDisable(GLConstants.GL_TEXTURE_3D);
  }

  /**
   * Load buffer data into memory
   * 
   * @param painter
   * @param buffer texture data
   * @param shape the dimensions of 3d texture.
   * @see https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glTexImage3D.xml
   */
  public void setTextureData(final IPainter painter, Buffer buffer, int[] shape) {
    // define how pixels are stored in memory
    painter.glPixelStorei(GLConstants.GL_UNPACK_ALIGNMENT, 1);
    
    // specify a 3 dimensional texture image with a single LOD, R float internal format, dynamical
    // number of voxel for width, height, depth, R float input format
    // no border : the buffer holds exactly shape[0]*shape[1]*shape[2] values. A border of 1 would
    // let GL read (shape[0]+2)*(shape[1]+2)*(shape[2]+2) values, out of the buffer
    painter.glTexImage3D(GLConstants.GL_TEXTURE_3D, 0, GLConstants.GL_R32F, shape[2], shape[1],
        shape[0], 0, GLConstants.GL_RED, GLConstants.GL_FLOAT, buffer);
  }

  protected boolean validateTexID(final IPainter painter, final boolean throwException) {
    painter.glActiveTexture(GLConstants.GL_TEXTURE0);
    painter.glEnable(GLConstants.GL_TEXTURE_3D);

    // The shader reads the volume through the "volumeTexture" sampler, bound to texture unit 0 at
    // rendering. The texture itself needs its own name : a uniform location is not a texture name.
    if (texID == 0) {
      int[] tmp = new int[1];
      painter.glGenTextures(1, tmp, 0);
      texID = tmp[0];
    }

    if (texID == 0 && throwException) {
      throw new RuntimeException("Create texture ID invalid: texID " + texID + ", glerr 0x"
          + Integer.toHexString(painter.glGetError()));
    }

    return 0 != texID;
  }


  @Override
  public void draw(IPainter painter) {
    Camera cam = painter.getCamera();

    if (!mounted) {
      mount(painter);
    }
    bind(painter);
    colormapTexure.bind(painter);
    


    colormapTexure.update(painter);

    doTransform(painter);

    float mvmatrix[] = new float[16];
    float projmatrix[] = new float[16];

    Coord3d eye = painter.getCamera().getEye();
    eye = eye.sub(cam.getTarget());
    eye = eye.normalizeTo(1);

    painter.glGetFloatv(GLConstants.GL_MODELVIEW_MATRIX, mvmatrix, 0);
    painter.glGetFloatv(GLConstants.GL_PROJECTION_MATRIX, projmatrix, 0);

    float[] pmat = mvmatrix.clone();

    float[] success = invertMatrix(mvmatrix, pmat);

    float[] eye1 = new float[] {eye.x, eye.y, eye.z, 1};

    Coord3d range = bbox.getRange();

    float[] frange = new float[] {range.x, range.y, range.z, 1};

    if (success != null) {
      normalizeVec3(frange);
      success = success.clone();
      // Arrays.fill(success, 0);
      success[0] /= frange[0];
      success[5] /= frange[1];
      success[10] /= (1 * frange[2]);
      multMatrixVec(success, eye1, eye1);
      normalizeVec3(eye1);
    }
    ////
    shaderProgram.bind(painter);
    shaderProgram.setUniform(painter, "eye", eye1, 4);
    shaderProgram.setUniform(painter, "minMax", new float[] {min, max}, 2);
    int idt = painter.glGetUniformLocation(shaderProgram.getProgramId(), "volumeTexture");
    int idc = painter.glGetUniformLocation(shaderProgram.getProgramId(), "transfer");
    painter.glUniform1i(idt, 0); // refer to GL_TEXTURE0, the volume
    painter.glUniform1i(idc, 1); // refer to GL_TEXTURE1, the colormap

    // validation, once samplers refer to their texture units
    if(!validateAtMount)
      shaderProgram.validateProgram(painter);


    painter.glEnable(GLConstants.GL_BLEND);
    painter.glEnable(GLConstants.GL_CULL_FACE);
    painter.glBlendFunc(GLConstants.GL_SRC_ALPHA, GLConstants.GL_ONE_MINUS_SRC_ALPHA);
    painter.glPolygonMode(GLConstants.GL_FRONT, GLConstants.GL_FILL);
    painter.glCullFace(GLConstants.GL_BACK);
    
    shapeVBO.draw(painter);
    shaderProgram.unbind(painter);
    
    colormapTexure.unbind(painter);
    unbind(painter);

    //gl.glDisable(GLConstants.GL_CULL_FACE);

    
    if (disposed) {
      painter.glDeleteTextures(1, new int[] {texID}, 0);
      buffer = null;
      shaderProgram.destroy(painter);
    }
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    // TODO Auto-generated method stub

  }

  @Override
  public void dispose() {
    disposed = true;
    shapeVBO.dispose();
  }

  @Override
  public void updateBounds() {
    bbox = new BoundingBox3d(0, 1, 0, 1, 0, 1);

  }

  @Override
  public ColorMapper getColorMapper() {
    return mapper;
  }

  @Override
  public void setColorMapper(ColorMapper mapper) {
    this.mapper = mapper;
    if (colormapTexure != null)
      colormapTexure.updateColormap(mapper);
  }

  /* MATRIX UTILITIES (column major 4x4 matrices) */

  /**
   * Invert a column major 4x4 matrix.
   * 
   * @return the inverted matrix stored in out, or null if the matrix is not invertible.
   */
  protected static float[] invertMatrix(float[] m, float[] out) {
    float[] inv = new float[16];

    inv[0] = m[5] * m[10] * m[15] - m[5] * m[11] * m[14] - m[9] * m[6] * m[15]
        + m[9] * m[7] * m[14] + m[13] * m[6] * m[11] - m[13] * m[7] * m[10];
    inv[4] = -m[4] * m[10] * m[15] + m[4] * m[11] * m[14] + m[8] * m[6] * m[15]
        - m[8] * m[7] * m[14] - m[12] * m[6] * m[11] + m[12] * m[7] * m[10];
    inv[8] = m[4] * m[9] * m[15] - m[4] * m[11] * m[13] - m[8] * m[5] * m[15]
        + m[8] * m[7] * m[13] + m[12] * m[5] * m[11] - m[12] * m[7] * m[9];
    inv[12] = -m[4] * m[9] * m[14] + m[4] * m[10] * m[13] + m[8] * m[5] * m[14]
        - m[8] * m[6] * m[13] - m[12] * m[5] * m[10] + m[12] * m[6] * m[9];
    inv[1] = -m[1] * m[10] * m[15] + m[1] * m[11] * m[14] + m[9] * m[2] * m[15]
        - m[9] * m[3] * m[14] - m[13] * m[2] * m[11] + m[13] * m[3] * m[10];
    inv[5] = m[0] * m[10] * m[15] - m[0] * m[11] * m[14] - m[8] * m[2] * m[15]
        + m[8] * m[3] * m[14] + m[12] * m[2] * m[11] - m[12] * m[3] * m[10];
    inv[9] = -m[0] * m[9] * m[15] + m[0] * m[11] * m[13] + m[8] * m[1] * m[15]
        - m[8] * m[3] * m[13] - m[12] * m[1] * m[11] + m[12] * m[3] * m[9];
    inv[13] = m[0] * m[9] * m[14] - m[0] * m[10] * m[13] - m[8] * m[1] * m[14]
        + m[8] * m[2] * m[13] + m[12] * m[1] * m[10] - m[12] * m[2] * m[9];
    inv[2] = m[1] * m[6] * m[15] - m[1] * m[7] * m[14] - m[5] * m[2] * m[15]
        + m[5] * m[3] * m[14] + m[13] * m[2] * m[7] - m[13] * m[3] * m[6];
    inv[6] = -m[0] * m[6] * m[15] + m[0] * m[7] * m[14] + m[4] * m[2] * m[15]
        - m[4] * m[3] * m[14] - m[12] * m[2] * m[7] + m[12] * m[3] * m[6];
    inv[10] = m[0] * m[5] * m[15] - m[0] * m[7] * m[13] - m[4] * m[1] * m[15]
        + m[4] * m[3] * m[13] + m[12] * m[1] * m[7] - m[12] * m[3] * m[5];
    inv[14] = -m[0] * m[5] * m[14] + m[0] * m[6] * m[13] + m[4] * m[1] * m[14]
        - m[4] * m[2] * m[13] - m[12] * m[1] * m[6] + m[12] * m[2] * m[5];
    inv[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2] * m[11]
        - m[5] * m[3] * m[10] - m[9] * m[2] * m[7] + m[9] * m[3] * m[6];
    inv[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2] * m[11]
        + m[4] * m[3] * m[10] + m[8] * m[2] * m[7] - m[8] * m[3] * m[6];
    inv[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1] * m[11]
        - m[4] * m[3] * m[9] - m[8] * m[1] * m[7] + m[8] * m[3] * m[5];
    inv[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1] * m[10]
        + m[4] * m[2] * m[9] + m[8] * m[1] * m[6] - m[8] * m[2] * m[5];

    float det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12];

    if (det == 0)
      return null;

    for (int i = 0; i < 16; i++)
      out[i] = inv[i] / det;
    return out;
  }

  /**
   * Multiply a column major 4x4 matrix with a 4 component vector.
   * 
   * Components of out are written one after the other, hence using the same array for vec and out
   * makes the computation of a component use the components already computed.
   */
  protected static float[] multMatrixVec(float[] m, float[] vec, float[] out) {
    for (int i = 0; i < 4; i++) {
      out[i] = vec[0] * m[i] + vec[1] * m[4 + i] + vec[2] * m[8 + i] + vec[3] * m[12 + i];
    }
    return out;
  }

  /** Normalize the 3 first components of a vector, or set them to 0 if the vector is null. */
  protected static float[] normalizeVec3(float[] vec) {
    float lengthSq = vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2];
    if (Math.abs(lengthSq) < Math.ulp(1f)) {
      vec[0] = vec[1] = vec[2] = 0;
    } else {
      float invLength = 1f / (float) Math.sqrt(lengthSq);
      vec[0] *= invLength;
      vec[1] *= invLength;
      vec[2] *= invLength;
    }
    return vec;
  }
}
