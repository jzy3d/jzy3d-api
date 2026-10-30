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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Reads meshes in the PLY file format.
 */
public final class PlyReaderFile implements PlyReader {

    /** Types in this file. */
    private List<ElementType> elements;
    /** Maps element type name to number of elements of this type. */
    private Map<String, Integer> elementCounts;
    /** Format of the file. */
    private Format format;
    /** Stream to read the data from. {@code null} if the format is ASCII. */
    private BinaryPlyInputStream binaryStream;
    /** Original input stream. */
    private final PushbackInputStream inputStream;
    /** Stream to read the data from. {@code null} if the format is binary. */
    private BufferedReader asciiReader;
    /** Index of the next element group to return. */
    private int nextElement = 0;
    /** Reader last returned. */
    private ElementReader currentReader;
    /** Raw header lines */
    private List<String> rawHeaders;

    /**
     * Opens a PLY file.
     * <p>If the file name ends with {@code .gz} the file will be decompressed
     * automatically.</p>
     * @param file File to open.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException if an error occurs during opening or reading.
     */
    public PlyReaderFile(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file must not be null.");
        }
        if (file.getName().endsWith(".gz")) {
            this.inputStream = new PushbackInputStream(
                    new GZIPInputStream(new FileInputStream(file)));
        } else {
            this.inputStream = new PushbackInputStream(
                    new FileInputStream(file));
        }
        readHeader(this.inputStream);
    }

    /**
     * Opens a PLY file.
     * @param file Path to the file to open.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException if an error occurs during opening or reading.
     */
    public PlyReaderFile(final String file) throws IOException {
        this(new File(file));
    }

    /**
     * Opens a PLY file.
     * @param stream InputStream from which to read the file.
     * @throws NullPointerException if {@code stream} is {@code null}.
     * @throws IOException if an error occurs during opening or reading.
     */
    public PlyReaderFile(final InputStream stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("stream must not be null.");
        }
        this.inputStream = new PushbackInputStream(stream);
        readHeader(this.inputStream);
    }

    /**
     * Reads the header and prepares everything to read the file's content.
     * @param stream Stream to read the file from.
     * @throws IOException if an error occurs during reading.
     */
    private void readHeader(final PushbackInputStream stream)
            throws IOException {
        UnbufferedASCIIReader hdrReader = new UnbufferedASCIIReader(stream);
        String magic = hdrReader.readLine();
        if (!"ply".equals(magic)) {
            throw new IOException("Invalid PLY file: does not start "
                    + "with 'ply'.");
        }

        format = null;
        ElementType.HeaderEntry currentElement = null;
        List<Property> currentElementProperties = null;
        elements = new ArrayList<ElementType>();
        elementCounts = new HashMap<String, Integer>();
        rawHeaders = new ArrayList<String>();

        for (String line = hdrReader.readLine(); true;
                line = hdrReader.readLine()) {
            if (line == null) {
                throw new IOException("Unexpected end of file while "
                        + "reading the header.");
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            rawHeaders.add(line);
            if (line.startsWith("format ")) {
                if (format != null) {
                    throw new IOException("Multiple format definitions.");
                }
                format = Format.parse(line);
            } else if (line.startsWith("element ")) {
                if (currentElement != null) {
                    // finish the last element
                    ElementType element = new ElementType(
                            currentElement.getName(),
                            currentElementProperties);
                    elements.add(element);
                    elementCounts.put(currentElement.getName(),
                            currentElement.getCount());
                    currentElement = null;
                    currentElementProperties = null;
                }
                currentElement = ElementType.parse(line);
                currentElementProperties = new ArrayList<Property>();
            } else if (line.startsWith("property ")) {
                if (currentElement == null) {
                    throw new IOException("Property without element found.");
                }
                Property property = Property.parse(line);
                currentElementProperties.add(property);
            } else if (line.startsWith("end_header")) {
                break;
            }
        }

        if (currentElement != null) {
            // finish the last element
            ElementType element = new ElementType(currentElement.getName(),
                    currentElementProperties);
            elements.add(element);
            elementCounts.put(currentElement.getName(),
                    currentElement.getCount());
            currentElement = null;
            currentElementProperties = null;
        }
        elements = Collections.unmodifiableList(elements);

        if (format == null) {
            throw new IOException("Missing format header entry.");
        }

        switch (format) {
            case ASCII:
                this.asciiReader = new BufferedReader(new InputStreamReader(
                        stream, Charset.forName("US-ASCII")));
                this.binaryStream = null;
                break;
            case BINARY_BIG_ENDIAN:
                this.asciiReader = null;
                this.binaryStream = new BinaryPlyInputStream(
                        Channels.newChannel(stream), ByteOrder.BIG_ENDIAN);
                break;
            case BINARY_LITTLE_ENDIAN:
                this.asciiReader = null;
                this.binaryStream = new BinaryPlyInputStream(
                        Channels.newChannel(stream), ByteOrder.LITTLE_ENDIAN);
                break;
            default:
                throw new IOException("Unsupported format: " + format);
        }
    }

    /**
     * Gets all element types in this PLY file.
     * <p>The order of the list
     * is the same in which the corresponding readers are returned
     * by {@link #nextElementReader()}.</p>
     * @return Immutable list with all element types.
     */
    @Override
    public List<ElementType> getElementTypes() {
        return elements;
    }

    /**
     * Gets the number of elements for a given element type.
     * @param elementType Name of the element type.
     * @return Number of elements of the given type.
     * @throws NullPointerException if {@code elementType} is {@code null}.
     * @throws IllegalArgumentException if there is no such type in this
     * file.
     */
    @Override
    public int getElementCount(final String elementType) {
        if (elementType == null) {
            throw new IllegalArgumentException("elementType must not be null.");
        }
        Integer count = elementCounts.get(elementType);
        if (count == null) {
            throw new IllegalArgumentException(
                    "Type does not exist in this file.");
        } else {
            return count;
        }
    }

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
    @Override
    public ElementReader nextElementReader() throws IOException {
        if (currentReader != null && !currentReader.isClosed()) {
            throw new IllegalStateException(
                    "Previous element stream needs to be closed first.");
        }
        currentReader = nextElementReaderInternal();
        return currentReader;
    }

    /**
     * Returns all the raw lines in the ply header, including
     * lines that are not supported by this library.
     *
     * @return The list of header lines
     */
    @Override
    public List<String> getRawHeaders() {
        return rawHeaders;
    }

    /**
     * Closes the file.
     * @throws IOException if closing fails. 
     */
    @Override
    public void close() throws IOException {
        if (currentReader != null) {
            currentReader.close();
        }
        this.inputStream.close();
    }

    /**
     * Creates the next element reader.
     * @return next element Reader.
     */
    private ElementReader nextElementReaderInternal() {
        if (nextElement >= elements.size()) {
            return null;
        }
        try {
            ElementType type = elements.get(nextElement);
            switch (format) {
                case ASCII:
                    return new AsciiElementReader(
                            type,
                            getElementCount(type.getName()),
                            asciiReader);
                case BINARY_BIG_ENDIAN:
                case BINARY_LITTLE_ENDIAN:
                    return new BinaryElementReader(
                            type,
                            getElementCount(type.getName()),
                            binaryStream);
                default:
                    throw new UnsupportedOperationException("PLY format "
                            + format + " is currently not supported.");
            }
        }
        finally {
            nextElement++;
        }
    }
}
