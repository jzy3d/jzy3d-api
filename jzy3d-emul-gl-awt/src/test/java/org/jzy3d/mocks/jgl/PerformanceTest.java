package org.jzy3d.mocks.jgl;
import static org.mockito.Mockito.mock;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class PerformanceTest {

   @Test
   public void mockedInterface() {
      long start = System.nanoTime();
      //mock(Closeable.class);
      long end = System.nanoTime();

      System.out.println("Took " + TimeUnit.NANOSECONDS.toMillis(end - start) + "ms");
   }

}