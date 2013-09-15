package org.jzy3d.bridge.newt.controllers.keyboard;

import jogamp.newt.awt.event.AWTNewtEventFactory;

import com.jogamp.newt.Window;

/**
 *
 * @author cmh
 */
public class NewtToAWTKeyListener implements java.awt.event.KeyListener {

	private final com.jogamp.newt.event.KeyListener keyListener;
	private final com.jogamp.newt.Window source;

	public NewtToAWTKeyListener(Window source, com.jogamp.newt.event.KeyListener keyListener) {
		this.source = source;
		this.keyListener = keyListener;
	}

	protected com.jogamp.newt.event.KeyEvent convertEvent(java.awt.event.KeyEvent event) {
		return AWTNewtEventFactory.createKeyEvent(event, source);	
	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent ke) {
		keyListener.keyPressed(convertEvent(ke));
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent ke) {
		keyListener.keyReleased(convertEvent(ke));
	}

	@Override
	public void keyTyped(java.awt.event.KeyEvent ke) {
		keyListener.keyTyped(convertEvent(ke));
	}
	
}
