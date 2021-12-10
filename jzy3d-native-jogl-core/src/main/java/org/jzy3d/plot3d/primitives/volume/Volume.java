package org.jzy3d.plot3d.primitives.volume;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLException;

/**
 * 
 * https://developer.nvidia.com/gpugems/gpugems/part-vi-beyond-triangles/chapter-39-volume-rendering-techniques
 * https://www.codeproject.com/Articles/352270/Getting-Started-with-Volume-Rendering-using-OpenGL
 * https://community.khronos.org/t/volume-rendering-with-3d-textures/73681
 */
public class Volume extends Drawable implements IGLBindedResource {

  /** The GL texture ID. */
  protected int texID;
  protected Buffer buffer;
  protected int[] shape;
  protected boolean mounted = false;

  protected boolean disposed;
  //protected ColorMapper mapper;

  /**
   * Instanciate a drawable volume.
   * 
   * @param buffer provides (x,y,z,V) tuples where (x,y,z) are voxel index in the volume and V the
   *        value used for coloring voxels
   * @param shape a 3 element array indicating the number of voxels for each dimension
   * @param mapper the colormap handler that will apply a colormap to a value range
   * @param bbox the real world range of each axis, since the input buffer provide tuples with index
   *        and not coordinates
   */
  public Volume(Buffer buffer, int[] shape, BoundingBox3d bbox) {
    this.buffer = buffer;
    this.buffer.rewind();

    this.shape = shape;
    this.bbox = bbox;
  }

  @Override
  public void mount(IPainter painter) {
    GL2 gl = ((NativeDesktopPainter) painter).getGL().getGL2();

    if (!mounted) {
      bind(gl);

      mounted = true;
    }
  }

  @Override
  public boolean hasMountedOnce() {
    return mounted;
  }

  public void bind(final GL2 gl) throws GLException {
    gl.glEnable(GL2.GL_TEXTURE_3D);
    gl.glActiveTexture(GL.GL_TEXTURE0);

    // Generate texture
    IntBuffer ib = Buffers.newDirectIntBuffer(1);
    gl.glGenTextures(1, ib);
    texID = ib.get(0);

    System.out.println("Volume : " + texID);

    // Declare a 3D texture
    gl.glBindTexture(GL2.GL_TEXTURE_3D, texID);

    //gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);

    /*// Will keep max or min texture value upon overflow on the X dimension
    gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);

    // Will keep max or min texture value upon overflow on the Y dimension
    gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);*/

    // Will keep max or min texture value upon overflow on the Z dimension
    gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_CLAMP_TO_BORDER);
    
    // Will apply linear interpolation when zooming in texture voxels
    gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

    // Will apply linear interpolation when zooming out texture voxels
    gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);

    // Define how pixels are stored in memory
    //gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
    //gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);


    // https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glTexImage3D.xhtml

    // Specify a 3 dimensional texture image with a single LOD, RGBA float internal format,
    // dynamical
    // number of voxel for width, height, depth, no border, RGBA float input format
    gl.getGL2().glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL2.GL_COMPRESSED_RGBA, shape[2], shape[1], shape[0], 0,
        GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);

    // internal could be GL_COMPRESSED_RGBA

    //gl.glBindTexture( GL2.GL_TEXTURE_3D, 0 );
  }

  @Override
  public void draw(IPainter painter) {
    if (!mounted) {
      mount(painter);
    }

    doTransform(painter);


    GL2 gl = ((NativeDesktopPainter) painter).getGL().getGL2();


   /* gl.glEnable(GL2.GL_BLEND);
    gl.glEnable(GL2.GL_CULL_FACE);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    gl.glPolygonMode(GL.GL_FRONT, GL2GL3.GL_FILL);
    gl.glCullFace(GL.GL_BACK);*/
    // gl.glDisable(GL2.GL_CULL_FACE);

    //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

    gl.glEnable(GL2.GL_ALPHA_TEST);
    //gl.getGL2().glAlphaFunc(GL.GL_GREATER, 0.03f);

    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    gl.getGL2().glMatrixMode(GL.GL_TEXTURE);

    gl.glEnable(GL2.GL_TEXTURE_3D);
    gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glBindTexture(GL2.GL_TEXTURE_3D, texID);


    
    //System.out.println(zInc);
    
    
    float texXmin = 0;
    float texXmax = 1;

    float texYmin = 0;
    float texYmax = 1;

    //float texZmin = 0;
    //float texZmax = 1;

    float zInc = bbox.getZRange().getRange() / shape[2];


    float texZ = 0;
    for (float z = bbox.getZmin(); z <= bbox.getZmax(); z+=zInc) {
      
      //float texZ = z
      
      //System.out.println(z + " > " + texZ);
      
      gl.glBegin(GL2.GL_QUADS);

      gl.glTexCoord3f(texXmin, texYmin, texZ);
      gl.glVertex3f(bbox.getXmin(), bbox.getYmin(), z);

      gl.glTexCoord3f(texXmax, texYmin, texZ);
      gl.glVertex3f(bbox.getXmax(), bbox.getYmin(), z);

      gl.glTexCoord3f(texXmax, texYmax, texZ);
      gl.glVertex3f(bbox.getXmax(), bbox.getYmax(), z);

      gl.glTexCoord3f(texXmin, texYmax, texZ);
      gl.glVertex3f(bbox.getXmin(), bbox.getYmax(), z);

      gl.glEnd();
      
      texZ+=1;

    }


    if (disposed) {
      gl.glDeleteTextures(1, new int[] {texID}, 0);
      buffer = null;
    }
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    // TODO Auto-generated method stub

  }

  @Override
  public void dispose() {
    disposed = true;
  }

  @Override
  public void updateBounds() {
    // nothing to do
  }

}
