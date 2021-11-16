package org.jzy3d.chart;
import java.awt.BorderLayout;
import java.awt.BufferCapabilities;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

public class JoglContextTestCase {
	private static JFrame mainFrame = null;

	private static void buildSceneAndCheckConfigurations() {
		System.out.println("sun.java2d.noddraw:" + System.getProperty("sun.java2d.noddraw"));

		GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] graphDevsOrig = graphEnv.getScreenDevices();

		GraphicsConfiguration gcDef = graphDevsOrig[0].getDefaultConfiguration();

		// *************************************************************************
		// NOTE: if I comment the following loop, which apparently does NOTHING
		// (except calling .getGraphicsConfigurations()), this test case
		// returns "GL version: 4.6" on my PC, while with the following loop
		// uncommented, it returns "GL version: 1.1" --> this would cause VTK
		// to CRASH!
		// The single call responsible for the "GL 1.1" issue, for some reason,
		// seems to be 'graphDevsOrig[i].getConfigurations();'
		// *************************************************************************
		for (int i = 0; i < graphDevsOrig.length; i++) {
			System.out.println("Screen device # " + i + ": " + graphDevsOrig[i].getIDstring());
			GraphicsConfiguration[] graphConfs = graphDevsOrig[i].getConfigurations();

			for (int j = 0; j < graphConfs.length; j++) {
				GraphicsConfiguration gc = graphConfs[j];

				System.out.println(" Screen device # " + i + ", configuration # " + j + ":" + gc);

				print(gc);
			}
		}

		buildScene(gcDef);
	}

	private static void print(GraphicsConfiguration gc) {
		System.out.println("  ColorModel   : " + gc.getColorModel());

		ImageCapabilities ic = gc.getImageCapabilities();
		System.out.println("  Img Caps     : " + str(ic));
		System.out.println("  Bounds       : " + gc.getBounds());
		System.out.println("  Translucency :  " + gc.isTranslucencyCapable());

		BufferCapabilities bc = gc.getBufferCapabilities();
		System.out.println("  Buffer caps / Back:  " + str(bc.getBackBufferCapabilities()));
		System.out.println("  Buffer caps / Front:  " + str(bc.getFrontBufferCapabilities()));
		System.out.println("  Buffer caps / FullScreen:  " + bc.isFullScreenRequired());
	}

	private static String str(ImageCapabilities ic) {
		return "accelerated: " + ic.isAccelerated()
		+ " true volatile: " + ic.isTrueVolatile();
	}

	private static void buildScene(GraphicsConfiguration graphConf) {
		GLCanvas glCanvas = new GLCanvas(new GLCapabilities(GLProfile.getMaximum(true)));
		glCanvas.addGLEventListener(new GLEventListener() {
			@Override
			public void init(final GLAutoDrawable drawable) {
//        final GL gl = drawable.getGL();
//        System.out.println(JoglVersion.getGLInfo(gl, null));
//        System.out.println("* Context = " + drawable.getContext().toString());
				System.out.println("* Context GL version: " + drawable.getContext().getGLVersion());

				print(glCanvas.getGraphicsConfiguration());
			}

			@Override
			public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width,
					final int height) {
			}

			@Override
			public void display(final GLAutoDrawable drawable) {
			}

			@Override
			public void dispose(final GLAutoDrawable drawable) {
			}
		});

		// UI part
		mainFrame = new JFrame("SimpleVTK", graphConf);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(new BorderLayout());

		mainFrame.setSize(1000, 600);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		mainFrame.getContentPane().add(glCanvas, BorderLayout.CENTER);
		glCanvas.requestFocus();

		System.out.println("NewFrame created in thread [" + Thread.currentThread().getId() + "], isEDT: "
				+ SwingUtilities.isEventDispatchThread());
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildSceneAndCheckConfigurations();
			}
		});
	}
}
