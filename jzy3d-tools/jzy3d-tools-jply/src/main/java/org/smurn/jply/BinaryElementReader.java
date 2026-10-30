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

import java.io.IOException;
import java.util.List;

/**
 * Reads elements from a PLY file in binary format (used both byte orders).
 */
class BinaryElementReader implements ElementReader {

    /** Type of the elements we read. */
    private final ElementType type;
    /** Stream to read the data from. */
    private final BinaryPlyInputStream stream;
    /** Number of elements. */
    private final int count;
    /** Index of the next row to read. */
    private int nextRow = 0;
    /** Flag indicating if the user closed this reader. */
    private boolean closed = false;

    /**
     * Creates an instance.
     * @param type Type of the elements to be read.
     * @param count Number of elements to be read.
     * @param stream Stream to read the data from. Must be configured
     * to use the right byte order.
     * @throws NullPointerException if {@code stream} is {@code null}.
     */
    BinaryElementReader(final ElementType type, final int count,
            final BinaryPlyInputStream stream) {
        if (type == null || stream == null) {
            throw new NullPointerException();
        }
        this.type = type;
        this.count = count;
        this.stream = stream;
    }

    @Override
    public ElementType getElementType() {
        return type;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Element readElement() throws IOException {
        if (closed) {
            throw new IllegalStateException("Reader is closed.");
        }
        if (nextRow >= getCount()) {
            return null;
        }

        List<Property> properties = type.getProperties();
        double[][] values = new double[properties.size()][];
        for (int i = 0; i < values.length; i++) {
            Property property = properties.get(i);
            if (property instanceof ListProperty) {
                values[i] = readListProperty((ListProperty) property);
            } else {
                values[i] = new double[]{readProperty(property)};
            }
        }

        nextRow++;
        return new Element(values, type);
    }

    /**
     * Read the value of a property.
     * @param property Property to read.
     * @return Value read.
     * @throws IOException if reading fails.
     */
    private double readProperty(final Property property) throws IOException {
        return stream.read(property.getType());
    }

    /**
     * Reads the values of a list-property.
     * @param property Property to read.
     * @return Values of that property.
     * @throws IOException if reading fails.
     */
    private double[] readListProperty(final ListProperty property)
            throws IOException {
        int valueCount = (int) stream.read(property.getCountType());
        double[] values = new double[valueCount];
        for (int i = 0; i < values.length; i++) {
            values[i] = stream.read(property.getType());
        }
        return values;
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}