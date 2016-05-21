package org.jzy3d.picking;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.pickable.Pickable;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.transform.Scale;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

/**
 * @see: http://www.opengl.org/resources/faq/technical/selection.htm
 * 
 * @author Martin Pernollet
 *
 */
public class PickingSupport {
    public static int BRUSH_SIZE = 10;
    public static int BUFFER_SIZE = 2048;
    
	public PickingSupport(){
		this(BRUSH_SIZE);
	}
	
	public PickingSupport(int brushSize){
		this(brushSize, BUFFER_SIZE);
	}
	
	public PickingSupport(int brushSize, int bufferSize){
		this.brushSize = brushSize;
		this.bufferSize = bufferSize;
	}

	/*************************/

	public boolean addObjectPickedListener(IObjectPickedListener listener){
		return verticesListener.add(listener);
	}
	
	public boolean removeObjectPickedListener(IObjectPickedListener listener){
		return verticesListener.remove(listener);
	}

	protected void fireObjectPicked(List<? extends Object> v){
		for(IObjectPickedListener listener: verticesListener){
			listener.objectPicked(v, this);
		}
	}
	
	/*************************/
	
	public void registerDrawableObject(AbstractDrawable drawable, Object model){
        if(drawable instanceof Pickable){
            registerPickableObject((Pickable)drawable, model);
        }
    }
	
	public synchronized void registerPickableObject(Pickable pickable, Object model){
		pickable.setPickingId(pickId++);
		pickables.put(pickable.getPickingId(), pickable);
		pickableTargets.put(pickable, model);
	}
	
	public synchronized void getPickableObject(int id){
		pickables.get(id);
	}
	
	/*************************/
	
	protected TicToc perf = new TicToc();
	
	public void pickObjects(GL gl, GLU glu, View view, Graph graph, IntegerCoord2d pickPoint) {
	    perf.tic();
	    
        int viewport[] = new int[4];
        int selectBuf[] = new int[bufferSize]; // TODO: move @ construction
        IntBuffer selectBuffer = Buffers.newDirectIntBuffer(bufferSize);
        
        
        if (!gl.isGL2()) throw new UnsupportedOperationException();
        
        // Prepare selection data
        gl.getGL2().glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);        
        gl.getGL2().glSelectBuffer(bufferSize, selectBuffer);        
        gl.getGL2().glRenderMode(GL2.GL_SELECT);         
        gl.getGL2().glInitNames();
        gl.getGL2().glPushName(0); 

        // Retrieve view settings
        Camera camera = view.getCamera();
        CameraMode cMode = view.getCameraMode();
        Coord3d viewScaling = view.getLastViewScaling();
        Transform viewTransform = new Transform(new Scale(viewScaling));
        double xpick = pickPoint.x;
        double ypick = pickPoint.y;
        
        // Setup projection matrix
        gl.getGL2().glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.getGL2().glPushMatrix();
        {
        	gl.getGL2().glLoadIdentity();
	        // Setup picking matrix, and update view frustrum
	        glu.gluPickMatrix(xpick, ypick, brushSize, brushSize, viewport, 0);
	        camera.doShoot(gl, glu, cMode);
	        
	        // Draw each pickable element in select buffer
	        gl.getGL2().glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	        
	        synchronized(this){
    	        for(Pickable pickable: pickables.values()){
    	        	setCurrentName(gl, pickable);
    	        	pickable.setTransform(viewTransform);
    	        	pickable.draw(gl, glu, camera);
    	        	releaseCurrentName(gl);
    	        }
	        }
	        // Back to projection matrix
	        gl.getGL2().glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        }
        gl.getGL2().glPopMatrix();
        gl.glFlush();
        
        // Process hits
        int hits = gl.getGL2().glRenderMode(GL2.GL_RENDER);
        selectBuffer.get(selectBuf);
        List<Pickable> picked = processHits(hits, selectBuf);
        
        // Trigger an event
        List<Object> clickedObjects = new ArrayList<Object>(hits);
        for(Pickable pickable: picked){
        	Object vertex = pickableTargets.get(pickable);
        	clickedObjects.add(vertex);
        }
        perf.toc();
        
        fireObjectPicked(clickedObjects);
    }
	
	public double getLastPickPerfMs(){
	    return perf.elapsedMilisecond();
	}
	
    protected void setCurrentName(GL gl, Pickable pickable){
    	if(method==0)
    		gl.getGL2().glLoadName(pickable.getPickingId());
    	else
    		gl.getGL2().glPushName(pickable.getPickingId());
    } 
    
    protected void releaseCurrentName(GL gl){
    	if(method==0)
    		;
    	else
    		gl.getGL2().glPopName();
    }
    
    protected static int method = 0;
    
    /*********************/
    
    /** Provides the number of picked object by a click. */
    @SuppressWarnings("unused")
	protected List<Pickable> processHits(int hits, int buffer[]) {
        int names, ptr = 0;
        int z1, z2=0;
        
        List<Pickable> picked = new ArrayList<Pickable>();
        
        for (int i = 0; i < hits; i++) { 
            names = buffer[ptr]; ptr++;
            z1 = buffer[ptr]; ptr++;
            z2 = buffer[ptr]; ptr++;

            for (int j = 0; j < names; j++) {
            	int idj = buffer[ptr]; ptr++;
            	if( ! pickables.containsKey(idj) )
            		throw new RuntimeException("internal error: pickable id not found in registry!");
                picked.add( pickables.get(idj) );
            }
        }
        return picked;
    }

  	public synchronized void unRegisterAllPickableObjects(){
  		pickables.clear();
  		pickableTargets.clear();
  	}    
    
    /*********************/

    protected static int pickId = 0;
	protected Map<Integer, Pickable> pickables = new HashMap<Integer, Pickable>();
	protected List<IObjectPickedListener> verticesListener = new ArrayList<IObjectPickedListener>(1);
	protected Map<Pickable, Object> pickableTargets = new HashMap<Pickable, Object>();
	protected int brushSize;
	protected int bufferSize;
}
