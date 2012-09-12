package glredbook1314;

import glredbook10.GLSkeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.gl2.GLUT;
 

public class shadowmap //
        extends GLSkeleton<GLCanvas>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private GLUT glut;
    private FPSAnimator animator;
//    private KeyEvent key;
    private boolean textureOn = true;
    private boolean compareMode = true;
    private boolean funcMode = true;
    private boolean animate = true;

    private static final int SHADOW_MAP_WIDTH = 256;
    private static final int SHADOW_MAP_HEIGHT = 256;

    private double fovy = 60.0;
    private double nearPlane = 10.0;
    private double farPlane = 100.0;

    private float angle = 0.0f;
    private float torusAngle = 0.0f;

    private float lightPos[] = { 25.0f, 25.0f, 25.0f, 1.0f };
    private float lookat[] = { 0.0f, 0.0f, 0.0f };
    private float up[] = { 0.0f, 0.0f, 1.0f };

    private boolean showShadow = false;

     @Override
    protected GLCanvas createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLCanvas panel = new GLCanvas(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }

    public void run() {
        shadowmap demo = new shadowmap();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("shadowmap");
        frame.getContentPane().add(demo.drawable);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(512, 512);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
        //
        animator = new FPSAnimator(demo.drawable, 30);
        animator.start();
    }

    public static void main(String[] args) {
        // set argument 'NotFirstUIActionOnProcess' in the JNLP's application-desc tag for example
        // <application-desc main-class="demos.j2d.TextCube"/>
        //   <argument>NotFirstUIActionOnProcess</argument>
        // </application-desc>
        // boolean firstUIActionOnProcess = 0==args.length || !args[0].equals("NotFirstUIActionOnProcess") ;
        // GLProfile.initSingleton(firstUIActionOnProcess);
        //GLProfile.initSingleton(); // just lazy to touch all html/jnlp's

        new shadowmap().run();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();
        //
        final float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };

        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT,
                SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, 0, GL2.GL_DEPTH_COMPONENT,
                GL.GL_UNSIGNED_BYTE, null);

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, white, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, white, 0);

        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D,//
                GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D,//
                GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_COMPARE_FUNC, GL.GL_LEQUAL);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_DEPTH_TEXTURE_MODE, GL.GL_LUMINANCE);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D,//
                GL2.GL_TEXTURE_COMPARE_MODE, GL2.GL_COMPARE_R_TO_TEXTURE);

        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
        gl.glTexGeni(GL2.GL_R, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
        gl.glTexGeni(GL2.GL_Q, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);

        gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE & GL2.GL_SPECULAR);

        gl.glCullFace(GL.GL_BACK);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
        gl.glEnable(GL2.GL_TEXTURE_GEN_R);
        gl.glEnable(GL2.GL_TEXTURE_GEN_Q);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glEnable(GL.GL_CULL_FACE);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        float radius = 30;

        if (textureOn)
            gl.glEnable(GL2.GL_TEXTURE_2D);
        else
            gl.glDisable(GL2.GL_TEXTURE_2D);

        if (compareMode)
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_MODE,
                    GL2.GL_COMPARE_R_TO_TEXTURE );
        else
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_MODE,
                  GL.GL_NONE );

        if (funcMode)
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_FUNC,
                    GL.GL_LEQUAL  );
        else        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_FUNC,
                 GL.GL_GEQUAL);

        
