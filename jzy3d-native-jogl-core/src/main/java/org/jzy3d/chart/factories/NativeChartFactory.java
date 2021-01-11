package org.jzy3d.chart.factories;

import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public abstract class NativeChartFactory extends ChartFactory{
	
	GLCapabilities capabilities;
	
	public NativeChartFactory() {
		this(null);
	}

	public NativeChartFactory(IPainterFactory painterFactory) {
		super(painterFactory);
		capabilities = getOffscreenCapabilities(detectGLProfile());
		capabilities.setHardwareAccelerated(true);
	}

	/** Return Open GL Capabilities */
	public GLCapabilities getCapabilities() {
		return capabilities;
	}
	
	protected ICanvas newOffscreenCanvas(NativeChartFactory factory, Scene scene, Quality quality, boolean traceGL,
			boolean debugGL) {
		return new OffscreenCanvas(factory, scene, quality, getCapabilities(), width, height, traceGL, debugGL);
	}
	
	//@Override
	/** Only needed by {@link INativeCanvas} */
    public Renderer3d newRenderer3D(View view, boolean traceGL, boolean debugGL) {
        return new Renderer3d(view, traceGL, debugGL);
    }

    //@Override
	/** Only needed by {@link INativeCanvas} */
    public Renderer3d newRenderer3D(View view) {
        return newRenderer3D(view, false, false);
    }

    
    /*@Override
    public Renderer newRenderer(View view, boolean traceGL, boolean debugGL) {
        return new NativeRenderer(view, traceGL, debugGL);
    }*/

	
	/************ PROFILE AND CAPABILITIES HELPERS ************/
	
	public static GLProfile detectGLProfile() {
        if (!(GLProfile.isAvailable(GLProfile.GL2) || GLProfile.isAvailable(GLProfile.GL2ES2))) {
            throw new UnsupportedOperationException("Jzy3d requires OpenGL 2 or OpenGL 2 ES 2");
        }

        if (GLProfile.isAvailable(GLProfile.GL2)) {
            // Preferred profile = GL2
            return GLProfile.get(GLProfile.GL2);
        } else {
            // second option for Android = GL2ES2
            return GLProfile.get(GLProfile.GL2ES2);
        }
    }
    
	/** This profile has prove to fix the fact that using a raw GLCapabilities without settings let screenshot as gray only */
    public static GLCapabilities getOffscreenCapabilities(GLProfile glp) {
        GLCapabilities caps = new GLCapabilities(glp); 
        caps.setHardwareAccelerated(true); 
        caps.setDoubleBuffered(false); 
        caps.setAlphaBits(8); 
        caps.setRedBits(8); 
        caps.setBlueBits(8); 
        caps.setGreenBits(8); 
        caps.setOnscreen(false);
        return caps;
    } 
}
