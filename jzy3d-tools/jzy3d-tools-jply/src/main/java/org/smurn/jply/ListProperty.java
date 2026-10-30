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
 * Declaration of a list property of an element.
 * <p>A list property stores a list of values for an element. The length
 * of the list can be different for every element.</p>
 * <p>Some property names have a pre-defined meaning in the specification
 * and should only be used accordingly. See {@link ElementType} for a list
 * and a description of those properties.</p>
 * <p>All instances of this class are immutable.</p>
 */
public class ListProperty extends Property {

    /** Type of the element count value. */
    private final DataType countType;

    /**
     * Creates an instance.
     * @param countType Data type of the value storing the number of
     * elements in the list.
     * @param name  Name of the property.
     * @param type Data type of this property.
     * @throws NullPointerException if {@code countType}, {@code name} or
     * {@code type} is {@code null}.
     */
    public ListProperty(final DataType countType, final String name,
            final DataType type) {
        super(name, type);
        if (countType == null) {
            throw new NullPointerException("countType must not be null.");
        }
        this.countType = countType;
    }

    /**
     * Gets the type of the value storing the number of elements in the list.
     * @return Data type. Is never {@code null}.
     */
    public DataType getCountType() {
        return countType;
    }

    /**
     * Parses a list-property header line.
     * @param line Header line.
     * @return Property described on the line.
     * @throws IOException if the header line as an invalid format.
     */
    static Property parse(final String line) throws IOException {
        if (!line.startsWith("property ")) {
            throw new IOException("not a property: '"
                    + line + "'");
        }
        String definition = line.substring("property ".length());
        definition = definition.trim();

        if (!definition.startsWith("list ")) {
            throw new IllegalArgumentException("not a list property: '"
                    + line + "'");
        }
        definition = definition.substring("list ".length());
        definition = definition.trim();

        String[] parts = definition.split(" +", 3);
        if (parts.length != 3) {
            throw new IOException("Expected three parts in list property "
                    + "definition: '" + line + "'");
        }
        String countType = parts[0];
        String type = parts[1];
        String name = parts[2];

        DataType dataType;
        DataType countDataType;
        try {
            dataType = DataType.parse(type);
            countDataType = DataType.parse(countType);
        }
        catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }

        return new ListProperty(countDataType, name, dataType);
    }

    @Override
    public String toString() {
        return "property list " + countType + " " + getType() + " " + getName();
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
        ListProperty rhs = (ListProperty) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.appendSuper(super.equals(obj));
        builder.append(countType, rhs.countType);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.appendSuper(super.hashCode());
        builder.append(countType);
        return builder.toHashCode();
    }
}
