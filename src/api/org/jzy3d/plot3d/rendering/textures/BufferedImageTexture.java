package org.jzy3d.plot3d.rendering.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class BufferedImageTexture extends SharedTexture {
    public BufferedImageTexture(BufferedImage image) {
        super();
        this.image = image;
    }

    public Texture getTexture(GL2 gl) {
        if (texture == null)
            mount(gl);
        else { // execute onmount even if we did not mount
               // if( action != null )
               // action.execute();
        }
        return texture;
    }

    /** A GL2 context MUST be current. */
    public void mount(GL2 gl) {
        try {
            load(gl, image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        coords = texture.getImageTexCoords();
        halfWidth = texture.getWidth() / 2;
        halfHeight = texture.getHeight() / 2;
        // System.out.println("mount texture: " + file + " halfWidth=" +
        // halfWidth + " halfHeight=" + halfHeight);
    }

    protected void load(GL2 gl, BufferedImage image) throws GLException, IOException {
        texture = AWTTextureIO.newTexture(GLProfile.getDefault(), image, false); 
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR); // different from shared texture!
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
    }

    /** returns null*/
    public String getFile() {
        return file;
    }

    public TextureCoords getCoords() {
        return coords;
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    protected Texture texture;
    protected BufferedImage image;
    protected TextureCoords coords;
    protected float halfWidth;
    protected float halfHeight;
}
