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
 * Generates texture coordinates by projecting the vertices onto a axis
 * aligned plane.
 */
class PlanarTexGenStrategy implements TexGenStrategy {

    private final Axis uAxis;
    private final Axis vAxis;

    /**
     * Creates an instance.
     * @param uAxis   Which axis is used for the {@code u} texture coordinate.
     * @param vAxis   Which axis is used for the {@code v} texture coordinate.
     * @throws NullPointerException if {@code uAxis} or {@code vAxis} is
     * {@code null}.
     */
    PlanarTexGenStrategy(final Axis uAxis, final Axis vAxis) {
        if (uAxis == null) {
            throw new NullPointerException("uAxis must not be null.");
        }
        if (vAxis == null) {
            throw new NullPointerException("vAxis must not be null.");
        }
        this.uAxis = uAxis;
        this.vAxis = vAxis;
    }

    @Override
    public void generateCoordinates(final Element element,
            final RectBounds bounds) {
        if (element == null || bounds == null) {
            throw new NullPointerException();
        }

        // Normalize coordinates into [0,1] range.
        double x = element.getDouble("x");
        double y = element.getDouble("y");
        double z = element.getDouble("z");
        x = ( x - bounds.getMinX() ) / ( bounds.getMaxX() - bounds.getMinX() );
        y = ( y - bounds.getMinY() ) / ( bounds.getMaxY() - bounds.getMinY() );
        z = ( z - bounds.getMinZ() ) / ( bounds.getMaxZ() - bounds.getMinZ() );

        double u = 0.0;
        switch (uAxis) {
            case X:
                u = x;
                break;
            case Y:
                u = y;
                break;
            case Z:
                u = z;
                break;
            default:
                throw new IllegalStateException("Invalid axis.");
        }
        double v = 0.0;
        switch (vAxis) {
            case X:
                v = x;
                break;
            case Y:
                v = y;
                break;
            case Z:
                v = z;
                break;
            default:
                throw new IllegalStateException("Invalid axis.");
        }

        element.setDouble("u", u);
        element.setDouble("v", v);
    }
}
