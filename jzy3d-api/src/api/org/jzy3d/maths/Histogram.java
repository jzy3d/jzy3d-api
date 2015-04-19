package org.jzy3d.maths;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
public class Histogram<X,V> {
    Map<X,V> data;
    
    public Histogram(){
        data = new HashMap<X,V>();
    }

    public static void main(String[] args) {
        Histogram<Integer, Integer> h = new Histogram<Integer, Integer>();
        h.put(20, 3);
        h.put(30, 5);
        h.put(40, 15);
        h.put(50, 4);
        h.console();

    
        
        float min = 0;
        float max = 1;
        int bins = 20;
        
        Histogram<Range, Float> h2 = new Histogram<Range, Float>();

    }

    public void put(X x,V value){
        data.put(x, value);
    }
    
    private void console(){
        SortedSet<X> xs = new TreeSet<X>(data.keySet());
        for(X x : xs){
            V value = data.get(x);
            consoleKeyValue(x, value);
        }
    }

    private void consoleKeyValue(X x, V value) {
        consoleKey(x);
        consoleValue(value);
        System.out.println();
    }

    private void consoleKey(X key) {
        System.out.print(key + " : ");
    }

    private void consoleValue(V value) {
        System.out.print(value);
    }
}