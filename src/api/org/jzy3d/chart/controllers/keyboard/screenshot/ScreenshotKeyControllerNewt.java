package org.jzy3d.chart.controllers.keyboard.screenshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.AbstractController;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewt;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

/** Saves a screenshot in PNG format once key S is pressed. 
 * 
 */
public class ScreenshotKeyControllerNewt extends AbstractController implements KeyListener, IScreenshotKeyController {
    protected Chart chart;
    protected String outputFile;
    protected List<IScreenshotEventListener> listeners = new ArrayList<IScreenshotEventListener>(1);

    
    public ScreenshotKeyControllerNewt(Chart chart, String outputFile) {
        super();
        register(chart);
        this.chart = chart;
        this.outputFile = outputFile;
    }
    
    public void register(Chart chart){
		super.register(chart);
		ICanvas c = chart.getCanvas();
		if(c instanceof CanvasNewt){
			CanvasNewt cnt = (CanvasNewt)c;
			cnt.getWindow().addKeyListener(this);
		}
		else{
			throw new IllegalArgumentException("Using this camera key controller requires a CanvasNewt. Having: " + c.getClass().getSimpleName());
		}
	}
	
	public void dispose(){
		for(Chart c: targets){
			ICanvas ca = c.getCanvas();
			if(ca instanceof CanvasNewt){
				CanvasNewt cnt = (CanvasNewt)ca;
				cnt.getWindow().removeKeyListener(this);
			}
		}
		
		super.dispose(); // i.e. target=null
	}
    
    @Override
	public void screenshot(Chart chart, String filename) throws IOException {
        File output = new File(filename);
        if (!output.getParentFile().exists())
            output.mkdirs();
        ImageIO.write(chart.screenshot(), "png", output);
    }
    
    @Override
	public void addListener(IScreenshotEventListener listener){
        listeners.add(listener);
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
    
    
    protected void fireError(String file, Exception e){
        for(IScreenshotEventListener listener: listeners){
            listener.failedScreenshot(file, e);
        }
    }
}
