package org.jzy3d.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** A class for holding measurements about something in a very loose way using a map */
public class Measure {
  protected long timestamp;
  protected Map<String, Object> observations;

  public Measure() {
    this(System.nanoTime(), new HashMap<>());
  }

  public Measure(Map<String, Object> observations) {
    this(System.nanoTime(), observations);
  }

  public Measure(long timestamp, Map<String, Object> observations) {
    super();
    this.timestamp = timestamp;
    this.observations = observations;
  }
  
  public long getTimestamp() {
    return timestamp;
  }

  
  public Set<String> getObservationsOrdered(){
    TreeSet<String> t = new TreeSet<>(getObservations());
    return t;
  }

  public Set<String> getObservations(){
    return this.observations.keySet();
  }

  public Set<Map.Entry<String, Object>> getEntries(){
    return this.observations.entrySet();
  }

  public static class CanvasPerfMeasure extends Measure {
    public CanvasPerfMeasure(int width, int heigth, int pixels, double d) {
      super();
      observations.put("width", width);
      observations.put("heigth", heigth);
      observations.put("pixels", pixels);
      observations.put("mili", d);
    }
    
    public double getMili() {
      return (double)observations.get("mili");
    }
    public int getPixels() {
      return (int)observations.get("pixels");
    }
  }
  
}
