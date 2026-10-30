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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Reads elements from a PLY file in ASCII format.
 */
class AsciiElementReader implements ElementReader {

    /** Type of the elements we read. */
    private final ElementType type;

    /** Source to read from. */
    private final BufferedReader reader;

    /** Number of elements. */
    private final int count;

    /** Index of the next row to read. */
    private int nextRow = 0;

    /** Flag indicating if the user closed this reader. */
    private boolean closed = false;

    /**
     * Creates an instance.
     * @param type Type of the elements to read.
     * @param count Number of elements to read.
     * @param reader Source to read the elements from.
     */
    AsciiElementReader(final ElementType type, final int count,
            final BufferedReader reader) {
        if (type == null) {
            throw new NullPointerException("definition must not be null.");
        }
        if (reader == null) {
            throw new NullPointerException("reader must not be null.");
        }
        this.type = type;
        this.count = count;
        this.reader = reader;
    }

    @Override
    public ElementType getElementType() {
        return type;
    }

    @Override
    public Element readElement() throws IOException {
        if (closed) {
            throw new IllegalStateException("Reader is closed.");
        }
        if (nextRow >= getCount()) {
            return null;
        }

        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Unexpected end of file.");
        }
        line = line.trim();
        String[] parts = line.split(" +");
        double[] numbers = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            numbers[i] = Double.parseDouble(parts[i]);
        }

        // distribute the values to the properties
        int pos = 0;
        List<Property> properties = type.getProperties();
        double[][] values = new double[properties.size()][];
        for (int propI = 0; propI < properties.size(); propI++) {
            Property property = properties.get(propI);
            if (property instanceof ListProperty) {
                if (Math.abs(Math.round(numbers[pos]) - numbers[pos]) > 1E-6){
                    throw new IOException("array size is not integer.");
                }
                int valueCount = (int) Math.round(numbers[pos++]);
                
                values[propI] = Arrays.copyOfRange(numbers, pos,
                        pos + valueCount);
                pos += valueCount;
            } else {
                values[propI] = new double[]{numbers[pos++]};
            }
        }
        if (pos != numbers.length) {
            throw new IOException(
                    "Invalid PLY format. To many values for an element.");
        }

        nextRow++;
        return new Element(values, type);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void close() throws IOException {
        // Consume the remaining elements so that we
        // are at the right position for the next group of elements.
        while (nextRow < getCount()) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Unexpected end of file.");
            }
            nextRow++;
        }
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
