package org.jzy3d.plot3d.rendering.ddp;

import java.io.File;
import java.net.URL;

import org.jzy3d.plot3d.primitives.IGLRenderer;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public abstract class AbstractDepthPeelingAlgorithm implements IDepthPeelingAlgorithm{
    public final static float MAX_DEPTH = 1.0f;
    
    protected int g_drawBuffers[] = { GL2.GL_COLOR_ATTACHMENT0, GL2.GL_COLOR_ATTACHMENT1, GL2.GL_COLOR_ATTACHMENT2, GL2.GL_COLOR_ATTACHMENT3, GL2.GL_COLOR_ATTACHMENT4, GL2.GL_COLOR_ATTACHMENT5, GL2.GL_COLOR_ATTACHMENT6 };
    public int g_quadDisplayList;
    public int g_numPasses = 1;
    public int g_numGeoPasses = 0;
    public boolean g_useOQ = true;  
    public float[] g_white = new float[] { 1.0f, 1.0f, 1.0f };
    public float[] g_black = new float[] { 0.0f };
    public float[] g_backgroundColor = g_white;
    public float[] g_opacity = new float[] { 0.6f };
    public int[] g_queryId = new int[1];


    GLU glu = new GLU();


    public AbstractDepthPeelingAlgorithm() {
    }
    
    protected abstract void buildShaders(GL2 gl);
    protected abstract void destroyShaders(GL2 gl);
    
    protected void reloadShaders(GL2 gl) {
        destroyShaders(gl);
        buildShaders(gl);
    }
    
    protected void buildFullScreenQuad(GL2 gl) {
        GLU glu = GLU.createGLU(gl);
    
        g_quadDisplayList = gl.glGenLists(1);
        gl.glNewList(g_quadDisplayList, GL2.GL_COMPILE);
    
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0f, 1.0f, 0.0f, 1.0f);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glBegin(GL2.GL_QUADS);
        {
            gl.glVertex2f(0.0f, 0.0f);
            gl.glVertex2f(1.0f, 0.0f);
            gl.glVertex2f(1.0f, 1.0f);
            gl.glVertex2f(0.0f, 1.0f);
        }
        gl.glEnd();
        gl.glPopMatrix();
    
        gl.glEndList();
    }
    
    public void buildFinish(GL2 gl) {
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_NORMALIZE);
        gl.glGenQueries(1, g_queryId, 0);
    }    
    
    /* ACTUAL RENDERING */
    
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

    protected void tasksToRender(GL2 gl) {
        tasksToRender.draw(gl, glu, null);
        incrementGeoPasses();
    }
    
    protected void resetNumPass(){
        g_numGeoPasses = 0;
    }
    
    protected void incrementGeoPasses(){
        g_numGeoPasses++;
    }
    
    @Override
    public void dispose(GL2 gl){
        destroyShaders(gl);
    }
    
    protected URL shader(String glsl){
        return getClass().getClassLoader().getResource(File.separator + "org" 
                                                     + File.separator + "jzy3d"
                                                     + File.separator + "plot3d"
                                                     + File.separator + "rendering"
                                                     + File.separator + "ddp"
                                                     + File.separator + "algorithms"
                                                     + File.separator + glsl);
    }
}