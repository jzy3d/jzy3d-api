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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.ListProperty;
import org.smurn.jply.Property;

/**
 * Element reader that can change the type of the elements on the fly.
 * <p>The reader maps the elements from the source type to the target type.
 * The mapping rules are:</p>
 * <ul>
 * <li>Properties of both types are compared by their name.
 * Renaming mappings are applied.</li>
 * <li>Properties that only exist in the source type are removed.</li>
 * <li>Properties that only exist in the destination type are filled
 * either with 0 or with an empty list in the case of list-properties.</li>
 * <li>Properties that exist on both sides are kept:
 *   <ul>
 *   <li>In case of different data types java's casting rules are used.</li>
 *   <li>If a non-list-property is mapped to a list-property a list with a 
 *   single entry is created.</li>
 *   <li>If a list-property is mapped to a non-list-property the first value
 *   is taken and the rest is discarded. If the list is empty, the value
 *   is set to 0.</li>
 *   </ul>
 * </li>
 * </ul>
 */
class TypeChangingElementReader implements ElementReader {

    /** Source of the elements. */
    private final ElementReader reader;
    /** Type of the elements we produce. */
    private final ElementType targetType;
    /** Properties shared between the source and the target type that are
    non-list properties in the target. */
    private final List<String> sharedPropertiesNoList;
    /** Properties shared between the source and the target type that are
    list properties in the target. */
    private final List<String> sharedPropertiesList;
    /** Map for property renames. */
    private final Map<String, String> propertyNameMap;

    /**
     * Creates an instance.
     * @param reader Source of the elements.
     * @param targetType Type for the elements produced by this reader.
     * @throws NullPointerException if {@code reader} or {@code targetType}
     * is {@code null}.
     */
    TypeChangingElementReader(final ElementReader reader,
            final ElementType targetType) {
        this(reader, targetType, Collections.<String, String>emptyMap());
    }

    /**
     * Creates an instance.
     * @param reader Source of the elements.
     * @param targetType Type for the elements produced by this reader.
     * @param sourceNames Maps property names from the target type to the
     * source type. Only entries for properties that changed the name
     * are required.
     * @throws NullPointerException if {@code reader} or {@code targetType}
     * is {@code null}.
     */
    TypeChangingElementReader(final ElementReader reader,
            final ElementType targetType,
            final Map<String, String> sourceNames) {
        if (reader == null) {
            throw new NullPointerException("reader must not be null.");
        }
        if (targetType == null) {
            throw new NullPointerException("targetType must not be null.");
        }
        if (sourceNames == null) {
            throw new NullPointerException("renames must not be null.");
        }
        this.reader = reader;
        this.targetType = targetType;
        this.propertyNameMap = Collections.unmodifiableMap(
                new HashMap<String, String>(sourceNames));

        // find properties shared by both types
        ElementType sourceType = reader.getElementType();
        List<String> sharedPropertiesNoListTmp = new ArrayList<String>();
        List<String> sharedPropertiesListTmp = new ArrayList<String>();
        for (Property targetProp : targetType.getProperties()) {
            boolean match = false;
            for (Property sourceProp : sourceType.getProperties()) {
                if (sourceProp.getName().
                        equals(getSourceName(targetProp.getName()))) {
                    match = true;
                    break;
                }
            }
            if (match) {
                if (targetProp instanceof ListProperty) {
                    sharedPropertiesListTmp.add(targetProp.getName());
                } else {
                    sharedPropertiesNoListTmp.add(targetProp.getName());
                }
            }
        }
        this.sharedPropertiesNoList = Collections.unmodifiableList(
                sharedPropertiesNoListTmp);
        this.sharedPropertiesList = Collections.unmodifiableList(
                sharedPropertiesListTmp);
    }

    /**
     * Get the name of a property in the source type.
     * @param property Name of the property in the target type.
     * @return Name of a property in the source type.
     */
    private String getSourceName(final String property) {
        if (propertyNameMap.containsKey(property)) {
            return propertyNameMap.get(property);
        } else {
            return property;
        }
    }

    @Override
    public ElementType getElementType() {
        return targetType;
    }

    @Override
    public int getCount() {
        return reader.getCount();
    }

    @Override
    public Element readElement() throws IOException {
        Element source = reader.readElement();
        if (source == null) {
            return null;
        }
        Element target = new Element(targetType);

        for (String property : sharedPropertiesNoList) {
            double[] values = source.getDoubleList(getSourceName(property));
            if (values.length > 0) {
                target.setDouble(property, values[0]);
            }
        }
        for (String property : sharedPropertiesList) {
            double[] values = source.getDoubleList(getSourceName(property));
            target.setDoubleList(property, values);
        }
        return target;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public boolean isClosed() {
        return reader.isClosed();
    }
}
