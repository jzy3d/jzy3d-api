package org.jzy3d.plot3d.rendering.ddp;

import org.apache.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.GLSLProgram.Strictness;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public class DepthPeelingChart extends Chart {
    public DepthPeelingChart(IChartFactory factory, Quality quality) {
		super(factory, quality);
	}

	static Logger LOGGER = Logger.getLogger(DepthPeelingChart.class);

    public static Chart get(Quality quality, String chartType) {
        return get(quality, chartType, PeelingMethod.DUAL_PEELING_MODE);
    }

    public static Chart get(Quality quality, String chartType, PeelingMethod method) {
        return get(quality, chartType, method, Strictness.CONSOLE_NO_WARN_UNIFORM_NOT_FOUND);
    }

    public static Chart get(Quality quality, String chartType, PeelingMethod method, Strictness strictness) {
        return get(quality, chartType, method, strictness, true);
    }

    public static Chart get(Quality quality, String chartType, final PeelingMethod method, Strictness strictness, boolean editFactories) {
        GLSLProgram.DEFAULT_STRICTNESS = strictness;

        IChartFactory factory = new PeelingChartFactory(method);

        LOGGER.info("is available GL2 : " + GLProfile.isAvailable(GLProfile.GL2));
        LOGGER.info("is available GL3 : " + GLProfile.isAvailable(GLProfile.GL3));
        LOGGER.info("is available GL4 : " + GLProfile.isAvailable(GLProfile.GL4));

        /* IF TOO LOW PROFILE ON MAC */
        // ERROR : GLSLProgram: ERROR: 0:10: '' : extension 'ARB_draw_buffers'
        // is not supported
        
        // GLProfile.getDefault(); (isGL2 true)
        // GLProfile.getMinimum(true);
        // GLProfile.getMaxFixedFunc(true);

        /* IF TOO HIGH PROFILE ON MAC */
        // ERROR : jogamp.opengl.gl4.GL4bcImpl.getGL2(GL4bcImpl.java:40488) :
        // Not a GL2 implementation
        
        // GLProfile.getMaxProgrammable(true/false);
        // GLProfile.getMaxProgrammableCore(true/false);
        // GLProfile.getGL2GL3();
        // GLProfile.getGL3();

        /* IF TOO HIGH PROFILE ON MAC */

        
        // GL4BC
        //GLProfile profile = GLProfile.get(GLProfile.GL4bc);

        // Profile XXXX is not available on
        // MacOSXGraphicsDevice[type .macosx, connection decon, unitID 0, handle
        // 0x0, owner false, NullToolkitLock[obj 0x5f375618]], but:
        // [GLProfile[GL2ES1/GL2.hw], GLProfile[GL2ES2/GL3.hw],
        // GLProfile[GL2/GL2.hw], GLProfile[GL2/GL2.hw], GLProfile[GL3/GL3.hw],
        // GLProfile[GL2GL3/GL3.hw]]


        GLProfile profile = GLProfile.get(GLProfile.GL4); 
        
        
        // GL2
        //GLProfile profile = GLProfile.get(GLProfile.GL2); 
        //GLSLProgram: ERROR: 0:10: 'DepthTex' : syntax error: syntax error
        
        // GLProfile.get(GLProfile.GL4bc); 


        LOGGER.info(profile);

        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setHardwareAccelerated(true);

        Chart chart = null;//new DepthPeelingChart(factory, quality, chartType, capabilities);
        chart.getView().setSquared(false);
        chart.getView().setAxeBoxDisplayed(true);
        return chart;
    }
}
