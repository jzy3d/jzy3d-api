package org.jzy3d.demos.javafx;

import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.util.Animator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;


/*
--module-path /Users/martin/Dev/javafx-sdk-19/lib --add-modules javafx.controls,javafx.graphics 
--add-opens javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED
--add-opens javafx.controls/com.sun.javafx.charts=ALL-UNNAMED
--add-opens javafx.controls/com.sun.javafx.scene.control.inputmap=ALL-UNNAMED
--add-opens javafx.graphics/com.sun.javafx.iio=ALL-UNNAMED
--add-opens javafx.graphics/com.sun.javafx.iio.common=ALL-UNNAMED
--add-opens javafx.graphics/com.sun.javafx.css=ALL-UNNAMED
--add-opens javafx.base/com.sun.javafx.runtime=ALL-UNNAMED
-Dnativewindow.debug.JAWT --add-exports=java.desktop/sun.awt=ALL-UNNAMED
 */

// --add-modules javafx.graphics,javafx.controls 
// --add-opens javafx.graphics com.sun.javafx.tk=ALL-UNNAMED
public class DemoJavaFX_NewtCanvasJFX extends Application {

  @Override
  public void start(Stage stage) {
    
    Platform.setImplicitExit(false);
    final Group g = new Group();
    Scene scene = new Scene(g, 800, 600);
    stage.setScene(scene);
    stage.show();
    com.jogamp.newt.Display jfxNewtDisplay = NewtFactory.createDisplay(null, false);
    final Screen screen = NewtFactory.createScreen(jfxNewtDisplay, 0);
    final GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
    final GLWindow glWindow1 = GLWindow.create(screen, caps);
    glWindow1.addGLEventListener(new GLEventListener() {
      private float rotateT = 0.0f;

      public void init(final GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
      }

      public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width,
          final int height) {
        GL2 gl = drawable.getGL().getGL2();
        final float aspect = (float) width / (float) height;
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        final float fh = 0.5f;
        final float fw = fh * aspect;
        gl.glFrustumf(-fw, fw, -fh, fh, 1.0f, 1000.0f);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
      }

      public void display(final GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);

        // rotate about the three axes
        gl.glRotatef(rotateT, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateT, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(rotateT, 0.0f, 0.0f, 1.0f);

        // Draw A Quad
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f(0.0f, 1.0f, 1.0f); // set the color of the quad
        gl.glVertex3f(-1.0f, 1.0f, 0.0f); // Top Left
        gl.glVertex3f(1.0f, 1.0f, 0.0f); // Top Right
        gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom Right
        gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom Left
        // Done Drawing The Quad
        gl.glEnd();

        // increasing rotation for the next iteration
        rotateT += 0.2f;
      }

      public void dispose(final GLAutoDrawable drawable) {}
    });
    final NewtCanvasJFX glCanvas = new NewtCanvasJFX(glWindow1);
    glCanvas.setWidth(800);
    glCanvas.setHeight(600);
    g.getChildren().add(glCanvas);
    final Animator anim = new Animator(glWindow1);
    anim.start();
  }

  public static void main(String[] args) {
    launch();
  }
}
