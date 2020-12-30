package org.jzy3d.plot3d.rendering.shaders;

import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class ShadedDrawable extends Shaderable{
    Drawable drawable;
    
    public ShadedDrawable(Drawable drawable, ShaderFilePair shaders){
        this.drawable = drawable;
        this.shaders = shaders;
    }
    
    @Override
    public void display(IPainter painter, GL2 gl, GLU glu) {
        executeProgram(painter, gl); 
        
        if(drawable!=null)
            drawable.draw(painter);
    }
}
