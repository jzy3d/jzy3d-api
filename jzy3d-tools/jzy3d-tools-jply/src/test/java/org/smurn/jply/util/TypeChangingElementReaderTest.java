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

import java.util.HashMap;
import java.util.Map;
import org.smurn.jply.Property;
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
 * Unit tests for {@link TypeChangingElementReader}.
 */
public class TypeChangingElementReaderTest {

    @Test
    public void getElementType() {

        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT));

        ElementType targetType = new ElementType(
                "cat",
                new Property("dog", DataType.INT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);

        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType);

        assertEquals(targetType, target.getElementType());
    }

    @Test
    public void renameElementType() throws IOException {
        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT));

        ElementType targetType = new ElementType(
                "fooNEW",
                new Property("bar", DataType.INT));

        Element element0 = new Element(sourceType);
        element0.setInt("bar", 42);
        Element expected0 = new Element(targetType);
        expected0.setInt("bar", 42);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(null);

        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType);

        assertEquals(expected0, target.readElement());
        assertNull(target.readElement());
    }

        @Test
    public void renameProperty() throws IOException {
        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT));

        ElementType targetType = new ElementType(
                "fooNEW",
                new Property("barNew", DataType.INT));

        Element element0 = new Element(sourceType);
        element0.setInt("bar", 42);
        Element expected0 = new Element(targetType);
        expected0.setInt("barNew", 42);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(null);

        Map<String,String> sourceNames = new HashMap<String,String>();
        sourceNames.put("barNew", "bar");
        
        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType, sourceNames);

        assertEquals(expected0, target.readElement());
    }
    
    @Test
    public void changeDataType() throws IOException {
        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT));

        ElementType targetType = new ElementType(
                "fooNEW",
                new Property("bar", DataType.FLOAT));

        Element element0 = new Element(sourceType);
        element0.setInt("bar", 42);
        Element expected0 = new Element(targetType);
        expected0.setInt("bar", 42);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(null);

        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType);

        assertEquals(expected0, target.readElement());
    }

    @Test
    public void removeProperty() throws IOException {
        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT),
                new Property("jdoe", DataType.INT));

        ElementType targetType = new ElementType(
                "foo",
                new Property("bar", DataType.INT));

        Element element0 = new Element(sourceType);
        element0.setInt("bar", 42);
        element0.setInt("jdoe", 32);
        Element expected0 = new Element(targetType);
        expected0.setInt("bar", 42);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(null);

        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType);

        assertEquals(expected0, target.readElement());
    }

    @Test
    public void addProperty() throws IOException {
        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT));

        ElementType targetType = new ElementType(
                "foo",
                new Property("bar", DataType.INT),
                new Property("jdoe", DataType.INT));

        Element element0 = new Element(sourceType);
        element0.setInt("bar", 42);
        Element expected0 = new Element(targetType);
        expected0.setInt("bar", 42);
        expected0.setInt("jdoe", 0);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(null);

        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType);

        assertEquals(expected0, target.readElement());
    }

    @Test
    public void addListProperty() throws IOException {
        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT));

        ElementType targetType = new ElementType(
                "foo",
                new Property("bar", DataType.INT),
                new ListProperty(DataType.SHORT, "jdoe", DataType.INT));

        Element element0 = new Element(sourceType);
        Element expected0 = new Element(targetType);
        expected0.setIntList("jdoe", new int[0]);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(null);

        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType);

        assertEquals(expected0, target.readElement());
    }

    @Test
    public void changeListToNoListSingleValue() throws IOException {
        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT),
                new ListProperty(DataType.SHORT, "jdoe", DataType.INT));

        ElementType targetType = new ElementType(
                "foo",
                new Property("bar", DataType.INT),
                new Property("jdoe", DataType.INT));

        Element element0 = new Element(sourceType);
        element0.setIntList("jdoe", new int[]{42});
        Element expected0 = new Element(targetType);
        expected0.setInt("jdoe", 42);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(null);

        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType);

        assertEquals(expected0, target.readElement());
    }

    @Test
    public void changeListToNoListMultiValue() throws IOException {
        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT),
                new ListProperty(DataType.SHORT, "jdoe", DataType.INT));

        ElementType targetType = new ElementType(
                "foo",
                new Property("bar", DataType.INT),
                new Property("jdoe", DataType.INT));

        Element element0 = new Element(sourceType);
        element0.setIntList("jdoe", new int[]{42, 43, 44});
        Element expected0 = new Element(targetType);
        expected0.setInt("jdoe", 42);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(null);

        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType);

        assertEquals(expected0, target.readElement());
    }

    @Test
    public void changeListToNoListNoValue() throws IOException {
        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT),
                new ListProperty(DataType.SHORT, "jdoe", DataType.INT));

        ElementType targetType = new ElementType(
                "foo",
                new Property("bar", DataType.INT),
                new Property("jdoe", DataType.INT));

        Element element0 = new Element(sourceType);
        element0.setIntList("jdoe", new int[]{0});
        Element expected0 = new Element(targetType);
        expected0.setInt("jdoe", 0);

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);
        when(reader.readElement()).
                thenReturn(element0).
                thenReturn(null);

        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType);

        assertEquals(expected0, target.readElement());
    }

    @Test
    public void closeCallsClose() throws IOException {
        ElementType sourceType = new ElementType(
                "foo",
                new Property("bar", DataType.INT));

        ElementType targetType = new ElementType(
                "cat",
                new Property("dog", DataType.INT));

        ElementReader reader = mock(ElementReader.class);
        when(reader.getElementType()).thenReturn(sourceType);

        TypeChangingElementReader target = new TypeChangingElementReader(
                reader, targetType);

        target.close();

        verify(reader).close();
    }
}
