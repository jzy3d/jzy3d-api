package org.jzy3d.chart.factories;

import org.junit.Assert;
import org.junit.Test;

import com.jogamp.opengl.GLCapabilities;


public class TestNativeChartFactory {
	@Test
	public void hasGoodSettingsToMakeOffscreenRenderingWithColor() {
		NativeChartFactory f = new NativeChartFactory() {};
	
		GLCapabilities caps = f.getCapabilities();
		
		Assert.assertTrue(caps.getHardwareAccelerated());
		Assert.assertFalse(caps.isOnscreen());
		Assert.assertEquals(8, caps.getAlphaBits());
		Assert.assertEquals(8, caps.getRedBits());
		Assert.assertEquals(8, caps.getBlueBits());
		Assert.assertEquals(8, caps.getGreenBits());
		
	}

}
