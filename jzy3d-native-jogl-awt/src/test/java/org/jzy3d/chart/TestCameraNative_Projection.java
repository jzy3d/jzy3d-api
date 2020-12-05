package org.jzy3d.chart;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d.Corners;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * Possible improvement : mock canvas (avoid displaying for faster test,
 * independent from OS)
 * 
 * @author martin
 */
public class TestCameraNative_Projection {
	static int APP_BAR_HEIGHT = 22; // pixel number of Application bar on top
	static Rectangle FRAME_SIZE = new Rectangle(800, 600);


	@Test
	public void whenOptimalViewpoint_thenCameraProjectAxisCornersToTheYminAndYmaxCoordinates() throws InterruptedException, IOException {
		// GIVEN
		AWTChartFactory factory = new AWTChartFactory();

		Quality q = Quality.Advanced;

		// ATTENTION : viewport of a retina display has double number of pixel
		// Also, the Y value is 600, whereas the height is 578
		q.setPreserveViewportSize(true);

		Chart chart = factory.newChart(q);
		chart.add(surface());
		chart.open(this.getClass().getSimpleName(), FRAME_SIZE);
		chart.addMouseCameraController();
		
		
		// -----------------------------------------------------------------------
		// When Viewpoint is supposed to project AxisBox so that corner touch canvas border
		chart.getView().setViewPoint(View.VIEWPOINT_AXIS_CORNER_TOUCH_BORDER, true);
		
		
		Thread.sleep(500);
		
		// -----------------------------------------------------------------------
		// Then at least one corner of AxisBox touch canvas top and bottom border
		
		assertAxisCornersTouchCanvasTopAndBottomBorder(chart, FRAME_SIZE);
		
		//chart.screenshot(new File("target/" + this.getClass().getSimpleName() + ".png"));
	}

	private void assertAxisCornersTouchCanvasTopAndBottomBorder(Chart chart, Rectangle FRAME_SIZE) {
		NativeDesktopPainter p = (NativeDesktopPainter)chart.getPainter();
		p.getCurrentContext(chart.getCanvas()).makeCurrent();
		
		
		int MAX_PIXEL_NUMBER_TO_FRAME_BORDER = 3;

		boolean atLeastOneCornerNearTop = false;
		boolean atLeastOneCornerNearBottom = false;
		

		Corners corners = ((AxisBox)chart.getView().getAxis()).getCorners();
		Camera camera = chart.getView().getCamera();

		for(Coord3d corner: corners.getAll()) {
			//System.out.println("3d : " + corner);
			Coord3d corner2d = camera.modelToScreen(chart.getPainter(), corner);

			if(corner2d.y < MAX_PIXEL_NUMBER_TO_FRAME_BORDER) {
				atLeastOneCornerNearBottom = true;
			}
			
			if(corner2d.y > (FRAME_SIZE.height - APP_BAR_HEIGHT) - MAX_PIXEL_NUMBER_TO_FRAME_BORDER) {
				atLeastOneCornerNearTop = true;
			}
			
			
			System.out.println(" 2d : " + corner2d);
		}
		
		System.out.println(FRAME_SIZE.height - APP_BAR_HEIGHT);
		
		Assert.assertTrue("At least one corner is near to canvas top border (tolerance " + MAX_PIXEL_NUMBER_TO_FRAME_BORDER + " pixels)", atLeastOneCornerNearTop);
		Assert.assertTrue("At least one corner is near to canvas bottom border (tolerance " + MAX_PIXEL_NUMBER_TO_FRAME_BORDER + " pixels)", atLeastOneCornerNearBottom);

		
		
		p.getCurrentContext(chart.getCanvas()).release();
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
