package org.jzy3d.io.obj;

import java.io.File;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TestObjFile {
    @Test
    public void loadBunny(){
        OBJFile objFile = new OBJFile();
        
        String objFilePath = "data/objfiles/bunny.obj";
        objFile.loadModelFromFilename("file://" + new File(objFilePath).getAbsolutePath());
        

        int countVertices = objFile.getPositionCount();
        int indexSize = objFile.getIndexCount();
        int vertexSize = objFile.getCompiledVertexCount();
        int byteOffset = objFile.getCompiledVertexSize();
        int normalOffset = objFile.getCompiledNormalOffset();
        int dimensions = objFile.getPositionSize(); 

        Assert.assertFalse(objFile.hasNormals());
        
        Assert.assertEquals(80289, countVertices);
        Assert.assertEquals(482409, indexSize);
        Assert.assertEquals(1447227, vertexSize);
        Assert.assertEquals(0, byteOffset);
        Assert.assertEquals(-1, normalOffset);
        Assert.assertEquals(3, dimensions);
        
        objFile.compileModel();

    }
    
    @Ignore /*To run me, download file indicated in data/objfiles/dragon.url */
    @Test
    public void loadDragon(){
        OBJFile objFile = new OBJFile();
        
        String objFilePath = "data/objfiles/dragon.obj";
        objFile.loadModelFromFilename("file://" + new File(objFilePath).getAbsolutePath());
        
        int countVertices = objFile.getPositionCount();
        int indexSize = objFile.getIndexCount();
        int vertexSize = objFile.getCompiledVertexCount();
        int byteOffset = objFile.getCompiledVertexSize();
        int normalOffset = objFile.getCompiledNormalOffset();
        int dimensions = objFile.getPositionSize(); 

        Assert.assertTrue(objFile.hasNormals());

        Assert.assertEquals(437645, countVertices);
        Assert.assertEquals(2614242, indexSize);
        Assert.assertEquals(15685452, vertexSize);
        Assert.assertEquals(0, byteOffset);
        Assert.assertEquals(-1, normalOffset);
        Assert.assertEquals(3, dimensions);
    }
    
    
}
