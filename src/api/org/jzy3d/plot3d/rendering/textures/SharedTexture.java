package org.jzy3d.plot3d.rendering.textures;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import org.jzy3d.plot3d.primitives.IGLBindedResource;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

public class SharedTexture implements IGLBindedResource{
    protected SharedTexture() {
        this.texture = null;
    }
    
    public SharedTexture(String file) {
        this.texture = null;
        this.file = file;
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
            load(gl, file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        coords = texture.getImageTexCoords();
        halfWidth = texture.getWidth() / 2;
        halfHeight = texture.getHeight() / 2;
        // System.out.println("mount texture: " + file + " halfWidth=" +
        // halfWidth + " halfHeight=" + halfHeight);
    }
    
    public boolean hasMountedOnce(){
        return texture!=null;
    }

    protected void load(GL2 gl, String fileName) throws GLException, IOException {
        texture = TextureIO.newTexture(new File(fileName), false);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    }

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

    /*
     * public BoundingBox3d getBounds(PlaneAxis plane){
     * 
     * }
     */

    protected Texture texture;
    protected String file;
    protected TextureCoords coords;
    protected float halfWidth;
    protected float halfHeight;
}
