package org.jzy3d.replay.old;

import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import sun.awt.SunToolkit;

public class ToolkitUtils {
    private Method syncNativeQueue;
    private boolean isSyncNativeQueueZeroArguments;
    public ToolkitUtils() {
        syncNativeQueue = null;
        isSyncNativeQueueZeroArguments = true;
        try {
            // Since it's a protected method, we have to iterate over declared
            // methods and setAccessible.
            Method[] methods = SunToolkit.class.getDeclaredMethods();
            for (Method method: methods) {
                String name = method.getName();
                if ("syncNativeQueue".equals(name)) {
                    List<Class<?>> parameterTypes = Arrays.asList(method.getParameterTypes());
                    if (Arrays.<Class<?>>asList(Long.class).equals(parameterTypes)) {
                        isSyncNativeQueueZeroArguments = false;
                    } else if (parameterTypes.isEmpty() && null == syncNativeQueue) {
                        isSyncNativeQueueZeroArguments = true;
                    } else {
                        continue;
                    }
                    syncNativeQueue = method;
                    syncNativeQueue.setAccessible(true);
                }
            }
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        if (syncNativeQueue == null)
            throw new IllegalStateException("Could not find method SunToolkit.syncNativeQueue.");
    }

    /**
     * Block until Swing has dispatched events caused by the Robot or user.
     * 
     * <p>
     * It is based on {@link SunToolkit#realSync()}. Use that method if you want
     * to try to wait for everything to settle down (e.g. if an event listener
     * calls {@link java.awt.Component#requestFocus()},
     * {@link SwingUtilities#invokeLater(Runnable)}, or
     * {@link javax.swing.Timer}, realSync will block until all of those are
     * done, or throw exception after trying). The disadvantage of realSync is
     * that it throws {@link SunToolkit.InfiniteLoop} when the queues don't
     * become idle after 20 tries.
     * 
     * <p>
     * Use this method if you only want to wait until the direct event listeners
     * have been called. For example, if you need to simulate a user click
     * followed by a stream input, then you can ensure that they will reach the
     * program under test in the right order:
     * 
     * <pre>
     * robot.mousePress(InputEvent.BUTTON1);
     * toolkitUtils.flushInputEvents(10000);
     * writer.write("done with press");
     * </pre>
     * 
     * @see {@link java.awt.Robot#waitForIdle()} is no good; does not wait for
     *      OS input events to get to the Java process.
     * @see {@link SunToolkit#realSync()} tries 20 times to wait for queues to
     *      settle and then throws exception. In contrast, flushInputEvents does
     *      not wait for queues to settle, just to flush what's already on them
     *      once.
     * @see {@link java.awt.Toolkit#sync()} flushes graphics pipeline but not
     *      input events.
     * 
     * @param syncNativeQueueTimeout
     *            timeout to use for syncNativeQueue. Something like 10000 is
     *            reasonable.
     */
    public void flushInputEvents(long syncNativeQueueTimeout) {
        SunToolkit toolkit = (SunToolkit) Toolkit.getDefaultToolkit();

        // 1) SunToolkit.syncNativeQueue: block until the operating system
        // delivers Robot or user events to the process.
        try {
            if (isSyncNativeQueueZeroArguments) {
                // java 1.6
                syncNativeQueue.invoke(toolkit);
            } else {
                // java 1.7
                syncNativeQueue.invoke(toolkit, syncNativeQueueTimeout);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        // 2) SunToolkit.flushPendingEvents: block until the Toolkit thread
        // (aka AWT-XAWT, AWT-AppKit, or AWT-Windows) delivers enqueued events
        // to the EventQueue
        SunToolkit.flushPendingEvents();

        // 3) SwingUtilities.invokeAndWait: block until the Swing thread (aka
        // AWT-EventQueue-0) has dispatched all the enqueued input events.
        try {
            SwingUtilities.invokeAndWait(new Runnable(){
                @Override public void run() {}});
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
