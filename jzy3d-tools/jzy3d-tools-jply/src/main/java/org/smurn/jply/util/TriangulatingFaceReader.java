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
import java.util.LinkedList;
import java.util.List;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.Property;

/**
 * Special reader for face elements producing only triangles.
 * <p>Wraps a {@link ElementReader} and splits faces with more than three
 * triangles up on the fly.</p>
 * <p>The winding order of the faces is not changed.</p>
 */
class TriangulatingFaceReader implements ElementReader {

    /** Source of the faces. */
    private final ElementReader reader;
    /**
     * Elements (triangular faces) to be returned on the next read operations
     * before the next face is read from the source reader.
     */
    private final List<Element> elementQueue = new LinkedList<Element>();
    /** If this stream has been closed. */
    private boolean closed = false;

    /**
     * Creates an instance.
     * @param reader Reader reading face elements with potentially
     * more than 3 vertices per face.
     * @throws NullPointerException if {@code reader} is {@code null}.
     * @throws IllegalArgumentException if the reader does not read faces
     * or if the faces do not have a vertex_index list.
     */
    TriangulatingFaceReader(final ElementReader reader) {
        if (reader == null) {
            throw new NullPointerException("reader must not be null.");
        }
        if (!"face".equals(reader.getElementType().getName())) {
            throw new IllegalArgumentException("This class can only be used"
                    + " on face readers.");
        }
        boolean found = false;
        for (Property p : reader.getElementType().getProperties()) {
            if ("vertex_index".equals(p.getName())) {
                found = true;
            }
        }
        if (!found) {
            throw new IllegalArgumentException(
                    "Face element as no vertex_index property.");
        }
        this.reader = reader;
    }

    @Override
    public ElementType getElementType() {
        return reader.getElementType();
    }

    @Override
    public int getCount() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Element readElement() throws IOException {
        if (closed) {
            throw new IllegalStateException("Reader is closed.");
        }
        if (elementQueue.isEmpty()) {
            Element element = reader.readElement();
            if (element == null) {
                return null;
            }
            int[] indices = element.getIntList("vertex_index");
            if (indices.length == 3) {
                return element;
            } else if (indices.length < 3) {
                throw new IOException("face with less than three vertices.");
            } else {
                for (int i = 0; i < indices.length - 2; i++) {
                    Element triangle = element.clone();
                    triangle.setIntList("vertex_index", new int[]{
                                indices[0],
                                indices[i + 1],
                                indices[i + 2]
                            });
                    elementQueue.add(triangle);
                }
            }
        }
        return elementQueue.remove(0);
    }

    @Override
    public void close() throws IOException {
        reader.close();
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
