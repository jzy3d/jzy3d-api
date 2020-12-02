package org.jzy3d.emulgl;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

public class TestChart {
	@Test
	public void givenEmulatedChart_whenAddSurface_thenViewIsInAutotFitMode() {
		
		// Given
		
		ChartFactory factory = new EmulGLChartFactory();
		factory.setOffscreen(500, 500);
		
		Chart chart = factory.newChart();
		
		// INVESTIGUER POURQUOI AUTO_FIT!!!
		// L'appel à view.init déclenche un setting de bound manuel
		// view.initBounds(...) avec 0,0,0
		chart.getView().setBoundMode(ViewBoundMode.AUTO_FIT); 

		// --------------------------
		// When
		
		chart.add(surface());
		
		// --------------------------
		// Then
		
		Assert.assertEquals(ViewBoundMode.AUTO_FIT, chart.getView().getBoundsMode());
		Assert.assertFalse("Near clipping plane != 0", 0==chart.getView().getCamera().getNear());
		Assert.assertFalse("Far clipping plane != 0", 0==chart.getView().getCamera().getFar());
	}
	
	@Test
	public void givenEmulatedChart_whenAddMouseController_thenViewIsController() {
		
		// Given
		
		ChartFactory factory = new EmulGLChartFactory();
		factory.setOffscreen(500, 500);
		
		Chart chart = factory.newChart();
		
		// INVESTIGUER POURQUOI AUTO_FIT!!!
		// L'appel à view.init déclenche un setting de bound manuel
		// view.initBounds(...) avec 0,0,0
		chart.getView().setBoundMode(ViewBoundMode.AUTO_FIT);
		
		chart.add(surface());


		// --------------------------
		// When
		
		AWTCameraMouseController mouse = (AWTCameraMouseController)chart.addMouseCameraController();
		
		mouse.setUpdateViewDefault(true);
		
		// --------------------------
		// Then
		
		Assert.assertNotNull("Mouse is defined", mouse);
		Assert.assertEquals("Mouse has its owner chart as target", chart, mouse.getChart());
		Assert.assertTrue("Mouse will refresh view upon action", mouse.isUpdateViewDefault());
		
		// when mouse drag, viewpoint change
		// when viewpoint change, clear picture is invoked
		
	}
	
	protected Shape surface() {
		Mapper mapper = new Mapper() {
			@Override
			public double f(double x, double y) {
				return x * Math.sin(x * y);
			}
		};
		Range range = new Range(-3, 3);
		int steps = 50;

		Shape surface = new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
		surface.setPolygonOffsetFillEnable(false);
		
		ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
				surface.getBounds().getZmax(), new Color(1, 1, 1, .5f));
		
		surface.setColorMapper(colorMapper);
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(true);
		surface.setWireframeColor(Color.BLACK);
		return surface;
	}
}
