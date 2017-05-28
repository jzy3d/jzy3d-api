package org.jzy3d.plot3d.rendering.shaders;

import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.plot3d.primitives.IGLRenderer;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;


public class Shaderable implements IShaderable{
    public GLSLProgram program;    
    protected GLU glu = new GLU();
    protected ShaderFilePair shaders = new ShaderFilePair(Shaderable.class, "shade_vertex.glsl", "shade_fragment.glsl");

    @Override
    public void init(GL2 gl, int width, int height) {
        loadProgram(gl);
    }

    @Override
    public void display(GL2 gl, GLU glu) {
        executeProgram(gl); 
    }

    @Override
    public void reshape(GL2 gl, int width, int height) {
    }
   
    @Override
    public void dispose(GL2 gl) {
        destroyProgram(gl);
    }
   
    /* PROGRAM */

    // http://www.opengl.org/wiki/GLSL_:_common_mistakes
    protected void loadProgram(GL2 gl) {
        program = new GLSLProgram();
        program.loadAndCompileVertexShader(gl, shaders.getVertexURL());
        program.loadAndCompileFragmentShader(gl, shaders.getFragmentURL());
        program.link(gl);
    }

    protected void destroyProgram(GL2 gl) {
        program.destroy(gl);
    }

    protected void executeProgram(GL2 gl) {
        float[] alpha = new float[] { 0.6f };
        
        program.bind(gl); 
        //program.setUniform(gl, "Alpha", alpha, 1);
        renderTasks(gl);        
        program.unbind(gl);
    }
    

    
    /* ACTUAL RENDERING */
    
    protected void renderTasks(GL2 gl) {
        tasksToRender.draw(gl, glu, null);
    }

    IGLRenderer tasksToRender = new IGLRenderer() {
        @Override
        public void draw(GL gl, GLU glu, Camera cam) {
            throw new RuntimeException("nothing to render?!");
        }
    };
    
    @Override
    public IGLRenderer getTasksToRender() {
        return tasksToRender;
    }

    @Override
    public void setTasksToRender(IGLRenderer tasksToRender) {
        this.tasksToRender = tasksToRender;
    }
}
