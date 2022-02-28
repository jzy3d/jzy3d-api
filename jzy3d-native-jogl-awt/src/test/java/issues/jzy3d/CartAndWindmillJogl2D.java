package issues.jzy3d;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

/**
 * A 2D OpenGL animation that demonstrates the use of glPushMatrix and
 * glPopMatrix to implement hierarchical modeling. This program is almost a port
 * of HierarchicalModeling2D.java, which did more or less the same thing using
 * Java Graphics2D.
 */
public class CartAndWindmillJogl2D extends JPanel implements GLEventListener {

	public static void main(String[] args) {
		JFrame window = new JFrame("Hierarchical Modeling in 2D With Jogl");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.setContentPane(new CartAndWindmillJogl2D());
		window.pack();
		window.setVisible(true);
	}

	private NewtCanvasAWT drawable; // The OpenGL display panel.
	private int frameNumber; // Current frame number, increases by 1 in each frame.

	/**
	 * Constructor creates the GLJPanel that will be used for drawing and adds it to
	 * the main panel. It also starts a timer to draw the animation. And it sets the
	 * preferred size to be 700-by-500.
	 */
	public CartAndWindmillJogl2D() {
		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		// create a NEWT window
		GLWindow glWindow = GLWindow.create(caps);
		glWindow.setUndecorated(true);
		// window.addGLEventListener(this);
		drawable = new Canvas(glWindow);
		drawable.setPreferredSize(new Dimension(700, 500));
		glWindow.addGLEventListener(this);
		this.setLayout(new BorderLayout());
			
		/**
		 * //The following code verifies that layout is not honoring pixel Scale
		setLayout(null);
		this.setPreferredSize(new Dimension(700, 500));
		drawable.setBounds(0, 0, 2*700, 2*500);
		*/
		add(drawable);
		Timer timer = new Timer(30, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				frameNumber++;
				drawable.repaint();
			}
		});
		timer.setInitialDelay(1000);
		timer.start();
	}

	/**
	 * This method is called when the GLJPanel is created. It initializes the GL
	 * context. Here, it sets the clear color to be sky blue and it sets the
	 * xy-limits for drawing so that x ranges from 0 to 7 and y ranges from -1 to 4.
	 */
	public void init(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();
		gl2.glClearColor(0.5f, 0.5f, 1, 1);
		// The next three lines set up the coordinates system.
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(0, 7, -1, 4, -1, 1);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
	}

	/**
	 * This method is called when the GLJPanel needs to be redrawn. It draws the
	 * current frame of the animation.
	 */
	public void display(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT); // Fills the scene with blue.
		gl2.glLoadIdentity();

		/* Draw three green triangles to form a ridge of hills in the background */

		gl2.glColor3f(0, 0.6f, 0.2f);
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex2f(-3, -1);
		gl2.glVertex2f(1.5f, 1.65f);
		gl2.glVertex2f(5, -1);
		gl2.glEnd();
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex2f(-3, -1);
		gl2.glVertex2f(3, 2.1f);
		gl2.glVertex2f(7, -1);
		gl2.glEnd();
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex2f(0, -1);
		gl2.glVertex2f(6, 1.2f);
		gl2.glVertex2f(20, -1);
		gl2.glEnd();

		/* Draw a bluish-gray rectangle to represent the road. */

		gl2.glColor3f(0.4f, 0.4f, 0.5f);
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex2f(0, -0.4f);
		gl2.glVertex2f(7, -0.4f);
		gl2.glVertex2f(7, 0.4f);
		gl2.glVertex2f(0, 0.4f);
		gl2.glEnd();

		/*
		 * Draw a white line to represent the stripe down the middle of the road.
		 */

		gl2.glLineWidth(6); // Set the line width to be 6 pixels.
		gl2.glColor3f(1, 1, 1);
		gl2.glBegin(GL2.GL_LINES);
		gl2.glVertex2f(0, 0);
		gl2.glVertex2f(7, 0);
		gl2.glEnd();
		gl2.glLineWidth(1); // Reset the line width to be 1 pixel.

		/*
		 * Draw the sun. The drawSun method draws the sun centered at (0,0). A 2D
		 * translation is applied to move the center of the sun to (5,3.3). A rotation
		 * makes it rotate
		 */

		gl2.glPushMatrix();
		gl2.glTranslated(5.8, 3, 0);
		gl2.glRotated(-frameNumber * 0.7, 0, 0, 1);
		drawSun(gl2);
		gl2.glPopMatrix();

		/*
		 * Draw three windmills. The drawWindmill method draws the windmill with its
		 * base at (0,0), and the top of the pole at (0,3). Each windmill is first
		 * scaled to change its size and then translated to move its base to a different
		 * paint. In the animation, the vanes of the windmill rotate. That rotation is
		 * done with a transform inside the drawWindmill method.
		 */

		gl2.glPushMatrix();
		gl2.glTranslated(0.75, 1, 0);
		gl2.glScaled(0.6, 0.6, 1);
		drawWindmill(gl2);
		gl2.glPopMatrix();

		gl2.glPushMatrix();
		gl2.glTranslated(2.2, 1.6, 0);
		gl2.glScaled(0.4, 0.4, 1);
		drawWindmill(gl2);
		gl2.glPopMatrix();

		gl2.glPushMatrix();
		gl2.glTranslated(3.7, 0.8, 0);
		gl2.glScaled(0.7, 0.7, 1);
		drawWindmill(gl2);
		gl2.glPopMatrix();

		/*
		 * Draw the cart. The drawCart method draws the cart with the center of its base
		 * at (0,0). The body of the cart is 5 units long and 2 units high. A scale is
		 * first applied to the cart to make its size more reasonable for the picture.
		 * Then a translation is applied to move the cart horizontally. The amount of
		 * the translation depends on the frame number, which makes the cart move from
		 * left to right across the screen as the animation progresses. The cart
		 * animation repeats every 300 frames. At the beginning of the animation, the
		 * cart is off the left edge of the screen.
		 */

		gl2.glPushMatrix();
		gl2.glTranslated(-3 + 13 * (frameNumber % 300) / 300.0, 0, 0);
		gl2.glScaled(0.3, 0.3, 1);
		drawCart(gl2);
		gl2.glPopMatrix();

	}

	/**
	 * Draw a sun with radius 0.5 centered at (0,0). There are also 13 rays which
	 * extend outside from the sun for another 0.25 units.
	 */
	private void drawSun(GL2 gl2) {
		gl2.glColor3f(1, 1, 0);
		for (int i = 0; i < 13; i++) { // Draw 13 rays, with different rotations.
			gl2.glRotatef(360f / 13, 0, 0, 1); // Note that the rotations accumulate!
			gl2.glBegin(GL2.GL_LINES);
			gl2.glVertex2f(0, 0);
			gl2.glVertex2f(0.75f, 0);
			gl2.glEnd();
		}
		drawDisk(gl2, 0.5);
		gl2.glColor3f(0, 0, 0);
	}

	/**
	 * Draw a 32-sided regular polygon as an approximation for a circular disk.
	 * (This is necessary since OpenGL has no commands for drawing ovals, circles,
	 * or curves.) The disk is centered at (0,0) with a radius given by the
	 * parameter.
	 */
	private void drawDisk(GL2 gl2, double radius) {
		gl2.glBegin(GL2.GL_POLYGON);
		for (int d = 0; d < 32; d++) {
			double angle = 2 * Math.PI / 32 * d;
			gl2.glVertex2d(radius * Math.cos(angle), radius * Math.sin(angle));
		}
		gl2.glEnd();
	}

	/**
	 * Draw a windmill, consisting of a pole and three vanes. The pole extends from
	 * the point (0,0) to (0,3). The vanes radiate out from (0,3). A rotation that
	 * depends on the frame number is applied to the whole set of vanes, which
	 * causes the windmill to rotate as the animation proceeds. Note that this
	 * method changes the current transform in the GL context gl! The caller of this
	 * subroutine should take care to save and restore the original transform, if
	 * necessary.
	 */
	private void drawWindmill(GL2 gl2) {
		gl2.glColor3f(0.8f, 0.8f, 0.9f);
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex2f(-0.05f, 0);
		gl2.glVertex2f(0.05f, 0);
		gl2.glVertex2f(0.05f, 3);
		gl2.glVertex2f(-0.05f, 3);
		gl2.glEnd();
		gl2.glTranslatef(0, 3, 0);
		gl2.glRotated(frameNumber * (180.0 / 46), 0, 0, 1);
		gl2.glColor3f(0.4f, 0.4f, 0.8f);
		for (int i = 0; i < 3; i++) {
			gl2.glRotated(120, 0, 0, 1); // Note: These rotations accumulate.
			gl2.glBegin(GL2.GL_POLYGON);
			gl2.glVertex2f(0, 0);
			gl2.glVertex2f(0.5f, 0.1f);
			gl2.glVertex2f(1.5f, 0);
			gl2.glVertex2f(0.5f, -0.1f);
			gl2.glEnd();
		}
	}

	/**
	 * Draw a cart consisting of a rectangular body and two wheels. The wheels are
	 * drawn by the drawWheel() method; a different translation is applied to each
	 * wheel to move them into position under the body. The body of the cart is a
	 * red rectangle with corner at (0,-2.5), width 5, and height 2. The center of
	 * the bottom of the rectangle is at (0,0).
	 */
	private void drawCart(GL2 gl2) {
		gl2.glPushMatrix();
		gl2.glTranslatef(-1.5f, -0.1f, 0);
		gl2.glScalef(0.8f, 0.8f, 1);
		drawWheel(gl2);
		gl2.glPopMatrix();
		gl2.glPushMatrix();
		gl2.glTranslatef(1.5f, -0.1f, 0);
		gl2.glScalef(0.8f, 0.8f, 1);
		drawWheel(gl2);
		gl2.glPopMatrix();
		gl2.glColor3f(1, 0, 0);
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex2f(-2.5f, 0);
		gl2.glVertex2f(2.5f, 0);
		gl2.glVertex2f(2.5f, 2);
		gl2.glVertex2f(-2.5f, 2);
		gl2.glEnd();
	}

	/**
	 * Draw a wheel, centered at (0,0) and with radius 1. The wheel has 15 spokes
	 * that rotate in a clockwise direction as the animation proceeds.
	 */
	private void drawWheel(GL2 gl2) {
		gl2.glColor3f(0, 0, 0);
		drawDisk(gl2, 1);
		gl2.glColor3f(0.75f, 0.75f, 0.75f);
		drawDisk(gl2, 0.8);
		gl2.glColor3f(0, 0, 0);
		drawDisk(gl2, 0.2);
		gl2.glRotatef(frameNumber * 20, 0, 0, 1);
		gl2.glBegin(GL2.GL_LINES);
		for (int i = 0; i < 15; i++) {
			gl2.glVertex2f(0, 0);
			gl2.glVertex2d(Math.cos(i * 2 * Math.PI / 15), Math.sin(i * 2 * Math.PI / 15));
		}
		gl2.glEnd();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl2 = drawable.getGL().getGL2();
		gl2.glViewport(0,0,drawable.getSurfaceWidth(),drawable.getSurfaceHeight());
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}

	public void dispose(GLAutoDrawable arg0) {
	}
	
	public class Canvas extends NewtCanvasAWT {
		public Canvas(GLWindow glWindow) {
			super(glWindow);
		}
		@Override
		public Dimension getPreferredSize() {
			Dimension d=super.getPreferredSize();
			return new Dimension(d.width,d.height);
		}
		@Override
		public int getWidth() {
			double scale = getPixelScaleX();
			
			return (int)(super.getWidth() * scale);
		}
		
		@Override
		public int getHeight() {
			double scale = getPixelScaleY();

			return (int)(super.getHeight() * scale);
		}
		
		protected double getPixelScaleX() {
			Graphics2D g2d = (Graphics2D)getGraphics();
			AffineTransform globalTransform = g2d.getTransform();
			return globalTransform.getScaleX();
		}
		
		protected double getPixelScaleY() {
			Graphics2D g2d = (Graphics2D)getGraphics();
			AffineTransform globalTransform = g2d.getTransform();
			return globalTransform.getScaleY();
		}
		
	}
}