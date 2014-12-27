package org.jzy3d.maths;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestUtils {
    @Test
    public void testElapsedTimeFormatter(){
        assertEquals("0:00:01.000", Utils.time2str(1000));
    }
}
