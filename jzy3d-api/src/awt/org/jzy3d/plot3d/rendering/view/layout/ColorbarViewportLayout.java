package org.jzy3d.plot3d.rendering.view.layout;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.plot2d.rendering.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportBuilder;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

public class ColorbarViewportLayout implements IViewportLayout{
    protected float screenSeparator = 1.0f;
    protected boolean hasMeta = true;
    
    @Override
    public void update(Chart chart) {
        final Scene scene = chart.getScene();
        final ICanvas canvas = chart.getCanvas();
        final List<ILegend> list = scene.getGraph().getLegends();
        
        computeSeparator(canvas, list);
        sceneViewPort = ViewportBuilder.column(canvas, 0, screenSeparator);
        backgroundViewPort = new ViewportConfiguration(canvas);
    }

    public void computeSeparator(final ICanvas canvas, final List<ILegend> list) {
        hasMeta = list.size() > 0;
        if (hasMeta) {
            int minwidth = 0;
            for (ILegend data : list) {
                minwidth += data.getMinimumSize().width;
            }
            screenSeparator = ((float) (canvas.getRendererWidth() - minwidth)) / ((float) canvas.getRendererWidth());///0.7f;
        }
        else{
            screenSeparator = 1.0f;
        }
    }
    
    @Override
    public void render(GL gl, GLU glu, Chart chart){
        View view = chart.getView();
        view.renderBackground(gl, glu, backgroundViewPort);
        view.renderScene(gl, glu, sceneViewPort);

        renderLegends(gl, glu, chart);
        // fix overlay on top of chart
        view.renderOverlay(gl, view.getCamera().getLastViewPort());
    }
    
    protected void renderLegends(GL gl, GLU glu, Chart chart){
        if (hasMeta){
            Scene scene = chart.getScene();

            renderLegends(gl, glu, screenSeparator, 1.0f, scene.getGraph().getLegends(), chart.getCanvas());
        }
    }
    
    /**
     * Renders the legend within the screen slice given by the left and right parameters.
     */
    protected void renderLegends(GL gl, GLU glu, float left, float right, List<ILegend> data, ICanvas canvas) {
        float slice = (right - left) / data.size();
        int k = 0;
        for (ILegend layer : data) {
            layer.setViewportMode(ViewportMode.STRETCH_TO_FILL);
            layer.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(), left + slice * (k++), left + slice * k);
            layer.render(gl, glu);
        }
    }
    
    public void showLayout(AWTView view) {
        Renderer2d layoutBorder = new Renderer2d() {
            @Override
            public void paint(Graphics g) {
                if (pencil == null)
                    pencil = new CanvasAWT((Graphics2D) g);
                if (zone1.width > 0)
                    pencil.drawRect(null, zone1.x, zone1.y, zone1.width, zone1.height, true);
                if (zone2.width > 0)
                    pencil.drawRect(null, zone2.x, zone2.y, zone2.width, zone2.height, true);
            }
            CanvasAWT pencil = null;
        };
        view.addRenderer2d(layoutBorder);
    }
    
    protected Rectangle zone1 = new Rectangle(0, 0, 0, 0);
    protected Rectangle zone2 = new Rectangle(0, 0, 0, 0);
    protected ViewportConfiguration sceneViewPort;
    protected ViewportConfiguration backgroundViewPort;
}
