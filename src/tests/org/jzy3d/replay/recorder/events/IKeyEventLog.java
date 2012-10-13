package org.jzy3d.replay.recorder.events;

public interface IKeyEventLog extends IEventLog{
	public KeyEventType getType();
	public int getKeyCode();
	
	public enum KeyEventType{
		KEY_PRESS,
		KEY_RELEASE,
		KEY_TYPED
	}
}
