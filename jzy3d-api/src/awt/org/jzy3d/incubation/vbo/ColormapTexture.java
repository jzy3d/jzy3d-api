package org.jzy3d.incubation.vbo;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.GLBuffers;

public class ColormapTexture {
	
	private int texID;
    private ByteBuffer image;
    private int[] shape;
    private boolean isUpdate = false;
    private String name = null;
	private int id;
    
    
    public ColormapTexture(ColorMapper mapper, String name, int id) {
    	this(mapper);
    	this.name = name;
    	this.id = id;
    }
    
    public ColormapTexture(ColorMapper mapper) {
    	double min = mapper.getMin();
    	double max = mapper.getMax();
    	
    	int nColors = 256;
    	
    	image = GLBuffers.newDirectByteBuffer(4 * nColors *4);
    	
    	double step = (max-min)/nColors;
    	
    	for (int i = 0; i < nColors; i++) {
    		Color c = mapper.getColor(min + (i*step));
    		image.putFloat(c.r);
    		image.putFloat(c.g);
    		image.putFloat(c.b);
    		image.putFloat(c.a);
    	}
    	image.rewind();
    }
    
    public void updateColormap(ColorMapper mapper) {
    	double min = mapper.getMin();
    	double max = mapper.getMax();
    	
    	int nColors = 256;
    	
    	double step = (max-min)/nColors;
    	
    	for (int i = 0; i < nColors; i++) {
    		Color c = mapper.getColor(min + (i*step));
    		image.putFloat(c.r);
    		image.putFloat(c.g);
    		image.putFloat(c.b);
    		image.putFloat(c.a);
    	}
    	image.rewind();
    	
    	isUpdate = true;
    }
    
    public void update(GL gl) {
    	if (!isUpdate) return;
    	setTextureData(gl, image, shape);
    	isUpdate = false;
    }

	
	public void bind(final GL gl) throws GLException {
		gl.glEnable(GL2.GL_TEXTURE_1D);
        validateTexID(gl, true);
        gl.glBindTexture(GL2.GL_TEXTURE_1D, texID);
        if (name != null) {
        	gl.glActiveTexture(GL.GL_TEXTURE1);
        }
        gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
//        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
//        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MAG_FILTER,
                GL.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MIN_FILTER,
                GL.GL_NEAREST);
        setTextureData(gl,image,shape);
    }
	
	public void setTextureData(final GL gl, Buffer buffer, int[] shape) {
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		gl.getGL2().glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL.GL_RGBA32F, 256, 0, GL2.GL_RGBA, GL.GL_FLOAT, buffer);
//		gl.getGL2().glTexSubImage3D(GL2.GL_TEXTURE_3D,0,0, 0,0, shape[0], shape[1], shape[2], GL2ES2.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
//		gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
//		gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
//		gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);
	}
	
	public int getID() {
		return id;
	}
	
	private boolean validateTexID(final GL gl, final boolean throwException) {
		
		if (name != null) {
			gl.glActiveTexture(GL.GL_TEXTURE1);
			gl.glEnable(GL2.GL_TEXTURE_1D);
			int id = gl.getGL2().glGetUniformLocation(this.id, name);
			
			if ( id >= 0) {
				texID = id;
			}
		} else if( 0 == texID ) {
            if( null != gl ) {
                final int[] tmp = new int[1];
                gl.glGenTextures(1, tmp, 0);
                texID = tmp[0];
                if ( 0 == texID && throwException ) {
                    throw new GLException("Create texture ID invalid: texID "+texID+", glerr 0x"+Integer.toHexString(gl.glGetError()));
                }
            } else if ( throwException ) {
                throw new GLException("No GL context given, can't create texture ID");
            }
        }
        return 0 != texID;
    }
	
}
