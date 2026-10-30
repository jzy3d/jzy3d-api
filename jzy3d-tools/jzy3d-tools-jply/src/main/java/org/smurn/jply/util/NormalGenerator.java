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
import org.smurn.jply.ListProperty;
import org.smurn.jply.Property;

/**
 * Generates normal vectors for all vertices.
 * <p>This method reads both the vertices and the faces. The resulting
 * normals are stored directly in the {@code nx,ny,nz} properties of the
 * vertices. The properties must already exist in the vertices. Existing
 * normal vectors are overwritten.</p>
 * <p>The normals are generated based on a weighted sum of all faces
 * using the vertex. The weight is the angle of the face at that vertex.</p>
 * <p>The winding order of the vertices is assumed to be counter-clockwise
 * by default. This can be changed with {@link #setCounterClockwise(boolean)}.
 * </p>
 * <p>Malformed vertices, where no normal can be generated (for example if
 * they are not used by any face), are assigned the normal vector (0,0,0).</p>
 */
class NormalGenerator {

    private static final double EPSILON = 1E-6;
    private boolean counterClockwise = true;

    /**
     * Gets if the generator assumes that the winding order is counter-clockwise
     * or clockwise.
     * @return {@code true} for counter-clockwise.
     */
    public boolean isCounterClockwise() {
        return counterClockwise;
    }

    /**
     * Sets if the generator assumes that the winding order is counter-clockwise
     * or clockwise.
     * @param counterClockwise {@code true} for counter-clockwise.
     */
    public void setCounterClockwise(final boolean counterClockwise) {
        this.counterClockwise = counterClockwise;
    }

    /**
     * Performs the normal generation.
     * @param vertexReader Reader providing the vertices.
     * @param faceReader Reader providing the faces.
     * @throws IOException if reading fails.
     * @throws NullPointerException if {@code vertexReader} or 
     * {@code faceReader} is {@code null}.
     * @throws IllegalArgumentException if 
     * <ul>
     * <li>The vertex reader does not provide elements with a type named
     * {@code vertex}.</li>
     * <li>The face reader does not provide elements with a type named
     * {@code face}.</li>
     * <li>The vertex reader does not provide elements with three
     * non-list-properties {@code x}, {@code y} and {@code z}.</li>
     * <li>The face reader does not provide elements a list-property
     * named {@code vertex_index}.</li>
     * </ul>
     */
    public void generateNormals(final RandomElementReader vertexReader,
            final ElementReader faceReader) throws IOException {
        if (vertexReader == null) {
            throw new NullPointerException("vertexReader must not be null.");
        }
        if (faceReader == null) {
            throw new NullPointerException("faceReader must not be null.");
        }
        if (!vertexReader.getElementType().getName().equals("vertex")) {
            throw new IllegalArgumentException(
                    "vertexReader does not read vertices.");
        }
        boolean foundX = false;
        boolean foundY = false;
        boolean foundZ = false;
        for (Property p : vertexReader.getElementType().getProperties()) {
            foundX |= p.getName().equals("x") && !(p instanceof ListProperty);
            foundY |= p.getName().equals("y") && !(p instanceof ListProperty);
            foundZ |= p.getName().equals("z") && !(p instanceof ListProperty);
        }
        if (!foundX || !foundY || !foundZ) {
            throw new IllegalArgumentException("Vertex type does not include"
                    + " the three non-list properties x y and z.");
        }

        if (!faceReader.getElementType().getName().equals("face")) {
            throw new IllegalArgumentException(
                    "faceReader does not read faces.");
        }
        boolean foundVertexIndex = false;
        for (Property p : faceReader.getElementType().getProperties()) {
            foundVertexIndex |= p.getName().equals("vertex_index")
                    && (p instanceof ListProperty);
        }
        if (!foundVertexIndex) {
            throw new IllegalArgumentException(
                    "Face type does not include a list property named "
                    + "vertex_index.");
        }

        // Phase 1: accumulate weighted normals
        for (Element face = faceReader.readElement(); face != null;
                face = faceReader.readElement()) {

            accumulateNormals(vertexReader, face);
        }
        // Phase 2: normalize the normal vectors
        for (Element vertex = vertexReader.readElement(); vertex != null;
                vertex = vertexReader.readElement()) {

            normalize(vertex);
        }
    }

    /**
     * Normalizes the normal vector of a vertex.
     * @param vertex Vertex to normalize.
     */
    private void normalize(final Element vertex) {
        double nx = vertex.getDouble("nx");
        double ny = vertex.getDouble("ny");
        double nz = vertex.getDouble("nz");
        double n = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (n < EPSILON) {
            vertex.setDouble("nx", 0);
            vertex.setDouble("ny", 0);
            vertex.setDouble("nz", 0);
        }
        vertex.setDouble("nx", nx / n);
        vertex.setDouble("ny", ny / n);
        vertex.setDouble("nz", nz / n);
    }

    /**
     * Calculate the face normal, weight by angle and add them to
     * the vertices.
     * @param vertices Vertices buffer.
     * @param face  Face to process.
     * @throws IOException if reading fails.
     */
    private void accumulateNormals(final RandomElementReader vertices,
            final Element face) throws IOException {
        int[] indices = face.getIntList("vertex_index");
        for (int i = 0; i < indices.length; i++) {
            int pre;
            int post;
            if (counterClockwise) {
                pre = (i + indices.length - 1) % indices.length;
                post = (i + 1) % indices.length;
            } else {
                pre = (i + 1) % indices.length;
                post = (i + indices.length - 1) % indices.length;
            }

            Element centerVertex;
            Element preVertex;
            Element postVertex;
            try {
                centerVertex = vertices.readElement(indices[i]);
                preVertex = vertices.readElement(indices[pre]);
                postVertex = vertices.readElement(indices[post]);
                accumulateNormal(centerVertex, preVertex, postVertex);
            } catch (IndexOutOfBoundsException e) {
                // we ignore defects in the normals.
            }
        }
    }

    /**
     * Calculate the face normal, weight by angle and add them to
     * the vertex.
     * @param center Vertex for which to sum the normal.
     * @param pre Neighbor vertex on the face.
     * @param post Neighbor vertex on the face.
     */
    private void accumulateNormal(final Element center, final Element pre,
            final Element post) {
        double cx = center.getDouble("x");
        double cy = center.getDouble("y");
        double cz = center.getDouble("z");

        double ax = post.getDouble("x") - cx;
        double ay = post.getDouble("y") - cy;
        double az = post.getDouble("z") - cz;
        double a = Math.sqrt(ax * ax + ay * ay + az * az);
        if (a < EPSILON) {
            return;
        }

        double bx = pre.getDouble("x") - cx;
        double by = pre.getDouble("y") - cy;
        double bz = pre.getDouble("z") - cz;
        double b = Math.sqrt(bx * bx + by * by + bz * bz);
        if (b < EPSILON) {
            return;
        }

        double nx = ay * bz - az * by;
        double ny = az * bx - ax * bz;
        double nz = ax * by - ay * bx;
        double n = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (n < EPSILON) {
            return;
        }

        double sin = n / (a * b);
        double dot = ax * bx + ay * by + az * bz;

        double angle;
        if (dot < 0) {
            angle = Math.PI - Math.asin(sin);
        } else {
            angle = Math.asin(sin);
        }
        double factor = angle / n;
        nx *= factor;
        ny *= factor;
        nz *= factor;

        center.setDouble("nx", center.getDouble("nx") + nx);
        center.setDouble("ny", center.getDouble("ny") + ny);
        center.setDouble("nz", center.getDouble("nz") + nz);
    }
}
