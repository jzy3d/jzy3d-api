package org.jzy3d.swt;
/*
 * bezcurve.java This program uses evaluators to draw a Bezier curve.
 */

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;

import jgl.GL;
import jgl.wt.swt.JGLCanvas;

public class bezcurve extends JGLCanvas {

	public bezcurve(Composite parent, int style, GLData data) {

		super(parent, style, data);
		myinit();
	}

	private static final float ctrlpoints[][] = {{-4.0f, -4.0f, 0.0f}, {-2.0f, 4.0f, 0.0f}, {2.0f, -4.0f, 0.0f}, {4.0f, 4.0f, 0.0f}};

	private void myinit() {

		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glShadeModel(GL.GL_FLAT);
		gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, ctrlpoints);
		gl.glEnable(GL.GL_MAP1_VERTEX_3);
	}

	@Override
	protected void doPaint(GC gc) {

		int i;
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for(i = 0; i <= 30; i++)
			gl.glEvalCoord1f(i / 30.0f);
		gl.glEnd();
		/* The following code displays the control points as dots. */
		gl.glPointSize(5.0f);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glBegin(GL.GL_POINTS);
		for(i = 0; i < 4; i++)
			gl.glVertex3fv(ctrlpoints[i]);
		gl.glEnd();
		gl.glFlush();
		super.doPaint(gc);
	}

	@Override
	protected void doResize(int w, int h) {

		gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		if(w <= h) {
			gl.glOrtho(-5.0f, 5.0f, -5.0f * h / w, 5.0f * h / w, -5.0f, 5.0f);
		} else {
			gl.glOrtho(-5.0f * w / h, 5.0f * w / h, -5.0f, 5.0f, -5.0f, 5.0f);
		}
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
	}


}
