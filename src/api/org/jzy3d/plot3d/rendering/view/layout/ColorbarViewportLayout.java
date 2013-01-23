package org.jzy3d.plot3d.rendering.view.layout;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.chart.Chart;
import org.jzy3d.plot2d.rendering.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.Legend;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportBuilder;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

public class ColorbarViewportLayout implements IViewportLayout{
    protected float screenSeparator = 1.0f;
    protected boolean hasMeta = true;
    
    @Override
    public void update(Chart chart) {
        final Scene scene = chart.getScene();
        final ICanvas canvas = chart.getCanvas();
        final List<Legend> list = scene.getGraph().getLegends();
        
        // Compute an optimal layout so that we use the minimal area for metadata
        hasMeta = list.size() > 0;
        if (hasMeta) {
            int minwidth = 0;
            for (Legend data : list) {
                minwidth += data.getMinimumSize().width;
            }
            screenSeparator = ((float) (canvas.getRendererWidth() - minwidth)) / ((float) canvas.getRendererWidth());///0.7f;
        }
        else{
            screenSeparator = 1.0f;
        }
        
        sceneViewPort = ViewportBuilder.column(canvas, 0, screenSeparator);
        backgroundViewPort = new ViewportConfiguration(canvas);
    }

    
    @Override
    public void render(GL2 gl, GLU glu, Chart chart){
        View view = chart.getView();
        view.renderBackground(gl, glu, backgroundViewPort);
        view.renderScene(gl, glu, sceneViewPort);

        Scene scene = chart.getScene();
        if (hasMeta)
            renderLegends(gl, glu, screenSeparator, 1.0f, scene.getGraph().getLegends(), chart.getCanvas());

        // fix overlay on top of chart
        //System.out.println(scenePort);
        view.renderOverlay(gl, view.getCamera().getLastViewPort());
    }
    
    /**
     * Renders the legend within the screen slice given by the left and right parameters.
     */
    protected void renderLegends(GL2 gl, GLU glu, float left, float right, List<Legend> data, ICanvas canvas) {
        float slice = (right - left) / (float) data.size();
        int k = 0;
        for (Legend layer : data) {
            layer.setViewportMode(ViewportMode.STRETCH_TO_FILL);
            layer.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(), left + slice * (k++), left + slice * k);
            layer.render(gl, glu);
        }
    }
    
    public void showLayout(View view) {
        Renderer2d layoutBorder = new Renderer2d() {
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
    

    Rectangle zone1 = new Rectangle(0, 0, 0, 0);
    Rectangle zone2 = new Rectangle(0, 0, 0, 0);

    protected ViewportConfiguration sceneViewPort;
    protected ViewportConfiguration backgroundViewPort;
}
