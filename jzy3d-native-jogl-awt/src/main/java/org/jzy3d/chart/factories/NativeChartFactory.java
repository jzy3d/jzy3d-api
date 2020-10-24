package org.jzy3d.chart.factories;

import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.painters.Painter;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public abstract class NativeChartFactory extends ChartComponentFactory{
	
	GLCapabilities capabilities;
	
	public NativeChartFactory() {
		capabilities = new GLCapabilities(detectGLProfile());
		capabilities.setHardwareAccelerated(false);
	}

	/** Return Open GL Capabilities */
	public GLCapabilities getCapabilities() {
		return capabilities;
	}
	
	public Painter newPainter() {
		return new NativeDesktopPainter();
	}
	
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
