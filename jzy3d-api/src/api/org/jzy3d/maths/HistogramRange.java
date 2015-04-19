package org.jzy3d.maths;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HistogramRange {
    protected Range[] ranges ;
    protected Map<Range,Integer> data;
    
    public HistogramRange(float min, float max, int bins){
        initBins(min, max, bins);
    }

    private void initBins(float min, float max, int bins) {
        data = new HashMap<Range,Integer>(bins);
        ranges = new Range[bins];
        
        
        float step = (max-min)/bins;
        float rmin = min;
        for (int i = 0; i < bins - 1; i++) {
            ranges[i] = new Range(rmin, rmin + step);
            data.put(ranges[i], 0);
            rmin = rmin + step;
        }
        ranges[bins-1] = new Range(rmin, max);
        data.put(ranges[bins-1], 0);
    }
    
    public void add(float value){
        for(Entry<Range,Integer> e: data.entrySet()){
            Range r = e.getKey();
            if(r.isIn(value)){
                e.setValue(e.getValue()+1);
                return;
            }
        }
        throw new IllegalArgumentException("value could not be added to any pre-configured bin. Are you adding a value out of the min-max range you used to build " + HistogramRange.class.getSimpleName() + "?");
    }
    
    public Range[] ranges(){
        return ranges;
    }

    public int getCount(int bin) {
        return data.get(ranges[bin]);
    }
    
    public void setCount(int bin, int value) {
        data.put(ranges[bin], value);
    }

    public void console() {
        for (int i = 0; i < ranges.length; i++) {
            System.out.println(ranges[i] + " : " + data.get(ranges[0]));
        }
    }

    
    public int computeMaxCount() {
        int max = Integer.MIN_VALUE;
        for(Entry<Range,Integer> e : data.entrySet()){
            int v = e.getValue();
            if(v>max)
                max = v;
        }
        return max;
    }
}
