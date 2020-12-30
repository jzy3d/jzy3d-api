package org.jzy3d.chart.factories;

import org.jzy3d.chart.NativeAnimator;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;

import com.jogamp.opengl.GLAutoDrawable;

public abstract class NativePainterFactory implements IPainterFactory{
	IChartFactory chartFactory;
	
	@Override
	public IPainter newPainter() {
		return new NativeDesktopPainter();
	}

	@Override
    public NativeAnimator newAnimator(ICanvas canvas) {
        return new NativeAnimator((GLAutoDrawable)canvas);
    }

	public IChartFactory getChartFactory() {
		return chartFactory;
	}

	public void setChartFactory(IChartFactory chartFactory) {
		this.chartFactory = chartFactory;
	}
	
	
}
