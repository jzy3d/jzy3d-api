package org.jzy3d.chart;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d.Corners;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

/**
 * TODO : MUST ALSO TEST WITH COLORBARS TODO : MUST ALSO VERIFY APPROPRIATE
 * LOOK_TO AND ORTHO CALL OF THE CAMERA!!
 * 
 * Possible improvement : mock canvas (avoid displaying for faster test,
 * independent from OS)
 * 
 * @author martin
 */
public class TestCamera {

	@Test
	public void whenResize_thenCameraViewportUpdatesAccordingToMode() throws InterruptedException {
		// GIVEN
		AWTChartFactory factory = new AWTChartFactory();

		Quality q = Quality.Advanced;

// ATTENTION : viewport of a retina display has double number of pixel		
// Also, the Y value is 600, whereas the height is 578
		q.setPreserveViewportSize(true);

		Chart chart = factory.newChart(q);
		chart.add(surface());
		Camera camera = chart.getView().getCamera();

		// ----------------------------------------
		// When opening window to a chosen size

		Rectangle FRAME_SIZE = new Rectangle(800, 600);
		int APP_BAR_HEIGHT = 22; // pixel number of Application bar on top

		FrameAWT frame = (FrameAWT) chart.open("TestCamera", FRAME_SIZE);

		Thread.sleep(500); // let time for opening window otheriwse follwing assertions may fail

		// Then scene viewport size is set to occupy the full frame
		ViewAndColorbarsLayout layout = (ViewAndColorbarsLayout) ((ChartView) chart.getView()).getLayout();
		ViewportConfiguration sceneViewport = layout.getSceneViewport();

		Assert.assertEquals(FRAME_SIZE.width, sceneViewport.getWidth());
		Assert.assertEquals(FRAME_SIZE.height - APP_BAR_HEIGHT, sceneViewport.getHeight());
		Assert.assertEquals(0, sceneViewport.getX());
		Assert.assertEquals(FRAME_SIZE.height - APP_BAR_HEIGHT, sceneViewport.getY());

		// Then camera viewport size is set to occupy the full frame
		Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, camera.getLastViewPort().getMode());
		Assert.assertEquals(FRAME_SIZE.width, camera.getLastViewPort().getWidth());
		Assert.assertEquals(FRAME_SIZE.height - APP_BAR_HEIGHT, camera.getLastViewPort().getHeight());

		// ----------------------------------------
		// When change canvas size and update view

		Rectangle CANVAS_SIZE_V = new Rectangle(100, 300);
		frame.setBounds(0, 0, CANVAS_SIZE_V.width, CANVAS_SIZE_V.height);
		frame.repaint();

		Thread.sleep(300); // let time for resize and redraw otherwise following assertions may fail

		// Then viewport on the complete canvas
		Assert.assertEquals(CANVAS_SIZE_V.height - APP_BAR_HEIGHT, camera.getLastViewPort().getHeight());
		Assert.assertEquals(CANVAS_SIZE_V.width, camera.getLastViewPort().getWidth());

		// ----------------------------------------
		// When set STRETCH_TO_FILL

		camera.setViewportMode(ViewportMode.STRETCH_TO_FILL);
		chart.getView().shoot();

		// Then viewport on the complete canvas
		Assert.assertEquals(ViewportMode.STRETCH_TO_FILL, camera.getLastViewPort().getMode());
		Assert.assertEquals(CANVAS_SIZE_V.height - APP_BAR_HEIGHT, camera.getLastViewPort().getHeight());
		Assert.assertEquals(CANVAS_SIZE_V.width, camera.getLastViewPort().getWidth());

		// ----------------------------------------
		// When set SQUARE in vertical rectangle frame

		camera.setViewportMode(ViewportMode.SQUARE);
		chart.getView().shoot();

		// Then viewport is SQUARE
		Assert.assertEquals(ViewportMode.SQUARE, camera.getLastViewPort().getMode());

		int sideLength = 100;
		Assert.assertEquals(sideLength, camera.getLastViewPort().getHeight());
		Assert.assertEquals(sideLength, camera.getLastViewPort().getWidth());
		Assert.assertEquals(0, camera.getLastViewPort().getX());
//Assert.assertEquals(0, camera.getLastViewPort().getY());

		// ----------------------------------------
		// When set SQUARE in a horizontal rectangle frame

		camera.setViewportMode(ViewportMode.SQUARE);

		Rectangle CANVAS_SIZE_H = new Rectangle(400, 100);
		frame.setBounds(0, 0, CANVAS_SIZE_H.width, CANVAS_SIZE_H.height);
		frame.repaint();

		Thread.sleep(300); // let time for resize and redraw otherwise following assertions may fail

		sideLength = 100 - APP_BAR_HEIGHT;
		Assert.assertEquals(sideLength, camera.getLastViewPort().getHeight());
		Assert.assertEquals(sideLength, camera.getLastViewPort().getWidth());
		// check viewport is shifted to the right so that square viewport is centered
		Assert.assertEquals(CANVAS_SIZE_H.width / 2 - sideLength / 2, camera.getLastViewPort().getX());

	}

	@Test
	public void whenOptimalViewpoint_thenCameraProjectAxisCornersToTheYminAndYmaxCoordinates() throws InterruptedException {
		// GIVEN
		AWTChartFactory factory = new AWTChartFactory();

		Quality q = Quality.Advanced;

		// ATTENTION : viewport of a retina display has double number of pixel
		// Also, the Y value is 600, whereas the height is 578
		q.setPreserveViewportSize(true);

		Chart chart = factory.newChart(q);
		chart.add(surface());
		
		Camera camera = chart.getView().getCamera();

		// ----------------------------------------
		// When opening window to a chosen size

		Rectangle FRAME_SIZE = new Rectangle(800, 600);
		int APP_BAR_HEIGHT = 22; // pixel number of Application bar on top

		FrameAWT frame = (FrameAWT) chart.open("TestCamera", FRAME_SIZE);

		chart.addMouseCameraController();
		
		
		// azimuth facing : 1.5*Math.PI / 3
		
		chart.getView().setViewPoint(View.VIEWPOINT_AXIS_CORNER_TOUCH_BORDER, true);
		Corners corners = ((AxisBox)chart.getView().getAxis()).getCorners();
		
		
		Thread.sleep(1000);
		
		for(Coord3d corner: corners.getAll()) {
			//System.out.println("3d : " + corner);
			Coord3d corner2d = camera.modelToScreen(chart.getPainter(), corner);

			System.out.println(" 2d : " + corner2d);
		}
		
		
		Thread.sleep(500000); // let time for opening window otheriwse follwing assertions may fail

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
		int steps = 60;

		// ---------------------------
		// CUSTOMIZE SURFACE BUILDER FOR JGL
		SurfaceBuilder builder = new SurfaceBuilder();

		// ---------------------------
		// MAKE SURFACE
		Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
		surface.setPolygonOffsetFillEnable(false);

		ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
				surface.getBounds().getZmax(), new Color(1, 1, 1, 0.650f));
		surface.setColorMapper(colorMapper);
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(true);
		surface.setWireframeColor(Color.BLACK);
		return surface;
	}
}
