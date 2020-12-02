package org.jzy3d.plot3d.rendering.view.layout;

import java.awt.image.BufferedImage;
import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportBuilder;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

public class EmulGLViewAndColorbarsLayout extends ViewAndColorbarsLayout {

	public EmulGLViewAndColorbarsLayout() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(Painter painter, Chart chart) {
		View view = chart.getView();
		view.renderBackground(backgroundViewport);

		// Here we force the scene to be rendered on the entire screen to avoid a GRAY
		// (=CLEAR COLOR?) BAND
		// that can't be overriden by legend image
		sceneViewport = ViewportBuilder.column(chart.getCanvas(), 0, 1);// screenSeparator);

		view.renderScene(sceneViewport);

		renderLegends(painter, chart);

		// fix overlay on top of chart
		view.renderOverlay(view.getCamera().getLastViewPort());
	}

	@Override
	protected void renderLegends(Painter painter, float left, float right, List<ILegend> legends, ICanvas canvas) {
		EmulGLPainter emulGL = (EmulGLPainter) painter;

		// ---------------------------------------
		/// HAAACKKKKYYYYY : SHIFT THE VIEWPORT

		float shift = (right - left) * canvas.getRendererWidth() / 2;
		emulGL.getGL().setShiftHoritontally((int) -shift);

		float slice = (right - left) / legends.size();
		int k = 0;
		for (ILegend legend : legends) {
			legend.setViewportMode(ViewportMode.STRETCH_TO_FILL);
			legend.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(), left + slice * (k++),
					left + slice * k);

			// legend.render(painter); // BYPASS IMAGE RENDERING THAT DOES NOT WORK WELL IN
			// jGL
			// legend.get

			if (legend instanceof AWTColorbarLegend) {
				AWTColorbarLegend leg = (AWTColorbarLegend) legend;

				int x = leg.getScreenLeft();
				// System.out.println(((AWTColorbarLegend) legend).getScreenLeft());
				emulGL.getGL().appendImageToDraw((BufferedImage) leg.getImage(), x, 0);

			}
		}
	}

}
