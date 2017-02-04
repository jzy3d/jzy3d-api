package org.jzy3d.plot3d.rendering.shaders.mandelbrot;

public class MandelbrotSetting {
	private float x = -2;
	private float y = -2;
	private float height = 4;
	private float width = 4;
	private int iterations = 128;

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public void copyTo(MandelbrotSetting copy) {
		copy.x = x;
		copy.y = y;
		copy.height = height;
		copy.width = width;
		copy.iterations = iterations;
	}
}
