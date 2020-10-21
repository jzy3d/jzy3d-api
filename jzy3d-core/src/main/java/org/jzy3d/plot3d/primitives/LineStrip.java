package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.plot3d.primitives.symbols.MaskImageSymbolHandler;
import org.jzy3d.plot3d.primitives.symbols.SymbolHandler;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.GLU;

/**
 * Color works as follow:
 * <ul>
 * <li>If wireframe color is null (default), uses each point color and performs color interpolation
 * <li>Otherwise apply a uniform wireframe color.
 * </ul>
 * 
 * 
 * Dotted line are built using
 * 
 * http://www.glprogramming.com/red/images/Image35.gif
 * 
 * @author Martin Pernollet
 */
public class LineStrip extends AbstractWireframeable {
    public LineStrip() {
        this(2);
    }

    public LineStrip(int n) {
        points = new ArrayList<Point>(n);
        bbox = new BoundingBox3d();
        symbolHandler = new MaskImageSymbolHandler(n);
        setWireframeColor(null);
    }

    public LineStrip(Coord3d... coords) {
        this(Arrays.asList(coords));
    }

    public LineStrip(Color color, Coord3d... coords) {
        this(Arrays.asList(coords));
        setWireframeColor(color);
    }

    public LineStrip(List<Coord3d> coords) {
        this(coords.size());

        addAllPoints(coords);
    }

    public LineStrip(Point c1, Point c2) {
        this();
        add(c1);
        add(c2);
    }
    
    /**Set the wireframe color.*/
    @Override
    public void setWireframeColor(Color color){
        super.setWireframeColor(color);
        if(color!=null && points!=null){
            for(Point p: points){
                p.setColor(color);
            }
        }
    }
    
    /**
     * A convenient shortcut for {@link #setWireframeColor}
     * @param color
     */
    public void setColor(Color color){
        setWireframeColor(color);
    }
    
    /**
     * A convenient shortcut for {@link #getWireframeColor}
     */
    public Color getColor(){
        return getWireframeColor();
    }
    
    /* */

    @Override
    public void draw(GL gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);
        if (points.size() > 1) {
            drawLine(gl);
        } else if (points.size() == 1 && !showPoints) {
            drawPoints(gl);

        }

        if (showSymbols && symbolHandler!=null) {
            symbolHandler.drawSymbols(gl, glu, cam);
        }

