package org.jzy3d.plot3d.primitives.vbo;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.io.BufferUtil;
import org.jzy3d.io.Console;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;


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
    image = BufferUtil.newDirectByteBuffer(4 * nColors * 4);
    
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


  public void update(IPainter painter) {
    if (!isUpdate)
      return;
    setTextureData(painter, image, shape);
    isUpdate = false;
  }


  public void bind(final IPainter painter)  {
    
    painter.glEnable(GLConstants.GL_TEXTURE_1D);
    
    // Verify 
    validateTexID(painter, true);
    
    painter.glBindTexture(GLConstants.GL_TEXTURE_1D, texID);
    if (name != null) {
      painter.glActiveTexture(GLConstants.GL_TEXTURE1);
    }

    // Will keep max or min value pixel value if passing overflowing outside texture
    painter.glTexParameteri(GLConstants.GL_TEXTURE_1D, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP_TO_EDGE);

    // When zooming in, will choose the nearest pixel (no interpolation)
    painter.glTexParameteri(GLConstants.GL_TEXTURE_1D, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_NEAREST);

    // When zooming out, will choose the nearest pixel (no interpolation)
    painter.glTexParameteri(GLConstants.GL_TEXTURE_1D, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_NEAREST);

    // Store texture in memory
    setTextureData(painter, image, shape /* unused */);
  }
  
  public void unbind(IPainter painter) {
    if (name != null) {
      painter.glActiveTexture(GLConstants.GL_TEXTURE1);
    }
    painter.glBindTexture(GLConstants.GL_TEXTURE_1D, 0);
    painter.glDisable(GLConstants.GL_TEXTURE_1D);

    // let other drawables use the default texture unit
    painter.glActiveTexture(GLConstants.GL_TEXTURE0);
  }

  public void setTextureData(final IPainter painter, Buffer buffer, int[] shape /* unused */) {
    // define how pixels are stored in memory
    painter.glPixelStorei(GLConstants.GL_UNPACK_ALIGNMENT, 1);

    // specify a 1 dimensional texture image with a single LOD, RGBA float internal format, 256
    // pixels, RGBA float input format
    painter.glTexImage1D(GLConstants.GL_TEXTURE_1D, 0, GLConstants.GL_RGBA32F, nColors, 0, GLConstants.GL_RGBA, GLConstants.GL_FLOAT,
        buffer);
    
    // painter.glTexSubImage3D(GLConstants.GL_TEXTURE_3D,0,0, 0,0, shape[0], shape[1], shape[2],
    // GLConstants.GL_RGBA, GLConstants.GL_UNSIGNED_BYTE, buffer);
    // painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP_TO_EDGE);
    // painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP_TO_EDGE);
    // painter.glTexParameteri(GLConstants.GL_TEXTURE_3D, GLConstants.GL_TEXTURE_WRAP_R, GLConstants.GL_CLAMP_TO_EDGE);
  }

  public int getID() {
    return id;
  }

  protected boolean validateTexID(final IPainter painter, final boolean throwException) {
    // if a variable name is given, the shader reads the colormap through this sampler, bound to
    // texture unit 1
    if (name != null) {
      painter.glActiveTexture(GLConstants.GL_TEXTURE1);
      painter.glEnable(GLConstants.GL_TEXTURE_1D);
    }

    // generate a texture : a uniform location is not a texture name
    if (0 == texID) {
      if (null != painter) {
        final int[] tmp = new int[1];
        
        // generate a single texture and store its id
        painter.glGenTextures(1, tmp, 0);
        texID = tmp[0];
        
        // check if id is valid
        if (0 == texID && throwException) {
          throw new RuntimeException("Create texture ID invalid: texID " + texID + ", glerr 0x"
              + Integer.toHexString(painter.glGetError()));
        }
      } else if (throwException) {
        throw new RuntimeException("No GL context given, can't create texture ID");
      }
    }
    return 0 != texID;
  }


}
