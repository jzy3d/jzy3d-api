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
 * Unit tests for {@link AxisShufflingVertexReader}.
 */
public class AxisShufflingVertexReaderTest {

    @Test
    public void shuffle() throws IOException {
        ElementType vertexType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE));

        Element vertex = new Element(vertexType);
        vertex.setDouble("x", 1);
        vertex.setDouble("y", 2);
        vertex.setDouble("z", 3);

        ElementReader vertexReaderMock = mock(ElementReader.class);
        when(vertexReaderMock.getElementType()).thenReturn(vertexType);
        when(vertexReaderMock.readElement()).
                thenReturn(vertex).thenReturn(null);

        ElementReader target = new AxisShufflingVertexReader(vertexReaderMock,
                Axis.Y, Axis.Z, Axis.X);

        Element expected = new Element(vertexType);
        expected.setDouble("x", 2);
        expected.setDouble("y", 3);
        expected.setDouble("z", 1);

        assertEquals(expected, target.readElement());
    }

    @Test
    public void invert() throws IOException {
        ElementType vertexType = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE));

        Element vertex = new Element(vertexType);
        vertex.setDouble("x", 1);
        vertex.setDouble("y", 2);
        vertex.setDouble("z", 3);

        ElementReader vertexReaderMock = mock(ElementReader.class);
        when(vertexReaderMock.getElementType()).thenReturn(vertexType);
        when(vertexReaderMock.readElement()).
                thenReturn(vertex).thenReturn(null);

        ElementReader target = new AxisShufflingVertexReader(vertexReaderMock,
                Axis.X_INVERTED, Axis.Y_INVERTED, Axis.Z_INVERTED);

        Element expected = new Element(vertexType);
        expected.setDouble("x", -1);
        expected.setDouble("y", -2);
        expected.setDouble("z", -3);

        assertEquals(expected, target.readElement());
    }
}
