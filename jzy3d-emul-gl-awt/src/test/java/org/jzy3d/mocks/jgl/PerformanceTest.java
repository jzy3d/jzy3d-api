package org.jzy3d.mocks.jgl;
import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;


public class PerformanceTest {
//("Just to evaluate the cost of mocks")
//   @Test
   public void mockedInterface() {
      long start = System.nanoTime();
      //mock(Closeable.class);
      long end = System.nanoTime();

      System.out.println("Took " + TimeUnit.NANOSECONDS.toMillis(end - start) + "ms");
   }

}