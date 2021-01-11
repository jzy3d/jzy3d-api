package org.jzy3d.plot3d.rendering.ddp;

import org.apache.log4j.Logger;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.IGLRenderer;
import org.jzy3d.plot3d.rendering.ddp.algorithms.DualDepthPeelingAlgorithm;
import org.jzy3d.plot3d.rendering.ddp.algorithms.FrontToBackPeelingAlgorithm;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.ddp.algorithms.WeightedAveragePeelingAlgorithm;
import org.jzy3d.plot3d.rendering.ddp.algorithms.WeightedSumPeelingAlgorithm;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import jogamp.opengl.gl4.GL4bcImpl;

/**
 * Execute depth peeling methods in a Jzy3d {@link Renderer3d}
 * 
 * This feature is based on Order Independent Transparency algorithms published
 * by Louis Bavoil (NVIDIA Corporation).
 * 
 * The renderer support 4 methods:
 * <ul>
 * <li>Dual depth peeling (accurate)
 * <li>Front to back peeling (accurate)
 * <li>Weighted average peeling (fast)
 * <li>Weighted sum peeling (fast)
 * </ul>
 * 
 * Depth peeling is traditionally used to perform order independent transparency
 * (OIT) with N geometry passes for N transparency layers. Dual depth peeling
 * enables peeling N transparency layers in N/2+1 passes, by peeling from the
 * front and the back simultaneously using a min-max depth buffer. This sample
 * performs either normal or dual depth peeling and blends on the fly.
 * 
 * @author Louis Bavoil - original paper and C++ code
 * @author Martin Pernollet - port to Jzy3d
 */
public class DepthPeelingRenderer3d extends Renderer3d {
    protected Logger LOGGER = Logger.getLogger(DepthPeelingRenderer3d.class);

    protected IDepthPeelingAlgorithm dualPeelingAlgorithm;
    protected boolean autoSwapBuffer = false;
    protected static boolean AXE_IN_PEELING = false;
    protected static boolean DEBUG = false;

    public DepthPeelingRenderer3d(final DepthPeelingView view, boolean traceGL, boolean debugGL) {
        this(PeelingMethod.WEIGHTED_AVERAGE_MODE, view, traceGL, debugGL);
    }

    public DepthPeelingRenderer3d(PeelingMethod algorithm, final DepthPeelingView view, boolean traceGL, boolean debugGL) {
        super(view, traceGL, debugGL);
        dualPeelingAlgorithm = getDepthPeelingAlgorithm(algorithm);
        dualPeelingAlgorithm.setTasksToRender(getDepthPeelingContentRenderer(view));
    }

    @Override
    public void init(GLAutoDrawable canvas) {
        if (canvas != null) {// && canvas.getGL() != null &&
                             // canvas.getGL().getGL2() != null && view != null)
                             // {
            /*
             * if (debugGL)
             * canvas.getGL().getContext().setGL(GLPipelineFactory.create
             * ("javax.media.opengl.Debug", null, canvas.getGL(), null)); if
             * (traceGL)
             * canvas.getGL().getContext().setGL(GLPipelineFactory.create
             * ("javax.media.opengl.Trace", null, canvas.getGL(), new Object[] {
             * System.err }));
             */
            view.init();
        }

        // super.init(drawable);

        canvas.setAutoSwapBufferMode(autoSwapBuffer);
        dualPeelingAlgorithm.init(view.getPainter(), getGL2(canvas), width, height);
    }

    protected GL2 getGL2(GLAutoDrawable drawable) {
        LOGGER.info("GL context : " + drawable.getContext());
        LOGGER.info("GL profile : " + drawable.getGLProfile());
        LOGGER.info("GL impl : " + drawable.getGL().getClass().getSimpleName());

        LOGGER.info("isGL : " + drawable.getGL().isGL());
        LOGGER.info("isGL2 : " + drawable.getGL().isGL2());
        LOGGER.info("isGLES1 : " + drawable.getGL().isGL2ES1());
        LOGGER.info("isGLES2 : " + drawable.getGL().isGL2ES2());
        LOGGER.info("isGLES3 : " + drawable.getGL().isGL2ES3());
        LOGGER.info("isGL2GL3 : " + drawable.getGL().isGL2GL3());
        LOGGER.info("isGL3 : " + drawable.getGL().isGL3());
        LOGGER.info("isGL3core : " + drawable.getGL().isGL3core());
        LOGGER.info("isGL3ES3 : " + drawable.getGL().isGL3ES3());
        LOGGER.info("isGL4 : " + drawable.getGL().isGL4());
        LOGGER.info("isGL4bc : " + drawable.getGL().isGL4bc());

        
        if (drawable.getGL() instanceof GL4bcImpl) {
            return drawable.getGL().getGL3().getGL2();//(GL2)drawable;//drawable.getGL().getGL2();//getGL3().getGL2();
        } else {
            return (GL2)drawable;//drawable.getGL().getGL2();
        }
    }

    public static boolean DECOMPOSE_VIEW = true;

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = getGL2(drawable);

        preDisplay(gl);
        dualPeelingAlgorithm.display(view.getPainter(), gl, glu); // will call taskToRender
        postDisplay(gl);

        if (!autoSwapBuffer)
            drawable.swapBuffers();
    }

    public void postDisplay(GL2 gl) {
        view.renderOverlay();
    }

    public void preDisplay(GL2 gl) {
        ((DepthPeelingView) view).clearPeeledView(gl, glu, width, height);
    }

    public static IGLRenderer getDepthPeelingContentRenderer(final View view) {
        return new IGLRenderer() {
            @Override
            public void draw(IPainter painter) {

                ((DepthPeelingView) view).renderPeeledView();
            }
        };
    }

    /**
     * Rebuild all depth peeling buffers for the new screen size.
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = getGL2(drawable);

        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            dualPeelingAlgorithm.reshape(view.getPainter(), gl, width, height);
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        dualPeelingAlgorithm.dispose(view.getPainter(), getGL2(drawable));
    }

    public static IDepthPeelingAlgorithm getDepthPeelingAlgorithm(PeelingMethod method) {
        if (method == PeelingMethod.DUAL_PEELING_MODE)
            return new DualDepthPeelingAlgorithm();
        else if (method == PeelingMethod.F2B_PEELING_MODE)
            return new FrontToBackPeelingAlgorithm();
        else if (method == PeelingMethod.WEIGHTED_AVERAGE_MODE)
            return new WeightedAveragePeelingAlgorithm();
        else if (method == PeelingMethod.WEIGHTED_SUM_MODE)
            return new WeightedSumPeelingAlgorithm();
        else
            throw new RuntimeException("Unknown method:" + method);
    }
}
