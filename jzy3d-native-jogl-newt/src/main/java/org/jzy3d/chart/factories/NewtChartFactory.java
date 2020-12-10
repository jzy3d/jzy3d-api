package org.jzy3d.chart.factories;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTNativeView;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

/**
 * Still using some AWT components
 * 
 * @author martin
 *
 */
public class NewtChartFactory extends NativeChartFactory {
    static Logger logger = Logger.getLogger(NewtChartFactory.class);
    
    public static Chart chart() {
        return chart(Quality.Intermediate);
    }

    public static Chart chart(Quality quality) {
        NewtChartFactory f = new NewtChartFactory();
        return f.newChart(quality);
    }

    /* */
    
    public NewtChartFactory() {
    	super(new NewtPainterFactory());
    }

    public NewtChartFactory(IPainterFactory painterFactory) {
    	super(painterFactory);
    }


    // TODO : create a NewtChart for consistency
    @Override
    public Chart newChart(IChartFactory factory, Quality quality) {
        return new AWTChart(factory, quality);
    }

    @Override
    public IViewportLayout newViewportLayout() {
        return new ViewAndColorbarsLayout();
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
    public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL) {
        return new AWTRenderer3d(view, traceGL, debugGL);
    }


    @Override
    public IChartFactory getFactory() {
        return this;
    }

    public JFrame newFrame(JPanel panel) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // ignore failure to set default look en feel;
        }
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    
}
