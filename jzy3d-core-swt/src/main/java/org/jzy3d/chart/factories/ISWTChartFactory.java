package org.jzy3d.chart.factories;

import org.eclipse.swt.widgets.Composite;

/**
 * Marker contract for any {@link IChartFactory} that targets the SWT toolkit.
 *
 * Toolkit-agnostic SWT helpers (canvases, chart factories) need a parent
 * {@link Composite} at construction time to honor the SWT widget hierarchy.
 * This interface lets backend-specific painter factories (JOGL, PanamaGL)
 * retrieve the parent without depending on a particular chart factory class.
 */
public interface ISWTChartFactory {
  Composite getComposite();
}
