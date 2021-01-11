package org.jzy3d.chart.swt;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.NativeAnimator;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.chart.factories.NativeChartFactory;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTNativeView;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.newt.opengl.GLWindow;

public class SWTChartFactory extends NativeChartFactory {
    private static final Logger logger = Logger.getLogger(SWTChartFactory.class);

    protected final Composite canvas;

    public SWTChartFactory(Composite canvas) {
    	super(new SWTPainterFactory());

        this.canvas = canvas;
    }

    public SWTChartFactory(Composite canvas, IPainterFactory painterFactory) {
    	super(painterFactory);
        this.canvas = canvas;
    }
    
    public static Chart chart(Composite parent) {
        SWTChartFactory f = new SWTChartFactory(parent);
        return f.newChart(Quality.Intermediate);
    }

    public static Chart chart(Composite parent, Quality quality) {
        SWTChartFactory f = new SWTChartFactory(parent);
        return f.newChart(quality);
    }

    /* */

    /**
     */
    @Override
    public Chart newChart(IChartFactory factory, Quality quality) {
        return new SWTChart(canvas, factory, quality);
    }
    
    /** Dedicated to {@link CanvasNewtSWT} implementation. */
    public NativeAnimator newAnimator(GLWindow canvas) {
        return new NativeAnimator(canvas);
    }

    public Composite getComposite() {
        return canvas;
    }

    /**
     * The AWTView support Java2d defined components (tooltips, background
     * images)
     */
    @Override
    public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        return new AWTNativeView(factory, scene, canvas, quality);
    }

    /** Provide AWT Texture loading for screenshots */
    @Override
    public Renderer3d newRenderer3D(View view, boolean traceGL, boolean debugGL) {
        return new AWTRenderer3d(view, traceGL, debugGL);
    }


    @Override
    public IChartFactory getFactory() {
        return this;
    }

    
}
