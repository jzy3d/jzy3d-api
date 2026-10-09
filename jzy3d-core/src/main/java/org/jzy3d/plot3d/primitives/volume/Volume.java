package org.jzy3d.plot3d.primitives.volume;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.transform.Transform;

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
  public Volume(FloatBuffer buffer, int[] shape, BoundingBox3d bbox) {
    this.buffer = buffer;
    this.buffer.rewind();

    this.shape = shape;
    this.bbox = bbox;
  }

  @Override
  public void mount(IPainter painter) {

    if (!mounted) {
      bind(painter);

      mounted = true;
    }
  }

  @Override
  public boolean hasMountedOnce() {
    return mounted;
  }

  public void bind(final IPainter painter)  {
    painter.glEnable(GLConstants.GL_TEXTURE_3D);
    //gl.glActiveTexture(GLConstants.GL_TEXTURE0);

    // Generate texture
    int[] ids = new int[1];
    painter.glGenTextures(1, ids, 0);
    texID = ids[0];

    //System.out.println("Volume : " + texID);

    // Declare a 3D texture
    painter.glBindTexture(GLConstants.GL_TEXTURE_3D, texID);

    //gl.glTexEnvi(GLConstants.GL_TEXTURE_ENV, GLConstants.GL_TEXTURE_ENV_MODE, GLConstants.GL_REPLACE);

    // Will keep max or min texture value upon overflow on the X dimension
    painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP_TO_EDGE);

    // Will keep max or min texture value upon overflow on the Y dimension
    painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP_TO_EDGE);

    // Will keep max or min texture value upon overflow on the Z dimension
    painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_WRAP_R, GLConstants.GL_CLAMP_TO_EDGE);

    // Will apply linear interpolation when zooming in texture voxels
    painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_LINEAR);

    // Will apply linear interpolation when zooming out texture voxels
    painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_LINEAR);

    // Define how pixels are stored in memory
    painter.glPixelStorei(GLConstants.GL_UNPACK_ALIGNMENT, 1);
    //gl.glPixelStorei(GLConstants.GL_PACK_ALIGNMENT, 1);


    // https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glTexImage3D.xhtml

    // Specify a 3 dimensional texture image with a single LOD, RGBA float internal format,
    // dynamical
    // number of voxel for width, height, depth, no border, RGBA float input format
    painter.glTexImage3D(GLConstants.GL_TEXTURE_3D, 0, GLConstants.GL_RGBA, shape[0], shape[1], shape[2], 0,
        GLConstants.GL_RGBA, GLConstants.GL_FLOAT, buffer);

    // internal could be GL_COMPRESSED_RGBA

    painter.glBindTexture( GLConstants.GL_TEXTURE_3D, 0 );
  }

  @Override
  public void draw(IPainter painter) {
    if (!mounted) {
      mount(painter);
    }

    doTransform(painter);




   /* painter.glEnable(GLConstants.GL_BLEND);
    painter.glEnable(GLConstants.GL_CULL_FACE);
    painter.glBlendFunc(GLConstants.GL_SRC_ALPHA, GLConstants.GL_ONE_MINUS_SRC_ALPHA);
    painter.glPolygonMode(GLConstants.GL_FRONT, GLConstants.GL_FILL);
    painter.glCullFace(GLConstants.GL_BACK);*/
    // painter.glDisable(GLConstants.GL_CULL_FACE);

    //gl.glPolygonMode(GLConstants.GL_FRONT_AND_BACK, GLConstants.GL_FILL);


    painter.glDisable(GLConstants.GL_CULL_FACE);

    painter.glEnable(GLConstants.GL_ALPHA_TEST);
    //painter.glAlphaFunc(GLConstants.GL_GREATER, 0.03f);

    painter.glEnable(GLConstants.GL_BLEND);
    painter.glBlendFunc(GLConstants.GL_SRC_ALPHA, GLConstants.GL_ONE_MINUS_SRC_ALPHA);

    painter.glMatrixMode(GLConstants.GL_TEXTURE);

    painter.glEnable(GLConstants.GL_TEXTURE_3D);
    //gl.glActiveTexture(GLConstants.GL_TEXTURE0);
    painter.glBindTexture(GLConstants.GL_TEXTURE_3D, texID);



    //System.out.println(zInc);


    float texXmin = 0;
    float texXmax = shape[0];

    float texYmin = 0;
    float texYmax = shape[1];

    float texZmin = 0;
    float texZmax = shape[2];

    float zIncTex = 1;///texZmax;
    float zIncWorld = bbox.getZRange().getRange() / texZmax;


    float texZCurrent = texZmin;

    for (float zWorld = bbox.getZmin(); zWorld <= bbox.getZmax(); zWorld+=zIncWorld) {

      //System.out.println("texZCur:" + texZCurrent);

      //float texZ = z

      //System.out.println(zWorld + " in world is " + texZCurrent + " in texture");

      painter.glBegin(GLConstants.GL_QUADS);

      painter.glTexCoord3f(texXmin, texYmin, texZCurrent);
      painter.glVertex3f(bbox.getXmin(), bbox.getYmin(), zWorld);

      painter.glTexCoord3f(texXmax, texYmin, texZCurrent);
      painter.glVertex3f(bbox.getXmax(), bbox.getYmin(), zWorld);

      painter.glTexCoord3f(texXmax, texYmax, texZCurrent);
      painter.glVertex3f(bbox.getXmax(), bbox.getYmax(), zWorld);

      painter.glTexCoord3f(texXmin, texYmax, texZCurrent);
      painter.glVertex3f(bbox.getXmin(), bbox.getYmax(), zWorld);

      painter.glEnd();

      texZCurrent+=1;


    }


    if (disposed) {
      painter.glDeleteTextures(1, new int[] {texID}, 0);
      buffer = null;
    }

    painter.glBindTexture( GLConstants.GL_TEXTURE_3D, 0 );
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
