package org.jzy3d.maths;

import static org.junit.Assert.assertEquals;
import java.text.ParseException;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

public class TestUtils {
  @Test
  public void testElapsedTimeFormatter() {
    assertEquals("0:00:01.000", Utils.time2str(1000));
  }

  @Test
  public void dateInit() throws ParseException {
    Date d = Utils.str2date("03/20/1980");
    Assert.assertEquals(80, d.getYear());
    Assert.assertEquals(2, d.getMonth());
  }
}
