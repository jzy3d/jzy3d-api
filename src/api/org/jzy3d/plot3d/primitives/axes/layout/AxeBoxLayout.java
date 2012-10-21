package org.jzy3d.plot3d.primitives.axes.layout;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.axes.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;


public class AxeBoxLayout implements IAxeLayout{
	/** Default AxeBox layout */
	public AxeBoxLayout(){
		setXAxeLabel("X");
		setYAxeLabel("Y");
		setZAxeLabel("Z");

		setXAxeLabelDisplayed(true);
		setYAxeLabelDisplayed(true);
		setZAxeLabelDisplayed(true);

		setXTickProvider(new SmartTickProvider(5));
		setYTickProvider(new SmartTickProvider(5));
		setZTickProvider(new SmartTickProvider(6));
		
		setXTickRenderer(new DefaultDecimalTickRenderer(4));
		setYTickRenderer(new DefaultDecimalTickRenderer(4));
		setZTickRenderer(new DefaultDecimalTickRenderer(6));
		
		setFaceDisplayed(false);
		setXTickLabelDisplayed(true);
		setYTickLabelDisplayed(true);
		setZTickLabelDisplayed(true);
		
		setMainColor(Color.BLACK);
	}
	
	@Override
	public void setMainColor(Color color) {
		mainColor = color;
		setXTickColor(color);
		setYTickColor(color);
		setZTickColor(color);
		setGridColor(color);
        setQuadColor(color.negative());
	}
	
	@Override
	public Color getMainColor() {
		return mainColor;
	}
	
	@Override
	public double[] getXTicks(double min, double max) {
		lastXmin = min;
		lastXmax = max;
		xTicks = xTickProvider.generateTicks(min, max);
		return xTicks;
	}

	@Override
	public double[] getYTicks(double min, double max) {
		lastYmin = min;
		lastYmax = max;
		yTicks = yTickProvider.generateTicks(min, max);
		return yTicks;
	}

	@Override
	public double[] getZTicks(double min, double max) {
		lastZmin = min;
		lastZmax = max;
		zTicks = zTickProvider.generateTicks(min, max);
		return zTicks;
	}
	
	public String getXAxeLabel() {
		return xAxeLabel;
	}

	public void setXAxeLabel(String axeLabel) {
		xAxeLabel = axeLabel;
	}

	public String getYAxeLabel() {
		return yAxeLabel;
	}

	public void setYAxeLabel(String axeLabel) {
		yAxeLabel = axeLabel;
	}

	public String getZAxeLabel() {
		return zAxeLabel;
	}

	public void setZAxeLabel(String axeLabel) {
		zAxeLabel = axeLabel;
	}

	public double[] getXTicks() {
		return xTicks;
	}

	public double[] getYTicks() {
		return yTicks;
	}

	public double[] getZTicks() {
		return zTicks;
	}

	public ITickProvider getXTickProvider() {
		return xTickProvider;
	}

	public void setXTickProvider(ITickProvider tickProvider) {
		xTickProvider = tickProvider;
		
		if(lastXmin!=Float.NaN) // update ticks if we can
			getXTicks(lastXmin, lastXmax);
	}

	public ITickProvider getYTickProvider() {
		return yTickProvider;
	}

	public void setYTickProvider(ITickProvider tickProvider) {
		yTickProvider = tickProvider;
		
		if(lastYmin!=Float.NaN) // update ticks if we can
			getYTicks(lastYmin, lastYmax);
	}

	public ITickProvider getZTickProvider() {
		return zTickProvider;
	}

	public void setZTickProvider(ITickProvider tickProvider) {
		zTickProvider = tickProvider;
		
		if(lastZmin!=Float.NaN)  // update ticks if we can
			getZTicks(lastZmin, lastZmax);
	}

	public ITickRenderer getXTickRenderer() {
		return xTickRenderer;
	}

	public void setXTickRenderer(ITickRenderer tickRenderer) {
		xTickRenderer = tickRenderer;
	}

	public ITickRenderer getYTickRenderer() {
		return yTickRenderer;
	}

