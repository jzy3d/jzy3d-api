package org.jzy3d.plot3d.primitives.axes;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Vector3d;
import org.jzy3d.plot3d.primitives.axes.layout.AxeBoxLayout;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.overlay.TextOverlay;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

/**The AxeBox displays a box with front face invisible and ticks labels.
 * @author Martin Pernollet
 */
public class AxeBox implements IAxe{
	public AxeBox(BoundingBox3d bbox){
	    this(bbox, new AxeBoxLayout());
	}
	public AxeBox(BoundingBox3d bbox, IAxeLayout layout){
		this.layout = layout;
		if(bbox.valid())
		    setAxe(bbox);
		else
		    setAxe(new BoundingBox3d(-1, 1, -1, 1, -1, 1));
		wholeBounds = new BoundingBox3d();
		init();
	}
	
	protected void init(){
		setScale(new Coord3d(1.0f, 1.0f, 1.0f));
	}
	
	public void dispose(){
		if(txtRenderer!=null)
			txtRenderer.dispose();
	}
	
    public ITextRenderer getTextRenderer(){
        return txt;
    }
    
    public void setTextRenderer(ITextRenderer renderer){
        txt = renderer;
    }
	
	public TextOverlay getExperimentalTextRenderer(){
		return txtRenderer;
	}
	
	/** Initialize a text renderer that will reference the target canvas for getting
	 * its dimensions (in order to convert coordinates from OpenGL2 to Java2d).
	 * 
	 * @param canvas
	 */
	public void setExperimentalTextOverlayRenderer(ICanvas canvas){
		txtRenderer = new TextOverlay(canvas);
	}
	
	public View getView() {
		return view;
	}
	
	/** When setting a current view, the AxeBox can know the view is on mode CameraMode.TOP,
	 * and optimize some axis placement.*/
	public void setView(View view) {
		this.view = view;
	}
	
	/***********************************************************/
	
	@Override
	public void setAxe(BoundingBox3d bbox){
		this.boxBounds = bbox;
		setAxeBox(bbox.getXmin(), bbox.getXmax(), 
				  bbox.getYmin(), bbox.getYmax(), 
				  bbox.getZmin(), bbox.getZmax() );
	}	
	
	@Override
	public BoundingBox3d getBoxBounds(){
		return boxBounds;
	}

	@Override
	public IAxeLayout getLayout() {
		return layout;
	}
	
	/** Return the boundingBox of this axis, including the volume occupied by the texts.
	 * This requires calling {@link draw()} before, which computes actual ticks position in 3d,
	 * and updates the bounds.
	 */
	public BoundingBox3d getWholeBounds(){
		return wholeBounds;
	}
	
	public Coord3d getCenter(){
		return center;
	}
	
	/**  Set the scaling factor that are applyed on this object before 
	 * GL2 commands.*/
	public void setScale(Coord3d scale){
		this.scale = scale;
	}
	
	/***********************************************************/
		
