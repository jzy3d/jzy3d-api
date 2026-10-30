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

import java.io.Closeable;
import java.io.IOException;

/**
 * Reads a stream of elements.
 */
public interface ElementReader extends Closeable{

    /**
     * The element type of all elements read by this stream.
     * @return Element type of all elements. Never {@code null}.
     */
    ElementType getElementType();

    /**
     * Total number of elements provided by this stream.
     * <p>This is equivalent to {@code PlyReader.getElementCount(...)}.</p>
     * @return Number of elements.
     */
    int getCount();

    /**
     * Reads the next element.
     * @return The next element or {@code null} if there are no more
     * elements.
     * @throws IOException if reading fails.
     * @throws IllegalStateException if the stream is closed.
     */
    Element readElement() throws IOException;

    /**
     * Closes this stream.
     * @throws IOException if closing the stream fails.
     */
    void close() throws IOException;

    /**
     * Checks if this stream is closed.
     * @return {@code true} if the stream was closed already.
     */
    boolean isClosed();
}
