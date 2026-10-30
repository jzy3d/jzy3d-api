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

import org.junit.Test;
import org.smurn.jply.DataType;
import org.smurn.jply.Element;
import org.smurn.jply.ElementType;
import org.smurn.jply.Property;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link PlanarTexGenStrategy}.
 */
public class PlanarTexGenStrategyTest {

    @Test
    public void xy() {

        ElementType type = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE),
                new Property("u", DataType.DOUBLE),
                new Property("v", DataType.DOUBLE));

        Element element = new Element(type);
        element.setDouble("x", 12);
        element.setDouble("y", 130);
        element.setDouble("z", 1400);

        Element expected = new Element(type);
        expected.setDouble("x", 12);
        expected.setDouble("y", 130);
        expected.setDouble("z", 1400);
        expected.setDouble("u", 0.2);
        expected.setDouble("v", 0.3);

        RectBounds bounds = new RectBounds(10, 20, 100, 200, 1000, 2000);

        PlanarTexGenStrategy target = new PlanarTexGenStrategy(Axis.X, Axis.Y);

        target.generateCoordinates(element, bounds);

        assertEquals(expected, element);
    }

    @Test
    public void xz() {

        ElementType type = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE),
                new Property("u", DataType.DOUBLE),
                new Property("v", DataType.DOUBLE));

        Element element = new Element(type);
        element.setDouble("x", 12);
        element.setDouble("y", 130);
        element.setDouble("z", 1400);

        Element expected = new Element(type);
        expected.setDouble("x", 12);
        expected.setDouble("y", 130);
        expected.setDouble("z", 1400);
        expected.setDouble("u", 0.2);
        expected.setDouble("v", 0.4);

        RectBounds bounds = new RectBounds(10, 20, 100, 200, 1000, 2000);

        PlanarTexGenStrategy target = new PlanarTexGenStrategy(Axis.X, Axis.Z);

        target.generateCoordinates(element, bounds);

        assertEquals(expected, element);
    }

    @Test
    public void yz() {

        ElementType type = new ElementType(
                "vertex",
                new Property("x", DataType.DOUBLE),
                new Property("y", DataType.DOUBLE),
                new Property("z", DataType.DOUBLE),
                new Property("u", DataType.DOUBLE),
                new Property("v", DataType.DOUBLE));

        Element element = new Element(type);
        element.setDouble("x", 12);
        element.setDouble("y", 130);
        element.setDouble("z", 1400);

        Element expected = new Element(type);
        expected.setDouble("x", 12);
        expected.setDouble("y", 130);
        expected.setDouble("z", 1400);
        expected.setDouble("u", 0.3);
        expected.setDouble("v", 0.4);

        RectBounds bounds = new RectBounds(10, 20, 100, 200, 1000, 2000);

        PlanarTexGenStrategy target = new PlanarTexGenStrategy(Axis.Y, Axis.Z);

        target.generateCoordinates(element, bounds);

        assertEquals(expected, element);
    }
}
