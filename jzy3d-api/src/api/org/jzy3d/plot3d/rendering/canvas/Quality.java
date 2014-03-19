package org.jzy3d.plot3d.rendering.canvas;

import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.opengl.util.Animator;

/** Provides a structure for setting the rendering quality, i.e., the tradeoff
 * between computation speed, and graphic quality. Following mode have an impact
 * on the way the {@link View} makes its GL2 initialization.
 * The {@link Quality} may also activate an {@link AbstractOrderingStrategy} algorithm
 * that enables clean alpha results.
 * 
 * Fastest:
 * - No transparency, no color shading, just handle depth buffer.
 * 
 * Intermediate:
 * - include Fastest mode abilities
 * - Color shading, mainly usefull to have interpolated colors on polygons.
 * 
 * Advanced:
 * - include Intermediate mode abilities
 * - Transparency (GL2 alpha blending + polygon ordering in scene graph)
 *  
 * Nicest:
 * - include Advanced mode abilities
 * - Anti aliasing on wires
 * 
 * 
 * Toggling rendering model: one may either choose to have a repaint-on-demand 
 * or repaint-continuously model. Setting isAnimated(false) will desactivate a
 * the {@link Animator} updating the choosen {@link ICanvas} implementation.
 * 
 * setAutoSwapBuffer(false) will equaly configure the {@link ICanvas}.
 * 
 * @author Martin Pernollet
 */
public class Quality {
	
    
    /**
     * Enables alpha, color interpolation and antialiasing on lines, points, and polygons.
     */
	public static final Quality Nicest       = new Quality(true, true, true, true, true, true, true);
	/**
	 * Enables alpha and color interpolation.
	 */
	public static final Quality Advanced     = new Quality(true, true, true, false, false, false, true);
	/**
     * Enables color interpolation.
     */
	public static final Quality Intermediate = new Quality(true, false, true, false, false, false, true);
	/**
     * Minimal quality to allow fastest rendering (no alpha, interpolation or antialiasing).
     */
	public static final Quality Fastest      = new Quality(true, false, false, false, false, false, true);
	
	/** Initialize a Quality configuration for a View.*/
	public Quality(boolean depthActivated, boolean alphaActivated, boolean smoothColor, boolean smoothPoint, boolean smoothLine, boolean smoothPolygon, boolean disableDepth){
		this.depthActivated = depthActivated;
		this.alphaActivated = alphaActivated;
		this.smoothColor    = smoothColor;
		this.smoothPoint    = smoothPoint;
        this.smoothLine     = smoothLine;
        this.smoothPolygon  = smoothPolygon;
		this.disableDepthBufferWhenAlpha = disableDepth;
	}
	
	public boolean isDepthActivated() {
		return depthActivated;
	}
	
	public void setDepthActivated(boolean depthActivated) {
		this.depthActivated = depthActivated;
	}
	
	public boolean isAlphaActivated() {
		return alphaActivated;
	}
	
	public void setAlphaActivated(boolean alphaActivated) {
		this.alphaActivated = alphaActivated;
	}
	
	public boolean isSmoothColor() {
		return smoothColor;
	}
	
	public void setSmoothColor(boolean smoothColor) {
		this.smoothColor = smoothColor;
	}
	
	public boolean isSmoothLine() {
		return smoothLine;
	}
	
	public void setSmoothEdge(boolean smoothLine) {
		this.smoothLine = smoothLine;
	}
	
	public boolean isSmoothPoint() {
        return smoothPoint;
    }
    
    public void setSmoothPoint(boolean smoothPoint) {
        this.smoothPoint = smoothPoint;
    }
    
    public boolean isSmoothPolygon() {
        return smoothPolygon;
    }
    
    public void setSmoothPolygon(boolean smoothPolygon) {
        this.smoothPolygon = smoothPolygon;
    }
	
	public boolean isDisableDepthBufferWhenAlpha() {
		return disableDepthBufferWhenAlpha;
	}

	public void setDisableDepthBufferWhenAlpha(boolean disableDepthBufferWhenAlpha) {
		this.disableDepthBufferWhenAlpha = disableDepthBufferWhenAlpha;
	}
	
	public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean isAnimated) {
        this.isAnimated = isAnimated;
    }

    public boolean isAutoSwapBuffer() {
        return isAutoSwapBuffer;
    }

    public void setAutoSwapBuffer(boolean isAutoSwapBuffer) {
        this.isAutoSwapBuffer = isAutoSwapBuffer;
    }

    private boolean depthActivated;
	private boolean alphaActivated;
	private boolean smoothColor;
	private boolean smoothPoint;
    private boolean smoothLine;
    private boolean smoothPolygon;
	protected boolean disableDepthBufferWhenAlpha;
	protected boolean isAnimated = true;
	protected boolean isAutoSwapBuffer = true;
}
