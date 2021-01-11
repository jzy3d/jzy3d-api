package org.jzy3d.plot3d.rendering.view.layout;

import java.util.ArrayList;
//import java.awt.Rectangle;
import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportBuilder;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

/**
 * This class handles the layout of multiple OpenGL viewports
 * <ul>
 * <li>The {@link View} which handles its viewport with the {@link Camera}. If the view is and {@link AWTView} or children, it has its own overlay
 * <li>The {@link ILegend} objects which handle their viewport on their own. They are added to the right of the chart according to the number of {@link Drawable} having a {@link ILegend} set such as {@link AWTColorbarLegend}
 * </ul>
 * 
 * This allow making a composition of 3D and 2D content in a single screen.
 * 
 * @author Martin Pernollet
 */
public class ViewAndColorbarsLayout implements IViewportLayout{
    protected float screenSeparator = 1.0f;
    protected boolean hasMeta = true;
    
    @Override
    public void update(Chart chart) {
        final ICanvas canvas = chart.getCanvas();
        final List<ILegend> list = getLegends(chart);
        
        computeSeparator(canvas, list);
        sceneViewport = ViewportBuilder.column(canvas, 0, screenSeparator);
        backgroundViewport = new ViewportConfiguration(canvas);
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
    public void render(IPainter painter, Chart chart){
    	View view = chart.getView();
        view.renderBackground(backgroundViewport);
        view.renderScene(sceneViewport);

        renderLegends(painter, chart);

        
        // fix overlay on top of chart
        view.renderOverlay(view.getCamera().getLastViewPort());
    }
    
    protected void renderLegends(IPainter painter, Chart chart){
        if (hasMeta){
            renderLegends(painter, screenSeparator, 1.0f, getLegends(chart), chart.getCanvas());
        }
    }
    
    /**
     * Renders the legend within the screen slice given by the left and right parameters.
     */
    protected void renderLegends(IPainter painter, float left, float right, List<ILegend> legends, ICanvas canvas) {
        float slice = (right - left) / legends.size();
        int k = 0;
        for (ILegend legend : legends) {
            legend.setViewportMode(ViewportMode.STRETCH_TO_FILL);
            legend.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(), left + slice * (k++), left + slice * k);
            legend.render(painter);
        }
    }
    
    /*public void showLayout(AWTView view) {
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
    }*/
    
    protected List<ILegend> getLegends(Chart chart){
    	if(chart!=null && chart.getScene()!=null && chart.getScene().getGraph()!=null && chart.getScene().getGraph().getLegends()!=null)
    		return chart.getScene().getGraph().getLegends();
    	else 
    		return new ArrayList<>();
    }
    
    
    /** Return the scene viewport as it was processed according to the number of legends to display. */
    public ViewportConfiguration getSceneViewport() {
		return sceneViewport;
	}

    /** Return the scene viewport as it was processed to cover the whole canvas. */
	public ViewportConfiguration getBackgroundViewport() {
		return backgroundViewport;
	}



	protected ViewportConfiguration sceneViewport;
    protected ViewportConfiguration backgroundViewport;
}
