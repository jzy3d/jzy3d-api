package org.jzy3d.demos.surface;

import java.io.File;
import java.io.IOException;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class SurfaceDemoEmulGL {
	
	static final float ALPHA_FACTOR = 0.55f;//.61f;

	public static void main(String[] args) {
		Shape surface = surface();
		
		EmulGLChartFactory factory = new EmulGLChartFactory();

		Quality q = Quality.Advanced; // assez propre avec l'ancienne méthode de setQuality 
		Chart chart = factory.newChart(q);
		chart.add(surface);
		
		((EmulGLCanvas)chart.getCanvas()).setProfileDisplayMethod(false);
		

		chart.open();
		
		// --------------------------------
		
		CameraThreadController rotation = new CameraThreadController(chart);
		rotation.setStep(0.025f);
		rotation.setUpdateViewDefault(true);
		
		AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();
		mouse.addSlaveThreadController(rotation);

		rotation.setUpdateViewDefault(true);
		mouse.setUpdateViewDefault(false); // keep to false otherwise double rendering
		chart.setAnimated(true);

		try {
			chart.screenshot(new File("target/" + SurfaceDemoEmulGL.class.getSimpleName() + ".png"));
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
		surface.setWireframeWidth(1);
		return surface;
	}

}
