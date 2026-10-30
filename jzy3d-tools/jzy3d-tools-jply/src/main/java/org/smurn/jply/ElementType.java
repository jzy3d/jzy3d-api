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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Declaration of an element type.
 * <p>Each element in a PLY file has a type. The type defines what values
 * (properties) the element has.</p>
 * <p>Some element types are described in the PLY specification and should
 * only be used accordingly to ensure best compatibility with other
 * PLY applications.</p>
 * <dl>
 * <dt>{@code vertex}</dt>
 * <dd>Element type for vertex definitions. The {@code vertex} type should have
 * at least three (non-list) properties named {@code x}, {@code y} and {@code z}
 * for the position of the vertex. <br/>
 * The specification also mentions optional {@code red}, {@code green},
 * {@code blue} (non-list) properties to define the color of a vertex.<br/>
 * For more material properties a reference to a material definition can be
 * added to a vertex with the {@code material_index} property
 * (of an integer type). The value of that property is a (zero-based) index
 * to an element of type {@code material} (see below).</dd>
 * <dt>{@code face}</dt>
 * <dd>Element type for polygon definitions. The {@code face} type should have
 * at least one list-property called {@code vertex_index}, storing a list
 * of (zero-based) indicies into the {@code vertex} elements.<br/>
 * The indicies select the vertices that form the polygon. The vertices
 * should all lie on a common plane. There should be at least three
 * vertices per face.<br/>
 * Color and material of a polygon can be specified with the same properties
 * as used for vertices.
 * </dd>
 * <dt>{@code edge}</dt>
 * <dd>Element type for line segments. The {@code edge} type should
 * have at least two (non-list) properties {@code vertex1} and {@code vertex2},
 * storing a (zero-based) index into the {@code vertex} elements defining
 * the start and end point of the edge.<br/>
 * Color and material of an edge can be specified with the same properties
 * as used for vertices.</dd>
 * <dt>{@code material}</dt>
 * <dd>Type for material description elements. Each element describes
 * a material. Materials can be referenced from other elements using
 * the {@code material_index} property.<br>
 * There are several properties of a material mentioned in the specification:
 * <lu>
 * <li>{@code ambient_red}</li>
 * <li>{@code ambient_green}</li>
 * <li>{@code ambient_blue}</li>
 * <li>{@code ambient_coeff}</li>
 * <li>{@code diffuse_red}</li>
 * <li>{@code diffuse_green}</li>
 * <li>{@code diffuse_blue}</li>
 * <li>{@code diffuse_coeff}</li>
 * <li>{@code specular_red}</li>
 * <li>{@code specular_green}</li>
 * <li>{@code specular_blue}</li>
 * <li>{@code specular_coeff}</li>
 * <li>{@code specular_power}</li>
 * </lu>
 * </dd>
 * The data type mentioned in the specification is {@code float} for all
 * properties but indices where it should be {@code int} and color components
 * where it should be {@code uchar}.
 * </dl>
 * <p>All instances of this class are immutable.</p>
 */
public final class ElementType {

    /** Name of this type. */
    private final String name;
    /** Properties of this type. */
    private final List<Property> properties;
    /** Maps property names to property index. */
    private final Map<String, Integer> propertyMap;

    /**
     * Creates an instance.
     * @param name  Name of the element type.
     * @param properties Properties of the elements of this type.
     * Must not be {@code null}.
     * @throws NullPointerException if {@code name} or {@code properties} is
     * {@code null}.
     * @throws IllegalArgumentException if the properties don't have unique
     * names.
     */
    public ElementType(final String name, final List<Property> properties) {
        if (name == null) {
            throw new NullPointerException("name must not be null.");
        }
        if (properties == null) {
            throw new NullPointerException("properties must not be null.");
        }
        this.name = name;
        this.properties = Collections.unmodifiableList(
                new ArrayList<Property>(properties));

        HashMap<String, Integer> propertyMapTmp =
                new HashMap<String, Integer>();
        for (int i = 0; i < properties.size(); i++) {
            propertyMapTmp.put(properties.get(i).getName(), i);
        }
        this.propertyMap = Collections.unmodifiableMap(propertyMapTmp);
        if (propertyMap.size() != properties.size()) {
            throw new IllegalArgumentException(
                    "properties must have unique names.");
        }
    }

    /**
     * Creates an instance.
     * @param name  Name of the element type.
     * @param properties Properties of the elements of this type.
     * @throws NullPointerException if {@code name} or {@code properties} is
     * {@code null}.
     * @throws IllegalArgumentException if the properties don't have unique
     * names.
     */
    public ElementType(final String name, final Property... properties) {
        this(name, Arrays.asList(properties));
    }

    /**
     * Gets the name of the element type.
     * <p>Some names are given a specific meaning by the PLY specification and
     * should only be used accordingly. See {@link ElementType}.</p>
     * @return Name of the element type. Is never {@code null} or empty,
     * contains only ASCII characters.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all properties defined for this element type.
     * @return Immutable list of properties. I never {@code null}.
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * Gets the map that maps from property name to the index of that
     * property in the {@link #getProperties()} list.
     * @return Immutable map.
     */
    Map<String, Integer> getPropertyMap() {
        return propertyMap;
    }

    /**
     * Represents the result of parsing the header line.
     */
    static class HeaderEntry {

        private final String name;
        private final int count;

        /**
         * Creates an instance.
         * @param name  Name of the element type.
         * @param count Number of elements of this type.
         */
        public HeaderEntry(final String name, final int count) {
            this.name = name;
            this.count = count;
        }

        /**
         * Gets the number of elements of the type.
         * @return Number of elements.
         */
        public int getCount() {
            return count;
        }

        /**
         * Gets the name of the type.
         * @return Name of the type.
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Parses a header line starting an element description.
     * @param elementLine Header line.
     * @return ElementType without properties.
     * @throws IOException if the header line has an invalid format.
     */
    static HeaderEntry parse(final String elementLine) throws IOException {
        if (!elementLine.startsWith("element ")) {
            throw new IOException("not an element: '"
                    + elementLine + "'");
        }
        String definition = elementLine.substring("element ".length());

        String[] parts = definition.split(" +", 2);
        if (parts.length != 2) {
            throw new IOException("Expected two parts in element definition: '"
                    + elementLine + "'");
        }
        String name = parts[0];
        String countStr = parts[1];
        int count;
        try {
            count = Integer.parseInt(countStr);
        }
        catch (NumberFormatException e) {
            throw new IOException("Invalid element entry. Not an integer: '"
                    + countStr + "'.");
        }
        return new HeaderEntry(name, count);
    }

    @Override
    public String toString() {
        return "element " + name + " properties=" + properties;
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
        ElementType rhs = (ElementType) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(name, rhs.name);
        builder.append(properties, rhs.properties);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(name);
        builder.append(properties);
        return builder.toHashCode();
    }
}
