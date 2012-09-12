package org.jzy3d.plot3d.rendering.view;

/**
 * A {@link ViewPort} states how a particular GL rendering should
 * occupy screen. It is define by integer width and height, and
 * support a left
 * @author Martin
 *
 */
public class ViewPort{
	public ViewPort(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public ViewPort(int width, int height, int x, int y) {
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
	
	public static ViewPort slice(int width, int height, float left, float right){
		int thiswidth  = (int)((right-left)*(float)width);
		int thisheight = height;
		int thisx      = (int)(left*width);
		int thisy      = thisx + thiswidth;
		return new ViewPort(thiswidth, thisheight, thisx, thisy);
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public String toString(){
		return "(ViewPort) width=" + width + " height=" + height + " x=" + x + " y=" + y;
	}
	
	protected int width;
	protected int height;
	protected int x;
	protected int y;
}
