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
import java.util.Arrays;
import org.junit.Test;
import org.smurn.jply.DataType;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;
import org.smurn.jply.Property;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link WrappingPlyReader}.
 */
public class WrappingPlyReaderTest {

    @Test
    public void passthrough() throws IOException {

        ElementType typeA = new ElementType(
                "ta", new Property("a", DataType.DOUBLE));
        ElementType typeB = new ElementType(
                "tb", new Property("b", DataType.DOUBLE));

        ElementReader readerA = mock(ElementReader.class);
        when(readerA.getCount()).thenReturn(0);
        when(readerA.getElementType()).thenReturn(typeA);

        ElementReader readerB = mock(ElementReader.class);
        when(readerB.getCount()).thenReturn(0);
        when(readerB.getElementType()).thenReturn(typeB);

        PlyReader plyReader = mock(PlyReader.class);
        when(plyReader.getElementTypes()).thenReturn(Arrays.asList(typeA, typeB));
        when(plyReader.getElementCount(anyString())).thenReturn(0);
        when(plyReader.nextElementReader()).
                thenReturn(readerA).
                thenReturn(readerB).
                thenReturn(null);

        WrappingPlyReader target = new WrappingPlyReader(plyReader);

        assertEquals(Arrays.asList(typeA, typeB), target.getElementTypes());
        assertEquals(readerA, target.nextElementReader());
        assertEquals(readerB, target.nextElementReader());
        assertEquals(null, target.nextElementReader());
    }

    @Test
    public void wrapOne() throws IOException {

        ElementType typeA = new ElementType(
                "ta", new Property("a", DataType.DOUBLE));
        ElementType typeB = new ElementType(
                "tb", new Property("b", DataType.DOUBLE));
        ElementType typeC = new ElementType(
                "tc", new Property("c", DataType.DOUBLE));

        ElementReader readerA = mock(ElementReader.class);
        when(readerA.getCount()).thenReturn(0);
        when(readerA.getElementType()).thenReturn(typeA);

        ElementReader readerB = mock(ElementReader.class);
        when(readerB.getCount()).thenReturn(0);
        when(readerB.getElementType()).thenReturn(typeB);

        final ElementReader readerC = mock(ElementReader.class);
        when(readerC.getCount()).thenReturn(0);
        when(readerC.getElementType()).thenReturn(typeC);

        PlyReader plyReader = mock(PlyReader.class);
        when(plyReader.getElementTypes()).thenReturn(Arrays.asList(typeA, typeB));
        when(plyReader.getElementCount(anyString())).thenReturn(0);
        when(plyReader.nextElementReader()).
                thenReturn(readerA).
                thenReturn(readerB).
                thenReturn(null);

        WrappingPlyReader.WrapperFactory factory =
                new WrappingPlyReader.WrapperFactory(typeA, typeC) {

                    @Override
                    public ElementReader wrap(ElementReader reader) {
                        return readerC;
                    }
                };

        WrappingPlyReader target = new WrappingPlyReader(plyReader, factory);

        assertEquals(Arrays.asList(typeC, typeB), target.getElementTypes());
        assertEquals(readerC, target.nextElementReader());
        assertEquals(readerB, target.nextElementReader());
        assertEquals(null, target.nextElementReader());
    }

    @Test
    public void wrapAll() throws IOException {

        ElementType typeA = new ElementType(
                "ta", new Property("a", DataType.DOUBLE));
        ElementType typeB = new ElementType(
                "tb", new Property("b", DataType.DOUBLE));
        ElementType typeC = new ElementType(
                "tc", new Property("c", DataType.DOUBLE));
        ElementType typeD = new ElementType(
                "td", new Property("d", DataType.DOUBLE));

        ElementReader readerA = mock(ElementReader.class);
        when(readerA.getCount()).thenReturn(0);
        when(readerA.getElementType()).thenReturn(typeA);

        ElementReader readerB = mock(ElementReader.class);
        when(readerB.getCount()).thenReturn(0);
        when(readerB.getElementType()).thenReturn(typeB);

        final ElementReader readerC = mock(ElementReader.class);
        when(readerC.getCount()).thenReturn(0);
        when(readerC.getElementType()).thenReturn(typeC);

        final ElementReader readerD = mock(ElementReader.class);
        when(readerD.getCount()).thenReturn(0);
        when(readerD.getElementType()).thenReturn(typeD);

        PlyReader plyReader = mock(PlyReader.class);
        when(plyReader.getElementTypes()).thenReturn(Arrays.asList(typeA, typeB));
        when(plyReader.getElementCount(anyString())).thenReturn(0);
        when(plyReader.nextElementReader()).
                thenReturn(readerA).
                thenReturn(readerB).
                thenReturn(null);

        WrappingPlyReader.WrapperFactory factory1 =
                new WrappingPlyReader.WrapperFactory(typeA, typeC) {

                    @Override
                    public ElementReader wrap(ElementReader reader) {
                        return readerC;
                    }
                };
        WrappingPlyReader.WrapperFactory factory2 =
                new WrappingPlyReader.WrapperFactory(typeB, typeD) {

                    @Override
                    public ElementReader wrap(ElementReader reader) {
                        return readerD;
                    }
                };

        WrappingPlyReader target = new WrappingPlyReader(plyReader,
                factory1, factory2);

        assertEquals(Arrays.asList(typeC, typeD), target.getElementTypes());
        assertEquals(readerC, target.nextElementReader());
        assertEquals(readerD, target.nextElementReader());
        assertEquals(null, target.nextElementReader());
    }
}
