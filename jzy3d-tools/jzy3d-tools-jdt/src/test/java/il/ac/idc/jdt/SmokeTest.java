package il.ac.idc.jdt;

import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class SmokeTest {
  @Test
  public void shouldRunOnExampleData() throws Exception {
    for (String data : Arrays.asList("t1_1000.tsin", "t1_5000.tsin", "il_1000.smf", "t1_5000.smf",
        "terra_13000.smf", "terra_13000.tsin")) {
      List<Point> points =
          IOParsers.readPoints(this.getClass().getResourceAsStream("/inputs/" + data));
      DelaunayTriangulation dt = new DelaunayTriangulation(points);
      File smfTemp = File.createTempFile("jdt-", ".smf");
      File tsinTemp = File.createTempFile("jdt-", ".tsin");

      try {
        IOParsers.exportSmf(dt.getTriangulation(), smfTemp);
        assertThat(data + " trangulation is correct for smf",
            IOUtils.contentEqualsIgnoreEOL(
                new InputStreamReader(FileUtils.openInputStream(smfTemp)), new InputStreamReader(
                    this.getClass().getResourceAsStream("/outputs/" + data + "_result.smf"))));
        IOParsers.exportTsin(dt, tsinTemp);
        assertThat(data + " trangulation is correct tsin",
            IOUtils.contentEqualsIgnoreEOL(
                new InputStreamReader(FileUtils.openInputStream(tsinTemp)), new InputStreamReader(
                    this.getClass().getResourceAsStream("/outputs/" + data + "_result.tsin"))));
      } catch (IOException e) {
        e.printStackTrace();
        Assert.fail("Failed on " + data);
      } finally {
        FileUtils.deleteQuietly(smfTemp);
        FileUtils.deleteQuietly(tsinTemp);
      }
    }
  }
}
