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

/**
 * Operation modes for texture coordinate generation by
 * {@link NormalizingPlyReader}.
 */
public enum TextureMode {

    /**
     * Do not change existing texture coordinates, nor add any if the
     * coordinates are missing in the file.
     */
    PASS_THROUGH,
    /**
     * Generate texture coordinates based on the vertex's x (u) and y (v)
     * coordinates.
     * <p>The scale and offset is selected to use the complete [0,1] range of
     * the texture coordinates.</p>
     * <p>If there are existing coordinates in the file this mode does
     * nothing.</p>
     */
    XY,
    /**
     * Generate texture coordinates based on the vertex's x (u) and z (v)
     * coordinates.
     * <p>The scale and offset is selected to use the complete [0,1] range of
     * the texture coordinates.</p>
     * <p>If there are existing coordinates in the file this mode does
     * nothing.</p>
     */
    XZ,
    /**
     * Generate texture coordinates based on the vertex's y (u) and z (v)
     * coordinates.
     * <p>The scale and offset is selected to use the complete [0,1] range of
     * the texture coordinates.</p>
     * <p>If there are existing coordinates in the file this mode does
     * nothing.</p>
     */
    YZ
}
