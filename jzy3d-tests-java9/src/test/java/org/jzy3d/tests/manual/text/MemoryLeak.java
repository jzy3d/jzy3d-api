package org.jzy3d.tests.manual.text;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Vector3d;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.CoplanarityManager;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.rendering.legends.overlay.Legend;
import org.jzy3d.plot3d.rendering.legends.overlay.OverlayLegendRenderer;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.drawable.DrawableText;
import org.jzy3d.plot3d.text.renderers.TextRenderer;;

public class MemoryLeak {

  public static void main(String[] args) {
    AWTChartFactory f = new AWTChartFactory();
    AWTChart chart = f.newChart();
    
    Composite prodVecInfo = new Composite();
    
    Composite prodScalInfo = new Composite();
    
    Composite productInfo = new Composite(prodVecInfo, prodScalInfo);



    Vector3d v1 = new Vector3d(1, 0, 0);
    Vector3d v2 = new Vector3d(0, 1.5, 0);
    Vector3d v3 = new Vector3d(v1.cross(v2));
    Vector3d v4 = new Vector3d(v1.cross(v3));

    LineStrip ln1 = new LineStrip(Color.BLUE, v1.coords());
    LineStrip ln2 = new LineStrip(Color.GREEN, v2.coords());
    LineStrip ln3 = new LineStrip(Color.RED, v3.coords());
    LineStrip ln4 = new LineStrip(Color.GRAY, v4.coords());

    DrawableText t1 = new DrawableText("v1", v1.coord2(), ln1.getColor());
    DrawableText t2 = new DrawableText("v2", v2.coord2(), ln2.getColor());
    DrawableText t3 = new DrawableText("v3=v1 x v2 | v1.v2=" + v1.dot(v2), v3.coord2(), ln3.getColor());
    DrawableText t4 = new DrawableText("v4=v1 x (v1 x v2)", v4.coord2(), ln4.getColor());

    Horizontal h = Horizontal.RIGHT;
    t2.setHalign(h);
    t3.setHalign(h);
    t4.setHalign(h);
    
    boolean fallbackGLUT = false;
    
    t1.getRenderer().setUseGlutBitmap(fallbackGLUT);
    t2.getRenderer().setUseGlutBitmap(fallbackGLUT);
    t3.getRenderer().setUseGlutBitmap(fallbackGLUT);
    t4.getRenderer().setUseGlutBitmap(fallbackGLUT);
    ((TextRenderer)chart.getView().getAxis().getTextRenderer()).setUseGlutBitmap(fallbackGLUT);

    
    boolean cover = true;
    
    if(cover) {
      List<Drawable> d = new ArrayList<>();
      d.add(ln1);
      d.add(ln2);
      d.add(ln3);
      d.add(ln4);
  
      d.add(t1);
      d.add(t2);
      d.add(t3);
      d.add(t4);
      
      CoplanarityManager cop = new CoplanarityManager(d, productInfo);
      chart.add(cop);
      
    }
    else {
      
      chart.add(prodVecInfo);
      chart.add(prodScalInfo);
       
      chart.add(ln1);
      chart.add(ln2);
      chart.add(ln3);
      chart.add(ln4);

      chart.add(t1);
      chart.add(t2);
      chart.add(t3);
      chart.add(t4);
      
    }
    
    Legend info = new Legend("v1.v2=" + v1.dot(v2), null);
    OverlayLegendRenderer legend = new OverlayLegendRenderer(info);
    legend.getLayout().setBorderColor(null);

    chart.add(legend);


    chart.getView().setBoundManual(new BoundingBox3d(-2, 2, -2, 2, -2, 2));
    chart.open("Produit scalaire");
    chart.addMouse();


    float angle = 2;//degree  //(float) Math.PI / 10;
    Coord3d axis = new Coord3d(0, 0, 1);

    int k = 0;
    int kmax = -1;//(int)(1.85*360/angle)+1;
    
    int pause = 10;
    
    

    boolean loop = true;
    
    while (loop) {
      try {
        // rotate second vector a bit
        v2 = new Vector3d(v2.coord2().rotate(angle, axis));

        // update scalar product
        v3 = new Vector3d(v1.cross(v2));
        v4 = new Vector3d(v1.cross(v3));

        // update representation
        ln2.get(1).xyz = v2.coord2();
        ln3.get(1).xyz = v3.coord2();
        ln4.get(1).xyz = v4.coord2();

        t2.setPosition(v2.coord2());
        t3.setPosition(v3.coord2());
        t4.setPosition(v4.coord2());
        
        t3.setText("v3=v1 x v2 | v1.v2=" + v1.dot(v2));
        //t3.setText("v3=v1 x v2 | v1.v2=" + v1.dot(v2));
        
        info.setLabel("Dot product v1.v2=" + v1.dot(v2));
        
        
        
        Coord3d pvOrientation = new Coord3d();
        pvOrientation.x = v2.coord2().x;
        pvOrientation.y = v2.coord2().y;
        pvOrientation.z = v1.cross(v2).z;
        
        LineStrip vi = new LineStrip(Color.CYAN, Coord3d.ORIGIN, pvOrientation);

        
        Coord3d scOrientation = new Coord3d();
        scOrientation.x = v2.coord2().x;
        scOrientation.y = v2.coord2().y;
        scOrientation.z = v1.dot(v2);
        
        LineStrip si = new LineStrip(Color.YELLOW, Coord3d.ORIGIN, scOrientation);

        
        
        if(k<(360/angle)) {
          prodVecInfo.add(vi);
          prodScalInfo.add(si);
        }

        k++;
        System.out.print(".");
        if(k==3023)
          System.out.println("x");
          
        chart.render();
        chart.sleep(pause);
        System.out.print("_");
        

        
        if (k % 50 == 0) {
          System.out.print(k + "\n" + k / 100);
        }
        
        if(kmax>0 && k>kmax)
          break;
      } 
      
      catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

}
