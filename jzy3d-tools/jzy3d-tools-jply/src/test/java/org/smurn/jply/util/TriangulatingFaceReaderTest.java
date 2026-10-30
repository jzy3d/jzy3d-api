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

import java.io.IOException;
import org.smurn.jply.Element;
import org.junit.Test;
import org.smurn.jply.DataType;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.ListProperty;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link TriangulatingFaceReader}.
 */
public class TriangulatingFaceReaderTest {

    @Test
    public void getElementType() {

        ElementType type = new ElementType(
                "face",
                new ListProperty(
                DataType.UCHAR,
                "vertex_index", DataType.INT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);

        TriangulatingFaceReader target = new TriangulatingFaceReader(reader);

        assertEquals(type, target.getElementType());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getCount() {

        ElementType type = new ElementType(
                "face",
                new ListProperty(
                DataType.UCHAR,
                "vertex_index", DataType.INT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);

        TriangulatingFaceReader target = new TriangulatingFaceReader(reader);

        target.getCount();
    }

    @Test
    public void readTriangle() throws IOException {

        ElementType type = new ElementType(
                "face",
                new ListProperty(
                DataType.UCHAR,
                "vertex_index", DataType.INT));

        Element element = new Element(type);
        element.setIntList("vertex_index", new int[]{2, 3, 4});

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).thenReturn(element).thenReturn(null);

        TriangulatingFaceReader target = new TriangulatingFaceReader(reader);

        assertEquals(element, target.readElement());
    }

    @Test
    public void readQuad() throws IOException {

        ElementType type = new ElementType(
                "face",
                new ListProperty(
                DataType.UCHAR,
                "vertex_index", DataType.INT));

        // input face
        Element element = new Element(type);
        element.setIntList("vertex_index", new int[]{2, 3, 4, 5});

        // expected outputs
        Element triangle1 = new Element(type);
        triangle1.setIntList("vertex_index", new int[]{2, 3, 4});
        Element triangle2 = new Element(type);
        triangle2.setIntList("vertex_index", new int[]{2, 4, 5});

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).thenReturn(element).thenReturn(null);

        TriangulatingFaceReader target = new TriangulatingFaceReader(reader);

        assertEquals(triangle1, target.readElement());
        assertEquals(triangle2, target.readElement());
    }

    @Test
    public void readMixed() throws IOException {

        ElementType type = new ElementType(
                "face",
                new ListProperty(
                DataType.UCHAR,
                "vertex_index", DataType.INT));

        // input face
        Element element1 = new Element(type);
        element1.setIntList("vertex_index", new int[]{0, 1, 2});

        Element element2 = new Element(type);
        element2.setIntList("vertex_index", new int[]{3, 4, 5, 6});

        Element element3 = new Element(type);
        element3.setIntList("vertex_index", new int[]{7, 8, 9, 10, 11});

        // expected outputs
        Element triangle1 = element1;

        Element triangle2 = new Element(type);
        triangle2.setIntList("vertex_index", new int[]{3, 4, 5});
        Element triangle3 = new Element(type);
        triangle3.setIntList("vertex_index", new int[]{3, 5, 6});

        Element triangle4 = new Element(type);
        triangle4.setIntList("vertex_index", new int[]{7, 8, 9});
        Element triangle5 = new Element(type);
        triangle5.setIntList("vertex_index", new int[]{7, 9, 10});
        Element triangle6 = new Element(type);
        triangle6.setIntList("vertex_index", new int[]{7, 10, 11});

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).
                thenReturn(element1).
                thenReturn(element2).
                thenReturn(element3).
                thenReturn(null);

        TriangulatingFaceReader target = new TriangulatingFaceReader(reader);

        assertEquals(triangle1, target.readElement());
        assertEquals(triangle2, target.readElement());
        assertEquals(triangle3, target.readElement());
        assertEquals(triangle4, target.readElement());
        assertEquals(triangle5, target.readElement());
        assertEquals(triangle6, target.readElement());
        assertNull(target.readElement());
    }

    @Test
    public void closeCallsClose() throws IOException {
        ElementType type = new ElementType(
                "face",
                new ListProperty(
                DataType.UCHAR,
                "vertex_index", DataType.INT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);

        TriangulatingFaceReader target = new TriangulatingFaceReader(reader);

        target.close();

        verify(reader).close();
    }

    @Test(expected = IllegalStateException.class)
    public void readClosedFails() throws IOException {
        ElementType type = new ElementType(
                "face",
                new ListProperty(
                DataType.UCHAR,
                "vertex_index", DataType.INT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);

        TriangulatingFaceReader target = new TriangulatingFaceReader(reader);

        target.close();
        target.readElement();
    }

    @Test(expected = IllegalStateException.class)
    public void readClosedFailsWithQueue() throws IOException {
        ElementType type = new ElementType(
                "face",
                new ListProperty(
                DataType.UCHAR,
                "vertex_index", DataType.INT));

        Element element = new Element(type);
        element.setIntList("vertex_index", new int[]{2, 3, 4, 5});

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).thenReturn(element).thenReturn(null);

        TriangulatingFaceReader target = new TriangulatingFaceReader(reader);

        target.readElement();
        target.close();
        target.readElement();
    }
}
