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
import java.util.List;

/**
 * Interface for classes reading PLY file structures.
 */
public interface PlyReader extends Closeable{

    /**
     * Gets all element types in this PLY file.
     * <p>The order of the list
     * is the same in which the corresponding readers are returned
     * by {@link #nextElementReader()}.</p>
     * @return Immutable list with all element types.
     */
    List<ElementType> getElementTypes();

    /**
     * Gets the number of elements for a given element type.
     * @param elementType Name of the element type.
     * @return Number of elements of the given type.
     * @throws NullPointerException if {@code elementType} is {@code null}.
     * @throws IllegalArgumentException if there is no such type in this
     * file.
     */
    int getElementCount(final String elementType);

    /**
     * Returns the reader to read the first group of elements.
     * <p>Each group corresponds to an element type. The groups are
     * returned in the order given in the file. This is also the
     * same order as in the list given by {@link #getElementTypes()}.</p>
     * <p>Each returned reader must be closed before the next reader
     * is requested with this method.</p>
     * @return Reader for the next group of elements or {@code null} if
     * there are no more groups.
     * @throws IOException if an error occurs during reading.
     */
    ElementReader nextElementReader() throws IOException;

    /**
     * Returns all the raw lines in the ply header, including
     * lines that are not supported by this library.
     *
     * @return The list of header lines
     */
    List<String> getRawHeaders();

    /**
     * Closes the file.
     * @throws IOException if closing fails. 
     */
    void close() throws IOException;
}
