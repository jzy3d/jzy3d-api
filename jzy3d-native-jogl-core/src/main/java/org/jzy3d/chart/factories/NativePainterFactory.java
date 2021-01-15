package org.jzy3d.chart.factories;

import org.jzy3d.chart.NativeAnimator;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.symbols.SymbolHandler;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.image.IImageWrapper;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public abstract class NativePainterFactory implements IPainterFactory{
	IChartFactory chartFactory;
	
	GLCapabilities capabilities;

	/**
	 * Initialize a factory with a default desired {@link GLCapabilities} defined by
	 * {@link NativePainterFactory#getOffscreenCapabilities(GLProfile)} based on the
	 * detected {@link GLProfile}, either {@link GLProfile#GL2} or
	 * {@link GLProfile#GL2ES2} if {@link GLProfile#GL2} is not available. If none
	 * of these profile is available, an {@link UnsupportedOperationException} is
	 * thrown.
	 */
	public NativePainterFactory() {
		this(getOffscreenCapabilities(detectGLProfile()));
	}

	public NativePainterFactory(GLCapabilities capabilities) {
		this.capabilities = capabilities;
	}
	
	/** Return desired Open GL Capabilities */
	public GLCapabilities getCapabilities() {
		return capabilities;
	}
	
	// @Override
	/** Only needed by {@link INativeCanvas} */
	public Renderer3d newRenderer3D(View view, boolean traceGL, boolean debugGL) {
		return new Renderer3d(view, traceGL, debugGL);
	}

	// @Override
	/** Only needed by {@link INativeCanvas} */
	public Renderer3d newRenderer3D(View view) {
		return newRenderer3D(view, false, false);
	}

	
	@Override
	public IPainter newPainter() {
		return new NativeDesktopPainter();
	}

	@Override
    public NativeAnimator newAnimator(ICanvas canvas) {
        return new NativeAnimator((GLAutoDrawable)canvas);
    }

	@Override
    public SymbolHandler newSymbolHandler(IImageWrapper image){
		throw new NotImplementedException();
        //return new AWTNativeSymbolHandler(image);
    }

	public IChartFactory getChartFactory() {
		return chartFactory;
	}

	public void setChartFactory(IChartFactory chartFactory) {
		this.chartFactory = chartFactory;
	}
	
	
	/************ OPENGL PROFILE AND CAPABILITIES HELPERS ************/

	public static GLProfile detectGLProfile() {
		if (!(GLProfile.isAvailable(GLProfile.GL2) || GLProfile.isAvailable(GLProfile.GL2ES2))) {
			throw new UnsupportedOperationException("Jzy3d requires an OpenGL 2 or OpenGL 2 ES 2 hardware");
		}

		if (GLProfile.isAvailable(GLProfile.GL2)) {
			// Preferred profile = GL2
			return GLProfile.get(GLProfile.GL2);
		} else {
			// second option for Android = GL2ES2
			return GLProfile.get(GLProfile.GL2ES2);
		}
	}

	/**
	 * This profile has prove to fix the fact that using a raw GLCapabilities
	 * without settings let screenshot as gray only
	 */
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
