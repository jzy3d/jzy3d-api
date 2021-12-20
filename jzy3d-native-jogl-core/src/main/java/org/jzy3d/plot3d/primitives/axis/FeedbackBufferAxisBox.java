package org.jzy3d.plot3d.primitives.axis;

import java.nio.FloatBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.RenderMode;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;

/**
 * This {@link AxisBox} implementation was the first to appear in Jzy3d. It computes hidden faces
 * via method {@link getHiddenQuads} using the OpenGL feedback buffer, which seems to fail on some
 * laptop GPU. (especially Lenovo notepads).
 * 
 * @author Martin Pernollet
 */
public class FeedbackBufferAxisBox extends AxisBox implements IAxis {
  static Logger logger = LogManager.getLogger(FeedbackBufferAxisBox.class);

  public FeedbackBufferAxisBox(BoundingBox3d bbox) {
    super(bbox);
  }

  public FeedbackBufferAxisBox(BoundingBox3d bbox, IAxisLayout layout) {
    super(bbox, layout);
  }
  
  /** Make all GL2 calls allowing to build a cube with 6 separate quads. Each quad is indexed from
   * 0.0f to 5.0f using glPassThrough, and may be traced in feedback mode when mode=GL2.GL_FEEDBACK */
  protected void drawCube(IPainter painter, RenderMode mode) {
    for (int q = 0; q < 6; q++) {
      if (!getQuadIsHidden()[q]) { // makes culling useless!
        
        // the below check to passtrough is made for feedback buffer axis box
        if (mode == RenderMode.FEEDBACK)
          painter.glPassThrough(q);
        
        //painter.glBegin_Quad();
        painter.glBegin_LineLoop();
        for (int v = 0; v < 4; v++) {
          painter.vertex(quadx[q][v], quady[q][v], quadz[q][v], spaceTransformer);
        }
        painter.glEnd();
      }
    }
  }

  /**
   * Render the cube into the feedback buffer, in order to parse feedback and determine which quad
   * where displayed or not.
   */
  @Override
  protected boolean[] getHiddenQuads(IPainter painter) {
    int feedbacklength = 1024;
    FloatBuffer floatbuffer = Buffers.newDirectFloatBuffer(feedbacklength);
    float[] feedback = new float[feedbacklength];

    // Draw the cube into feedback buffer
    painter.glFeedbackBuffer(feedbacklength, GL2.GL_3D_COLOR, floatbuffer);
    painter.glRenderMode(RenderMode.FEEDBACK);
    drawCube(painter, RenderMode.FEEDBACK);
    painter.glRenderMode(RenderMode.RENDER);

    // Parse feedback buffer and return hidden quads
    floatbuffer.get(feedback);
    return getEmptyTokens(6, feedback, feedbacklength);
  }

