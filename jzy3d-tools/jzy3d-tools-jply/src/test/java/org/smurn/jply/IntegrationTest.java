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
package org.smurn.jply;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 */
public class IntegrationTest {

    @Test
    public void alternativeDataTypes() throws IOException {
        PlyReaderFile reader = new PlyReaderFile(
                ClassLoader.getSystemResourceAsStream("simple_alternative_datatype.ply"));

        ElementType vertexType = new ElementType("vertex",
                new Property("x", DataType.FLOAT),
                new Property("y", DataType.FLOAT),
                new Property("z", DataType.FLOAT));

        Element expectedVertex0 = new Element(vertexType);
        expectedVertex0.setDouble("x", 0);
        expectedVertex0.setDouble("y", 0);
        expectedVertex0.setDouble("z", 0);

        Element expectedVertex1 = new Element(vertexType);
        expectedVertex1.setDouble("x", 0);
        expectedVertex1.setDouble("y", 0);
        expectedVertex1.setDouble("z", 1);

        Element expectedVertex2 = new Element(vertexType);
        expectedVertex2.setDouble("x", 0);
        expectedVertex2.setDouble("y", 1);
        expectedVertex2.setDouble("z", 1);

        ElementType faceType = new ElementType("face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));

        Element expectedFace0 = new Element(faceType);
        expectedFace0.setIntList("vertex_index", new int[]{0, 1, 2});

        Element expectedFace1 = new Element(faceType);
        expectedFace1.setIntList("vertex_index", new int[]{1, 0, 1, 2});

        ElementReader vertexReader = reader.nextElementReader();
        Element vertex0 = vertexReader.readElement();
        Element vertex1 = vertexReader.readElement();
        Element vertex2 = vertexReader.readElement();
        assertNull(vertexReader.readElement());
        vertexReader.close();

        ElementReader faceReader = reader.nextElementReader();
        Element face0 = faceReader.readElement();
        Element face1 = faceReader.readElement();
        assertNull(faceReader.readElement());
        faceReader.close();

        assertEquals(expectedVertex0, vertex0);
        assertEquals(expectedVertex1, vertex1);
        assertEquals(expectedVertex2, vertex2);
        assertEquals(expectedFace0, face0);
        assertEquals(expectedFace1, face1);
    }
    
    @Test
    public void binaryBigEndian() throws IOException {
        PlyReaderFile reader = new PlyReaderFile(
                ClassLoader.getSystemResourceAsStream("simple-bigendian.ply"));

        ElementType vertexType = new ElementType("vertex",
                new Property("x", DataType.FLOAT),
                new Property("y", DataType.FLOAT),
                new Property("z", DataType.FLOAT));

        Element expectedVertex0 = new Element(vertexType);
        expectedVertex0.setDouble("x", 0);
        expectedVertex0.setDouble("y", 0);
        expectedVertex0.setDouble("z", 0);

        Element expectedVertex1 = new Element(vertexType);
        expectedVertex1.setDouble("x", 0);
        expectedVertex1.setDouble("y", 0);
        expectedVertex1.setDouble("z", 1);

        Element expectedVertex2 = new Element(vertexType);
        expectedVertex2.setDouble("x", 0);
        expectedVertex2.setDouble("y", 1);
        expectedVertex2.setDouble("z", 1);

        ElementType faceType = new ElementType("face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));

        Element expectedFace0 = new Element(faceType);
        expectedFace0.setIntList("vertex_index", new int[]{0, 1, 2});

        Element expectedFace1 = new Element(faceType);
        expectedFace1.setIntList("vertex_index", new int[]{1, 0, 1, 2});

        ElementReader vertexReader = reader.nextElementReader();
        Element vertex0 = vertexReader.readElement();
        Element vertex1 = vertexReader.readElement();
        Element vertex2 = vertexReader.readElement();
        assertNull(vertexReader.readElement());
        vertexReader.close();

        ElementReader faceReader = reader.nextElementReader();
        Element face0 = faceReader.readElement();
        Element face1 = faceReader.readElement();
        assertNull(faceReader.readElement());
        faceReader.close();

        assertEquals(expectedVertex0, vertex0);
        assertEquals(expectedVertex1, vertex1);
        assertEquals(expectedVertex2, vertex2);
        assertEquals(expectedFace0, face0);
        assertEquals(expectedFace1, face1);
    }

    @Test
    public void binaryLittleEndian() throws IOException {
        PlyReaderFile reader = new PlyReaderFile(
                ClassLoader.getSystemResourceAsStream("simple-littleendian.ply"));

        ElementType vertexType = new ElementType("vertex",
                new Property("x", DataType.FLOAT),
                new Property("y", DataType.FLOAT),
                new Property("z", DataType.FLOAT));

        Element expectedVertex0 = new Element(vertexType);
        expectedVertex0.setDouble("x", 0);
        expectedVertex0.setDouble("y", 0);
        expectedVertex0.setDouble("z", 0);

        Element expectedVertex1 = new Element(vertexType);
        expectedVertex1.setDouble("x", 0);
        expectedVertex1.setDouble("y", 0);
        expectedVertex1.setDouble("z", 1);

        Element expectedVertex2 = new Element(vertexType);
        expectedVertex2.setDouble("x", 0);
        expectedVertex2.setDouble("y", 1);
        expectedVertex2.setDouble("z", 1);

        ElementType faceType = new ElementType("face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));

        Element expectedFace0 = new Element(faceType);
        expectedFace0.setIntList("vertex_index", new int[]{0, 1, 2});

        Element expectedFace1 = new Element(faceType);
        expectedFace1.setIntList("vertex_index", new int[]{1, 0, 1, 2});

        ElementReader vertexReader = reader.nextElementReader();
        Element vertex0 = vertexReader.readElement();
        Element vertex1 = vertexReader.readElement();
        Element vertex2 = vertexReader.readElement();
        assertNull(vertexReader.readElement());
        vertexReader.close();

        ElementReader faceReader = reader.nextElementReader();
        Element face0 = faceReader.readElement();
        Element face1 = faceReader.readElement();
        assertNull(faceReader.readElement());
        faceReader.close();

        assertEquals(expectedVertex0, vertex0);
        assertEquals(expectedVertex1, vertex1);
        assertEquals(expectedVertex2, vertex2);
        assertEquals(expectedFace0, face0);
        assertEquals(expectedFace1, face1);
    }

    @Test
    public void cube() throws IOException {

        PlyReaderFile reader = new PlyReaderFile(
                ClassLoader.getSystemResourceAsStream("cube.ply"));
        ElementReader vertices = reader.nextElementReader();

        Element vertex;
        assertNotNull(vertex = vertices.readElement());
        assertEquals(0, vertex.getInt("x"));
        assertEquals(0, vertex.getInt("y"));
        assertEquals(0, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertEquals(0, vertex.getInt("x"));
        assertEquals(0, vertex.getInt("y"));
        assertEquals(1, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(0, vertex.getInt("x"));
        assertEquals(1, vertex.getInt("y"));
        assertEquals(1, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(0, vertex.getInt("x"));
        assertEquals(1, vertex.getInt("y"));
        assertEquals(0, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(1, vertex.getInt("x"));
        assertEquals(0, vertex.getInt("y"));
        assertEquals(0, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(1, vertex.getInt("x"));
        assertEquals(0, vertex.getInt("y"));
        assertEquals(1, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(1, vertex.getInt("x"));
        assertEquals(1, vertex.getInt("y"));
        assertEquals(1, vertex.getInt("z"));

        assertNotNull(vertex = vertices.readElement());
        assertNotNull(vertex);
        assertEquals(1, vertex.getInt("x"));
        assertEquals(1, vertex.getInt("y"));
        assertEquals(0, vertex.getInt("z"));

        assertNull(vertex = vertices.readElement());
        vertices.close();

        ElementReader faces = reader.nextElementReader();
        Element face;
        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{0, 1, 2, 3}, face.getIntList("vertex_index"));

        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{7, 6, 5, 4}, face.getIntList("vertex_index"));

        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{0, 4, 5, 1}, face.getIntList("vertex_index"));

        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{1, 5, 6, 2}, face.getIntList("vertex_index"));

        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{2, 6, 7, 3}, face.getIntList("vertex_index"));

        assertNotNull(face = faces.readElement());
        assertArrayEquals(new int[]{3, 7, 4, 0}, face.getIntList("vertex_index"));

        assertNull(face = faces.readElement());
    }

    @Test
    public void windowsNewLines() throws IOException {
        StringBuilder str = new StringBuilder();
        str.append("ply\r\n");
        str.append("format ascii 1.0\r\n");
        str.append("element vertex 2\r\n");
        str.append("property float x\r\n");
        str.append("end_header\r\n");
        str.append("1.5\r\n");
        str.append("2.5\r\n");

        PlyReader target = new PlyReaderFile(new ByteArrayInputStream(str.toString().getBytes("UTF-8")));

        ElementType expectedType = new ElementType("vertex", new Property("x", DataType.FLOAT));
        Element expected0 = new Element(expectedType);
        expected0.setDouble("x", 1.5);
        Element expected1 = new Element(expectedType);
        expected1.setDouble("x", 2.5);

        assertEquals(Arrays.asList(expectedType), target.getElementTypes());
        ElementReader reader = target.nextElementReader();
        Element actual0 = reader.readElement();
        Element actual1 = reader.readElement();

        assertEquals(expected0, actual0);
        assertEquals(expected1, actual1);
    }
}
