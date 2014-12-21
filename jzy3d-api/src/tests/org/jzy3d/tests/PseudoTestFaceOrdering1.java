package org.jzy3d.tests;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.CompositeColorMapperUpdatePolicy;
import org.jzy3d.colors.OrderingStrategyScoreColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.events.IViewPointChangedListener;
import org.jzy3d.events.ViewPointChangedEvent;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.FixedDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ScientificNotationTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.annotation.CameraEyeOverlayAnnotation;
import org.jzy3d.plot3d.text.drawable.DrawableTextBillboard;

/**
 * Set surface colors according to each point distance to camera: red is near, blue is far.
 * 
 * Changing scale factor to >> 1 shows an erroneous computation of coordinates distances:
 * <ul>
 * <li>nearest surface polygons on a surface bump appear more far (more blue) than some actually 
 * other more far polygons.
 * <li>when data has a very large Z range, distance computation becomes very unprecise with points all appearing to min or max distance
 *</ul>
 * Moreover, scaling the surface shows that the actual camera eye point is scaled with the surface
 * and not with the view bounds (axebox).
 * 
 * @author Martin
 */
public class PseudoTestFaceOrdering1 {
    static double MAPPER_ZSCALE_FACTOR = 100;
    
    public static void main(String[] args) throws Exception {
        PseudoTestFaceOrdering1 surface = new PseudoTestFaceOrdering1();
        surface.BuildAndLaunch();
    }

    public void BuildAndLaunch() {
        IChartComponentFactory factory = getFactory();
        final Chart chart = new Chart(factory, Quality.Advanced, "newt");
        chart.getAxeLayout().setZTickRenderer(new ScientificNotationTickRenderer(1));
        chart.getAxeLayout().setYTickRenderer(new FixedDecimalTickRenderer(1));
        chart.getAxeLayout().setXTickRenderer(new FixedDecimalTickRenderer(1));
        
        // allow camera eye transform according to view scaling
        //BarycentreOrderingStrategy s = (BarycentreOrderingStrategy)chart.getScene().getGraph().getStrategy();
        //s.setView(chart.getView()); // experimental solution: scale camera eye with current view scaling
        
        genMapperSurface(chart.getView(), chart.getScene().getGraph(), chart.getAxeLayout());
        ((AWTView)chart.getView()).addRenderer2d(new CameraEyeOverlayAnnotation(chart.getView()));
        //chart.getView().getCamera().setUseSquaredDistance(false);
        
        // Points and Textes
        //createPoints(chart);

        // chart.getView().setSquared(false);
        ChartLauncher.openChart(chart);
        
        //chart.getView().getViewPointL
    }

    private AWTChartComponentFactory getFactory() {
        return new AWTChartComponentFactory();
    }

    public void createPoints(final Chart chart) {
        _experiencesPoints = new ArrayList<Point>();
        _experiencesLabels = new ArrayList<DrawableTextBillboard>();
        for (int i = 0; i < _expX.length; i++) {
            Coord3d coord = new Coord3d(_expX[i], _expY[i], _expZ[i]);
            // Points ...
            Point point = new Point(coord, Color.BLACK, 5);
            _experiencesPoints.add(point);

            // Labels ...
            String txt = _expIndex[i];
            DrawableTextBillboard label = new DrawableTextBillboard(txt, coord.add(new Coord3d(0.1, 0.1, 0.1)), Color.BLACK);
            _experiencesLabels.add(label);
        }

        chart.getScene().getGraph().add(_experiencesLabels);
        chart.getScene().getGraph().add(_experiencesPoints);
    }
    
    /**
     * Build a mapper based surface
     */
    public Shape genMapperSurface(final View view, final Graph graph, final IAxeLayout layout){
        Mapper mapper = new Mapper() {
            @Override
            public double f(double x, double y) {
                return MAPPER_ZSCALE_FACTOR * Math.sin(x / 10) * Math.cos(y / 20) * x;
            }
        };
        Range range = new Range(-150, 150);
        int steps = 50;
        
     // Create the object to represent the function over the given range
        OrthonormalGrid grid = new OrthonormalGrid(range, steps, range, steps);
        OrthonormalTessellator tesselator = new OrthonormalTessellator() {
            /*protected AbstractDrawable newQuad(Point p[]) {
                AbstractDrawable quad = new TesselatedPolygon(p);
                return quad;
            }*/
        };
        surface = (Shape) tesselator.build(grid.apply(mapper));
        
        return createSurface(surface, view, graph, layout);
    }
    
    /**
     * Build a delaunay based surface
     */
    public Shape genDelaunaySurface(final View view, final Graph graph, final IAxeLayout layout){
        List<Coord3d> data = new ArrayList<Coord3d>();
        for (int i = 0; i < _x.length; i++) {
            data.add(new Coord3d(_x[i], _y[i], _z[i]));
        }
        surface = Builder.buildDelaunay(data);
        return createSurface(surface, view, graph, layout);
    }

