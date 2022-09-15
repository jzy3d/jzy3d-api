package org.jzy3d.os;

import org.junit.Assert;
import org.junit.Test;

public class TestOperatingSystem {
  @Test
  public void windows() {
    Assert.assertTrue(new OperatingSystem("Windows").isWindows());
    Assert.assertTrue(new OperatingSystem("Windows 10").isWindows());
    Assert.assertTrue(new OperatingSystem("Windows 11").isWindows());
    Assert.assertFalse(new OperatingSystem("Windows").isMac());
    Assert.assertFalse(new OperatingSystem("Windows").isUnix());
    Assert.assertFalse(new OperatingSystem("Windows").isSolaris());

    Assert.assertTrue(new OperatingSystem("mac os x").isMac());
    Assert.assertFalse(new OperatingSystem("mac os x").isWindows());
    
    Assert.assertTrue(OperatingSystem.WINDOWS.isWindows());
  }
  
  @Test
  public void macos() {
    Assert.assertTrue(new OperatingSystem("mac os x").isMac());
    Assert.assertFalse(new OperatingSystem("mac os x").isWindows());
    
    Assert.assertTrue(OperatingSystem.MACOS.isMac());
  }
  
  @Test
  public void unix() {
    Assert.assertTrue(OperatingSystem.UNIX.isUnix());
  }
}
