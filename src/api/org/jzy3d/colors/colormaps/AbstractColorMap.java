package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IColorMappable;

public abstract class AbstractColorMap implements IColorMap{
    public abstract Color getColor(double x, double y, double z, double zMin, double zMax);

    public AbstractColorMap() {
        direction = true;
    }
    
    public void setDirection(boolean isStandard) {
        direction = isStandard;
    }

    public boolean getDirection() {
        return direction;
    }

    public Color getColor(IColorMappable colorable, double x, double y, double z) {
        return getColor(x, y, z, colorable.getMin(), colorable.getMax());
    }

    protected double processRelativeZValue(double z, double zMin, double zMax) {
        double rel_value = 0;
    
        if (z < zMin) {
            if (direction)
                rel_value = 0;
            else
                rel_value = 1;
        } else if (z > zMax){
            if (direction)
                rel_value = 1;
            else
                rel_value = 0;
        }
        else {
            if (direction)
                rel_value = (z - zMin) / (zMax - zMin);
            else
                rel_value = (zMax - z) / (zMax - zMin);
        }
        return rel_value;
    }

    public Color getColor(IColorMappable colorable, double z) {
        return getColor(0.0f, 0.0f, z, colorable.getMin(), colorable.getMax()); 
    }

    /** @inheritDoc */
    public double colorComponentRelative(double value, double center, double topwidth, double bottomwidth) {
        return colorComponentAbsolute(value, center - (bottomwidth / 2), center + (bottomwidth / 2), center - (topwidth / 2), center + (topwidth / 2));
    }

    public double colorComponentRelativeNoLeftBorder(double value, double center, double topwidth, double bottomwidth) {
        return colorComponentAbsolute(value, center - (bottomwidth / 2), center + (bottomwidth / 2), center - (bottomwidth / 2), center + (topwidth / 2));
    }

    public double colorComponentRelativeNoRightBorder(double value, double center, double topwidth, double bottomwidth) {
        return colorComponentAbsolute(value, center - (bottomwidth / 2), center + (bottomwidth / 2), center - (topwidth / 2), center + (bottomwidth / 2));
    }

    
    /** @inheritDoc */
    public double colorComponentAbsolute(double value, double bLeft, double bRight, double tLeft, double tRight) {
        double output = 0;
        // a gauche ou a droite du creneau
        if ((value < bLeft) || (value >= bRight)) {
            output = 0;
        }
        // sur le plateau haut
        else if ((value >= tLeft) && (value < tRight)) {
            output = 1;
        }
        // sur la pente gauche du creneau
        else if ((value >= bLeft) && (value < tLeft)) {
            output = (value - bLeft) / (tLeft - bLeft);
        }
        // sur la pente droite du creneau
        else if ((value >= tRight) && (value < bRight)) {
            output = (value - bRight) / (tRight - bRight);
        }
        return output;
    }

    protected boolean direction;
}