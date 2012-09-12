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

/**
 * The program draws antialiased points, in RGBA mode.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class antipoint//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }

    public static void main(String[] args) {

        antipoint demo = new antipoint();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("antipoint");
        frame.setSize(100, 100);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    /*
     * Initialize point anti-aliasing for RGBA mode, including alpha blending,
     * hint, and point size. These points are 3.0 pixels big.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glEnable(GL2.GL_POINT_SMOOTH);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_DONT_CARE);
        gl.glPointSize(3.0f);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /*
     * display() draws several points.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        int i;

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glBegin(GL.GL_POINTS);
        for (i = 1; i < 10; i++) {
            gl.glVertex2f((float) i * 10.0f, (float) i * 10.0f);
        }
        gl.glEnd();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w < h)
            gl.glOrtho(0.0, 100.0, 0.0, 100.0 * (float) h / (float) w, -1.0,
                    1.0);
        else
            gl.glOrtho(0.0, 100.0 * (float) w / (float) h, 0.0, 100.0, -1.0,
                    1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
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

}
/*
 * For the software in this directory (c) Copyright 1993, Silicon Graphics, Inc.
 * ALL RIGHTS RESERVED Permission to use, copy, modify, and distribute this
 * software for any purpose and without fee is hereby granted, provided that the
 * above copyright notice appear in all copies and that both the copyright
 * notice and this permission notice appear in supporting documentation, and
 * that the name of Silicon Graphics, Inc. not be used in advertising or
 * publicity pertaining to distribution of the software without specific,
 * written prior permission.
 * 
 * THE MATERIAL EMBODIED ON THIS SOFTWARE IS PROVIDED TO YOU "AS-IS" AND WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT
 * LIMITATION, ANY WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR
 * PURPOSE. IN NO EVENT SHALL SILICON GRAPHICS, INC. BE LIABLE TO YOU OR ANYONE
 * ELSE FOR ANY DIRECT, SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES
 * OF ANY KIND, OR ANY DAMAGES WHATSOEVER, INCLUDING WITHOUT LIMITATION, LOSS OF
 * PROFIT, LOSS OF USE, SAVINGS OR REVENUE, OR THE CLAIMS OF THIRD PARTIES,
 * WHETHER OR NOT SILICON GRAPHICS, INC. HAS BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH LOSS, HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, ARISING OUT OF OR
 * IN CONNECTION WITH THE POSSESSION, USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * US Government Users Restricted Rights Use, duplication, or disclosure by the
 * Government is subject to restrictions set forth in FAR 52.227.19(c)(2) or
 * subparagraph (c)(1)(ii) of the Rights in Technical Data and Computer Software
 * clause at DFARS 252.227-7013 and/or in similar or successor clauses in the
 * FAR or the DOD or NASA FAR Supplement. Unpublished-- rights reserved under
 * the copyright laws of the United States. Contractor/manufacturer is Silicon
 * Graphics, Inc., 2011 N. Shoreline Blvd., Mountain View, CA 94039-7311.
 * 
 * OpenGL(TM) is a trademark of Silicon Graphics, Inc.
 */