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
import java.util.ArrayList;
import java.util.List;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;

/**
 * Element reader with random access to the elements.
 * <p>This reader wraps another reader and buffers
 * all elements in memory to provide random access to them. The elements
 * are not changed (nor the order of them).</p>
 * <p>Note that elements are mutable. Changing an element will also
 * change the element buffered in this reader. Reading it again will produce
 * the same instance.</p>
 * <p>The order in which the elements are returned by {@link #readElement()}
 * is not affected by calls to {@link #readElement(int)}. Nor must an element
 * first be read by {@link #readElement()} before it can be accessed by
 * {@link #readElement(int)}. The two methods are independent of each other.</p>
 */
class BufferedElementReader implements RandomElementReader {

    private final ElementReader reader;
    private List<Element> buffer = new ArrayList<Element>();
    private int nextElement = 0;
    private boolean closed = false;
    private boolean sourceClosed = false;

    /**
     * Creates an instance.
     * @param reader Source of the elements. The same elements will
     * be provided by this reader and in the same order.
     * @throws NullPointerException if {@code reader} is {@code null}.
     */
    BufferedElementReader(final ElementReader reader) {
        if (reader == null) {
            throw new NullPointerException("reader must not be null.");
        }
        this.reader = reader;
    }

    @Override
    public ElementType getElementType() {
        return reader.getElementType();
    }

    @Override
    public int getCount() {
        if (sourceClosed) {
            return buffer.size();
        } else {
            return reader.getCount();
        }
    }

    /**
     * Reads an element at a specific position.
     * <p>This method does not influence the order in which the elements
     * are read by {@link #readElement()}.</p>
     * <p>The execution time of this method can be in the order of
     * {@code O(index)} in the worst case. On average its {@code O(1)}.</p>
     * @param index Zero-bound index of the element to read.
     * @return Element at the given index.
     * @throws IOException if reading fails.
     * @throws IndexOutOfBoundsException if the index is out of range (negative
     * or greater-equal to the number of elements).
     */
    @Override
    public Element readElement(final int index) throws IOException {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index is negative.");
        }
        if (closed) {
            throw new IllegalStateException("Reader is closed.");
        }
        while (index >= buffer.size()) {
            Element element = reader.readElement();
            if (element == null) {
                throw new IndexOutOfBoundsException(
                        "Index is larger or equal to the number of elements.");
            } else {
                buffer.add(element);
            }
        }
        return buffer.get(index);
    }

    @Override
    public Element readElement() throws IOException {
        if (closed) {
            throw new IllegalStateException("Reader is closed.");
        }
        while (nextElement >= buffer.size()) {
            Element element;
            if (sourceClosed) {
                // we have read everything already.
                element = null;
            } else {
                element = reader.readElement();
            }
            if (element == null) {
                return null;
            } else {
                buffer.add(element);
            }
        }
        return buffer.get(nextElement++);
    }

    /**
     * Resets this stream to the starting position.
     * <p>After calling this the next call to {@link #readElement()}
     * will return the first element again.</p>
     */
    public void reset() {
        nextElement = 0;
    }

    /**
     * Creates a duplicate of this stream.
     * <p>Closing the duplicate will not close this stream.</p>
     * <p>Both stream have a independent current position.</p>
     * @return Stream that can be closed without affecting this stream.
     */
    @Override
    public RandomElementReader duplicate() {
        if (closed) {
            throw new IllegalStateException("Reader is closed.");
        }

        return new RandomElementReader() {

            private boolean closed = false;
            private int nextElement = 0;

            @Override
            public Element readElement(int index) throws IOException {
                if (closed) {
                    throw new IllegalStateException("Reader closed");
                }
                return BufferedElementReader.this.readElement(index);
            }

            @Override
            public ElementType getElementType() {
                return BufferedElementReader.this.getElementType();
            }

            @Override
            public int getCount() {
                return BufferedElementReader.this.getCount();
            }

            @Override
            public Element readElement() throws IOException {

                if (nextElement >= getCount()) {
                    return null;
                }
                if (closed) {
                    throw new IllegalStateException("Reader closed");
                }
                return BufferedElementReader.this.readElement(nextElement++);
            }

            @Override
            public void close() throws IOException {
                closed = true;
            }

            @Override
            public RandomElementReader duplicate() {
                return BufferedElementReader.this.duplicate();
            }

            @Override
            public boolean isClosed() {
                return closed;
            }
        };
    }

    /**
     * Detaches this stream from the underlying stream by buffering every
     * element.
     * <P>This method ensures that all elements from the underlying stream
     * have been read and stored in the internal buffer. The underlying
     * stream is then closed, but this stream is kept open.</p>
     * <p>This stream should still be closed using {@link #close()} once
     * no longer needed.</p>
     * @throws IOException if reading fails.
     * @throws IllegalStateException if the stream is closed.
     */
    public void detach() throws IOException {
        if (closed) {
            throw new IllegalStateException("Reader is closed.");
        }

        for (Element element = reader.readElement();
                element != null; element = reader.readElement()) {
            buffer.add(element);
        }

        reader.close();
        sourceClosed = true;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        sourceClosed = true;
        buffer = null;  // let the GC have it.
        reader.close();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
