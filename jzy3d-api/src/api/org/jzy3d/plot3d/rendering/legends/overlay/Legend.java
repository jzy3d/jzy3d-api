package org.jzy3d.plot3d.rendering.legends.overlay;

import java.awt.Shape;

import org.jzy3d.colors.Color;

public class Legend{
        public String label;
        public Color color;
        public Shape shape;
        
        public Legend(String label, Color color, Shape shape) {
            super();
            this.label = label;
            this.color = color;
            this.shape = shape;
        }
        
        public Legend(String label, Color color) {
            super();
            this.label = label;
            this.color = color;
        }
    }