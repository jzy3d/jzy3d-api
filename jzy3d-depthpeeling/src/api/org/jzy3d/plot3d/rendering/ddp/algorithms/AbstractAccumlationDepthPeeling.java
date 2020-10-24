package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.rendering.ddp.AbstractDepthPeelingAlgorithm;

import com.jogamp.opengl.GL2;

public abstract class AbstractAccumlationDepthPeeling extends AbstractDepthPeelingAlgorithm {

    public int[] g_accumulationTexId = new int[2];
    public int[] g_accumulationFboId = new int[1];

    public AbstractAccumlationDepthPeeling() {
        super();
    }
    
    @Override
    public void init(Painter painter, GL2 gl, int width, int height) {
        initAccumulationRenderTargets(gl, width, height);
       
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);

        buildShaders(gl);
        buildFullScreenQuad(gl);
        buildFinish(gl);
    }
    
    @Override
    public void reshape(Painter painter, GL2 gl, int width, int height) {
        deleteAccumulationRenderTargets(gl);
        initAccumulationRenderTargets(gl, width, height);
    }

    protected void initAccumulationRenderTargets(GL2 gl, int g_imageWidth, int g_imageHeight) {
        gl.glGenTextures(2, g_accumulationTexId, 0);
    
        gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[0]);
        gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RGBA16F, g_imageWidth, g_imageHeight, 0, GL2.GL_RGBA, GL2.GL_FLOAT, null);
    
        gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[1]);
        gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_FLOAT_R32_NV, g_imageWidth, g_imageHeight, 0, GL2.GL_RGBA, GL2.GL_FLOAT, null);
    
        // gl.glTexImage2D( GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RGBA16F,
        // g_imageWidth, g_imageHeight, 0, GL2.GL_RGBA, GL2.GL_FLOAT, null);
    
        gl.glGenFramebuffers(1, g_accumulationFboId, 0);
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_accumulationFboId[0]);
        gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[0], 0);
        gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT1, GL2.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[1], 0);
    
    }

    protected void deleteAccumulationRenderTargets(GL2 gl) {
        gl.glDeleteFramebuffers(1, g_accumulationFboId, 0);
        gl.glDeleteTextures(2, g_accumulationTexId, 0);
    }
    


}