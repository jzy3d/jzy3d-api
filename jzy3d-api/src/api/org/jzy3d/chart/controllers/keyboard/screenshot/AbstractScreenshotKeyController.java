package org.jzy3d.chart.controllers.keyboard.screenshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.AbstractController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.maths.Utils;

import com.jogamp.opengl.util.texture.TextureIO;

public class AbstractScreenshotKeyController extends AbstractController implements IScreenshotKeyController {
	protected Chart chart;
	protected String outputFile;
	protected List<IScreenshotEventListener> listeners = new ArrayList<IScreenshotEventListener>(1);
    
	public AbstractScreenshotKeyController(Chart chart, String outputFile) {
		super(chart);
		register(chart);
		this.chart = chart;
		this.outputFile = outputFile;
	}

	@Override
	public void register(Chart chart) {
		super.register(chart);
		chart.getCanvas().addKeyController(this);
	}

	@Override
	public void dispose() {
		for (Chart c : targets) {
			c.getCanvas().removeKeyController(this);
		}

		super.dispose(); // i.e. target=null
	}

	@Override
	public void screenshot(Chart chart, String filename) throws IOException {
		File output = new File(filename);
		if (!output.getParentFile().exists())
			output.mkdirs();
		TextureIO.write(chart.screenshot(), new File(filename));
	}

	@Override
	public void screenshot(Chart chart) throws IOException {
		String fileName = AWTChartComponentFactory.SCREENSHOT_FOLDER + "/capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
		this.outputFile = fileName;
		File output = new File(fileName);
		if (!output.getParentFile().exists())
			output.mkdirs();
		TextureIO.write(chart.screenshot(), new File(fileName));
	}

	@Override
	public void addListener(IScreenshotEventListener listener) {
		listeners.add(listener);
	}

	protected void fireDone(String file) {
		for (IScreenshotEventListener listener : listeners) {
			listener.doneScreenshot(file);
		}
	}

	protected void fireError(String file, Exception e) {
		for (IScreenshotEventListener listener : listeners) {
			listener.failedScreenshot(file, e);
		}
    }

    @Override
    public String getFilename() {
        return outputFile;
    }

    @Override
    public void setFilename(String filename) {
        outputFile = filename;
	}

}