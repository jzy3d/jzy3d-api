package org.jzy3d.plot3d.primitives.axes.layout;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.axes.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;


public interface IAxeLayout {
	public void setMainColor(Color color);
	public Color getMainColor();

	//public void setTickDisplayed(boolean status);
	//public boolean isTickDisplayed();
	public Color getGridColor();
	public void setGridColor(Color gridColor);

	// not for axebase
	public void setFaceDisplayed(boolean status);
	public boolean isFaceDisplayed();
	public Color getQuadColor();
	public void setQuadColor(Color quadColor);
	
	public void setXAxeLabel(String label);
	public void setYAxeLabel(String label);
	public void setZAxeLabel(String label);
	public String getXAxeLabel();
	public String getYAxeLabel();
	public String getZAxeLabel();

	public void setXAxeLabelDisplayed(boolean axeLabelDisplayed);
	public void setYAxeLabelDisplayed(boolean axeLabelDisplayed);
	public void setZAxeLabelDisplayed(boolean axeLabelDisplayed);
	public boolean isXAxeLabelDisplayed();
	public boolean isYAxeLabelDisplayed();
	public boolean isZAxeLabelDisplayed();
	
	public void setXTickLabelDisplayed(boolean tickLabelDisplayed);
	public void setYTickLabelDisplayed(boolean tickLabelDisplayed);
	public void setZTickLabelDisplayed(boolean tickLabelDisplayed);
	public boolean isXTickLabelDisplayed();
	public boolean isYTickLabelDisplayed();
	public boolean isZTickLabelDisplayed();
	
	public void setXTickProvider(ITickProvider provider);
	public void setYTickProvider(ITickProvider provider);
	public void setZTickProvider(ITickProvider provider);
	public ITickProvider getXTickProvider();
	public ITickProvider getYTickProvider();
	public ITickProvider getZTickProvider();
	
	public void setXTickRenderer(ITickRenderer renderer);
	public void setYTickRenderer(ITickRenderer renderer);
	public void setZTickRenderer(ITickRenderer renderer);
	public ITickRenderer getXTickRenderer();
	public ITickRenderer getYTickRenderer();
	public ITickRenderer getZTickRenderer();
	
	/*public void updateXTicks(float min, float max);
	public void updateYTicks(float min, float max);
	public void updateZTicks(float min, float max);*/
	
	public double[] getXTicks(double min, double max);
	public double[] getYTicks(double min, double max);
	public double[] getZTicks(double min, double max);
	public double[] getXTicks();
	public double[] getYTicks();
	public double[] getZTicks();
	
	public void setXTickColor(Color color);
	public void setYTickColor(Color color);
	public void setZTickColor(Color color);
	public Color getXTickColor();
	public Color getYTickColor();
	public Color getZTickColor();
}
