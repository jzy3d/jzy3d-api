package org.jzy3d.plot3d.rendering.view;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.tooltips.ITooltipRenderer;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

public class AWTView extends ChartView {

  protected List<ITooltipRenderer> tooltips;
  protected List<AWTRenderer2d> renderers;
  protected AWTImageViewport backgroundViewport;
  protected BufferedImage backgroundImage = null;
  // protected java.awt.Color overlayBackground = new java.awt.Color(0, 0, 0, 0);

  public AWTView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    super(factory, scene, canvas, quality);
  }

  @Override
  public void initInstance(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    super.initInstance(factory, scene, canvas, quality);
    this.backgroundViewport = new AWTImageViewport();
    this.renderers = new ArrayList<AWTRenderer2d>(1);
    this.tooltips = new ArrayList<ITooltipRenderer>();
  }

  @Override
  public void dispose() {
    super.dispose();
    renderers.clear();
  }

  // DON T KNOW WHY THE HELL IT IS THERE. MOVED TO SUPER CLASS 
  /*@Override
  protected void renderAxeBox(IAxis axe, Scene scene, Camera camera, Coord3d scaling,
      boolean axeBoxDisplayed) {
    if (axeBoxDisplayed) {
      painter.glMatrixMode_ModelView();

      scene.getLightSet().disable(painter);

      axe.setScale(scaling);
      axe.draw(painter);
      
      if (displayAxisWholeBounds) { // for debug
        AxisBox abox = (AxisBox) axe;
        BoundingBox3d box = abox.getWholeBounds();
        Parallelepiped p = new Parallelepiped(box);
        p.setFaceDisplayed(false);
        p.setWireframeColor(Color.MAGENTA);
        p.setWireframeDisplayed(true);
        p.draw(painter);
      }

      scene.getLightSet().enableLightIfThereAreLights(painter);
    }
  }*/

  // MOVE TO SUPER CLASS THAT HAS EMPTY IMPLEMENTATION FOR FORGOTTEN REASON
  @Override
  protected void correctCameraPositionForIncludingTextLabels(IPainter painter,
      ViewportConfiguration viewport) {
    cam.setViewPort(viewport);
    cam.shoot(painter, cameraMode);
    axis.draw(painter);
    clear();

    // Base camera radius on {@link AxisBox#getWholeBounds} to ensure we display text labels
    // complete
    BoundingBox3d newBounds = axis.getWholeBounds().scale(scaling);

    if (viewMode == ViewPositionMode.TOP) {
      float radius = Math.max(newBounds.getXmax() - newBounds.getXmin(),
          newBounds.getYmax() - newBounds.getYmin()) / 2;
      radius += (radius * CAMERA_RENDERING_SPHERE_RADIUS_FACTOR_VIEW_ON_TOP);
      cam.setRenderingSphereRadius(radius);
    } else {
      cam.setRenderingSphereRadius(
          (float) newBounds.getRadius() * cameraRenderingSphereRadiusFactor);
    }

    Coord3d target = newBounds.getCenter();
    Coord3d eye = viewpoint.cartesian().add(target);
    cam.setPosition(eye, target);
  }

  @Override
  public void renderBackground(float left, float right) {
    if (backgroundImage != null) {
      backgroundViewport.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(), left,
          right);
      backgroundViewport.render(painter);
    }
  }

  @Override
  public void renderBackground(ViewportConfiguration viewport) {
    if (backgroundImage != null) {
      backgroundViewport.setViewPort(viewport);
      backgroundViewport.render(painter);
    }
  }

  /** Set a buffered image, or null to desactivate background image */
  public void setBackgroundImage(BufferedImage i) {
    backgroundImage = i;
    backgroundViewport.setImage(backgroundImage, backgroundImage.getWidth(),
        backgroundImage.getHeight());
    backgroundViewport.setViewportMode(ViewportMode.STRETCH_TO_FILL);
    // when stretched, applyViewport() is cheaper to compute, and this does
    // not change
    // the picture rendering.
    // bgViewport.setScreenGridDisplayed(true);
  }

  public BufferedImage getBackgroundImage() {
    return backgroundImage;
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

  public void addRenderer2d(AWTRenderer2d renderer) {
    renderers.add(renderer);
  }

  public void removeRenderer2d(AWTRenderer2d renderer) {
    renderers.remove(renderer);
  }

  public List<AWTRenderer2d> getRenderers2d() {
    return renderers;
  }

  public boolean hasOverlayStuffs() {
    return tooltips.size() > 0 || renderers.size() > 0;
  }
}
