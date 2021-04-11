package org.jzy3d.bridge.swt;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * Adapts an AWT component into an SWT container. The following piece of code shows a convenient way
 * of porting an AWT component to SWT:<br>
 * <br>
 * <code>
 * <ul>public class SWTplot extends AWTplot{<br>
 * 	   <ul>public SWTplot(Composite parent){<br>
 *         <ul>super();<br>
 *		   Bridge.adapt(parent, this);</ul>
 *     }</ul>
 * }</ul>
 * </code><br>
 * Important notice: AWT components may trigger events (mouse, keyboard, component events, etc).
 * When attaching a listener on a bridged AWT component that is supposed to modify an SWT widget
 * (such as a text field), it is required to perform this modification into the SWT UI event queue.
 * If not, the target SWT component will throw a {@link org.eclipse.swt.SWTException} with message
 * "Invalid thread access". Indeed, all SWT widgets check that they are modified inside the UI
 * thread.<br>
 * <br>
 * Thus, it is suggested to compute events this way:<br>
 * <code>
 * Text    info = new Text(parent, SWT.None);<br>
 * SWTplot plot = new SWTplot(parent);<br>
 * plot.addPlotListener(new PlotListener(){<br>
 *      <ul>public void handleEvent(Event e){<br>
 *      	<ul><b>Display.getDefault().asyncExec(new Runnable(){</b><br>
 *      		<ul>info.setText(e.toString());<br></ul>
 *          });<br></ul>
 *      }</ul>
 * });<br>
 * </code><br>
 * <br>
 * If problems are encountered with the Bridge, it is possible to check:<br>
 * http://wiki.eclipse.org/Albireo_SWT_AWT_bugs<br>
 * http://www.eclipsezone.com/eclipse/forums/t45697.html<br>
 *
 * @author Martin Pernollet
 */
public class SWT_AWT_Bridge {

  public static void adapt(Composite containerSWT, final Component componentAWT) {
    Composite embedder = new Composite(containerSWT, SWT.EMBEDDED);
    embedder.setLayoutData(new GridData(GridData.FILL_BOTH));

    adaptIn(embedder, componentAWT);
  }

  public static void adaptIn(Composite embedder, final Component componentAWT) {
    final Frame frame = SWT_AWT.new_Frame(embedder);
    frame.add(componentAWT);

    // disposing the frame cleanly
    embedder.addDisposeListener(e -> EventQueue.invokeLater(frame::dispose));
    // the awt component is supposed to be disposed by the user
  }
}
