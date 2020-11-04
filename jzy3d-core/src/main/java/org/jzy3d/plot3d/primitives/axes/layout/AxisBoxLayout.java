package org.jzy3d.plot3d.primitives.axes.layout;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.axes.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.providers.SmartTickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;


public class AxisBoxLayout implements IAxisLayout{
	/** Default AxeBox layout */
	public AxisBoxLayout(){
		setXAxisLabel("X");
		setYAxisLabel("Y");
		setZAxisLabel("Z");

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
	
	@Override
    public String getXAxisLabel() {
		return xAxeLabel;
	}

	@Override
    public void setXAxisLabel(String axeLabel) {
		xAxeLabel = axeLabel;
	}

	@Override
    public String getYAxisLabel() {
		return yAxeLabel;
	}

	@Override
    public void setYAxisLabel(String axeLabel) {
		yAxeLabel = axeLabel;
	}

	@Override
    public String getZAxisLabel() {
		return zAxeLabel;
	}

	@Override
    public void setZAxisLabel(String axeLabel) {
		zAxeLabel = axeLabel;
	}

	@Override
    public double[] getXTicks() {
		return xTicks;
	}

	@Override
    public double[] getYTicks() {
		return yTicks;
	}

	@Override
    public double[] getZTicks() {
		return zTicks;
	}

	@Override
    public ITickProvider getXTickProvider() {
		return xTickProvider;
	}

	@Override
    public void setXTickProvider(ITickProvider tickProvider) {
		xTickProvider = tickProvider;
		
		if(lastXmin!=Float.NaN) // update ticks if we can
			getXTicks(lastXmin, lastXmax);
	}

	@Override
    public ITickProvider getYTickProvider() {
		return yTickProvider;
	}

	@Override
    public void setYTickProvider(ITickProvider tickProvider) {
		yTickProvider = tickProvider;
		
		if(lastYmin!=Float.NaN) // update ticks if we can
			getYTicks(lastYmin, lastYmax);
	}

	@Override
    public ITickProvider getZTickProvider() {
		return zTickProvider;
	}

	@Override
    public void setZTickProvider(ITickProvider tickProvider) {
		zTickProvider = tickProvider;
		
		if(lastZmin!=Float.NaN)  // update ticks if we can
			getZTicks(lastZmin, lastZmax);
	}

	@Override
    public ITickRenderer getXTickRenderer() {
		return xTickRenderer;
	}

	@Override
    public void setXTickRenderer(ITickRenderer tickRenderer) {
		xTickRenderer = tickRenderer;
	}

	@Override
    public ITickRenderer getYTickRenderer() {
		return yTickRenderer;
	}

	@Override
    public void setYTickRenderer(ITickRenderer tickRenderer) {
		yTickRenderer = tickRenderer;
	}

	@Override
    public ITickRenderer getZTickRenderer() {
		return zTickRenderer;
	}

	@Override
    public void setZTickRenderer(ITickRenderer tickRenderer) {
		zTickRenderer = tickRenderer;
	}

	@Override
    public Color getXTickColor() {
		return xTickColor;
	}

	@Override
    public void setXTickColor(Color tickColor) {
		xTickColor = tickColor;
	}

	@Override
    public Color getYTickColor() {
		return yTickColor;
	}

	@Override
    public void setYTickColor(Color tickColor) {
		yTickColor = tickColor;
	}

	@Override
    public Color getZTickColor() {
		return zTickColor;
	}

	@Override
    public void setZTickColor(Color tickColor) {
		zTickColor = tickColor;
	}
	
	@Override
    public boolean isFaceDisplayed() {
		return faceDisplayed;
	}

	@Override
    public void setFaceDisplayed(boolean faceDisplayed) {
		this.faceDisplayed = faceDisplayed;
	}
	
	@Override
    public Color getQuadColor() {
		return quadColor;
	}

	@Override
    public void setQuadColor(Color quadColor) {
		this.quadColor = quadColor;
	}

	@Override
    public Color getGridColor() {
		return gridColor;
	}

	@Override
    public void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
	}

	@Override
    public boolean isXAxeLabelDisplayed() {
		return xAxeLabelDisplayed;
	}

	@Override
    public void setXAxeLabelDisplayed(boolean axeLabelDisplayed) {
		xAxeLabelDisplayed = axeLabelDisplayed;
	}

	@Override
    public boolean isYAxeLabelDisplayed() {
		return yAxeLabelDisplayed;
	}

	@Override
    public void setYAxeLabelDisplayed(boolean axeLabelDisplayed) {
		yAxeLabelDisplayed = axeLabelDisplayed;
	}

	@Override
    public boolean isZAxeLabelDisplayed() {
		return zAxeLabelDisplayed;
	}

	@Override
    public void setZAxeLabelDisplayed(boolean axeLabelDisplayed) {
		zAxeLabelDisplayed = axeLabelDisplayed;
	}
	
	@Override
    public boolean isXTickLabelDisplayed() {
		return xTickLabelDisplayed;
	}

	@Override
    public void setXTickLabelDisplayed(boolean tickLabelDisplayed) {
		xTickLabelDisplayed = tickLabelDisplayed;
	}

	@Override
    public boolean isYTickLabelDisplayed() {
		return yTickLabelDisplayed;
	}

	@Override
    public void setYTickLabelDisplayed(boolean tickLabelDisplayed) {
		yTickLabelDisplayed = tickLabelDisplayed;
	}

	@Override
    public boolean isZTickLabelDisplayed() {
		return zTickLabelDisplayed;
	}

	@Override
    public void setZTickLabelDisplayed(boolean tickLabelDisplayed) {
		zTickLabelDisplayed = tickLabelDisplayed;
	}
	
	@Override
    public boolean isTickLineDisplayed() {
        return tickLineDisplayed;
    }

    @Override
    public void setTickLineDisplayed(boolean tickLineDisplayed) {
        this.tickLineDisplayed = tickLineDisplayed;
    }





    /**********************************************************/
	
	protected boolean tickLineDisplayed = true;
	
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
