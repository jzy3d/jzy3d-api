package org.jzy3d.chart.factories;

import org.jzy3d.chart.NativeAnimator;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;

import com.jogamp.opengl.GLAutoDrawable;

public abstract class NativePainterFactory implements IPainterFactory{

	@Override
	public Painter newPainter() {
		return new NativeDesktopPainter();
	}

	@Override
    public NativeAnimator newAnimator(ICanvas canvas) {
        return new NativeAnimator((GLAutoDrawable)canvas);
    }

}
