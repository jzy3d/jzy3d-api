/*
 * Copyright 2011 Stefan C. Mueller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smurn.jply.util;

import org.smurn.jply.ListProperty;
import java.io.IOException;
import org.smurn.jply.Element;
import org.junit.Test;
import org.smurn.jply.DataType;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.Property;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link NormalGenerator}.
 */
public class NormalGeneratorTest {
    
    @Test
    public void singleFace() throws IOException {
        
        ElementType vertexType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE),
                new Property("nx", DataType.DOUBLE),
                new Property("ny", DataType.DOUBLE),
                new Property("nz", DataType.DOUBLE));
        
        Element vertex0 = new Element(vertexType);
        vertex0.setDouble("x", 0);
        vertex0.setDouble("y", 0);
        vertex0.setDouble("z", 0);
        Element vertex1 = new Element(vertexType);
        vertex1.setDouble("x", 1);
        vertex1.setDouble("y", 0);
        vertex1.setDouble("z", 0);
        Element vertex2 = new Element(vertexType);
        vertex2.setDouble("x", 1);
        vertex2.setDouble("y", 1);
        vertex2.setDouble("z", 0);
        
        Element expected0 = new Element(vertexType);
        expected0.setDouble("x", 0);
        expected0.setDouble("y", 0);
        expected0.setDouble("z", 0);
        expected0.setDouble("nx", 0);
        expected0.setDouble("ny", 0);
        expected0.setDouble("nz", 1);
        Element expected1 = new Element(vertexType);
        expected1.setDouble("x", 1);
        expected1.setDouble("y", 0);
        expected1.setDouble("z", 0);
        expected1.setDouble("nx", 0);
        expected1.setDouble("ny", 0);
        expected1.setDouble("nz", 1);
        Element expected2 = new Element(vertexType);
        expected2.setDouble("x", 1);
        expected2.setDouble("y", 1);
        expected2.setDouble("z", 0);
        expected2.setDouble("nx", 0);
        expected2.setDouble("ny", 0);
        expected2.setDouble("nz", 1);
        
        ElementType faceType = new ElementType(
                "face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));
        
        Element face0 = new Element(faceType);
        face0.setIntList("vertex_index", new int[]{0, 1, 2});
        
        
        ElementReader vertexReaderMock = mock(ElementReader.class);
        when(vertexReaderMock.getElementType()).thenReturn(vertexType);
        when(vertexReaderMock.readElement()).
                thenReturn(vertex0).
                thenReturn(vertex1).
                thenReturn(vertex2).
                thenReturn(null);
        BufferedElementReader vertexReader =
                new BufferedElementReader(vertexReaderMock);
        
        ElementReader faceReader = mock(ElementReader.class);
        when(faceReader.getElementType()).thenReturn(faceType);
        when(faceReader.readElement()).
                thenReturn(face0).
                thenReturn(null);
        
        NormalGenerator target = new NormalGenerator();
        
        target.generateNormals(vertexReader, faceReader);
        
        vertexReader.reset();
        assertEquals(expected0, vertexReader.readElement());
        assertEquals(expected1, vertexReader.readElement());
        assertEquals(expected2, vertexReader.readElement());
    }
    
    @Test
    public void twoFace() throws IOException {
        
        ElementType vertexType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE),
                new Property("nx", DataType.DOUBLE),
                new Property("ny", DataType.DOUBLE),
                new Property("nz", DataType.DOUBLE));
        
        Element vertex0 = new Element(vertexType);
        vertex0.setDouble("x", 0);
        vertex0.setDouble("y", 0);
        vertex0.setDouble("z", 0);
        Element vertex1 = new Element(vertexType);
        vertex1.setDouble("x", 1);
        vertex1.setDouble("y", 0);
        vertex1.setDouble("z", 0);
        Element vertex2 = new Element(vertexType);
        vertex2.setDouble("x", 1);
        vertex2.setDouble("y", 1);
        vertex2.setDouble("z", 0);
        Element vertex3 = new Element(vertexType);
        vertex3.setDouble("x", 0.5);
        vertex3.setDouble("y", 0.5);
        vertex3.setDouble("z", Math.sqrt(2.0) / 2.0);
        
        Element expected0 = new Element(vertexType);
        expected0.setDouble("x", 0);
        expected0.setDouble("y", 0);
        expected0.setDouble("z", 0);
        expected0.setDouble("nx", 0.5);
        expected0.setDouble("ny", -0.5);
        expected0.setDouble("nz", Math.sqrt(0.5));
        Element expected1 = new Element(vertexType);
        expected1.setDouble("x", 1);
        expected1.setDouble("y", 0);
        expected1.setDouble("z", 0);
        expected1.setDouble("nx", 0);
        expected1.setDouble("ny", 0);
        expected1.setDouble("nz", 1);
        Element expected2 = new Element(vertexType);
        expected2.setDouble("x", 1);
        expected2.setDouble("y", 1);
        expected2.setDouble("z", 0);
        expected2.setDouble("nx", 0.5);
        expected2.setDouble("ny", -0.5);
        expected2.setDouble("nz", Math.sqrt(0.5));
        Element expected3 = new Element(vertexType);
        expected3.setDouble("x", 0.5);
        expected3.setDouble("y", 0.5);
        expected3.setDouble("z", Math.sqrt(2.0) / 2.0);
        expected3.setDouble("nx", 1.0 / Math.sqrt(2));
        expected3.setDouble("ny", -1.0 / Math.sqrt(2));
        expected3.setDouble("nz", 0);
        
        ElementType faceType = new ElementType(
                "face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));
        
        Element face0 = new Element(faceType);
        face0.setIntList("vertex_index", new int[]{0, 1, 2});
        Element face1 = new Element(faceType);
        face1.setIntList("vertex_index", new int[]{0, 2, 3});
        
        
        ElementReader vertexReaderMock = mock(ElementReader.class);
        when(vertexReaderMock.getElementType()).thenReturn(vertexType);
        when(vertexReaderMock.readElement()).
                thenReturn(vertex0).
                thenReturn(vertex1).
                thenReturn(vertex2).
                thenReturn(vertex3).
                thenReturn(null);
        BufferedElementReader vertexReader =
                new BufferedElementReader(vertexReaderMock);
        
        ElementReader faceReader = mock(ElementReader.class);
        when(faceReader.getElementType()).thenReturn(faceType);
        when(faceReader.readElement()).
                thenReturn(face0).
                thenReturn(face1).
                thenReturn(null);
        
        NormalGenerator target = new NormalGenerator();
        
        target.generateNormals(vertexReader, faceReader);
        
        vertexReader.reset();
        assertTrue(expected0.equals(vertexReader.readElement(), 1E-6));
        assertTrue(expected1.equals(vertexReader.readElement(), 1E-6));
        assertTrue(expected2.equals(vertexReader.readElement(), 1E-6));
    }
    
    @Test
    public void wideAngle() throws IOException {
        
        ElementType vertexType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE),
                new Property("nx", DataType.DOUBLE),
                new Property("ny", DataType.DOUBLE),
                new Property("nz", DataType.DOUBLE));
        
        Element vertex0 = new Element(vertexType);
        vertex0.setDouble("x", 0);
        vertex0.setDouble("y", 0);
        vertex0.setDouble("z", 0);
        Element vertex1 = new Element(vertexType);
        vertex1.setDouble("x", 1);
        vertex1.setDouble("y", 0);
        vertex1.setDouble("z", 0);
        Element vertex2 = new Element(vertexType);
        vertex2.setDouble("x", -1);
        vertex2.setDouble("y", 1);
        vertex2.setDouble("z", 0);
        Element vertex3 = new Element(vertexType);
        vertex3.setDouble("x", 0);
        vertex3.setDouble("y", 0);
        vertex3.setDouble("z", 1);
        
        Element expected0 = new Element(vertexType);
        expected0.setDouble("x", 0);
        expected0.setDouble("y", 0);
        expected0.setDouble("z", 0);
        expected0.setDouble("nx", 0);
        expected0.setDouble("ny", 2.0/Math.sqrt(13));
        expected0.setDouble("nz", 3.0/Math.sqrt(13));
        
        ElementType faceType = new ElementType(
                "face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));
        
        Element face0 = new Element(faceType);
        face0.setIntList("vertex_index", new int[]{0, 1, 2});
        Element face1 = new Element(faceType);
        face1.setIntList("vertex_index", new int[]{0, 3, 1});
        
        
        ElementReader vertexReaderMock = mock(ElementReader.class);
        when(vertexReaderMock.getElementType()).thenReturn(vertexType);
        when(vertexReaderMock.readElement()).
                thenReturn(vertex0).
                thenReturn(vertex1).
                thenReturn(vertex2).
                thenReturn(vertex3).
                thenReturn(null);
        BufferedElementReader vertexReader =
                new BufferedElementReader(vertexReaderMock);
        
        ElementReader faceReader = mock(ElementReader.class);
        when(faceReader.getElementType()).thenReturn(faceType);
        when(faceReader.readElement()).
                thenReturn(face0).
                thenReturn(face1).
                thenReturn(null);
        
        NormalGenerator target = new NormalGenerator();
        
        target.generateNormals(vertexReader, faceReader);
        
        vertexReader.reset();
        Element actual = vertexReader.readElement();
        assertTrue(expected0.equals(actual, 1E-6));
    }
    
    @Test
    public void narrowAngle() throws IOException {
        
        ElementType vertexType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE),
                new Property("nx", DataType.DOUBLE),
                new Property("ny", DataType.DOUBLE),
                new Property("nz", DataType.DOUBLE));
        
        Element vertex0 = new Element(vertexType);
        vertex0.setDouble("x", 0);
        vertex0.setDouble("y", 0);
        vertex0.setDouble("z", 0);
        Element vertex1 = new Element(vertexType);
        vertex1.setDouble("x", 1);
        vertex1.setDouble("y", 0);
        vertex1.setDouble("z", 0);
        Element vertex2 = new Element(vertexType);
        vertex2.setDouble("x", 1);
        vertex2.setDouble("y", 1);
        vertex2.setDouble("z", 0);
        Element vertex3 = new Element(vertexType);
        vertex3.setDouble("x", 0);
        vertex3.setDouble("y", 0);
        vertex3.setDouble("z", 1);
        
        Element expected0 = new Element(vertexType);
        expected0.setDouble("x", 0);
        expected0.setDouble("y", 0);
        expected0.setDouble("z", 0);
        expected0.setDouble("nx", 0);
        expected0.setDouble("ny", 2.0/Math.sqrt(5));
        expected0.setDouble("nz", 1.0/Math.sqrt(5));
        
        ElementType faceType = new ElementType(
                "face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));
        
        Element face0 = new Element(faceType);
        face0.setIntList("vertex_index", new int[]{0, 1, 2});
        Element face1 = new Element(faceType);
        face1.setIntList("vertex_index", new int[]{0, 3, 1});
        
        
        ElementReader vertexReaderMock = mock(ElementReader.class);
        when(vertexReaderMock.getElementType()).thenReturn(vertexType);
        when(vertexReaderMock.readElement()).
                thenReturn(vertex0).
                thenReturn(vertex1).
                thenReturn(vertex2).
                thenReturn(vertex3).
                thenReturn(null);
        BufferedElementReader vertexReader =
                new BufferedElementReader(vertexReaderMock);
        
        ElementReader faceReader = mock(ElementReader.class);
        when(faceReader.getElementType()).thenReturn(faceType);
        when(faceReader.readElement()).
                thenReturn(face0).
                thenReturn(face1).
                thenReturn(null);
        
        NormalGenerator target = new NormalGenerator();
        
        target.generateNormals(vertexReader, faceReader);
        
        vertexReader.reset();
        Element actual = vertexReader.readElement();
        assertTrue(expected0.equals(actual, 1E-6));
    }
    
    
    @Test
    public void clockwise() throws IOException {
        
        ElementType vertexType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE),
                new Property("nx", DataType.DOUBLE),
                new Property("ny", DataType.DOUBLE),
                new Property("nz", DataType.DOUBLE));
        
        Element vertex0 = new Element(vertexType);
        vertex0.setDouble("x", 0);
        vertex0.setDouble("y", 0);
        vertex0.setDouble("z", 0);
        Element vertex1 = new Element(vertexType);
        vertex1.setDouble("x", 1);
        vertex1.setDouble("y", 0);
        vertex1.setDouble("z", 0);
        Element vertex2 = new Element(vertexType);
        vertex2.setDouble("x", 1);
        vertex2.setDouble("y", 1);
        vertex2.setDouble("z", 0);
        
        Element expected0 = new Element(vertexType);
        expected0.setDouble("x", 0);
        expected0.setDouble("y", 0);
        expected0.setDouble("z", 0);
        expected0.setDouble("nx", 0);
        expected0.setDouble("ny", 0);
        expected0.setDouble("nz", -1);
        Element expected1 = new Element(vertexType);
        expected1.setDouble("x", 1);
        expected1.setDouble("y", 0);
        expected1.setDouble("z", 0);
        expected1.setDouble("nx", 0);
        expected1.setDouble("ny", 0);
        expected1.setDouble("nz", -1);
        Element expected2 = new Element(vertexType);
        expected2.setDouble("x", 1);
        expected2.setDouble("y", 1);
        expected2.setDouble("z", 0);
        expected2.setDouble("nx", 0);
        expected2.setDouble("ny", 0);
        expected2.setDouble("nz", -1);
        
        ElementType faceType = new ElementType(
                "face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));
        
        Element face0 = new Element(faceType);
        face0.setIntList("vertex_index", new int[]{0, 1, 2});
        
        
        ElementReader vertexReaderMock = mock(ElementReader.class);
        when(vertexReaderMock.getElementType()).thenReturn(vertexType);
        when(vertexReaderMock.readElement()).
                thenReturn(vertex0).
                thenReturn(vertex1).
                thenReturn(vertex2).
                thenReturn(null);
        BufferedElementReader vertexReader =
                new BufferedElementReader(vertexReaderMock);
        
        ElementReader faceReader = mock(ElementReader.class);
        when(faceReader.getElementType()).thenReturn(faceType);
        when(faceReader.readElement()).
                thenReturn(face0).
                thenReturn(null);
        
        NormalGenerator target = new NormalGenerator();
        target.setCounterClockwise(false);
        target.generateNormals(vertexReader, faceReader);
                
        vertexReader.reset();
        assertEquals(expected0, vertexReader.readElement());
        assertEquals(expected1, vertexReader.readElement());
        assertEquals(expected2, vertexReader.readElement());
    }
}
