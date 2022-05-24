package org.jzy3d.plot3d.primitives.volume;

import java.nio.Buffer;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.vbo.ColormapTexture;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.VectorUtil;

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
    GL gl = ((NativeDesktopPainter) painter).getGL();

    if (!mounted) {
      shapeVBO.mount(painter);
      shaderProgram = new GLSLProgram();

      // load shaders handling the volume (a.k.a 3D texture)
      ShaderFilePair sfp = new ShaderFilePair(this.getClass(), "volume.vert", "volume.frag");
      shaderProgram.loadAndCompileShaders(gl.getGL2(), sfp);
      shaderProgram.link(gl.getGL2());


      bind(gl);

      // create the colormap as a 1D texture made of 256 pixels
      colormapTexure = new ColormapTexture(mapper, "transfer", shaderProgram.getProgramId());
      colormapTexure.bind(gl);
      mounted = true;
    }
  }

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

  public void bind(final GL gl) throws GLException {
    gl.glEnable(GL2.GL_TEXTURE_3D);

    // Verify a texture can be enabled and mapped to the shader variable name
    validateTexID(gl, true);

    // Declare a 3D texture
    gl.glBindTexture(GL2.GL_TEXTURE_3D, texID);
    gl.glActiveTexture(GL.GL_TEXTURE0);

    // Will keep max or min texture value upon overflow on the X dimension
    gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);

    // Will keep max or min texture value upon overflow on the Y dimension
    gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);

    // Will keep max or min texture value upon overflow on the Z dimension
    gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_CLAMP);

    // Will apply linear interpolation when zooming in texture voxels
    gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

    // Will apply linear interpolation when zooming out texture voxels
    gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

    // Load buffer data into memory
    setTextureData(gl, buffer, shape);
  }
  
  public void unbind(final GL gl) {
    gl.glBindTexture(GL2.GL_TEXTURE_3D, 0);
  }

  /**
   * Load buffer data into memory
   * 
   * @param gl
   * @param buffer texture data
   * @param shape the dimensions of 3d texture.
   * @see https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glTexImage3D.xml
   */
  public void setTextureData(final GL gl, Buffer buffer, int[] shape) {
    // define how pixels are stored in memory
    gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

    // specify a 3 dimensional texture image with a single LOD, R float internal format, dynamical
    // number of voxel for width, height, depth, R float input format
    gl.getGL2().glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL.GL_R32F, shape[2], shape[1], shape[0], 1,
        GL2.GL_RED, GL.GL_FLOAT, buffer);

  }

  protected boolean validateTexID(final GL gl, final boolean throwException) {
    gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glEnable(GL2.GL_TEXTURE_3D);
    int id = gl.getGL2().glGetUniformLocation(shaderProgram.getProgramId(), "volumeTexture");

    if (id >= 0) {
      texID = id;
    } else {
      throw new GLException("Create texture ID invalid: texID " + texID + ", glerr 0x"
          + Integer.toHexString(gl.glGetError()));
    }

    return 0 != texID;
  }

  @Override
  public void draw(IPainter painter) {
    Camera cam = painter.getCamera();
    GL gl = ((NativeDesktopPainter) painter).getGL();

    if (!mounted) {
      mount(painter);
    }
    else {
      bind(gl);
    }

    colormapTexure.update(gl);

    doTransform(painter);

    float mvmatrix[] = new float[16];
    float projmatrix[] = new float[16];

    Coord3d eye = painter.getCamera().getEye();
    eye = eye.sub(cam.getTarget());
    eye = eye.normalizeTo(1);

    gl.getGL2().glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
    gl.getGL2().glGetFloatv(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);

    float[] pmat = mvmatrix.clone();

    float[] success = FloatUtil.invertMatrix(mvmatrix, pmat);

    float[] eye1 = new float[] {eye.x, eye.y, eye.z, 1};

    Coord3d range = bbox.getRange();

    float[] frange = new float[] {range.x, range.y, range.z, 1};

    if (success != null) {
      VectorUtil.normalizeVec3(frange);
      success = success.clone();
      // Arrays.fill(success, 0);
      success[0] /= frange[0];
      success[5] /= frange[1];
      success[10] /= (1 * frange[2]);
      FloatUtil.multMatrixVec(success, eye1, eye1);
      VectorUtil.normalizeVec3(eye1);
    }
    ////
    shaderProgram.bind(gl.getGL2());
    shaderProgram.setUniform(gl.getGL2(), "eye", eye1, 4);
    shaderProgram.setUniform(gl.getGL2(), "minMax", new float[] {min, max}, 2);
    int idt = gl.getGL2().glGetUniformLocation(shaderProgram.getProgramId(), "volumeTexture");
    int idc = gl.getGL2().glGetUniformLocation(shaderProgram.getProgramId(), "transfer");
    gl.getGL2().glUniform1i(idt, 0); // refer to GL_TEXTURE0, the volume
    gl.getGL2().glUniform1i(idc, 1); // refer to GL_TEXTURE1, the colormap


    gl.glEnable(GL2.GL_BLEND);
    gl.glEnable(GL2.GL_CULL_FACE);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    gl.getGL2().glPolygonMode(GL.GL_FRONT, GL2GL3.GL_FILL);
    gl.glCullFace(GL.GL_BACK);
    
    shapeVBO.draw(painter);
    shaderProgram.unbind(gl.getGL2());
    
    colormapTexure.unbind(gl);
    unbind(gl);

    //gl.glDisable(GL2.GL_CULL_FACE);

    
    if (disposed) {
      gl.glDeleteTextures(1, new int[] {texID}, 0);
      buffer = null;
      shaderProgram.destroy(gl.getGL2());
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
}
