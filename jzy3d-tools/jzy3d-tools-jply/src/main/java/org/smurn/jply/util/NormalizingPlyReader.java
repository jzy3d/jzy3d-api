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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.smurn.jply.DataType;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.ListProperty;
import org.smurn.jply.PlyReader;
import org.smurn.jply.Property;

/**
 * Ply Reader that does some normalization on the data.
 * <p>This wrapper helps decoupling the user's code from the specifics of
 * individual PLY files.</p>
 */
public class NormalizingPlyReader implements PlyReader {

    private final RandomPlyReader plyReader;

    private final NormalMode normalMode;

    private final TexGenStrategy texGenStrategy;

    private final boolean generateNormals;

    private final List<ElementType> elementTypes;

    /**
     * Creates an instance.
     * @param plyReader The reader providing the data to be normalized.
     * @param tesselationMode    Tesselation operation.
     * @param normalMode Normal vector generation operation.
     * @param textureMode Texture coordinate generation operation.
     */
    public NormalizingPlyReader(final PlyReader plyReader,
            final TesselationMode tesselationMode,
            final NormalMode normalMode, final TextureMode textureMode) {
        this(plyReader, tesselationMode, normalMode, textureMode,
                Axis.X, Axis.Y, Axis.Z);
    }

    /**
     * Creates an instance.
     * @param plyReader The reader providing the data to be normalized.
     * @param tesselationMode    Tesselation operation.
     * @param normalMode Normal vector generation operation.
     * @param textureMode Texture coordinate generation operation.
     * @param x The axis in the source to map to the x in the normalized data.
     * @param y The axis in the source to map to the y in the normalized data.
     * @param z The axis in the source to map to the z in the normalized data.
     */
    public NormalizingPlyReader(final PlyReader plyReader,
            final TesselationMode tesselationMode,
            final NormalMode normalMode, final TextureMode textureMode,
            final Axis x, final Axis y, final Axis z) {

        if (plyReader == null) {
            throw new NullPointerException("plyReader must not be null.");
        }
        if (tesselationMode == null) {
            throw new NullPointerException("tesslationMode must not be null.");
        }
        if (normalMode == null) {
            throw new NullPointerException("normalMode must not be null.");
        }
        if (textureMode == null) {
            throw new NullPointerException("textureMode must not be null.");
        }
        if (x == null) {
            throw new NullPointerException("x must not be null.");
        }
        if (y == null) {
            throw new NullPointerException("y must not be null.");
        }
        if (z == null) {
            throw new NullPointerException("z must not be null.");
        }

        TexGenStrategy texGenStrategyTmp;
        switch (textureMode) {
            case PASS_THROUGH:
                texGenStrategyTmp = null;
                break;
            case XY:
                texGenStrategyTmp = new PlanarTexGenStrategy(Axis.X, Axis.Y);
                break;
            case XZ:
                texGenStrategyTmp = new PlanarTexGenStrategy(Axis.X, Axis.Z);
                break;
            case YZ:
                texGenStrategyTmp = new PlanarTexGenStrategy(Axis.Y, Axis.Z);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported texture generation mode.");
        }

        // Make a map to find the element types by name
        Map<String, ElementType> typeMap = new HashMap<String, ElementType>();
        for (ElementType type : plyReader.getElementTypes()) {
            typeMap.put(type.getName(), type);
        }

        if (!typeMap.containsKey("vertex")) {
            throw new IllegalArgumentException(
                    "PLY file contains no vertex data.");
        }

        if (!typeMap.containsKey("face")
                && (tesselationMode != TesselationMode.PASS_THROUGH
                || normalMode != NormalMode.PASS_THROUGH)) {

            throw new IllegalArgumentException(
                    "PLY file contains no face data.");
        }

        // find out if we need to rename the vertex_index property of face.
        final ElementType renamedFaceType;
        final Map<String, String> sourceNameMap = new HashMap<String, String>();
        if (typeMap.containsKey("face")) {
            List<Property> properties = typeMap.get("face").getProperties();
            List<Property> newProperties = new ArrayList<Property>();
            boolean renameVertexIndex = false;
            for (Property property : properties) {
                if (property.getName().equals("vertex_indices")) {
                    renameVertexIndex = true;
                    newProperties.add(
                            new ListProperty(
                            ((ListProperty) property).getCountType(),
                            "vertex_index",
                            property.getType()));
                    sourceNameMap.put("vertex_index", "vertex_indices");
                } else {
                    newProperties.add(property);
                }
            }
            if (renameVertexIndex) {
                renamedFaceType = new ElementType("face", newProperties);
                typeMap.put("face", renamedFaceType);
            } else {
                renamedFaceType = null;
            }
        } else {
            renamedFaceType = null;
        }

        List<WrappingPlyReader.WrapperFactory> wrappers =
                new LinkedList<WrappingPlyReader.WrapperFactory>();

        // Add the face wrappers if required
        if (tesselationMode == TesselationMode.TRIANGLES
                || renamedFaceType != null) {

            ElementType newFaceType = renamedFaceType != null
                    ? renamedFaceType : typeMap.get("face");

            wrappers.add(new WrappingPlyReader.WrapperFactory(
                    typeMap.get("face"), newFaceType) {

                @Override
                public ElementReader wrap(final ElementReader reader) {
                    ElementReader wrapped = reader;
                    if (renamedFaceType != null) {
                        wrapped = new TypeChangingElementReader(
                                wrapped, renamedFaceType, sourceNameMap);
                    }
                    if (tesselationMode == TesselationMode.TRIANGLES) {
                        wrapped = new TriangulatingFaceReader(wrapped);
                    }
                    return wrapped;
                }
            });
        }

        // Build the new vertex element type
        final ElementType unwrappedVertexType = typeMap.get("vertex");
        ElementType maybeWithNormal;
        if (normalMode != NormalMode.PASS_THROUGH) {
            maybeWithNormal = addNormalProps(unwrappedVertexType);
            generateNormals = !maybeWithNormal.equals(unwrappedVertexType);
        } else {
            maybeWithNormal = unwrappedVertexType;
            generateNormals = false;
        }
        ElementType maybeWithNormalTexture;
        if (texGenStrategyTmp != null) {
            maybeWithNormalTexture = addTextureProps(maybeWithNormal);
            if (maybeWithNormalTexture.equals(maybeWithNormal)) {
                texGenStrategyTmp = null;
            }
        } else {
            maybeWithNormalTexture = maybeWithNormal;
        }
        final ElementType wrappedVertexType = maybeWithNormalTexture;

        // Do we need to shuffle the axes?
        final boolean shuffleAxes = x != Axis.X || y != Axis.Y || z != Axis.Z;

        // Add a wrapper around the source to convert the vertex element type.
        if (!unwrappedVertexType.equals(wrappedVertexType) || shuffleAxes) {
            typeMap.put("vertex", wrappedVertexType);
            wrappers.add(new WrappingPlyReader.WrapperFactory(
                    unwrappedVertexType, wrappedVertexType) {

                @Override
                public ElementReader wrap(final ElementReader reader) {
                    ElementReader newReader = reader;
                    if (shuffleAxes) {
                        newReader = new AxisShufflingVertexReader(
                                newReader, x, y, z);
                    }
                    if (!unwrappedVertexType.equals(wrappedVertexType)) {
                        newReader = new TypeChangingElementReader(
                                reader, wrappedVertexType);
                    }
                    return newReader;
                }
            });
        }

        this.normalMode = normalMode;
        this.texGenStrategy = texGenStrategyTmp;
        this.plyReader = new RandomPlyReader(
                new WrappingPlyReader(plyReader, wrappers));


        // prepare the types we offer (in the order of the source)
        List<ElementType> types = new ArrayList<ElementType>();
        for (ElementType type : plyReader.getElementTypes()) {
            types.add(typeMap.get(type.getName()));
        }
        elementTypes = Collections.unmodifiableList(types);
    }

