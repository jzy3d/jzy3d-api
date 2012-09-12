package glredbook11;

import glredbook10.GLSkeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;
import javax.swing.JFrame;

/**
 * This program demonstrates polygon tessellation. Two tesselated objects are
 * drawn. The first is a rectangle with a triangular hole. The second is a
 * smooth shaded, self-intersecting star. Note the exterior rectangle is drawn
 * with its vertices in counter-clockwise order, but its interior clockwise.
 * Note the combineCallback is needed for the self-intersecting star. Also note
 * that removing the TessProperty for the star will make the interior unshaded
 * (WINDING_ODD).
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 * @NOTE Java arrays are column major whereas C arrays are row major
 */
public class tess//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private int startList;

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
        tess demo = new tess();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("tess");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        /*
         * jogl specific addition for tessellation
         */
        tessellCallBack tessCallback = new tessellCallBack(gl, glu);
        //
        double rect[][] = new double[][] { // [4][3] in C; reverse here
        { 50.0, 50.0, 0.0 }, { 200.0, 50.0, 0.0 }, { 200.0, 200.0, 0.0 },
                { 50.0, 200.0, 0.0 } };
        double tri[][] = new double[][] {// [3][3]
        { 75.0, 75.0, 0.0 }, { 125.0, 175.0, 0.0 }, { 175.0, 75.0, 0.0 } };
        double star[][] = new double[][] {// [5][6]; 6x5 in java
        { 250.0, 50.0, 0.0, 1.0, 0.0, 1.0 },
                { 325.0, 200.0, 0.0, 1.0, 1.0, 0.0 },
                { 400.0, 50.0, 0.0, 0.0, 1.0, 1.0 },
                { 250.0, 150.0, 0.0, 1.0, 0.0, 0.0 },
                { 400.0, 150.0, 0.0, 0.0, 1.0, 0.0 } };

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        startList = gl.glGenLists(2);

        GLUtessellator tobj = glu.gluNewTess();

        glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// glVertex3dv);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);

        /* rectangle with triangular hole inside */
        gl.glNewList(startList, GL2.GL_COMPILE);
        gl.glShadeModel(GL2.GL_FLAT);
        glu.gluTessBeginPolygon(tobj, null);
        glu.gluTessBeginContour(tobj);
        glu.gluTessVertex(tobj, rect[0], 0, rect[0]);
        glu.gluTessVertex(tobj, rect[1], 0, rect[1]);
        glu.gluTessVertex(tobj, rect[2], 0, rect[2]);
        glu.gluTessVertex(tobj, rect[3], 0, rect[3]);
        glu.gluTessEndContour(tobj);
        glu.gluTessBeginContour(tobj);
        glu.gluTessVertex(tobj, tri[0], 0, tri[0]);
        glu.gluTessVertex(tobj, tri[1], 0, tri[1]);
        glu.gluTessVertex(tobj, tri[2], 0, tri[2]);
        glu.gluTessEndContour(tobj);
        glu.gluTessEndPolygon(tobj);
        gl.glEndList();

        glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);

        /* smooth shaded, self-intersecting star */
        gl.glNewList(startList + 1, GL2.GL_COMPILE);
        gl.glShadeModel(GL2.GL_SMOOTH);
        glu.gluTessProperty(tobj, //
                GLU.GLU_TESS_WINDING_RULE, //
                GLU.GLU_TESS_WINDING_POSITIVE);
        glu.gluTessBeginPolygon(tobj, null);
        glu.gluTessBeginContour(tobj);
        glu.gluTessVertex(tobj, star[0], 0, star[0]);
        glu.gluTessVertex(tobj, star[1], 0, star[1]);
        glu.gluTessVertex(tobj, star[2], 0, star[2]);
        glu.gluTessVertex(tobj, star[3], 0, star[3]);
        glu.gluTessVertex(tobj, star[4], 0, star[4]);
        glu.gluTessEndContour(tobj);
        glu.gluTessEndPolygon(tobj);
        gl.glEndList();
        glu.gluDeleteTess(tobj);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glCallList(startList);
        gl.glCallList(startList + 1);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, (double) w, 0.0, (double) h);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);

        default:
            break;
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

    /*
     * Tessellator callback implemenation with all the callback routines. YOu
     * could use GLUtesselatorCallBackAdapter instead. But
     */
    class tessellCallBack //
            implements GLUtessellatorCallback //
    {
        private GL2 gl;
        private GLU glu;

        public tessellCallBack(GL2 gl, GLU glu) {
            this.gl = gl;
            this.glu = glu;
        }

        public void begin(int type) {
            gl.glBegin(type);
        }

        public void end() {
            gl.glEnd();
        }

        public void vertex(Object vertexData) {
            double[] pointer;
            if (vertexData instanceof double[]) {
                pointer = (double[]) vertexData;
                if (pointer.length == 6)
                    gl.glColor3dv(pointer, 3);
                gl.glVertex3dv(pointer, 0);
            }

        }

        public void vertexData(Object vertexData, Object polygonData) {
        }

        /*
         * combineCallback is used to create a new vertex when edges intersect.
         * coordinate location is trivial to calculate, but weight[4] may be
         * used to average color, normal, or texture coordinate data. In this
         * program, color is weighted.
         */
        public void combine(double[] coords, Object[] data, //
                float[] weight, Object[] outData) {
            double[] vertex = new double[6];
            int i;

            vertex[0] = coords[0];
            vertex[1] = coords[1];
            vertex[2] = coords[2];
            for (i = 3; i < 6/* 7OutOfBounds from C! */; i++)
                vertex[i] = weight[0] //
                        * ((double[]) data[0])[i] + weight[1]
                        * ((double[]) data[1])[i] + weight[2]
                        * ((double[]) data[2])[i] + weight[3]
                        * ((double[]) data[3])[i];
            outData[0] = vertex;
        }

        public void combineData(double[] coords, Object[] data, //
                float[] weight, Object[] outData, Object polygonData) {
        }

        public void error(int errnum) {
            String estring;

            estring = glu.gluErrorString(errnum);
            System.err.println("Tessellation Error: " + estring);
            System.exit(0);
        }

        public void beginData(int type, Object polygonData) {
        }

        public void endData(Object polygonData) {
        }

        public void edgeFlag(boolean boundaryEdge) {
        }

        public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
        }

        public void errorData(int errnum, Object polygonData) {
        }
    }// tessellCallBack

}// tess
