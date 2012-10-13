package org.jzy3d.replay.recorder.events;

public interface IWindowEventLog extends IEventLog{
	public WindowEventType getType();
	public Object getValue();
	
	public enum WindowEventType{
		WINDOW_OPENED,
		WINDOW_CLOSING,
		WINDOW_CLOSED,
		WINDOW_MOVED,
	}
}
