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
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;

/**
 * Element reader for vertices that exchanges axes.
 */
class AxisShufflingVertexReader implements ElementReader {

    private final ElementReader reader;
    private final Axis xAxis;
    private final Axis yAxis;
    private final Axis zAxis;

    /**
     * Creates an instance.
     * @param reader    Reader that provides the elements.
     * @param x The axis in the source to map to the x in the normalized data.
     * @param y The axis in the source to map to the y in the normalized data.
     * @param z The axis in the source to map to the z in the normalized data.
     */
    public AxisShufflingVertexReader(final ElementReader reader,
            final Axis x, final Axis y, final Axis z) {
        if (reader == null) {
            throw new NullPointerException("reader must not be null");
        }
        if (x == null) {
            throw new NullPointerException("x must not be null.");
        }
        if (y == null) {
            throw new NullPointerException("y must not be null.");
        }
        if (z == null) {
            throw new NullPointerException("z must not be null.");
        }
        if (!"vertex".equals(reader.getElementType().getName())) {
            throw new IllegalArgumentException("This class can only be used"
                    + " on vertex readers.");
        }
        this.reader = reader;
        this.xAxis = x;
        this.yAxis = y;
        this.zAxis = z;
    }

    @Override
    public ElementType getElementType() {
        return reader.getElementType();
    }

    @Override
    public int getCount() {
        return reader.getCount();
    }

    @Override
    public Element readElement() throws IOException {
        Element element = reader.readElement();
        if (element == null) {
            return null;
        }

        double x = element.getDouble("x");
        double y = element.getDouble("y");
        double z = element.getDouble("z");

        element = element.clone();
        element.setDouble("x", getValue(x, y, z, this.xAxis));
        element.setDouble("y", getValue(x, y, z, this.yAxis));
        element.setDouble("z", getValue(x, y, z, this.zAxis));
        return element;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public boolean isClosed() {
        return reader.isClosed();
    }

    /**
     * Returns the value of a given axis.
     * @param x Input x value.
     * @param y Input y value.
     * @param z Input z value.
     * @param axis Axis to return.
     * @return The value of a given axis.
     */
    private double getValue(final double x, final double y, final double z,
            final Axis axis) {
        switch (axis) {
            case X:
                return x;
            case X_INVERTED:
                return -x;
            case Y:
                return y;
            case Y_INVERTED:
                return -y;
            case Z:
                return z;
            case Z_INVERTED:
                return -z;
            default:
                throw new IllegalArgumentException("Unsupported axis.");
        }
    }
}
