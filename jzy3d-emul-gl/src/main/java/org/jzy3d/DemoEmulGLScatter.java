package org.jzy3d;

import java.util.Random;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

public class DemoEmulGLScatter {
	public static void main(String[] args) throws Exception {

		int size = 50000;
		float x;
		float y;
		float z;
		float a;

		Coord3d[] points = new Coord3d[size];
		Color[] colors = new Color[size];

		Random r = new Random();
		r.setSeed(0);

		for (int i = 0; i < size; i++) {
			x = r.nextFloat() - 0.5f;
			y = r.nextFloat() - 0.5f;
			z = r.nextFloat() - 0.5f;
			points[i] = new Coord3d(x, y, z);
			a = 0.75f;
			colors[i] = new Color(x, y, z, a);
		}

		Scatter scatter = new Scatter(points, colors);
		scatter.setWidth(3);

		// --------------------------------
		Quality q = Quality.Advanced;

		Chart chart = new EmulGLChartFactory().newChart(q);
		chart.getView().setBoundMode(ViewBoundMode.AUTO_FIT); // INVESTIGUER POURQUOI AUTO_FIT!!!
		chart.getScene().add(scatter);
		chart.open();

		// --------------------------------
		CameraThreadController rotation = new CameraThreadController(chart);
		rotation.setStep(0.025f);
		rotation.setUpdateViewDefault(true);

		AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();
		mouse.addSlaveThreadController(rotation);

		boolean fixWithAnimator = true;
		if (fixWithAnimator) {
			rotation.setUpdateViewDefault(true);
			mouse.setUpdateViewDefault(false); // keep to false otherwise double rendering
			((EmulGLCanvas) chart.getCanvas()).startAnimator();
		} else {

		}
	}
}