	/**
	 * Draws the AxeBox. The camera is used to determine which axis is closest
	 * to the ur point ov view, in order to decide for an axis on which 
	 * to diplay the tick values.
	 */
	@Override
	public void draw(GL2 gl, GLU glu, Camera camera){
		// Set scaling
		gl.glLoadIdentity();
		gl.glScalef(scale.x, scale.y, scale.z);
		
		// Set culling
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glFrontFace(GL2.GL_CCW);
		gl.glCullFace(GL2.GL_FRONT);
		
		// Draw cube in feedback buffer for computing hidden quads
		quadIsHidden = getHiddenQuads(gl, camera);	
		
		// Plain part of quad making the surrounding box
		if( layout.isFaceDisplayed() ){
			Color quadcolor = layout.getQuadColor();
			gl.glPolygonMode(GL2.GL_BACK, GL2.GL_FILL);
			gl.glColor4f(quadcolor.r, quadcolor.g, quadcolor.b, quadcolor.a);
			gl.glLineWidth(1.0f);
			gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
			gl.glPolygonOffset(1.0f, 1.0f); // handle stippling
			drawCube(gl, GL2.GL_RENDER);
			gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
		}
		
		// Edge part of quads making the surrounding box
		Color gridcolor = layout.getGridColor();
		gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
		gl.glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
		gl.glLineWidth(1);			
		drawCube(gl, GL2.GL_RENDER);	
				
		// Draw grids on non hidden quads
		gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
		gl.glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
		gl.glLineWidth(1);
		gl.glLineStipple(1, (short)0xAAAA);
		gl.glEnable(GL2.GL_LINE_STIPPLE);		
		for(int quad=0; quad<6; quad++)
			if(!quadIsHidden[quad])		
				drawGridOnQuad(gl, quad);
		gl.glDisable(GL2.GL_LINE_STIPPLE);
		
		// Draw ticks on the closest axes
		wholeBounds.reset();
		wholeBounds.add(boxBounds);
		
		//gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		
		// Display x axis ticks
		if(xrange>0 && layout.isXTickLabelDisplayed()){
			
			// If we are on top, we make direct axe placement
			if( view != null && view.getViewMode().equals(ViewPositionMode.TOP) ){
				BoundingBox3d bbox = drawTicks(gl, glu, camera, 1, AXE_X, layout.getXTickColor(), Halign.LEFT, Valign.TOP); // setup tick labels for X on the bottom 
				wholeBounds.add(bbox);
			}
			// otherwise computed placement
			else{
				int xselect = findClosestXaxe(camera);
				if(xselect>=0){
					BoundingBox3d bbox = drawTicks(gl, glu, camera, xselect, AXE_X, layout.getXTickColor());
					wholeBounds.add(bbox);
				}
				else{
					//System.err.println("no x axe selected: " + Arrays.toString(quadIsHidden));
					// HACK: handles "on top" view, when all face of cube are drawn, which forbid to select an axe automatically
					BoundingBox3d bbox = drawTicks(gl, glu, camera, 2, AXE_X, layout.getXTickColor(), Halign.CENTER, Valign.TOP); 
					wholeBounds.add(bbox);
				}
			}
		}
		
		// Display y axis ticks
		if(yrange>0 && layout.isYTickLabelDisplayed()){
			if( view != null && view.getViewMode().equals(ViewPositionMode.TOP) ){
				BoundingBox3d bbox = drawTicks(gl, glu, camera, 2, AXE_Y, layout.getYTickColor(), Halign.LEFT, Valign.GROUND); // setup tick labels for Y on the left 
				wholeBounds.add(bbox);
			}
			else{
				int yselect = findClosestYaxe(camera);
				if(yselect>=0){
					BoundingBox3d bbox = drawTicks(gl, glu, camera, yselect, AXE_Y, layout.getYTickColor());
					wholeBounds.add(bbox);
				}
				else{
					//System.err.println("no y axe selected: " + Arrays.toString(quadIsHidden));
					// HACK: handles "on top" view, when all face of cube are drawn, which forbid to select an axe automatically
					BoundingBox3d bbox = drawTicks(gl, glu, camera, 1, AXE_Y, layout.getYTickColor(), Halign.RIGHT, Valign.GROUND);
					wholeBounds.add(bbox);
				}
			}
		}
		
		// Display z axis ticks
		if(zrange>0 && layout.isZTickLabelDisplayed()){
			if( view != null && view.getViewMode().equals(ViewPositionMode.TOP) ){
				
			}
			else{
				int zselect = findClosestZaxe(camera);
				if(zselect>=0){
					BoundingBox3d bbox = drawTicks(gl, glu, camera, zselect, AXE_Z, layout.getZTickColor());
					wholeBounds.add(bbox);
				}				
			}
		}
		
		// Unset culling
		gl.glDisable(GL2.GL_CULL_FACE);
	}
	
	/***********************************************************/
	
