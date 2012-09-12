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
 * Use the accumulation buffer to do full-scene antialiasing on a scene with
 * perspective projection.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class accpersp//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLUT glut;
    //
    private static final int ACSIZE = 8;

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
      
        accpersp demo = new accpersp();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("accpersp");
        frame.setSize(250, 250);
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
        float mat_ambient[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        float mat_specular[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        float light_position[] = new float[] { 0.0f, 0.0f, 10.0f, 1.0f };
        float lm_ambient[] = new float[] { 0.2f, 0.2f, 0.2f, 1.0f };

        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 50.0f);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lm_ambient, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL2.GL_FLAT);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearAccum(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        int viewport[] = new int[4];
        int jitter;

        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

        gl.glClear(GL2.GL_ACCUM_BUFFER_BIT);
        for (jitter = 0; jitter < ACSIZE; jitter++) {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            accPerspective(gl, 50.0, (double) viewport[2]
                    / (double) viewport[3], 1.0, 15.0, j8[jitter].x,
                    j8[jitter].y, 0.0, 0.0, 1.0);
            displayObjects(gl);
            gl.glAccum(GL2.GL_ACCUM, 1.0f / ACSIZE);
        }
        gl.glAccum(GL2.GL_RETURN, 1.0f);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(x, y, w, h);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {

    }

    /*
     * accFrustum() The first 6 arguments are identical to the glFrustum() call.
     * pixdx and pixdy are anti-alias jitter in pixels. Set both equal to 0.0
     * for no anti-alias jitter. eyedx and eyedy are depth-of field jitter in
     * pixels. Set both equal to 0.0 for no depth of field effects. focus is
     * distance from eye to plane in focus. focus must be greater than, but not
     * equal to 0.0. Note that accFrustum() calls glTranslatef(). You will
     * probably want to insure that your ModelView matrix has been initialized
     * to identity before calling accFrustum().
     */
    private void accFrustum(GL2 gl, double left, double right, double bottom,
            double top, double near, double far, double pixdx, double pixdy,
            double eyedx, double eyedy, double focus) {
        double xwsize, ywsize;
        double dx, dy;
        int viewport[] = new int[4];

        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

        xwsize = right - left;
        ywsize = top - bottom;

        dx = -(pixdx * xwsize / (double) viewport[2] + eyedx * near / focus);
        dy = -(pixdy * ywsize / (double) viewport[3] + eyedy * near / focus);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(left + dx, right + dx, bottom + dy, top + dy, near, far);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef((float) -eyedx, (float) -eyedy, 0.0f);
    }

    /*
     * accPerspective() The first 4 arguments are identical to the
     * gluPerspective() call. pixdx and pixdy are anti-alias jitter in pixels.
     * Set both equal to 0.0 for no anti-alias jitter. eyedx and eyedy are
     * depth-of field jitter in pixels. Set both equal to 0.0 for no depth of
     * field effects. focus is distance from eye to plane in focus. focus must
     * be greater than, but not equal to 0.0. Note that accPerspective() calls
     * accFrustum().
     */
    void accPerspective(GL2 gl, double fovy, double aspect, double near,
            double far, double pixdx, double pixdy, double eyedx, double eyedy,
            double focus) {
        double fov2, left, right, bottom, top;

        fov2 = ((fovy * Math.PI) / 180.0) / 2.0;

        top = near / (Math.cos(fov2) / Math.sin(fov2));
        bottom = -top;

        right = top * aspect;
        left = -right;

        accFrustum(gl, left, right, bottom, top, near, far, pixdx, pixdy,
                eyedx, eyedy, focus);
    }

    private void displayObjects(GL2 gl) {
        float torus_diffuse[] = new float[] { 0.7f, 0.7f, 0.0f, 1.0f };
        float cube_diffuse[] = new float[] { 0.0f, 0.7f, 0.7f, 1.0f };
        float sphere_diffuse[] = new float[] { 0.7f, 0.0f, 0.7f, 1.0f };
        float octa_diffuse[] = new float[] { 0.7f, 0.4f, 0.4f, 1.0f };

        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);

        gl.glPushMatrix();
        gl.glTranslatef(-0.80f, 0.35f, 0.0f);
        gl.glRotatef(100.0f, 1.0f, 0.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, torus_diffuse, 0);
        glut.glutSolidTorus(0.275, 0.85, 16, 16);
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
        glut.glutSolidSphere(1.0, 16, 16);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0.70f, -0.90f, 0.25f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, octa_diffuse, 0);
        glut.glutSolidOctahedron();
        gl.glPopMatrix();

        gl.glPopMatrix();
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

    class jitter_point {
        private static final int MAX_SAMPLES = 66;
        float x, y;

        public jitter_point(double x, double y) {
            this.x = (float) x;
            this.y = (float) y;
        }
    }
    /* 2 jitter points */
    jitter_point j2[] = { new jitter_point(0.246490f, 0.249999f),
            new jitter_point(-0.246490f, -0.249999f) };

    /* 3 jitter points */
    jitter_point j3[] = { new jitter_point(-0.373411, -0.250550),//
            new jitter_point(0.256263, 0.368119), //
            new jitter_point(0.117148, -0.117570) };

    /* 4 jitter points */
    jitter_point j4[] = { new jitter_point(-0.208147, 0.353730),
            new jitter_point(0.203849, -0.353780),
            new jitter_point(-0.292626, -0.149945),
            new jitter_point(0.296924, 0.149994) };

    /* 8 jitter points */
    jitter_point j8[] = { new jitter_point(-0.334818, 0.435331),
            new jitter_point(0.286438, -0.393495),
            new jitter_point(0.459462, 0.141540),
            new jitter_point(-0.414498, -0.192829),
            new jitter_point(-0.183790, 0.082102),
            new jitter_point(-0.079263, -0.317383),
            new jitter_point(0.102254, 0.299133),
            new jitter_point(0.164216, -0.054399) };

    /* 15 jitter points */
    jitter_point j15[] = { new jitter_point(0.285561, 0.188437),
            new jitter_point(0.360176, -0.065688),
            new jitter_point(-0.111751, 0.275019),
            new jitter_point(-0.055918, -0.215197),
            new jitter_point(-0.080231, -0.470965),
            new jitter_point(0.138721, 0.409168),
            new jitter_point(0.384120, 0.458500),
            new jitter_point(-0.454968, 0.134088),
            new jitter_point(0.179271, -0.331196),
            new jitter_point(-0.307049, -0.364927),
            new jitter_point(0.105354, -0.010099),
            new jitter_point(-0.154180, 0.021794),
            new jitter_point(-0.370135, -0.116425),
            new jitter_point(0.451636, -0.300013),
            new jitter_point(-0.370610, 0.387504) };

    /* 24 jitter points */
    jitter_point j24[] = { new jitter_point(0.030245, 0.136384),
            new jitter_point(0.018865, -0.348867),
            new jitter_point(-0.350114, -0.472309),
            new jitter_point(0.222181, 0.149524),
            new jitter_point(-0.393670, -0.266873),
            new jitter_point(0.404568, 0.230436),
            new jitter_point(0.098381, 0.465337),
            new jitter_point(0.462671, 0.442116),
            new jitter_point(0.400373, -0.212720),
            new jitter_point(-0.409988, 0.263345),
            new jitter_point(-0.115878, -0.001981),
            new jitter_point(0.348425, -0.009237),
            new jitter_point(-0.464016, 0.066467),
            new jitter_point(-0.138674, -0.468006),
            new jitter_point(0.144932, -0.022780),
            new jitter_point(-0.250195, 0.150161),
            new jitter_point(-0.181400, -0.264219),
            new jitter_point(0.196097, -0.234139),
            new jitter_point(-0.311082, -0.078815),
            new jitter_point(0.268379, 0.366778),
            new jitter_point(-0.040601, 0.327109),
            new jitter_point(-0.234392, 0.354659),
            new jitter_point(-0.003102, -0.154402),
            new jitter_point(0.297997, -0.417965) };

    /* 66 jitter points */
    jitter_point j66[] = { new jitter_point(0.266377, -0.218171),
            new jitter_point(-0.170919, -0.429368),
            new jitter_point(0.047356, -0.387135),
            new jitter_point(-0.430063, 0.363413),
            new jitter_point(-0.221638, -0.313768),
            new jitter_point(0.124758, -0.197109),
            new jitter_point(-0.400021, 0.482195),
            new jitter_point(0.247882, 0.152010),
            new jitter_point(-0.286709, -0.470214),
            new jitter_point(-0.426790, 0.004977),
            new jitter_point(-0.361249, -0.104549),
            new jitter_point(-0.040643, 0.123453),
            new jitter_point(-0.189296, 0.438963),
            new jitter_point(-0.453521, -0.299889),
            new jitter_point(0.408216, -0.457699),
            new jitter_point(0.328973, -0.101914),
            new jitter_point(-0.055540, -0.477952),
            new jitter_point(0.194421, 0.453510), //
            new jitter_point(0.404051, 0.224974), //
            new jitter_point(0.310136, 0.419700),
            new jitter_point(-0.021743, 0.403898),
            new jitter_point(-0.466210, 0.248839),
            new jitter_point(0.341369, 0.081490),
            new jitter_point(0.124156, -0.016859),
            new jitter_point(-0.461321, -0.176661),
            new jitter_point(0.013210, 0.234401),
            new jitter_point(0.174258, -0.311854),
            new jitter_point(0.294061, 0.263364),
            new jitter_point(-0.114836, 0.328189),
            new jitter_point(0.041206, -0.106205),
            new jitter_point(0.079227, 0.345021),
            new jitter_point(-0.109319, -0.242380),
            new jitter_point(0.425005, -0.332397),
            new jitter_point(0.009146, 0.015098),
            new jitter_point(-0.339084, -0.355707),
            new jitter_point(-0.224596, -0.189548),
            new jitter_point(0.083475, 0.117028),
            new jitter_point(0.295962, -0.334699),
            new jitter_point(0.452998, 0.025397),
            new jitter_point(0.206511, -0.104668),
            new jitter_point(0.447544, -0.096004),
            new jitter_point(-0.108006, -0.002471),
            new jitter_point(-0.380810, 0.130036),
            new jitter_point(-0.242440, 0.186934),
            new jitter_point(-0.200363, 0.070863),
            new jitter_point(-0.344844, -0.230814),
            new jitter_point(0.408660, 0.345826),
            new jitter_point(-0.233016, 0.305203),
            new jitter_point(0.158475, -0.430762),
            new jitter_point(0.486972, 0.139163),
            new jitter_point(-0.301610, 0.009319),
            new jitter_point(0.282245, -0.458671),
            new jitter_point(0.482046, 0.443890),
            new jitter_point(-0.121527, 0.210223),
            new jitter_point(-0.477606, -0.424878),
            new jitter_point(-0.083941, -0.121440),
            new jitter_point(-0.345773, 0.253779),
            new jitter_point(0.234646, 0.034549),
            new jitter_point(0.394102, -0.210901),
            new jitter_point(-0.312571, 0.397656),
            new jitter_point(0.200906, 0.333293),
            new jitter_point(0.018703, -0.261792),
            new jitter_point(-0.209349, -0.065383),
            new jitter_point(0.076248, 0.478538),
            new jitter_point(-0.073036, -0.355064),
            new jitter_point(0.145087, 0.221726) };
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