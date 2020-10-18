package org.jzy3d.plot3d.rendering.textures;

import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class BufferedImageTexture extends SharedTexture {
    public BufferedImageTexture(BufferedImage image) {
        super();
        this.image = image;
        this.textureMagnificationFilter = GL.GL_LINEAR;
        this.textureMinificationFilter = GL.GL_LINEAR;
    }

    @Override
    public Texture getTexture(GL gl) {
        if (texture == null)
            mount(gl);
        else { // execute onmount even if we did not mount
               // if( action != null )
               // action.execute();
        }
        return texture;
    }

    /** A GL2 context MUST be current. */
    @Override
    public void mount(GL gl) {
        try {
            load(gl, image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        coords = texture.getImageTexCoords();
        halfWidth = texture.getWidth() / 2;
        halfHeight = texture.getHeight() / 2;
    }

    protected void load(GL gl, BufferedImage image) {
        texture = AWTTextureIO.newTexture(gl.getGLProfile(), image, useMipMap); //GLProfile.getDefault()
              
        if(textureMagnificationFilter!=-1)
            texture.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, textureMagnificationFilter);
        if(textureMinificationFilter!=-1)
            texture.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, textureMinificationFilter);
    }
    
    protected BufferedImage image;
}
