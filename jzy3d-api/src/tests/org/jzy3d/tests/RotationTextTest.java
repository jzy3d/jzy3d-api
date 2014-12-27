package org.jzy3d.tests;

import java.util.ArrayList;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.pickable.PickablePoint;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.drawable.DrawableTextBitmap;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Transform;

public class RotationTextTest extends AbstractAnalysis {
	//https://github.com/zhivko/jzy3d-api.git
	public static RotationTextTest instance;
	public MyComposite myComposite;
	public ArrayList<PickablePoint> points;

	public static void main(String[] args) throws Exception {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		instance = new RotationTextTest();
		AnalysisLauncher.open(instance);
		CanvasAWT canvas = (CanvasAWT) instance.chart.getCanvas();
		canvas.setSize(600, 600);
		instance.chart.getView().setViewPositionMode(ViewPositionMode.FREE);

		for (int i = 0; i < -1; i++) {
			double angle = 4;
			Rotate rotate = new Rotate(angle, new Coord3d(0, 0, 1));
			Transform t = new Transform();
			t.add(rotate);
			instance.myComposite.applyGeometryTransform(t);
			instance.chart.render();
			Thread.currentThread();
            Thread.sleep(300);
			//File f = new File("c:\\temp\\pic_" + i + ".jpg");
//			try {
////				instance.chart.screenshot(f);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	
	
	}

	@Override
	public void init() {

		System.out.println("Creating chart! Thread: " + Thread.currentThread().getName());
		myComposite = new MyComposite();
		points = new ArrayList<PickablePoint>();
		chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());

		CanvasAWT canvas = (CanvasAWT) chart.getCanvas();
		// canvas.getAnimator().start();
		System.out.println("Animator started?: " + canvas.getAnimator().isStarted());
		PickablePoint mp1 = new PickablePoint(new Coord3d(0, 2, 2), Color.GRAY, 5);
		PickablePoint mp2 = new PickablePoint(new Coord3d(2, 2, 2), Color.GRAY, 5);
		PickablePoint mp3 = new PickablePoint(new Coord3d(1, 5, 2), Color.GRAY, 5);
		PickablePoint mp4 = new PickablePoint(new Coord3d(1, 3, 3), Color.GRAY, 5);
		points.add(mp1);
		points.add(mp2);
		points.add(mp3);
		points.add(mp4);

		LineStrip ls = new LineStrip();
		ls.setWireframeColor(Color.GRAY);
		ls.add(mp1);
		ls.add(mp2);
		ls.add(mp3);
		ls.add(mp4);
		ls.add(mp2);
		ls.add(mp4);
		ls.add(mp1);
		ls.setWireframeWidth(6);
		ls.setWidth(6);

		float distance = (float) mp2.xyz.distance(mp1.xyz);
		Coord3d delta = mp2.xyz.sub(mp1.xyz).normalizeTo((float) (distance * 0.5));
		Coord3d textPos = mp1.xyz.add(delta);

		DrawableTextBitmap t4 = new DrawableTextBitmap("edge", textPos, Color.BLACK);
		t4.setHalign(Halign.LEFT); // TODO: invert
		myComposite.add(t4);
		myComposite.add(ls);
		addAxis();

		chart.getScene().getGraph().add(myComposite,true);

	}

	public void addAxis() {
		LineStrip yAxis = new LineStrip();
		yAxis.setWireframeColor(Color.GREEN);
		yAxis.add(new Point(new Coord3d(0, 0, 0)));
		yAxis.add(new Point(new Coord3d(0, 1, 0)));

		LineStrip xAxis = new LineStrip();
		xAxis.setWireframeColor(Color.BLUE);
		xAxis.add(new Point(new Coord3d(0, 0, 0)));
		xAxis.add(new Point(new Coord3d(0.3, 0, 0)));

		LineStrip zAxis = new LineStrip();
		zAxis.setWireframeColor(Color.RED);
		zAxis.add(new Point(new Coord3d(0, 0, 0)));
		zAxis.add(new Point(new Coord3d(0, 0, 0.3)));
		myComposite.add(xAxis);
		myComposite.add(zAxis);
		myComposite.add(yAxis);

	}

}