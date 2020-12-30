package org.jzy3d.chart;

import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;

public class EmulGLAnimator implements IAnimator{
	private static final int RENDERING_LOOP_PAUSE = 100;
	protected EmulGLCanvas canvas;
	protected Thread t;
	
	

	public EmulGLAnimator(EmulGLCanvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void start() {
		stop();
		
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					canvas.doDisplay();
					
					try {
						Thread.sleep(RENDERING_LOOP_PAUSE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}, "EmulGLAnimator");
		t.run();
	}

	@Override
	public void stop() {
		if(t!=null) {
			t.interrupt();
		}
	}
}
