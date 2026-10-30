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
 * Unit tests for {@link RandomPlyReader}.
 */
public class RandomPlyReaderTest {

    @Test
    public void sequential() throws IOException {

        ElementType type1 = new ElementType(
                "type1", new Property("a", DataType.DOUBLE));
        ElementType type2 = new ElementType(
                "type2", new Property("b", DataType.DOUBLE));

        Element e1 = new Element(type1);
        Element e2 = new Element(type2);

        ElementReader reader1 = mock(ElementReader.class);
        when(reader1.getElementType()).thenReturn(type1);
        when(reader1.getCount()).thenReturn(1);
        when(reader1.readElement()).thenReturn(e1).thenReturn(null);

        ElementReader reader2 = mock(ElementReader.class);
        when(reader2.getElementType()).thenReturn(type2);
        when(reader2.getCount()).thenReturn(1);
        when(reader2.readElement()).thenReturn(e2).thenReturn(null);

        PlyReader reader = mock(PlyReader.class);
        when(reader.getElementTypes()).thenReturn(Arrays.asList(type1, type2));
        when(reader.nextElementReader()).
                thenReturn(reader1).thenReturn(reader2).thenReturn(null);

        PlyReader target = new RandomPlyReader(reader);

        ElementReader t1 = target.nextElementReader();
        assertEquals(e1, t1.readElement());
        t1.close();
        ElementReader t2 = target.nextElementReader();
        assertEquals(e2, t2.readElement());
        t2.close();
    }

    @Test
    public void random() throws IOException {

        ElementType type1 = new ElementType(
                "type1", new Property("a", DataType.DOUBLE));
        ElementType type2 = new ElementType(
                "type2", new Property("b", DataType.DOUBLE));

        Element e1 = new Element(type1);
        Element e2 = new Element(type2);

        ElementReader reader1 = mock(ElementReader.class);
        when(reader1.getElementType()).thenReturn(type1);
        when(reader1.getCount()).thenReturn(1);
        when(reader1.readElement()).thenReturn(e1).thenReturn(null);

        ElementReader reader2 = mock(ElementReader.class);
        when(reader2.getElementType()).thenReturn(type2);
        when(reader2.getCount()).thenReturn(1);
        when(reader2.readElement()).thenReturn(e2).thenReturn(null);

        PlyReader reader = mock(PlyReader.class);
        when(reader.getElementTypes()).thenReturn(Arrays.asList(type1, type2));
        when(reader.nextElementReader()).
                thenReturn(reader1).thenReturn(reader2).thenReturn(null);

        RandomPlyReader target = new RandomPlyReader(reader);

        RandomElementReader t2 = target.getElementReader("type2");
        assertEquals(e2, t2.readElement());
        t2.close();
        ElementReader t1 = target.getElementReader("type1");
        assertEquals(e1, t1.readElement());
        t1.close();
    }

    @Test
    public void readTwice() throws IOException {

        ElementType type1 = new ElementType(
                "type1", new Property("a", DataType.DOUBLE));
        ElementType type2 = new ElementType(
                "type2", new Property("b", DataType.DOUBLE));

        Element e1 = new Element(type1);
        Element e2 = new Element(type2);

        ElementReader reader1 = mock(ElementReader.class);
        when(reader1.getElementType()).thenReturn(type1);
        when(reader1.getCount()).thenReturn(1);
        when(reader1.readElement()).thenReturn(e1).thenReturn(null);

        ElementReader reader2 = mock(ElementReader.class);
        when(reader2.getElementType()).thenReturn(type2);
        when(reader2.getCount()).thenReturn(1);
        when(reader2.readElement()).thenReturn(e2).thenReturn(null);

        PlyReader reader = mock(PlyReader.class);
        when(reader.getElementTypes()).thenReturn(Arrays.asList(type1, type2));
        when(reader.nextElementReader()).
                thenReturn(reader1).thenReturn(reader2).thenReturn(null);

        RandomPlyReader target = new RandomPlyReader(reader);

        RandomElementReader t2 = target.getElementReader("type2");
        assertEquals(e2, t2.readElement());
        t2.close();
        ElementReader t1 = target.getElementReader("type2");
        assertEquals(e2, t1.readElement());
        t1.close();
    }
}