	/**
	 * Set the parameters and data of the AxeBox.
	 */
	protected void setAxeBox(float xmin, float xmax, float ymin, float ymax, float zmin, float zmax){
		// Compute center
		center = new Coord3d((xmax+xmin)/2, (ymax+ymin)/2, (zmax+zmin)/2);
		xrange = xmax-xmin;
		yrange = ymax-ymin;
		zrange = zmax-zmin;
		
		// Define configuration of 6 quads (faces of the box)
		quadx = new float[6][4];
		quady = new float[6][4];
		quadz = new float[6][4];
		
		// x near
		quadx[0][0] = xmax;  quady[0][0] = ymin;  quadz[0][0] = zmax; 
		quadx[0][1] = xmax;  quady[0][1] = ymin;  quadz[0][1] = zmin;
		quadx[0][2] = xmax;  quady[0][2] = ymax;  quadz[0][2] = zmin;
		quadx[0][3] = xmax;  quady[0][3] = ymax;  quadz[0][3] = zmax;
		// x far
		quadx[1][0] = xmin;  quady[1][0] = ymax;  quadz[1][0] = zmax; 
		quadx[1][1] = xmin;  quady[1][1] = ymax;  quadz[1][1] = zmin;
		quadx[1][2] = xmin;  quady[1][2] = ymin;  quadz[1][2] = zmin;
		quadx[1][3] = xmin;  quady[1][3] = ymin;  quadz[1][3] = zmax;
		// y near
		quadx[2][0] = xmax;  quady[2][0] = ymax;  quadz[2][0] = zmax; 
		quadx[2][1] = xmax;  quady[2][1] = ymax;  quadz[2][1] = zmin;
		quadx[2][2] = xmin;  quady[2][2] = ymax;  quadz[2][2] = zmin;
		quadx[2][3] = xmin;  quady[2][3] = ymax;  quadz[2][3] = zmax;
		// y far
		quadx[3][0] = xmin;  quady[3][0] = ymin;  quadz[3][0] = zmax; 
		quadx[3][1] = xmin;  quady[3][1] = ymin;  quadz[3][1] = zmin;
		quadx[3][2] = xmax;  quady[3][2] = ymin;  quadz[3][2] = zmin;
		quadx[3][3] = xmax;  quady[3][3] = ymin;  quadz[3][3] = zmax;
		// z top
		quadx[4][0] = xmin;  quady[4][0] = ymin;  quadz[4][0] = zmax; 
		quadx[4][1] = xmax;  quady[4][1] = ymin;  quadz[4][1] = zmax;
		quadx[4][2] = xmax;  quady[4][2] = ymax;  quadz[4][2] = zmax;
		quadx[4][3] = xmin;  quady[4][3] = ymax;  quadz[4][3] = zmax;
		// z down
		quadx[5][0] = xmax;  quady[5][0] = ymin;  quadz[5][0] = zmin; 
		quadx[5][1] = xmin;  quady[5][1] = ymin;  quadz[5][1] = zmin;
		quadx[5][2] = xmin;  quady[5][2] = ymax;  quadz[5][2] = zmin;
		quadx[5][3] = xmax;  quady[5][3] = ymax;  quadz[5][3] = zmin;
		
		// Define configuration of each quad's normal
		normx = new float[6];
		normy = new float[6];
		normz = new float[6];
		
		normx[0] = xmax; normy[0] =  0;   normz[0] =    0;
		normx[1] = xmin; normy[1] =  0;   normz[1] =    0;
		normx[2] =  0;   normy[2] = ymax; normz[2] =    0;
		normx[3] =  0;   normy[3] = ymin; normz[3] =    0;
		normx[4] =  0;   normy[4] =  0;   normz[4] = zmax;
		normx[5] =  0;   normy[5] =  0;   normz[5] = zmin;
		
		// Define quad intersections that generate an axe
		// axe{A}quads[i][q]
		// A = axe direction (X, Y, or Z)
		// i = axe id (0 to 4)
		// q = quad id (0 to 1: an intersection is made of two quads)
		int na = 4; // n axes per dimension
		int np = 2; // n points for an axe
		int nq = 2;
		int i;      // axe id

		axeXquads = new int[na][nq];
		axeYquads = new int[na][nq];
		axeZquads = new int[na][nq];
		
		i = 0; axeXquads[i][0] = 4; axeXquads[i][1] = 3; // quads making axe x0
		i = 1; axeXquads[i][0] = 3; axeXquads[i][1] = 5; // quads making axe x1
		i = 2; axeXquads[i][0] = 5; axeXquads[i][1] = 2; // quads making axe x2
		i = 3; axeXquads[i][0] = 2; axeXquads[i][1] = 4; // quads making axe x3
		i = 0; axeYquads[i][0] = 4; axeYquads[i][1] = 0; // quads making axe y0
		i = 1; axeYquads[i][0] = 0; axeYquads[i][1] = 5; // quads making axe y1
		i = 2; axeYquads[i][0] = 5; axeYquads[i][1] = 1; // quads making axe y2
		i = 3; axeYquads[i][0] = 1; axeYquads[i][1] = 4; // quads making axe y3		
		i = 0; axeZquads[i][0] = 3; axeZquads[i][1] = 0; // quads making axe z0
		i = 1; axeZquads[i][0] = 0; axeZquads[i][1] = 2; // quads making axe z1
		i = 2; axeZquads[i][0] = 2; axeZquads[i][1] = 1; // quads making axe z2
		i = 3; axeZquads[i][0] = 1; axeZquads[i][1] = 3; // quads making axe z3
		
		// Define configuration of 4 axe per dimension:
		//  axe{A}d[i][p], where
		//
		//  A = axe direction (X, Y, or Z)
		//  d = dimension (x coordinate, y coordinate or z coordinate)
		//  i = axe id (0 to 4)
		//  p = point id (0 to 1)
		//
		// Note: the points making an axe are from - to +
		//       (i.e. direction is given by p0->p1)

		axeXx = new float[na][np];
		axeXy = new float[na][np];
		axeXz = new float[na][np];	
		axeYx = new float[na][np];
		axeYy = new float[na][np];
		axeYz = new float[na][np];		
		axeZx = new float[na][np];
		axeZy = new float[na][np];
		axeZz = new float[na][np];
		
		i = 0; // axe x0
		axeXx[i][0] = xmin; axeXy[i][0] = ymin; axeXz[i][0] = zmax;
		axeXx[i][1] = xmax; axeXy[i][1] = ymin; axeXz[i][1] = zmax;
		i = 1; // axe x1
		axeXx[i][0] = xmin; axeXy[i][0] = ymin; axeXz[i][0] = zmin;
		axeXx[i][1] = xmax; axeXy[i][1] = ymin; axeXz[i][1] = zmin;
		i = 2; // axe x2
		axeXx[i][0] = xmin; axeXy[i][0] = ymax; axeXz[i][0] = zmin;
		axeXx[i][1] = xmax; axeXy[i][1] = ymax; axeXz[i][1] = zmin;
		i = 3; // axe x3
		axeXx[i][0] = xmin; axeXy[i][0] = ymax; axeXz[i][0] = zmax;
		axeXx[i][1] = xmax; axeXy[i][1] = ymax; axeXz[i][1] = zmax;		
		i = 0; // axe y0
		axeYx[i][0] = xmax; axeYy[i][0] = ymin; axeYz[i][0] = zmax;
		axeYx[i][1] = xmax; axeYy[i][1] = ymax; axeYz[i][1] = zmax;
		i = 1; // axe y1
		axeYx[i][0] = xmax; axeYy[i][0] = ymin; axeYz[i][0] = zmin;
		axeYx[i][1] = xmax; axeYy[i][1] = ymax; axeYz[i][1] = zmin;
		i = 2; // axe y2
		axeYx[i][0] = xmin; axeYy[i][0] = ymin; axeYz[i][0] = zmin;
		axeYx[i][1] = xmin; axeYy[i][1] = ymax; axeYz[i][1] = zmin;
		i = 3; // axe y3
		axeYx[i][0] = xmin; axeYy[i][0] = ymin; axeYz[i][0] = zmax;
		axeYx[i][1] = xmin; axeYy[i][1] = ymax; axeYz[i][1] = zmax;		
		i = 0; // axe z0
		axeZx[i][0] = xmax; axeZy[i][0] = ymin; axeZz[i][0] = zmin;
		axeZx[i][1] = xmax; axeZy[i][1] = ymin; axeZz[i][1] = zmax;
		i = 1; // axe z1
		axeZx[i][0] = xmax; axeZy[i][0] = ymax; axeZz[i][0] = zmin;
		axeZx[i][1] = xmax; axeZy[i][1] = ymax; axeZz[i][1] = zmax;
		i = 2; // axe z2
		axeZx[i][0] = xmin; axeZy[i][0] = ymax; axeZz[i][0] = zmin;
		axeZx[i][1] = xmin; axeZy[i][1] = ymax; axeZz[i][1] = zmax;
		i = 3; // axe z3
		axeZx[i][0] = xmin; axeZy[i][0] = ymin; axeZz[i][0] = zmin;
		axeZx[i][1] = xmin; axeZy[i][1] = ymin; axeZz[i][1] = zmax;
		
		layout.getXTicks(xmin, xmax); // prepare ticks to display in the layout tick buffer
		layout.getYTicks(ymin, ymax);
		layout.getZTicks(zmin, zmax);
		/*setXTickMode(TICK_REGULAR, 3);5
		setYTickMode(TICK_REGULAR, 3);5
		setZTickMode(TICK_REGULAR, 5);6*/
	}
	
	
	
