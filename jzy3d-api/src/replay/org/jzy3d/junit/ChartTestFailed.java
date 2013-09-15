package org.jzy3d.junit;
import java.awt.image.BufferedImage;


public class ChartTestFailed extends Exception{
	public ChartTestFailed() {
		super();
	}

	public ChartTestFailed(String message, Throwable cause) {
		super(message, cause);
	}

	public ChartTestFailed(String message) {
		super(message);
	}

	public ChartTestFailed(Throwable cause) {
		super(cause);
	}
	
	public ChartTestFailed(String message, BufferedImage i1, BufferedImage i2) {
		super(message);
		this.i1 = i1;
		this.i2 = i2;
	}

	public BufferedImage getImage1() {
		return i1;
	}

	public BufferedImage getImage2() {
		return i2;
	}

	protected BufferedImage i1;
	protected BufferedImage i2;
}

