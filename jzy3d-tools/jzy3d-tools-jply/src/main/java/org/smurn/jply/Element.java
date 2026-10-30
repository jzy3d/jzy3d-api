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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Element of a PLY file.
 * <p>Each element has a number of values that describe its properties. The
 * type of an element describes which properties are present in an element.</p>
 */
public final class Element implements Cloneable {

    /** Values of this element. The first index selects the property. */
    private final double[][] data;
    /** Type of this element. */
    private final ElementType type;
    /** Maps from property name to the index in {@code values}. */
    private final Map<String, Integer> propertyMap;

    /**
     * Creates an instance.
     * @param values Array containing an array of values for each property.
     * @param type Element type of the element.
     */
    Element(final double[][] values, final ElementType type) {
        if (values == null || type == null) {
            throw new NullPointerException();
        }
        this.data = values;
        this.type = type;
        this.propertyMap = type.getPropertyMap();
    }

    /**
     * Creates an instance with default values.
     * <p>Properties are set to 0, list properties are set to an empty list.</p>
     * @param type Type of the element to create.
     */
    public Element(final ElementType type) {
        this.type = type;
        this.propertyMap = type.getPropertyMap();
        List<Property> properties = type.getProperties();
        data = new double[properties.size()][];
        for (int i = 0; i < data.length; i++) {
            if (properties.get(i) instanceof ListProperty) {
                data[i] = new double[0];
            } else {
                data[i] = new double[]{0.0};
            }
        }
    }

    /**
     * Gets the type of this element.
     * @return Element type. Is never {@code null}.
     */
    public ElementType getType() {
        return type;
    }

    /**
     * Gets a property value of this element.
     * <p>If the property is a list-property, the first element is returned.</p>
     * @param propertyName Name of the property.
     * @return Value of the current element casted to {@code int}.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     * @throws IndexOutOfBoundsException if the requested property is a
     * list property with zero elements.
     */
    public int getInt(final String propertyName) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        if (data[index].length == 0) {
            throw new IndexOutOfBoundsException(
                    "The property value is a list with zero entries.");
        }
        return (int) data[index][0];
    }

    /**
     * Gets a property value of this element.
     * <p>If the property is a list-property, the first element is returned.</p>
     * @param propertyName Name of the property.
     * @return Value of the current element casted to {@code double}.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     * @throws IndexOutOfBoundsException if the requested property is a
     * list property with zero elements.
     */
    public double getDouble(final String propertyName) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        if (data[index].length == 0) {
            throw new IndexOutOfBoundsException(
                    "The property value is a list with zero entries.");
        }
        return (double) data[index][0];
    }

    /**
     * Gets a property value list of this element.
     * <p>If the property is not list-property, a list with a single
     * element is returned.</p>
     * @param propertyName Name of the property.
     * @return Values of the current element, each casted to {@code int}.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     */
    public int[] getIntList(final String propertyName) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        int[] v = new int[data[index].length];
        for (int i = 0; i < v.length; i++) {
            v[i] = (int) data[index][i];
        }
        return v;
    }

    /**
     * Gets a property value list of this element.
     * <p>If the property is not list-property, a list with a single
     * element is returned.</p>
     * @param propertyName Name of the property.
     * @return Values of the current element, each casted to {@code double}.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     */
    public double[] getDoubleList(final String propertyName) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        return data[index].clone();
    }

    /**
     * Sets the value of a property-list.
     * If the property is not a list, the first element is used.
     * @param propertyName Name of the property to set.
     * @param values Values to set for that property.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     * @throws IndexOutOfBoundsException if the property is not a list property
     * and the given array does not have exactly one element.
     */
    public void setDoubleList(final String propertyName,
            final double[] values) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        if (type.getProperties().get(index) instanceof ListProperty) {
            this.data[index] = values.clone();
        } else {
            if (values.length != 0) {
                throw new IndexOutOfBoundsException("property is not a list");
            }
            this.data[index][0] = values[0];
        }
    }

    /**
     * Sets the value of a property-list.
     * If the property is a list, the list will be set to a single entry.
     * @param propertyName Name of the property to set.
     * @param value Value to set for that property.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     */
    public void setDouble(final String propertyName, final double value) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        this.data[index] = new double[]{value};
    }

    /**
     * Sets the value of a property-list.
     * If the property is not a list, the first element is used.
     * @param propertyName Name of the property to set.
     * @param values Values to set for that property.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     * @throws IndexOutOfBoundsException if the property is not a list property
     * and the given array does not have exactly one element.
     */
    public void setIntList(final String propertyName, final int[] values) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        if (type.getProperties().get(index) instanceof ListProperty) {
            this.data[index] = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                this.data[index][i] = values[i];
            }
        } else {
            if (values.length != 0) {
                throw new IndexOutOfBoundsException("property is not a list");
            }
            this.data[index][0] = values[0];
        }
    }

    /**
     * Sets the value of a property-list.
     * If the property is a list, the list will be set to a single entry.
     * @param propertyName Name of the property to set.
     * @param value Value to set for that property.
     * @throws NullPointerException if {@code propertyName} is {@code null}.
     * @throws IllegalArgumentException if the element type does not have
     * a property with the given name.
     */
    public void setInt(final String propertyName, final int value) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName must not be null.");
        }
        Integer index = propertyMap.get(propertyName);
        if (index == null) {
            throw new IllegalArgumentException("non existent property: '"
                    + propertyName + "'.");
        }
        this.data[index] = new double[]{value};
    }

    @Override
    public Element clone() {
        double[][] clone = new double[data.length][];
        for (int i = 0; i < clone.length; i++) {
            clone[i] = data[i].clone();
        }
        return new Element(clone, type);
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
        Element rhs = (Element) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(type, rhs.type);
        builder.append(data, rhs.data);
        return builder.isEquals();
    }

    /**
     * Checks if two elements are approximately equal.
     * <p>Other than {@link #equals(java.lang.Object)} this method
     * allows to be tolerant to small numerical differences in the values.</p>
     * @param rhs Element to compare against.
     * @param epsilon Maximal allowed difference between two values.
     * @return {@code true} if they have the same type and structure
     * and no value differs by more than {@code epsilon}.
     */
    public boolean equals(final Element rhs, final double epsilon) {
        if (rhs == null) {
            return false;
        }
        if (rhs == this) {
            return true;
        }
        if (!this.type.equals(rhs.type)) {
            return false;
        }
        if (data.length != rhs.data.length) {
            return false;
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i].length != rhs.data[i].length) {
                return false;
            }
            for (int j = 0; j < data[i].length; j++) {
                if (Math.abs(data[i][j] - rhs.data[i][j]) > epsilon) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(type);
        builder.append(data);
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Element ");
        str.append(type.getName());
        str.append(" {");
        for (int i = 0; i < type.getProperties().size(); i++) {
            Property property = type.getProperties().get(i);
            if (i > 0) {
                str.append(" ");
            }
            str.append(property.getName());
            str.append("=");
            if (property instanceof ListProperty) {
                str.append(Arrays.toString(data[i]));
            } else {
                str.append(data[i][0]);
            }
        }
        str.append("}");
        return str.toString();
    }
}
