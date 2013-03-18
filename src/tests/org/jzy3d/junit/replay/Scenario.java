package org.jzy3d.junit.replay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.io.SimpleFile;
import org.jzy3d.junit.replay.events.EventParser;
import org.jzy3d.junit.replay.events.IEventLog;

public class Scenario {
	protected List<IEventLog> events;
	protected String name;
	
	public Scenario(String name){
		this.name = name;
		this.events = new ArrayList<IEventLog>();
	}
	
	public void register(IEventLog event){
		System.out.println(event);
		events.add(event);
	}
	
	public List<IEventLog> getEvents() {
		return events;
	}

	public void setEvents(List<IEventLog> events) {
		this.events = events;
	}

	public void save() throws Exception{
		save(ScenarioFiles.SCENARIO_FOLDER + name + "/", name);
	}

	protected void save(String folder) throws Exception{
		save(folder + "/" + name + "/", name);
	}
	
	protected void save(String folder, String name) throws Exception{
		File ff = new File(folder);
		if(!ff.exists())
			ff.mkdirs();
		saveEvents(folder, name);
	}

	protected void saveEvents(String folder, String name) throws Exception {
		StringBuilder sb = new StringBuilder();
		for(IEventLog log: events)
			sb.append(log.toString() + "\n");
		String file = getEventFile(folder, name);
		SimpleFile.write(sb.toString(), file);
		info("saved " + events.size() + " events in " + file);
	}

	public void info(String file) {
		System.out.println("---------------------------------------------------");
		System.out.println(file);
		System.out.println("---------------------------------------------------");
		//Logger.getLogger(this.getClass()).info("saved events: " + file);
	}

	public String getEventFile(String folder, String name) {
		return folder + name + ScenarioFiles.FILE_EVENTS;
	}
	
	/* */
	
	public void load() throws Exception{
		load(ScenarioFiles.SCENARIO_FOLDER, name);
	}
	
	protected void load(String folder, String name) throws IOException{
		loadEvents(folder, name);
	}
	
	protected void loadEvents(String folder, String name) throws IOException {
		events.clear();
		String file = getEventFile(folder, name);
		List<String> lines = SimpleFile.read(file);
		EventParser parser = new EventParser();
		
		for(String s: lines){
			//System.out.println("read : " + s);
			IEventLog event = parser.parse(s);
			if(event!=null){
				System.out.println("parsed : " + event);
				events.add(event);
			}
			else
				System.err.println("non parsable event : " + s);
		}
		
		info("parsed " + events.size() + " events from " + file);
	}

    public String getName() {
        return name;
    }
}
