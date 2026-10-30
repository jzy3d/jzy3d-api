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
import org.smurn.jply.Property;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link BufferedElementReader}.
 */
public class BufferedElementReaderTest {

    @Test(expected = NullPointerException.class)
    public void ctrNull() {
        new BufferedElementReader(null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexCheckLow() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        Element element0 = new Element(type);
        element0.setInt("bar", 12);
        Element element1 = new Element(type);
        element1.setInt("bar", 12);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(element1).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);

        target.readElement(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexCheckHigh() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        Element element0 = new Element(type);
        element0.setInt("bar", 12);
        Element element1 = new Element(type);
        element1.setInt("bar", 12);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(element1).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);

        target.readElement(2);
    }

    @Test
    public void passThrough() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        Element element0 = new Element(type);
        element0.setInt("bar", 12);
        Element element1 = new Element(type);
        element1.setInt("bar", 12);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(element1).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);

        assertEquals(element0, target.readElement());
        assertEquals(element1, target.readElement());
        assertNull(target.readElement());
    }

    @Test
    public void reset() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        Element element0 = new Element(type);
        element0.setInt("bar", 12);
        Element element1 = new Element(type);
        element1.setInt("bar", 12);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(element1).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);

        assertEquals(element0, target.readElement());
        assertEquals(element1, target.readElement());
        assertNull(target.readElement());
        target.reset();
        assertEquals(element0, target.readElement());
        assertEquals(element1, target.readElement());
        assertNull(target.readElement());
    }

    @Test
    public void indexReadAfter() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        Element element0 = new Element(type);
        element0.setInt("bar", 12);
        Element element1 = new Element(type);
        element1.setInt("bar", 12);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(element1).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);

        target.readElement();
        target.readElement();

        assertEquals(element0, target.readElement(0));
        assertEquals(element1, target.readElement(1));
    }

    @Test
    public void indexReadBefore() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        Element element0 = new Element(type);
        element0.setInt("bar", 12);
        Element element1 = new Element(type);
        element1.setInt("bar", 12);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(element1).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);

        assertEquals(element0, target.readElement(0));
        assertEquals(element1, target.readElement(1));
    }

    @Test
    public void readUnaffected() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        Element element0 = new Element(type);
        element0.setInt("bar", 12);
        Element element1 = new Element(type);
        element1.setInt("bar", 12);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(element1).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);

        target.readElement(1);

        assertEquals(element0, target.readElement());
        assertEquals(element1, target.readElement());
    }

    @Test
    public void detachedRead() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        Element element0 = new Element(type);
        element0.setInt("bar", 12);
        Element element1 = new Element(type);
        element1.setInt("bar", 12);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(element1).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);

        target.detach();

        assertEquals(element0, target.readElement());
        assertEquals(element1, target.readElement());
    }

    @Test
    public void detachClosesUnderlyingStream() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);

        BufferedElementReader target = new BufferedElementReader(reader);

        target.detach();
        verify(reader).close();
    }

    @Test
    public void closeCallsClose() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);

        BufferedElementReader target = new BufferedElementReader(reader);

        target.close();

        verify(reader).close();
    }

    @Test(expected = IllegalStateException.class)
    public void readClosed() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);

        BufferedElementReader target = new BufferedElementReader(reader);

        target.close();
        target.readElement();
    }

    @Test(expected = IllegalStateException.class)
    public void readClosedIndex() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);

        BufferedElementReader target = new BufferedElementReader(reader);

        target.close();
        target.readElement(0);
    }

    @Test
    public void duplicateType() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));
        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);
        RandomElementReader dup = target.duplicate();

        assertEquals(type, dup.getElementType());
    }

    @Test
    public void duplicateReadsFromStart() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        Element element0 = new Element(type);
        element0.setInt("bar", 12);
        Element element1 = new Element(type);
        element1.setInt("bar", 12);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.getCount()).thenReturn(2);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(element1).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);
        target.readElement();

        RandomElementReader dup = target.duplicate();
        assertEquals(element0, dup.readElement());
        assertEquals(element1, dup.readElement());
    }

    @Test
    public void duplicateRandom() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        Element element0 = new Element(type);
        element0.setInt("bar", 12);
        Element element1 = new Element(type);
        element1.setInt("bar", 12);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.getCount()).thenReturn(2);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(element1).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);
        target.readElement();

        RandomElementReader dup = target.duplicate();
        assertEquals(element1, dup.readElement(1));
        assertEquals(element0, dup.readElement(0));
    }

    @Test
    public void duplicateClose() throws IOException {
        ElementType type = new ElementType(
                "foo",
                new Property("bar", DataType.USHORT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(type);
        when(reader.readElement()).
                thenReturn(null);

        BufferedElementReader target = new BufferedElementReader(reader);
        RandomElementReader dup = target.duplicate();
        dup.close();

        verify(reader, never()).close();
    }
}
