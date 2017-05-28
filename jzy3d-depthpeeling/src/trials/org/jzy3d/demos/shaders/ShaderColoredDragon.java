package org.jzy3d.demos.shaders;

import org.jzy3d.io.obj.OBJFileLoader;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;

public class ShaderColoredDragon {
    public static void main(String[] args){
        OBJFileLoader loader = new OBJFileLoader("models/dragon.obj");
        DrawableVBO j = new DrawableVBO(loader);
    }
}
