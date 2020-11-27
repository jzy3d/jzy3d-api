package org.jzy3d.plot3d.rendering.legends.colorbars;

import java.awt.image.BufferedImage;

import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot2d.primitive.AWTAbstractImageGenerator;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.legends.AWTLegend;

public class AWTColorbarLegend extends AWTLegend implements IColorbarLegend {
    public AWTColorbarLegend(Drawable parent, Chart chart) {
        this(parent, chart.getView().getAxis().getLayout());
    }

    public AWTColorbarLegend(Drawable parent, IAxisLayout layout) {
        this(parent, layout.getZTickProvider(), layout.getZTickRenderer(), layout.getMainColor(), layout.getMainColor().negative());
    }

    public AWTColorbarLegend(Drawable parent, IAxisLayout layout, Color foreground) {
        this(parent, layout.getZTickProvider(), layout.getZTickRenderer(), foreground, null);
    }

    public AWTColorbarLegend(Drawable parent, IAxisLayout layout, Color foreground, Color background) {
        this(parent, layout.getZTickProvider(), layout.getZTickRenderer(), foreground, background);
    }

    public AWTColorbarLegend(Drawable parent, ITickProvider provider, ITickRenderer renderer) {
        this(parent, provider, renderer, Color.BLACK, Color.WHITE);

    }

    public AWTColorbarLegend(Drawable parent, ITickProvider provider, ITickRenderer renderer, Color foreground, Color background) {
        super(parent, foreground, background);
        this.provider = provider;
        this.renderer = renderer;
        this.minimumDimension = new Dimension(AWTAbstractImageGenerator.MIN_BAR_WIDTH, AWTAbstractImageGenerator.MIN_BAR_HEIGHT);

        initImageGenerator(parent, provider, renderer);
    }

    public void initImageGenerator(Drawable parent, ITickProvider provider, ITickRenderer renderer) {
        if (parent != null && parent instanceof IMultiColorable) {
            IMultiColorable mc = ((IMultiColorable) parent);
            if (mc.getColorMapper() != null) {
                imageGenerator = new AWTColorbarImageGenerator(mc.getColorMapper(), provider, renderer);
            }
        }
    }

    @Override
    public void render(Painter painter) {
        painter.glEnable_Blend();
        super.render(painter);
    }

    @Override
    public BufferedImage toImage(int width, int height) {
        if (imageGenerator != null) {
            setGeneratorColors();
            return imageGenerator.toImage(Math.max(width - 25, 1), Math.max(height - 25, 1));
        }
        return null;
    }


    protected ITickProvider provider;
    protected ITickRenderer renderer;
}
