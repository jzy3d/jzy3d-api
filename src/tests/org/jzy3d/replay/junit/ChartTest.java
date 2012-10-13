package org.jzy3d.replay.junit;

import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jzy3d.chart.Chart;

/**
 * Primitives for chart tests.
 * 
 * @author martin
 */
public class ChartTest {
	/*
	 * In Java, a mouse click only registers if the mouse is pressed and
	 * released without moving the mouse at all. This is difficult for most
	 * users to accomplish, so most UI elements (like buttons) react to the
	 * mouse press and release events and ignore the "click".
	 */

	/**
	 * If test case image does not exist, build it for the first time.
	 * 
	 * Failure to compare the chart with the test case image will create an
	 * image <code>data/tests/error-[name].png</code>. This image is always
	 * deleted before running a testcase.
	 * 
	 * Calling clean() will delete the test case image.
	 * 
	 * @param chart
	 * @throws IOException
	 *             if a non chart related error occurs. Actual chart test errors
	 *             call <code>fail(...)</code>
	 */
	public void execute(Chart chart) throws IOException {
		clean(new File(getTestCaseFailedFileName()));

		if (!isBuilt())
			build(chart);
		test(chart);
	}

	public void clean() {
		if (!clean(getTestCaseFile()) && isBuilt())
			Logger.getLogger(ChartTest.class).warn(
					"test case file not cleaned: " + getTestCaseFileName());
	}

	public boolean clean(File file) {
		return file.delete();
	}

	public void build(Chart chart) throws IOException {
		screenshot(chart, getTestCaseFileName());
	}

	public boolean isBuilt() {
		return getTestCaseFile().exists();
	}

	public void test(Chart chart) throws IOException {
		try {
			compare(chart, getTestCaseFileName());
		} catch (IOException e) {
			fail("IOException: " + e.getMessage());
		} catch (ChartTestFailed e) {
			screenshot(chart, getTestCaseFailedFileName());
			fail("Chart test failed: " + e.getMessage() + " see "
					+ getTestCaseFailedFileName());
		}
	}

	/* */

	public void compare(Chart chart, String filename) throws IOException,
			ChartTestFailed {
		BufferedImage i = chart.screenshot();

		if (i != null) {
			BufferedImage i2 = loadTestCaseImage();
			compare(i, i2);
		} else {
			throw new RuntimeException("screenshot not available");
		}
	}

	private BufferedImage loadTestCaseImage() throws IOException {
		return ImageIO.read(getTestCaseFile());
	}

	public void compare(BufferedImage i1, BufferedImage i2)
			throws ChartTestFailed {
		// int rbg = image.getRGB((int) x, (maxRow) - ((int) y));

		int i1W = i1.getWidth();
		int i1H = i1.getHeight();
		int i2W = i2.getWidth();
		int i2H = i2.getHeight();

		if (i1W == i2W && i1H == i2H) {
			for (int i = 0; i < i1W; i++) {
				for (int j = 0; j < i1H; j++) {
					int p1rgb = i1.getRGB(i, j);
					int p2rgb = i2.getRGB(i, j);
					if (p1rgb != p2rgb) {
						String m = "pixel diff @(" + i + "," + j + ")";
						throw new ChartTestFailed(m, i1, i2);
					}
				}
			}
		} else {
			String m = "image size differ: i1={" + i1W + "," + i1H + "} i2={"
					+ i2W + "," + i2H + "}";
			throw new ChartTestFailed(m);
		}
	}
	
	

	/* */

	public void screenshot(Chart chart, String filename) throws IOException {
		File output = new File(filename);
		if (!output.getParentFile().exists())
			output.mkdirs();
		BufferedImage i = chart.screenshot();

		if (i != null) {
			ImageIO.write(i, "png", output);
			Logger.getLogger(ChartTest.class).info(
					"Initialize test " + getTestCaseFileName());
		} else {
			throw new RuntimeException("screenshot not available");
		}
	}

	/* */

	public File getTestCaseFile() {
		return new File(getTestCaseFileName());
	}

	public String getTestCaseFileName() {
		return getTestCaseFolder() + getTestName() + ".png";
	}

	public String getTestCaseFailedFileName() {
		return getTestCaseFolder() + "error-" + getTestName() + ".png";
	}

	public String getTestCaseFolder() {
		return "data/tests/";
	}

	public String getTestName() {
		return this.getClass().getSimpleName();
	}

	public String getTestCanvasType() {
		return "offscreen, " + WIDTH + ", " + HEIGHT;
	}

	/* */

	// int bufImgType = BufferedImage.TYPE_3BYTE_BGR;// );
	int WIDTH = 800;
	int HEIGHT = 600;
}
