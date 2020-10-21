package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * A {@link SimplePolygon} makes the simplest possible GL rendering with
 * especially no:
 * <ul>
 * <li>gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
 * <li>gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
 * </ul>
 * 
 * @author Martin Pernollet
 */
public class SimplePolygon extends Polygon implements ISingleColorable, IMultiColorable {
    public SimplePolygon() {
        super();
    }

    /**********************************************************************/

    @Override
    public void draw(GL gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);

        if (gl.isGL2()) {

            // Draw content of polygon
            if (facestatus) {
                // if(wfstatus)
                // polygonOffseFillEnable(gl);

                gl.getGL2().glBegin(GL2.GL_POLYGON);
                for (Point p : points) {
                    if (mapper != null) {
                        Color c = mapper.getColor(p.xyz); // TODO: should store
                                                          // result in the
                                                          // point color
                        gl.getGL2().glColor4f(c.r, c.g, c.b, c.a);
                    } else {
                        gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                    }
                    gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                }
                gl.getGL2().glEnd();
                // if(wfstatus)
                // polygonOffsetFillDisable(gl);
            }

            // Draw edge of polygon
            if (wfstatus) {
                gl.getGL2().glBegin(GL2.GL_POLYGON);
                gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                gl.getGL2().glLineWidth(wfwidth);
                for (Point p : points) {
                    gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                }
                gl.getGL2().glEnd();
            }
        } else {
            // Draw content of polygon
            if (facestatus) {
                // if(wfstatus)
                // polygonOffseFillEnable(gl);

                GLES2CompatUtils.glBegin(GL2.GL_POLYGON);
                for (Point p : points) {
                    if (mapper != null) {
                        Color c = mapper.getColor(p.xyz); // TODO: should store
                                                          // result in the
                                                          // point color

                        GLES2CompatUtils.glColor4f(c.r, c.g, c.b, c.a);
                    } else {

                        GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                    }

                    GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                }

                GLES2CompatUtils.glEnd();
                // if(wfstatus)
                // polygonOffsetFillDisable(gl);
            }

            // Draw edge of polygon
            if (wfstatus) {

                GLES2CompatUtils.glBegin(GL2.GL_POLYGON);

                GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);

                GLES2CompatUtils.glLineWidth(wfwidth);
                for (Point p : points) {

                    GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                }

                GLES2CompatUtils.glEnd();
            }
        }

        doDrawBounds(gl, glu, cam);
    }
}
