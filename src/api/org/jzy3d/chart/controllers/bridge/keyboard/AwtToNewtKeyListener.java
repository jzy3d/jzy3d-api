package org.jzy3d.chart.controllers.bridge.keyboard;

import com.jogamp.newt.event.KeyEvent;
import java.awt.Component;
import org.jzy3d.chart.controllers.bridge.AwtToNewtUtilities;

/**
 *
 * @author Nils Hoffmann
 */
public class AwtToNewtKeyListener implements com.jogamp.newt.event.KeyListener {

	private final java.awt.event.KeyListener keyListener;
	private final java.awt.Component source;

	public AwtToNewtKeyListener(Component source, java.awt.event.KeyListener keyListener) {
		this.source = source;
		this.keyListener = keyListener;
	}

	protected java.awt.event.KeyEvent convertEvent(KeyEvent event, int eventId) {
		return new java.awt.event.KeyEvent(source, eventId, event.getWhen(), AwtToNewtUtilities.mask(event), AwtToNewtUtilities.mapKeyCode(event), event.getKeyChar());
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		keyListener.keyPressed(convertEvent(ke, java.awt.event.KeyEvent.KEY_PRESSED));
	}

	@Override
	public void keyReleased(KeyEvent ke) {
		keyListener.keyReleased(convertEvent(ke, java.awt.event.KeyEvent.KEY_RELEASED));
	}

	@Override
	public void keyTyped(KeyEvent ke) {
		keyListener.keyTyped(convertEvent(ke, java.awt.event.KeyEvent.KEY_TYPED));
	}
	
}
