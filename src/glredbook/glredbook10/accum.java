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
 * Do a sixteen pass
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class accum//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    
    private GLUT glut;
    private static final int ACSIZE = 16;
    private int width, height;
    private float dx[] = new float[ACSIZE];
    private float dy[] = new float[ACSIZE];
    private float jitter3[][] = new float[][] { { 0.5f, 0.5f },
            { 1.35899e-05f, 0.230369f }, { 0.000189185f, 0.766878f }, };
    private float jitter16[][] = new float[][] { { 0.4375f, 0.4375f },
            { 0.1875f, 0.5625f }, { 0.9375f, 1.1875f },
            { 0.4375f, 0.9375f - 1 }, { 0.6875f, 0.5625f },
            { 0.1875f, 0.0625f }, { 0.6875f, 0.3125f }, { 0.1875f, 0.3125f },
            { 0.4375f, 0.1875f }, { 0.9375f - 1, 0.4375f },
            { 0.6875f, 0.8125f }, { 0.4375f, 0.6875f }, { 0.6875f, 0.0625f },
            { 0.9375f, 0.9375f }, { 1.1875f, 0.8125f }, { 0.9375f, 0.6875f }, };

    private float jitter29[][] = new float[][] { { 0.5f, 0.5f },
            { 0.498126f, 0.141363f }, { 0.217276f, 0.651732f },
            { 0.439503f, 0.954859f }, { 0.734171f, 0.836294f },
            { 0.912454f, 0.79952f }, { 0.406153f, 0.671156f },
            { 0.0163892f, 0.631994f }, { 0.298064f, 0.843476f },
            { 0.312025f, 0.0990405f }, { 0.98135f, 0.965697f },
            { 0.841999f, 0.272378f }, { 0.559348f, 0.32727f },
            { 0.809331f, 0.638901f }, { 0.632583f, 0.994471f },
            { 0.00588314f, 0.146344f }, { 0.713365f, 0.437896f },
            { 0.185173f, 0.246584f }, { 0.901735f, 0.474544f },
            { 0.366423f, 0.296698f }, { 0.687032f, 0.188184f },
            { 0.313256f, 0.472999f }, { 0.543195f, 0.800044f },
            { 0.629329f, 0.631599f }, { 0.818263f, 0.0439354f },
            { 0.163978f, 0.00621497f }, { 0.109533f, 0.812811f },
            { 0.131325f, 0.471624f }, { 0.0196755f, 0.331813f }, };

    private float jitter90[][] = new float[][] { { 0.5f, 0.5f },
            { 0.784289f, 0.417355f }, { 0.608691f, 0.678948f },
            { 0.546538f, 0.976002f }, { 0.972245f, 0.270498f },
            { 0.765121f, 0.189392f }, { 0.513193f, 0.743827f },
            { 0.123709f, 0.874866f }, { 0.991334f, 0.745136f },
            { 0.56342f, 0.0925047f }, { 0.662226f, 0.143317f },
            { 0.444563f, 0.928535f }, { 0.248017f, 0.981655f },
            { 0.100115f, 0.771923f }, { 0.593937f, 0.559383f },
            { 0.392095f, 0.225932f }, { 0.428776f, 0.812094f },
            { 0.510615f, 0.633584f }, { 0.836431f, 0.00343328f },
            { 0.494037f, 0.391771f }, { 0.617448f, 0.792324f },
            { 0.688599f, 0.48914f }, { 0.530421f, 0.859206f },
            { 0.0742278f, 0.665344f }, { 0.979388f, 0.626835f },
            { 0.183806f, 0.479216f }, { 0.151222f, 0.0803998f },
            { 0.476489f, 0.157863f }, { 0.792675f, 0.653531f },
            { 0.0990416f, 0.267284f }, { 0.776667f, 0.303894f },
            { 0.312904f, 0.296018f }, { 0.288777f, 0.691008f },
            { 0.460097f, 0.0436075f }, { 0.594323f, 0.440751f },
            { 0.876296f, 0.472043f }, { 0.0442623f, 0.0693901f },
            { 0.355476f, 0.00442787f }, { 0.391763f, 0.361327f },
            { 0.406994f, 0.696053f }, { 0.708393f, 0.724992f },
            { 0.925807f, 0.933103f }, { 0.850618f, 0.11774f },
            { 0.867486f, 0.233677f }, { 0.208805f, 0.285484f },
            { 0.572129f, 0.211505f }, { 0.172931f, 0.180455f },
            { 0.327574f, 0.598031f }, { 0.685187f, 0.372379f },
            { 0.23375f, 0.878555f }, { 0.960657f, 0.409561f },
            { 0.371005f, 0.113866f }, { 0.29471f, 0.496941f },
            { 0.748611f, 0.0735321f }, { 0.878643f, 0.34504f },
            { 0.210987f, 0.778228f }, { 0.692961f, 0.606194f },
            { 0.82152f, 0.8893f }, { 0.0982095f, 0.563104f },
            { 0.214514f, 0.581197f }, { 0.734262f, 0.956545f },
            { 0.881377f, 0.583548f }, { 0.0560485f, 0.174277f },
            { 0.0729515f, 0.458003f }, { 0.719604f, 0.840564f },
            { 0.325388f, 0.7883f }, { 0.26136f, 0.0848927f },
            { 0.393754f, 0.467505f }, { 0.425361f, 0.577672f },
            { 0.648594f, 0.0248658f }, { 0.983843f, 0.521048f },
            { 0.272936f, 0.395127f }, { 0.177695f, 0.675733f },
            { 0.89175f, 0.700901f }, { 0.632301f, 0.908259f },
            { 0.782859f, 0.53611f }, { 0.0141421f, 0.855548f },
            { 0.0437116f, 0.351866f }, { 0.939604f, 0.0450863f },
            { 0.0320883f, 0.962943f }, { 0.341155f, 0.895317f },
            { 0.952087f, 0.158387f }, { 0.908415f, 0.820054f },
            { 0.481435f, 0.281195f }, { 0.675525f, 0.25699f },
            { 0.585273f, 0.324454f }, { 0.156488f, 0.376783f },
            { 0.140434f, 0.977416f }, { 0.808155f, 0.77305f },
            { 0.282973f, 0.188937f }, };

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        // we absolutely need accumulation buffer for this
        caps.setAccumBlueBits(16);
        caps.setAccumGreenBits(16);
        caps.setAccumRedBits(16);
        caps.setAccumAlphaBits(16);
        //
        GLJPanel canvas = new GLJPanel(caps);
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        return canvas;
    }

    public static void main(String[] args) {
        accum demo = new accum();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("accum");
        frame.setSize(512, 512);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glut = new GLUT();
        //
        float ambient[] = { 0.4f, 0.4f, 0.4f, 1.0f };
        float diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float position[] = { 0.0f, 2.0f, 2.0f, 0.0f };
        float mat_ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        float mat_diffuse[] = { 0.7f, 0.7f, 0.7f, 1.0f };
        float mat_specular[] = { 0.9f, 0.9f, 0.9f, 1.0f };
        float mat_shininess[] = { 50.0f };
        float lmodel_ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);

        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, mat_shininess, 0);

        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_AUTO_NORMAL);
        gl.glEnable(GL2.GL_NORMALIZE);

        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_FRONT);

        gl.glClearAccum(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL2.GL_ACCUM_BUFFER_BIT);
        loaddxdy();
        for (int i = 0; i < (ACSIZE); i++) {
            System.out.println("Pass " + i);
            gl.glPushMatrix();
            gl.glTranslatef(dx[i], dy[i], 0.0f);
            gl.glRotatef(45.0f, 1.0f, 1.0f, 1.0f);
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
            glut.glutSolidTeapot(1.0f);
            gl.glPopMatrix();
            gl.glAccum(GL2.GL_ACCUM, 1.0f / (ACSIZE));
        }
        System.out.println("final job");
        gl.glAccum(GL2.GL_RETURN, 1.0f);
        System.out.println("done");
        gl.glFlush();
        drawable.swapBuffers();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        width = w;
        height = h;
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            gl.glOrtho(-3.0, 3.0, -3.0 * (float) h / (float) w, 3.0 * (float) h
                    / (float) w, -15.0, 15.0);
        else
            gl.glOrtho(-3.0 * (float) w / (float) h, 3.0 * (float) w
                    / (float) h, -3.0, 3.0, -15.0, 15.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void loaddxdy() {
        int i;
        for (i = 0; i < ACSIZE; i++) {
            dx[i] = jitter16[i][0] * 10 / width;
            dy[i] = jitter16[i][1] * 10 / height;
        }
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