    /**
     * Setup surface coloring policy and change listeners.
     */
    public Shape createSurface(final Shape surface, final View view, final Graph graph, final IAxeLayout layout) {
        Color factor = new Color(1, 1, 1, 0.75f);

        colormap = new ColorMapRainbow();
        colormap.setDirection(false);
        
        colormapper = new OrderingStrategyScoreColorMapper(colormap, new CompositeColorMapperUpdatePolicy(), graph, factor);
        surface.setColorMapper(colormapper);
        surface.setWireframeDisplayed(true);

        colorbar = new AWTColorbarLegend(surface, layout);
        surface.setLegend(colorbar);

        view.addViewPointChangedListener(new IViewPointChangedListener(){
            @Override
            public void viewPointChanged(ViewPointChangedEvent e) {
                //System.out.println("min:" + colormapper.getMin() + " max:" + colormapper.getMax());
                //TicToc t = new TicToc();
                //t.tic();
                updateColorMapperRange();
                //t.toc();
            }
        });
        
        graph.add(surface);
        
        return surface;
    }
    
    public void updateColorMapperRange(){
        colormapper.preDraw(surface); 
    }
    
    protected IColorMap colormap;
    protected ColorMapper colormapper;
    protected AWTColorbarLegend colorbar;
    protected Shape surface;
    
    private final double[] _x;
    private final double[] _y;
    private final double[] _z;
    private final double[] _expX;
    private final double[] _expY;
    private final double[] _expZ;
    private final String[] _expIndex;
    private ArrayList<Point> _experiencesPoints;
    private ArrayList<DrawableTextBillboard> _experiencesLabels;


