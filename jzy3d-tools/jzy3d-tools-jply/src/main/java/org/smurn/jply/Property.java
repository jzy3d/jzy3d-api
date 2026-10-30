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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Declaration of a property of an element.
 * <p>Each element is defined by its properties, a vertex for example typically
 * has at least the three properties for x, y and z.</p>
 * <p>There are two different kind of properties in PLY files, the normal
 * ones that add a single value to an element, and list properties that
 * add a variable length list of values to an element. The first one are
 * declared by this class, the list properties by the sub-class
 * {@link ListProperty}.</p>
 * <p>Some property names have a pre-defined meaning in the specification
 * and should only be used accordingly. See {@link ElementType} for a list
 * and a description of those properties.</p>
 * <p>All instances of this class are immutable.</p>
 */
public class Property {

    /** Name of this property. */
    private final String name;
    /** Type of this property. */
    private final DataType type;

    /**
     * Creates an instance.
     * @param name  Name of the property.
     * @param type Data type of this property.
     * @throws NullPointerException if {@code name} or {@code type} is
     * {@code null}.
     */
    public Property(final String name, final DataType type) {
        if (name == null) {
            throw new NullPointerException("name must not be null.");
        }
        if (type == null) {
            throw new NullPointerException("type must not be null.");
        }
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name of this property.
     * @return Name of this property. Is never {@code null} or empty,
     * contains only ASCII characters.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the data type of this property.
     * @return Data type of this property. Is never {@code null}.
     */
    public DataType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "property " + type + " " + name;
    }

    /**
     * Parses a property header line.
     * @param propertyLine Header line.
     * @return Property described on the line.
     * @throws IOException if the header line as an invalid format.
     */
    static Property parse(final String propertyLine) throws IOException {
        if (!propertyLine.startsWith("property ")) {
            throw new IOException("not a property: '"
                    + propertyLine + "'");
        }
        String definition = propertyLine.substring("property ".length());

        if (definition.startsWith("list")) {
            return ListProperty.parse(propertyLine);
        }

        String[] parts = definition.split(" +", 2);
        if (parts.length != 2) {
            throw new IOException("Expected two parts in property definition: '"
                    + propertyLine + "'");
        }
        String type = parts[0];
        String name = parts[1];

        DataType dataType;
        try {
            dataType = DataType.parse(type);
        }
        catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }

        return new Property(name, dataType);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Property rhs = (Property) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(name, rhs.name);
        builder.append(type, rhs.type);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(name);
        builder.append(type);
        return builder.toHashCode();
    }
}