//        if (showShadow)
//        {  
//        return;}

        if (showShadow)
            return;

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        generateShadowMap(gl);
        generateTextureMatrix(gl);

        gl.glPushMatrix();
        glu.gluLookAt(radius * Math.cos(angle), radius * Math.sin(angle), 30,
                lookat[0], lookat[1], lookat[2], //
                up[0], up[1], up[2]);
        drawObjects(gl, false);
        gl.glPopMatrix();

        gl.glFlush();
        // angle += (float) Math.PI / 10000;
        // torusAngle += .1;
        idle();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(fovy, (double) w / h, nearPlane, farPlane);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void idle() {
        angle += (float) Math.PI / 10000;
        torusAngle += .1;
    }

    private void drawObjects(GL2 gl, boolean shadowRender) {
        boolean textureOn = gl.glIsEnabled(GL2.GL_TEXTURE_2D);

        if (shadowRender)
            gl.glDisable(GL2.GL_TEXTURE_2D);

        if (!shadowRender) {
            gl.glNormal3f(0, 0, 1);
            gl.glColor3f(1, 1, 1);
            gl.glRectf(-20.0f, -20.0f, 20.0f, 20.0f);
        }

        gl.glPushMatrix();
        gl.glTranslatef(11, 11, 11);
        gl.glRotatef(54.73f, -5, 5, 0);
        gl.glRotatef(torusAngle, 1, 0, 0);
        gl.glColor3f(1, 0, 0);
        glut.glutSolidTorus(1, 4, 8, 36);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(2, 2, 2);
        gl.glColor3f(0, 0, 1);
        glut.glutSolidCube(4);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(lightPos[0], lightPos[1], lightPos[2]);
        gl.glColor3f(1, 1, 1);
        glut.glutWireSphere(0.5, 6, 6);
        gl.glPopMatrix();

        if (shadowRender && textureOn)
            gl.glEnable(GL2.GL_TEXTURE_2D);
    }
    FloatBuffer depthImageBuf = GLBuffers.newDirectFloatBuffer(SHADOW_MAP_WIDTH * SHADOW_MAP_HEIGHT);
    
    private void generateShadowMap(GL2 gl) {
        int viewport[] = {0,0,0,0};//new int[4];
        float lightPos[] = {0,0,0,0};//new float[4];

        gl.glGetLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

        gl.glViewport(0, 0, SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT);

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluPerspective(80.0, 1.0, 10.0, 1000.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluLookAt(lightPos[0], lightPos[1], lightPos[2],//
                lookat[0], lookat[1], lookat[2], //
                up[0], up[1], up[2]);

        drawObjects(gl, true);

        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        gl.glCopyTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT, 0, 0,
                SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, 0);

        gl.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);

        if (showShadow) {
            // float depthImage[][] = new
            // float[SHADOW_MAP_WIDTH][SHADOW_MAP_HEIGHT];
//            FloatBuffer depthImageBuf = BufferUtil//
//                    .newFloatBuffer(SHADOW_MAP_HEIGHT * SHADOW_MAP_WIDTH);
            gl.glReadPixels(0, 0, SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT,
                    GL2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, depthImageBuf);
            gl.glWindowPos2f(viewport[2] / 2, 0);
            depthImageBuf.rewind();
//            gl.glDrawPixels(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT,
//                    GL.GL_LUMINANCE, GL.GL_FLOAT, depthImageBuf);
            gl.glDrawPixels(SHADOW_MAP_HEIGHT, SHADOW_MAP_WIDTH,
                    GL.GL_LUMINANCE, GL.GL_FLOAT, depthImageBuf);
//            depthImageBuf = null;
            // glutSwapBuffers();
//            demo.canvas.display();
            // demo.canvas.display();causes overflow!
        }
    }
    FloatBuffer tmpMatbuf = GLBuffers.newDirectFloatBuffer(16);

    private void generateTextureMatrix(GL2 gl) {
        // float tmpMatrix[] = new float[16];

        /*
         * Set up projective texture matrix. We use the GL_MODELVIEW matrix
         * stack and OpenGL matrix commands to make the matrix.
         */
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(0.5f, 0.5f, 0.0f);
        gl.glScalef(0.5f, 0.5f, 1.0f);
        glu.gluPerspective(60.0f, 1.0, 1.0, 1000.0);
        glu.gluLookAt(lightPos[0], lightPos[1], lightPos[2], //
                lookat[0],
                lookat[1], lookat[2], //
                up[0], up[1], up[2]);
        // gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, tmpMatrix, 0);
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, tmpMatbuf);
        gl.glPopMatrix();

        // System.out.println(tmpMatbuf.toString());
        // for (int i =0; i < tmpMatbuf.capacity(); i++)
        // System.out.println(tmpMatbuf.get(i)+".");
        // transposeMatrix(FloatBuffer.wrap(tmpMatrix));
//        tmpMatbuf.rewind();
        // transposeMatrix(tmpMatbuf);
        transpose(tmpMatbuf);

        gl.glTexGenfv(GL2.GL_S, GL2.GL_OBJECT_PLANE, tmpMatbuf);// , 0);
        tmpMatbuf.position(4);
        // System.out.println(tmpMatbuf.position());
        gl.glTexGenfv(GL2.GL_T, GL2.GL_OBJECT_PLANE, tmpMatbuf);// , 4);
        tmpMatbuf.position(8);
        // System.out.println(tmpMatbuf.position());
        gl.glTexGenfv(GL2.GL_R, GL2.GL_OBJECT_PLANE, tmpMatbuf);// , 8);
        tmpMatbuf.position(12);
        // System.out.println(tmpMatbuf.position());
        gl.glTexGenfv(GL2.GL_Q, GL2.GL_OBJECT_PLANE, tmpMatbuf);// , 12);
        // System.out.println(tmpMatbuf.position());
        tmpMatbuf.rewind();
    }

    private void transpose(FloatBuffer mat) {
        
        float tmp = mat.get(1);
        mat.put(1, mat.get(4));
        mat.put(4, tmp);
        
        tmp= mat.get(2);
        mat.put(2, mat.get(8));
        mat.put(8, tmp);
        
        tmp = mat.get(3 );
        mat.put(3, mat.get(12));
        mat.put(12, tmp);
        
        tmp = mat.get(6);
        mat.put(6, mat.get(9));
        mat.put(9, tmp);

        tmp = mat.get(7);
        mat.put(7, mat.get(13));
        mat.put(13, tmp);
        
        tmp = mat.get(11);
        mat.put(11, mat.get(14));
        mat.put(14, tmp);mat.rewind();
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
//        this.key = key;
        switch (key.getKeyChar()) {

        case KeyEvent.VK_ESCAPE:
            new Thread() {
                public void run() {
                    animator.stop();
                }
            }.start();
            System.exit(0);
            break;

        case 's':
            showShadow = !showShadow;
            break;

        case 'p':
            animate = !animate;
            if (animate)
                animator.start();
            else
                animator.stop();
            break;

        case 't':
            textureOn = !textureOn;
            System.out.println("textureOn: " + textureOn);
            break;

        case 'm':
            compareMode = !compareMode;
            System.out.println("Compare mode " + (compareMode ? "On" : "Off"));
            break;

        case 'f':
            funcMode = !funcMode;
            System.out.println//
                    ("Operator " + (funcMode ? "GL_LEQUAL" : "GL_GEQUAL"));
            break;

        }

        super.refresh();
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
