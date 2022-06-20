package org.jzy3d.tests.manual.layout;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;

/**
 * QTP
 * valider avec / sans HiDPI : 
 * - le layout du texte est le même
 * - les marges / offset sont appliquées correctement
 * - marche côté gauche / côté droit
 * - avec vertical / oblique / etc
 * 
 * - Le calcul d'offset de label ne prend pas en compte la rotation du texte dans la place qu'il prend
 * 
 * @author martin
 *
 */
public class MTest_Layout_Native {

  static final float ALPHA_FACTOR = 0.55f;// .61f;

  public static void main(String[] args) {
    MTest_Layout d = new MTest_Layout();
    d.setFactory(new AWTChartFactory());
    d.init();
    
    Chart chart = d.getChart();
    chart.open(800, 600);
  }


}
