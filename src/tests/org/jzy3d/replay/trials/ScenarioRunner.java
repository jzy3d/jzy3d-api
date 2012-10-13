package org.jzy3d.replay.trials;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jzy3d.replay.recorder.ScenarioFiles;

public class ScenarioRunner {
	public static void main(String[] args){
		testAll();
	}
	
	public static void testAll(){
		File f = new File(ScenarioFiles.SCENARIO_FOLDER);
		File[] allfiles = f.listFiles();
		File[] files = f.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File arg0, String arg1) {
				Matcher isEventFile = eventFilePattern.matcher(arg1);
				if(isEventFile.matches()){
					return true;
				}
				return false;
			}
			
		});
		
		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i]);
		}
	}
	
	static protected Pattern eventFilePattern = Pattern.compile(".*" + ScenarioFiles.FILE_EVENTS);
}
