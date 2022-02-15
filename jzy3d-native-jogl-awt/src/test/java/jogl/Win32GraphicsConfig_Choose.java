package jogl;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import jogamp.nativewindow.jawt.windows.Win32SunJDKReflection;
//import sun.awt.Win32GraphicsConfig;

/**
 * Analyzing 
 * 
 * https://forum.jogamp.org/Unable-to-determine-Graphics-Configuration-Am-I-setting-up-something-wrong-td4041606.html
 * 
 * @author Martin
 */
public class Win32GraphicsConfig_Choose {

	public static void main(String[] args) {

		getPixelFormatByReflection();

		//getPixelFormat();

	}

	// Unsastified link if it is called first. OK if reflective calls is made before
	//
	// Will only work on Windows!
	/*private static void getPixelFormat() {
		System.out.println("=== getPixelFormat ===");
		sun.awt.Win32GraphicsDevice d = new sun.awt.Win32GraphicsDevice(0);

		// reproduce what is found in Win32SunJDKReflection.graphicsConfigurationGet
		int pixelFormatID = 0;
		
		Win32GraphicsConfig c = sun.awt.Win32GraphicsConfig.getConfig(d, pixelFormatID);
		
		System.out.println(c);
		
		// reproduce what is found in Win32SunJDKReflection.graphicsConfigurationGetPixelFormatID
		
		int pfdId = c.getVisual();
	}*/
	
	private static void getPixelFormatByReflection() {
		System.out.println("=== getPixelFormatByReflection ===");

		final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		int pixelFormatID = 0;

		GraphicsConfiguration gc = Win32SunJDKReflection.graphicsConfigurationGet(device, pixelFormatID);
		
		System.out.println(gc);
	}

}