    /**
     * Surface
     */
    public PseudoTestFaceOrdering1() {
        _expX = new double[] { -1.0, 1.0, 0.0, 0.0, 0.0 };
        _expY = new double[] { 0.0, 0.0, -1.0, 1.0, 0.0 };
        _expZ = new double[] { 1799635.862225038, 2778958.3605334656, 2308941.6737486282, 2791418.430038142, 2778052.031336538 };
        _expIndex = new String[] { "17", "18", " 19", "20", "27" };

        _y = new double[] { -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5,
                0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2,
                0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1,
                0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3,
                -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, -1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };

        _x = new double[] { -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.9, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.8, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.7, -0.6, -0.6, -0.6, -0.6, -0.6, -0.6, -0.6, -0.6,
                -0.6, -0.6, -0.6, -0.6, -0.6, -0.6, -0.6, -0.6, -0.6, -0.6, -0.6, -0.6, -0.6, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.4, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2,
                -0.2, -0.2, -0.2, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3,
                0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9,
                0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };

        _z = new double[] { 1648446.8, 1691895.4, 1732207.9, 1769384.4, 1803424.9, 1834329.2, 1862097.6, 1886729.9, 1908226.1, 1926586.4, 1941810.5, 1953898.6, 1962850.8, 1968666.8, 1971346.8, 1970890.6, 1967298.5, 1960570.4, 1950706.1, 1937705.9, 1921569.6, 1761433.5, 1805073.4, 1845577.0, 1882944.8, 1917176.2, 1948271.9, 1976231.4, 2001054.9, 2022742.4, 2041293.8, 2056709.1, 2068988.4, 2078131.6, 2084138.9, 2087010.0, 2086745.1, 2083344.1, 2076807.1, 2067134.1, 2054325.1, 2038380.0, 1866066.5, 1909897.5, 1950592.4, 1988151.2, 2022574.0, 2053860.8, 2082011.5, 2107026.2,
                2128904.8, 2147647.2, 2163253.8, 2175724.2, 2185058.8, 2191257.2, 2194319.5, 2194245.8, 2191036.0, 2184690.2, 2175208.2, 2162590.5, 2146836.5, 1962345.9, 2006368.0, 2047254.1, 2085004.1, 2119618.0, 2151096.0, 2179438.0, 2204643.8, 2226713.5, 2245647.2, 2261445.0, 2274106.8, 2283632.2, 2290021.8, 2293275.2, 2293392.8, 2290374.2, 2284219.5, 2274929.0, 2262502.2, 2246939.5, 2050271.5, 2094484.8, 2135562.0, 2173503.2, 2208308.5, 2239977.5, 2268510.5, 2293907.5, 2316168.5, 2335293.5, 2351282.2, 2364135.2, 2373852.0, 2380432.8, 2383877.5, 2384186.0, 2381358.8,
                2375395.2, 2366295.8, 2354060.2, 2338688.5, 2129843.5, 2174248.0, 2215516.2, 2253648.8, 2288645.0, 2320505.2, 2349229.5, 2374817.8, 2397270.0, 2416586.0, 2432766.0, 2445810.0, 2455718.0, 2462490.0, 2466125.8, 2466625.8, 2463989.5, 2458217.2, 2449308.8, 2437264.5, 2422084.0, 2201061.8, 2245657.2, 2287117.0, 2325440.5, 2360628.0, 2392679.5, 2421594.8, 2447374.2, 2470017.5, 2489524.8, 2505896.0, 2519131.2, 2529230.5, 2536193.5, 2540020.5, 2540711.5, 2538266.5, 2532685.5, 2523968.2, 2512115.0, 2497125.8, 2263926.2, 2308713.0, 2350363.8, 2388878.5, 2424257.2,
                2456499.8, 2485606.5, 2511577.0, 2534411.5, 2554110.0, 2570672.2, 2584098.8, 2594389.0, 2601543.2, 2605561.5, 2606443.8, 2604189.8, 2598800.0, 2590274.0, 2578612.0, 2563814.0, 2318437.0, 2363415.0, 2405257.0, 2443962.8, 2479532.8, 2511966.5, 2541264.2, 2567426.0, 2590451.8, 2610341.2, 2627095.0, 2640712.5, 2651194.0, 2658539.5, 2662748.8, 2663822.2, 2661759.5, 2656560.8, 2648226.0, 2636755.2, 2622148.2, 2364594.0, 2409763.2, 2451796.2, 2490693.5, 2526454.5, 2559079.5, 2588568.5, 2614921.2, 2638138.2, 2658219.0, 2675163.8, 2688972.5, 2699645.2, 2707181.8,
                2711582.5, 2712847.0, 2710975.5, 2705968.0, 2697824.2, 2686544.8, 2672129.0, 2402397.5, 2447757.8, 2489982.0, 2529070.5, 2565022.5, 2597838.8, 2627519.0, 2654063.0, 2677471.0, 2697743.0, 2714879.0, 2728879.0, 2739742.8, 2747470.5, 2752062.2, 2753518.0, 2751837.8, 2747021.2, 2739069.0, 2727980.5, 2713756.0, 2431847.0, 2477398.8, 2519814.2, 2559093.5, 2595237.0, 2628244.2, 2658115.8, 2684851.0, 2708450.2, 2728913.2, 2746240.5, 2760431.5, 2771486.5, 2779405.5, 2784188.5, 2785835.5, 2784346.2, 2779721.0, 2771959.8, 2761062.5, 2747029.2, 2452943.0, 2498685.8,
                2541292.5, 2580763.2, 2617097.8, 2650296.2, 2680358.8, 2707285.2, 2731075.5, 2751730.0, 2769248.2, 2783630.5, 2794876.8, 2802986.8, 2807961.0, 2809799.0, 2808501.0, 2804067.0, 2796497.0, 2785791.0, 2771948.8, 2465685.5, 2511619.2, 2554417.2, 2594079.0, 2630604.8, 2663994.5, 2694248.0, 2721365.8, 2745347.2, 2766192.8, 2783902.2, 2798475.8, 2809913.0, 2818214.5, 2823379.8, 2825409.0, 2824302.2, 2820059.2, 2812680.5, 2802165.5, 2788514.5, 2470074.0, 2516199.0, 2559188.0, 2599041.0, 2635758.0, 2669339.0, 2699783.8, 2727092.5, 2751265.2, 2772302.0, 2790202.8,
                2804967.2, 2816595.8, 2825088.2, 2830444.8, 2832665.2, 2831749.8, 2827698.0, 2820510.2, 2810186.5, 2796726.8, 2466108.8, 2512425.0, 2555605.2, 2595649.5, 2632557.5, 2666329.8, 2696965.8, 2724465.8, 2748829.5, 2770057.5, 2788149.2, 2803105.0, 2814924.8, 2823608.5, 2829156.2, 2831567.8, 2830843.2, 2826982.8, 2819986.2, 2809853.8, 2796585.2, 2453790.0, 2500297.5, 2543668.8, 2583904.2, 2621003.5, 2654966.8, 2685794.0, 2713485.0, 2738040.2, 2759459.2, 2777742.2, 2792889.2, 2804900.2, 2813775.0, 2819513.8, 2822116.5, 2821583.2, 2817914.0, 2811108.8, 2801167.2,
                2788089.8, 2433117.5, 2479816.0, 2523378.5, 2563805.2, 2601095.5, 2635250.0, 2666268.5, 2694150.8, 2718897.0, 2740507.2, 2758981.5, 2774319.5, 2786521.8, 2795587.8, 2801517.8, 2804311.8, 2803969.8, 2800491.5, 2793877.2, 2784127.2, 2771241.0, 2404091.2, 2450981.0, 2494734.8, 2535352.5, 2572834.0, 2607179.8, 2638389.2, 2666462.8, 2691400.2, 2713201.8, 2731867.0, 2747396.2, 2759789.5, 2769046.8, 2775168.0, 2778153.2, 2778002.2, 2774715.2, 2768292.2, 2758733.2, 2746038.2, 2366711.2, 2413792.2, 2457737.2, 2498546.0, 2536218.8, 2570755.5, 2602156.2, 2630421.0,
                2655549.8, 2677542.2, 2696398.8, 2712119.2, 2724703.8, 2734152.2, 2740464.5, 2743641.0, 2743681.2, 2740585.5, 2734353.5, 2724985.8, 2712481.8, 2320977.5, 2368249.8, 2412385.8, 2453386.0, 2491249.8, 2525977.8, 2557569.8, 2586025.5, 2611345.5, 2633529.2, 2652577.0, 2668488.8, 2681264.2, 2690903.8, 2697407.5, 2700775.0, 2701006.2, 2698101.8, 2692061.2, 2682884.5, 2670571.8 };
    }
}