    /**
     * Adds properties for nx, ny and nz if they don't already exist.
     * @param sourceType Source vertex type.
     * @return Vertex type guaranteed to have nx, ny and nz.
     */
    private static ElementType addNormalProps(final ElementType sourceType) {
        List<Property> properties = new ArrayList<Property>();
        boolean foundNX = false;
        boolean foundNY = false;
        boolean foundNZ = false;
        for (Property property : sourceType.getProperties()) {
            if (property.getName().equals("nx")) {
                foundNX = true;
            }
            if (property.getName().equals("ny")) {
                foundNY = true;
            }
            if (property.getName().equals("nz")) {
                foundNZ = true;
            }
            properties.add(property);
        }
        if (foundNX && foundNY && foundNZ) {
            return sourceType;
        }
        if (!foundNX) {
            properties.add(new Property("nx", DataType.DOUBLE));
        }
        if (!foundNY) {
            properties.add(new Property("ny", DataType.DOUBLE));
        }
        if (!foundNZ) {
            properties.add(new Property("nz", DataType.DOUBLE));
        }
        return new ElementType("vertex", properties);
    }

    /**
     * Adds properties for u and v if they don't already exist.
     * @param sourceType Source vertex type.
     * @return Vertex type guaranteed to have u and v.
     */
    private static ElementType addTextureProps(final ElementType sourceType) {
        List<Property> properties = new ArrayList<Property>();
        boolean foundU = false;
        boolean foundV = false;

        for (Property property : sourceType.getProperties()) {
            if (property.getName().equals("u")) {
                foundU = true;
            }
            if (property.getName().equals("v")) {
                foundV = true;
            }
            properties.add(property);
        }
        if (foundU && foundV) {
            return sourceType;
        }
        if (!foundU) {
            properties.add(new Property("u", DataType.DOUBLE));
        }
        if (!foundV) {
            properties.add(new Property("v", DataType.DOUBLE));
        }
        return new ElementType("vertex", properties);
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
        return elementTypes;
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
        return plyReader.getElementCount(elementType);
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
        RandomElementReader reader = plyReader.nextElementReader();
        if (reader == null) {
            return reader;
        }

        if (!reader.getElementType().getName().equals("vertex")) {
            return reader;
        }

        if (generateNormals) {
            RandomElementReader faces = plyReader.getElementReader("face");

            NormalGenerator generator = new NormalGenerator();
            if (normalMode == NormalMode.ADD_NORMALS_CW) {
                generator.setCounterClockwise(false);
            }

            generator.generateNormals(reader.duplicate(), faces);
        }
        RectBounds bounds = new RectBounds();
        if (texGenStrategy != null) {

            // Find the bounds of the model
            ElementReader boundsReader = reader.duplicate();
            for (Element element = boundsReader.readElement();
                    element != null; element = boundsReader.readElement()) {
                bounds.addPoint(
                        element.getDouble("x"),
                        element.getDouble("y"),
                        element.getDouble("z"));
            }

            // Generate the texture coordinates.
            ElementReader texReader = reader.duplicate();
            for (Element element = texReader.readElement();
                    element != null; element = texReader.readElement()) {

                texGenStrategy.generateCoordinates(element, bounds);
            }
        }
        return reader;
    }

    @Override
    public List<String> getRawHeaders() {
        return plyReader.getRawHeaders();
    }

    /**
     * Closes the file.
     * @throws IOException if closing fails. 
     */
    @Override
    public void close() throws IOException {
        plyReader.close();
    }
}
