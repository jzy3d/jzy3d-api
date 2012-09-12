package glredbook10;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;


/**
 * This program draws several overlapping filled polygons to demonstrate the
 * effect order has on alpha blending results. Use the 't' key to toggle the
 * order of drawing polygons.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class alpha// 
        extends GLSkeleton<GLCanvas>
        implements GLEventListener, KeyListener {

    @Override
    protected GLCanvas createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLCanvas canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        return canvas;
    }

    /*
     * Main Loop Open window with initial window size, title bar, RGBA display
     * mode, and handle input events.
     */
    public static void main(String[] args) {
        // set argument 'NotFirstUIActionOnProcess' in the JNLP's application-desc tag for example
        // <application-desc main-class="demos.j2d.TextCube"/>
        //   <argument>NotFirstUIActionOnProcess</argument>
        // </application-desc>
        // boolean firstUIActionOnProcess = 0==args.length || !args[0].equals("NotFirstUIActionOnProcess") ;
        // GLProfile.initSingleton(firstUIActionOnProcess);
        //GLProfile.initSingleton(); // just lazy to touch all html/jnlp's

        alpha demo = new alpha();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("alpha");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    /*
     * Initialize alpha blending function.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glClearColor(0, 0, 0, 0);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor4f(1.0f, 1.0f, 0.0f, 0.75f);
        gl.glRectf(0.0f, 0.0f, 0.5f, 1.0f);

        gl.glColor4f(0.0f, 1.0f, 1.0f, 0.75f);
        gl.glRectf(0.0f, 0.0f, 1.f, 0.5f);
        /* draw colored polygons in reverse order in upper right */
        gl.glColor4f(0.f, 1.0f, 1.0f, 0.75f);
        gl.glRectf(0.5f, 0.5f, 1.0f, 1.0f);

        gl.glColor4f(1.0f, 1.0f, 0.0f, 0.75f);
        gl.glRectf(0.5f, 0.5f, 1.0f, 1.0f);

        gl.glEnd();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            glu.gluOrtho2D(0.0, 1.0, 0.0, 1.0 * h / w);
        else
            glu.gluOrtho2D(0.0, 1.0 * w / h, 0.0, 1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
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
