package org.jzy3d.chart.controllers.keyboard.camera;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jzy3d.chart.Chart;

/** Saves a screenshot in PNG format once key S is pressed. 
 * 
 */
public class ScreenshotKeyListener implements KeyListener {
    protected Chart chart;
    protected String outputFile;
    protected List<IScreenshotEventListener> listeners = new ArrayList<IScreenshotEventListener>(1);

    
    public ScreenshotKeyListener(Chart chart, String outputFile) {
        super();
        this.chart = chart;
        this.outputFile = outputFile;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
        case 's':
            try {
                screenshot(chart, outputFile);
                fireDone(outputFile);
            } catch (IOException e1) {
                fireError(outputFile, e1);
            }
        default:
            break;
        }
    }
    
    public void screenshot(Chart chart, String filename) throws IOException {
        File output = new File(filename);
        if (!output.getParentFile().exists())
            output.mkdirs();
        ImageIO.write(chart.screenshot(), "png", output);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    protected void fireDone(String file){
        for(IScreenshotEventListener listener: listeners){
            listener.doneScreenshot(file);
        }
    }
    
    public void addListener(IScreenshotEventListener listener){
        listeners.add(listener);
    }

    protected void fireError(String file, Exception e){
        for(IScreenshotEventListener listener: listeners){
            listener.failedScreenshot(file, e);
        }
    }

    
    public interface IScreenshotEventListener{
        public void doneScreenshot(String file);
        public void failedScreenshot(String file, Exception e);
    }
}
