package glredbook;

import glredbook10.GLSkeleton;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

/**
 * Slideshow applet for iterating through the red book samples.
 * @author michael-bien.com
 */
public class JOGLApplet extends JApplet {

    private GLSkeleton<?> skeleton;

    @Override
    public void start() {
        //start a default sample
        String className = getParameter("demo");
        loadDemo(className);
    }

    @Override
    public void stop() {
        if (skeleton != null) {
            skeleton.runExit();
        }
    }

    /*
     * called via javascript
     */
    public void loadDemo(String className) {

        if (skeleton != null) {
            skeleton.runExit();

            // remove old drawable on EDT
            SwingUtilities.invokeLater(new Runnable() {
                final Component drawable = (Component)skeleton.drawable;
                public void run() {
                    remove(drawable);
                }
            });
        }

        log().info("i'll try to instantiate: " + className);

        try {

            final Class<?> clazz = Class.forName(className);

            try {
                skeleton = (GLSkeleton<?>) clazz.newInstance();
                System.out.println(skeleton);

                // add new drawable on EDT
                SwingUtilities.invokeLater(new Runnable() {
                    final GLSkeleton<?> s = skeleton;
                    public void run() {
                        if(skeleton == s) {
                            add((Component) s.drawable);
                            System.out.println("added");
                            validate();
                        }
                    }
                });
            } catch (InstantiationException ex) {
                log().log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                log().log(Level.SEVERE, null, ex);
            }


        } catch (ClassNotFoundException ex) {
            log().log(Level.SEVERE, "can't find main class", ex);
        }
    }

    private Logger log() {
        return Logger.getLogger(JOGLApplet.class.getName());
    }
}