  /**
   * This utility function inform wether a GL_PASS_THROUGH_TOKEN was followed by other OpenGL tokens
   * (GL_POLYGON_TOKEN, GL_LINE_TOKEN, etc), or not. In the first case, the given token is
   * considered as non empty, in the other case, it is considered as empty. The expected use of this
   * function is to inform the user wether a polygon has been displayed or not in the case Culling
   * was activated.
   * 
   * Note: this function is only intended to work in the case where gl.glFeedbackBuffer is called
   * with GL.GL_3D_COLOR as second argument, i.e. vertices are made of x, y, z, alpha values and a
   * color. Thus, no texture may be used, and trying to parse a GL_BITMAP_TOKEN,
   * GL_DRAW_PIXEL_TOKEN, or GL_COPY_PIXEL_TOKEN will throw a RuntimeException.
   * 
   * Note: this function only works with PASS_THROUGH_TOKEN with a positive integer value, since
   * this value will be used as an index in the boolean array returned by the function
   * 
   * TODO: use a map to associate a float token to a polygon with an id?
   * 
   * @throws a RuntimeException if a parsed token is either GL_BITMAP_TOKEN, GL_DRAW_PIXEL_TOKEN, or
   *         GL_COPY_PIXEL_TOKEN.
   */
  protected boolean[] getEmptyTokens(int ntokens, float[] buffer, int size) {
    boolean[] isempty = new boolean[ntokens];
    boolean printout = false; // for debugging parsing

    int count = size;
    float token_type;
    float prevtoken_type = Float.NaN;
    float passthrough_value = Float.NaN;
    float prevpassthrough_value = Float.NaN;
    int prevtoken_id;

    float EMPTY_TOKEN = 0.0f;

    while (count > 0) {
      token_type = buffer[size - count];
      count--;

      // Case of a PASS THROUGH token
      if (token_type == GL2.GL_PASS_THROUGH_TOKEN) { // can't use switch cause we have floats
        passthrough_value = buffer[size - count];
        count--;

        if (printout)
          logger.info("GL.GL_PASS_THROUGH_TOKEN: " + passthrough_value);

        // If the preceding token is also a GL_PASS_THROUGH_TOKEN
        // we consider it as an empty token
        if (!Float.isNaN(prevpassthrough_value)) {
          prevtoken_id = (int) prevpassthrough_value;
          if (token_type == prevtoken_type)
            isempty[prevtoken_id] = true;
          else
            isempty[prevtoken_id] = false;
        }

        // Store current token value as previous
        prevpassthrough_value = passthrough_value;
      }

      // Other cases: just consume buffer
      else if (token_type == GL2.GL_POINT_TOKEN) {
        if (printout) {
          logger.info(" GL.GL_POINT_TOKEN");
          count = print3DcolorVertex(size, count, buffer);
        } else {
          count = count - 7;
        }
      } else if (token_type == GL2.GL_LINE_TOKEN) {
        if (printout) {
          logger.info(" GL.GL_LINE_TOKEN ");
          count = print3DcolorVertex(size, count, buffer);
          count = print3DcolorVertex(size, count, buffer);
        } else {
          count = count - 14;
        }
      } else if (token_type == GL2.GL_LINE_RESET_TOKEN) {
        if (printout) {
          logger.info(" GL.GL_LINE_RESET_TOKEN ");
          count = print3DcolorVertex(size, count, buffer);
          count = print3DcolorVertex(size, count, buffer);
        } else {
          count = count - 14;
        }
      } else if (token_type == GL2.GL_POLYGON_TOKEN) {
        int n = (int) buffer[size - count];
        count--;

        if (printout)
          logger.info(" GL.GL_POLYGON_TOKEN: " + n + " vertices");

        for (int i = 0; i < n; i++) {
          if (printout)
            count = print3DcolorVertex(size, count, buffer);
          else
            count = count - 7;
        }
      }

      // Case of an empty token
      else if (token_type == EMPTY_TOKEN) { // MAYBE THE CONTENT IS 0.0?? DEPENDING ON THE VALUE OF
                                            // THE TOKEN CONSTANTS!!!
        ; // this is a non token: array was not filled

        // If the prev token is a GL_PASS_THROUGH_TOKEN, it was empty
        if (prevtoken_type == GL2.GL_PASS_THROUGH_TOKEN) {
          prevtoken_id = (int) prevpassthrough_value;
          isempty[prevtoken_id] = true;
        }
        break;
      }
      // Case of an unknown token
      else if (token_type == GL2.GL_BITMAP_TOKEN)
        throw new RuntimeException("Unknown token:" + token_type
            + ". This function is not intended to work with GL_BITMAP_TOKEN.");
      else if (token_type == GL2.GL_DRAW_PIXEL_TOKEN)
        throw new RuntimeException("Unknown token:" + token_type
            + ". This function is not intended to work with GL_DRAW_PIXEL_TOKEN.");
      else if (token_type == GL2.GL_COPY_PIXEL_TOKEN)
        throw new RuntimeException("Unknown token:" + token_type
            + ". This function is not intended to work with GL_COPY_PIXEL_TOKEN.");
      else {
        throw new RuntimeException("Unknown token type: " + token_type + "\n count= " + count);
      }
      // Update previous token
      prevtoken_type = token_type;
    }
    return isempty;
  }

  /**
   * Print out parameters of a gl call in 3dColor mode.
   */
  protected int print3DcolorVertex(int size, int count, float[] buffer) {
    int i;
    int id = size - count;
    int veclength = 7;

    System.out.print("  [" + id + "]");
    for (i = 0; i < veclength; i++) {
      System.out.print(" " + buffer[size - count]);
      count = count - 1;
    }
    System.out.println();
    return count;
  }
}
