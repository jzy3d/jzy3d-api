package org.jzy3d.plot3d.primitives.axes.symbols;

import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.transform.Transform;

/**
 * Renders an image at the specified 3d position.
 * 
 * The equivalent of {@link DrawableTexture}.
 */
public class EmulGLDrawableImage extends Drawable{
	protected BufferedImage image;
	protected Coord3d position;
	
	public EmulGLDrawableImage(BufferedImage image) {
		this.image = image;
		this.position = Coord3d.ORIGIN.clone();
	}
	
	public EmulGLDrawableImage(BufferedImage image, Coord3d position) {
		this.image = image;
		this.position = position;
	}

	@Override
	public void draw(Painter painter) {
		if(painter instanceof EmulGLPainter) {
			EmulGLPainter emulgl = (EmulGLPainter) painter;
			
			Coord3d screenPosition = painter.getCamera().modelToScreen(painter, position);
			
			emulgl.getGL().appendImageToDraw(image, (int)screenPosition.x-image.getWidth()/2, (int)screenPosition.y-image.getHeight()/2);
		}
	}

	@Override
	public void applyGeometryTransform(Transform transform) {
        Logger.getLogger(EmulGLDrawableImage.class).warn("not implemented");
	}

	@Override
	public void updateBounds() {
        Logger.getLogger(EmulGLDrawableImage.class).warn("not implemented");
	}

	public Coord3d getPosition() {
		return position;
	}

	public void setPosition(Coord3d position) {
		this.position = position;
	}
}

