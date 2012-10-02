package org.jzy3d.chart.controllers.mouse.camera;

import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;

public interface ICameraMouseController {
	public void addSlaveThreadController(CameraThreadController thread);
}
