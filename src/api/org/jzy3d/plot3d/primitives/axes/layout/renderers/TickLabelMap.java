package org.jzy3d.plot3d.primitives.axes.layout.renderers;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link ITickRenderer} that can store a list of labels for given axis values.
 * @author Martin Pernollet
 */
public class TickLabelMap implements ITickRenderer{
    public void register(float value, String string){
        tickValues.put(value, string);
    }
    
    public boolean contains(float value){
        return tickValues.containsKey(value);
    }
    
    public Map<Float, String> getMap(){
        return tickValues;
    }
    
    @Override
    public String format(float value) {
        if( tickValues.get(value) != null )
            return tickValues.get(value);
        else
            return "";
    }
    
    protected Map<Float, String> tickValues = new HashMap<Float, String>();    
}
