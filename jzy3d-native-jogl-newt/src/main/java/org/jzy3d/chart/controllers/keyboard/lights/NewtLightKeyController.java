package org.jzy3d.chart.controllers.keyboard.lights;

import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.lights.Light;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class NewtLightKeyController implements KeyListener, ILightKeyController{
    
    public NewtLightKeyController(Chart chart) {
        super();
        this.chart = chart;
    }
    
    public NewtLightKeyController(Chart chart, int lightId) {
        super();
        this.chart = chart;
        this.lightId = lightId;
    }

    public NewtLightKeyController(Chart chart, Light light) {
        super();
        this.chart = chart;
        this.lightId = light.getId();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        switch( e.getKeyChar()){
        case KeyEvent.VK_2: chart.getScene().getLightSet().get(lightId).getPosition().x -= 10; chart.render(); break;
        case KeyEvent.VK_8: chart.getScene().getLightSet().get(lightId).getPosition().x += 10; chart.render(); break;
        case KeyEvent.VK_4: chart.getScene().getLightSet().get(lightId).getPosition().y -= 10; chart.render(); break;
        case KeyEvent.VK_6: chart.getScene().getLightSet().get(lightId).getPosition().y += 10; chart.render(); break;
        case KeyEvent.VK_9: chart.getScene().getLightSet().get(lightId).getPosition().z += 10; chart.render(); break;
        case KeyEvent.VK_7: chart.getScene().getLightSet().get(lightId).getPosition().z -= 10; chart.render(); break;
        
        //KeyEvent.;
        default: break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {}
    
    protected Chart chart;
    protected int lightId = 0;

}
