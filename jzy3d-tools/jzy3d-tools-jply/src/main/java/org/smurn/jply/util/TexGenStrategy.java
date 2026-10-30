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

import org.smurn.jply.Element;

/**
 * Interface for strategies to generate texture coordinates.
 */
interface TexGenStrategy {

    /**
     * Generates the texture coordinates.
     * <p>The element must provide {@code x}, {@code y} and {@code z} values
     * and have properties named {@code u} and {@code v} in which the results
     * are stored.
     * @param element   Element for which to generate the coordinates.
     * @param bounds    Area covered by the model.
     */
    void generateCoordinates(Element element, RectBounds bounds);
   
}
