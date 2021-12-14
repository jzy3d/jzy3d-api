package org.jzy3d.plot3d.primitives.vbo;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.io.BufferUtil;
import org.jzy3d.io.Console;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.GLBuffers;


/**
 * 
 * @see https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glTexImage1D.xhtml
 * @see https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glTexParameter.xhtml
 * 
 */
public class ColormapTexture {

  protected int id;
  protected int texID;
  
  protected ByteBuffer image;
  protected int[] shape;
  
  protected boolean isUpdate = false;
  protected String name = null;
  
  protected int nColors = 256;



  public ColormapTexture(ColorMapper mapper, String name, int id) {
    this(mapper);
    this.name = name;
    this.id = id;
  }

  public ColormapTexture(ColorMapper mapper) {
    image = GLBuffers.newDirectByteBuffer(4 * nColors * 4);
    
    feedBufferWithColormap(mapper);
  }

  public void updateColormap(ColorMapper mapper) {
    feedBufferWithColormap(mapper);

    isUpdate = true;
  }
  
  protected void feedBufferWithColormap(ColorMapper mapper) {
    double min = mapper.getMin();
    double max = mapper.getMax();

    double step = (max - min) / nColors;

    for (int i = 0; i < nColors; i++) {
      Color c = mapper.getColor(min + (i * step));
      image.putFloat(c.r);
      image.putFloat(c.g);
      image.putFloat(c.b);
      image.putFloat(c.a);
      
      //Console.println(c);
    }

    BufferUtil.rewind(image);
  }


  public void update(GL gl) {
    if (!isUpdate)
      return;
    setTextureData(gl, image, shape);
    isUpdate = false;
  }


  public void bind(final GL gl) throws GLException {
    
    gl.glEnable(GL2.GL_TEXTURE_1D);
    
    // Verify 
    validateTexID(gl, true);
    
    gl.glBindTexture(GL2.GL_TEXTURE_1D, texID);
    if (name != null) {
      gl.glActiveTexture(GL.GL_TEXTURE1);
    }

    // Will keep max or min value pixel value if passing overflowing outside texture
    gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);

    // When zooming in, will choose the nearest pixel (no interpolation)
    gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

    // When zooming out, will choose the nearest pixel (no interpolation)
    gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);

    // Store texture in memory
    setTextureData(gl, image, shape /* unused */);
  }
  
  public void unbind(GL gl) {
    gl.glBindTexture(GL2.GL_TEXTURE_1D, 0);
  }

  public void setTextureData(final GL gl, Buffer buffer, int[] shape /* unused */) {
    // define how pixels are stored in memory
    gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

    // specify a 1 dimensional texture image with a single LOD, RGBA float internal format, 256
    // pixels, RGBA float input format
    gl.getGL2().glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL.GL_RGBA32F, nColors, 0, GL2.GL_RGBA, GL.GL_FLOAT,
        buffer);
    
    // gl.getGL2().glTexSubImage3D(GL2.GL_TEXTURE_3D,0,0, 0,0, shape[0], shape[1], shape[2],
    // GL2ES2.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
    // gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
    // gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
    // gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);
  }

  public int getID() {
    return id;
  }

  protected boolean validateTexID(final GL gl, final boolean throwException) {
    // if a variable name is given, retrieve the existing texture ID from it
    if (name != null) {
      gl.glActiveTexture(GL.GL_TEXTURE1);
      
      
      gl.glEnable(GL2.GL_TEXTURE_1D);
      
      // retrieve the location of a uniform (global variable) stored in GPU through a shader
      int id = gl.getGL2().glGetUniformLocation(this.id, name);

      if (id >= 0) {
        texID = id;
      }
    } 
    // otherwise generate a texture
    else if (0 == texID) {
      if (null != gl) {
        final int[] tmp = new int[1];
        
        // generate a single texture and store its id
        gl.glGenTextures(1, tmp, 0);
        texID = tmp[0];
        
        // check if id is valid
        if (0 == texID && throwException) {
          throw new GLException("Create texture ID invalid: texID " + texID + ", glerr 0x"
              + Integer.toHexString(gl.glGetError()));
        }
      } else if (throwException) {
        throw new GLException("No GL context given, can't create texture ID");
      }
    }
    return 0 != texID;
  }

}