	/******************************************************************/
	/**                    DRAW AXEBOX ELEMENTS                      **/
	
	/**
	 * Make all GL2 calls allowing to build a cube with 6 separate quads.
	 * Each quad is indexed from 0.0f to 5.0f using glPassThrough,
	 * and may be traced in feedback mode when mode=GL2.GL_FEEDBACK 
	 */
	protected void drawCube(GL2 gl, int mode){
		for(int q=0; q<6; q++){
			if(mode==GL2.GL_FEEDBACK)
				gl.glPassThrough((float)q);
			gl.glBegin(GL2.GL_QUADS);
				for(int v=0; v<4; v++){
					gl.glVertex3f( quadx[q][v], quady[q][v], quadz[q][v]);
				}
			gl.glEnd();
		}
	}
	
	/**
	 * Draw a grid on the desired quad.
	 */
	protected void drawGridOnQuad(GL2 gl, int quad){
		// Draw X grid along X axis
		if((quad!=0)&&(quad!=1)){
			double[] xticks = layout.getXTicks();
			for(int t=0; t<xticks.length; t++){
				gl.glBegin(GL2.GL_LINES);
					gl.glVertex3d( xticks[t], quady[quad][0], quadz[quad][0]);
					gl.glVertex3d( xticks[t], quady[quad][2], quadz[quad][2]);
				gl.glEnd();
			}
		}
		// Draw Y grid along Y axis
		if((quad!=2)&&(quad!=3)){
		    double[] yticks = layout.getYTicks();
			for(int t=0; t<yticks.length; t++){
				gl.glBegin(GL2.GL_LINES);
					gl.glVertex3d( quadx[quad][0], yticks[t], quadz[quad][0]);
					gl.glVertex3d( quadx[quad][2], yticks[t], quadz[quad][2]);
				gl.glEnd();
			}
		}
		// Draw Z grid along Z axis
		if((quad!=4)&&(quad!=5)){
		    double[] zticks = layout.getZTicks();
			for(int t=0; t<zticks.length; t++){
				gl.glBegin(GL2.GL_LINES);
					gl.glVertex3d( quadx[quad][0], quady[quad][0], zticks[t]);
					gl.glVertex3d( quadx[quad][2], quady[quad][2], zticks[t]);
				gl.glEnd();
			}
		}
	}
    
