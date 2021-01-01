package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

public abstract class Geometry extends Wireframeable implements ISingleColorable, IMultiColorable {
	/**
	 * Initializes an empty {@link Geometry} with face status defaulting to true,
	 * and wireframe status defaulting to false.
	 */
	public Geometry() {
		super();
		points = new ArrayList<Point>(4);
		bbox = new BoundingBox3d();
		center = new Coord3d();
		polygonMode = PolygonMode.FRONT_AND_BACK;
	}

	/* * */

	@Override
	public void draw(IPainter painter) {
		doTransform(painter);

		if (mapper != null)
			mapper.preDraw(this);

		// Draw content of polygon
		drawFace(painter);

		// drawing order is important for EmulGL to cleanly render polygon edges

		// Draw edge of polygon
		drawWireframe(painter);

		if (mapper != null)
			mapper.postDraw(this);

		doDrawBoundsIfDisplayed(painter);
	}

	protected void drawFace(IPainter painter) {
		if (faceDisplayed) {
			painter.glPolygonMode(polygonMode, PolygonFill.FILL);

			if (wireframeDisplayed && polygonWireframeDepthTrick)
				applyDepthRangeForUnderlying(painter);

			if (wireframeDisplayed && polygonOffsetFillEnable)
				polygonOffseFillEnable(painter);

			callPointsForFace(painter);

			if (wireframeDisplayed && polygonOffsetFillEnable)
				polygonOffsetFillDisable(painter);
		}
	}

	protected void drawWireframe(IPainter painter) {
		if (wireframeDisplayed) {
			painter.glPolygonMode(polygonMode, PolygonFill.LINE);

			if (polygonWireframeDepthTrick)
				applyDepthRangeForOverlying(painter);

			if (polygonOffsetFillEnable)
				polygonOffseFillEnable(painter);

			callPointForWireframe(painter);

			if (polygonOffsetFillEnable)
				polygonOffsetFillDisable(painter);
		}
	}

	/**
	 * Drawing the point list in wireframe mode
	 */
	protected void callPointForWireframe(IPainter painter) {
		painter.color(wireframeColor);
		painter.glLineWidth(getWireframeWidth());
		painter.glBegin_LineLoop(); // changed for JGL as wireframe polygon are transformed to pair of triangles

		for (Point p : points) {
			painter.vertex(p.xyz, spaceTransformer);
		}
		painter.glEnd();
	}

	/**
	 * Drawing the point list in face mode (polygon content)
	 */
	protected void callPointsForFace(IPainter painter) {
		begin(painter);
		for (Point p : points) {
			if (mapper != null) {
				Color c = mapper.getColor(p.xyz);
				painter.color(c);
			} else {
				painter.color(p.rgb);
			}
			painter.vertex(p.xyz, spaceTransformer);
		}
		painter.glEnd();
	}

	/**
	 * Invoke GL begin with the actual geometry type {@link GL#GL_POINTS},
	 * {@link GL#GL_LINES}, {@link GL#GL_TRIANGLES}, {@link GL2#GL_POLYGON} ...
	 */
	protected abstract void begin(IPainter painter);

	/* DATA */

	public void add(float x, float y, float z) {
		add(new Coord3d(x, y, z));
	}

	public void add(Coord3d coord) {
		add(new Point(coord, wireframeColor), true);
	}

	public void add(Point point) {
		add(point, true);
	}

	/** Add a point to the polygon. */
	public void add(Point point, boolean updateBounds) {
		points.add(point);
		if (updateBounds) {
			updateBounds();
		}
	}

	@Override
	public void applyGeometryTransform(Transform transform) {
		for (Point p : points) {
			p.xyz = transform.compute(p.xyz);
		}
		updateBounds();
	}

	@Override
	public void updateBounds() {
		bbox.reset();
		bbox.add(getPoints());

		// recompute center
		center = new Coord3d();
		for (Point p : points)
			center = center.add(p.xyz);
		center = center.div(points.size());
	}

	@Override
	public Coord3d getBarycentre() {
		return center;
	}

	public Point get(int p) {
		return points.get(p);
	}

	public List<Point> getPoints() {
		return points;
	}

	public int size() {
		return points.size();
	}

	/* DISTANCES */

	@Override
	public double getDistance(Camera camera) {
		return getBarycentre().distance(camera.getEye());
	}

	@Override
	public double getShortestDistance(Camera camera) {
		double min = Float.MAX_VALUE;
		double dist = 0;
		for (Point point : points) {
			dist = point.getDistance(camera);
			if (dist < min)
				min = dist;
		}

		dist = getBarycentre().distance(camera.getEye());
		if (dist < min)
			min = dist;
		return min;
	}

	@Override
	public double getLongestDistance(Camera camera) {
		double max = 0;
		double dist = 0;
		for (Point point : points) {
			dist = point.getDistance(camera);
			if (dist > max)
				max = dist;
		}
		return max;
	}

	/* SETTINGS */

	public PolygonMode getPolygonMode() {
		return polygonMode;
	}

	/**
	 * A null polygonMode imply no any call to gl.glPolygonMode(...) at rendering
	 */
	public void setPolygonMode(PolygonMode polygonMode) {
		this.polygonMode = polygonMode;
	}

	/* COLOR */

	@Override
	public void setColorMapper(ColorMapper mapper) {
		this.mapper = mapper;

		fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
	}

	@Override
	public ColorMapper getColorMapper() {
		return mapper;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;

		for (Point p : points)
			p.setColor(color);

		fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public String toString(int depth) {
		return (Utils.blanks(depth) + "(" + this.getClass().getSimpleName() + ") #points:" + points.size());
	}

	/* */

	protected PolygonMode polygonMode;
	protected ColorMapper mapper;
	protected List<Point> points;
	protected Color color;
	protected Coord3d center;
}
