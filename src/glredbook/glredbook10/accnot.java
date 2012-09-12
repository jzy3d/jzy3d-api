package glredbook10;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class accnot//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLUT glut;
 
    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        caps.setAccumBlueBits(16);
        caps.setAccumGreenBits(16);
        caps.setAccumRedBits(16);
        //
        GLJPanel canvas = new GLJPanel(caps);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        return canvas;
    }

    public static void main(String[] args) {
        accnot demo = new accnot();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("accnot");
        frame.setSize(250, 250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();

    }

    /*
     * Initialize lighting and other values.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glut = new GLUT();
        //
        float mat_ambient[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float light_position[] = { 0.0f, 0.0f, 10.0f, 1.0f };
        float lm_ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };

        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 50.0f);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lm_ambient, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        float torus_diffuse[] = { 0.7f, 0.7f, 0.0f, 1.0f };
        float cube_diffuse[] = { 0.0f, 0.7f, 0.7f, 1.0f };
        float sphere_diffuse[] = { 0.7f, 0.0f, 0.7f, 1.0f };
        float octa_diffuse[] = { 0.7f, 0.4f, 0.4f, 1.0f };

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glShadeModel(GL2.GL_FLAT);
        gl.glPushMatrix();
        gl.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);

        gl.glPushMatrix();
        gl.glTranslatef(-0.80f, 0.35f, 0.0f);
        gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, torus_diffuse, 0);
        glut.glutSolidTorus(0.275f, 0.85f, 10, 10);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(-0.75f, -0.50f, 0.0f);
        gl.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
        gl.glRotatef(45.0f, 1.0f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, cube_diffuse, 0);
        glut.glutSolidCube(1.5f);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0.75f, 0.60f, 0.0f);
        gl.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, sphere_diffuse, 0);
        glut.glutSolidSphere(1.0f, 10, 10);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0.70f, -0.90f, 0.25f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, octa_diffuse, 0);
        glut.glutSolidOctahedron();
        gl.glPopMatrix();

        gl.glPopMatrix();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            gl.glOrtho(-2.25, 2.25, -2.25 * h / w, 2.25 * h / w, -10.0, 10.0);
        else
            gl.glOrtho(-2.25 * w / h, 2.25 * w / h, -2.25, 2.25, -10.0, 10.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;

        default:
            break;
        }
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}//
/*
 *  For the software in this directory
 * (c) Copyright 1993, Silicon Graphics, Inc.
 * ALL RIGHTS RESERVED 
 * Permission to use, copy, modify, and distribute this software for 
 * any purpose and without fee is hereby granted, provided that the above
 * copyright notice appear in all copies and that both the copyright notice
 * and this permission notice appear in supporting documentation, and that 
 * the name of Silicon Graphics, Inc. not be used in advertising
 * or publicity pertaining to distribution of the software without specific,
 * written prior permission. 
 *
 * THE MATERIAL EMBODIED ON THIS SOFTWARE IS PROVIDED TO YOU "AS-IS"
 * AND WITHOUT WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR OTHERWISE,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE.  IN NO EVENT SHALL SILICON
 * GRAPHICS, INC.  BE LIABLE TO YOU OR ANYONE ELSE FOR ANY DIRECT,
 * SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER, INCLUDING WITHOUT LIMITATION,
 * LOSS OF PROFIT, LOSS OF USE, SAVINGS OR REVENUE, OR THE CLAIMS OF
 * THIRD PARTIES, WHETHER OR NOT SILICON GRAPHICS, INC.  HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS, HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE
 * POSSESSION, USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * US Government Users Restricted Rights 
 * Use, duplication, or disclosure by the Government is subject to
 * restrictions set forth in FAR 52.227.19(c)(2) or subparagraph
 * (c)(1)(ii) of the Rights in Technical Data and Computer Software
 * clause at DFARS 252.227-7013 and/or in similar or successor
 * clauses in the FAR or the DOD or NASA FAR Supplement.
 * Unpublished-- rights reserved under the copyright laws of the
 * United States.  Contractor/manufacturer is Silicon Graphics,
 * Inc., 2011 N.  Shoreline Blvd., Mountain View, CA 94039-7311.
 *
 * OpenGL(TM) is a trademark of Silicon Graphics, Inc.
 */