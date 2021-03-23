package org.jzy3d.monitor;

import java.io.File;
import java.io.IOException;
import com.google.common.collect.ArrayListMultimap;

public abstract class Monitor {
  protected ArrayListMultimap<IMonitorable, Measure> observations;
  
  public Monitor() {
    observations = ArrayListMultimap.create();
  }

  public void add(IMonitorable monitorable, Measure observation) {
    observations.put(monitorable, observation);
  }
  
  public ArrayListMultimap<IMonitorable, Measure> getObservations(){
    return observations;
  }
  
  public abstract void dump(File file) throws IOException;
  public abstract void load(File file) throws IOException;

  public void dump(String file) throws IOException{
    dump(new File(file));
  }
  
  public void load(String file) throws IOException{
    load(new File(file));
  }
  

}
