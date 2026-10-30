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
 * Operation mode for normal vector generation by {@link NormalizingPlyReader}.
 */
public enum NormalMode {

    /**
     * Do not change existing normals, nor add any if normals are missing in
     * the file.
     */
    PASS_THROUGH,

    /**
     * Do not change existing normals, but generate normals if the file does
     * not provide them.
     * <p>Assumes that the faces are given in counter-clockwise order in a
     * right-handed coordinate system (or clockwise order in a left-handed
     * system).</p>
     */
    ADD_NORMALS_CCW,
    
    /**
     * Do not change existing normals, but generate normals if the file does
     * not provide them.
     * <p>Assumes that the faces are given in clockwise order in a
     * right-handed coordinate system (or counter-clockwise order in a
     * left-handed).</p>
     */
    ADD_NORMALS_CW
}
