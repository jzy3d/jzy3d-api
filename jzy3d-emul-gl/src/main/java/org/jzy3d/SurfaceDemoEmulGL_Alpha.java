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
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * TODO : stop my Animator properly
 * TODO : single animator interface for all
 * 
 * TODO : why need to chart.getView().setBoundMode(ViewBoundMode.AUTO_FIT);
 * TODO : why need to q.setAlphaActivated(false); to avoid DARK ALPHA (problem not existing in POC)
 */
public class SurfaceDemoEmulGL_Alpha {
	
	static final float ALPHA_FACTOR = .50f;

	public static void main(String[] args) {
		//LoggerUtils.minimal();
		
		// ---------------------
		// JZY3D CONTENT

		Shape surface = surface();
		surface.setPolygonWireframeDepthTrick(true); // IMPORTANT FOR JGL TO RENDER COPLANAR POLYGON & EDGE WHEN ALPHA DISABLED

		EmulGLChartFactory factory = new EmulGLChartFactory();

		Quality q = Quality.Advanced; // assez propre avec l'ancienne méthode de setQuality 
		
		// Le mode Fastest      blend la couleur mais ne fait pas le calcul de la transparence + WEIRD WIREFRAME
		// Le mode Intermediate blend la couleur mais ne fait pas le calcul de la transparence + WEIRD WIREFRAME -> Offset fill bug with glClearColor
		// Le mode Advanced     active le calcul de la transparence mais BLACK BACKGROUND bug (surface should be white)
		
		
		q.setAlphaActivated(false); 
		// ALPHA BUG
		// Also, q.setAlphaActivated(false) is what involve a WEIRD WIREFRAME effect
		
		//q.setSmoothEdge(true);
		//q.setSmoothPolygon(true);
		//q.setSmoothColor(false);
		
		//q.setSmoothColor(false);
		Chart chart = factory.newChart(q);
		chart.add(surface);
		chart.getView().setAxisDisplayed(false);
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

		try {
			chart.screenshot(new File("target/" + SurfaceDemoEmulGL_Alpha.class.getSimpleName() + ".png"));
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
		// CUSTOMIZE SURFACE BUILDER FOR JGL

		SurfaceBuilder builder = new SurfaceBuilder();
		
		Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
		
		//surface.setPolygonOffsetFillEnable(false);
		
		ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
				surface.getBounds().getZmax(), new Color(1, 1, 1, ALPHA_FACTOR));//0.65f));
		surface.setColorMapper(colorMapper);
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(true);
		surface.setWireframeColor(Color.BLACK);
		return surface;
	}

}
