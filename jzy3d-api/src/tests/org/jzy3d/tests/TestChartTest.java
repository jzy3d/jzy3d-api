package org.jzy3d.tests;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jzy3d.junit.ChartTest;
import org.jzy3d.junit.ChartTestFailed;

/**
 * Testing the test tool!
 * 
 * @author martin
 *
 */
public class TestChartTest {
    ChartTest test;
    
    @Before
    public void before(){
        test = new ChartTest();
    }
    
    @Test
    public void compareImageWithHerselfSucceed() throws IOException{
        BufferedImage bi = test.loadBufferedImage("data/tests/SimpleChartTest.png");
        try {
            test.compare(bi, bi);
        } catch (ChartTestFailed e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue(true);
    }
    
    @Test
    public void compareImageWithAnotherFails() throws IOException{
        BufferedImage bi1 = test.loadBufferedImage("data/tests/SimpleChartTest.png");
        BufferedImage bi2 = test.loadBufferedImage("data/tests/SimpleChartTest#ERROR#.png");
        try {
            test.compare(bi1, bi2);
        } catch (ChartTestFailed e) {
            Assert.assertTrue(e.getMessage(), true);
            return;
        }
        Assert.fail("two different image should throw an exception");
    }
}
