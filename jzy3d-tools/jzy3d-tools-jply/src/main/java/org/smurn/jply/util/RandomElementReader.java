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

/**
 * Extends the element reader interface with random access functionality.
 */
interface RandomElementReader extends ElementReader {

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
    Element readElement(final int index) throws IOException;

    /**
     * Creates a duplicate of this stream.
     * <p>Closing the duplicate will not close this stream.</p>
     * <p>Both stream have a independent current position.</p>
     * @return Stream that can be closed without affecting this stream.
     */
    RandomElementReader duplicate();
}