        drawPointsIfEnabled(gl);
    }

    

    public void drawLine(GL gl) {
        //gl.glLineWidth(wfwidth);
        if (gl.isGL2()) {
            drawLineGL2(gl);
        } else {
            drawLineGLES2();
        }
    }

    public void drawLineGLES2() {
        GLES2CompatUtils.glBegin(GL.GL_LINE_STRIP);

        if (wfcolor == null) {
            for (Point p : points) {
                colorGLES2(p.rgb);
                vertexGLES2(p.xyz);
                //GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                //GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
        } else {
            for (Point p : points) {
                colorGLES2(wfcolor);
                vertexGLES2(p.xyz);
                //GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                //GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
        }
        GLES2CompatUtils.glEnd();
    }

    public void drawLineGL2(GL gl) {
        if (stipple) {
            gl.getGL2().glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
            gl.glEnable(GL2.GL_LINE_STIPPLE);
            gl.getGL2().glLineStipple(stippleFactor, stipplePattern);
        }
        gl.getGL2().glLineWidth(wfwidth); 

        gl.getGL2().glBegin(GL.GL_LINE_STRIP);

        if (wfcolor == null) {
            for (Point p : points) {
                colorGL2(gl, p.rgb);
                vertexGL2(gl, p.xyz);
            }
        } else {
            for (Point p : points) {
                colorGL2(gl, wfcolor);
                vertexGL2(gl, p.xyz);
            }
        }
        gl.getGL2().glEnd();
        
        if (stipple) {
            gl.glDisable(GL2.GL_LINE_STIPPLE);
        }
    }

    public void drawPointsIfEnabled(GL gl) {
        if (showPoints) {
            drawPoints(gl);
        }
    }

    public void drawPoints(GL gl) {
        if (gl.isGL2()) {
            drawPointsGL2(gl);
        } else {
            drawPointsGLES2();
        }
    }

    public void drawPointsGLES2() {
        GLES2CompatUtils.glBegin(GL.GL_POINTS);

        for (Point p : points) {
            if (wfcolor == null)
                colorGLES2(p.rgb);
            else
                colorGLES2(wfcolor);
            vertexGLES2(p.xyz);
        }

        GLES2CompatUtils.glEnd();
    }

    public void drawPointsGL2(GL gl) {
        gl.getGL2().glBegin(GL.GL_POINTS);

        gl.getGL2().glPointSize(wfwidth);

        for (Point p : points) {
            if (wfcolor == null)
                colorGL2(gl, p.rgb);
            else
                colorGL2(gl, wfcolor);
            vertexGL2(gl, p.xyz);
        }

        gl.getGL2().glEnd();
    }

    /* */

    @Override
    public void applyGeometryTransform(Transform transform) {
        for (Point p : points) {
            p.xyz = transform.compute(p.xyz);
        }
        updateBounds();
    }

    @Override
    public void updateBounds() {
        bbox.reset();
        for (Point p : points)
            bbox.add(p);
    }

    public void add(Point point) {
        if (showSymbols) {
            symbolHandler.addSymbolOn(point);
        }

        points.add(point);
        bbox.add(point);
    }

    public void add(Coord3d coord3d) {
        add(new Point(coord3d));
    }

    public void add(List<Coord3d> coords) {
        for (Coord3d c : coords)
            add(c);
    }

    public void addAll(List<Point> points) {
        for (Point p : points)
            add(p);
    }

    public void addAll(LineStrip strip) {
        addAll(strip.getPoints());
    }

    public void addAllPoints(List<Coord3d> coords) {
        for (Coord3d c : coords) {
            Point p = new Point(c);
            add(p);
        }
    }

    public void clear() {
        points.clear();
        updateBounds();
    }

    public Point get(int p) {
        return points.get(p);
    }

    public Point getLastPoint() {
        int last = points.size() - 1;
        if (last >= 0)
            return points.get(last);
        return null;
    }

    public List<Point> getPoints() {
        return points;
    }

    public int size() {
        return points.size();
    }

    /** A shortcut for {@link #setWireframeWidth} */
    public void setWidth(float width) {
        setWireframeWidth(width);
    }

    /** A shortcut for {@link #getWireframeWidth} */
    public float getWidth() {
        return getWireframeWidth();
    }

    public boolean isShowPoints() {
        return showPoints;
    }

    public void setShowPoints(boolean showPoints) {
        this.showPoints = showPoints;
    }

    public boolean isShowSymbols() {
        return showSymbols;
    }

    public void setShowSymbols(boolean showSymbols) {
        if (!showSymbols){
            symbolHandler.clear();           
        }
        else {
            if(symbolHandler!=null){
                symbolHandler.clear();           

                for (Point point : getPoints()) {
                    symbolHandler.addSymbolOn(point);
                }               
            }
        }
        this.showSymbols = showSymbols;
    }

    /**
     * Indicates if stippled rendering is enabled for this line.
     * 
     * @see http://www.glprogramming.com/red/chapter02.html (Stippled line section)
     */
    public boolean isStipple() {
        return stipple;
    }

    /**
     * Enable or disable stippled rendering.
     * 
     * @see http://www.glprogramming.com/red/chapter02.html (Stippled line section)
     */
    public void setStipple(boolean stipple) {
        this.stipple = stipple;
    }

    /**
     * Stippled line factor.
     * 
     * @see http://www.glprogramming.com/red/images/Image35.gif
     * @see http://www.glprogramming.com/red/chapter02.html (Stippled line section)
     */
    public int getStippleFactor() {
        return stippleFactor;
    }

    /**
     * Stippled line factor.
     * 
     * @see http://www.glprogramming.com/red/images/Image35.gif
     * @see http://www.glprogramming.com/red/chapter02.html (Stippled line section)
     */
    public void setStippleFactor(int stippleFactor) {
        this.stippleFactor = stippleFactor;
    }

    /**
     * Stippled line pattern.
     * 
     * @see http://www.glprogramming.com/red/images/Image35.gif
     * @see http://www.glprogramming.com/red/chapter02.html (Stippled line section)
     */
    public short getStipplePattern() {
        return stipplePattern;
    }

    /**
     * Stippled line pattern.
     * 
     * @see http://www.glprogramming.com/red/images/Image35.gif
     * @see http://www.glprogramming.com/red/chapter02.html (Stippled line section)
     */
    public void setStipplePattern(short stipplePattern) {
        this.stipplePattern = stipplePattern;
    }

    @Override
    public double getDistance(Camera camera) {
        return getBarycentre().distance(camera.getEye());
    }

    @Override
    public double getShortestDistance(Camera camera) {
        double min = Float.MAX_VALUE;
        double dist = 0;
        for (Point point : points) {
            dist = point.getDistance(camera);
            if (dist < min)
                min = dist;
        }
        return min;
    }

    @Override
    public double getLongestDistance(Camera camera) {
        double max = 0;
        double dist = 0;
        for (Point point : points) {
            dist = point.getDistance(camera);
            if (dist < max)
                max = dist;
        }
        return max;
    }

    /**
     * Merge lines by selecting the most relevant connection point: A-B to C-D if distance BC is shorter than distance DA C-D to A-B
     */
    public static LineStrip merge(LineStrip strip1, LineStrip strip2) {
        Coord3d a = strip1.get(0).xyz;
        Coord3d b = strip1.get(strip1.size() - 1).xyz;
        Coord3d c = strip2.get(0).xyz;
        Coord3d d = strip2.get(strip2.size() - 1).xyz;

        double bc = b.distance(c);
        double da = d.distance(a);

        if (bc > da) {
            strip1.addAll(strip2);
            return strip1;
        } else {
            strip2.addAll(strip1);
            return strip2;
        }
    }
    
    public SymbolHandler getSymbolHandler() {
        return symbolHandler;
    }

    public void setSymbolHandler(SymbolHandler symbolHandler) {
        this.symbolHandler = symbolHandler;
    }
    
    @Override
    public void setSpaceTransformer(SpaceTransformer spaceTransformer){
        super.setSpaceTransformer(spaceTransformer);
        if (showSymbols && symbolHandler!=null) {
            symbolHandler.setSpaceTransformer(spaceTransformer);
        }
    }

    /**********************************************************************/

    @Override
    public String toString(int depth) {
        return (Utils.blanks(depth) + "(LineStrip) #points:" + points.size());
    }

    /**********************************************************************/

    protected List<Point> points;
    // protected float width;
    protected boolean showPoints = false;
    protected boolean showSymbols = false;

    protected boolean stipple = false;
    protected int stippleFactor = 4;
    protected short stipplePattern = (short) 0xAAAA;
    protected SymbolHandler symbolHandler = null;
}
