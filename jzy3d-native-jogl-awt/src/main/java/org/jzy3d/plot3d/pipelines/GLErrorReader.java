package org.jzy3d.plot3d.pipelines;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2ES2;

/**
 * This utility allows checking the GL2 error bit after a call to a GL2 command.
 * 
 * When an error is detected, a textual information is printed out in console.
 * 
 * @author Martin Pernollet
 */
public class GLErrorReader {
  static Logger logger = LogManager.getLogger(GLErrorReader.class);

  /**
   * If an error occured, print it and add an information string at the following line.
   */
  public static void getAndPrintError(GL gl, String info) {
    if (getAndPrintError(gl))
      logger.error(info);
  }

  /** If an error occured, print it. */
  public static boolean getAndPrintError(GL gl) {
    int err = gl.glGetError();

    boolean status = true; // assume an error

    if (err == GL.GL_NO_ERROR) {
      // No error has been recorded. The value of this symbolic constant is guaranteed to be zero.
      status = false;
    } else if (err == GL.GL_INVALID_ENUM) {
      logger.error(
          "GL_INVALID_ENUM: An unacceptable value is specified for an enumerated argument. The offending command is ignored, having no side effect other than to set the error flag.");
    } else if (err == GL.GL_INVALID_VALUE) {
      logger.error(
          "GL_INVALID_VALUE: A numeric argument is out of range. The offending command is ignored, having no side effect other than to set the error flag.");
    } else if (err == GL.GL_INVALID_OPERATION) {
      logger.error(
          "GL_INVALID_OPERATION : The specified operation is not allowed in the current state. The offending command is ignored, having no side effect other than to set the error flag.");
    } else if (err == GL2ES1.GL_STACK_OVERFLOW || err == GL2ES2.GL_STACK_OVERFLOW) {
      logger.error(
          "GL_STACK_OVERFLOW: This command would cause a stack overflow. The offending command is ignored, having no side effect other than to set the error flag.");
    } else if (err == GL2ES1.GL_STACK_UNDERFLOW || err == GL2ES2.GL_STACK_UNDERFLOW) {
      logger.error(
          "GL_STACK_UNDERFLOW: This command would cause a stack underflow. The offending command is ignored, having no side effect other than to set the error flag.");
    } else if (err == GL.GL_OUT_OF_MEMORY) {
      logger.error(
          "GL_OUT_OF_MEMORY: There is not enough memory left to execute the command. The state of the GL2 is undefined, except for the state of the error flags, after this error is recorded.");
    }

    return status;
  }
}
