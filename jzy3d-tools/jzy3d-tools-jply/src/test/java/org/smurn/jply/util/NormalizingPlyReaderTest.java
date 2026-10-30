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
import java.util.Arrays;
import org.smurn.jply.Element;
import org.junit.Test;
import org.smurn.jply.DataType;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;
import org.smurn.jply.Property;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link NormalizingPlyReader}.
 */
public class NormalizingPlyReaderTest {

    @Test
    public void shuffleAxes() throws IOException {

        ElementType vertexType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE));

        Element vertex = new Element(vertexType);
        vertex.setDouble("x", 1);
        vertex.setDouble("y", 2);
        vertex.setDouble("z", 3);

        ElementType faceType = new ElementType(
                "face",
                new ListProperty(DataType.UCHAR, "vertex_indices", DataType.INT));


        ElementReader vertexReader = mock(ElementReader.class);
        when(vertexReader.getElementType()).thenReturn(vertexType);
        when(vertexReader.readElement()).thenReturn(vertex).thenReturn(null);

        ElementReader faceReader = mock(ElementReader.class);
        when(faceReader.getElementType()).thenReturn(faceType);
        when(faceReader.readElement()).thenReturn(null);

        PlyReader plyReader = mock(PlyReader.class);
        when(plyReader.nextElementReader()).
                thenReturn(vertexReader).thenReturn(faceReader);
        when(plyReader.getElementTypes()).
                thenReturn(Arrays.asList(vertexType, faceType));

        PlyReader target = new NormalizingPlyReader(
                plyReader,
                TesselationMode.PASS_THROUGH, NormalMode.PASS_THROUGH, TextureMode.PASS_THROUGH,
                Axis.Y_INVERTED, Axis.X, Axis.Z_INVERTED);


        Element expected = new Element(vertexType);
        expected.setDouble("x", -2);
        expected.setDouble("y", 1);
        expected.setDouble("z", -3);
        
        assertEquals(expected, target.nextElementReader().readElement());
    }

    @Test
    public void renameVertexIndex() throws IOException {

        ElementType vertexType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE));

        ElementType faceType = new ElementType(
                "face",
                new ListProperty(DataType.UCHAR, "vertex_indices", DataType.INT));

        Element face0 = new Element(faceType);
        face0.setIntList("vertex_indices", new int[]{0, 1, 2, 3});

        ElementReader vertexReader = mock(ElementReader.class);
        when(vertexReader.getElementType()).thenReturn(vertexType);
        when(vertexReader.readElement()).thenReturn(null);

        ElementReader faceReader = mock(ElementReader.class);
        when(faceReader.getElementType()).thenReturn(faceType);
        when(faceReader.readElement()).
                thenReturn(face0).
                thenReturn(null);

        PlyReader plyReader = mock(PlyReader.class);
        when(plyReader.nextElementReader()).
                thenReturn(vertexReader).thenReturn(faceReader);
        when(plyReader.getElementTypes()).
                thenReturn(Arrays.asList(vertexType, faceType));

        PlyReader target = new NormalizingPlyReader(
                plyReader,
                TesselationMode.PASS_THROUGH, NormalMode.PASS_THROUGH, TextureMode.PASS_THROUGH);

        ElementType faceTypeExpected = new ElementType(
                "face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));

        Element face0Expected = new Element(faceTypeExpected);
        face0Expected.setIntList("vertex_index", new int[]{0, 1, 2, 3});

        assertEquals(faceTypeExpected, target.getElementTypes().get(1));
        target.nextElementReader().close();
        ElementReader reader = target.nextElementReader();
        assertEquals(faceTypeExpected, reader.getElementType());

        assertEquals(face0Expected, reader.readElement());
    }

    @Test
    public void xy() throws IOException {

        ElementType inType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE));

        Element vMin = new Element(inType);
        vMin.setDouble("x", 10);
        vMin.setDouble("y", 100);
        vMin.setDouble("z", 1000);

        Element vMax = new Element(inType);
        vMax.setDouble("x", 20);
        vMax.setDouble("y", 200);
        vMax.setDouble("z", 2000);

        Element element = new Element(inType);
        element.setDouble("x", 12);
        element.setDouble("y", 130);
        element.setDouble("z", 1400);

        ElementType outType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE),
                new Property("u", DataType.DOUBLE),
                new Property("v", DataType.DOUBLE));

        Element expected = new Element(outType);
        expected.setDouble("x", 12);
        expected.setDouble("y", 130);
        expected.setDouble("z", 1400);
        expected.setDouble("u", 0.2);
        expected.setDouble("v", 0.3);

        RectBounds bounds = new RectBounds(10, 20, 100, 200, 1000, 2000);

        ElementReader vertexReader = mock(ElementReader.class);
        when(vertexReader.getElementType()).thenReturn(inType);
        when(vertexReader.readElement()).
                thenReturn(element).
                thenReturn(vMin).
                thenReturn(vMax).
                thenReturn(null);

        PlyReader plyReader = mock(PlyReader.class);
        when(plyReader.nextElementReader()).
                thenReturn(vertexReader).thenReturn(null);
        when(plyReader.getElementTypes()).
                thenReturn(Arrays.asList(inType));

        PlyReader target = new NormalizingPlyReader(
                plyReader,
                TesselationMode.PASS_THROUGH, NormalMode.PASS_THROUGH, TextureMode.XY);

        Element actual = target.nextElementReader().readElement();

        assertEquals(expected, actual);
    }

    @Test
    public void readQuad() throws IOException {

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
        vertex0.setDouble("nx", 1);
        vertex0.setDouble("ny", 1);
        vertex0.setDouble("nz", 2);
        Element vertex1 = new Element(vertexType);
        vertex1.setDouble("x", 1);
        vertex1.setDouble("y", 0);
        vertex1.setDouble("z", 0);
        vertex1.setDouble("nx", 1);
        vertex1.setDouble("ny", 1);
        vertex1.setDouble("nz", 2);
        Element vertex2 = new Element(vertexType);
        vertex2.setDouble("x", 1);
        vertex2.setDouble("y", 1);
        vertex2.setDouble("z", 0);
        vertex2.setDouble("nx", 1);
        vertex2.setDouble("ny", 1);
        vertex2.setDouble("nz", 2);
        Element vertex3 = new Element(vertexType);
        vertex3.setDouble("x", 0.5);
        vertex3.setDouble("y", 0.5);
        vertex3.setDouble("z", Math.sqrt(2.0) / 2.0);
        vertex3.setDouble("nx", 1);
        vertex3.setDouble("ny", 1);
        vertex3.setDouble("nz", 2);

        ElementType faceType = new ElementType(
                "face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));

        Element face0 = new Element(faceType);
        face0.setIntList("vertex_index", new int[]{0, 1, 2, 3});

        Element expectedFace0 = new Element(faceType);
        expectedFace0.setIntList("vertex_index", new int[]{0, 1, 2});

        Element expectedFace1 = new Element(faceType);
        expectedFace1.setIntList("vertex_index", new int[]{0, 2, 3});


        ElementReader vertexReader = mock(ElementReader.class);
        when(vertexReader.getElementType()).thenReturn(vertexType);
        when(vertexReader.readElement()).
                thenReturn(vertex0).
                thenReturn(vertex1).
                thenReturn(vertex2).
                thenReturn(vertex3).
                thenReturn(null);

        ElementReader faceReader = mock(ElementReader.class);
        when(faceReader.getElementType()).thenReturn(faceType);
        when(faceReader.readElement()).
                thenReturn(face0).
                thenReturn(null);

        PlyReader plyReader = mock(PlyReader.class);
        when(plyReader.nextElementReader()).
                thenReturn(vertexReader).thenReturn(faceReader);
        when(plyReader.getElementTypes()).
                thenReturn(Arrays.asList(vertexType, faceType));

        PlyReader target = new NormalizingPlyReader(
                plyReader,
                TesselationMode.TRIANGLES, NormalMode.ADD_NORMALS_CCW, TextureMode.PASS_THROUGH);

        ElementReader actualVertexReader = target.nextElementReader();
        assertEquals(vertex0, actualVertexReader.readElement());
        assertEquals(vertex1, actualVertexReader.readElement());
        assertEquals(vertex2, actualVertexReader.readElement());
        assertEquals(vertex3, actualVertexReader.readElement());

        ElementReader actualFaceReader = target.nextElementReader();
        assertEquals(expectedFace0, actualFaceReader.readElement());
        assertEquals(expectedFace1, actualFaceReader.readElement());
    }

    @Test
    public void addNormals() throws IOException {
        ElementType vertexType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE));
        ElementType vertexTypeAfter = new ElementType(
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

        Element expected0 = new Element(vertexTypeAfter);
        expected0.setDouble("x", 0);
        expected0.setDouble("y", 0);
        expected0.setDouble("z", 0);
        expected0.setDouble("nx", 0.5);
        expected0.setDouble("ny", -0.5);
        expected0.setDouble("nz", Math.sqrt(0.5));
        Element expected1 = new Element(vertexTypeAfter);
        expected1.setDouble("x", 1);
        expected1.setDouble("y", 0);
        expected1.setDouble("z", 0);
        expected1.setDouble("nx", 0);
        expected1.setDouble("ny", 0);
        expected1.setDouble("nz", 1);
        Element expected2 = new Element(vertexTypeAfter);
        expected2.setDouble("x", 1);
        expected2.setDouble("y", 1);
        expected2.setDouble("z", 0);
        expected2.setDouble("nx", 0.5);
        expected2.setDouble("ny", -0.5);
        expected2.setDouble("nz", Math.sqrt(0.5));
        Element expected3 = new Element(vertexTypeAfter);
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


        ElementReader vertexReader = mock(ElementReader.class);
        when(vertexReader.getElementType()).thenReturn(vertexType);
        when(vertexReader.readElement()).
                thenReturn(vertex0).
                thenReturn(vertex1).
                thenReturn(vertex2).
                thenReturn(vertex3).
                thenReturn(null);

        ElementReader faceReader = mock(ElementReader.class);
        when(faceReader.getElementType()).thenReturn(faceType);
        when(faceReader.readElement()).
                thenReturn(face0).
                thenReturn(face1).
                thenReturn(null);

        PlyReader plyReader = mock(PlyReader.class);
        when(plyReader.nextElementReader()).
                thenReturn(vertexReader).thenReturn(faceReader);
        when(plyReader.getElementTypes()).
                thenReturn(Arrays.asList(vertexType, faceType));

        PlyReader target = new NormalizingPlyReader(
                plyReader,
                TesselationMode.PASS_THROUGH, NormalMode.ADD_NORMALS_CCW, TextureMode.PASS_THROUGH);

        ElementReader actualVertexReader = target.nextElementReader();

        assertTrue(expected0.equals(actualVertexReader.readElement(), 1E-6));
        assertTrue(expected1.equals(actualVertexReader.readElement(), 1E-6));
        assertTrue(expected2.equals(actualVertexReader.readElement(), 1E-6));
        assertTrue(expected3.equals(actualVertexReader.readElement(), 1E-6));

    }

    @Test
    public void existingNormals() throws IOException {
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
        vertex0.setDouble("nx", 1);
        vertex0.setDouble("ny", 1);
        vertex0.setDouble("nz", 2);
        Element vertex1 = new Element(vertexType);
        vertex1.setDouble("x", 1);
        vertex1.setDouble("y", 0);
        vertex1.setDouble("z", 0);
        vertex1.setDouble("nx", 1);
        vertex1.setDouble("ny", 1);
        vertex1.setDouble("nz", 2);
        Element vertex2 = new Element(vertexType);
        vertex2.setDouble("x", 1);
        vertex2.setDouble("y", 1);
        vertex2.setDouble("z", 0);
        vertex2.setDouble("nx", 1);
        vertex2.setDouble("ny", 1);
        vertex2.setDouble("nz", 2);
        Element vertex3 = new Element(vertexType);
        vertex3.setDouble("x", 0.5);
        vertex3.setDouble("y", 0.5);
        vertex3.setDouble("z", Math.sqrt(2.0) / 2.0);
        vertex3.setDouble("nx", 1);
        vertex3.setDouble("ny", 1);
        vertex3.setDouble("nz", 2);

        ElementType faceType = new ElementType(
                "face",
                new ListProperty(DataType.UCHAR, "vertex_index", DataType.INT));

        Element face0 = new Element(faceType);
        face0.setIntList("vertex_index", new int[]{0, 1, 2});
        Element face1 = new Element(faceType);
        face1.setIntList("vertex_index", new int[]{0, 2, 3});


        ElementReader vertexReader = mock(ElementReader.class);
        when(vertexReader.getElementType()).thenReturn(vertexType);
        when(vertexReader.readElement()).
                thenReturn(vertex0).
                thenReturn(vertex1).
                thenReturn(vertex2).
                thenReturn(vertex3).
                thenReturn(null);

        ElementReader faceReader = mock(ElementReader.class);
        when(faceReader.getElementType()).thenReturn(faceType);
        when(faceReader.readElement()).
                thenReturn(face0).
                thenReturn(face1).
                thenReturn(null);

        PlyReader plyReader = mock(PlyReader.class);
        when(plyReader.nextElementReader()).
                thenReturn(vertexReader).thenReturn(faceReader);
        when(plyReader.getElementTypes()).
                thenReturn(Arrays.asList(vertexType, faceType));

        PlyReader target = new NormalizingPlyReader(
                plyReader,
                TesselationMode.PASS_THROUGH, NormalMode.ADD_NORMALS_CCW, TextureMode.PASS_THROUGH);

        ElementReader actualVertexReader = target.nextElementReader();
        assertEquals(vertex0, actualVertexReader.readElement());
        assertEquals(vertex1, actualVertexReader.readElement());
        assertEquals(vertex2, actualVertexReader.readElement());
        assertEquals(vertex3, actualVertexReader.readElement());
    }
}
