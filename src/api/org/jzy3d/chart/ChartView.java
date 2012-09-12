package org.jzy3d.chart;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.plot2d.rendering.CanvasAWT;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.Legend;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewPort;



/**
 * A {@link ChartView} allows displaying a 3d scene on the left,
 * and a set of {@link AbstractDrawable}'s {@link Legend} on the right.
 *
 * @author Martin Pernollet
 */
public class ChartView extends View {
    public ChartView(Scene scene, ICanvas canvas, Quality quality) {
        super(scene, canvas, quality);

        // display zones
        zone1 = new Rectangle(0, 0, 0, 0);
        zone2 = new Rectangle(0, 0, 0, 0);
    }

    public void showLayout() {
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
        addRenderer2d(layoutBorder);
    }


    /*******************************************************************/


    /**
     * Set the camera held by this view, and draw the scene graph.
     * Performs all transformations of eye, target coordinates to adapt the camera settings
     * to the scaled scene.
     */
    public void render(GL2 gl, GLU glu) {
        List<Legend> list = scene.getGraph().getLegends();
        boolean hasMeta = list.size() > 0;

        // Compute an optimal layout so that we use the minimal area for metadata
        float screenSeparator = 1.0f;
        if (hasMeta) {
            int minwidth = 0;
            for (Legend data : list) {
                minwidth += data.getMinimumSize().width;
            }
            screenSeparator = ((float) (canvas.getRendererWidth() - minwidth)) / ((float) canvas.getRendererWidth());///0.7f;
        }

        ViewPort sceneViewPort = ViewPort.slice(canvas.getRendererWidth(), canvas.getRendererHeight(), 0, screenSeparator);
        ViewPort backgroundViewPort = new ViewPort(canvas.getRendererWidth(), canvas.getRendererHeight());
        
        renderBackground(gl, glu, backgroundViewPort);
    	renderScene(gl, glu, sceneViewPort);
    	
        if (hasMeta)
            renderFaces(gl, glu, screenSeparator, 1.0f);
        
        // fix overlay on top of chart
        //System.out.println(scenePort);
        renderOverlay(gl, cam.getLastViewPort());
        //renderOverlay(gl);
        if( dimensionDirty )
        	dimensionDirty = false;
    }

    /**
     * Renders the metadata within the screen slice given by the left and right parameters.
     */
    protected void renderFaces(GL2 gl, GLU glu, float left, float right) {
        List<Legend> data = scene.getGraph().getLegends();

        float slice = (right - left) / (float) data.size();
        int k = 0;
        for (Legend layer : data) {
            layer.setStretchToFill(true);
            layer.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(), left + slice * (k++), left + slice * k);
            layer.render(gl, glu);
        }
    }

    /******************************************************************/

    Rectangle zone1;
	Rectangle zone2;
}

