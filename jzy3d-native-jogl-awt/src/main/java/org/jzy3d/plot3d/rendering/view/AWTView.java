package org.jzy3d.plot3d.rendering.view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.plot3d.primitives.Parallelepiped;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.tooltips.ITooltipRenderer;
import org.jzy3d.plot3d.rendering.tooltips.Tooltip;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.Overlay;

public class AWTView extends ChartView {
    
    public AWTView(IChartComponentFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        super(factory, scene, canvas, quality);
        this.bgViewport = new AWTImageViewport();
        this.renderers = new ArrayList<Renderer2d>(1);
        this.tooltips = new ArrayList<ITooltipRenderer>();
    }

    @Override
    public void dispose() {
        super.dispose();
        renderers.clear();
    }

    @Override
    protected void renderAxeBox(GL gl, GLU glu, IAxe axe, Scene scene, Camera camera, Coord3d scaling, boolean axeBoxDisplayed) {
        if (axeBoxDisplayed) {
            if (gl.isGL2()) {
                gl.getGL2().glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
            } else {
                GLES2CompatUtils.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
            }
            scene.getLightSet().disable(gl);

            axe.setScale(scaling);
            axe.draw(gl, glu, camera);
            if (DISPLAY_AXE_WHOLE_BOUNDS) { // for debug
                AxeBox abox = (AxeBox) axe;
                BoundingBox3d box = abox.getWholeBounds();
                Parallelepiped p = new Parallelepiped(box);
                p.setFaceDisplayed(false);
                p.setWireframeColor(Color.MAGENTA);
                p.setWireframeDisplayed(true);
                p.draw(gl, glu, camera);
            }

            scene.getLightSet().enableLightIfThereAreLights(gl);
        }
    }

    @Override
    protected void correctCameraPositionForIncludingTextLabels(GL gl, GLU glu, ViewportConfiguration viewport) {
        cam.setViewPort(viewport);
        cam.shoot(gl, glu, cameraMode);
        axe.draw(gl, glu, cam);
        clear(gl);

        //AxeBox abox = (AxeBox) axe;
        BoundingBox3d newBounds = axe.getWholeBounds().scale(scaling);

        if (viewmode == ViewPositionMode.TOP) {
            float radius = Math.max(newBounds.getXmax() - newBounds.getXmin(), newBounds.getYmax() - newBounds.getYmin()) / 2;
            radius += (radius * STRETCH_RATIO);
            cam.setRenderingSphereRadius(radius);
        } else
            cam.setRenderingSphereRadius((float) newBounds.getRadius());

        Coord3d target = newBounds.getCenter();
        Coord3d eye = viewpoint.cartesian().add(target);
        cam.setTarget(target);
        cam.setEye(eye);
    }

    /**
     * Renders all provided {@link Tooltip}s and {@link Renderer2d}s on top of
     * the scene.
     * 
     * Due to the behaviour of the {@link Overlay} implementation, Java2d
     * geometries must be drawn relative to the {@link Chart}'s
     * {@link IScreenCanvas}, BUT will then be stretched to fit in the
     * {@link Camera}'s viewport. This bug is very important to consider, since
     * the Camera's viewport may not occupy the full {@link IScreenCanvas}.
     * Indeed, when View is not maximized (like the default behaviour), the
     * viewport remains square and centered in the canvas, meaning the Overlay
     * won't cover the full canvas area.
     * 
     * In other words, the following piece of code draws a border around the
     * {@link View}, and not around the complete chart canvas, although queried
     * to occupy chart canvas dimensions:
     * 
     * g2d.drawRect(1, 1, chart.getCanvas().getRendererWidth()-2,
     * chart.getCanvas().getRendererHeight()-2);
     * 
     * {@link renderOverlay()} must be called while the OpenGL2 context for the
     * drawable is current, and after the OpenGL2 scene has been rendered.
     */
    @Override
    public void renderOverlay(GL gl, ViewportConfiguration viewport) {
        if (!hasOverlayStuffs())
            return;

        if (overlay == null)
            this.overlay = new Overlay(canvas.getDrawable());

        if (gl.isGL2()) {
            // TODO: don't know why needed to allow working with Overlay!!!????
            gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
        }

        gl.glViewport(viewport.x, viewport.y, viewport.width, viewport.height);

        if (overlay != null && viewport.width > 0 && viewport.height > 0) {
            
            try {
                if(canvas.getDrawable().getSurfaceWidth()>0 && canvas.getDrawable().getSurfaceHeight()>0){
                    //System.out.println("surf width=" + canvas.getDrawable().getSurfaceWidth());
                    //System.out.println("surf height=" + canvas.getDrawable().getSurfaceHeight());
                    Graphics2D g2d = overlay.createGraphics();

                    g2d.setBackground(bgOverlay);
                    g2d.clearRect(0, 0, canvas.getRendererWidth(), canvas.getRendererHeight());

                    // Tooltips
                    for (ITooltipRenderer t : tooltips)
                        t.render(g2d);

                    // Renderers
                    for (Renderer2d renderer : renderers)
                        renderer.paint(g2d, canvas.getRendererWidth(), canvas.getRendererHeight());

                    overlay.markDirty(0, 0, canvas.getRendererWidth(), canvas.getRendererHeight());
                    overlay.drawAll();
                    g2d.dispose();
                }
                
                
                

            } catch (Exception e) {
                LOGGER.error(e, e);
            }
        }
    }

    @Override
    public void renderBackground(GL gl, GLU glu, float left, float right) {
        if (bgImg != null) {
            bgViewport.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(), left, right);
            bgViewport.render(gl, glu);
        }
    }

    @Override
    public void renderBackground(GL gl, GLU glu, ViewportConfiguration viewport) {
        if (bgImg != null) {
            bgViewport.setViewPort(viewport);
            bgViewport.render(gl, glu);
        }
    }

    /** Set a buffered image, or null to desactivate background image */
    public void setBackgroundImage(BufferedImage i) {
        bgImg = i;
        bgViewport.setImage(bgImg, bgImg.getWidth(), bgImg.getHeight());
        bgViewport.setViewportMode(ViewportMode.STRETCH_TO_FILL);
        // when stretched, applyViewport() is cheaper to compute, and this does
        // not change
        // the picture rendering.
        // bgViewport.setScreenGridDisplayed(true);
    }

    public BufferedImage getBackgroundImage() {
        return bgImg;
    }

    public void clearTooltips() {
        tooltips.clear();
    }

    public void setTooltip(ITooltipRenderer tooltip) {
        tooltips.clear();
        tooltips.add(tooltip);
    }

    public void addTooltip(ITooltipRenderer tooltip) {
        tooltips.add(tooltip);
    }

    public void setTooltips(List<ITooltipRenderer> tooltip) {
        tooltips.clear();
        tooltips.addAll(tooltip);
    }

    public void addTooltips(List<ITooltipRenderer> tooltip) {
        tooltips.addAll(tooltip);
    }

    public List<ITooltipRenderer> getTooltips() {
        return tooltips;
    }

    public void addRenderer2d(Renderer2d renderer) {
        renderers.add(renderer);
    }

    public void removeRenderer2d(Renderer2d renderer) {
        renderers.remove(renderer);
    }

    protected boolean hasOverlayStuffs() {
        return tooltips.size() > 0 || renderers.size() > 0;
    }

    protected List<ITooltipRenderer> tooltips;
    protected List<Renderer2d> renderers;
    protected java.awt.Color bgOverlay = new java.awt.Color(0, 0, 0, 0);
    protected AWTImageViewport bgViewport;
    protected BufferedImage bgImg = null;
    protected Overlay overlay;
}
