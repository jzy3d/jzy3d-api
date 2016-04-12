package org.jzy3d.plot3d.rendering.canvas;

import java.io.File;
import java.io.IOException;

import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.util.texture.TextureData;

public class VoidCanvas implements ICanvas{
    public VoidCanvas(IChartComponentFactory factory, Scene scene, Quality quality) {
        view = scene.newView(this, quality);
        renderer = factory.newRenderer(view);
    }
    
    @Override
    public View getView() {
        return view;
    }

    @Override
    public GLDrawable getDrawable() {
        return null;
    }

    @Override
    public int getRendererWidth() {
        return 0;
    }

    @Override
    public int getRendererHeight() {
        return 0;
    }

    @Override
    public Renderer3d getRenderer() {
        return renderer;
    }

    @Override
    public void forceRepaint() {
    }

    @Override
    public TextureData screenshot() {
        throw new RuntimeException("Unsupported: " + INFO);
    }

    @Override
    public TextureData screenshot(File file) throws IOException {
        throw new RuntimeException("Unsupported: " + INFO);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void addMouseController(Object o)  {}

    @Override
    public void addKeyController(Object o)  {}

    @Override
    public void removeMouseController(Object o)  {}

    @Override
    public void removeKeyController(Object o) {}

    @Override
    public String getDebugInfo() {
        return null;
    }
    
    protected View view;
    protected Renderer3d renderer;

    protected static String INFO = VoidCanvas.class + " is not able to render anything and simply let you use Jzy3d geometry model";

    @Override
    public void setPixelScale(float[] scale) {
        
    }
}
