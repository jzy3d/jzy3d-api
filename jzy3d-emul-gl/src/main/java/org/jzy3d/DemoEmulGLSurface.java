package org.jzy3d;

import java.io.File;
import java.io.IOException;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

/**
 * TODO : stop my Animator properly
 * TODO : single animator interface for all
 * 
 * TODO : why need to chart.getView().setBoundMode(ViewBoundMode.AUTO_FIT);
 * TODO : why need to q.setAlphaActivated(false); to avoid DARK ALPHA (problem not existing in POC)
 */
public class DemoEmulGLSurface {
	
	static final float ALPHA_FACTOR = 0.55f;//.61f;

	public static void main(String[] args) {
		//LoggerUtils.minimal();
		
		// ---------------------
		// JZY3D CONTENT

		Shape surface = surface();
		//surface.setWireframeDisplayed(false);

		EmulGLChartFactory factory = new EmulGLChartFactory();

		Quality q = Quality.Advanced; // assez propre avec l'ancienne méthode de setQuality 
		//q.setAlphaActivated(false); /// ALPHA BUG
		
		//q.setSmoothEdge(true);
		//q.setSmoothPolygon(true);
		//q.setSmoothColor(false);
		
		//q.setSmoothColor(false);
		Chart chart = factory.newChart(q);
		chart.getView().setBoundMode(ViewBoundMode.AUTO_FIT); // INVESTIGUER POURQUOI AUTO_FIT!!!
		

		chart.add(surface);
		
		((EmulGLCanvas)chart.getCanvas()).setProfileDisplayMethod(true);
		
		// --------------------------------
        		
        //chart.getView().getCamera().setViewportMode(ViewportMode.SQUARE);

		chart.open();
		
		
		
		// --------------------------------
		CameraThreadController rotation = new CameraThreadController(chart);
		rotation.setStep(0.025f);
		rotation.setUpdateViewDefault(true);
		
		AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();
		mouse.addSlaveThreadController(rotation);
		
		//chart.getView().setBackgroundColor(Color.BLUE);
		//chart.getView().setAxisDisplayed(true);
		
		boolean fixWithAnimator = true;
		if(fixWithAnimator) {
			rotation.setUpdateViewDefault(true);
			mouse.setUpdateViewDefault(false); // keep to false otherwise double rendering
			((EmulGLCanvas)chart.getCanvas()).getAnimation().start();	
		}
		else {
			
		}
		// autre solution  :quand drag, n'envoyer d'event repaint que si un certain temps s'est écoulé
		// quand paint, n'envoyer d'event repain que si un certain temps s'est écoule
		
		

		try {
			chart.screenshot(new File("target/" + DemoEmulGLSurface.class.getSimpleName() + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private static Shape surface() {

		// ---------------------------
		// DEFINE SURFACE MATHS
		Mapper mapper = new Mapper() {
			@Override
			public double f(double x, double y) {
				return x * Math.sin(x * y);
			}
		};
		Range range = new Range(-3, 3);
		int steps = 50;

		// ---------------------------
		// MAKE SURFACE

		SurfaceBuilder builder = new SurfaceBuilder();

		Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
		
		surface.setPolygonOffsetFillEnable(false); // VERY IMPORTANT FOR JGL TO WORK !!
		
		ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
				surface.getBounds().getZmax(), new Color(1, 1, 1, ALPHA_FACTOR));//0.65f));
		surface.setColorMapper(colorMapper);
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(true);
		surface.setWireframeColor(Color.BLACK);
		return surface;
	}

}
