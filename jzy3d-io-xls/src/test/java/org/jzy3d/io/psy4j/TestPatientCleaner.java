package org.jzy3d.io.psy4j;

import org.junit.Assert;
import org.junit.Test;

public class TestPatientCleaner {
  @Test
  public void patientCleaner() {
    PatientCleaner c=  new PatientCleaner();
    
    Assert.assertEquals("coucou", c.clean("coucou"));
    Assert.assertEquals("coucou", c.clean(" coucou  "));
    Assert.assertEquals("02", c.clean("fevrier"));
    Assert.assertEquals("02", c.clean("février"));
    Assert.assertEquals("08", c.clean("août"));
    Assert.assertEquals("12", c.clean("décembre"));
    Assert.assertEquals("ham1288", c.clean("ham121988"));
    Assert.assertEquals("art1271", c.clean("art decembre 1971"));
    
    /*// rou91979
    // ag021972
//ben9
//ber1010985
    //bey13071973
    //deg
    //dur
    
    
    //gar08
    //gar0886
    
    //gue
    //gue0284
    
    //had
    //had1982
    
    //he'd091979
    //hed0979
    
    //laj.051957
//laj0557
    
    lat
    lat0300
    
    luq
    luq1969
    
    mar
    mar0284
    mar02984
    
    pech101984
    pech1984
    
    tri
    tri0289
    tri0377*/
  }

}