    /*
     * Draws ticks on the given direction, by using the desired axe. 
     */
	protected BoundingBox3d drawTicks(GL2 gl, GLU glu, Camera cam, int axis, int direction, Color color){
		return drawTicks(gl, glu, cam, axis, direction, color, null, null);
	}
	
	protected BoundingBox3d drawTicks(GL2 gl, GLU glu, Camera cam, int axis, int direction, Color color, Halign hal, Valign val){
		int quad_0; 
		int quad_1;
		Halign hAlign;
		Valign vAlign;
		float tickLength = 20.0f; // with respect to range
		float axeLabelDist = 2.5f;
		BoundingBox3d ticksTxtBounds = new BoundingBox3d();
		
		// Retrieve the quads that intersect and create the selected axe
		if(direction==AXE_X){
			quad_0 = axeXquads[axis][0];
			quad_1 = axeXquads[axis][1];
		}
		else if(direction==AXE_Y){
			quad_0 = axeYquads[axis][0];
			quad_1 = axeYquads[axis][1];
		}
		else{ //(axis==AXE_Z)
			quad_0 = axeZquads[axis][0];
			quad_1 = axeZquads[axis][1];
		}
		
		// Computes POSition of ticks lying on the selected axe 
		// (i.e. 1st point of the tick line)
		double xpos = normx[quad_0] + normx[quad_1];
		double ypos = normy[quad_0] + normy[quad_1];
		double zpos = normz[quad_0] + normz[quad_1];
		
		// Variables for storing the position of the LABel position
		// (2nd point on the tick line)
		double xlab;
		double ylab;
		double zlab;

		// Computes the DIRection of the ticks
		// assuming initial vector point is the center 
		float xdir = ( normx[quad_0] + normx[quad_1] ) - center.x;
		float ydir = ( normy[quad_0] + normy[quad_1] ) - center.y;
		float zdir = ( normz[quad_0] + normz[quad_1] ) - center.z; 
		xdir = xdir==0?0:xdir/Math.abs(xdir); // so that direction as length 1
		ydir = ydir==0?0:ydir/Math.abs(ydir);
		zdir = zdir==0?0:zdir/Math.abs(zdir);
		
		// Draw the label for axis
		String axeLabel;
		int dist = 1;
		if(direction==AXE_X){ 
			xlab  = center.x;
			ylab  = axeLabelDist*(yrange/tickLength)*dist*ydir + ypos;
			zlab  = axeLabelDist*(zrange/tickLength)*dist*zdir + zpos;
			axeLabel = layout.getXAxeLabel();
		}else if(direction==AXE_Y){
			xlab  = axeLabelDist*(xrange/tickLength)*dist*xdir + xpos;
			ylab  = center.y;
			zlab  = axeLabelDist*(zrange/tickLength)*dist*zdir + zpos;
			axeLabel = layout.getYAxeLabel();
		}else{ 
			xlab  = axeLabelDist*(xrange/tickLength)*dist*xdir + xpos;
			ylab  = axeLabelDist*(yrange/tickLength)*dist*ydir + ypos;
			zlab  = center.z;
			axeLabel = layout.getZAxeLabel();
		}
		
		if( (direction==AXE_X && layout.isXAxeLabelDisplayed())
		 || (direction==AXE_Y && layout.isYAxeLabelDisplayed())
		 || (direction==AXE_Z && layout.isZAxeLabelDisplayed()) ){
			Coord3d labelPosition = new Coord3d(xlab, ylab, zlab);
			if(txtRenderer!=null)
				txtRenderer.appendText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);
			else{
				BoundingBox3d labelBounds = txt.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, color);		
				if(labelBounds!=null)
				    ticksTxtBounds.add( labelBounds );
			}
		}
		
		// Retrieve the selected tick positions
		double ticks[];
		if(direction==AXE_X) 
			ticks = layout.getXTicks();
		else if(direction==AXE_Y) 
			ticks = layout.getYTicks();
		else //(axis==AXE_Z) 
			ticks = layout.getZTicks();
		
		// Draw the ticks, labels, and dotted lines iteratively
		String tickLabel = "";		
		gl.glColor3f(color.r, color.g, color.b);
		gl.glLineWidth(1);
		
		for(int t=0; t<ticks.length; t++){
			// Shift the tick vector along the selected axis
			// and set the tick length
			if(direction==AXE_X){
				xpos  = ticks[t];
				xlab  = xpos;
				ylab  = (yrange/tickLength)*ydir + ypos;
				zlab  = (zrange/tickLength)*zdir + zpos;
				tickLabel = layout.getXTickRenderer().format(xpos);
			}
			else if(direction==AXE_Y){
				ypos  = ticks[t];
				xlab  = (xrange/tickLength)*xdir + xpos;
				ylab  = ypos;
				zlab  = (zrange/tickLength)*zdir + zpos;
				tickLabel = layout.getYTickRenderer().format(ypos);
			}
			else{ //(axis==AXE_Z)
				zpos  = ticks[t];
				xlab  = (xrange/tickLength)*xdir + xpos;
				ylab  = (yrange/tickLength)*ydir + ypos;
				zlab  = zpos;
				tickLabel = layout.getZTickRenderer().format(zpos);
			}
			Coord3d tickPosition = new Coord3d(xlab, ylab, zlab);
			
			// Draw the tick line
			gl.glBegin(GL2.GL_LINES);
				gl.glVertex3d( xpos, ypos, zpos ); 
				gl.glVertex3d( xlab, ylab, zlab ); 			
			gl.glEnd();
			
			// Select the alignement of the tick label
			if(hal==null)
				hAlign = cam.side(tickPosition)?Halign.LEFT:Halign.RIGHT;
			else
				hAlign = hal;
			
			if(val==null){
				if(direction==AXE_Z)
					vAlign = Valign.CENTER;
				else{
					if(zdir>0)
						vAlign = Valign.TOP;
					else
						vAlign = Valign.BOTTOM;
				}
			}
			else
				vAlign = val;
			
			// Draw the text label of the current tick
			if(txtRenderer!=null)
				txtRenderer.appendText(gl, glu, cam, tickLabel, tickPosition, hAlign, vAlign, color);
			else{
				BoundingBox3d tickBounds = txt.drawText(gl, glu, cam, tickLabel, tickPosition, hAlign, vAlign, color);
				if(tickBounds!=null)
				    ticksTxtBounds.add( tickBounds );
			}
		}
		
		return ticksTxtBounds;
	}

	
    
	/******************************************************************/
	/**                    AXIS SELECTION                            **/
	
    /**
     * Selects the closest displayable X axe from camera
     */
    protected int findClosestXaxe(Camera cam){
    	int na = 4;
    	double [] distAxeX = new double[na];
		
    	// keeps axes that are not at intersection of 2 quads
		for(int a=0; a<na; a++){
			if(quadIsHidden[axeXquads[a][0]] ^ quadIsHidden[axeXquads[a][1]])
				distAxeX[a] = new Vector3d(axeXx[a][0], axeXy[a][0], axeXz[a][0], 
                						   axeXx[a][1], axeXy[a][1],  axeXz[a][1]
                					).distance(cam.getEye());
			else
				distAxeX[a] = Double.MAX_VALUE;
		}
		
		// prefers the lower one
		for(int a=0; a<na; a++){
			if(distAxeX[a] < Double.MAX_VALUE){
				if(center.z > (axeXz[a][0]+axeXz[a][1])/2)
					distAxeX[a] *= -1;
			}
		}
		
		return min(distAxeX);
    }
    
    /**
     * Selects the closest displayable Y axe from camera
     */
    protected int findClosestYaxe(Camera cam){
    	int na = 4;
    	double [] distAxeY = new double[na];
		
    	// keeps axes that are not at intersection of 2 quads
		for(int a=0; a<na; a++){
			if(quadIsHidden[axeYquads[a][0]] ^ quadIsHidden[axeYquads[a][1]])
				distAxeY[a] = new Vector3d(axeYx[a][0], axeYy[a][0], axeYz[a][0], 
                						   axeYx[a][1], axeYy[a][1], axeYz[a][1]
				                          ).distance(cam.getEye());
			else
				distAxeY[a] = Double.MAX_VALUE;
		}
		
		// prefers the lower one
		for(int a=0; a<na; a++){
			if(distAxeY[a] < Double.MAX_VALUE){
				if(center.z > (axeYz[a][0]+axeYz[a][1])/2)
					distAxeY[a] *= -1;
			}
		}
		
		return min(distAxeY);
    }
        
    /**
     * Selects the closest displayable Z axe from camera
     */
    protected int findClosestZaxe(Camera cam){
    	int na = 4;
    	double [] distAxeZ = new double[na];
		
    	// keeps axes that are not at intersection of 2 quads
		for(int a=0; a<na; a++){
			if(quadIsHidden[axeZquads[a][0]] ^ quadIsHidden[axeZquads[a][1]])
				distAxeZ[a] = new Vector3d(axeZx[a][0], axeZy[a][0], axeZz[a][0], 
						                   axeZx[a][1], axeZy[a][1], axeZz[a][1]
                                          ).distance(cam.getEye());
			else
				distAxeZ[a] = Double.MAX_VALUE;
		}
		
		// prefers the right one
		for(int a=0; a<na; a++){
			if(distAxeZ[a] < Double.MAX_VALUE){
				Coord3d axeCenter = new Coord3d((axeZx[a][0]+axeZx[a][1])/2, 
						                        (axeZy[a][0]+axeZy[a][1])/2, 
						                        (axeZz[a][0]+axeZz[a][1])/2 );
				if(!cam.side(axeCenter))
					distAxeZ[a] *= -1;
			}
		}
		
		return min(distAxeZ);
    }
    
	/** Return the index of the minimum value contained in the input array of doubles.
	 * If no value is smaller than Double.MAX_VALUE, the returned index is -1.*/
	protected int min(double [] values){
		double minv = Double.MAX_VALUE;
		int   index = -1;
		
		for( int i=0; i<values.length; i++ )
			if( values[i] < minv ){
				minv = values[i];
				index = i;
			}
		return index;
	}
    
	/******************************************************************/
    /**                COMPUTATION OF HIDDEN QUADS                   **/
    
	/** Computes the visibility of each cube face. */
	protected boolean [] getHiddenQuads(GL gl, Camera cam){
        boolean [] status = new boolean[6];
        
        Coord3d se = cam.getEye().div(scale);

        if(se.x <= center.x){
            status[0] = false;
            status[1] = true;
        }
        else{
            status[0] = true;
            status[1] = false;
        }
        if(se.y <= center.y){
            status[2] = false;
            status[3] = true;
        }
        else{
            status[2] = true;
            status[3] = false;
        }
        if(se.z <= center.z){
            status[4] = false;
            status[5] = true;
        }
        else{
            status[4] = true;
            status[5] = false;
        }
        return status;
    }
	
	/******************************************************************/

	/**
	 *  Print out parameters of a gl call in 3dColor mode.
	 */
    protected int print3DcolorVertex(int size, int count, float[] buffer) {
        int i;
        int id = size - count;
        int veclength = 7;
        
        System.out.print("  [" + id + "]");
        for (i = 0; i < veclength; i++) {
            System.out.print(" " + buffer[size - count]);
            count = count - 1;
        }
        System.out.println();
        return count;
    }
	
	/**
	 * Print out display status of quads.
	 */
	protected void printHiddenQuads(){
		for(int t=0; t<quadIsHidden.length; t++)
			if(quadIsHidden[t])
				System.out.println("Quad[" + t + "] is not displayed");
			else
				System.out.println("Quad[" + t + "] is displayed");
	}
	
	/******************************************************************/
	
    protected static final int PRECISION = 6;
    
    protected View view;
    
    protected ITextRenderer txt = new TextBitmapRenderer();	// use this text renderer to get occupied volume by text
	protected TextOverlay  txtRenderer;	// keep it null in order to not use it
	//protected TextBillboard txt = new TextBillboard();	
	
	protected IAxeLayout layout;
	
	protected BoundingBox3d boxBounds;
	protected BoundingBox3d wholeBounds;
	protected Coord3d       center;
	protected Coord3d       scale;
	
	protected float   xrange;
	protected float   yrange;
	protected float   zrange;
		
	protected float   quadx[][];
	protected float   quady[][];
	protected float   quadz[][];
	
	protected float   normx[];
	protected float   normy[];
	protected float   normz[];
	
	protected float   axeXx[][];
	protected float   axeXy[][];
	protected float   axeXz[][];
	protected float   axeYx[][];
	protected float   axeYy[][];
	protected float   axeYz[][];
	protected float   axeZx[][];
	protected float   axeZy[][];
	protected float   axeZz[][];
	
	protected int     axeXquads[][];
	protected int     axeYquads[][];
	protected int     axeZquads[][];
	
	protected boolean quadIsHidden[];
	
	protected static final int AXE_X = 0;
	protected static final int AXE_Y = 1;
	protected static final int AXE_Z = 2;
}
