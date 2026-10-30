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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;

/**
 * PlyReader that can be used to wrap individual element readers.
 */
class WrappingPlyReader implements PlyReader {

    private final PlyReader reader;
    private final Map<String, WrapperFactory> unwrappedMap;
    private final Map<String, WrapperFactory> wrappedMap;
    private final List<ElementType> elementTypes;

    /**
     * Factory that can wrap element readers.
     */
    abstract static class WrapperFactory {

        private final ElementType unwrappedType;
        private final ElementType wrappedType;

        /**
         * Creates a wrapper factory.
         * @param unwrappedType Type before wrapping.
         * @param wrappedType Type after wrapping.
         */
        WrapperFactory(final ElementType unwrappedType,
                final ElementType wrappedType) {
            this.unwrappedType = unwrappedType;
            this.wrappedType = wrappedType;
        }

        /**
         * Gets the type before wrapping.
         * @return Type before wrapping.
         */
        public final ElementType getUnwrappedType() {
            return unwrappedType;
        }

        /**
         * Gets the type after wrapping.
         * @return Type after wrapping.
         */
        public final ElementType getWrappedType() {
            return wrappedType;
        }

        /**
         * Wraps an element reader.
         * @param reader Element reader to wrap.
         * @return Wrapped element reader. May be the same reader if
         * no wrapping is intended.
         */
        public abstract ElementReader wrap(final ElementReader reader);
    }

    /**
     * Creates an instance.
     * @param reader    Source of the unwrapped data.
     * @param wrappers  Wrappers to apply to the data.
     * @throws NullPointerException if {@code reader} is {@code null}.
     */
    WrappingPlyReader(final PlyReader reader,
            final WrapperFactory... wrappers) {
        this(reader, Arrays.asList(wrappers));
    }

    /**
     * Creates an instance.
     * @param reader    Source of the unwrapped data.
     * @param wrappers  Wrappers to apply to the data.
     * @throws NullPointerException if {@code reader} or {@code wrappers}
     * is {@code null}.
     */
    WrappingPlyReader(final PlyReader reader,
            final Iterable<WrapperFactory> wrappers) {
        if (reader == null) {
            throw new NullPointerException("reader must not be null.");
        }
        if (wrappers == null) {
            throw new NullPointerException("wrappers must not be null.");
        }
        this.reader = reader;

        // put wrappers in the maps
        Map<String, WrapperFactory> unwrappedMapTmp =
                new HashMap<String, WrapperFactory>();
        Map<String, WrapperFactory> wrappedMapTmp =
                new HashMap<String, WrapperFactory>();
        for (WrapperFactory wrapper : wrappers) {
            unwrappedMapTmp.put(wrapper.getUnwrappedType().getName(), wrapper);
            wrappedMapTmp.put(wrapper.getWrappedType().getName(), wrapper);
        }
        this.unwrappedMap = Collections.unmodifiableMap(unwrappedMapTmp);
        this.wrappedMap = Collections.unmodifiableMap(wrappedMapTmp);

        // build output type list
        List<ElementType> types = new ArrayList<ElementType>();
        for (ElementType type : reader.getElementTypes()) {
            if (unwrappedMapTmp.containsKey(type.getName())) {
                types.add(unwrappedMapTmp.get(type.getName()).getWrappedType());
            } else {
                types.add(type);
            }
        }
        this.elementTypes = Collections.unmodifiableList(types);
    }

    @Override
    public List<ElementType> getElementTypes() {
        return elementTypes;
    }

    /**
     * This method is NOT supported on this type since the wrappers
     * might change the element count.
     * <p>If the count is needed, consider wrapping this PLY reader in a
     * {@link RandomPlyReader}. This will ensure that this method works as
     * expected.</p>
     * @param elementType Name of the element type.
     * @return Number of elements of the given type.
     * @throws UnsupportedOperationException IS ALWAYS THROWN.
     */
    @Override
    public int getElementCount(final String elementType) {
        throw new UnsupportedOperationException(
                "Wrappers might change the element count.");
    }

    @Override
    public ElementReader nextElementReader() throws IOException {
        ElementReader unwrapped = reader.nextElementReader();
        if (unwrapped == null) {
            return null;
        }
        WrapperFactory factory =
                unwrappedMap.get(unwrapped.getElementType().getName());
        if (factory != null) {
            return factory.wrap(unwrapped);
        } else {
            return unwrapped;
        }
    }

    @Override
    public List<String> getRawHeaders() {
        return reader.getRawHeaders();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
