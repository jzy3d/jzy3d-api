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

/**
 * Data type of values stored in PLY files.
 */
public enum DataType {

    /** One byte signed integer. */
    CHAR,
    /** One byte unsigned integer. */
    UCHAR,
    /** Two byte signed integer. */
    SHORT,
    /** Two byte unsigned integer. */
    USHORT,
    /** Four byte signed integer. */
    INT,
    /** Four byte unsigned integer. */
    UINT,
    /** four byte floating point number. */
    FLOAT,
    /** Eight byte byte floating point number. */
    DOUBLE;

    /**
     * Parses the PLY name of a data type.
     * @param typeName Name of the data type.
     * @return Data type.
     */
    static DataType parse(final String typeName) {
        if (typeName == null) {
            throw new NullPointerException("typeName must not be null.");
        }
        if ("char".equals(typeName)) {
            return CHAR;
        } else if ("uchar".equals(typeName)) {
            return UCHAR;
        } else if ("short".equals(typeName)) {
            return SHORT;
        } else if ("ushort".equals(typeName)) {
            return USHORT;
        } else if ("int".equals(typeName)) {
            return INT;
        } else if ("uint".equals(typeName)) {
            return UINT;
        } else if ("float".equals(typeName)) {
            return FLOAT;
        } else if ("double".equals(typeName)) {
            return DOUBLE;
        } else if ("int8".equals(typeName)) {
            return CHAR;
        } else if ("uint8".equals(typeName)) {
            return UCHAR;
        } else if ("int16".equals(typeName)) {
            return SHORT;
        } else if ("uint16".equals(typeName)) {
            return USHORT;
        } else if ("int32".equals(typeName)) {
            return INT;
        } else if ("uint32".equals(typeName)) {
            return UINT;
        } else if ("float32".equals(typeName)) {
            return FLOAT;
        } else if ("float64".equals(typeName)) {
            return DOUBLE;
        } else {
            throw new IllegalArgumentException("Not a valid PLY data type.");
        }
    }
}
