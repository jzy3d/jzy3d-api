package org.jzy3d.chart.factories;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;

import com.jogamp.opengl.GLCapabilities;


public class TestNativeChartFactory {
	@Test
	public void hasGoodSettingsToMakeOffscreenRenderingWithColor() {
		NativeChartFactory f = new NativeChartFactory() {
			
			@Override
			public IViewportLayout newViewportLayout() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ICameraMouseController newMouseCameraController(Chart chart) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ICameraKeyController newKeyboardCameraController(Chart chart) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	
		GLCapabilities caps = f.getCapabilities();
		
		Assert.assertTrue(caps.getHardwareAccelerated());
		Assert.assertFalse(caps.isOnscreen());
		Assert.assertEquals(8, caps.getAlphaBits());
		Assert.assertEquals(8, caps.getRedBits());
		Assert.assertEquals(8, caps.getBlueBits());
		Assert.assertEquals(8, caps.getGreenBits());
		
	}

}
