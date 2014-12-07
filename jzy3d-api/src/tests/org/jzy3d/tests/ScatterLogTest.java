package org.jzy3d.tests;


import java.util.Random;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.AxeTransformableAWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.AxeTransformableScatter;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformerSet;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.LogAxeTransformer;
import org.jzy3d.plot3d.rendering.canvas.Quality;


public class ScatterLogTest extends AbstractAnalysis{

		public static void main(String[] args) throws Exception {
			AnalysisLauncher.open(new ScatterLogTest());
		}
		
		AxeTransformerSet transformers = new AxeTransformerSet(/*null, new LogAxeTransformer(),null*/);
		
		public void init(){
	        int size = 500000;
	        float x;
	        float y;
	        float z;
	        float a;
	        
	        Coord3d[] points = new Coord3d[size];
	        Color[]   colors = new Color[size];
	        
	        Random r = new Random();
	        r.setSeed(0);
	        
	        for(int i=0; i<size; i++){
	            x = r.nextFloat() + 0.1f;
	            y = r.nextFloat() + 0.1f;
	            z = r.nextFloat() + 0.1f;
	            points[i] = new Coord3d(x, y, z);
	            a = 0.25f;
	            colors[i] = new Color(x, y, z, a);
	        }
	        
	        AxeTransformableScatter scatter = new AxeTransformableScatter(points, colors, transformers);
	        chart = AxeTransformableAWTChartComponentFactory.chart(Quality.Advanced, "awt", transformers);
	        chart.getView().setTransformers(transformers);
	        chart.getScene().add(scatter);
	    }
	}

