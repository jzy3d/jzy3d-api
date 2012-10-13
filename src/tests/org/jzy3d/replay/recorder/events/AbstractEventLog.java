package org.jzy3d.replay.recorder.events;

public class AbstractEventLog implements IEventLog{

	protected long since;

	@Override
	public long since() {
		return since;
	}

	public AbstractEventLog() {
		super();
	}

}