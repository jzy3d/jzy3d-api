package org.jzy3d.incubation.volume;

import java.nio.Buffer;

import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.incubation.vbo.ColormapTexture;
import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.VectorUtil;

public class Texture3D extends Drawable implements IGLBindedResource,IMultiColorable{

	/** The GL texture ID. */
    private int texID;
    private Buffer buffer;
    private int[] shape;
    private boolean mounted = false;
    private DrawableVBO shapeVBO;
    private GLSLProgram shaderProgram;
    private float min;
    private float max;
    private ColormapTexture colormapTexure;
    
	private boolean disposed;
	private ColorMapper mapper;
    
    public Texture3D(Buffer buffer, int[] shape, float min, float max, ColorMapper mapper, BoundingBox3d bbox) {
    	this.buffer = buffer;
    	buffer.rewind();
    	this.shape = shape;
    	this.bbox = bbox;
    	this.shapeVBO = new CubeVBO(new CubeVBOBuilder(bbox));
    	this.min = min;
    	this.max = max;
    	this.mapper = mapper;
    }
	
	@Override
	public void mount(Painter painter) {
		GL gl = ((NativeDesktopPainter)painter).getGL();
		if (!mounted) {
			shapeVBO.mount(painter);
			shaderProgram = new GLSLProgram();
			ShaderFilePair sfp = new ShaderFilePair(this.getClass(), "volume.vert", "volume.frag");
			shaderProgram.loadAndCompileShaders(gl.getGL2(),sfp);
			shaderProgram.link(gl.getGL2());
			bind(gl);
			colormapTexure = new ColormapTexture(mapper,"transfer",shaderProgram.getProgramId());
			colormapTexure.bind(gl);
			mounted = true;
		}
	}
	
	public void setMin(Number min) {
		this.min = min.floatValue();
	}
	
	public void setMax(Number max) {
		this.max = max.floatValue();
	}

	@Override
	public boolean hasMountedOnce() {
		return mounted;
	}
	
	public void bind(final GL gl) throws GLException {
		gl.glEnable(GL2.GL_TEXTURE_3D);
        validateTexID(gl, true);
        gl.glBindTexture(GL2.GL_TEXTURE_3D, texID);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        
        setTextureData(gl,buffer,shape);
    }
	
	/**
	 * 
	 * @param gl
	 * @param buffer texture data
	 * @param shape the dimensions of 3d texture.
	 * @see https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glTexImage3D.xml
	 */
	public void setTextureData(final GL gl, Buffer buffer, int[] shape) {
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		gl.getGL2().glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL.GL_R32F, shape[2], shape[1], shape[0], 0, GL2.GL_RED, GL.GL_FLOAT, buffer);
	}
	
	private boolean validateTexID(final GL gl, final boolean throwException) {
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glEnable(GL2.GL_TEXTURE_3D);
		int id = gl.getGL2().glGetUniformLocation(shaderProgram.getProgramId(), "volumeTexture");
		
		if ( id >= 0) {
			texID = id;
		}
		
        return 0 != texID;
    }

	@Override
	public void draw(Painter painter, GL gl, GLU glu, Camera cam) {
		
		if (!mounted) {
			mount(painter);
		}
		
		colormapTexure.update(gl);
		
		doTransform(painter, cam);
    	
    	float mvmatrix[] = new float[16];
    	float projmatrix[] = new float[16];
    	
    	Coord3d eye = cam.getEye();
    	eye = eye.sub(cam.getTarget());
    	eye = eye.normalizeTo(1);
    	
    	gl.getGL2().glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
    	gl.getGL2().glGetFloatv(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
    	
    	float[] pmat = mvmatrix.clone();
    	
    	float[] success = FloatUtil.invertMatrix(mvmatrix, pmat);
    	
    	float[] eye1 = new float[] {eye.x,eye.y,eye.z,1};
    	
    	Coord3d range = bbox.getRange();
    	
    	float[] frange = new float[] {range.x,range.y,range.z,1};
    	
    	if (success != null) {
    		VectorUtil.normalizeVec3(frange);
    		success = success.clone();
//    		Arrays.fill(success, 0);
    		success[0] /= frange[0];
    		success[5] /= frange[1];
    		success[10] /= (1*frange[2]);
    		FloatUtil.multMatrixVec(success, eye1, eye1);
    		VectorUtil.normalizeVec3(eye1);
    	}
////    	
    	shaderProgram.bind(gl.getGL2());
    	shaderProgram.setUniform(gl.getGL2(), "eye", eye1,4);
    	shaderProgram.setUniform(gl.getGL2(), "minMax", new float[] {min,max},2);
    	int idt = gl.getGL2().glGetUniformLocation(shaderProgram.getProgramId(), "volumeTexture");
    	int idc = gl.getGL2().glGetUniformLocation(shaderProgram.getProgramId(), "transfer");
    	gl.getGL2().glUniform1i(idt, 0);
    	gl.getGL2().glUniform1i(idc, 1);
    	
    	
       gl.glEnable(GL2.GL_BLEND);
       gl.glEnable(GL2.GL_CULL_FACE);
       gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
       gl.getGL2().glPolygonMode(GL.GL_FRONT, GL2GL3.GL_FILL);
       gl.glCullFace(GL.GL_BACK);
		
       shapeVBO.draw(painter, gl, glu, cam);
       shaderProgram.unbind(gl.getGL2());
       
       if (disposed) {
    	   gl.glDeleteTextures(1, new int[] {texID}, 0);
    	   buffer = null;
    	   shaderProgram.destroy(gl.getGL2());
       }
         
	}

	@Override
	public void applyGeometryTransform(Transform transform) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dispose() {
		disposed = true;
		shapeVBO.dispose();
	}

	@Override
	public void updateBounds() {
		bbox = new BoundingBox3d(0,1,0,1,0,1);
		
	}

	@Override
	public ColorMapper getColorMapper() {
		return mapper;
	}

	@Override
	public void setColorMapper(ColorMapper mapper) {
		this.mapper = mapper;
		if (colormapTexure != null) colormapTexure.updateColormap(mapper);
	}
}
