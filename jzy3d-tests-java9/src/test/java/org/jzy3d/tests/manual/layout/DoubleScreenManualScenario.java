package org.jzy3d.tests.manual.layout;

import java.util.Scanner;
import org.jzy3d.chart.Chart;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.junit.NativePlatform;

public class DoubleScreenManualScenario {
  Chart chart;
  ChartTester tester;
  String baselinePath = "src/test/resources/manual-" + new NativePlatform().getLabel() + "/";
  String baselineName;

  public DoubleScreenManualScenario(Chart chart, String baselineName) {
    super();
    this.chart = chart;
    this.baselineName = baselineName;
    
    if(chart.getCanvas().isNative()) {
      tester = new NativeChartTester();      
    }
    else {
      tester = new ChartTester();
    }

    tester.setTestCaseInputFolder(baselinePath);
  }

  public void start() {
    chart.render(); // required for EmulGL to be complete
    tester.assertSimilar(chart, tester.path(baselineName + "-screen-1"));


    query("Move the frame to the secondary screen and hit Enter key");

    chart.render();
    tester.assertSimilar(chart, tester.path(baselineName + "-screen-2"));

    // DONE
    System.out.println("Test success!");

  }

  public static void query(String question) {
    System.out.println(question);

    Scanner in = new Scanner(System.in);
    String s = in.nextLine();
    in.close();

    System.out.println("Thanks!");

  }

}
