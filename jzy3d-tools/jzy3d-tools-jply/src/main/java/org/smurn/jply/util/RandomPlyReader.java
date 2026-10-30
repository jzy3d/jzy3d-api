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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;

/**
 * Ply reader wrapper providing random order access to the element readers.
 * <p>This class caches read elements in memory.</p>
 */
class RandomPlyReader implements PlyReader {

    private final PlyReader reader;
    private final Map<String, BufferedElementReader> buffer;
    private int nextType = 0;
    private boolean closed = false;

    /**
     * Creates an instance.
     * @param reader Reader that provides the data.
     */
    public RandomPlyReader(final PlyReader reader) {
        if (reader == null) {
            throw new NullPointerException("reader must not be null.");
        }
        this.reader = reader;
        this.buffer = new HashMap<String, BufferedElementReader>();
        for (ElementType type : reader.getElementTypes()) {
            buffer.put(type.getName(), null);
        }
    }

    @Override
    public List<ElementType> getElementTypes() {
        return reader.getElementTypes();
    }

    @Override
    public int getElementCount(final String elementType) {
        try {
            return getElementReader(elementType).getCount();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns an element reader for the given element type.
     * @param elementType Element type.
     * @return Reader for the elements of the given type.
     * @throws IOException if reading fails.
     * @throws NullPointerException if {@code elementType} is {@code null}.
     * @throws IllegalArgumentException if there is no element with the given
     * name.
     * @throws IllegalStateException if this reader is closed.
     */
    public RandomElementReader getElementReader(final String elementType)
            throws IOException {
        if (elementType == null) {
            throw new NullPointerException("elementType must not be null.");
        }
        if (!buffer.containsKey(elementType)) {
            throw new IllegalArgumentException("No such element type.");
        }
        if (closed) {
            throw new IllegalStateException("Reader is closed.");
        }
        while (buffer.get(elementType) == null) {
            ElementReader eReader = reader.nextElementReader();
            BufferedElementReader bReader = new BufferedElementReader(eReader);
            bReader.detach();
            buffer.put(eReader.getElementType().getName(), bReader);
        }
        return buffer.get(elementType).duplicate();
    }

    @Override
    public RandomElementReader nextElementReader() throws IOException {
        if (closed) {
            throw new IllegalStateException("Reader is closed.");
        }
        if (nextType >= getElementTypes().size()) {
            return null;
        }
        String type = getElementTypes().get(nextType++).getName();
        return getElementReader(type);
    }

    @Override
    public List<String> getRawHeaders() {
        return reader.getRawHeaders();
    }

    @Override
    public void close() throws IOException {
        closed = true;
        reader.close();
    }
}
