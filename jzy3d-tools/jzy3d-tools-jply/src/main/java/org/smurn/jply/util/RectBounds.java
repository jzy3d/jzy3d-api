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
 * Three-dimensional, axis-oriented rectangular bounds.
 */
class RectBounds {

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double minZ;
    private double maxZ;

    /**
     * Creates an instance with given bounds.
     * @param minX Minimum value.
     * @param maxX Maximum value.
     * @param minY Minimum value.
     * @param maxY Maximum value.
     * @param minZ Minimum value.
     * @param maxZ Maximum value.
     */
    RectBounds(final double minX, final double maxX, final double minY,
            final double maxY, final double minZ, final double maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    /**
     * Creates an instance with initially invalid bounds ready to be used
     * with {@link #addPoint(double, double, double)}.
     */
    RectBounds() {
        this.minX = Double.POSITIVE_INFINITY;
        this.maxX = Double.NEGATIVE_INFINITY;
        this.minY = Double.POSITIVE_INFINITY;
        this.maxY = Double.NEGATIVE_INFINITY;
        this.minZ = Double.POSITIVE_INFINITY;
        this.maxZ = Double.NEGATIVE_INFINITY;
    }

    /**
     * Increases the bounds to include a given point.
     * <p>If the point is already in the interior of the bounded
     * area the bounds are not changed.</p>
     * @param x Coordinate of the point to include.
     * @param y Coordinate of the point to include.
     * @param z  Coordinate of the point to include.
     */
    public void addPoint(final double x, final double y, final double z) {
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        minZ = Math.min(minZ, z);
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
        maxZ = Math.max(maxZ, z);
    }

    /**
     * Gets the upper boundary of x.
     * @return Upper boundary of x.
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * Gets the upper boundary of y.
     * @return Upper boundary of y.
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * Gets the upper boundary of z.
     * @return Upper boundary of z.
     */
    public double getMaxZ() {
        return maxZ;
    }

    /**
     * Gets the lower boundary of x.
     * @return Lower boundary of x.
     */
    public double getMinX() {
        return minX;
    }

    /**
     * Gets the lower boundary of y.
     * @return Lower boundary of y.
     */
    public double getMinY() {
        return minY;
    }

    /**
     * Gets the lower boundary of z.
     * @return Lower boundary of z.
     */
    public double getMinZ() {
        return minZ;
    }
}
