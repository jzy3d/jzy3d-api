package org.jzy3d.plot3d.rendering.legends.series;

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
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

public class LegendViewportLayout implements IViewportLayout{
    protected float screenSeparator = 1.0f;
    protected boolean hasMeta = true;
    
    @Override
    public void update(Chart chart) {
        final Scene scene = chart.getScene();
        final ICanvas canvas = chart.getCanvas();
        final List<ILegend> list = scene.getGraph().getLegends();
        computeScreenSeparator(canvas, list);
        sceneViewPort = ViewportBuilder.column(canvas, 0, 1);// <<< change this
        backgroundViewPort = new ViewportConfiguration(canvas);
    }

    public void computeScreenSeparator(final ICanvas canvas, final List<ILegend> list) {
        hasMeta = list.size() > 0;
        if (hasMeta) {
            int minwidth = 0;
            for (ILegend data : list) {
                minwidth += data.getMinimumSize().width;
                break;
            }
            screenSeparator = ((float) (canvas.getRendererWidth() - minwidth)) / ((float) canvas.getRendererWidth());
        }
        else{
            screenSeparator = 1.0f;
        }
    }
    
    @Override
    public void render(GL gl, GLU glu, Chart chart){
        View view = chart.getView();
        view.renderBackground(backgroundViewPort);
        view.renderScene(sceneViewPort);
        List<ILegend> legends = chart.getScene().getGraph().getLegends();
        if (hasMeta)
            renderLegends(gl, glu, screenSeparator, 1.0f, legends, chart.getCanvas());
        view.renderOverlay(view.getCamera().getLastViewPort());
    }
    
    protected void renderLegends(GL gl, GLU glu, float left, float right, List<ILegend> data, ICanvas canvas) {
        ILegend layer = data.get(data.size()-1);
        //System.out.println(layer + " " +left + " " + right);
        layer.setViewportMode(ViewportMode.STRETCH_TO_FILL);
        layer.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(), left , right);
        layer.render(gl, glu);
    }
    
    public void showLayout(AWTView view) {
        Renderer2d layoutBorder = new Renderer2d() {
            @Override
            public void paint(Graphics g, int canvasWidth, int canvasHeight) {
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
