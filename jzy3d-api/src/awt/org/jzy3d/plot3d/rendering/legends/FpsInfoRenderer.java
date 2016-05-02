package org.jzy3d.plot3d.rendering.legends;

import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

import com.jogamp.opengl.GLAnimatorControl;

public class FpsInfoRenderer implements Renderer2d{
    protected Chart chart;
    protected GLAnimatorControl control;
    
    public FpsInfoRenderer(Chart chart){
        this.control = ((IScreenCanvas)chart.getCanvas()).getAnimator();
        ((AWTChart)chart).addRenderer(this);
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(java.awt.Color.BLACK);
        g2d.drawString(control.getLastFPS()+" FPS", 50, 50);
    }
}
