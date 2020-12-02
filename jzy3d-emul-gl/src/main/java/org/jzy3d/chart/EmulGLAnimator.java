package org.jzy3d.chart;

import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;

public class EmulGLAnimator implements Animator{
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
					
					//Thread.yield();
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		t.run();
	}

	@Override
	public void stop() {
		if(t!=null) {
			t.interrupt();
		}
	}
	

}