	public void setYTickRenderer(ITickRenderer tickRenderer) {
		yTickRenderer = tickRenderer;
	}

	public ITickRenderer getZTickRenderer() {
		return zTickRenderer;
	}

	public void setZTickRenderer(ITickRenderer tickRenderer) {
		zTickRenderer = tickRenderer;
	}

	public Color getXTickColor() {
		return xTickColor;
	}

	public void setXTickColor(Color tickColor) {
		xTickColor = tickColor;
	}

	public Color getYTickColor() {
		return yTickColor;
	}

	public void setYTickColor(Color tickColor) {
		yTickColor = tickColor;
	}

	public Color getZTickColor() {
		return zTickColor;
	}

	public void setZTickColor(Color tickColor) {
		zTickColor = tickColor;
	}
	
	public boolean isFaceDisplayed() {
		return faceDisplayed;
	}

	public void setFaceDisplayed(boolean faceDisplayed) {
		this.faceDisplayed = faceDisplayed;
	}
	
	public Color getQuadColor() {
		return quadColor;
	}

	public void setQuadColor(Color quadColor) {
		this.quadColor = quadColor;
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
	}

	public boolean isXAxeLabelDisplayed() {
		return xAxeLabelDisplayed;
	}

	public void setXAxeLabelDisplayed(boolean axeLabelDisplayed) {
		xAxeLabelDisplayed = axeLabelDisplayed;
	}

	public boolean isYAxeLabelDisplayed() {
		return yAxeLabelDisplayed;
	}

	public void setYAxeLabelDisplayed(boolean axeLabelDisplayed) {
		yAxeLabelDisplayed = axeLabelDisplayed;
	}

	public boolean isZAxeLabelDisplayed() {
		return zAxeLabelDisplayed;
	}

	public void setZAxeLabelDisplayed(boolean axeLabelDisplayed) {
		zAxeLabelDisplayed = axeLabelDisplayed;
	}
	
	public boolean isXTickLabelDisplayed() {
		return xTickLabelDisplayed;
	}

	public void setXTickLabelDisplayed(boolean tickLabelDisplayed) {
		xTickLabelDisplayed = tickLabelDisplayed;
	}

	public boolean isYTickLabelDisplayed() {
		return yTickLabelDisplayed;
	}

	public void setYTickLabelDisplayed(boolean tickLabelDisplayed) {
		yTickLabelDisplayed = tickLabelDisplayed;
	}

	public boolean isZTickLabelDisplayed() {
		return zTickLabelDisplayed;
	}

	public void setZTickLabelDisplayed(boolean tickLabelDisplayed) {
		zTickLabelDisplayed = tickLabelDisplayed;
	}



	/**********************************************************/
	
	protected String xAxeLabel;
	protected String yAxeLabel;
	protected String zAxeLabel;
	protected boolean xAxeLabelDisplayed;
	protected boolean yAxeLabelDisplayed;
	protected boolean zAxeLabelDisplayed;

	protected double  xTicks[];
	protected double  yTicks[];
	protected double  zTicks[];
	
	protected ITickProvider xTickProvider;
	protected ITickProvider yTickProvider;
	protected ITickProvider zTickProvider;

	protected ITickRenderer xTickRenderer;
	protected ITickRenderer yTickRenderer;
	protected ITickRenderer zTickRenderer;
	
	protected Color   xTickColor;
	protected Color   yTickColor;
	protected Color   zTickColor;

	protected boolean xTickLabelDisplayed;
	protected boolean yTickLabelDisplayed;
	protected boolean zTickLabelDisplayed;

	protected boolean faceDisplayed;

	protected Color   quadColor;
	protected Color   gridColor;
	
	protected double lastXmin = Float.NaN;
	protected double lastXmax = Float.NaN;
	protected double lastYmin = Float.NaN;
	protected double lastYmax = Float.NaN;
	protected double lastZmin = Float.NaN;
	protected double lastZmax = Float.NaN;
	
	protected Color mainColor;
}
