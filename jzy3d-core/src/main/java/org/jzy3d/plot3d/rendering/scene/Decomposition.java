package org.jzy3d.plot3d.rendering.scene;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;

public class Decomposition {
    public static ArrayList<Drawable> getDecomposition(List<Drawable> drawables) {
        ArrayList<Drawable> monotypes = new ArrayList<Drawable>();

        for (Drawable c : drawables) {
            if (c != null && c.isDisplayed()) {
                if (c instanceof Composite)
                    monotypes.addAll(getDecomposition((Composite) c));
                else if (c instanceof Drawable)
                    monotypes.add(c);
            }
        }
        return monotypes;
    }

    /** Recursively expand all monotype Drawables from the given Composite. */
    public static ArrayList<Drawable> getDecomposition(Composite input) {
        ArrayList<Drawable> selection = new ArrayList<Drawable>();

        // composite internally make use of synchronisation on its list of child, so we do so
        synchronized (input.getDrawables()) {
            for (Drawable c : input.getDrawables()) {
                if (c != null && c.isDisplayed()) {
                    if (c instanceof Composite)
                        selection.addAll(getDecomposition((Composite) c));
                    else if (c instanceof Drawable)
                        selection.add(c);
                }
            }
        }
        return selection;
    }
}
