package org.jzy3d.plot3d.rendering.legends;

import java.awt.Image;
import java.io.IOException;

import org.jzy3d.chart.ChartView;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.events.IDrawableListener;
import org.jzy3d.io.FileImage;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.rendering.view.ImageViewport;



/** A {@link Legend} represent information concerning a {@link AbstractDrawable} that may be
 * displayed as a metadata in the {@link ChartView}.
 * 
 * The constructor of a {@link Legend} registers itself as listener of its
 * parent {@link AbstractDrawable}, and unregister itself when it is disposed.
 * 
 * When defining a concrete {@link Legend}, one should:
 * <ul>
 * <li>override the {@link toImage(int width, int height)} method, that defines the picture representation.
 * <li>override the {@link drawableChanged(DrawableChangedEvent e)} method, that must select events that actually triggers an image update.
 * </ul>
 * 
 * Last, a {@link Legend} optimizes rendering by :
 * <ul>
 * <li>storing current image dimension,
 * <li>computing a new image only if the required {@link Legend} dimensions changed.
 * </ul>
 * 
 * @author Martin Pernollet
 */
public abstract class Legend extends ImageViewport implements IDrawableListener{

	public Legend(AbstractDrawable parent){
		this.parent = parent;
		if(parent!=null)
			parent.addDrawableListener(this);
	}

	public void dispose(){
		if(parent!=null)
			parent.removeDrawableListener(this);
	}

	/****************************************************************/

	public abstract Image toImage(int width, int height);

	public abstract void drawableChanged(DrawableChangedEvent e);

	/****************************************************************/

	/** Defines viewport dimensions, and precompute an image if required (i.e. if
	 * the viewport dimension have changed
	 */
	public void setViewPort(int width, int height, float left, float right){
		super.setViewPort(width, height, left, right);

		int imgWidth  = (int)(width*(right-left));

		if(imageWidth!=imgWidth || imageHeight!=height)
			setImage(toImage(imgWidth, height));
	}

	/** Recompute the picture, using last used dimensions.*/
	public void updateImage(){
		setImage(toImage(imageWidth, imageHeight));
	}
	
	public void saveImage(){
		try {
			FileImage.savePNG(imageObj, "data/colorbar.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/****************************************************************/

	protected AbstractDrawable parent;
}
